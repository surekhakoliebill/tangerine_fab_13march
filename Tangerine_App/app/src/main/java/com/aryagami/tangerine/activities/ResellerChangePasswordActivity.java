package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserInfo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ResellerChangePasswordActivity extends AppCompatActivity {

    Activity activity = this;
    ProgressDialog progressDialog;
    TextInputEditText currentPassword, newPassword, confirmPassword;
    Pattern pattern;
    Matcher matcher;
    ImageButton backImageButton;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@^_`{|}~]).{6,20})";

//@#*&$!% --> !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    public ResellerChangePasswordActivity() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        newPassword = (TextInputEditText) findViewById(R.id.reseller_new_password);
        currentPassword = (TextInputEditText) findViewById(R.id.reseller_current_password);
        confirmPassword = (TextInputEditText) findViewById(R.id.confirm_password);
        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
        //Fabric.with(this, new Crashlytics());
        Button submit = (Button) findViewById(R.id.reseller_save_btn);

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currPasswordText = currentPassword.getText().toString();
                String newPasswordText = newPassword.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();
                if (!currPasswordText.isEmpty()) {
                    if(!currPasswordText.contains("Tangerine") | !currPasswordText.contains("tangerine")| !currPasswordText.contains("Lyca")| !currPasswordText.contains("lyca")) {

                        if (!newPasswordText.isEmpty()) {
                            if (!currPasswordText.trim().equalsIgnoreCase(newPasswordText.trim())) {
                                if (validate(newPasswordText)) {
                                    if (!confirmPasswordText.isEmpty()) {

                                        if (newPasswordText.equals(confirmPasswordText)) {

                                            changePassword(currPasswordText, newPasswordText);

                                        } else {
                                            MyToast.makeMyToast(activity, "Entered New & Confirm Passwords are not Matched.", Toast.LENGTH_SHORT);
                                        }

                                    } else {
                                        MyToast.makeMyToast(activity, "Please enter Confirm Password.", Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    MyToast.makeMyToast(activity, "Entered New Password is not Matched with Password Policy.", Toast.LENGTH_SHORT);
                                }
                            } else {
                                MyToast.makeMyToast(activity, "Entered New & Old passwords should be Different..", Toast.LENGTH_SHORT);
                            }
                        } else {
                            MyToast.makeMyToast(activity, "Please enter New Password.", Toast.LENGTH_SHORT);
                        }
                    }else{
                        MyToast.makeMyToast(activity, "Current Password should not include such text as, Lyca, Tangerine, tangerine, lyca.", Toast.LENGTH_SHORT);
                    }
                } else {
                    MyToast.makeMyToast(activity, "Please enter Current Password.", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(ResellerChangePasswordActivity.this);
    }

    private void changePassword(String currPassword, String newPassword) {
        UserInfo userInfo = new UserInfo();
        userInfo.userId = UserSession.getUserId(ResellerChangePasswordActivity.this);

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = new byte[0];
            byte[] hash1 = new byte[0];
            try {
                hash = digest.digest(currPassword.getBytes("UTF-8"));
                hash1 = digest.digest(newPassword.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getMessage()+" "+e.getCause(),"ResellerChangePasswordActivity");
            }
            userInfo.oldPassword = LoginActivity.bytesToHex(hash);
            userInfo.password = LoginActivity.bytesToHex(hash1);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getMessage()+" "+e.getCause(),"ResellerChangePasswordActivity");
        }

        RestServiceHandler service = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "");
            progressDialog.setCanceledOnTouchOutside(false);
            service.changePassword(userInfo, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    if (isFinishing()) {
                        return;
                    }
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    UserInfo changedPassword = (UserInfo) data.get(0);
                    if (changedPassword.status.equals("success")) {

                        displayAlertMessage(getResources().getString(R.string.changed_password));

                    } else {
                      //  BugReport.postBugReport(activity, Constants.emailId, changedPassword.status, "ChangePassword");

                        if (changedPassword.status.equals("INVALID_SESSION")) {
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {
                            displayAlertMessage(changedPassword.status);
                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    if (isFinishing()) {
                        return;
                    }
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    displayAlertMessage("Error:" + error + "\t " + getResources().getString(R.string.request_password_change) + ", " + status);
                    BugReport.postBugReport(activity, Constants.emailId, "ERROR:"+error+"\n STATUS:"+status, "ChangePassword- ResellerChangePasswordActivity");

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getMessage()+" "+e.getCause(),"ResellerChangePasswordActivity");
        }


    }

    private void displayAlertMessage(String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        activity.finish();
                        Intent intent = new Intent(activity, NavigationMainActivity.class);
                        startActivity(intent);

                    }
                });
        alertDialog.show();
    }

    public boolean validate(final String password) {
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
