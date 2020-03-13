package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.Subscription;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.PictureUtility;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.soundcloud.android.crop.Crop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.aryagami.util.PictureUtility.selectImage;

public class UpdateVisaActivity extends AppCompatActivity {
    public String filePrefix = "";
    Boolean isVisaImage = false;
    UserRegistration userRegistration = new UserRegistration();
    Activity activity = this;
    List<View> visaImages = new ArrayList<View>();
    int mYear,mMonth,mDay;
    ProgressDialog progressDialog,progressDialog1;
    Subscription[] subscriptionArray,inactiveSubscriptionsArray;
    UserLogin userLoginDetails;
    List<Subscription> inactiveSubscriptionVisaExpire = new ArrayList<Subscription>();
    int activatedCount = 0;
    Long randomNumber;
    int passportUploadSuccessCount = 0;
    String filename = "";
    String postDocID, postfilename, uuid;
    NewOrderCommand command;
    List<String> tempVisaUrl = new ArrayList<String>();

    LinearLayout thumbnailsLayoutVisa;
    List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

    public  void onTrimMemory(int level) {
        System.gc();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_visa_activity);

        userRegistration = (UserRegistration) getIntent().getSerializableExtra("REGISTRATIOIN");

        Button cancel = (Button) findViewById(R.id.cancel_btn);
        Button submit = (Button) findViewById(R.id.submit_btn);
        Button captureVisa = (Button) findViewById(R.id.capture_visa);
        final TextInputEditText visaExpiryDate = (TextInputEditText) findViewById(R.id.validity_date);

        thumbnailsLayoutVisa = (LinearLayout)findViewById(R.id.visa_new_picture_thumbnails);
        thumbnailsLayoutVisa.removeAllViews();
        uuid = UUID.randomUUID().toString();

        visaExpiryDate.setInputType(InputType.TYPE_NULL);
        visaExpiryDate.requestFocus();
        visaExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                              visaExpiryDate.setText(year+"-"+((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1))+"-"+((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth))+" 00:00:00");

