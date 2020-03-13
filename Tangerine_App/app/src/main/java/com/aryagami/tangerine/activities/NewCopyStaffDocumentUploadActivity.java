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
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ApproveReseller;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DocumentTypes;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ResellerStaff;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.FilePath;
import com.aryagami.util.MarshMallowPermission;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NewCopyStaffDocumentUploadActivity extends AppCompatActivity {

    UserRegistration userRegistration;
    ApproveReseller approveReseller;
    List<Button> buttonsList = new ArrayList<Button>();
    Button certifiedcompany, Certifiedformfile, auditedfinancial, URAcertified, previousbusiness, bankstatement, copiesOfDirectors,
            coi, Taxclarence, copyofdirectorsids, memorandum;
    TextView notification, notification1, notification2, notification3, notification4, notification5, notification6;
    private boolean isPdfFile = false;
    Activity activity = this;
    Uri pdfUri;
    PdfDocumentData[] uploadedDocArray;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    DocumentTypes[] pdfUpload, companyDocs;
    int mandatoryDocCount = 0;
    List<DocumentTypes> companyDocList = new ArrayList<DocumentTypes>();
    Button savebtn, back;
    ProgressDialog progressDialog,progressDialog2, progressDialog3 ;
    UserLogin userLoginResult;
    NewOrderCommand postStaffOrder;
    int pdfDocUploadedCount = 0;
    int actualDocCount = 0;
    NewOrderCommand command = new NewOrderCommand();
    String finalUserId, uuid;
    Boolean isDisableButton = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_staff_document_upload);

        postStaffOrder = RegistrationData.getPostStaffOrder();
        userRegistration = RegistrationData.getRegistrationData();

        coi = findViewById(R.id.certified_company);
        Taxclarence = findViewById(R.id.Certified_form_file);
        copyofdirectorsids = findViewById(R.id.copies_directories);
        memorandum = findViewById(R.id.audited_financial);

        //certifiedcompany = findViewById(R.id.certified_company);
        //Certifiedformfile = findViewById(R.id.Certified_form_file);
        //copiesOfDirectors = findViewById(R.id.copies_directories);
        //auditedfinancial = findViewById(R.id.audited_financial);
        //URAcertified = findViewById(R.id.URA_certified);
        //previousbusiness = findViewById(R.id.previous_business);
        //bankstatement = findViewById(R.id.bank_statement);
        uuid = UUID.randomUUID().toString();

        savebtn = (Button) findViewById(R.id.new_staff_document_save_and_continue_btn);
        back = (Button) findViewById(R.id.cancelfinal_btn);

        buttonsList.add(coi);
        buttonsList.add(Taxclarence);
        buttonsList.add(copyofdirectorsids);
        buttonsList.add(memorandum);

        //buttonsList.add(certifiedcompany);
        //buttonsList.add(Certifiedformfile);
        //buttonsList.add(auditedfinancial);
        //buttonsList.add(URAcertified);
        //buttonsList.add(previousbusiness);
        //buttonsList.add(bankstatement);
        //buttonsList.add(copiesOfDirectors);

        if (DocumentTypes.getCompanyDocArray() != null) {
            if (DocumentTypes.getCompanyDocArray().length != 0) {
                companyDocs = new DocumentTypes[DocumentTypes.getCompanyDocArray().length];
                uploadedDocArray = new PdfDocumentData[DocumentTypes.getCompanyDocArray().length];
                companyDocs = DocumentTypes.getCompanyDocArray();

                coi.setText(companyDocs[0].displayName);
                Taxclarence.setText(companyDocs[1].displayName);
                copyofdirectorsids.setText(companyDocs[2].displayName);
                memorandum.setText(companyDocs[3].displayName);

                //certifiedcompany.setText(companyDocs[0].displayName);
                //Certifiedformfile.setText(companyDocs[1].displayName);
                //copiesOfDirectors.setText(companyDocs[2].displayName);
                //auditedfinancial.setText(companyDocs[3].displayName);
                //URAcertified.setText(companyDocs[4].displayName);
                //previousbusiness.setText(companyDocs[5].displayName);
                //bankstatement.setText(companyDocs[6].displayName);


                for (DocumentTypes types : DocumentTypes.getCompanyDocArray()) {
                    if (types.isMandatory) {
                        mandatoryDocCount++;
                    }
                }
            }
        } else {
            getDocumentTypes();
        }

        notification = findViewById(R.id.notification);
        notification1 = findViewById(R.id.notification1);
        notification2 = findViewById(R.id.notification2);
        notification3 = findViewById(R.id.notification3);
        //notification4 = findViewById(R.id.notification4);
        //notification5 = findViewById(R.id.notification5);
        //notification6 = findViewById(R.id.notification6);

        coi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton) {
                    String documentName = coi.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity, 71, documentName);

                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.", Toast.LENGTH_SHORT);
                }
            }
        });

        Taxclarence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton){
                    String documentName = Taxclarence.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity,72 ,documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.", Toast.LENGTH_SHORT);
                }
            }
        });

        copyofdirectorsids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton){
                    String documentName = copyofdirectorsids.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity,73 ,documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.", Toast.LENGTH_SHORT);
                }
            }
        });
        memorandum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(!isDisableButton){
                    String documentName = memorandum.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity,74 ,documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.", Toast.LENGTH_SHORT);
                }
            }
        });

       /* URAcertified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton) {
                    String documentName = URAcertified.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity, 75, documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.",Toast.LENGTH_SHORT);
                }
            }
        });*/

        /*previousbusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton){
                    String documentName = previousbusiness.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity,76 ,documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.",Toast.LENGTH_SHORT);
                }
            }
        });*/

       /* bankstatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!isDisableButton){
                    String documentName = bankstatement.getText().toString().replace(" ", "_");
                    clearPreviousPdfData(documentName);
                    selectAlertItem(activity,77 ,documentName);
                }else{
                    MyToast.makeMyToast(activity,"Unable to get DocTypes from Server.",Toast.LENGTH_SHORT);
                }
            }
        });*/



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postStaffOrder.userInfo.userGroup.equals("Reseller Distributor")||postStaffOrder.userInfo.userGroup.equals("Reseller Retailer")) {

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    List<UserRegistration.UserDocCommand> postDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
                    postDocCommandList = collectPdfDocs();

                    if (postDocCommandList != null) {
                        PdfDocumentData.setStaffDocsList(Arrays.asList(uploadedDocArray));
                        postStaffOrder.userInfo.userDocs = postDocCommandList;
                        collectAllRequiredData();
                        try {
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                            serviceHandler.postStaffNewOrder(postStaffOrder, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    userLoginResult = (UserLogin) data.get(0);
                                    if (userLoginResult.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        RegistrationData.setPostLongitude(null);
                                        RegistrationData.setPostLongitude(null);
                                        finalUserId = userLoginResult.userId.toString();
                                        UploadDocuments(finalUserId);
                                        setAlertMessage("Reseller Added Successfully.\n Order No:"+userLoginResult.orderNo +"\nUser Id:"+ userLoginResult.userId.toString());
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        setAlertMessage(userLoginResult.status.toString());
                                    }
                                }
                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error" + error + "Status" + status,"STAFF -----");
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR:" + e.getCause() + "MESSAGE:" + e.getMessage(),"STAFF -----");
                        } catch (Exception e) {
                            e.printStackTrace();
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR:" + e.getCause() + "MESSAGE:" + e.getMessage(),"STAFF -----");
                        }
                    }
                }else if (postStaffOrder.userInfo.userGroup.equals("Reseller Staff")) {
                    addResellerStaff();
                }
            }
        });*/

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<UserRegistration.UserDocCommand> postDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
                postDocCommandList = collectPdfDocs();

                if (postDocCommandList != null) {
                    PdfDocumentData.setStaffDocsList(Arrays.asList(uploadedDocArray));
                    postStaffOrder.userInfo.userDocs = postDocCommandList;
                    UploadDocuments();
                } else {
                    MyToast.makeMyToast(activity, "Please Upload Documents", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void registerStaffUser() {
        collectAllRequiredData();
        try {
            RestServiceHandler serviceHandler = new RestServiceHandler();

            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
            serviceHandler.postStaffNewOrder(postStaffOrder, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    userLoginResult = (UserLogin) data.get(0);
                    if (userLoginResult.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        RegistrationData.setPostLongitude(null);
                        RegistrationData.setPostLongitude(null);
                        finalUserId = userLoginResult.userId.toString();

                        setAlertMessage("Reseller Added Successfully.\n Order No:"+userLoginResult.orderNo);
                    }else if(userLoginResult.status.equals("INVALID_SESSION")){
                        ReDirectToParentActivity.callLoginActivity(activity);

                    }else if(userLoginResult.status.equals("System Error")){

                        BugReport.postBugReport(activity, Constants.emailId,"Status: "+userLoginResult.status+"Reason: "+userLoginResult.reason,"New_order");

                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        setAlertMessage(userLoginResult.status.toString());
                    }
                }
                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"Error" + error + "Status" + status,"STAFF -----");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"ERROR:" + e.getCause() + "MESSAGE:" + e.getMessage(),"STAFF -----");
        } catch (Exception e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"ERROR:" + e.getCause() + "MESSAGE:" + e.getMessage(),"STAFF -----");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(NewCopyStaffDocumentUploadActivity.this);
    }

    private void setAlertMessage(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Message!");
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startNavigationMenuActivity();
                    }
                });
        alertDialog.show();
    }

    private void UploadDocuments() {
        pdfDocumentDataList = PdfDocumentData.getStaffDocsList();
        if(pdfDocumentDataList != null)
            if(pdfDocumentDataList.size() != 0) {
                for (PdfDocumentData docData : pdfDocumentDataList) {
                    if (docData != null) {
                        actualDocCount++;
                    }
                }
                progressDialog3 = ProgressDialogUtil.startProgressDialog(activity, "Please wait..., Uploading Documents!");

                for (PdfDocumentData docData : pdfDocumentDataList) {
                    if (docData != null) {
                        String fileUrl = "temp_documents/" + docData.docType + "/" + uuid + "/," + (docData.displayName.toString()).replace(" ", "_");
                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin login1 = (UserLogin) data.get(0);
                                if (login1.status.equals("success")) {
                                    if (++pdfDocUploadedCount == actualDocCount) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                        PdfDocumentData.setStaffDocsList(null);
                                        // MyToast.makeMyToast(activity, "Company Documents Uploaded Successfully.", Toast.LENGTH_LONG);

                                        if (postStaffOrder.userInfo.userGroup.equals("Reseller Distributor") || postStaffOrder.userInfo.userGroup.equals("Reseller Retailer")) {
                                            postStaffOrder.userInfo.documentsUploadPending = false;
                                            registerStaffUser();
                                        } else if (postStaffOrder.userInfo.userGroup.equals("Reseller Staff")){
                                            postStaffOrder.userInfo.documentsUploadPending = false;
                                            addResellerStaff();
                                        }
                                    }
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                    if (postStaffOrder.userInfo.userGroup.equals("Reseller Distributor") || postStaffOrder.userInfo.userGroup.equals("Reseller Retailer")) {
                                        // postStaffOrder.userInfo.
                                        postStaffOrder.userInfo.documentsUploadPending = true;
                                        registerStaffUser();
                                    } else if (postStaffOrder.userInfo.userGroup.equals("Reseller Staff")){
                                        postStaffOrder.userInfo.documentsUploadPending = true;
                                        addResellerStaff();
                                    }
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                BugReport.postBugReport(activity, Constants.emailId,"STATUS: "+status+",ERROR: ","Upload Reseller Docs");

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Alert!");
                                alertDialog.setMessage("Want to upload the documents in the background?");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (postStaffOrder.userInfo.userGroup.equals("Reseller Distributor") || postStaffOrder.userInfo.userGroup.equals("Reseller Retailer")) {
                                            // postStaffOrder.userInfo.
                                            postStaffOrder.userInfo.documentsUploadPending = true;
                                            registerStaffUser();
                                        } else if (postStaffOrder.userInfo.userGroup.equals("Reseller Staff")){
                                            postStaffOrder.userInfo.documentsUploadPending = true;
                                            addResellerStaff();
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }
                        });
                    }

                }
            }else {
                Toast.makeText(activity, "DocumentList is Empty.", Toast.LENGTH_SHORT).show();
            }
    }

    public void addResellerStaff(){
        collectResellerStaffData();
        RestServiceHandler serviceHandler = new RestServiceHandler();

        List<UserRegistration.UserDocCommand> postDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        postDocCommandList = collectPdfDocs();

        if(postDocCommandList != null){

            postStaffOrder.userInfo.userDocs = postDocCommandList;
            PdfDocumentData.setStaffDocsList(Arrays.asList(uploadedDocArray));
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, Adding Reseller Staff User...!");
                serviceHandler.postStaffUserRegistration(postStaffOrder.userInfo, new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        UserLogin response = (UserLogin) data.get(0);
                        if(response.status.equals("success")){

                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            setAlertMessage(response.message.toString()+"\n "+response.userName);

                        }else if(response.status.equals("INVALID_SESSION")){
                            ReDirectToParentActivity.callLoginActivity(activity);
                        }else{
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            setAlertMessage(response.status);
                        }
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        BugReport.postBugReport(activity, Constants.emailId,"STATUS: "+status+"ERROR: "+error,"ADD RESELLER STAFF");

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+e.getCause()+"MESSAGE:"+e.getMessage(),"ADD RESELLER STAFF");

            }
        }
    }

    private void collectResellerStaffData() {

        postStaffOrder.userInfo.resellerCode = UserSession.getResellerId(activity) ;
        postStaffOrder.userInfo.registrationType = "company";
        postStaffOrder.userInfo.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";
    }



