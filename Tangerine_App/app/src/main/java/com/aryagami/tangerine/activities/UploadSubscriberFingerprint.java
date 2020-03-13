package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.AlertMessage;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.PictureUtility;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.aryagami.util.AlertMessage.alertDialogMessage;

public class UploadSubscriberFingerprint extends AppCompatActivity implements ComponentCallbacks2 {

    Button uploadFingerprint, findSubscription, captureThumbFingerprint, captureIndexFingerprint;
    ImageView thumbFingerImage, indexFingerImage;
    EditText servedMSISDN;
    TextView userName, givenName, surName;
    SubscriptionCommand subscriptionCommand;
    Activity activity = this;
    String userId, fileName, filename1;
    String prefixUname = "";
    int imageUploadSuccessCount = 0;
    ProgressDialog progressDialog;
    List<Drawable> imagesList;
    LinearLayout user_detail_layout;
    ImageButton backImageButton;
    List<UserRegistration.UserDocCommand> docsList = new ArrayList<>();
    Boolean isThumbUploaded = false, isIndexUploaded = false, isFingerprint= false;
    Long randomNumber;
    ProgressDialog uploadProgressDialog, progressDialog1;
    int uploadedCommands = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_subscriber_fingerprint);
        //Fabric.with(this, new Crashlytics());
        servedMSISDN = (EditText) findViewById(R.id.serverdMSISDN_eText);
        uploadFingerprint = (Button) findViewById(R.id.upload_finger_print);
        findSubscription = (Button) findViewById(R.id.check_subscription_btn);
        captureThumbFingerprint = (Button) findViewById(R.id.capture_thumb_print);
        captureIndexFingerprint = (Button) findViewById(R.id.capture_index_finger_print);
        userName = (TextView) findViewById(R.id.username_value);
        givenName = (TextView) findViewById(R.id.givenname_value);
        surName = (TextView) findViewById(R.id.surname_value);
        user_detail_layout = (LinearLayout) findViewById(R.id.user_detail_layout);

        captureThumbFingerprint.setEnabled(false);
        captureIndexFingerprint.setEnabled(false);
        uploadFingerprint.setEnabled(false);

        thumbFingerImage = (ImageView) findViewById(R.id.thumb_image);
        indexFingerImage = (ImageView) findViewById(R.id.index_finger_image);

        servedMSISDN.setText("256");
        servedMSISDN.setSelection(3);

        randomNumber = generateRandomNumber();

        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        findSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrationData.setSubscriberThumbImageDrawable(null);
                RegistrationData.setSubscriberIndexImageDrawable(null);

                if (servedMSISDN.getText().length() != 12) {
                    MyToast.makeMyToast(activity, "Please enter correct MSISDN Number", Toast.LENGTH_SHORT).show();
                } else {
                    checkSubscriptionValue();
                }

            }
        });
        captureThumbFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
                    if (!memoryInfo.lowMemory) {
                        isFingerprint = true;
                        Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                        intent.putExtra("ScanningType", "Thumb");
                        startActivityForResult(intent, 200);
                    } else {
                        Toast.makeText(activity, "Low Memory-", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    userName.setText("Error Crash--- " + e.getMessage() + " " + e.getCause() + " " + e.getStackTrace());
                } catch (Error error) {
                    userName.setText("Error Crash--- " + error.getMessage() + " " + error.getCause() + " " + error.getStackTrace());
                }
            }
        });

        captureIndexFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
                    if (!memoryInfo.lowMemory) {
                        isFingerprint = true;
                        Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                        intent.putExtra("ScanningType", "Index");
                        startActivityForResult(intent, 200);
                    } else {
                        Toast.makeText(activity, "Low Memory-", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    userName.setText("Error Crash--- " + e.getMessage() + " " + e.getCause() + " " + e.getStackTrace());
                } catch (Error error) {
                    userName.setText("Error Crash--- " + error.getMessage() + " " + error.getCause() + " " + error.getStackTrace());
                }
            }
        });



        uploadFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              docsList = collectUploadedFingerprintData();
              if(docsList != null)
              if(docsList.size() != 0) {
                  uploadDocumentCommand(docsList);
              }else{
                  MyToast.makeMyToast(activity,"Please upload Fingerprints", Toast.LENGTH_SHORT);
              }
            }
        });

    }

    private void uploadDocumentCommand(List<UserRegistration.UserDocCommand> command) {
        if(command.size() != 0) {
            final int total = command.size();

            uploadProgressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, updating user information!");

            for (final UserRegistration.UserDocCommand userDocCommand : command) {
                try {
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    serviceHandler.updateDocument(userDocCommand, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {

                            UserLogin userLogin = (UserLogin)data.get(0);
                            if(userLogin.status.equals("success")){
                                if(++uploadedCommands == total){
                                    ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                                    MyToast.makeMyToast(activity,"Documents Uploaded Successfully.", Toast.LENGTH_SHORT);
                                    uploadDocuments(command);
                                }

                            }else if(userLogin.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else if(!userLogin.status.isEmpty()){
                                ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                alert.setTitle("Message");
                                alert.setMessage("Status:"+userLogin.status);
                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.create().show();
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                        }
                    });

                } catch (Exception io) {

                }
            }
        }

    }

    private void uploadDocuments(final List<UserRegistration.UserDocCommand> docCommandsList) {

        final int totalDocs = docCommandsList.size();
        final UserRegistration.UserDocCommand[] docCommand = new UserRegistration.UserDocCommand[docCommandsList.size()];
        docCommandsList.toArray(docCommand);

        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please Wait, Uploading Documents!");

        for (  int i=0; i<totalDocs; i++) {
            String prodPicDir = "documents/" + docCommand[i].docType +"/"+ docCommand[i].userId + "/";

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
                            ProgressDialogUtil.stopProgressDialog(progressDialog1);

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setIcon(R.drawable.success_icon);
                            alertDialog.setMessage("Fingerprint Uploaded Successfully.");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    RegistrationData.setSubscriberThumbImageDrawable(null);
                                    RegistrationData.setSubscriberIndexImageDrawable(null);
                                    UploadSubscriberFingerprint.this.finish();
                                }
                            });
                            alertDialog.show();
                        }

                    } else if (userLogin.status.equals("INVALID_SESSION")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                        ReDirectToParentActivity.callLoginActivity(activity);
                    } else if(!userLogin.status.isEmpty()){
                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                        //  registerUserAfterDocUploadFail();
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Alert!");
                        alertDialog.setMessage("Unable to upload your documents, please retry!");
                        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
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

                    // ProgressDialogUtil.stopProgressDialog(progressDialog1);
                    BugReport.postBugReport(activity, Constants.emailId, "STATUS:" + status + "ERROR:" + error, "USER_DOCS_NOT_UPLOADED");

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("Unable to upload your documents, please retry!");
                    alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });

        }




    }

    private List<UserRegistration.UserDocCommand> collectUploadedFingerprintData() {
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

         if(!isFingerprint){
             MyToast.makeMyToast(activity,"Please Upload Fingerprint.", Toast.LENGTH_SHORT);
             return null;
         }


            if(isThumbUploaded){
                UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                uDocCommand2.docFiles = prefixUname + "_thumb_fingerPrint_"+randomNumber+";";
                uDocCommand2.docType = "fingerprints";
                uDocCommand2.docFormat = "jpeg";
                uDocCommand2.reviewStatus = "Accepted";
                uDocCommand2.userId = userId;

                Bitmap bitMap = PictureUtility.drawableToBitmap(RegistrationData.getSubscriberThumbImageDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand2.imageData = imgData;
                userDocCommandList.add(uDocCommand2);
            }

            if(isIndexUploaded){
                UserRegistration.UserDocCommand uDocComnd = new UserRegistration.UserDocCommand();
                uDocComnd.docFiles = prefixUname + "_index_fingerPrint_"+randomNumber+";";
                uDocComnd.docType = "fingerprints";
                uDocComnd.docFormat = "jpeg";
                uDocComnd.reviewStatus = "Accepted";
                uDocComnd.userId = userId;
                Bitmap bitMap1 = PictureUtility.drawableToBitmap(RegistrationData.getSubscriberIndexImageDrawable());
                String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                uDocComnd.imageData = imgData1;
                userDocCommandList.add(uDocComnd);
            }


        return userDocCommandList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(UploadSubscriberFingerprint.this);
    }

    private void checkSubscriptionValue() {

        RestServiceHandler handle = new RestServiceHandler();
        try {

            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
            handle.getUserSubscriptionsByMSISDN(servedMSISDN.getText().toString(), new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    final SubscriptionCommand userLogin = (SubscriptionCommand) data.get(0);

                    if (userLogin.statusReason.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                        if (userLogin.userInfo != null) {
                           /* if(userLogin.userInfo.userDocs != null){
                                for(UserRegistration.UserDocCommand docCommand : userLogin.userInfo.userDocs){
                                    if(docCommand.docType.equalsIgnoreCase("fingerprints")){
                                        MyToast.makeMyToast(activity, "You already uploaded Fingerprints.", Toast.LENGTH_SHORT);
                                    }
                                }
                            }*/
                             userId = userLogin.userInfo.userId;
                            setUserDetails(userLogin.userInfo);
                        } else {
                            alertDialogMessage(activity, "Alert", "Unable to find the users details.");
                        }

                    } else {

                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                        if (userLogin.statusReason.equals("INVALID_SESSION")) {
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {
                            String message = "Status:-" + userLogin.statusReason.toString() + ". " + "Please try another Subscription Number.";
                            AlertMessage.alertDialogMessage(activity, "Message!", message);
                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"\n STATUS"+status,"UPLOAD FINGERPRINT");

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"UPLOAD FINGERPRINT");
        }
    }

    private void setUserDetails(UserRegistration userInfo) {

        user_detail_layout.setVisibility(View.VISIBLE);

        if (userInfo.userName != null) {
            userName.setText(userInfo.userName.toString());
            prefixUname = userInfo.userName.toString();
        } else {
            userName.setText("");
            prefixUname = "";
        }
        if (userInfo.fullName != null) {
            givenName.setText(userInfo.fullName);
        } else {
            givenName.setText("");
        }

        if (userInfo.surname != null) {
            surName.setText(userInfo.surname);
        } else {
            surName.setText("");
        }

        captureThumbFingerprint.setEnabled(true);
        captureIndexFingerprint.setEnabled(true);
        uploadFingerprint.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            imagesList = new ArrayList<Drawable>();

            if (RegistrationData.getSubscriberThumbImageDrawable() != null) {
                isThumbUploaded = true;
                thumbFingerImage.setVisibility(View.VISIBLE);
                thumbFingerImage.setImageDrawable(RegistrationData.getSubscriberThumbImageDrawable());
                imagesList.add(RegistrationData.getSubscriberThumbImageDrawable());
                captureIndexFingerprint.setEnabled(true);
            }
            if (RegistrationData.getSubscriberIndexImageDrawable() != null) {
                isIndexUploaded = true;
                indexFingerImage.setVisibility(View.VISIBLE);
                indexFingerImage.setImageDrawable(RegistrationData.getSubscriberIndexImageDrawable());
                imagesList.add(RegistrationData.getSubscriberIndexImageDrawable());
            }
        }
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
    public void onTrimMemory(int level) {
        System.gc();
    }
    private long generateRandomNumber() {
        double n = Math.random();
        long n3 = Math.round(Math.random() * 1000);
        return n3;
    }


    /*

       uploadFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageCount = 0;
                imageUploadSuccessCount = 0;

                if (imagesList != null)
                    if (imagesList.size() == 2) {
                        for (Drawable dbImage : imagesList) {

                            Bitmap bitmap = PictureUtility.drawableToBitmap(dbImage);
                            String imageData = PictureUtility.encodePicutreBitmap(bitmap);

                            if (!prefixUname.isEmpty() && imageCount == 0) {
                                fileName = prefixUname + "_thumb_fingerPrint_" + imageCount;
                            } else {
                                fileName = prefixUname + "_index_fingerPrint_" + imageCount;

                            }

                            String directoryPath = "fingerprints/" + userId + "/"; //*//*UserSession.getUserId(getApplicationContext()).toString()*//*+"/";

                            RestServiceHandler serviceHandler = new RestServiceHandler();

                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading fingerprint!");
                            serviceHandler.uploadImage(directoryPath + "," + fileName, imageData, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userInfo = (UserLogin) data.get(0);
                                    if (userInfo.status.equals("success")) {

                                        if (++imageUploadSuccessCount == imagesList.size()) {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            Toast.makeText(activity, getResources().getString(R.string.Successfully_uploaded_fingerprint), Toast.LENGTH_SHORT).show();


                                            Intent intent = new Intent(activity, NavigationMainActivity.class);
                                            startActivity(intent);

                                            RegistrationData.setSubscriberThumbImageDrawable(null);
                                            RegistrationData.setSubscriberIndexImageDrawable(null);
                                            System.gc();
                                        }

                                    } else {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                       // Toast.makeText(activity, "Status" + subscriptionCommand.status, Toast.LENGTH_SHORT).show();

                                        if (subscriptionCommand.status.equals("INVALID_SESSION")) {
                                            ReDirectToParentActivity.callLoginActivity(activity);
                                        } else {
                                            MyToast.makeMyToast(activity, "\nStatus:" + subscriptionCommand
                                                    .status.toString(), Toast.LENGTH_SHORT);
                                        }

                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n status"+status,"UPLOAD_SUBSCRIBER_FINGERPRINT");
                                }
                            });
                            imageCount++;
                        }
                    } else {
                        Toast.makeText(activity, "Please Scan your finger !", Toast.LENGTH_LONG).show();
                    }
            }
        });

    */

}
