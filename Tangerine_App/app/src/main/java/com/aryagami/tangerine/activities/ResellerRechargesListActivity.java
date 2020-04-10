package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ResellerETopupRequestsListAdapter;
import com.aryagami.tangerine.adapters.ResellerRechargesListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResellerRechargesListActivity extends AppCompatActivity {
    ListView rechargesListview;
    Activity activity = this;
    ProgressDialog progressDialog, progressDialog1;
    String todayString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller_recharges_list);
        rechargesListview = (ListView)findViewById(R.id.recharges_listview);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
         todayString = formatter.format(todayDate);

        getRechargesLstview();
        ImageButton backButton = (ImageButton)findViewById(R.id.back_imgbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                Intent intent = new Intent(activity, NavigationMainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getRechargesLstview() {

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

            serviceHandler.getResellerRechargeSalesForToday(UserSession.getResellerId(activity), todayString, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    ActivateCommand command = (ActivateCommand)data.get(0);
                    if(command.status.equals("success")){
                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                        if(command.resellerRechargesList != null){
                            if(command.resellerRechargesList.size() != 0){
                                ActivateCommand[] rechargesArray = new ActivateCommand[command.resellerRechargesList.size()];
                                command.resellerRechargesList.toArray(rechargesArray);

                                ArrayAdapter adapter = new ResellerRechargesListAdapter(activity, rechargesArray);
                                rechargesListview.setAdapter(adapter);

                            }else{
                                MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                            }

                        }else{
                            MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                        }

                    }else if(command.status.equals("INVALID_SESSION")){
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        ReDirectToParentActivity.callLoginActivity(activity);

                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                        alertBuilder.setIcon(R.drawable.success_icon);
                        alertBuilder.setTitle("Message!");
                        alertBuilder.setMessage("Status: "+command.status);
                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertBuilder.create().show();
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS: "+ status, "");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"ERROR"+e.getCause()+"MESSAGE: "+e.getMessage(), "");
        }
    }
}