/*
    public void approveReseller(String resellerId){
        RestServiceHandler serviceHandler = new RestServiceHandler();
        collectApproveResellerData(resellerId);
        progressDialog2 = ProgressDialogUtil.startProgressDialog(activity, "please wait, Approving Reseller......");

        try {
            serviceHandler.postApproveReseller(approveReseller, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin result = (UserLogin) data.get(0);
                    if (result.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog2);
                        UploadDocuments();
                        setAlertMessage("Reseller Approved...");

                    }else {
                        ProgressDialogUtil.stopProgressDialog(progressDialog2);
                        setAlertMessage("Reseller Not Approved...\nStatus:"+result.status.toString());
                    }

                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog2);
                    BugReport.postBugReport(activity,Constants.emailId,"ERROR:"+error+"STATUS:"+status,"RESELLER APPROVED");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity,Constants.emailId,"ERROR"+e.getCause()+"MESSAGE:"+e.getMessage(),"ADD RESELLER STAFF");
        }
    }
*/


    private void collectApproveResellerData(String resellerId){
        approveReseller = new ApproveReseller();
        approveReseller.resellerId = resellerId;
        approveReseller.status = "APPROVED";

    }

    private void startNavigationMenuActivity() {
        activity.finish();
        Intent i = new Intent(activity, NavigationMainActivity.class);
        startActivity(i);
    }

    private void collectAllRequiredData() {

        postStaffOrder.creditLimit = 0f;
        postStaffOrder.contract = new NewOrderCommand.ContractCommand();
        postStaffOrder.currentStatus = "0";
        postStaffOrder.fulfillmentDone = false;
        postStaffOrder.productListingIdCounts = new ArrayList<Integer>();
        postStaffOrder.productListingIds = new ArrayList<Long>();
        postStaffOrder.productListingPrice = new ArrayList<Float>();
        postStaffOrder.registrationServiceType = "Postpaid";
        ResellerStaff staffInfo = getResellerInfo();
        postStaffOrder.addresellerCommand = staffInfo;
        postStaffOrder.subscriptions = new ArrayList<SubscriptionCommand>();
        postStaffOrder.userInfo.registrationType = "company";
        postStaffOrder.userInfo.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";
        postStaffOrder.userInfo.tempUserToken = uuid;

        postStaffOrder.resellerLocation.latitudeValue = RegistrationData.getPostLatitude();
        postStaffOrder.resellerLocation.longitudeValue = RegistrationData.getPostLongitude();
    }

    private ResellerStaff getResellerInfo() {
        ResellerStaff staff = new ResellerStaff();
        staff.aggregatorId = UserSession.getResellerId(activity);
        staff.immedidateServiceCutoffDays = 1;

        if(postStaffOrder.userInfo.userGroup.equals("Reseller Distributor")){
            staff.isAggregator = false;
            staff.isDistributor = true;
            staff.isRetailer = false;
        }else if(postStaffOrder.userInfo.userGroup.equals("Reseller Retailer")){
            staff.isAggregator = false;
            staff.isDistributor = false;
            staff.isRetailer = true;
        }

        staff.paymentType = "Upfront";
        staff.resellerName = postStaffOrder.userInfo.fullName.toString();
        staff.status = "APPROVED";
        if(postStaffOrder.ussd != null)
            staff.pin = postStaffOrder.ussd;
        return staff;
    }

    private void getDocumentTypes() {
        pdfDocumentDataList = new ArrayList<PdfDocumentData>();
        RestServiceHandler serviceHandler1 = new RestServiceHandler();
        try {
            serviceHandler1.getPdfDocument(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    if (data.size() != 0) {
                        pdfUpload = new DocumentTypes[data.size()];
                        data.toArray(pdfUpload);

                        for (DocumentTypes upload : pdfUpload) {
                            if (upload.userType.equals("reseller")) {

                                companyDocList.add(upload);
                                mandatoryDocCount++;

                            }
                        }

                        companyDocs = new DocumentTypes[companyDocList.size()];
                        uploadedDocArray = new PdfDocumentData[companyDocList.size()];
                        companyDocList.toArray(companyDocs);

                        if (companyDocs.length != 0) {
                            coi.setText(companyDocs[0].displayName);
                            Taxclarence.setText(companyDocs[1].displayName);
                            copyofdirectorsids.setText(companyDocs[2].displayName);
                            memorandum.setText(companyDocs[3].displayName);
                            //URAcertified.setText(companyDocs[4].displayName);
                            //previousbusiness.setText(companyDocs[5].displayName);
                            //bankstatement.setText(companyDocs[6].displayName);
                        }
                    } else {
                        MyToast.makeMyToast(activity, "EMPTY DATA, Reason: INVALID_SESSION", Toast.LENGTH_SHORT);
                        isDisableButton = true;
                        savebtn.setVisibility(View.GONE);
                        ReDirectToParentActivity.callLoginActivity(activity);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+"\nSTATUS:"+status,"GET DOCUMENT TYPES");
                    ReDirectToParentActivity.callLoginActivity(activity);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getCause()+"\n MESSAGE:"+e.getMessage(),"GET DOCUMENT TYPES");
        }
    }

    private List<UserRegistration.UserDocCommand> collectPdfDocs() {
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        if(pdfDocumentDataList != null) {

            if(pdfDocumentDataList.size() >= 3) {
                for (PdfDocumentData documentData : uploadedDocArray) {
                    if (documentData != null) {
                        UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();
                        uDocCommand.docFiles = documentData.displayName.replace(" ", "_") + ";";
                        uDocCommand.docType = documentData.docType;
                        uDocCommand.docFormat = "pdf";
                        uDocCommand.reviewStatus = "Submitted";
                        uDocCommand.pdfRwaData = documentData.pdfRwaData;
                        userDocCommandList.add(uDocCommand);
                    }
                }
            }else{
                MyToast.makeMyToast(activity,"Please Upload Mandatory Documents", Toast.LENGTH_SHORT);
                return null;
            }
        }
        return userDocCommandList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 71 && resultCode == RESULT_OK) {
            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }
            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[0].displayName;
                docData1.docType = checkDisplayName(companyDocs[0].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[0] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }

        if (requestCode == 72 && resultCode == RESULT_OK) {
            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[1].displayName;
                docData1.docType = checkDisplayName(companyDocs[1].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[1] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification1.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }


        if (requestCode == 73 && resultCode == RESULT_OK) {
            // NEW
            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[2].displayName;
                docData1.docType = checkDisplayName(companyDocs[2].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[2] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification2.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }

        if (requestCode == 74 && resultCode == RESULT_OK) {
            //NEW
            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[3].displayName;
                docData1.docType = checkDisplayName(companyDocs[3].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[3] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification3.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }

       /* if (requestCode == 75 && resultCode == RESULT_OK) {
            //NEW

            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[4].displayName;
                docData1.docType = checkDisplayName(companyDocs[4].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[4] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification4.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }

        }*/

       /* if (requestCode == 76 && resultCode == RESULT_OK) {

            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[5].displayName;
                docData1.docType = checkDisplayName(companyDocs[5].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[5] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification5.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }*/

       /* if (requestCode == 77 && resultCode == RESULT_OK) {

            if (data != null)
                if (data.getData() != null) {
                    // from direct external Storage
                    pdfUri = data.getData();
                } else {
                    //from generate Pdf
                    String uri = data.getStringExtra("result");
                    if (uri != null)
                        pdfUri = Uri.parse(uri);
                }

            if(pdfUri != null) {
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = companyDocs[6].displayName;
                docData1.docType = checkDisplayName(companyDocs[6].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[6] = docData1;
                        pdfDocumentDataList.add(docData1);
                        notification6.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }

            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }*/
    }

    private String checkDisplayName(String displayName) {
        String docType = "";
        for (DocumentTypes pdfUpload : companyDocs) {
            if (pdfUpload.displayName.equals(displayName)) {
                docType = pdfUpload.docType;
                break;
            }
        }
        return docType;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    public void selectAlertItem(final Activity activity, final int requestCode, final String documentName) {
        final CharSequence[] items = {activity.getResources().getString(R.string.upload_pdf), activity.getResources().getString(R.string.generate_pdf), activity.getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.Pdf_dialog);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(activity.getResources().getString(R.string.upload_pdf))) {
                    MarshMallowPermission permission = new MarshMallowPermission(activity);

                    if (!permission.checkPermissionForExternalStorage()) {
                        permission.requestPermissionForExternalStorage();
                    }else {
                        isPdfFile = true;
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select PDF"), requestCode);
                    }
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

    public  void onTrimMemory(int level) {
        System.gc();
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