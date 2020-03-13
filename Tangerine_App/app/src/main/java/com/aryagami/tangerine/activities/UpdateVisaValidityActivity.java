package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.GetAllInfo;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ForeignUsersListingAdapter;
import com.aryagami.util.AlertMessage;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateVisaValidityActivity extends AppCompatActivity {

    ListView usersListView;
    Button searchBtn;
    TextInputEditText userNameText;
    String userName;
    Activity activity = this;
    ProgressDialog progressDialog;
    UserRegistration usersArray[], foreignUsers[];
    List<UserRegistration> foreignUsersList = new ArrayList<UserRegistration>();
    ImageButton backImageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_update_visa_validity);

        userNameText = (TextInputEditText)findViewById(R.id.username_text);
        searchBtn = (Button)findViewById(R.id.check_btn);
        usersListView = (ListView)findViewById(R.id.foreign_user_list);

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

                if(userNameText.getText().toString().isEmpty()){

                    MyToast.makeMyToast(activity,"Please Enter userName.", Toast.LENGTH_SHORT);
                }else{

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

                                                for(UserRegistration registration : usersArray){
                                                    if(registration.nationalIdentity != null)
                                                        if(registration.nationalIdentity.equals("Passport No")){
                                                            foreignUsersList.add(registration);
                                                        }
                                                }

                                                if(foreignUsersList.size() != 0) {
                                                    foreignUsers = new UserRegistration[foreignUsersList.size()];
                                                    foreignUsersList.toArray(foreignUsers);
                                                    usersListView.setVisibility(View.VISIBLE);
                                                    ArrayAdapter adapter = new ForeignUsersListingAdapter(activity, foreignUsers);
                                                    usersListView.setAdapter(adapter);
                                                }else{
                                                    usersListView.setAdapter(null);
                                                    MyToast.makeMyToast(activity,"Username Not Found, please try again.", Toast.LENGTH_SHORT);

                                                }
                                            }else{
                                                AlertMessage.alertDialogMessage(activity,"Message!","Username Not Found, please try again.");
                                                usersListView.setVisibility(View.GONE);
                                            }

                                        }else if(info.status.equals("INVALID_SESSION")){
                                            ReDirectToParentActivity.callLoginActivity(activity);
                                        }else{
                                            AlertMessage.alertDialogMessage(activity,"Message!","Username Not Found, please try again.");
                                            usersListView.setVisibility(View.GONE);
                                            //  MyToast.makeMyToast(activity,"STATUS"+info.status,Toast.LENGTH_SHORT);
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
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n STATUS:"+status,"UPDATE_VISA_VALIDITY");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Error"+e.getCause()+"MESSAGE:"+e.getMessage(),"UPDATE_VISA_VALIDITY");
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(UpdateVisaValidityActivity.this);
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }

}
