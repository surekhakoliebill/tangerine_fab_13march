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
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DocumentTypes;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.SimDocumentsUpload;
import com.aryagami.data.SimSwapList;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.FilePath;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimSwapPdfUploadActivity extends AppCompatActivity {

    Activity activity = this;
    ProgressDialog progressDialog;
    DocumentTypes[] companyDocs, pdfUpload;
    PdfDocumentData[] uploadedDocArray;
    int mandatoryDocCount = 0;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    List<DocumentTypes> companyDocList = new ArrayList<DocumentTypes>();
    Button upload_user_id_copy, upload_simswap_form_copy, upload_police_letter, upload_faulty_sim_copy;
    TextView notification, notification1, notification2, notification3;
    private boolean isPdfFile = false;
    int actualDocCount = 0;
    int pdfDocUploadedCount = 0;
    Uri pdfUri;
    SimDocumentsUpload simDocumentsUpload;
    String userId;
    SimSwapList simSwapList;
    String postMSISDN, postUserId, postSimSwapLogId;
    List<Button> buttonsList = new ArrayList<Button>();
    List<UserRegistration.UserDocCommand> postDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_swap_upload_document);
        userId = UserSession.getUserId(activity);
        simDocumentsUpload = new SimDocumentsUpload();
        simSwapList = new SimSwapList();

          postMSISDN = getIntent().getStringExtra("msisdn");
          postUserId = getIntent().getStringExtra("userId");
          postSimSwapLogId = getIntent().getStringExtra("simSwapLogId");


        Button upload = (Button) findViewById(R.id.sim_swap_document_upload_btn);
        Button cancel = (Button) findViewById(R.id.sim_replace_cancel_btn);

        upload_user_id_copy = (Button) findViewById(R.id.Upload_User_Id_Copy);
        upload_simswap_form_copy = (Button) findViewById(R.id.Upload_Simswap_Form_Copy);
        upload_police_letter = (Button) findViewById(R.id.Upload_Police_Letter);
        upload_faulty_sim_copy = (Button) findViewById(R.id.Upload_Faulty_Sim_Copy);

        buttonsList.add(upload_user_id_copy);
        buttonsList.add(upload_simswap_form_copy);
        buttonsList.add(upload_police_letter);
        buttonsList.add(upload_faulty_sim_copy);

        if (DocumentTypes.getSimSwapDocArray() != null) {
            if (DocumentTypes.getCompanyDocArray().length != 0) {
                companyDocs = new DocumentTypes[DocumentTypes.getCompanyDocArray().length];
                uploadedDocArray = new PdfDocumentData[DocumentTypes.getCompanyDocArray().length];
                companyDocs = DocumentTypes.getCompanyDocArray();

                upload_user_id_copy.setText(companyDocs[0].displayName);
                upload_simswap_form_copy.setText(companyDocs[1].displayName);
                upload_police_letter.setText(companyDocs[2].displayName);
                upload_faulty_sim_copy.setText(companyDocs[3].displayName);

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

        upload_user_id_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = upload_user_id_copy.getText().toString().replace(" ", "_");
                selectAlertItem(activity,71 ,documentName);
            }
        });

        upload_simswap_form_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = upload_simswap_form_copy.getText().toString().replace(" ", "_");
                selectAlertItem(activity,72 ,documentName);
            }
        });

        upload_police_letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = upload_police_letter.getText().toString().replace(" ", "_");
                selectAlertItem(activity,73 ,documentName);
            }
        });

        upload_faulty_sim_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = upload_faulty_sim_copy.getText().toString().replace(" ", "_");
                selectAlertItem(activity,74 ,documentName);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();

                postDocCommandList = collectPdfDocs();
                if (postDocCommandList != null){

                    try {
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                        serviceHandler.uploadSimSwapPdf(postUserId, postDocCommandList,new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin login = (UserLogin) data.get(0);
                                if (login.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    UploadDocuments();

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Sim Swap Document Uploaded Successfully...");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    startNavigationMenuActivity();
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Sim Swap Document not Uploaded...\nStatus:"+login.status.toString());
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    activity.finish();
                                                    Intent intent = new Intent(activity, NewSimSwapActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"SIMSWAP PDF UPLOAD ACTIVITY");
                                Intent intent = new Intent(activity, NewSimSwapActivity.class);
                                startActivity(intent);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"SIMSWAP PDF UPLOAD ACTIVITY");
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(SimSwapPdfUploadActivity.this);
    }


    private void startNavigationMenuActivity() {
        activity.finish();
        Intent i = new Intent(activity, NavigationMainActivity.class);
        startActivity(i);
    }


    private void UploadDocuments() {

        if(postDocCommandList.size() != 0 | postDocCommandList != null) {
            for (UserRegistration.UserDocCommand docData : postDocCommandList) {
                if (docData != null) {
                    actualDocCount++;
                }
            }

            for (UserRegistration.UserDocCommand docData : postDocCommandList) {
                if (docData != null) {

                    String[] docFileName = docData.docFiles.split(";");
                    String fileUrl = "sim_swap_documents" + "/" + postSimSwapLogId + "/," + docFileName[0];

                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                    uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin login1 = (UserLogin) data.get(0);
                            if (login1.status.equals("success")) {
                                if (++pdfDocUploadedCount == actualDocCount) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    MyToast.makeMyToast(activity, "Documents Uploaded Successfully.", Toast.LENGTH_LONG);

                                }
                            } else {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity, "Documents not Uploaded.", Toast.LENGTH_LONG);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"SIMSWAP PDF UPLOAD ACTIVITY");
                        }
                    });
                }

            }
        }else {
            Toast.makeText(activity, "DocumentList is Empty.", Toast.LENGTH_SHORT).show();

        }
    }

    private List<UserRegistration.UserDocCommand> collectPdfDocs() {

        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        if(pdfDocumentDataList != null) {
            //  if (pdfDocumentDataList.size() >= mandatoryDocCount) {
            for (PdfDocumentData documentData : uploadedDocArray) {
                if (documentData != null) {
                    UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();

                    uDocCommand.docFiles = postMSISDN + "_" + documentData.displayName.replace(" ", "_") + ";";
                    uDocCommand.docType = documentData.docType;
                    uDocCommand.docFormat = "pdf";
                    uDocCommand.reviewStatus = "Submitted";
                    uDocCommand.pdfRwaData = documentData.pdfRwaData;
                    uDocCommand.userId = postUserId;

                    userDocCommandList.add(uDocCommand);
                }
            }

        }

        //userRegistration.userDocs = userDocCommandList;
        return userDocCommandList;
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
                            if (upload.userType.equals("userSimswap")) {
                                companyDocList.add(upload);
                                if (upload.isMandatory) {
                                    mandatoryDocCount++;
                                }
                            }
                        }

                        companyDocs = new DocumentTypes[companyDocList.size()];
                        uploadedDocArray = new PdfDocumentData[companyDocList.size()];
                        companyDocList.toArray(companyDocs);

                        if (companyDocs.length != 0) {

                            upload_user_id_copy.setText(companyDocs[0].displayName);
                            upload_simswap_form_copy.setText(companyDocs[1].displayName);
                            upload_police_letter.setText(companyDocs[2].displayName);
                            upload_faulty_sim_copy.setText(companyDocs[3].displayName);

                        }else{

                            upload_user_id_copy.setText("NA");
                            upload_simswap_form_copy.setText("NA");
                            upload_police_letter.setText("NA");
                            upload_faulty_sim_copy.setText("NA");

                        }


                    } else {
                        MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"SIMSWAP PDF UPLOAD ACTIVITY");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"SIMSWAP PDF UPLOAD ACTIVITY");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 1 MB.", Toast.LENGTH_SHORT);
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
                docData1.displayName = companyDocs[1].displayName.toString();
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
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 1 MB.", Toast.LENGTH_SHORT);
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
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 1 MB.", Toast.LENGTH_SHORT);
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
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 1 MB.", Toast.LENGTH_SHORT);
                    }

                }else{
                    MyToast.makeMyToast(activity,"Unable to Encode it.", Toast.LENGTH_SHORT);
                }
            }else{
                MyToast.makeMyToast(activity,"File Path Not Found", Toast.LENGTH_SHORT);
            }
        }
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
}
