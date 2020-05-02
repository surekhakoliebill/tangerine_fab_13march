package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.CacheNewOrderData;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyBroadCastReceiver;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OnDemandContractInformationActivity extends AppCompatActivity {

    TextInputEditText contractPeriod,additionalTerms, additionalNotes, discountValue;
    Spinner billingCycleSpinner;
    NewOrderCommand newOrderCommand;
    RelativeLayout billingCycleLayout;
    RadioGroup billingFrequencyRadioGroup, discountTypeRadioGroup;
    RadioButton monthlyRadioBtn, quarterlyRadioBtn, flatRadioBtn, percentRadioBtn;
    Button placePostpaidOrder;
    Activity activity = this;
    ProgressDialog progressDialog, progressDialog1, progressDialog2;
    UserRegistration registration;
    List<PdfDocumentData> pdfDocumentList;
    int imageUploadSuccessCount = 0;
    int pdfDocUploadedCount = 0;
    int passportUploadSuccessCount = 0;
    String filename1 = "";
    String filename2 = "";
    String filename = "";
    int actualDocCount= 0;
    String[] billingCycle = {"1st Week","2nd Week","3rd Week","4th Week"};
    Button backButton;
    int fingerprintUploadSuccessCount = 0;
    Button saveForLaterButton;
    Spinner currencySpinner;
    String[] currencyType= {"UGX","USD"};
    List<UserRegistration.UserDocCommand> reUploadDocList = new ArrayList<UserRegistration.UserDocCommand>();
    String uuid ;
    int i;
    public Boolean documentUploadPending = false;
    List<UserRegistration.UserDocCommand> pendingDocumentsList = new ArrayList<UserRegistration.UserDocCommand>();
    int docUploadCount = 0;
    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();
    Map<String, UserRegistration.UserDocCommand> unUploadedDocs = new HashMap<String, UserRegistration.UserDocCommand>();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondemand_contract_information);
         uuid = UUID.randomUUID().toString();
        if(NewOrderCommand.getOnDemandNewOrderCommand()!= null){
            newOrderCommand = NewOrderCommand.getOnDemandNewOrderCommand();
        }

        if(RegistrationData.getOnDemandRegistrationData()!= null){
            registration = RegistrationData.getOnDemandRegistrationData();
        }

        contractPeriod = (TextInputEditText)findViewById(R.id.contract_period_etext);
        additionalNotes = (TextInputEditText)findViewById(R.id.addional_notes);
        additionalTerms = (TextInputEditText)findViewById(R.id.additional_terms_etext);
        discountValue = (TextInputEditText)findViewById(R.id.discount_value);

        billingCycleSpinner = (Spinner)findViewById(R.id.biling_cycle_spinner);
        billingCycleLayout = (RelativeLayout)findViewById(R.id.billing_cycle_layout);

        billingFrequencyRadioGroup = (RadioGroup)findViewById(R.id.bilingfrequency_radiogroup);
        monthlyRadioBtn = (RadioButton)findViewById(R.id.monthly_radiobutton);
        quarterlyRadioBtn = (RadioButton)findViewById(R.id.quarterly_radiobutton);
        placePostpaidOrder = (Button)findViewById(R.id.place_order);
        backButton = (Button)findViewById(R.id.back_btn);
        saveForLaterButton = (Button)findViewById(R.id.save_for_later);

        discountTypeRadioGroup = (RadioGroup)findViewById(R.id.discount_type_radiogroup);
        flatRadioBtn = (RadioButton)findViewById(R.id.flat_rb);
        percentRadioBtn = (RadioButton)findViewById(R.id.percent_rb);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, billingCycle);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        billingCycleSpinner.setAdapter(adapter1);
        currencySpinner = (Spinner)findViewById(R.id.currency_spinner);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, currencyType);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        currencySpinner.setAdapter(adapter2);

        billingFrequencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.monthly_radiobutton:
                        registration.billingFrequency = "Monthly";
                        billingCycleLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.quarterly_radiobutton:
                        registration.billingFrequency = "Quarterly";
                        billingCycleLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });

        discountTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.flat_rb:
                        registration.discountType = "Flat";
                        break;
                    case R.id.percent_rb:
                        registration.billingFrequency = "Percent";
                        break;
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /*placePostpaidOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    newOrderCommand = getAllRequiredData();
                    if(newOrderCommand != null) {
                        newOrderCommand.fulfillmentDone = false;

                        if (newOrderCommand.isPostpaid) {
                            newOrderCommand.registrationServiceType = "Postpaid";
                        } else {
                            newOrderCommand.registrationServiceType = "Prepaid";
                        }
                        if (newOrderCommand.isNewAccount) {
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                            serviceHandler.postNewOrder(newOrderCommand, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if(orderDetails.status != null) {
                                        if (orderDetails.status.equals("success")) {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            UserSession.setAllUserInformation(activity, null);

                                            if (registration.registrationType.equals("company")) {
                                                //upload Company Documents
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

                                                           // progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading Company Documents!");
                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin login1 = (UserLogin) data.get(0);
                                                                    if (login1.status.equals("success")) {
                                                                        if (++pdfDocUploadedCount == actualDocCount) {
                                                                          //  ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                                            PdfDocumentData.setFinalDocsList(null);
                                                                            MyToast.makeMyToast(activity, "Company Documents Uploaded Successfully.", Toast.LENGTH_LONG);
                                                                        }
                                                                    } else {
                                                                      //  ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                                    //    MyToast.makeMyToast(activity, "Company Documents not Uploaded.", Toast.LENGTH_LONG);
                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                   // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                                    BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");
                                                                }
                                                            });
                                                        }

                                                    }
                                                } else {
                                                    Toast.makeText(activity, "DocumentList is Empty.", Toast.LENGTH_SHORT).show();
                                                }

                                                // Upload Company Activation Request Form
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
                                                                    // startResellerActivity();
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
                                                                            RegistrationData.setNinIdImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                   // startResellerActivity();
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
                                                                            RegistrationData.setProfileImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    startResellerActivity();
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
                                                                            RegistrationData.setPassportIdImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {

                                                                }
                                                            });
                                                        }
                                                }

                                                // Upload Refugee Images
                                                if (RegistrationData.getRefugeeImages() != null) {

                                                    String prodPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!prodPicDir.isEmpty())
                                                            prodPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        prodPicDir += "documents/refugee/" + user.userId + "/";
                                                    }
                                                    int imageNum = 0;
                                                    imageUploadSuccessCount = 0;
                                                    if (RegistrationData.getRefugeeImages().size() != 0)
                                                        for (View view : RegistrationData.getRefugeeImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename1 = RegistrationData.getFilePrefix() + "_refugee_" + (imageNum++);
                                                            } else {
                                                                filename1 = "refugee_" + (imageNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", prodPicDir + "," + filename1, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++imageUploadSuccessCount == RegistrationData.getRefugeeImages().size()) {
                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setRefugeeImages(null);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    // startResellerActivity();
                                                                }
                                                            });
                                                        }

                                                }

                                                // Upload Activation Request Form
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
                                                                    // startResellerActivity();
                                                                }
                                                            });
                                                        }
                                                }

                                                //Upload Visa Images
                                                if (RegistrationData.getVisaImages() != null) {

                                                    String prodPicDir = "";
                                                    for (DataModel prodData : data) {
                                                        if (!prodPicDir.isEmpty())
                                                            prodPicDir += ":";
                                                        UserLogin user = (UserLogin) prodData;
                                                        prodPicDir += "documents/visa/" + user.userId + "/";
                                                    }
                                                    int imageNum = 0;
                                                    imageUploadSuccessCount = 0;
                                                    if (RegistrationData.getVisaImages().size() != 0)
                                                        for (View view : RegistrationData.getVisaImages()) {
                                                            ImageView imageView = (ImageView) view;
                                                            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                            if (RegistrationData.getFilePrefix() != null) {
                                                                filename1 = RegistrationData.getFilePrefix() + "_visa_" + (imageNum++);
                                                            } else {
                                                                filename1 = "visa_" + (imageNum++);
                                                            }

                                                            RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                            uploadImageServiceHandler.uploadPdf("jpeg", prodPicDir + "," + filename1, imgData, new RestServiceHandler.Callback() {
                                                                @Override
                                                                public void success(DataModel.DataType type, List<DataModel> data) {
                                                                    UserLogin userLogin = (UserLogin) data.get(0);
                                                                    if (userLogin.status.equals("success")) {

                                                                        if (++imageUploadSuccessCount == RegistrationData.getVisaImages().size()) {
                                                                            MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                                                            RegistrationData.setVisaImages(null);
                                                                        }

                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                                    //startResellerActivity();
                                                                }
                                                            });
                                                        }
                                                }

                                                // Upload Thumb & Index Fingerprint Images

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
                                                            // String imageData = PictureUtility.encodeWSQBitmap(RegistrationData.getByteData());

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
                                                                        startResellerActivity();
                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RestServiceHandler.ErrorCode error, String status) {

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

                                                        }
                                                    });
                                                }


                                            }

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setIcon(R.drawable.success_icon);
                                            alertDialog.setMessage("Order Placed Successfully.\n Order No:" + Html.fromHtml("<b>" + orderDetails.orderNo.toString() + "</b>") + ".");
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            deleteFolderAndImage();
                                                            startResellerActivity();
                                                        }
                                                    });
                                            alertDialog.show();
                                        } else {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            if (orderDetails.status.equals("INVALID_SESSION")) {
                                                ReDirectToParentActivity.callLoginActivity(activity);
                                            } else {
                                                MyToast.makeMyToast(activity, "\nStatus:" + orderDetails.status.toString() + "\n Reason:" + orderDetails.reason, Toast.LENGTH_SHORT);
                                            }
                                            startResellerActivity();
                                        }
                                    }else{
                                        MyToast.makeMyToast(activity, "Empty Response", Toast.LENGTH_SHORT);
                                        startResellerActivity();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId, "ERROR" + error + "STATUS" + status, "Contract information Postpaid Orde");
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Order Not Placed, Would you like to upload data in the background?");
                                    alertDialog.setPositiveButton(getResources().getString(R.string.save_alert),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    saveInCache();
                                                    UploadCacheDataInTheBackground();
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startResellerActivity();
                                                }
                                            });

                                    alertDialog.show();
                                }
                            });
                        } else {
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                            serviceHandler.postNewOrder(newOrderCommand, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if (orderDetails.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        UserSession.setAllUserInformation(activity, null);

                                        if (orderDetails.subscriptions != null) {
                                            if (orderDetails.subscriptions.size() != 0)
                                                activateSubcription(orderDetails.subscriptions);
                                        }

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.success_icon);
                                        alertDialog.setMessage("Order Placed Successfully.\n Order No:" + Html.fromHtml("<b>"+orderDetails.orderNo.toString()+"</b>"));
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();

                                                        deleteFolderAndImage();
                                                        startResellerActivity();
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
                                            alertDialog.setMessage(Html.fromHtml("<b>"+"STATUS"+"</b>") + orderDetails.status + Html.fromHtml("<b>"+"\n REASON:"+"</b>")+ orderDetails.reason);
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            startResellerActivity();
                                                        }
                                                    });

                                            alertDialog.show();
                                        }
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:" + status, "CONTRACT INFOrmatioin");
                                    startResellerActivity();
                                }
                            });
                        }
                    }else{
                        if(NewOrderCommand.getOnDemandNewOrderCommand()!= null){
                            newOrderCommand = NewOrderCommand.getOnDemandNewOrderCommand();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
                }
            }
        });*/

        placePostpaidOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    newOrderCommand = getAllRequiredData();
                    if(newOrderCommand != null) {
                        newOrderCommand.fulfillmentDone = false;

                        if (newOrderCommand.isPostpaid) {
                            newOrderCommand.registrationServiceType = "Postpaid";
                        } else {
                            newOrderCommand.registrationServiceType = "Prepaid";
                        }
                        if (newOrderCommand.isNewAccount) {
                           // MyToast.makeMyToast(activity,"UUID \t"+uuid, Toast.LENGTH_SHORT);

                            uploadPostpaidNewOrderDocuments();

                        } else {
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                            serviceHandler.postNewOrder(newOrderCommand, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if (orderDetails.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        UserSession.setAllUserInformation(activity, null);

                                        if (orderDetails.subscriptions != null) {
                                            if (orderDetails.subscriptions.size() != 0)
                                                activateSubcription(orderDetails.subscriptions);
                                        }

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setIcon(R.drawable.success_icon);
                                        alertDialog.setMessage("Order Placed Successfully.\n Order No:" + Html.fromHtml("<b>"+orderDetails.orderNo.toString()+"</b>"));
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
                                        }else if(orderDetails.status.equalsIgnoreCase("System Error")){

                                            BugReport.postBugReport(activity, Constants.emailId,"Status: "+orderDetails.status+"Reason: "+orderDetails.reason,"New_order");

                                        } else {
                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Alert!");
                                            if(orderDetails.reason != null){
                                                alertDialog.setMessage(Html.fromHtml("<b>"+"STATUS"+"</b>") + orderDetails.status + Html.fromHtml("<b>"+"\n REASON:"+"</b>")+ orderDetails.reason);
                                            }else{
                                                alertDialog.setMessage(Html.fromHtml("<b>"+"STATUS"+"</b>") + orderDetails.status);
                                            }
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            startNavigationActivity();
                                                        }
                                                    });

                                            alertDialog.show();
                                        }
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:" + status, "CONTRACT INFOrmatioin");
                                    startNavigationActivity();
                                }
                            });
                        }
                    }else{
                        if(NewOrderCommand.getOnDemandNewOrderCommand()!= null){
                            newOrderCommand = NewOrderCommand.getOnDemandNewOrderCommand();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
                }
            }
        });

        saveForLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Want to Save Data in Cache?");
                alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //saveInCache();
                        saveAndUploadCacheDataInTheBackground();

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    private void uploadPostpaidNewOrderDocuments() {

            if (RegistrationData.getPersonalRegistrationUserDocs() != null) {

                if(RegistrationData.getPersonalRegistrationUserDocs().size() != 0) {

                    for(UserRegistration.UserDocCommand docCommand: RegistrationData.getPersonalRegistrationUserDocs()){
                        unUploadedDocs.put(docCommand.docFiles,docCommand);
                    }

                   // pendingDocumentsList = RegistrationData.getPersonalRegistrationUserDocs();
                    progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, Uploading Documents...!");

                    final int totalDocs = RegistrationData.getPersonalRegistrationUserDocs().size();
                  //  final UserRegistration.UserDocCommand[] docCommand = new UserRegistration.UserDocCommand[RegistrationData.getPersonalRegistrationUserDocs().size()];
                   // RegistrationData.getPersonalRegistrationUserDocs().toArray(docCommand);
                  //  i=0; i<totalDocs; i++
                    for (final UserRegistration.UserDocCommand docCommand: RegistrationData.getPersonalRegistrationUserDocs()) {
                        String prodPicDir = "temp_documents/" + docCommand.docType +"/"+ uuid + "/";

                       String[] docName = docCommand.docFiles.split(";");
                        filename1 = docName[0];
                        imageUploadSuccessCount = 0;

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        String imageEncodedData;
                        if(docCommand.docFormat.equals("pdf")){
                            imageEncodedData = docCommand.pdfRwaData;
                        }else{
                            imageEncodedData = docCommand.imageData;
                        }
                        uploadImageServiceHandler.uploadPdf(docCommand.docFormat, prodPicDir + "," + filename1, imageEncodedData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {
                                     BugReport.postBugReport(activity, Constants.emailId, "Login Reseller Name: "+UserSession.getResellerName(activity)+"EncodedImageData: "+imageEncodedData, "POSTPAID_USER_UPLOADED_URL:"+prodPicDir);


                                    if (++imageUploadSuccessCount == totalDocs) {
                                        newOrderCommand.userInfo.documentsUploadPending = false;
                                       // MyToast.makeMyToast(activity, getResources().getString(R.string.Successfully_uploaded_product), Toast.LENGTH_LONG);
                                        RegistrationData.setPersonalRegistrationUserDocs(null);
                                        RegistrationData.setUserThumbImageDrawable(null);
                                        RegistrationData.setUserIndexImageDrawable(null);
                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                        deleteFolderAndImage();
                                        placePostpaidNewOrder();
                                    }
                                        ++docUploadCount;
                                    if(userLogin.fileName != null){
                                        if(!userLogin.fileName.isEmpty())
                                        unUploadedDocs.remove(userLogin.fileName);
                                    }
                                } else if (userLogin.status.equals("INVALID_SESSION")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Alert!");
                                    alertDialog.setMessage("Unable to upload your documents, please retry!");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ++docUploadCount;
                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                  BugReport.postBugReport(activity, Constants.emailId, "Login Reseller Name: "+UserSession.getResellerName(activity)+"EncodedImageData: "+imageEncodedData+"\t STATUS:" + status + "\t ERROR:" + error, "USER_DOCS_NOT_UPLOADED - URL:"+prodPicDir);

                              // if(i<totalDocs)
                               pendingDocumentsList.add(docCommand);

                               if(docUploadCount== totalDocs){
                                   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                   alertDialog.setCancelable(false);
                                   alertDialog.setTitle("Alert!");
                                   alertDialog.setMessage("Unable to Upload Documents!");
                                   alertDialog.setPositiveButton("Upload in the Background", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                           newOrderCommand.userInfo.documentsUploadPending = true;
                                           documentUploadPending = true;
                                         //  MyToast.makeMyToast(activity,"PENDING DOC LIST"+pendingDocumentsList.size(),Toast.LENGTH_SHORT);
                                           savePendingDocs(pendingDocumentsList);
                                           placePostpaidNewOrder();
                                       }
                                   }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           dialogInterface.dismiss();
                                           startNavigationActivity();
                                       }
                                   });
                                   alertDialog.show();
                               }

                            }
                        });

                    }

                }
            }

    }

    private void savePendingDocs(List<UserRegistration.UserDocCommand> pendingDocumentsList) {
      /*  List<UserRegistration.UserDocCommand> commandList = new ArrayList<UserRegistration.UserDocCommand>();
        commandList = CacheNewOrderData.loadUnUploadedDocList(activity);
        if (commandList != null) {
            for(UserRegistration.UserDocCommand docCommand: pendingDocumentsList){
                commandList.add(docCommand);
            }
            CacheNewOrderData.saveUnUploadedDocs(activity, commandList);

        } else {
            CacheNewOrderData.saveUnUploadedDocs(activity, pendingDocumentsList);

        }*/



    }

    private void placePostpaidNewOrder() {
        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.postNewOrder(newOrderCommand, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    final UserLogin orderDetails = (UserLogin) data.get(0);
                    if(orderDetails.status != null) {
                        if (orderDetails.status.equals("success")) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            UserSession.setAllUserInformation(activity, null);

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setIcon(R.drawable.success_icon);
                            alertDialog.setMessage("Order Placed Successfully.\n Order No:" + Html.fromHtml("<b>" + orderDetails.orderNo.toString() + "</b>") + ".");
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

                            }else if (orderDetails.status.equalsIgnoreCase("System Error")) {

                                BugReport.postBugReport(activity, Constants.emailId,"Status: "+orderDetails.status+"Reason: "+orderDetails.reason,"New_order");

                            } else {
                                //MyToast.makeMyToast(activity, "\nStatus:" + orderDetails.status.toString() + "\n Reason:" + orderDetails.reason, Toast.LENGTH_SHORT);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Alert!");
                                if(orderDetails.reason != null) {
                                    alertDialog.setMessage("Status: " + orderDetails.status + "\n Reason: " + orderDetails.reason);
                                }else{
                                    alertDialog.setMessage("Status: "+orderDetails.status);
                                }
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                startNavigationActivity();
                                            }
                                        });
                                alertDialog.show();
                            }

                        }
                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        MyToast.makeMyToast(activity, "Empty Response", Toast.LENGTH_SHORT);
                        startNavigationActivity();
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId, "ERROR" + error + "STATUS" + status, "Contract information Postpaid Order");
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
            BugReport.postBugReport(activity, Constants.emailId, "Message:" + e.getMessage() + "Cause: " + e.getCause(), "Contract Information Postpaid Order");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandContractInformationActivity.this);
    }

    public void postDocumentUploadComplete(String userId){
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
    private void saveNotUploadedDocuments() {

        List<UserRegistration.UserDocCommand> docCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        docCommandList = CacheNewOrderData.loadUnUploadedDocList(activity);
        if (docCommandList != null) {

            for(UserRegistration.UserDocCommand docCommand : reUploadDocList){
                docCommandList.add(docCommand);
            }
            CacheNewOrderData.saveUnUploadedDocs(activity, docCommandList);
            //alertMessageAfterSuccess("Alert!","Congratulation! Data Saved Successfully.");
        } else {
            CacheNewOrderData.saveUnUploadedDocs(activity, reUploadDocList);
           // alertMessageAfterSuccess("Alert!","Congratulation! Data Saved Successfully.");
        }
    }

    private void activateSubcription(List<String> subscriptions) {

        for (String subscriptionId : subscriptions) {
            if (subscriptionId != null) {
                ActivateCommand command = new ActivateCommand();
                command.entityName = "subscription";
                command.activationId = subscriptionId.toString();
                command.reason = "AUTO ACTIVATION FOR NEW REGISTRATION";
                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading fingerprint!");
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.activateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            if (userLogin.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("Subscription Activated Successfully. \n Current Status:"+userLogin.activationState);
                                alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            } else {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (userLogin.status.equals("INVALID_SESSION")) {
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {
                                    MyToast.makeMyToast(activity, "Status not updated, \nStatus:" + userLogin.status.toString(), Toast.LENGTH_SHORT);
                                }
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId, "Error" + error + "\t Status:\n" + status, "NewOrderPaymentActivity-> ActivateSubscription");

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId, "Cause:"+e.getCause()+",Message:"+e.getMessage(), "NewOrderPaymentActivity-> ActivateSubscription");
                }
            }
        }
    }


    private void saveAndUploadCacheDataInTheBackground() {

        List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
        commandList = CacheNewOrderData.loadNewOrderCacheList(activity);
        if (commandList != null) {

            commandList.add(newOrderCommand);
            CacheNewOrderData.saveNewOrderDataCache(activity, commandList);
        } else {
            List<NewOrderCommand> commandList1 = new ArrayList<NewOrderCommand>();
            commandList1.add(newOrderCommand);
            CacheNewOrderData.saveNewOrderDataCache(activity, commandList1);
        }
            Intent intent = new Intent(activity, MyBroadCastReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    activity, 98765, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + (1000), pendingIntent);
            }

    }

    private void saveInCache() {
       /* newOrderCommand = getAllRequiredData();
        newOrderCommand.fulfillmentDone = false;

        if(newOrderCommand.isPostpaid){
            newOrderCommand.registrationServiceType = "Postpaid";
        }else{
            newOrderCommand.registrationServiceType = "Prepaid";
        }
*/
        List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
        commandList = CacheNewOrderData.loadNewOrderCacheList(activity);
        if (commandList != null) {

            commandList.add(newOrderCommand);
            CacheNewOrderData.saveNewOrderDataCache(activity, commandList);

           // alertMessageAfterSuccess();

        } else {
            List<NewOrderCommand> commandList1 = new ArrayList<NewOrderCommand>();

            commandList1.add(newOrderCommand);
            CacheNewOrderData.saveNewOrderDataCache(activity, commandList1);

           // alertMessageAfterSuccess();
        }
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

    private void startNavigationActivity() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        activity.startActivity(intent);
    }


    private void deleteFolderAndImage() {

       // DELETING FOLDER & CONTENT FROM TANGERINE/TEMP
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tangerine/Temp");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            if(children != null |children.length!=0) {
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        }

    }

    private NewOrderCommand getAllRequiredData() {

        coordinates.longitudeValue = RegistrationData.getCurrentLongitude();
        coordinates.latitudeValue = RegistrationData.getCurrentLatitude();
        newOrderCommand.resellerLocation = coordinates;
        newOrderCommand.resellerCode = UserSession.getResellerId(activity);
        newOrderCommand.productListingIds = new ArrayList<Long>();
        newOrderCommand.productListings = new ArrayList<NewOrderCommand.ProductListing>();

        NewOrderCommand.ContractCommand contractCommand = new NewOrderCommand.ContractCommand();
        if(!contractPeriod.getText().toString().isEmpty()){
            contractCommand.periodNMonths = Long.parseLong(contractPeriod.getText().toString());
        }else{
            MyToast.makeMyToast(activity,"Please Enter Contract Period", Toast.LENGTH_SHORT);
            return null;
        }

        newOrderCommand.contract = contractCommand;
        newOrderCommand.subscriptions = new ArrayList<SubscriptionCommand>();

        if(flatRadioBtn.isChecked()){
            registration.discountType = "Flat";
           // MyToast.makeMyToast(activity,"Discount Type"+ registration.discountType,Toast.LENGTH_SHORT);
            if(!discountValue.getText().toString().isEmpty()){
                registration.discountValue = Float.parseFloat(discountValue.getText().toString());
            }else{
                registration.discountValue = 0f;
            }


        }else if(percentRadioBtn.isChecked()){

            if(!discountValue.getText().toString().isEmpty()){
                if(Integer.parseInt(discountValue.getText().toString()) >= 0 && Integer.parseInt(discountValue.getText().toString())<=100) {
                    registration.discountValue = Float.parseFloat(discountValue.getText().toString());
                }else{
                    MyToast.makeMyToast(activity,"Periodic Discount Value should be in range of 0 to 100%.", Toast.LENGTH_SHORT);
                    return null;
                }
            }else{
                registration.discountValue = 0f;
            }
            registration.discountType = "Percent";
           // MyToast.makeMyToast(activity,"Discount Type"+ registration.discountType,Toast.LENGTH_SHORT);

        }

        if(monthlyRadioBtn.isChecked()){
            int pos = billingCycleSpinner.getSelectedItemPosition()+1;
            registration.billingCycleId = Long.valueOf(pos);
            registration.billingFrequency = "Monthly";
        }else if(quarterlyRadioBtn.isChecked()){
            registration.billingFrequency = "Quarterly";
        }
        registration.billingCycleId = Long.valueOf(1);
        registration.billingFrequency = "Monthly";
        setUserRoles(registration);

        newOrderCommand.userInfo.currency = currencySpinner.getSelectedItem().toString();
        newOrderCommand.userInfo.tempUserToken = uuid;
       // MyToast.makeMyToast(activity,"newOrderCommand.userInfo.tempUserToken "+newOrderCommand.userInfo.tempUserToken, Toast.LENGTH_SHORT);
        return newOrderCommand;
    }

    private NewOrderCommand.ContractCommand collectContractInformation() {
        NewOrderCommand.ContractCommand command = new NewOrderCommand.ContractCommand();

        if(!contractPeriod.getText().toString().isEmpty()){
          command.periodNMonths = Long.parseLong(contractPeriod.getText().toString());
        }else{
            MyToast.makeMyToast(activity,"Please Enter Contract Period", Toast.LENGTH_SHORT);
            return null;
        }

        if(!discountValue.getText().toString().isEmpty()){
         registration.discountValue = Float.parseFloat(discountValue.getText().toString());
        }else{
            MyToast.makeMyToast(activity,"Please Enter Discount Value", Toast.LENGTH_SHORT);
            return null;
        }

        registration.discountType = "Flat";
        if(monthlyRadioBtn.isChecked()){
            int pos = billingCycleSpinner.getSelectedItemPosition()+1;
            registration.billingCycleId = Long.valueOf(pos);
            registration.billingFrequency = "Monthly";
        }else if(quarterlyRadioBtn.isChecked()){
            registration.billingFrequency = "Quarterly";
        }
        return command;
    }

    private void setUserRoles(final UserRegistration registration) {
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
        } else {
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                serviceHandler.getAllRoles(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        /*Roles[] rolesArray = new Roles[data.size()];
                        rolesArray = data.toArray(rolesArray);
                        if(rolesArray.length!= 0){
                            RegistrationData.setRoles(rolesArray);
                            for (Roles role : rolesArray) {
                                if (role.roleName != null) {
                                    if (role.roleName.equals("Consumer")) {
                                        registration.roleId = role.roleId.longValue();
                                        registration.roleName = role.roleName.toString();
                                    }
                                }
                            }
                        }else{
                            MyToast.makeMyToast(activity, "Unable to Fetch All User Roles.", Toast.LENGTH_SHORT);
                        }*/

                        Roles role = (Roles)data.get(0);
                        if(role != null){
                            if(role.status != null){
                                if(role.status.equals("success")){
                                    Roles[] rolesArray = new Roles[role.rolesList.size()];
                                    rolesArray = role.rolesList.toArray(rolesArray);
                                    if(rolesArray.length!= 0){
                                        RegistrationData.setRoles(rolesArray);
                                        for (Roles role1 : rolesArray) {
                                            if (role1.roleName != null) {
                                                if (role1.roleName.equals("Consumer")) {
                                                    registration.roleId = role1.roleId.longValue();
                                                    registration.roleName = role1.roleName.toString();
                                                }
                                            }
                                        }
                                    }
                                }else if(role.status.equals("INVALID_SESSION")){
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                }else{
                                    MyToast.makeMyToast(activity,"GET ROLES:"+role.status, Toast.LENGTH_SHORT);
                                }
                            }
                        }else{
                            MyToast.makeMyToast(activity,"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"STATUS:"+status,"GET_ROLES");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                BugReport.postBugReport(activity, Constants.emailId,"Cause"+e.getCause()+",Message"+e.getMessage(),"GET_USER_ROLES");
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
