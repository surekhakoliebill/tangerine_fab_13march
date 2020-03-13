package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.CacheNewOrderData;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.data.Subscription;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.FilePath;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OnDemandNewOrderPaymentActivity extends AppCompatActivity {

    EditText name, emailId, chequeNumber, chequeIssuer, bankDetails, date;
    Button backButton, placePrepaidOrderBtn,uploadPaymentCopy;
    Activity activity = this;
    NewOrderCommand command;
    ProgressDialog progressDialog, progressDialog2,progressDialog1;
    LinearLayout billingInformationLayout, chequeLayout;
    RadioGroup radioGroup, radioGroup1;
    TextView setupValue, totalPrice, depositValue,notification;
    int imageUploadSuccessCount = 0;
    int pdfDocUploadedCount = 0;
    int passportUploadSuccessCount = 0;
    String filePrefix = "";
    String filename = "";
    Uri pdfUri;
    UserRegistration registration;
    String filename1 = "";
    String filename2 = "";
    CheckBox fullfillmentCheck;
    Boolean isPdfFile = false;
    int actualDocCount = 0;
    Account account;
    NewOrderCommand.PaymentCommand paymentCommand = new NewOrderCommand.PaymentCommand();
    List<PdfDocumentData> pdfDocumentList;
    RadioButton orderComplete, orderPending, cash, cheque, billInvoice;
    int fingerprintUploadSuccessCount = 0;
    TextView airtimeValue;
    EditText depositValueText;
    Button saveForLaterButton;
    Spinner currencySpinner;
    String[] currencyType = {"UGX", "USD"};
    LinearLayout currencyLayout;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    PdfDocumentData docData = new PdfDocumentData();
    LinearLayout depositLayout;
    int i;
    int docUploadCount = 0;
    Boolean documentUploadPending = false;
    String uuid;
    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_ondemand_payment_activity);

        uuid = UUID.randomUUID().toString();
        name = (EditText) findViewById(R.id.name_value);
        emailId = (EditText) findViewById(R.id.email_id_value);
        TextView emailText = (TextView) findViewById(R.id.email);
        airtimeValue = (TextView) findViewById(R.id.deposite_value1);

        depositValueText = (EditText) findViewById(R.id.depo_value);

        uploadPaymentCopy = (Button)findViewById(R.id.payment_copy);
        notification = (TextView)findViewById(R.id.dis_notification);
        depositLayout = (LinearLayout)findViewById(R.id.deposit_value_layout);

        if (NewOrderCommand.getOnDemandNewOrderCommand() != null) {
            command = NewOrderCommand.getOnDemandNewOrderCommand();

                if(command.airtimeValue != null) {
                    airtimeValue.setText( String.valueOf(Math.round(command.airtimeValue))+" UGX");
                }else{
                    airtimeValue.setText("0 UGX");
                }

        } else {
            command = new NewOrderCommand();
        }

        if (RegistrationData.getOnDemandRegistrationData() != null) {
            registration = RegistrationData.getOnDemandRegistrationData();
            if (command.isNewAccount) {
                if (registration.userName != null) {
                    name.setText(registration.userName);
                } else {
                    name.setText("");
                }

                if (registration.email != null) {
                    emailId.setText(registration.email);
                } else {
                    emailId.setText("");
                }
            }
        }

        if (!command.isNewAccount) {
            if (NewOrderCommand.getExistingAccountDetails() != null) {
                emailText.setText("Account No-");
                account = NewOrderCommand.getExistingAccountDetails();
                if (account.username != null) {
                    name.setText(account.username);
                } else {
                    name.setText("");
                }

                if (account.accountId != null) {
                    emailId.setText(account.accountId);
                } else {
                    emailId.setText("");
                }
            }
        }

        currencySpinner = (Spinner) findViewById(R.id.currency_spinner);
        currencyLayout = (LinearLayout) findViewById(R.id.currency_layout);

        if (command.isPostpaid) {
            currencyLayout.setVisibility(View.VISIBLE);
            depositLayout.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, currencyType);
            adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            currencySpinner.setAdapter(adapter2);
        } else {
            currencyLayout.setVisibility(View.GONE);
            depositLayout.setVisibility(View.GONE);
        }

        billingInformationLayout = (LinearLayout) findViewById(R.id.billing_information_layout);

        chequeLayout = (LinearLayout) findViewById(R.id.cheque_layout);
        chequeNumber = (EditText) findViewById(R.id.cheque_number);
        chequeIssuer = (EditText) findViewById(R.id.cheque_issuer);
        bankDetails = (EditText) findViewById(R.id.bank_details);
        date = (EditText) findViewById(R.id.date);

        fullfillmentCheck = (CheckBox) findViewById(R.id.fulfillmentCheck);
        setupValue = (TextView) findViewById(R.id.setup_value);
        totalPrice = (TextView) findViewById(R.id.planprice_value);
        depositValue = (TextView) findViewById(R.id.deposite_value);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup1 = (RadioGroup) findViewById(R.id.radio_group1);

        cash = (RadioButton) findViewById(R.id.cash);
        cheque = (RadioButton) findViewById(R.id.cheque);
        billInvoice = (RadioButton) findViewById(R.id.bill);

        orderComplete = (RadioButton) findViewById(R.id.complete_order);
        orderPending = (RadioButton) findViewById(R.id.order_pending);
        backButton = (Button) findViewById(R.id.back_btn);
        saveForLaterButton = (Button) findViewById(R.id.save_for_later);

        placePrepaidOrderBtn = (Button) findViewById(R.id.place_order);

        setBillingDetails();

        if (command.depositValue != null && command.isPostpaid) {
            billingInformationLayout.setVisibility(View.GONE);

        } else if (!command.isPostpaid) {
            billingInformationLayout.setVisibility(View.GONE);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uploadPaymentCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreviousPdfData("payment_copy");
                selectAlertItem(activity, 500, "payment_copy");
            }
        });

        fullfillmentCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fullfillmentCheck.isChecked()) {
                    command.fulfillmentDone = false;
                } else {
                    command.fulfillmentDone = true;
                }
            }
        });


        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cash:
                        paymentCommand.paymentMethod = "CASH";
                        chequeLayout.setVisibility(View.GONE);
                        break;
                    case R.id.cheque:
                        paymentCommand.paymentMethod = "CHEQUE";
                        chequeLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.bill:
                        paymentCommand.paymentMethod = "Bill Invoice";
                        chequeLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });

        /*placePrepaidOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    command = fetchAllDetails();

                    if (command != null) {

                        if (command.isPostpaid) {
                            command.registrationServiceType = "Postpaid";
                            command.userInfo.currency = currencySpinner.getSelectedItem().toString();
                        } else {
                            command.registrationServiceType = "Prepaid";
                        }

                        if (!depositValueText.getText().toString().isEmpty()) {
                            command.depositValue = Float.parseFloat(depositValueText.getText().toString());
                        } else {
                            command.depositValue = 0.0f;
                        }

                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                        if (command.isNewAccount) {
                            serviceHandler.postNewOrder(command, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);

                                    if(orderDetails.status != null) {
                                        if (orderDetails.status.equals("success")) {
                                            UserSession.setAllUserInformation(activity, null);

                                            ProgressDialogUtil.stopProgressDialog(progressDialog);

                                            if (registration.registrationType.equals("company")) {

                                                pdfDocumentList = PdfDocumentData.getFinalDocsList();

                                                if (pdfDocumentList != null) {
                                                    for (PdfDocumentData docData : pdfDocumentList) {
                                                        if (docData != null) {
                                                            actualDocCount++;
                                                        }
                                                    }
                                                    for (final PdfDocumentData docData : pdfDocumentList) {
                                                        if (docData != null) {
                                                            final String fileUrl = "documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_");

                                                            progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading Company Documents!");
                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                                                            uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin login1 = (UserLogin) data.get(0);
                                                                    if (login1.status.equals("success")) {
                                                                        if (++pdfDocUploadedCount == actualDocCount) {
                                                                            ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                                                            PdfDocumentData.setFinalDocsList(null);
                                                                            MyToast.makeMyToast(activity, "Company Documents Uploaded Successfully.", Toast.LENGTH_LONG);
                                                                            //  startNavigationActivity();
                                                                        }
                                                                    } else {
                                                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                                                        MyToast.makeMyToast(activity, "Company Documents not Uploaded.", Toast.LENGTH_LONG);
                                                                        // startNavigationActivity();
                                                                    }

                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                                                    BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");
                                                                    // activity.finish();
                                                                    //startNavigationActivity();
                                                                }
                                                            });
                                                        }

                                                    }
                                                } else {
                                                    Toast.makeText(activity, "DocumentList is Empty.", Toast.LENGTH_SHORT).show();
                                                }


                                                // Upload Activation Form for Company
                                                if (RegistrationData.getActivationImages() != null) {

                                                    String prodPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!prodPicDir.isEmpty())
                                                            prodPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        prodPicDir += "documents/activation/" + user.userId + "/";
                                                    }
                                                    int imageNum = 0;
                                                    imageUploadSuccessCount = 0;
                                                    if (RegistrationData.getActivationImages().size() != 0)
                                                        for (View view : RegistrationData.getActivationImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename1 = RegistrationData.getFilePrefix() + "_activation_" + (imageNum++);
                                                            } else {
                                                                filename1 = "activation_" + (imageNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", prodPicDir + "," + filename1, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++imageUploadSuccessCount == RegistrationData.getActivationImages().size()) {
                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setActivationImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    // startNavigationActivity();
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }


                                                if (docData != null) {
                                                    if (docData != null) {
                                                        final String fileUrl = "reseller_documents/payment_documents/" + orderDetails.orderNo + "/,payment_copy";

                                                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                                                        if (docData.pdfRwaData != null)
                                                            uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin login1 = (UserLogin) data.get(0);
                                                                    if (login1.status.equals("success")) {
                                                                        MyToast.makeMyToast(activity, "Payment Copy Uploaded Successfully.", Toast.LENGTH_LONG);

                                                                    } else {
                                                                        MyToast.makeMyToast(activity, "Payment Copy not Uploaded.", Toast.LENGTH_LONG);
                                                                        // startNavigationActivity();
                                                                    }

                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");

                                                                }
                                                            });
                                                    }
                                                }
                                            } else if (registration.registrationType.equals("personal")) {

                                                //Upload Nin Document
                                                if (RegistrationData.getNinIdImages() != null) {

                                                    String prodPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!prodPicDir.isEmpty())
                                                            prodPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        prodPicDir += "documents/nin/" + user.userId + "/";
                                                    }
                                                    int imageNum = 0;
                                                    imageUploadSuccessCount = 0;
                                                    if (RegistrationData.getNinIdImages().size() != 0)
                                                        for (View view : RegistrationData.getNinIdImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename1 = RegistrationData.getFilePrefix() + "_nin_" + (imageNum++);
                                                                //   filename1 = RegistrationData.getFilePrefix() + "_" + (imageNum++);
                                                            } else {
                                                                filename1 = "nin_" + (imageNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", prodPicDir + "," + filename1, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {
                                                                        if (++imageUploadSuccessCount == RegistrationData.getNinIdImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }

                                                }

                                                //Upload Profile
                                                if (RegistrationData.getProfileImages() != null) {

                                                    String passportPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!passportPicDir.isEmpty())
                                                            passportPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        passportPicDir += "documents/profile/" + user.userId + "/";
                                                    }
                                                    int passportDocsNum = 0;
                                                    passportUploadSuccessCount = 0;
                                                    if (RegistrationData.getProfileImages().size() != 0)
                                                        for (View view : RegistrationData.getProfileImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename = RegistrationData.getFilePrefix() + "_profile_" + (passportDocsNum++);
                                                                //  filename = RegistrationData.getFilePrefix() + "_" + (passportDocsNum++);
                                                            } else {
                                                                filename = "profile_" + (passportDocsNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++passportUploadSuccessCount == RegistrationData.getProfileImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);

                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }
                                                // Upload Passport Images
                                                if (RegistrationData.getPassportIdImages() != null) {

                                                    String passportPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!passportPicDir.isEmpty())
                                                            passportPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        passportPicDir += "documents/passport/" + user.userId + "/";
                                                    }
                                                    int passportDocsNum = 0;
                                                    passportUploadSuccessCount = 0;
                                                    if (RegistrationData.getPassportIdImages().size() != 0)
                                                        for (View view : RegistrationData.getPassportIdImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename = RegistrationData.getFilePrefix() + "_passport_" + (passportDocsNum++);
                                                                // filename = RegistrationData.getFilePrefix() + "_" + (passportDocsNum++);
                                                            } else {
                                                                filename = "passport_" + (passportDocsNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++passportUploadSuccessCount == RegistrationData.getPassportIdImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }

                                                // Upload visa Images
                                                if (RegistrationData.getVisaImages() != null) {

                                                    String passportPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!passportPicDir.isEmpty())
                                                            passportPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        passportPicDir += "documents/visa/" + user.userId + "/";
                                                    }
                                                    int passportDocsNum = 0;
                                                    passportUploadSuccessCount = 0;
                                                    if (RegistrationData.getVisaImages().size() != 0)
                                                        for (View view : RegistrationData.getVisaImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename = RegistrationData.getFilePrefix() + "_visa_" + (passportDocsNum++);
                                                                // filename = RegistrationData.getFilePrefix() + "_" + (passportDocsNum++);
                                                            } else {
                                                                filename = "visa_" + (passportDocsNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++passportUploadSuccessCount == RegistrationData.getVisaImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setVisaImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }
                                                // UPLOAD REFUGEE IMAGES

                                                if (RegistrationData.getRefugeeImages() != null) {

                                                    String passportPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!passportPicDir.isEmpty())
                                                            passportPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        passportPicDir += "documents/refugee/" + user.userId + "/";
                                                    }
                                                    int passportDocsNum = 0;
                                                    passportUploadSuccessCount = 0;
                                                    if (RegistrationData.getRefugeeImages().size() != 0)
                                                        for (View view : RegistrationData.getRefugeeImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename = RegistrationData.getFilePrefix() + "_refugee_" + (passportDocsNum++);
                                                                // filename = RegistrationData.getFilePrefix() + "_" + (passportDocsNum++);
                                                            } else {
                                                                filename = "refugee_" + (passportDocsNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++passportUploadSuccessCount == RegistrationData.getRefugeeImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setRefugeeImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }

                                                //UPLOAD ACTIVATION REQUEST FORM
                                                if (RegistrationData.getActivationImages() != null) {

                                                    String passportPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!passportPicDir.isEmpty())
                                                            passportPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        passportPicDir += "documents/activation/" + user.userId + "/";
                                                    }
                                                    int passportDocsNum = 0;
                                                    passportUploadSuccessCount = 0;
                                                    if (RegistrationData.getActivationImages().size() != 0)
                                                        for (View view : RegistrationData.getActivationImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename = RegistrationData.getFilePrefix() + "_activation_" + (passportDocsNum++);
                                                                // filename = RegistrationData.getFilePrefix() + "_" + (passportDocsNum++);
                                                            } else {
                                                                filename = "activation_" + (passportDocsNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++passportUploadSuccessCount == RegistrationData.getActivationImages().size()) {

                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setActivationImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                        }
                                                }

                                                // Upload Thumb & Index FingerPrint
                                                if (RegistrationData.getIsFingerPrint()) {

                                                    int imageCount = 0;
                                                    String fileName = "";

                                                    String fingerPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!fingerPicDir.isEmpty())
                                                            fingerPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        fingerPicDir += "documents/fingerprints/" + user.userId + "/";
                                                    }
                                                    if (RegistrationData.getUserFingerprintsList() != null && RegistrationData.getUserFingerprintsList().size() != 0)
                                                        for (Drawable dbImage : RegistrationData.getUserFingerprintsList()) {

                                                            Bitmap bitmap = PictureUtility.drawableToBitmap(dbImage);
                                                            String imageData = PictureUtility.encodePicutreBitmap(bitmap);

                                                            if (orderDetails.username != null) {
                                                                if (imageCount == 0) {
                                                                    fileName = orderDetails.username + "_thumb_fingerPrint_" + imageCount;
                                                                } else {
                                                                    fileName = orderDetails.username + "_index_fingerPrint_" + imageCount;
                                                                }
                                                            } else {
                                                                if (imageCount == 0) {
                                                                    fileName = RegistrationData.getFilePrefix() + "_thumb_fingerPrint_" + imageCount;
                                                                } else {
                                                                    fileName = RegistrationData.getFilePrefix() + "_index_fingerPrint_" + imageCount;
                                                                }
                                                            }


                                                            //   String directoryPath = "fingerprints/" + orderDetails.userId + "/"; //UserSession.getUserId(getApplicationContext()).toString()+"/";

                                                            RestServiceHandler serviceHandler = new RestServiceHandler();

                                                            serviceHandler.uploadPdf("jpeg", fingerPicDir + "," + fileName, imageData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userInfo = (UserLogin) data.get(0);
                                                                    if (userInfo.status.equals("success")) {

                                                                        if (++fingerprintUploadSuccessCount == RegistrationData.getUserFingerprintsList().size()) {

                                                                            Toast.makeText(activity, getResources().getString(R.string.Successfully_uploaded_fingerprint), Toast.LENGTH_SHORT).show();

                                                                            RegistrationData.setUserThumbImageDrawable(null);
                                                                            RegistrationData.setUserIndexImageDrawable(null);
                                                                            RegistrationData.setUserFingerprintsList(null);
                                                                            System.gc();
                                                                        }

                                                                    } else {
                                                                        Toast.makeText(activity, "Status" + userInfo.status, Toast.LENGTH_SHORT).show();
                                                                        // startNavigationActivity();
                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                                }
                                                            });
                                                            imageCount++;

                                                        }
                                                }

                                                if (RegistrationData.getCapturedFingerprintDrawable() != null) {

                                                    String fileName = "";

                                                    String fingerPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!fingerPicDir.isEmpty())
                                                            fingerPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        fingerPicDir += "documents/fingerprints/" + user.userId + "/";
                                                    }

                                                    Bitmap bitmap = PictureUtility.drawableToBitmap(RegistrationData.getCapturedFingerprintDrawable());
                                                    String imageData = PictureUtility.encodePicutreBitmap(bitmap);

                                                    fileName = RegistrationData.getFilePrefix() + "_nin_fingerPrint_0";

                                                    RestServiceHandler serviceHandler = new RestServiceHandler();
                                                    serviceHandler.uploadPdf("jpeg", fingerPicDir + "," + fileName, imageData, new RestServiceHandler.Callback() {
                                                        @Override
                                                        public void success(DataModel.DataType type, List<DataModel> data) {
                                                            UserLogin userInfo = (UserLogin) data.get(0);
                                                            if (userInfo.status.equals("success")) {

                                                                RegistrationData.setCapturedFingerprintDrawable(null);
                                                                System.gc();

                                                            } else {
                                                                Toast.makeText(activity, "Status" + userInfo.status, Toast.LENGTH_SHORT).show();
                                                                // startNavigationActivity();
                                                            }
                                                        }

                                                        @Override
                                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                            BugReport.postBugReport(activity, Constants.emailId, "STATUS" + status + "\n ERROR:-" + error, "Activity");
                                                        }
                                                    });
                                                }


                                                if (docData != null) {
                                                    if (docData != null) {
                                                        final String fileUrl = "reseller_documents/payment_documents/" + orderDetails.orderNo + "/,payment_copy";

                                                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                                                        if (docData.pdfRwaData != null)
                                                            uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin login1 = (UserLogin) data.get(0);
                                                                    if (login1.status.equals("success")) {
                                                                        MyToast.makeMyToast(activity, "Payment Copy Uploaded Successfully.", Toast.LENGTH_LONG);

                                                                    } else {
                                                                        MyToast.makeMyToast(activity, "Payment Copy not Uploaded.", Toast.LENGTH_LONG);
                                                                        // startNavigationActivity();
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

                                            if (orderDetails.subscriptionList != null) {
                                                if (orderDetails.subscriptionList.size() > 0)
                                                    activateSubscription(orderDetails.subscriptionList);
                                            }

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setIcon(R.drawable.success_icon);
                                            alertDialog.setTitle("Success");
                                            alertDialog.setMessage("New Order Placed Successfully.\n Order No:" + orderDetails.orderNo.toString());
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            deleteFolderAndImage();
                                                            startNavigationActivity();
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
                                                //alertDialog.setIcon(R.drawable.success_icon);
                                                alertDialog.setTitle("Message");
                                                alertDialog.setMessage("Status" + orderDetails.status + "\n Reason:" + orderDetails.reason);
                                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.dismiss();
                                                                // deleteFolderAndImage();
                                                                startNavigationActivity();
                                                            }
                                                        });

                                                alertDialog.show();
                                            }
                                            //  Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                                            // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            //startNavigationActivity();
                                        }
                                    }else{
                                        MyToast.makeMyToast(activity,"Empty Response from Server.",Toast.LENGTH_SHORT);
                                        startNavigationActivity();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:"+status+"ERROR"+error, "OnDemand NEW ORDER PAYMENT ACTIVITY.");

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Order Not Placed, Would you like to upload data in the background?");
                                    alertDialog.setPositiveButton(getResources().getString(R.string.save_alert),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    saveInCache();
                                                    MyToast.makeMyToast(activity, "DATA SAVED IN CACHE", Toast.LENGTH_SHORT);
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startNavigationActivity();
                                                }
                                            });

                                    alertDialog.show();
                                }
                            });
                        } else {

                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                            if (account.accountId != null) {
                                command.userInfo.accountId = account.accountId;
                            }
                            serviceHandler.postNewOrder(command, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if (orderDetails.status.equals("success")) {
                                        UserSession.setAllUserInformation(activity, null);

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);


                                        if (orderDetails.subscriptionList != null) {
                                            if (orderDetails.subscriptionList.size() > 0)
                                                activateSubscription(orderDetails.subscriptionList);
                                        }

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.success_icon);
                                        alertDialog.setMessage("Order Placed Successfully.\n Order No:" + orderDetails.orderNo.toString());
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                       // deleteFolderAndImage();
                                                        startNavigationActivity();
                                                    }
                                                });

                                        alertDialog.show();
                                    } else {

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                       // MyToast.makeMyToast(activity, "Status: " + orderDetails.status + "\t Reason:" + orderDetails.reason, Toast.LENGTH_SHORT);
                                        *//*activity.finish();
                                        startNavigationActivity();*//*

                                        if (orderDetails.status.equals("INVALID_SESSION")) {
                                            ReDirectToParentActivity.callLoginActivity(activity);
                                        } else {

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setIcon(R.drawable.success_icon);
                                            alertDialog.setMessage("Status:" + orderDetails.status+"\n Reason:"+orderDetails.reason);
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            // deleteFolderAndImage();
                                                            startNavigationActivity();
                                                        }
                                                    });

                                            alertDialog.show();
                                        }

                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                     BugReport.postBugReport(activity,Constants.emailId,"ERROR:"+error+"\n STATUS:"+status,"POSTPAID-EXISTING ORDER");
                                    startNavigationActivity();
                                }
                            });
                        }
                    }else{
                        command = NewOrderCommand.getOnDemandNewOrderCommand();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:"+e.getMessage(), "CONTRACT INFOrmatioin");

                }
            }
        });*/

        placePrepaidOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    command = fetchAllDetails();

                    if (command != null) {

                        if (command.isPostpaid) {
                            command.registrationServiceType = "Postpaid";
                            command.userInfo.currency = currencySpinner.getSelectedItem().toString();
                        } else {
                            command.registrationServiceType = "Prepaid";
                        }

                        if (!depositValueText.getText().toString().isEmpty()) {
                            command.depositValue = Float.parseFloat(depositValueText.getText().toString());
                        } else {
                            command.depositValue = 0.0f;
                        }

                        if (command.isNewAccount) {

                            uploadPrepaidNewOrderDocuments();
                        } else {

                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                            if (account.accountId != null) {
                                command.userInfo.accountId = account.accountId;
                            }
                            serviceHandler.postNewOrder(command, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if (orderDetails.status.equals("success")) {
                                        UserSession.setAllUserInformation(activity, null);

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);


                                        if (orderDetails.subscriptionList != null) {
                                            if (orderDetails.subscriptionList.size() > 0)
                                                activateSubscription(orderDetails.subscriptionList);
                                        }

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.success_icon);
                                        alertDialog.setMessage("Order Placed Successfully.\n Order No:" + orderDetails.orderNo.toString());
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        // deleteFolderAndImage();
                                                        startNavigationActivity();
                                                    }
                                                });

                                        alertDialog.show();
                                    } else {

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        // MyToast.makeMyToast(activity, "Status: " + orderDetails.status + "\t Reason:" + orderDetails.reason, Toast.LENGTH_SHORT);
                                        /*activity.finish();
                                        startNavigationActivity();*/

                                        if (orderDetails.status.equals("INVALID_SESSION")) {
                                            ReDirectToParentActivity.callLoginActivity(activity);
                                        } else if (orderDetails.status.equalsIgnoreCase("System Error")){

                                            BugReport.postBugReport(activity, Constants.emailId,"Status: "+orderDetails.status+"Reason: "+orderDetails.reason,"New_order");

                                        } else {

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Alert!");
                                            if(orderDetails.reason != null) {
                                                alertDialog.setMessage(orderDetails.status + "\n Reason:" + orderDetails.reason);
                                            }else{
                                                alertDialog.setMessage(orderDetails.status);
                                            }
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            // deleteFolderAndImage();
                                                            startNavigationActivity();
                                                        }
                                                    });

                                            alertDialog.show();
                                        }

                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"\n STATUS:"+status,"POSTPAID-EXISTING ORDER");
                                    startNavigationActivity();
                                }
                            });
                        }
                    }else{
                        command = NewOrderCommand.getOnDemandNewOrderCommand();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:"+e.getMessage(), "CONTRACT Information");

                }
            }
        });


        saveForLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInCache();
            }
        });

    }

    private void uploadPrepaidNewOrderDocuments() {

        if (RegistrationData.getPersonalRegistrationUserDocs() != null) {

            if(RegistrationData.getPersonalRegistrationUserDocs().size() != 0) {

                // pendingDocumentsList = RegistrationData.getPersonalRegistrationUserDocs();
                progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "please wait, Uploading Documents...!");

                final int totalDocs = RegistrationData.getPersonalRegistrationUserDocs().size();
                final UserRegistration.UserDocCommand[] docCommand = new UserRegistration.UserDocCommand[RegistrationData.getPersonalRegistrationUserDocs().size()];
                RegistrationData.getPersonalRegistrationUserDocs().toArray(docCommand);
                for (  i=0; i<totalDocs; i++) {
                    String prodPicDir = "temp_documents/" + docCommand[i].docType +"/"+ uuid + "/";

                    String[] docName = docCommand[i].docFiles.split(";");
                    filename1 = docName[0];
                    imageUploadSuccessCount = 0;

                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                    String imageEncodedData;
                    if(docCommand[i].docFormat.equals("pdf")){
                        imageEncodedData = docCommand[i].pdfRwaData;
                    }else{
                        imageEncodedData = docCommand[i].imageData;
                    }
                    uploadImageServiceHandler.uploadPdf(docCommand[i].docFormat, prodPicDir + "," + filename1, imageEncodedData, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            if (userLogin.status.equals("success")) {

                                if (++imageUploadSuccessCount == totalDocs) {
                                    command.userInfo.documentsUploadPending = false;
                                   // MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                    RegistrationData.setPersonalRegistrationUserDocs(null);
                                    RegistrationData.setUserThumbImageDrawable(null);
                                    RegistrationData.setUserIndexImageDrawable(null);
                                    RegistrationData.setRefugeeThumbImageDrawable(null);
                                    RegistrationData.setCapturedFingerprintDrawable(null);
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);

                                    deleteFolderAndImage();
                                    placePrepaidNewOrder();
                                }/*else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Alert!");
                                    alertDialog.setMessage("Unable to upload your documents, please retry!");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startNavigationActivity();
                                        }
                                    });
                                    alertDialog.show();
                                }*/


                            } else if (userLogin.status.equals("INVALID_SESSION")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            } else if(!userLogin.status.isEmpty()){
                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                registerUserAfterDocUploadFail();

                            }
                            ++docUploadCount;
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ++docUploadCount;
                            ProgressDialogUtil.stopProgressDialog(progressDialog1);
                            BugReport.postBugReport(activity, Constants.emailId, "STATUS:" + status + "ERROR:" + error, "USER_DOCS_NOT_UPLOADED");

                            /*if(i<totalDocs)
                                pendingDocumentsList.add(docCommand[i]);
*/
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Alert!");
                            alertDialog.setMessage("Unable to upload your documents, please retry!");
                            alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Cache Background", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    registerUserAfterDocUploadFail();
                                }
                            });
                            alertDialog.show();
                        }
                    });

                }

            }
        }
    }

    private void registerUserAfterDocUploadFail() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Want to upload the documents in the background?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                command.userInfo.documentsUploadPending = true;
                documentUploadPending = true;
                placePrepaidNewOrder();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startNavigationActivity();
            }
        });
        alertDialog.show();

    }


    private void placePrepaidNewOrder() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");

            serviceHandler.postNewOrder(command, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    final UserLogin orderDetails = (UserLogin) data.get(0);

                    if(orderDetails.status != null) {
                        if (orderDetails.status.equals("success")) {
                            UserSession.setAllUserInformation(activity, null);

                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            if (docData != null) {
                                if (docData != null) {
                                    final String fileUrl = "reseller_documents/payment_documents/" + orderDetails.orderNo + "/,payment_copy";

                                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                                    if (docData.pdfRwaData != null)
                                        uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                            @Override
                                            public void success(DataModel.DataType type, List<DataModel> data) {
                                                UserLogin login1 = (UserLogin) data.get(0);
                                                if (login1.status.equals("success")) {
                                                  //  MyToast.makeMyToast(activity, "Payment Copy Uploaded Successfully.", Toast.LENGTH_LONG);

                                                } else {
                                                   // MyToast.makeMyToast(activity, "Payment Copy not Uploaded.", Toast.LENGTH_LONG);
                                                    // startNavigationActivity();
                                                }

                                            }

                                            @Override
                                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");
                                            }
                                        });
                                }
                            }
                            if (orderDetails.subscriptionList != null) {
                                if (orderDetails.subscriptionList.size() > 0)
                                    activateSubscription(orderDetails.subscriptionList);
                            }

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setIcon(R.drawable.success_icon);
                            alertDialog.setTitle("Success");
                            alertDialog.setMessage("New Order Placed Successfully.\n Order No:" + orderDetails.orderNo.toString());
                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();

                                            if(documentUploadPending){
                                                postDocumentUploadComplete(orderDetails.userId);
                                            }else {
                                                RegistrationData.setOnDemandRegistrationData(null);
                                                NewOrderCommand.setOnDemandNewOrderCommand(null);
                                                deleteFolderAndImage();
                                                startNavigationActivity();
                                            }
                                        }
                                    });

                            alertDialog.show();
                        } else {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);


                            if (orderDetails.status.equals("INVALID_SESSION")) {
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else if(orderDetails.status.equalsIgnoreCase("System Error")){

                                BugReport.postBugReport(activity, Constants.emailId,"Status: "+orderDetails.status+"Reason: "+orderDetails.reason,"New_order");

                            } else {

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                //alertDialog.setIcon(R.drawable.success_icon);
                                alertDialog.setTitle("Message");
                                if(orderDetails.reason != null) {
                                    alertDialog.setMessage("Status" + orderDetails.status + "\n Reason:" + orderDetails.reason);
                                }else{
                                    alertDialog.setMessage(orderDetails.status);
                                }
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                // deleteFolderAndImage();
                                                startNavigationActivity();
                                            }
                                        });

                                alertDialog.show();
                            }
                            //  Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                            // ProgressDialogUtil.stopProgressDialog(progressDialog);
                            //startNavigationActivity();
                        }
                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        MyToast.makeMyToast(activity,"Empty Response from Server.", Toast.LENGTH_SHORT);
                        startNavigationActivity();
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:"+status+"ERROR"+error, "OnDemand NEW ORDER PAYMENT ACTIVITY.");

                    /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    alertDialog.setCancelable(false);
                    alertDialog.setIcon(R.drawable.success_icon);
                    alertDialog.setMessage("Order Not Placed, Would you like to upload data in the background?");
                    alertDialog.setPositiveButton(getResources().getString(R.string.save_alert),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveInCache();
                                    MyToast.makeMyToast(activity, "DATA SAVED IN CACHE", Toast.LENGTH_SHORT);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startNavigationActivity();
                                }
                            });

                    alertDialog.show();*/

                    if(status.contains("Error-Code: Unknown IO Exception")){
                        alertMessageAfterSuccess("No Internet Connection!","You need to have Mobile Data or WiFi to access this.");
                    }else{
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Message!");
                        alertDialog.setMessage("Order Not Placed, please retry!");
                        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                                 /*setPositiveButton(getResources().getString(R.string.save_alert),
                                 new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                         saveInCache();
                                         dialog.dismiss();
                                         startNavigationActivity();
                                         // UploadCacheDataInTheBackground();
                                     }
                                 })*/


                        alertDialog.show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void postDocumentUploadComplete(String userId) {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog2 = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");

            serviceHandler.postDocumentUploadComplete(userId, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    UserLogin orderDetails = (UserLogin) data.get(0);
                    if (orderDetails.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog2);
                        if(orderDetails.reason != null) {
                            alertMessageAfterSuccess("Success!","Status:" + orderDetails.status + "\t Reason:" + orderDetails.reason);

                        }else{
                            alertMessageAfterSuccess("Success!","Status:" + orderDetails.status);

                        }

                    }else  if (orderDetails.status.equals("INVALID_SESSION")){
                        ReDirectToParentActivity.callLoginActivity(activity);
                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog2);
                        if(orderDetails.reason != null) {
                            alertMessageAfterSuccess("Message!","Status:" + orderDetails.status + "\t Reason:" + orderDetails.reason);
                        }else{
                            alertMessageAfterSuccess("Message!","Status:" + orderDetails.status);
                        }

                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog2);
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+status+"ERROR:"+error,"DocumentUploadComplete");
                    startNavigationActivity();
                }
            });

        }catch (Exception e){
            BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+",Cause:"+e.getCause(),"DocumentUploadComplete");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandNewOrderPaymentActivity.this);
    }

    private void activateSubscription(List<Subscription> subscriptions) {

        for (Subscription subscription : subscriptions) {
            if (subscription.subscriptionId != null) {
                ActivateCommand command = new ActivateCommand();
                command.entityName = "subscription";
                command.activationId = subscription.subscriptionId.toString();
                command.reason = "AUTO ACTIVATION FOR NEW REGISTRATION";
                try {

                    progressDialog2 = ProgressDialogUtil.startProgressDialog(activity, "please wait, Activating Subscriptions...!");
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.activateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            ProgressDialogUtil.stopProgressDialog(progressDialog2);
                           if(userLogin.status != null) {
                               if (userLogin.status.equals("success")) {

                                   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                   alertDialog.setCancelable(false);
                                   alertDialog.setMessage("Subscription Activated Successfully. \n Current Status: " + userLogin.activationState);
                                   alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                           new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                                   dialog.dismiss();
                                                   //  startNavigationActivity();
                                               }
                                           });
                                   alertDialog.show();
                               } else {
                                   // ProgressDialogUtil.stopProgressDialog(progressDialog2);
                                   if (userLogin.status.equals("INVALID_SESSION")) {
                                       ReDirectToParentActivity.callLoginActivity(activity);
                                   } else {
                                       MyToast.makeMyToast(activity, "Status not updated, \nStatus:" + userLogin.status.toString(), Toast.LENGTH_SHORT);
                                   }
                               }
                           }else{
                               MyToast.makeMyToast(activity,"Empty Response From Server.", Toast.LENGTH_LONG);
                               startNavigationActivity();
                           }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog2);
                            BugReport.postBugReport(activity, Constants.emailId, "Error" + error + "\t Status:\n" + status, "NewOrderPaymentActivity-> ActivateSubscription");

                        }
                    });
                } catch (Exception e) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog2);
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "STATUS"+e.getMessage(), "NewOrderPaymentActivity-> ActivateSubscription");
                }
            }
        }
    }

    private void saveInCache() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Order is Not Placed, Would you like to upload data in the background?");
        alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                command = fetchAllDetails();

                if (command != null) {

                    if (command.isPostpaid) {
                        command.registrationServiceType = "Postpaid";
                    } else {
                        command.registrationServiceType = "Prepaid";
                    }
                }

                List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
                commandList = CacheNewOrderData.loadNewOrderCacheList(activity);
                if (commandList != null) {

                    commandList.add(command);
                    CacheNewOrderData.saveNewOrderDataCache(activity, commandList);

                    //alertMessageAfterSuccess();

                } else {
                    List<NewOrderCommand> commandList1 = new ArrayList<NewOrderCommand>();

                    commandList1.add(command);
                    CacheNewOrderData.saveNewOrderDataCache(activity, commandList1);

                    //alertMessageAfterSuccess();
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void alertMessageAfterSuccess() {
        ProgressDialogUtil.stopProgressDialog(progressDialog);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Congratulation! Data Saved Successfully.");
        alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startNavigationActivity();
                    }
                });
        alertDialog.show();
    }

    private void alertMessageAfterSuccess(String title, String message) {

        // ProgressDialogUtil.stopProgressDialog(progressDialog);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startNavigationActivity();
                    }
                });
        alertDialog.show();
    }

    private void deleteFolderAndImage() {

        // DELETING FOLDER & CONTENT FROM TANGERINE/TEMP
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tangerine/Temp");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null | children.length != 0) {
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }

        }

    }

    private void startNavigationActivity() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        startActivity(intent);
    }

    private void setBillingDetails() {

        if (command.setupPrice != null) {
            //setupValue.setText( Math.round(command.setupPrice)+" "+command.currencyType);
            setupValue.setText( String.valueOf(Math.round(command.setupPrice))+" UGX");
        } else {
            //setupValue.setText("0 "+command.currencyType);
            setupValue.setText("0 UGX");
        }

        if (command.totalPlanPrice != null) {
            //totalPrice.setText(Math.round(command.totalPlanPrice)+" "+command.currencyType);
            totalPrice.setText( String.valueOf(Math.round(command.totalPlanPrice))+" UGX");
        } else {
            //totalPrice.setText("0 "+command.currencyType);
            totalPrice.setText("0 UGX");
        }

        if (command.depositValue != null) {
            //depositValue.setText(Math.round(command.depositValue)+" "+command.currencyType);
            depositValue.setText( String.valueOf(Math.round(command.depositValue))+" UGX");
        } else {
            //depositValue.setText("0 "+command.currencyType);
            depositValue.setText("0 UGX");
        }
    }

    private NewOrderCommand fetchAllDetails() {

        coordinates.longitudeValue = RegistrationData.getCurrentLongitude();
        coordinates.latitudeValue = RegistrationData.getCurrentLatitude();
        command.resellerLocation = coordinates;
        command.productListingIds = new ArrayList<Long>();
        command.productListings = new ArrayList<NewOrderCommand.ProductListing>();

        command.resellerCode = UserSession.getResellerId(activity);
        if (cheque.isChecked()) {

            if (!chequeNumber.getText().toString().isEmpty()) {
                paymentCommand.chequeNumber = chequeNumber.getText().toString();
            } else {
                Toast.makeText(activity, "Enter Cheque Number", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (!chequeIssuer.getText().toString().isEmpty()) {
                paymentCommand.chequeIssuer = chequeIssuer.getText().toString();
            }/*else{
              Toast.makeText(activity,"Enter Cheque Number",Toast.LENGTH_SHORT).show();
          }*/
            if (!bankDetails.getText().toString().isEmpty()) {
                paymentCommand.bankDetail = bankDetails.getText().toString();
            }

            if (!date.getText().toString().isEmpty()) {
                paymentCommand.chequeDate = date.getText().toString();
            }
        }

        if(pdfDocumentDataList.size()!=0){

        }else{
            MyToast.makeMyToast(activity,"Please Upload Payment Copy", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (command.depositValue != null) {
            paymentCommand.amountPaid = command.setupPrice + command.totalPlanPrice + command.depositValue;
        } else {
            paymentCommand.amountPaid = command.setupPrice + command.totalPlanPrice;
        }
        command.totalValue = paymentCommand.amountPaid;

        if (cash.isChecked()) {
            paymentCommand.paymentMethod = "CASH";
        } else if (cheque.isChecked()) {
            paymentCommand.paymentMethod = "CHEQUE";
        } else {
            paymentCommand.paymentMethod = "CASH";
        }

        paymentCommand.entityType = "Order";

        command.paymentInfo = paymentCommand;
        if (command.isNewAccount != null) {
            if (command.isNewAccount) {
                command.userInfo.discountType = "Flat";
                setUserRoles(command.userInfo);
            } else {
                if (RegistrationData.getRegistrationData() != null) {
                    UserRegistration registration = RegistrationData.getRegistrationData();
                    registration.userId = account.userId.toString();
                    setUserRoles(registration);
                    command.userInfo = registration;
                } else {
                    UserRegistration registration = new UserRegistration();
                    registration.userId = account.userId.toString();
                    setUserRoles(registration);
                    command.userInfo = registration;
                }
            }
        }

        if (fullfillmentCheck.isChecked()) {
            command.fulfillmentDone = false;
        } else {
            command.fulfillmentDone = true;
        }

        command.userInfo.currency = "UGX";
        command.userInfo.tempUserToken = uuid;
        return command;

    }

    private void setUserRoles(UserRegistration registration) {
        if (RegistrationData.getRoles() != null && RegistrationData.getRoles().length != 0) {

            Roles[] rolesArray = new Roles[RegistrationData.getRoles().length];
            rolesArray = RegistrationData.getRoles();

            if (rolesArray.length != 0) {
                for (Roles role : rolesArray) {
                    if (role.roleName != null) {
                        if (role.roleName.equals("Consumer")) {
                            registration.roleId = role.roleId.longValue();
                            registration.roleName = role.roleName.toString();
                        }
                    }
                }
            }
        }
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 500 && resultCode == RESULT_OK) {

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

                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }

            }
        }
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
}