                              /*  visaExpiryDate.setText(((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) + "-"
                                        + ((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1)) + "-" + year+" 00:00:00");
*/
                            }
                        }, mYear, mMonth, mDay);
                // Disable past dates in Android date picker
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
              Intent i = new Intent(activity, UpdateVisaValidityActivity.class);
              startActivity(i);
            }
        });

       /* submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRegistration registration = collectUserDocs();
                if (registration != null) {

                    if (visaExpiryDate.getText().toString().isEmpty()) {
                        MyToast.makeMyToast(activity, "Please Select Validity Date.", Toast.LENGTH_SHORT);
                        return;
                    } else {
                        userRegistration.visaValidityDate = visaExpiryDate.getText().toString();

                        RestServiceHandler serviceHandler = new RestServiceHandler();
                        try {
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait,Updating Visa Validity Date!");
                            serviceHandler.postUpdateVisaValidity(userRegistration, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {

                                    UserLogin orderDetails = (UserLogin) data.get(0);
                                    if (orderDetails.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        if (visaImages.size() != 0) {

                                            String passportPicDir =  "documents/visa/" + orderDetails.userId + "/";

                                            int passportDocsNum = 0;
                                            passportUploadSuccessCount = 0;
                                            if (visaImages.size() != 0)
                                                for (View view : visaImages) {
                                                    ImageView imageView = (ImageView) view;
                                                    Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                    String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                    if (filePrefix != null) {
                                                        filename = filePrefix + "_updated_visa_" + (randomNumber + passportDocsNum++);
                                                    } else {
                                                        filename = "updated_visa_" + (passportDocsNum++);
                                                    }

                                                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                    uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                        @Override
                                                        public void success(DataModel.DataType type, List<DataModel> data) {
                                                            UserLogin userLogin = (UserLogin) data.get(0);
                                                            if (userLogin.status.equals("success")) {

                                                                if (++passportUploadSuccessCount == visaImages.size()) {
                                                                    visaImages.clear();
                                                                    // MyToast.makeMyToast(activity, "Visa Image Uploaded Successfully.", Toast.LENGTH_LONG);

                                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                                    alertDialog.setCancelable(false);
                                                                    alertDialog.setMessage("Visa Validity Date Updated Successfully.");
                                                                    alertDialog.setNeutralButton("Ok",
                                                                            new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int id) {

                                                                                    dialog.dismiss();
                                                                                    startNavigationMenu();

                                                                                }
                                                                            });

                                                                    alertDialog.show();

                                                                }

                                                            } else {
                                                                 ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                                alertDialog.setCancelable(false);
                                                                alertDialog.setMessage("Visa Validity Date Not Updated.");
                                                                alertDialog.setNeutralButton("Ok",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int id) {

                                                                                dialog.dismiss();
                                                                                startNavigationMenu();
                                                                            }
                                                                        });

                                                                alertDialog.show();
                                                            }
                                                        }

                                                        @Override
                                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                            BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n STATUS:"+status,"UPDATE_VISA_VALIDITY");
                                                            startNavigationMenu();
                                                        }
                                                    });
                                                }
                                        }


                                    } else {

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                                        if (orderDetails.status.equals("INVALID_SESSION")) {
                                            ReDirectToParentActivity.callLoginActivity(activity);
                                        } else {

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setMessage("Visa Validity Date Not Updated Successfully.");
                                            alertDialog.setNeutralButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            dialog.dismiss();
                                                            startNavigationMenu();
                                                        }
                                                    });

                                            alertDialog.show();

                                        }
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    startNavigationMenu();
                                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n STATUS:"+status,"UPDATE_VISA_VALIDITY");

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            BugReport.postBugReport(activity, Constants.emailId,"Error"+e.getCause()+"MESSAGE:"+e.getMessage(),"UPDATE_VISA_VALIDITY");
                        }
                    }



                }
            }
        });*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRegistration registration = collectUserDocs();
                if (registration != null) {

                    if (visaExpiryDate.getText().toString().isEmpty()) {
                        MyToast.makeMyToast(activity, "Please Select Validity Date.", Toast.LENGTH_SHORT);
                        return;
                    } else {

                       userRegistration.visaValidityDate = visaExpiryDate.getText().toString();
                        uploadVisaDocuments(userRegistration.tempUserToken);

                    }
                }
            }
        });

        captureVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisaImage = true;
                selectImage(activity);
            }
        });

        setVisaDocument();

    }

    private void uploadVisaDocuments(String tempUserToken) {
        if (userDocCommandList.size() != 0) {

            String passportPicDir =  "temp_documents/visa/" + tempUserToken + "/";

            final int totalVisaImages = visaImages.size();

            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait,Uploading Visa Documents!");
                for (UserRegistration.UserDocCommand docCommand: userDocCommandList) {

                    String filenames[] = docCommand.docFiles.split(";");

                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                    uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," +filenames[0] , docCommand.imageData, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            if (userLogin.status.equals("success")) {
                                if (++passportUploadSuccessCount == totalVisaImages) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    visaImages.clear();

                                    updateVisaValidityDate();
                                }

                            } else if(userLogin.status.equals("INVALID_SESSION")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);

                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setTitle("Alert!");
                                alertDialog.setMessage("Unable to Update Visa Documents, please retry!");
                                alertDialog.setNeutralButton("Ok",
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
                            BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n STATUS:"+status,"UPDATE_VISA_VALIDITY");

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Alert!");
                            alertDialog.setMessage("Unable to Update Visa Documents, please retry!");
                            alertDialog.setNeutralButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });

                            alertDialog.show();
                        }
                    });
                }
        }
    }

    private void updateVisaValidityDate() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, Updating Visa Validity Date!");
        try {
            serviceHandler.postUpdateVisaValidity(userRegistration, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    UserLogin orderDetails = (UserLogin) data.get(0);
                    if (orderDetails.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog1);

                        //userRegistration.documentsUploadPending =false;
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setCancelable(false);
                        alertDialog.setIcon(R.drawable.success_icon);
                        alertDialog.setMessage("Visa Validity Date Updated Successfully.");
                        alertDialog.setNeutralButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        startNavigationMenu();
                                    }
                                });

                        alertDialog.show();


                    } else {

                        ProgressDialogUtil.stopProgressDialog(progressDialog1);

                        if (orderDetails.status.equals("INVALID_SESSION")) {
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Alert!");
                            alertDialog.setMessage(orderDetails.status.toString());
                            alertDialog.setNeutralButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.dismiss();
                                            startNavigationMenu();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                    startNavigationMenu();
                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"\n STATUS:"+status,"UPDATE VISA VALIDITY");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error"+e.getCause()+"MESSAGE:"+e.getMessage(),"UPDATE VISA VALIDITY");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(UpdateVisaActivity.this);
    }

    private void startNavigationMenu() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            if (isVisaImage) {
                View visaPictureContainer = findViewById(R.id.visa_images_cont1);
                PictureUtility.processNewVisaPictureRequestWithEdit(activity, visaPictureContainer, requestCode, data, visaImages, true);
                isVisaImage = false;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Crop.REQUEST_CROP)
                PictureUtility.editImageDone();
        }
    }

    private UserRegistration collectUserDocs() {
        if(visaImages.size() == 0){
            MyToast.makeMyToast(activity,"Please Upload Image.", Toast.LENGTH_SHORT);
            return  null;
        }
        filePrefix = buildProductPictureFilePrefix(userRegistration);
        randomNumber = generateRandomNumber();

        String visaDocuments = "";

       /* if(userRegistration.userDocs != null |userRegistration.userDocs.size() != 0)
        {
            List<UserRegistration.UserDocCommand> userDocCommandList1 = new ArrayList<UserRegistration.UserDocCommand>();
            userDocCommandList1 = userRegistration.userDocs;

            for(UserRegistration.UserDocCommand uDoc : userDocCommandList1) {
                if(uDoc != null){

                    String ninDoc[] = uDoc.docFiles.toString().split(";");

                    if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                        tempVisaUrl.add(Constants.visaImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                    } else {
                        tempVisaUrl.add(Constants.visaImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                    }

                }

            }

        }*/

        if(userRegistration.userDocs != null |userRegistration.userDocs.size() != 0){
            List<UserRegistration.UserDocCommand> userDocCommandList1 = new ArrayList<UserRegistration.UserDocCommand>();
            userDocCommandList1 = userRegistration.userDocs;
            for(UserRegistration.UserDocCommand uDoc : userDocCommandList1) {
              if(uDoc != null)
                  if(uDoc.docType.contains("visa")){
                      postDocID = uDoc.docId;
                      postfilename = uDoc.docFiles;
                  }else{
                      continue;
                  }
            }
        }


        if (visaImages.size() != 0) {
            View[] idImagesArray = new View[visaImages.size()];
            visaImages.toArray(idImagesArray);

            for (int numFiles = 0; numFiles < visaImages.size(); numFiles++) {
               // visaDocuments += filePrefix + "_updated_visa_" + (randomNumber + numFiles)+";";


            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();

            uDocCommand1.docFiles = filePrefix + "_updated_visa_" + (randomNumber + numFiles)+";";
            uDocCommand1.docType = "visa";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;
                if(numFiles ==0) {
                    uDocCommand1.docId = postDocID;
                }

                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);
                uDocCommand1.imageData = imgData;
                userDocCommandList.add(uDocCommand1);
            }

        }
        userRegistration.userDocs = userDocCommandList;
        return userRegistration;
    }


    private void setVisaDocument() {

        if(tempVisaUrl != null)
            for (String url : tempVisaUrl) {
                final ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutVisa.addView(thumbNail);

                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempVisaUrl.clear();
    }

    private Long generateRandomNumber() {
        double n = Math.random();
        long n3 = Math.round(Math.random() * 1000);
        return n3;
    }


    private String buildProductPictureFilePrefix(UserRegistration userRegInfo) {
        String prodNameToks[] = userRegInfo.userName.split(" ");
        String filePrefix = "";

        int count = 0;
        for (String tok : prodNameToks) {
            if (++count > Constants.PRODUCT_PICTURE_FILE_PREFIX_TOKENS_MAX) {
                break;
            }
            filePrefix += tok;
            if (count == 0) {
                filePrefix += "_";
            }
        }

        return filePrefix;
    }

}
