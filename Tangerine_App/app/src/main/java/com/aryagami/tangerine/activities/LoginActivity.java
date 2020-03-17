package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ResellerLoginInfo;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    UserLogin userLoginResult = null;
    UserRegistration navigationData = new UserRegistration();
    private ProgressDialog progressDialog;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    ResellerLoginInfo resellerLoginInfo;
    Button otpLoginButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        final Activity activity = this;

        if (UserSession.getSessionKey() != null) {
            if (UserSession.getUserGroup(activity).equals("Consumer")) {
                Intent intent = new Intent(activity, NavigationMainActivity.class);
                intent.putExtra("userId", UserSession.getUserId(activity));
                startActivity(intent);
            } else {
                Intent intent = new Intent(activity, NavigationMainActivity.class);
                startActivity(intent);
            }
        }
        Button signIn = (Button) findViewById(R.id.loginButton);
        otpLoginButton = (Button) findViewById(R.id.otpLoginButton);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler service = new RestServiceHandler();
                final UserLogin userLogin = new UserLogin();
                final EditText uname = (EditText) findViewById(R.id.msisdin_number);
                userLogin.servedMSISDN = uname.getText().toString();
                final EditText pwd = (EditText) findViewById(R.id.signInPassword);
                String password = pwd.getText().toString().replace(" ", "");
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes("UTF-8"));
                    userLogin.password = LoginActivity.bytesToHex(hash);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                progressDialog = ProgressDialogUtil.startProgressDialog(activity, getResources().getString(R.string.logging_in));
                progressDialog.setCanceledOnTouchOutside(false);
                try {
                    service.userLogin(userLogin, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {

                            if (isFinishing()) {
                                return;
                            }

                            userLoginResult = (UserLogin) data.get(0);
                            if (userLoginResult.status.equals("success")) {
                                UserSession.setUserId(activity, userLoginResult.userId);
                                UserSession.setServedMSISDN(activity, uname.getText().toString());
                                UserSession.setUserGroup(activity, userLoginResult.userGroup.toString());
                                navigationData.userGroup = userLoginResult.userGroup.toString();
                                UserSession.setSubcriptionId(activity, Constants.subscriptionId);
                                UserSession.setSessionKey(activity, userLoginResult.sessionKey);

                                if (userLoginResult.userName == null) {
                                    MyToast.makeMyToast(LoginActivity.this, "Invalid User", Toast.LENGTH_LONG);
                                } else {
                                    RestServiceHandler serviceHandler = new RestServiceHandler();
                                    try {
                                        serviceHandler.getResellerLoginInfo(new RestServiceHandler.Callback() {
                                            @Override
                                            public void success(DataModel.DataType type, List<DataModel> data) {
                                                if (data != null) {

                                                    resellerLoginInfo = (ResellerLoginInfo) data.get(0);
                                                    if(resellerLoginInfo.status.equals("success")) {
                                                        UserSession.setResellerId(activity, resellerLoginInfo.userInfo.resellerId);
                                                        UserSession.setResellerName(activity, resellerLoginInfo.userInfo.company);
                                                        navigationData.company = resellerLoginInfo.userInfo.company.toString();
                                                        UserSession.setAggregator(activity, resellerLoginInfo.aggregator);
                                                       // UserSession.setEnableMobileMoneyReg(activity, resellerLoginInfo.userInfo.enableMobileMoneyReg);
                                                        RegistrationData.setNavigationData(navigationData);
                                                        RegistrationData.setEnableMobileMoneyReg(resellerLoginInfo.userInfo.enableMobileMoneyReg);

                                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                        MyToast.makeMyToast(LoginActivity.this, "Successfully Login", Toast.LENGTH_LONG);
                                                            startNavigationActivity();
                                                    }else if(resellerLoginInfo.status.equals("INVALID_SESSION")){
                                                        ReDirectToParentActivity.callLoginActivity(LoginActivity.this);
                                                    }else{
                                                        MyToast.makeMyToast(LoginActivity.this, "Status:"+resellerLoginInfo.status, Toast.LENGTH_LONG);

                                                    }
                                                }
                                            }

                                            @Override
                                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                               // Toast.makeText(activity, "ERROR" + error + "STATUS:" + status, Toast.LENGTH_SHORT).show();
                                                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"LoginActivity- GET USER INFO");
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+",Cause:"+e.getCause(),"LoginActivity");
                                    }

                                  /*  ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    MyToast.makeMyToast(LoginActivity.this, "Successfully Login", Toast.LENGTH_LONG);
*/
                                   /* if (userLoginResult.forcePwdUpdate != null) {
                                        if (!userLoginResult.forcePwdUpdate) {
                                            if (UserSession.getUserGroup(activity).equals("Consumer")) {
                                                Intent intent = new Intent(activity, NavigationMainActivity.class);
                                                intent.putExtra("userId", userLoginResult.userId);
                                                startActivity(intent);
                                                activity.finish();
                                            } else {
                                                Intent intent = new Intent(activity, NavigationMainActivity.class);
                                                startActivity(intent);
                                                activity.finish();
                                            }
                                        } else {
                                            Intent intent = new Intent(activity, NavigationMainActivity.class);
                                            startActivity(intent);
                                            activity.finish();
                                        }
                                    }*/
                                }

                            } else {
                                MyToast.makeMyToast(activity, getResources().getString(R.string.user_login) + ", " + userLoginResult.status, Toast.LENGTH_SHORT);
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                          //  MyToast.makeMyToast(activity, getResources().getString(R.string.user_login) + ", " + status, Toast.LENGTH_SHORT);
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"LoginActivity");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+",Cause:"+e.getCause(),"LoginActivity");
                }
            }
        });

        otpLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(LoginActivity.this, NavigationMainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    public static boolean isLowMemory(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                return memoryInfo.lowMemory;
            }
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(LoginActivity.this);

        if( isLowMemory(LoginActivity.this)){
            MyToast.makeMyToast(LoginActivity.this, "Low memory...", Toast.LENGTH_SHORT).show();
        }else {
            //MyToast.makeMyToast(LoginActivity.this, "full memory...", Toast.LENGTH_SHORT).show();
        }

    }


    public static String bytesToHex(byte[] bytes) {
        String a = "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        a = new String(hexChars);
        return a.toLowerCase();
    }

}
