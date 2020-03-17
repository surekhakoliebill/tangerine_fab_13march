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
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aryagami.data.RegistrationData.postStaffOrder;

public class ValidateSimSwapActivity extends AppCompatActivity {

    Activity activity = this;
    ProgressDialog progressDialog;
    String postMSISDN;
    SimReplacementForm response;
    //TextInputEditText last_call_duration, last_called_number, last_recharge_amount, customers_name, email;

    TextInputEditText previous_bundle, first_called_number, second_called_number, third_called_number,
            first_sms_number, second_sms_number, third_sms_number, mostly_called_number, previous_recharge_amount,
            customers_name, email;
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

        previous_bundle = (TextInputEditText) findViewById(R.id.previous_bundle_used);
        first_called_number = (TextInputEditText) findViewById(R.id.first_called_number);
        second_called_number = (TextInputEditText) findViewById(R.id.second_called_number);
        third_called_number = (TextInputEditText) findViewById(R.id.third_called_number);
        first_sms_number = (TextInputEditText) findViewById(R.id.first_sms_number);
        second_sms_number = (TextInputEditText) findViewById(R.id.second_sms_number);
        third_sms_number = (TextInputEditText) findViewById(R.id.third_sms_number);
        mostly_called_number = (TextInputEditText) findViewById(R.id.mostly_called_number);
        previous_recharge_amount = (TextInputEditText) findViewById(R.id.previous_recharge_number);
        customers_name = (TextInputEditText) findViewById(R.id.customer_name);
        email = (TextInputEditText) findViewById(R.id.email_sim);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                simReplacementForm = collectAllValidateData();

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
                                } else if (response.status.equals("INVALID_SESSION")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    ReDirectToParentActivity.callLoginActivity(activity);

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

    private SimReplacementForm collectAllValidateData() {

        SimReplacementForm simReplacementForm = new SimReplacementForm();
        if (!previous_bundle.getText().toString().isEmpty()) {
            simReplacementForm.latestBundleUsed= previous_bundle.getText().toString();
        } else {
            MyToast.makeMyToast(activity, "Please enter previous bundle used", Toast.LENGTH_SHORT);
            return  null;
        }

        if (first_called_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter first Called Number", Toast.LENGTH_SHORT);
            return null;

        }
        if (second_called_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter second Called Number", Toast.LENGTH_SHORT);
            return null;

        }
        if (third_called_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter third Called Number", Toast.LENGTH_SHORT);
            return null;
        }

        if (first_sms_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter first SMS Number", Toast.LENGTH_SHORT);
            return null;

        }
        if (second_sms_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter second SMS Number", Toast.LENGTH_SHORT);
            return null;

        }
        if (third_sms_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter third SMS Number", Toast.LENGTH_SHORT);
            return null;
        }


        if (mostly_called_number.getText().toString().isEmpty()) {
            MyToast.makeMyToast(activity, "Please enter mostly Called Number", Toast.LENGTH_SHORT);
            return null;
        }

        List<String> mostlycallednumber =  new ArrayList<>();;
        mostlycallednumber.add(mostly_called_number.getText().toString());
        simReplacementForm.mostlyCalledNumbers = mostlycallednumber;

        List<String> lastThreeNum =  new ArrayList<>();
        lastThreeNum.add(first_called_number.getText().toString());
        lastThreeNum.add(second_called_number.getText().toString());
        lastThreeNum.add(third_called_number.getText().toString());

        simReplacementForm.lastThreeCalledNumbers = lastThreeNum;

        List<String> lastThreeSms =  new ArrayList<>();
        lastThreeSms.add(first_sms_number.getText().toString());
        lastThreeSms.add(second_sms_number.getText().toString());
        lastThreeSms.add(third_sms_number.getText().toString());

        simReplacementForm.lastThreeSmsSentNumbers = lastThreeSms;



        if (!customers_name.getText().toString().isEmpty()) {
            simReplacementForm.ownerName= customers_name.getText().toString();
        } else {
            MyToast.makeMyToast(activity, "Please enter Customer Name", Toast.LENGTH_SHORT);
            return null;

        }

        if (!email.getText().toString().isEmpty()) {
            simReplacementForm.email= email.getText().toString();
        } else {
            MyToast.makeMyToast(activity, "Please enter Email Id", Toast.LENGTH_SHORT);
            return null;
        }

        if (!previous_recharge_amount.toString().isEmpty()){
            simReplacementForm.airTimeAmountLoaded = Double.parseDouble(previous_recharge_amount.getText().toString());
        }

        //simReplacementForm.lastCalledPartyCheckLimit = 5;
        simReplacementForm.msisdn = postMSISDN;
        simReplacementForm.skipValidation = false;

        return  simReplacementForm;

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(ValidateSimSwapActivity.this);
    }
}
