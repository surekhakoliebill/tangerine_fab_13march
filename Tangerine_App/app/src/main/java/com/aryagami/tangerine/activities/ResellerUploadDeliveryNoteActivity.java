package com.aryagami.tangerine.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.aryagami.util.PictureUtility;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
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
                } /*else if (items[item].equals(activity.getResources().getString(R.string.generate_pdf))) {


                    Intent intent = new Intent(activity, ImageGridViewActivity.class);
                    intent.putExtra("key", 2);
                    intent.putExtra("documentPath", documentName);
                    startActivityForResult(intent, requestCode);

                } */else if (items[item].equals(activity.getResources().getString(R.string.cancel))) {
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
                String encodeData = getEncodeData(activity,pdfUri);
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




    /* Get uri related content real local file path. */
    public static String getPath(Context ctx, Uri uri) {
        String ret;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Android OS above sdk version 19.
                ret = getUriRealPathAboveKitkat(ctx, uri);
            } else {
                // Android OS below sdk version 19
                ret = getRealPath(ctx.getContentResolver(), uri, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DREG", "FilePath Catch: " + e);
            ret = getFilePathFromURI(ctx, uri);
        }
        return ret;
    }

    private static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String TEMP_DIR_PATH = Environment.getExternalStorageDirectory().getPath();
            File copyFile = new File(TEMP_DIR_PATH + File.separator + fileName);
            Log.d("DREG", "FilePath copyFile: " + copyFile);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream); // org.apache.commons.io
            inputStream.close();
            outputStream.close();
        } catch (Exception e) { // IOException
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getRealPath(ctx.getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether this uri represent a document or not. */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }
    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private static boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private static boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this document is provided by ExternalStorageProvider. */
    private static boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private static boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private static boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private static boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    @SuppressLint("Recycle")
    private static String getRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int columnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(columnIndex);
            }
        }

        return ret;
    }


    public static float getFileSize(String fileName) {
        File file = new File(fileName);

        if(!file.exists() || !file.isFile()){
            return -1;
        }
        float bytes = file.length();
        float kiloBytes = (bytes/1024);
        float megaBytes = (kiloBytes/1024);
        return megaBytes;
    }

    public static String getEncodeData(Activity activity, Uri pdfUri){
        String filepath = null;
        String encodeData = null;

        try {
            //  filepath = FilePath.getPath(activity,pdfUri);
            filepath = getPath(activity, pdfUri);

            if(filepath != null){
                float filesize  = getFileSize(filepath);
                if(filesize <= 10) {
                    encodeData = PictureUtility.encodeFileToBase64Binary(filepath);
                }else if(filesize ==-1){
                    encodeData = "File Not Found";
                }else {
                    encodeData = "File size is too Large";
                }
            }else{
                MyToast.makeMyToast(activity,"Unable to Encode it, please upload different Pdf.", Toast.LENGTH_SHORT);

            }
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Cause:"+e.getCause()+"Message"+e.getMessage()+"Stack"+ Log.getStackTraceString(e),"EncodeFile");
        }

        return encodeData;
    }
}
