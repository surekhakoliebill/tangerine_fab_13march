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
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.SimReplacementForm;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.ProgressDialogUtil;

import java.io.IOException;
import java.util.List;

public class ValidateSimSwapActivity extends AppCompatActivity {

    Activity activity = this;
    ProgressDialog progressDialog;
    String postMSISDN;
    SimReplacementForm response;
    TextInputEditText last_call_duration, last_called_number, last_recharge_amount, customers_name, email;
    SimReplacementForm simReplacementForm = new SimReplacementForm();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_swap_validate_form);
        postMSISDN = getIntent().getStringExtra("msisdn");

        Button upload = (Button) findViewById(R.id.sim_replace_upload_btn);
        Button cancel = (Button) findViewById(R.id.sim_replace_cancel_btn);
        last_call_duration = (TextInputEditText) findViewById(R.id.validate_form_last_call_duration);
        last_called_number = (TextInputEditText) findViewById(R.id.validate_form_last_called_number);
        last_recharge_amount = (TextInputEditText) findViewById(R.id.validate_form_last_recharge_name);
        customers_name = (TextInputEditText) findViewById(R.id.validate_form_customer_name);
        email = (TextInputEditText) findViewById(R.id.validate_form_email);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimReplacementForm simReplacementForm = new SimReplacementForm();
                if (!last_call_duration.getText().toString().isEmpty()) {
                    simReplacementForm.lastCallDuration= Long.valueOf(last_call_duration.getText().toString());
                } else {
                    simReplacementForm.lastCallDuration =  0l;
                }

                if (!last_called_number.getText().toString().isEmpty()) {
                    simReplacementForm.lastCalledParty= last_called_number.getText().toString();
                } else {
                    Toast.makeText(activity, "Please enter Last Called Number", Toast.LENGTH_SHORT);

                }

                if (!last_recharge_amount.getText().toString().isEmpty()) {
                    simReplacementForm.lastPaymentAmout= Float.valueOf(last_recharge_amount.getText().toString());
                } else {
                    Toast.makeText(activity, "Please enter Recharge Amount", Toast.LENGTH_SHORT);

                }

                if (!customers_name.getText().toString().isEmpty()) {
                    simReplacementForm.ownerName= customers_name.getText().toString();
                } else {
                    Toast.makeText(activity, "Please enter Customer Name", Toast.LENGTH_SHORT);

                }

                if (!email.getText().toString().isEmpty()) {
                    simReplacementForm.email= email.getText().toString();
                } else {
                    Toast.makeText(activity, "Please enter Email Id", Toast.LENGTH_SHORT);

                }


                simReplacementForm.lastCalledPartyCheckLimit = 5;
                simReplacementForm.msisdn = postMSISDN;


                if (simReplacementForm != null) {
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                        serviceHandler.postReplaceMentForm(simReplacementForm, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {

                                response = (SimReplacementForm) data.get(0);
                                if (response.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Ownership Verified. \n Accuracy:\t" + response.accuracy);
                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(activity, NewSimSwapActivity.class);
                                                    activity.startActivity(intent);
                                                }
                                            });
                                    alertDialog.show();

                                } else if (response.status.equals("failure")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Ownership Not Verified. \n Accuracy:\t" + response.accuracy);
                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Status:\t" + response.status);
                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();

                                                }
                                            });
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"Sim Swap Activity");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Sim Swap Activity");
                    }
                }

            }
            //}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(ValidateSimSwapActivity.this);
    }
}
