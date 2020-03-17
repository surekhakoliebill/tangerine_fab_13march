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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.SimSwapList;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.SimSwapListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;

import java.io.IOException;
import java.util.List;

public class NewSimSwapActivity extends AppCompatActivity {

    Button btn;
    Activity activity = this;
    ProgressDialog progressDialog;
    TextInputEditText enterMSISDN, enterReason;
    UserLogin command;
    ListView simSwapList;
    SimSwapList[] simListsArray;

    public  void onTrimMemory(int level) {
        System.gc();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sim_swap);
        simSwapList = (ListView) findViewById(R.id.new_sim_swap_list);
        command = new UserLogin();

        enterMSISDN = (TextInputEditText) findViewById(R.id.new_msisdn1);
        enterReason = (TextInputEditText) findViewById(R.id.new_reason);
        btn = (Button) findViewById(R.id.new_sim_submit_btn);

        getAllSimList();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                command = collectSimDetail();
                if(command != null){
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...");
                        serviceHandler.postSIMSwap(command, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                if (isFinishing()) {
                                    return;
                                }
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                UserLogin checkSubscriberSim = (UserLogin) data.get(0);
                                if (checkSubscriberSim.status.equals("success")) {
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Success");
                                    alertDialog.setMessage(getResources().getString(R.string.sim_swap));
                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    clearFields();
                                                    getAllSimList();
                                                    // activity.finish();
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    MyToast.makeMyToast(activity, checkSubscriberSim.status, Toast.LENGTH_LONG);
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
                    }
                }

            }
        });


    }

    private void getAllSimList() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity,"Please wait..!");

            serviceHandler.getAllSims(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    simListsArray = new SimSwapList[data.size()];
                    data.toArray(simListsArray);
                    if(simListsArray.length !=0) {
                        ArrayAdapter adapter = new SimSwapListAdapter(activity, simListsArray);
                        simSwapList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        MyToast.makeMyToast(activity,"Requests Are Empty", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(NewSimSwapActivity.this);
    }

    private void clearFields() {
        enterMSISDN.setText("");
        enterReason.setText("");
    }

    private UserLogin collectSimDetail(){

        if (enterMSISDN.getText().toString().isEmpty() | enterMSISDN.getText().toString().length() != 12) {
            MyToast.makeMyToast(activity, "Please Enter MSISDN", Toast.LENGTH_SHORT);
            return null;
        } else {
            command.msisdn = enterMSISDN.getText().toString();
        }

        if (!enterReason.getText().toString().isEmpty()) {
            command.reason = enterReason.getText().toString();
        } else {
            MyToast.makeMyToast(activity, "Please Enter Reason", Toast.LENGTH_SHORT);
            return null;
        }
        return command;
    }
}