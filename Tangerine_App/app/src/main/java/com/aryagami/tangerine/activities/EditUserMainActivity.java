package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.GetAllInfo;
import com.aryagami.data.Subscription;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.UsersListingAdapter;
import com.aryagami.util.AlertMessage;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditUserMainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ListView usersListView;
    ImageButton searchBtn, searchMSISDNNumberBtn;
    TextInputEditText userNameText, msisdnNumber;
    String userName;
    Activity activity = this;
    ProgressDialog progressDialog;
    UserRegistration usersArray[];
    LinearLayout userNameLayout, msisdnLayout;
    Spinner filterSpinner;
    String[] filterOptions = {"UserName", "MSISDN Number"};
    String checkMSISDNNumber;
    String userId;
    ImageButton backImageButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_main);

        userNameText = (TextInputEditText) findViewById(R.id.username_text);
        searchBtn = (ImageButton) findViewById(R.id.check_btn);
        msisdnNumber = (TextInputEditText) findViewById(R.id.msisdin_number);
        searchMSISDNNumberBtn = (ImageButton) findViewById(R.id.check_msisdn_btn);
        userNameLayout = (LinearLayout) findViewById(R.id.username_layout);
        msisdnLayout = (LinearLayout) findViewById(R.id.msisdn_layout);

        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        usersListView = (ListView) findViewById(R.id.users_list);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, filterOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        filterSpinner.setAdapter(adapter1);
        filterSpinner.setOnItemSelectedListener(EditUserMainActivity.this);

        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameText.getText().toString().isEmpty()) {
                    MyToast.makeMyToast(activity, "Please Enter userName.", Toast.LENGTH_SHORT);
                    usersListView.setVisibility(View.GONE);
                } else {
                    userName = userNameText.getText().toString().trim();
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                    try {
                        serviceHandler.getAllUsersByUsername("$", userName, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                               if(data != null){
                                   GetAllInfo info = new GetAllInfo();
                                   info = (GetAllInfo) data.get(0);

                                   if(info != null){
                                       if(info.status.equals("success")){

                                           if(info.userRegistrationList.size() !=0) {
                                               usersArray = new UserRegistration[info.userRegistrationList.size()];
                                               info.userRegistrationList.toArray(usersArray);

                                               usersListView.setVisibility(View.VISIBLE);
                                               ArrayAdapter adapter = new UsersListingAdapter(activity, usersArray);
                                               usersListView.setAdapter(adapter);
                                               adapter.notifyDataSetChanged();
                                           }else{
                                               AlertMessage.alertDialogMessage(activity,"Message!","Username Not Found, please try again.");
                                               usersListView.setVisibility(View.GONE);
                                           }

                                       }else if(info.status.equals("INVALID_SESSION")){
                                           ReDirectToParentActivity.callLoginActivity(activity);
                                       }else{
                                           AlertMessage.alertDialogMessage(activity,"Message!","Username Not Found, please try again.");
                                           usersListView.setVisibility(View.GONE);
                                       }
                                   }else{
                                       AlertMessage.alertDialogMessage(activity,"Message!","Username Not Found, please try again.");
                                       usersListView.setVisibility(View.GONE);
                                   }
                               }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"\n STATUS"+status,"UPDATE USER");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"UPDATE USER");                    }
                }
            }
        });

        searchMSISDNNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMSISDNNumber = msisdnNumber.getText().toString();
                if (!checkMSISDNNumber.isEmpty() && checkMSISDNNumber.length() == 12) {

                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, searching MSISDN..!");

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        serviceHandler.getUserSubscriptionsByMSISDN(checkMSISDNNumber, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {

                                final SubscriptionCommand userLogin = (SubscriptionCommand) data.get(0);

                                if (userLogin.statusReason.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    if (userLogin.userInfo != null) {
                                        usersArray = new UserRegistration[1];

                                        Subscription sub = new Subscription();
                                        sub.servedMSISDN = checkMSISDNNumber;
                                        List<Subscription> subList = new ArrayList<Subscription>();
                                        subList.add(sub);

                                        UserRegistration postUserData = new UserRegistration();
                                        postUserData = userLogin.userInfo;
                                        postUserData.userSubscriptions = subList;
                                        usersArray[0] = postUserData;
                                        usersListView.setVisibility(View.VISIBLE);
                                        ArrayAdapter adapter = new UsersListingAdapter(activity, usersArray);
                                        usersListView.setAdapter(adapter);
                                    } else {
                                        usersListView.setVisibility(View.GONE);
                                       AlertMessage.alertDialogMessage(activity,"","Subscription Not Found, please try again.");
                                    }
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    usersListView.setVisibility(View.GONE);
                                    if (userLogin.statusReason.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("Status:" + userLogin.statusReason + "\n Please try another Subscription Number.");
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"\n STATUS"+status,"UPDATE USER");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"UPDATE USER");                    }

                } else {
                    MyToast.makeMyToast(activity, "MSISDN Number field Should not be empty or less than 12 digits.", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(EditUserMainActivity.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.filter_spinner:

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("UserName")) {
                    userNameLayout.setVisibility(View.VISIBLE);
                    msisdnLayout.setVisibility(View.GONE);
                    usersListView.setVisibility(View.GONE);

                } else if (selectedItem.equals("MSISDN Number")) {
                    userNameLayout.setVisibility(View.GONE);
                    msisdnLayout.setVisibility(View.VISIBLE);
                    usersListView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
