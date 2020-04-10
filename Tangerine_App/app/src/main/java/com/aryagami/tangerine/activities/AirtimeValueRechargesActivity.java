package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.AirtimeValue;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ServiceBundleDetailVo;
import com.aryagami.data.SimReplacementForm;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AirtimeValueRechargesActivity extends AppCompatActivity {

    Button findSubscriptionbtn, rechargeButton;
    TextInputEditText msisdnEditText;
    String MSISDN = "";
    ProgressDialog progressDialog,progressDialog1, progressDialog2;
    Activity activity = this;
    String postSubscriptionId;
    Float sim;
    Spinner airtimeProductSpinner;
    List<AirtimeValue> airtimeProductsList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airtime_value_recharges);
        findSubscriptionbtn = (Button) findViewById(R.id.check_subscription_btn);
        msisdnEditText = (TextInputEditText) findViewById(R.id.serverdMSISDN_eText);
        airtimeProductSpinner = (Spinner)findViewById(R.id.airtime_products_spinner);
        rechargeButton = (Button) findViewById(R.id.recharge_button);

        findSubscriptionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msisdnEditText.getText().toString().isEmpty() | msisdnEditText.getText().length() != 9) {
                    MyToast.makeMyToast(activity, "Please enter correct MSISDN Number", Toast.LENGTH_SHORT);
                }else {
                    MSISDN = "256" + msisdnEditText.getText().toString().trim();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait......");

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        serviceHandler.checkSubscription(MSISDN, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                final UserLogin userLogin = (UserLogin) data.get(0);
                                postSubscriptionId = userLogin.subscriptionId;
                                if (userLogin.status.equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Subscription Found!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    airtimeProductSpinner.setVisibility(View.VISIBLE);
                                                    getAirtimeProducts();
                                                }
                                            });
                                    alertDialog.show();

                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("STATUS:" + userLogin.status.toString());
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

        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                String resellerId = UserSession.getResellerId(activity);
                float amount =Float.parseFloat( airtimeProductSpinner.getSelectedItem().toString());

                try{
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait......");

                    serviceHandler.postAirtimeDirectRecharge(resellerId, postSubscriptionId, amount, false, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin response = (UserLogin)data.get(0);
                            if(response.status.equals("success")){

                                ProgressDialogUtil.stopProgressDialog(progressDialog);

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Message!");
                                alertDialog.setMessage("Successfully Recharged.");
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                alertDialog.show();

                            }else if(response.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                if(response.immediateRecharge != null){
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if(response.immediateRecharge){

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setTitle("Message!");
                                        alertDialog.setMessage("Status: "+response.status);
                                        alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        immediateRecharge(resellerId, postSubscriptionId, amount, true);
                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertDialog.show();
                                    }else{
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setTitle("Message!");
                                        alertDialog.setMessage("STATUS:" + response.status.toString());
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        callParentActivity();
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Message!");
                                    alertDialog.setMessage("STATUS:" + response.status.toString());
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    callParentActivity();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity,Constants.emailId,"Error"+error+", STATUS"+status, "");
                        }
                    });

                }catch (IOException e){
                    BugReport.postBugReport(activity,Constants.emailId,"Error"+e.getCause()+", Message"+e.getMessage(), "");
                }
            }
        });
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity,AirtimeValueRechargesActivity.class);
        startActivity(intent);
    }

    private void immediateRecharge(String resellerId, String postSubscriptionId, float amount, boolean b) {
        try{
            RestServiceHandler serviceHandler = new RestServiceHandler();
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait......");

            serviceHandler.postAirtimeDirectRecharge(resellerId, postSubscriptionId, amount, b, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin response = (UserLogin)data.get(0);
                    if(response.status.equals("success")){

                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Message!");
                        alertDialog.setMessage("Successfully Recharged.");
                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        callParentActivity();
                                    }
                                });
                        alertDialog.show();

                    }else if(response.status.equals("INVALID_SESSION")){
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        ReDirectToParentActivity.callLoginActivity(activity);
                    }else{
                        if(response.immediateRecharge != null){
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            if(response.immediateRecharge){

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Message!");
                                alertDialog.setMessage("Status: "+response.status);
                                alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                immediateRecharge(resellerId, postSubscriptionId, amount, true);
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        callParentActivity();
                                    }
                                });
                                alertDialog.show();
                            }else{
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Message!");
                                alertDialog.setMessage("STATUS:" + response.status.toString());
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }else{
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Message!");
                            alertDialog.setMessage("STATUS:" + response.status.toString());
                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            callParentActivity();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity,Constants.emailId,"Error"+error+", STATUS"+status, "");
                }
            });

        }catch (IOException e){
            BugReport.postBugReport(activity,Constants.emailId,"Error"+e.getCause()+", Message"+e.getMessage(), "");
        }
    }


    private void getAirtimeProducts() {
        RestServiceHandler serviceHandler =  new RestServiceHandler();
        try {
            progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, fetching all products...");

            serviceHandler.getAirtimeProductsForRecharge(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    AirtimeValue airtimeValue = (AirtimeValue)data.get(0);
                    if(airtimeValue != null){
                        if(airtimeValue.status.equals("success")){
                            if(airtimeValue.airtimeProducts != null){
                                if(airtimeValue.airtimeProducts.size() != 0){
                                    airtimeProductSpinner.setVisibility(View.VISIBLE);
                                    rechargeButton.setVisibility(View.VISIBLE);

                                    airtimeProductsList = airtimeValue.airtimeProducts;

                                    List<Float> airtimeValuesList  = new ArrayList<>();
                                    for(AirtimeValue aValues: airtimeValue.airtimeProducts){
                                        if( aValues.voucherChannelEnabled != null){
                                            if(aValues.voucherChannelEnabled)
                                            airtimeValuesList.add(aValues.airtimeValue);
                                        }
                                    }
                                    Float[] artimeValues = new Float[airtimeValuesList.size()];
                                    airtimeValuesList.toArray(artimeValues);

                                    ArrayAdapter<Float> adapter1 = new ArrayAdapter<Float>(activity, android.R.layout.simple_spinner_item, artimeValues);
                                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                    airtimeProductSpinner.setAdapter(adapter1);

                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);

                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                                    rechargeButton.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                                rechargeButton.setVisibility(View.INVISIBLE);
                            }

                        }else if(airtimeValue.status.equals("INVALID_SESSION")){
                            ProgressDialogUtil.stopProgressDialog(progressDialog1);
                            ReDirectToParentActivity.callLoginActivity(activity);

                        }else{
                            ProgressDialogUtil.stopProgressDialog(progressDialog1);
                            MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                            rechargeButton.setVisibility(View.INVISIBLE);
                        }
                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                        rechargeButton.setVisibility(View.INVISIBLE);
                        MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity,Constants.emailId,"Error"+error+", STATUS"+status, "");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity,Constants.emailId,"Error"+e.getCause()+", Message"+e.getMessage(), "");
        }
    }



}
