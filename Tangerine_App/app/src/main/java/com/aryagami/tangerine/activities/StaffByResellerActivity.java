package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.ResellerStaff;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.StaffByResellerAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffByResellerActivity extends AppCompatActivity {
     ListView staffListView;
     Activity activity = this;
     ResellerStaff resellerStaffArray[];
     ProgressDialog progressDialog;
     ImageButton backImageButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_by_reseller);
        staffListView = (ListView)findViewById(R.id.staff_listview);
        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
        EditText search = (EditText)findViewById(R.id.search_list);
        getAllStaffByReseller();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startNavigationActivity();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(StaffByResellerActivity.this);
    }

    private void getAllStaffByReseller() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, Fetching list!");

            serviceHandler.getStaffByReseller(UserSession.getResellerId(activity), new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                       if(data != null){
                           ResellerStaff resellerStaff = new ResellerStaff();
                           resellerStaff = (ResellerStaff)data.get(0);
                           if(resellerStaff != null){
                               if (resellerStaff.status.equals("success")) {
                                   List<ResellerStaff> list = new ArrayList<ResellerStaff>();
                                   if (resellerStaff.staffList != null) {
                                       if(resellerStaff.staffList.size() != 0)
                                       for (ResellerStaff staff : resellerStaff.staffList) {
                                           list.add(staff);
                                       }

                                   }

                                   if (resellerStaff.retailersList != null ) {
                                       if(resellerStaff.retailersList.size() != 0)
                                       for (ResellerStaff staff : resellerStaff.retailersList) {
                                           list.add(staff);
                                       }
                                   }

                                   if (resellerStaff.distributorsList != null ) {
                                       if(resellerStaff.distributorsList.size() != 0)
                                       for (ResellerStaff staff : resellerStaff.distributorsList) {
                                           list.add(staff);
                                       }
                                   }
                                   resellerStaffArray = new ResellerStaff[list.size()];
                                   list.toArray(resellerStaffArray);

                                   staffListView.setVisibility(View.VISIBLE);
                                   ArrayAdapter adapter = new StaffByResellerAdapter(activity, resellerStaffArray);
                                   staffListView.setAdapter(adapter);

                               } else if (resellerStaff.status.equals("INVALID_SESSION")) {
                                   ReDirectToParentActivity.callLoginActivity(activity);
                               } else {
                                   staffListView.setVisibility(View.GONE);
                                   MyToast.makeMyToast(activity, "STATUS:" + resellerStaff.status, Toast.LENGTH_SHORT);
                               }
                           }
                       }else{
                           staffListView.setVisibility(View.GONE);
                           MyToast.makeMyToast(activity, "Empty DATA", Toast.LENGTH_SHORT);

                       }
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+"\n STATUS:"+status,"STAFF BY RESELLER ACTIVITY");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getCause()+"\n STATUS:"+e.getMessage(),"STAFF BY RESELLER ACTIVITY");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startNavigationActivity();

    }

    public void startNavigationActivity(){
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        startActivity(intent);

    }

}
