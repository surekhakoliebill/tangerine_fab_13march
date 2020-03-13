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
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.FilePath;
import com.aryagami.util.MarshMallowPermission;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResellerUploadDeliveryNoteActivity  extends AppCompatActivity {

    Button uploadDeliveryNote, uploadButton, cancelButton;
    TextView deliveryNotification;
    Activity activity = this;
    Uri pdfUri;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    PdfDocumentData[] uploadedDocArray = new PdfDocumentData[1];
    Map<String, PdfDocumentData> map = new HashMap<>();
    String requestId;
    ProgressDialog progressDialog1, progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window_for_upload_delivery_note);

        requestId = getIntent().getSerializableExtra("requestId").toString();


        uploadDeliveryNote = (Button)findViewById(R.id.upload_delivery_note);
        uploadButton = (Button)findViewById(R.id.submit_btn);
        cancelButton = (Button)findViewById(R.id.cancel_btn);
        deliveryNotification = (TextView)findViewById(R.id.delivery_notification);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uploadedDocArray.length == 1){
                    if(uploadedDocArray[0] != null){
                        String prodPicDir = "reseller_documents/payment_documents/"+requestId;

                        RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                        String imageEncodedData = "";
                        if(uploadedDocArray[0].pdfRwaData != null){
                            imageEncodedData = uploadedDocArray[0].pdfRwaData;
                        }
                        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, uploading delivery note.");

                        uploadImageServiceHandler.uploadPdf("pdf", prodPicDir + "," +"reseller_delivery_note" , imageEncodedData, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);

                                        finishFulfillment();


                                } else if (userLogin.status.equals("INVALID_SESSION")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else if(!userLogin.status.isEmpty()){
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                   activity.finish();

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {

                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                BugReport.postBugReport(activity, Constants.emailId, "STATUS:" + status + "ERROR:" + error, "USER_DOCS_NOT_UPLOADED");

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Alert!");
                                alertDialog.setMessage("Unable to upload delivery note, please retry!");
                                alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }
                        });


                    }else{
                        MyToast.makeMyToast(activity,"Please Upload Delivery Note.", Toast.LENGTH_SHORT);
                    }
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        uploadDeliveryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String documentName = "reseller_delivery_note";
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,71 ,documentName);
            }
        });
    }

    private void finishFulfillment() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        if(requestId != null) {
            try{
                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                serviceHandler.finishProductRequest(requestId, new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        UserLogin userLogin = (UserLogin) data.get(0);
                        // ProgressDialogUtil.stopProgressDialog(progressDialog);
                        if (userLogin.status.equals("success")) {

                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                            alertBuilder.setIcon(R.drawable.success_icon);
                            alertBuilder.setTitle("Success!");
                            alertBuilder.setMessage("Success.");
                            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    callParentActivity();
                                }
                            });
                            alertBuilder.create().show();

                        } else if (userLogin.status.equals("INVALID_SESSION")) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {

                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                            alertBuilder.setTitle("Alert");
                            alertBuilder.setMessage("Status:" + userLogin.status);
                            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    callParentActivity();
                                }
                            });
                            alertBuilder.create().show();
                        }
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"Reject Etopup Request" );
                    }
                });

            }catch (IOException io){
                BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
            }
        }

    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerProductRequestsActivity.class);
        activity.startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 71 ) {

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
                PdfDocumentData docData1 = new PdfDocumentData();
                docData1.displayName = "reseller_delivery_note";
               // docData1.docType = checkDisplayName(companyDocs[0].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {
                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[0] = docData1;

                        deliveryNotification.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
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
    }

}
