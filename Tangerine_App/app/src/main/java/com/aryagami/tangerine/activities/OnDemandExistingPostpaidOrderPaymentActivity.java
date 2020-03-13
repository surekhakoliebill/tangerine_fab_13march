package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.FilePath;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnDemandExistingPostpaidOrderPaymentActivity extends AppCompatActivity {

    NewOrderCommand newOrderCommand = new NewOrderCommand();
    TextView totalPlanPrice, setupValue, depositValue, airtimeValue,notification;
    CheckBox fulfillmentCheck;
    Button placePostpaidOrder, backButton,uploadPaymentCopy;
    Activity activity = this;
    ProgressDialog progressDialog,progressDialog2;
    Float exSetupPrice, exTotalPlanPrice, exDepositValue;
    Spinner currencySpinner;
    String[] currencyType={"UGX","USD"};
    Uri pdfUri;
    Boolean isPdfFile = false;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    PdfDocumentData docData = new PdfDocumentData();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ondemand_existing_postpaid_order_payment_activity);

        totalPlanPrice = (TextView) findViewById(R.id.ex_plan_price_value);
        depositValue = (TextView) findViewById(R.id.ex_deposit_value);
        setupValue = (TextView) findViewById(R.id.ex_setup_value);
        airtimeValue = (TextView) findViewById(R.id.airtime_value);
        uploadPaymentCopy = (Button)findViewById(R.id.payment_copy);
        notification = (TextView)findViewById(R.id.dis_notification);


       // newOrderCommand = (NewOrderCommand) getIntent().getSerializableExtra("ExistingOrder");

        if (NewOrderCommand.getOnDemandNewOrderCommand() != null) {
            newOrderCommand = NewOrderCommand.getOnDemandNewOrderCommand();
            if(newOrderCommand != null) {
                setPaymentDetails(newOrderCommand);
            }
        }

        fulfillmentCheck = (CheckBox) findViewById(R.id.fulfillmentCheck);

        placePostpaidOrder = (Button) findViewById(R.id.place_order);
        backButton = (Button) findViewById(R.id.back_btn);

        fulfillmentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                newOrderCommand.fulfillmentDone = isChecked;

            }
        });

        currencySpinner = (Spinner)findViewById(R.id.currency_spinner);

        uploadPaymentCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreviousPdfData("payment_copy");
                selectAlertItem(activity,501 ,"payment_copy");
            }
        });

        placePostpaidOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectPaymentDetails();

                if(pdfDocumentDataList.size()!=0){

                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait..");

                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    serviceHandler.finishExitingOrder(newOrderCommand, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {

                            final UserLogin orderDetails = (UserLogin) data.get(0);
                            if(orderDetails.status != null) {
                                if (orderDetails.status.toString().equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    uploadPaymentCopy(orderDetails);

                                    if (orderDetails.subscriptions != null) {
                                        if (orderDetails.subscriptions.size() != 0)
                                            activateSubscription(orderDetails.subscriptions);
                                    }

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Order Placed Successfully.\n Order No:" + orderDetails.orderNo);
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    callNavigationActivity();
                                                }
                                            });

                                    alertDialog.show();
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    if (orderDetails.status.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        if (orderDetails.reason != null) {
                                            alertDialog.setMessage("Status:\t" + orderDetails.status + "\n Reason:" + orderDetails.reason);
                                        } else {
                                            alertDialog.setMessage("Status:\t" + orderDetails.status);
                                        }
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        callNavigationActivity();
                                                    }
                                                });

                                        alertDialog.show();
                                    }
                                }
                            }else{
                                MyToast.makeMyToast(activity,"Empty Response from Server.", Toast.LENGTH_SHORT);
                                callNavigationActivity();
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            callAlertDialogue("Error:"+error);
                            BugReport.postBugReport(activity, Constants.emailId, "Error" + error + "\t Status:\n" + status, "OnDemandExistingOrderPaymentActivity-> Auto ActivateSubscription");

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "Error" + e.getCause() + "\t Status:\n" + e.getMessage(), "OnDemandExistingOrderPaymentActivity-> Auto ActivateSubscription");
                }

                }else{
                    MyToast.makeMyToast(activity,"Please Upload Payment Copy", Toast.LENGTH_SHORT).show();
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandExistingPostpaidOrderPaymentActivity.this);
    }

    private void uploadPaymentCopy(final UserLogin orderDetails) {

        if (docData != null) {
            if (docData != null) {
                final String fileUrl = "reseller_documents/payment_documents/" + orderDetails.paymentId + "/,payment_copy" ;

                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                if(docData.pdfRwaData != null)
                    uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin login1 = (UserLogin) data.get(0);
                            if (login1.status.equals("success")) {
                                MyToast.makeMyToast(activity, "Payment Copy Uploaded Successfully.", Toast.LENGTH_LONG);

                            } else {
                                MyToast.makeMyToast(activity, "Payment Copy not Uploaded.", Toast.LENGTH_LONG);
                                // startNavigationActivity();
                                if (login1.status.equals("INVALID_SESSION")) {
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {
                                    MyToast.makeMyToast(activity, "Payment Copy not Uploaded.", Toast.LENGTH_LONG);
                                }
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");

                        }
                    });
            }
        }
    }

    private void callNavigationActivity() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        startActivity(intent);
    }

    private void activateSubscription(List<String> subscriptions) {

        for (String subscriptionId : subscriptions) {
            if (subscriptionId != null) {
                ActivateCommand command = new ActivateCommand();
                command.entityName = "subscription";
                command.activationId = subscriptionId.toString();
                command.reason = "AUTO ACTIVATION FOR NEW REGISTRATION";
                try {

                    progressDialog2 = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading fingerprint!");
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.activateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            if(userLogin.status != null) {
                                if (userLogin.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog2);
                                    callAlertDialogue("Subscription Activated Successfully. \n Current Status:" + userLogin.activationState);
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog2);
                                    if (userLogin.status.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        callAlertDialogue("Subscription Not Activated, \nReason:" + userLogin.status.toString());
                                        // MyToast.makeMyToast(activity, "Status not updated, \nStatus:" + userLogin.status.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                            }else{
                                MyToast.makeMyToast(activity,"Empty Response!", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog2);
                            callAlertDialogue("Error:"+error);
                            BugReport.postBugReport(activity, Constants.emailId, "Error" + error + "\t Status:\n" + status, "NewOrderPaymentActivity-> ActivateSubscription");

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "", "NewOrderPaymentActivity-> ActivateSubscription");
                }
            }
        }
    }

    private void callAlertDialogue( String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                       // callNavigationActivity();
                    }
                });
        alertDialog.show();
    }


    private void setPaymentDetails(NewOrderCommand orderCommand) {


        if (orderCommand.totalPlanPrice != null) {
            totalPlanPrice.setText(orderCommand.totalPlanPrice.toString());
        } else {
            totalPlanPrice.setText("0");
            orderCommand.totalPlanPrice = 0f;
        }

        if (orderCommand.setupPrice != null) {
            setupValue.setText(orderCommand.setupPrice.toString());
        } else {
            setupValue.setText(" 0".toString());
            orderCommand.setupPrice = 0f;
        }

        if (orderCommand.depositValue != null) {
            depositValue.setText(orderCommand.depositValue.toString());
        } else {
            depositValue.setText(" 0".toString());
        }
        if(orderCommand.airtimeValue != null) {
            airtimeValue.setText(String.valueOf(orderCommand.airtimeValue));
        }else {
            airtimeValue.setText(" 0");
        }
    }

    private void collectPaymentDetails() {

        if (!depositValue.getText().toString().isEmpty()) {

            newOrderCommand.depositValue = Float.parseFloat(depositValue.getText().toString());
        }else{
            newOrderCommand.depositValue = 0f;
        }
        NewOrderCommand.PaymentCommand paymentCommand = new NewOrderCommand.PaymentCommand();

        paymentCommand.entityType = "Order";
        paymentCommand.paymentMethod = "Cash";
        //  paymentCommand.amountPaid = 0f;

        if (newOrderCommand.setupPrice != null) {
            exSetupPrice = newOrderCommand.setupPrice;
        } else {
            exSetupPrice = 0f;
        }

        if (newOrderCommand.depositValue != null) {
            exDepositValue = newOrderCommand.depositValue;
        } else {
            exDepositValue = 0f;
        }


        if (newOrderCommand.totalPlanPrice != null) {
            exTotalPlanPrice = newOrderCommand.totalPlanPrice;
        } else {
            exTotalPlanPrice = 0f;
        }

        paymentCommand.amountPaid = exDepositValue +  exTotalPlanPrice;

        newOrderCommand.totalValue = paymentCommand.amountPaid;

        if (fulfillmentCheck.isChecked()) {
            newOrderCommand.fulfillmentDone = true;
        } else {
            newOrderCommand.fulfillmentDone = false;
        }

        newOrderCommand.paymentInfo = paymentCommand;
        newOrderCommand.resellerCode = UserSession.getResellerId(activity);
        newOrderCommand.productListings = new ArrayList<NewOrderCommand.ProductListing>();



        //  newOrderCommand.userInfo.currency = currencySpinner.getSelectedItem().toString();

    }


    public void selectAlertItem(final Activity activity, final int requestCode, final String documentName) {
        final CharSequence[] items = {activity.getResources().getString(R.string.upload_pdf), activity.getResources().getString(R.string.generate_pdf), activity.getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.Pdf_dialog);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(activity.getResources().getString(R.string.upload_pdf))) {

                    isPdfFile = true;
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select PDF"), requestCode);

                } else if (items[item].equals(activity.getResources().getString(R.string.generate_pdf))) {

                    Intent intent = new Intent(activity, ImageGridViewActivity.class);
                    intent.putExtra("key", 2);
                    intent.putExtra("documentPath", documentName);
                    startActivityForResult(intent, requestCode);

                } else if (items[item].equals(activity.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void clearPreviousPdfData(String documentName) {
        File document = new File(getExternalCacheDir(),documentName);

        String output ="capture.pdf";
        File outputFile = new File(document,output);

        if(outputFile.isFile()){
            outputFile.delete();
        }

        if (document.isDirectory())
        {
            String[] children = document.list();
            if(children != null |children.length!=0) {
                for (int i = 0; i < children.length; i++) {
                    new File(document, children[i]).delete();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.vendor_fragment);
        fragment.onActivityResult(requestCode, resultCode, data);*/
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 501 && resultCode == RESULT_OK) {


            // Newlly added code-

            if(data != null)
                if(data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                }else{
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {

                docData.displayName = "payment_copy";
                docData.docType = "payment_copy";
                docData.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {

                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData.pdfRwaData = encodeData;

                        pdfDocumentDataList.add(docData);

                        notification.setText("A file is selected : payment_copy.pdf");
                    }else if (encodeData.equals("File Not Found")){

                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){

                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 1 MB.", Toast.LENGTH_SHORT);
                    }

                }

            }
        }
    }


}
