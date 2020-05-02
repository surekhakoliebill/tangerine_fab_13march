package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DocumentTypes;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.FilePath;
import com.aryagami.util.MarshMallowPermission;
import com.aryagami.util.MyToast;
import com.aryagami.util.PictureUtility;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aryagami.util.PictureUtility.selectImage;

public class OnDemandAccountSetupActivity extends AppCompatActivity {

    Button upload_nin_file, upload_profile_File, upload_passport_file,captureRefugee,captureVisaBtn,captureActicationFormBtn;
    UserRegistration userRegistration, data;
    ProgressDialog progressDialog;
    int imageUploadSuccessCount = 0;
    int passportUploadSuccessCount = 0;
    UserLogin userLoginResult;
    String filePrefix = "";
    Boolean isProfileImage = false;
    Boolean isPassportImage = false;
    Boolean isRefugeeImage = false;
    Boolean isActivationFormImage = false;
    Boolean isVisaImage = false;
    Boolean isNinImage = false;
    Activity activity = this;
    Spinner spinner;
    int mandatoryDocCount = 0;

    TextView notification, notification1, notification2, notification3, notification4, notification5, notification6, notification7;
    Button certifiedcompany, Certifiedformfile, copiesdirectories, auditedfinancial, URAcertified, previousbusiness, bankstatement;
    Button cmaButton;
    Uri pdfUri;
    List<Button> buttonsList = new ArrayList<Button>();

    LinearLayout defaultAccountContainer, associateAccountContainer, accountsetupPersonal, accountsetupCompany;
    RadioButton defaultAccount, associateAccount;
    List<View> idImages = new ArrayList<View>();
    List<View> passportImages = new ArrayList<View>();
    List<View> refugeeImages = new ArrayList<View>();
    List<View> profileImages = new ArrayList<View>();
    List<View> activationFormImages = new ArrayList<View>();
    List<View> visaImages = new ArrayList<View>();
    List<View> pdfFile = new ArrayList<View>();
    private boolean isFingerPrint = false;
    NewOrderCommand newOrderCommand;
    ImageView fingerPrintImage, capturedFingerPrint;
    private boolean isPdfFile = false;

    DocumentTypes[] pdfUpload, companyDocs, personalDocs;
    List<DocumentTypes> companyDocList = new ArrayList<DocumentTypes>();
    List<DocumentTypes> personalDocList = new ArrayList<DocumentTypes>();
    Bitmap bitmap;

    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    PdfDocumentData[] uploadedDocArray;

    Button captureThumbFingerprint, captureIndexFingerprint;
    ImageView thumbFingerImage, indexFingerImage;
    List<Drawable> userFingerprintsList = new ArrayList<Drawable>();
    
    LinearLayout capturedFingerprintLayout, uploadFingerprintLayout;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ondemand_fragment_account_setup);

        userRegistration = RegistrationData.getOnDemandRegistrationData();
        newOrderCommand = NewOrderCommand.getOnDemandNewOrderCommand();

        certifiedcompany = findViewById(R.id.certified_company);
        Certifiedformfile = findViewById(R.id.Certified_form_file);
        copiesdirectories = findViewById(R.id.copies_directories);
       // copiesdirectories.setVisibility(View.VISIBLE);
        auditedfinancial = findViewById(R.id.audited_financial);
        URAcertified = findViewById(R.id.URA_certified);
        previousbusiness = findViewById(R.id.previous_business);
        bankstatement = findViewById(R.id.bank_statement);
        cmaButton = findViewById(R.id.cma_doc);

        captureThumbFingerprint = findViewById(R.id.capture_thumb_print);
        captureIndexFingerprint = findViewById(R.id.capture_index_finger_print);
        captureThumbFingerprint.setEnabled(true);
        captureIndexFingerprint.setEnabled(true);

        thumbFingerImage = findViewById(R.id.thumb_image);
        indexFingerImage = findViewById(R.id.index_finger_image);
        captureRefugee = findViewById(R.id.capture_refugee);
        captureVisaBtn = findViewById(R.id.capture_visa);
        captureActicationFormBtn = findViewById(R.id.capture_activation_form);
        capturedFingerPrint = (ImageView)findViewById(R.id.thumb_image1);

        buttonsList.add(certifiedcompany);
        buttonsList.add(Certifiedformfile);
        buttonsList.add(auditedfinancial);
        buttonsList.add(URAcertified);
        buttonsList.add(previousbusiness);
        buttonsList.add(bankstatement);
        buttonsList.add(cmaButton);

        /*if (DocumentTypes.getCompanyDocArray() != null) {
            if (DocumentTypes.getCompanyDocArray().length != 0) {
                companyDocs = new DocumentTypes[DocumentTypes.getCompanyDocArray().length];
                uploadedDocArray = new PdfDocumentData[DocumentTypes.getCompanyDocArray().length];
                companyDocs = DocumentTypes.getCompanyDocArray();
                certifiedcompany.setText(companyDocs[0].displayName);
                Certifiedformfile.setText(companyDocs[1].displayName);
                auditedfinancial.setText(companyDocs[2].displayName);
                URAcertified.setText(companyDocs[3].displayName);
                previousbusiness.setText(companyDocs[4].displayName);
                bankstatement.setText(companyDocs[5].displayName);
                cmaButton.setText(companyDocs[6].displayName);

                for (DocumentTypes types : DocumentTypes.getCompanyDocArray()) {
                    if (types.isMandatory) {
                        mandatoryDocCount++;
                    }
                }
            }
        } else {
        }*/

        accountsetupPersonal = findViewById(R.id.personal_mandatory);
        accountsetupCompany = findViewById(R.id.company_mandatory);
        LinearLayout passportDocLayout = findViewById(R.id.passport_document_layout);
        LinearLayout ninDocLayout = findViewById(R.id.nin_document_layout);
        final LinearLayout refugeeDocLayout = findViewById(R.id.refugee_document_layout);
        LinearLayout activationDocLayout = findViewById(R.id.activation_form_layout);
        LinearLayout visaDocLayout = findViewById(R.id.visa_document_layout);

        capturedFingerprintLayout = (LinearLayout)findViewById(R.id.captured_fingerprint_layout);
        uploadFingerprintLayout = (LinearLayout)findViewById(R.id.upload_fingerprint_layout);

       if(userRegistration != null)
           if(userRegistration.registrationType != null) {
               if (userRegistration.registrationType.equals("personal") || userRegistration.registrationType.equals("retailer")) {
                   accountsetupPersonal.setVisibility(View.VISIBLE);
                   accountsetupCompany.setVisibility(View.GONE);

                   if (userRegistration.nationalIdentity != null) {
                       if (userRegistration.nationalIdentity.equals("Ugandan NationalID")) {
                           RegistrationData.setIsUgandan(true);
                           ninDocLayout.setVisibility(View.VISIBLE);
                           passportDocLayout.setVisibility(View.GONE);
                           refugeeDocLayout.setVisibility(View.GONE);
                           activationDocLayout.setVisibility(View.VISIBLE);
                           visaDocLayout.setVisibility(View.GONE);

                           /*capturedFingerprintLayout.setVisibility(View.VISIBLE);
                           uploadFingerprintLayout.setVisibility(View.GONE);
                           */

                           if(RegistrationData.getCapturedFingerprintDrawable()!= null){
                               capturedFingerprintLayout.setVisibility(View.VISIBLE);
                               uploadFingerprintLayout.setVisibility(View.GONE);
                               capturedFingerPrint.setVisibility(View.VISIBLE);
                               capturedFingerPrint.setImageDrawable(RegistrationData.getCapturedFingerprintDrawable());
                           }else{
                               capturedFingerprintLayout.setVisibility(View.GONE);
                               uploadFingerprintLayout.setVisibility(View.VISIBLE);
                           }

                       } else if (userRegistration.nationalIdentity.equals("Passport No")) {
                           ninDocLayout.setVisibility(View.GONE);
                           passportDocLayout.setVisibility(View.VISIBLE);
                           refugeeDocLayout.setVisibility(View.GONE);
                           activationDocLayout.setVisibility(View.VISIBLE);
                           visaDocLayout.setVisibility(View.VISIBLE);

                           capturedFingerprintLayout.setVisibility(View.GONE);
                           uploadFingerprintLayout.setVisibility(View.VISIBLE);

                       } else if (userRegistration.nationalIdentity.equals("Refugee")) {
                           ninDocLayout.setVisibility(View.GONE);
                           passportDocLayout.setVisibility(View.GONE);
                           refugeeDocLayout.setVisibility(View.VISIBLE);
                           activationDocLayout.setVisibility(View.VISIBLE);
                           visaDocLayout.setVisibility(View.GONE);

                          /* capturedFingerprintLayout.setVisibility(View.VISIBLE);
                           uploadFingerprintLayout.setVisibility(View.GONE);*/

                           if(RegistrationData.getRefugeeThumbImageDrawable()!= null){
                               capturedFingerprintLayout.setVisibility(View.VISIBLE);
                               uploadFingerprintLayout.setVisibility(View.GONE);
                               capturedFingerPrint.setVisibility(View.VISIBLE);
                               capturedFingerPrint.setImageDrawable(RegistrationData.getRefugeeThumbImageDrawable());
                           }else{
                               capturedFingerprintLayout.setVisibility(View.GONE);
                               uploadFingerprintLayout.setVisibility(View.VISIBLE);
                           }
                       }
                   }
               }else if(userRegistration.registrationType.equals("company")){
                   getDocumentTypes();
                   activationDocLayout.setVisibility(View.VISIBLE);
                   accountsetupPersonal.setVisibility(View.GONE);
                   accountsetupCompany.setVisibility(View.VISIBLE);
               }
           }

          /*  if(userRegistration != null)
            if(userRegistration.registrationType != null)
        if (userRegistration.registrationType.equals("personal")) {
            accountsetupPersonal.setVisibility(View.VISIBLE);
            accountsetupCompany.setVisibility(View.GONE);

        } else if(userRegistration.registrationType.equals("company")){
                getDocumentTypes();
                accountsetupPersonal.setVisibility(View.GONE);
                accountsetupCompany.setVisibility(View.VISIBLE);
        }*/

        if(RegistrationData.getCapturedFingerprintDrawable()!= null){
            capturedFingerPrint.setVisibility(View.VISIBLE);
            capturedFingerPrint.setImageDrawable(RegistrationData.getCapturedFingerprintDrawable());
        }

        if(RegistrationData.getRefugeeThumbImageDrawable()!= null){
            capturedFingerPrint.setVisibility(View.VISIBLE);
            capturedFingerPrint.setImageDrawable(RegistrationData.getRefugeeThumbImageDrawable());
        }

        captureThumbFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isFingerPrint = true;
                    RegistrationData.setIsFingerPrint(true);
                    Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "userThumb");
                    startActivityForResult(intent, 198);

                } catch (Exception e) {
                    // userName.setText("Error Crash--- " + e.getMessage() + " " + e.getCause() + " "+ e.getStackTrace());
                } catch (Error error) {
                    //userName.setText("Error Crash--- " + error.getMessage() + " " + error.getCause() + " "+ error.getStackTrace());
                }
            }
        });
        captureIndexFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isFingerPrint = true;
                    RegistrationData.setIsFingerPrint(true);
                    Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "userIndex");
                    startActivityForResult(intent, 199);
                } catch (Exception e) {
                    // userName.setText("Error Crash--- " + e.getMessage() + " " + e.getCause() + " "+ e.getStackTrace());
                } catch (Error error) {
                    //  userName.setText("Error Crash--- " + error.getMessage() + " " + error.getCause() + " "+ error.getStackTrace());
                }
            }
        });
        fingerPrintImage = findViewById(R.id.finger_image);
        Button submit = findViewById(R.id.submitfinal_btn);
        submit.setVisibility(View.GONE);
        Button cancel = findViewById(R.id.cancelfinal_btn);
        final Button saveAndContinue = findViewById(R.id.save_and_continue_btn);

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegistration = CollectDummyData(userRegistration);
                if (userRegistration.registrationType.equals("company")) {
                    UserRegistration registration1 = collectPdfDocs();
                    if(activationFormImages != null){
                        RegistrationData.setActivationImages(activationFormImages);
                    }
                    if (registration1 != null) {
                        RegistrationData.setFilePrefix(filePrefix);
                        newOrderCommand.userInfo = userRegistration;
                        PdfDocumentData.setFinalDocsList(Arrays.asList(uploadedDocArray));
                        NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommand);
                        if (!newOrderCommand.isPostpaid) {
                            Intent intent = new Intent(activity, OnDemandAddSubscriptionActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(activity, OnDemandContractInformationActivity.class);
                            startActivity(intent);
                        }
                    }

                } else if (userRegistration.registrationType.equals("personal") || userRegistration.registrationType.equals("retailer")) {

                    if (newOrderCommand != null) {
                        UserRegistration userRegistration2 = collectUserDocs();
                        if (userRegistration2 != null) {

                            if (idImages != null) {
                                RegistrationData.setNinIdImages(idImages);
                            }
                            if (passportImages != null) {
                                RegistrationData.setPassportIdImages(passportImages);
                            }
                            if (profileImages != null) {
                                RegistrationData.setProfileImages(profileImages);
                            }

                            if(activationFormImages != null){
                                RegistrationData.setActivationImages(activationFormImages);
                            }

                            if(visaImages != null){
                                RegistrationData.setVisaImages(visaImages);
                            }
                            if(refugeeImages != null){
                                RegistrationData.setRefugeeImages(refugeeImages);
                            }

                            RegistrationData.setFilePrefix(filePrefix);
                            RegistrationData.setUserFingerprintsList(userFingerprintsList);
                            newOrderCommand.userInfo = userRegistration;
                            NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommand);
                            if (!newOrderCommand.isPostpaid) {
                                Intent intent = new Intent(activity, OnDemandAddSubscriptionActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(activity, OnDemandContractInformationActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });

        upload_profile_File = findViewById(R.id.upload_profile_file);
        upload_passport_file = findViewById(R.id.capture_passport);
        upload_nin_file = findViewById(R.id.upload_nin_file);

        notification = findViewById(R.id.notification);
        notification1 = findViewById(R.id.notification1);
        notification2 = findViewById(R.id.notification2);
        notification3 = findViewById(R.id.notification3);
        notification4 = findViewById(R.id.notification4);
        notification5 = findViewById(R.id.notification5);
        notification6 = findViewById(R.id.notification6);
        notification7 = findViewById(R.id.notification7);

        certifiedcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = certifiedcompany.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,71 ,documentName);
            }
        });

        Certifiedformfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = Certifiedformfile.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,72 ,documentName);
               }
        });

        copiesdirectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = copiesdirectories.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,73 ,documentName);

            }
        });

        auditedfinancial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = auditedfinancial.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,74 ,documentName);
            }
        });

        URAcertified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = URAcertified.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,75 ,documentName);
            }
        });

        previousbusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = previousbusiness.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,76 ,documentName);
            }
        });

        bankstatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = bankstatement.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,77 ,documentName);
            }
        });

       /* cmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = cmaButton.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity,78 ,documentName);
            }
        });*/

        upload_profile_File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileImage = true;
                selectImage(activity);
            }
        });

        upload_passport_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPassportImage = true;
                selectImage(activity);
            }
        });

        upload_nin_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNinImage = true;
                selectImage(activity);
            }
        });

        captureVisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisaImage = true;
                selectImage(activity);
            }
        });

        captureActicationFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isActivationFormImage = true;
                selectImage(activity);
            }
        });

        captureRefugee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRefugeeImage = true;
                selectImage(activity);
            }
        });

        if (RegistrationData.getImageDrawable() != null) {
            fingerPrintImage.setVisibility(View.VISIBLE);
            fingerPrintImage.setImageDrawable(RegistrationData.getImageDrawable());
        }

        spinner = findViewById(R.id.item_one_spinner);
        submit.setVisibility(View.GONE);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {

                    userRegistration = CollectDummyData(userRegistration);
                    collectUserDocs();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                    serviceHandler.postUserRegisteration(userRegistration, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            userLoginResult = (UserLogin) data.get(0);
                            if (userLoginResult.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage(getResources().getString(R.string.Successfully_uploaded_user_data));
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }

                            String prodPicDir = "";
                            for (DataModel prodData : data) {
                                if (!prodPicDir.isEmpty())
                                    prodPicDir += ":";
                                UserLogin user = (UserLogin) prodData;
                                prodPicDir += "documents/nin/" + user.userId + "/";
                            }

                            int imageNum = 0;
                            imageUploadSuccessCount = 0;
                            if (idImages.size() != 0)
                                for (View view : idImages) {
                                    ImageView imageView = (ImageView) view;
                                    Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                    String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                    String filename = filePrefix + "_" + (imageNum++);
                                    ProgressDialogUtil.updateProgressDialog(progressDialog, "please wait, uploading National Id Documents!" + imageNum);
                                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                    uploadImageServiceHandler.uploadImage(prodPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {
                                            UserLogin userLogin = (UserLogin) data.get(0);
                                            if (userLogin.status.equals("success")) {
                                                if (++imageUploadSuccessCount == idImages.size()) {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setMessage(getResources().getString(R.string.Successfully_uploaded_user_data));
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
                                        }
                                    });
                                }

                            String passportPicDir = "";
                            for (DataModel prodData : data) {
                                if (!passportPicDir.isEmpty())
                                    passportPicDir += ":";
                                UserLogin user = (UserLogin) prodData;
                                passportPicDir += "documents/passport/" + user.userId + "/";
                            }
                            int passportDocsNum = 0;
                            passportUploadSuccessCount = 0;
                            if (passportImages != null)
                                for (View view : passportImages) {
                                    ImageView imageView = (ImageView) view;
                                    Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                    String imgData = PictureUtility.encodePicutreBitmap(bitMap);
                                    String filename = filePrefix + "_" + (passportDocsNum++);
                                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, uploading Passport Documents!");

                                    RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                    uploadImageServiceHandler.uploadImage(passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {
                                            UserLogin userLogin = (UserLogin) data.get(0);
                                            if (userLogin.status.equals("success")) {
                                                if (++passportUploadSuccessCount == passportImages.size()) {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setMessage(getResources().getString(R.string.Successfully_uploaded_passport_documents));
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
                                        }
                                    });
                                }

                            String fingerPicDir = "";
                            for (DataModel prodData : data) {
                                if (!fingerPicDir.isEmpty())
                                    fingerPicDir += ":";
                                UserLogin user = (UserLogin) prodData;
                                fingerPicDir += "documents/fingerprints/" + user.userId + "/";
                            }
                            if (isFingerPrint && RegistrationData.getImageDrawable() != null) {
                                Bitmap bitmap = PictureUtility.drawableToBitmap(RegistrationData.getImageDrawable());
                                String imageData = PictureUtility.encodePicutreBitmap(bitmap);
                                String fileName = "fingerPrint_0";
                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                serviceHandler.uploadImage(fingerPicDir + "," + fileName, imageData, new RestServiceHandler.Callback() {
                                    @Override
                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                        UserLogin userInfo = (UserLogin) data.get(0);
                                        if (userInfo.equals("success")) {
                                            Toast.makeText(activity, "Fingerprint Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    }
                                });

                            }
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Registration Success!");
                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            activity.finish();
                                            Intent intent = new Intent(activity, NavigationMainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            alertDialog.show();
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button finger_btn = findViewById(R.id.finger_print);
        finger_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFingerPrint = true;
                RegistrationData.setIsFingerPrint(true);
                Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                intent.putExtra("ScanningType", "userFinger");
                startActivityForResult(intent, 100);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        RadioGroup radioGroup1 = findViewById(R.id.radioGroup1);
        defaultAccountContainer = findViewById(R.id.default_account_container);
        associateAccountContainer = findViewById(R.id.existing_account_container);
        defaultAccount = findViewById(R.id.create_default_btn);
        associateAccount = findViewById(R.id.associate_existing_account_btn);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.create_default_btn:
                        defaultAccountContainer.setVisibility(View.VISIBLE);
                        saveAndContinue.setText("Continue");
                        associateAccountContainer.setVisibility(View.GONE);
                        break;
                    case R.id.associate_existing_account_btn:
                        defaultAccountContainer.setVisibility(View.GONE);
                        associateAccountContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandAccountSetupActivity.this);
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
                            if (upload.userType.equals("company")) {
                                if (upload.isMandatory) {
                                    companyDocList.add(upload);
                                    mandatoryDocCount++;
                                }
                            } else if (upload.userType.equals("personal")) {
                                personalDocList.add(upload);
                            }
                        }

                        companyDocs = new DocumentTypes[companyDocList.size()];
                        uploadedDocArray = new PdfDocumentData[companyDocList.size()];
                        personalDocs = new DocumentTypes[personalDocList.size()];
                        companyDocList.toArray(companyDocs);

                        if (companyDocs.length != 0) {
                            certifiedcompany.setText(companyDocs[0].displayName);
                            Certifiedformfile.setText(companyDocs[1].displayName);
                            copiesdirectories.setText(companyDocs[2].displayName);
                            auditedfinancial.setText(companyDocs[3].displayName);
                            URAcertified.setText(companyDocs[4].displayName);
                            previousbusiness.setText(companyDocs[5].displayName);
                            bankstatement.setText(companyDocs[6].displayName);
                        }
                        personalDocList.toArray(personalDocs);
                        enableCompanyDocButtons();

                    } else {
                        MyToast.makeMyToast(activity, "Unable to get Documents Types. Reason: INAVLID_SESSION", Toast.LENGTH_SHORT);
                        ReDirectToParentActivity.callLoginActivity(activity);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    disableCompanyDocButtons();
                    BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+"STATUS"+status,"GET_DOCUMENT_TYPES");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getCause()+"\n MESSAGE"+e.getMessage(),"GET_DOCUMENT_TYPES");
        }
    }

    private void disableCompanyDocButtons() {
        certifiedcompany.setEnabled(false);
        Certifiedformfile.setEnabled(false);
        copiesdirectories.setEnabled(false);
        auditedfinancial.setEnabled(false);
        URAcertified.setEnabled(false);
        previousbusiness.setEnabled(false);
        bankstatement.setEnabled(false);
    }

    private void enableCompanyDocButtons() {
        certifiedcompany.setEnabled(true);
        Certifiedformfile.setEnabled(true);
        copiesdirectories.setEnabled(true);
        auditedfinancial.setEnabled(true);
        URAcertified.setEnabled(true);
        previousbusiness.setEnabled(true);
        bankstatement.setEnabled(true);
    }


    private UserRegistration collectPdfDocs() {
        filePrefix = buildProductPictureFilePrefix(userRegistration);
        String activationDocuments = "";

        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        if(pdfDocumentDataList != null) {
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
        }


        if (activationFormImages.size() != 0) {
            for (int numFiles = 0; numFiles < activationFormImages.size(); numFiles++) {
                activationDocuments += filePrefix + "_activation_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
                uDocCommand3.docFiles = filePrefix + "_activation_" + numFiles + ";";
                uDocCommand3.docType = "activation";
                uDocCommand3.docFormat = "jpeg";
                uDocCommand3.reviewStatus = "Submitted";

                View[] idImagesArray = new View[activationFormImages.size()];
                activationFormImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand3.imageData = imgData;
                userDocCommandList.add(uDocCommand3);
            }
        }

        userRegistration.userDocs = userDocCommandList;
        RegistrationData.setPersonalRegistrationUserDocs(userDocCommandList);
        return userRegistration;
    }


    private UserRegistration collectUserDocs() {
        filePrefix = buildProductPictureFilePrefix(userRegistration);
        userRegistration.filePrefix = filePrefix;

        if (filePrefix != null) {

        }
        String docImages = "";
        String passportDocuments = "";
        String profileDocuments = "";
        String visaDocuments = "";
        String refugeeDocuments = "";
        String activationDocuments = "";
      //  UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        if (activationFormImages.size() == 0){
            MyToast.makeMyToast(activity, "Please Upload Activation Document.", Toast.LENGTH_LONG);
            return null;
        }

        if (profileImages.size() == 0) {
            MyToast.makeMyToast(activity, getResources().getString(R.string.Please_upload_profile_document), Toast.LENGTH_LONG);
            return null;
        }

        if (RegistrationData.getIsUgandan()) {
            if (idImages.size() == 0) {
                MyToast.makeMyToast(activity, getResources().getString(R.string.Please_upload_nin_document), Toast.LENGTH_LONG);
                return null;
            }

            if(RegistrationData.getCapturedFingerprintDrawable() == null){
               /* MyToast.makeMyToast(activity, "Please upload NIN User Fingerprint.", Toast.LENGTH_LONG);
                return null;*/
                if(RegistrationData.getUserThumbImageDrawable() == null){
                    MyToast.makeMyToast(activity, "Please upload thumb fingerprint.", Toast.LENGTH_LONG);
                    return null;
                }

                if(RegistrationData.getUserIndexImageDrawable() == null){
                    MyToast.makeMyToast(activity, "Please upload index fingerprint.", Toast.LENGTH_LONG);
                    return null;
                }
            }
        }


        if (RegistrationData.getIsForeigner()) {
            if (passportImages.size() == 0) {
                MyToast.makeMyToast(activity, getResources().getString(R.string.Please_upload_passport_document), Toast.LENGTH_LONG);
                return null;
            }
            if(visaImages.size() ==0){
                MyToast.makeMyToast(activity, "Please upload visa document.", Toast.LENGTH_LONG);
                return null;
            }

           /* Fingerprint Compulsory

           if(RegistrationData.getUserThumbImageDrawable() == null){
                MyToast.makeMyToast(activity, "Please upload thumb fingerprint.", Toast.LENGTH_LONG);
                return null;
            }

            if(RegistrationData.getUserIndexImageDrawable() == null){
                MyToast.makeMyToast(activity, "Please upload index fingerprint.", Toast.LENGTH_LONG);
                return null;
            }*/
        }

        if(RegistrationData.getIsRefugee()){
            if(refugeeImages.size() ==0){
                MyToast.makeMyToast(activity, "Please upload refugee document.", Toast.LENGTH_LONG);
                return null;
            }
/*
  Fingerprint Compulsory comment

  if(RegistrationData.getRefugeeThumbImageDrawable() == null){
                //MyToast.makeMyToast(activity, "Please Upload Refugee Fingerprint.", Toast.LENGTH_LONG);


                if(RegistrationData.getUserThumbImageDrawable() == null){
                    MyToast.makeMyToast(activity, "Please upload thumb fingerprint.", Toast.LENGTH_LONG);
                    return null;
                }

                if(RegistrationData.getUserIndexImageDrawable() == null){
                    MyToast.makeMyToast(activity, "Please upload index fingerprint.", Toast.LENGTH_LONG);
                    return null;
                }

            }*/
        }

        if (idImages.size() != 0) {
            for (int numFiles = 0; numFiles < idImages.size(); numFiles++) {
                docImages += filePrefix + "_nin_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();
                uDocCommand.docFiles = filePrefix + "_nin_" + numFiles + ";";
                uDocCommand.docType = "nin";
                uDocCommand.docFormat = "jpeg";
                uDocCommand.reviewStatus = "Submitted";

                View[] idImagesArray = new View[idImages.size()];
                idImages.toArray(idImagesArray);

                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand.imageData = imgData;
                userDocCommandList.add(uDocCommand);
            }
        }

        if (passportImages.size() != 0) {
            for (int numFiles = 0; numFiles < passportImages.size(); numFiles++) {
                passportDocuments += filePrefix + "_passport_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
                uDocCommand1.docFiles = filePrefix + "_passport_" + numFiles + ";";
                uDocCommand1.docType = "passport";
                uDocCommand1.docFormat = "jpeg";
                uDocCommand1.reviewStatus = "Submitted";

                View[] idImagesArray = new View[passportImages.size()];
                passportImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand1.imageData = imgData;
                userDocCommandList.add(uDocCommand1);
            }
        }

        if (profileImages.size() != 0) {
            for (int numFiles = 0; numFiles < profileImages.size(); numFiles++) {
                profileDocuments += filePrefix + "_profile_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
                uDocCommand3.docFiles = filePrefix + "_profile_" + numFiles + ";";
                uDocCommand3.docType = "profile";
                uDocCommand3.docFormat = "jpeg";
                uDocCommand3.reviewStatus = "Submitted";

                View[] idImagesArray = new View[profileImages.size()];
                profileImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand3.imageData = imgData;
                userDocCommandList.add(uDocCommand3);
            }
        }

        if (refugeeImages.size() != 0) {
            for (int numFiles = 0; numFiles < refugeeImages.size(); numFiles++) {
                refugeeDocuments += filePrefix + "_refugee_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
                uDocCommand3.docFiles = filePrefix + "_refugee_" + numFiles + ";";
                uDocCommand3.docType = "refugee";
                uDocCommand3.docFormat = "jpeg";
                uDocCommand3.reviewStatus = "Submitted";

                View[] idImagesArray = new View[refugeeImages.size()];
                refugeeImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand3.imageData = imgData;
                userDocCommandList.add(uDocCommand3);
            }
        }

        if (activationFormImages.size() != 0) {
            for (int numFiles = 0; numFiles < activationFormImages.size(); numFiles++) {
                activationDocuments += filePrefix + "_activation_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
                uDocCommand3.docFiles = filePrefix + "_activation_" + numFiles + ";";
                uDocCommand3.docType = "activation";
                uDocCommand3.docFormat = "jpeg";
                uDocCommand3.reviewStatus = "Submitted";

                View[] idImagesArray = new View[activationFormImages.size()];
                activationFormImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand3.imageData = imgData;
                userDocCommandList.add(uDocCommand3);
            }
        }

        if (visaImages.size() != 0) {
            for (int numFiles = 0; numFiles < visaImages.size(); numFiles++) {
                visaDocuments += filePrefix + "_visa_" + numFiles + ";";

                UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
                uDocCommand3.docFiles = filePrefix + "_visa_" + numFiles + ";";
                uDocCommand3.docType = "visa";
                uDocCommand3.docFormat = "jpeg";
                uDocCommand3.reviewStatus = "Submitted";

                View[] idImagesArray = new View[visaImages.size()];
                visaImages.toArray(idImagesArray);
                ImageView imageView = (ImageView)idImagesArray[numFiles];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand3.imageData = imgData;
                userDocCommandList.add(uDocCommand3);
            }
        }

        /*  WE ARE NOT GOING TO POST FINGERPRINT OBJCETS INSTEAD OF THAT DIRECTLY Uploading on the server.
         */


      /*
       //Commented on 7-02-2020
      if (isFingerPrint) {
            if (userFingerprintsList != null) {
                if (userFingerprintsList.size() == 2) {
                    UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                    uDocCommand2.docFiles = filePrefix + "_thumb_fingerPrint_0;";
                    uDocCommand2.docType = "fingerprints";
                    uDocCommand2.docFormat = "jpeg";
                    uDocCommand2.reviewStatus = "Accepted";

                    Bitmap bitMap = PictureUtility.drawableToBitmap(userFingerprintsList.get(0));
                    String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                    uDocCommand2.imageData = imgData;
                    userDocCommandList.add(uDocCommand2);

                    UserRegistration.UserDocCommand uDocComnd = new UserRegistration.UserDocCommand();
                    uDocComnd.docFiles = filePrefix + "_index_fingerPrint_1;";
                    uDocComnd.docType = "fingerprints";
                    uDocComnd.docFormat = "jpeg";
                    uDocComnd.reviewStatus = "Accepted";

                    Bitmap bitMap1 = PictureUtility.drawableToBitmap(userFingerprintsList.get(1));
                    String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                    uDocComnd.imageData = imgData1;
                    userDocCommandList.add(uDocComnd);
                    MyToast.makeMyToast(activity,"1st",Toast.LENGTH_SHORT);
                } else {
                    if(userFingerprintsList.size()!=0) {
                        UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                        uDocCommand2.docFiles = filePrefix + "_thumb_fingerPrint_0;";
                        uDocCommand2.docType = "fingerprints";
                        uDocCommand2.docFormat = "jpeg";
                        uDocCommand2.reviewStatus = "Accepted";

                        Bitmap bitMap = PictureUtility.drawableToBitmap(userFingerprintsList.get(0));
                        String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                        uDocCommand2.imageData = imgData;
                        userDocCommandList.add(uDocCommand2);
                        MyToast.makeMyToast(activity,"2nd",Toast.LENGTH_SHORT);
                    }
                }

            }

        }*/

        if(isFingerPrint){
            if(RegistrationData.getUserThumbImageDrawable() != null){
                UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                uDocCommand2.docFiles = filePrefix + "_thumb_fingerPrint_0;";
                uDocCommand2.docType = "fingerprints";
                uDocCommand2.docFormat = "jpeg";
                uDocCommand2.reviewStatus = "Accepted";

                Bitmap bitMap = PictureUtility.drawableToBitmap(RegistrationData.getUserThumbImageDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand2.imageData = imgData;
                userDocCommandList.add(uDocCommand2);
            }

            if(RegistrationData.getUserIndexImageDrawable() != null){
                UserRegistration.UserDocCommand uDocComnd = new UserRegistration.UserDocCommand();
                uDocComnd.docFiles = filePrefix + "_index_fingerPrint_1;";
                uDocComnd.docType = "fingerprints";
                uDocComnd.docFormat = "jpeg";
                uDocComnd.reviewStatus = "Accepted";

                Bitmap bitMap1 = PictureUtility.drawableToBitmap(RegistrationData.getUserIndexImageDrawable());
                String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                uDocComnd.imageData = imgData1;
                userDocCommandList.add(uDocComnd);
            }
        }

        if (RegistrationData.getIsUgandan())
        if(RegistrationData.getCapturedFingerprintDrawable() != null){

            UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
            uDocCommand2.docFiles = filePrefix + "_nin_verified_fingerPrint_0;";
            uDocCommand2.docType = "fingerprints";
            uDocCommand2.docFormat = "jpeg";
            uDocCommand2.reviewStatus = "Accepted";

            Bitmap bitMap = PictureUtility.drawableToBitmap(RegistrationData.getCapturedFingerprintDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

            uDocCommand2.imageData = imgData;
            userDocCommandList.add(uDocCommand2);
        }

        if(RegistrationData.getIsRefugee())
        if(RegistrationData.getRefugeeThumbImageDrawable() != null){

            UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
            uDocCommand2.docFiles = filePrefix + "_refugee_verified_fingerPrint_0;";
            uDocCommand2.docType = "fingerprints";
            uDocCommand2.docFormat = "jpeg";
            uDocCommand2.reviewStatus = "Accepted";

            Bitmap bitMap = PictureUtility.drawableToBitmap(RegistrationData.getRefugeeThumbImageDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

            uDocCommand2.imageData = imgData;
            userDocCommandList.add(uDocCommand2);
        }

        userRegistration.userDocs = userDocCommandList;
        RegistrationData.setPersonalRegistrationUserDocs(userDocCommandList);
        return userRegistration;

    }


    private UserRegistration CollectDummyData(UserRegistration user) {
        if (associateAccount.isChecked()) {
            user.accountId = spinner.getSelectedItem().toString();
        }

        user.resellerCode = UserSession.getResellerId(activity);
        user.currency = "UGX";
        if (userRegistration.registrationType.equals("personal")|| userRegistration.registrationType.equals("company")) {
            user.userGroup = "Consumer";
        }else if (userRegistration.registrationType.equals("retailer")){
            user.userGroup = "Reseller Retailer";
        }
        return user;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 198) {

            if (RegistrationData.getUserThumbImageDrawable() != null) {
                thumbFingerImage.setVisibility(View.VISIBLE);
                thumbFingerImage.setImageDrawable(RegistrationData.getUserThumbImageDrawable());
                userFingerprintsList.add(RegistrationData.getUserThumbImageDrawable());
                captureIndexFingerprint.setEnabled(true);
            }
        }

        if (requestCode == 199) {
            if (RegistrationData.getUserIndexImageDrawable() != null) {
                indexFingerImage.setVisibility(View.VISIBLE);
                indexFingerImage.setImageDrawable(RegistrationData.getUserIndexImageDrawable());
                userFingerprintsList.add(RegistrationData.getUserIndexImageDrawable());
            }
        }

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
                docData1.displayName = companyDocs[0].displayName.toString();
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
                PdfDocumentData docData = new PdfDocumentData();
                docData.displayName = companyDocs[1].displayName.toString();
                docData.docType = checkDisplayName(companyDocs[1].displayName.toString());
                docData.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {

                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData.pdfRwaData = encodeData;
                        uploadedDocArray[1] = docData;
                        pdfDocumentDataList.add(docData);

                        notification1.setText("A file is selected :" + docData.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){

                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){

                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }
                }
            }
        }

        if (requestCode == 73 && resultCode == RESULT_OK) {
            //New
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
                docData1.displayName = companyDocs[2].displayName.toString();
                docData1.docType = checkDisplayName(companyDocs[2].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);

                if (encodeData != null) {
                    docData1.pdfRwaData = encodeData;
                    uploadedDocArray[2] = docData1;
                    pdfDocumentDataList.add(docData1);
                    notification2.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                }
            }
        }

        if (requestCode == 74 && resultCode == RESULT_OK) {
            // NEW
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
                docData1.displayName = companyDocs[3].displayName.toString();
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
                }
            }
        }

        if (requestCode == 75 && resultCode == RESULT_OK) {
            //NEW
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
                docData1.displayName = companyDocs[4].displayName.toString();
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
                }
            }
        }

        if (requestCode == 76 && resultCode == RESULT_OK) {
            //NEW
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
                docData1.displayName = companyDocs[5].displayName.toString();
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
                        MyToast.makeMyToast(activity,"File Not Found, Please Re-upload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }
                }
            }
        }

        if (requestCode == 77 && resultCode == RESULT_OK) {
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
                docData1.displayName = companyDocs[6].displayName.toString();
                docData1.docType = checkDisplayName(companyDocs[6].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData!= null) {

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
                }
            }
        }


       /* if (requestCode == 78 && resultCode == RESULT_OK) {
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
                docData1.displayName = companyDocs[6].displayName.toString();
                docData1.docType = checkDisplayName(companyDocs[6].displayName.toString());
                docData1.imageData = pdfUri;
                String encodeData = FilePath.getEncodeData(activity,pdfUri);
                if (encodeData != null) {

                    if(!encodeData.equals("File size is too Large") && !encodeData.equals("File Not Found")){
                        docData1.pdfRwaData = encodeData;
                        uploadedDocArray[6] = docData1;
                        pdfDocumentDataList.add(docData1);

                        notification7.setText("A file is selected :" + docData1.displayName.toString().replace(" ","_")+".pdf");
                    }else if (encodeData.equals("File Not Found")){
                        MyToast.makeMyToast(activity,"File Not Found, Please reUpload.", Toast.LENGTH_SHORT);
                    }else if (encodeData.equals("File size is too Large")){
                        MyToast.makeMyToast(activity,"The uploaded file size should be less than 10 MB.", Toast.LENGTH_SHORT);
                    }
                }
            }
        }*/

        if (requestCode == 100) {
            fingerPrintImage.setVisibility(View.VISIBLE);
            fingerPrintImage.setImageDrawable(RegistrationData.getImageDrawable());
        }

        if (resultCode == RESULT_OK) {
            if (!isPdfFile) {
                if (isPassportImage) {
                    View passportPictureContainer = findViewById(R.id.passport_images_cont);
                    PictureUtility.processPassportPictureRequestWithEdit(activity, passportPictureContainer, requestCode, data, passportImages, true);
                    isPassportImage = false;
                } else if (isNinImage) {
                    View ninContainer = findViewById(R.id.product_images_cont);
                    PictureUtility.processNinPictureRequestWithEdit(activity, ninContainer, requestCode, data, idImages, true);
                    isNinImage = false;
                } else if (isProfileImage) {
                    View profilePictureContainer = findViewById(R.id.profile_images_cont);
                    PictureUtility.processProfilePictureRequestWithEdit(activity, profilePictureContainer, requestCode, data, profileImages, true);
                    isProfileImage = false;
                }else if (isRefugeeImage) {
                    View refugeePictureContainer = findViewById(R.id.refugee_images_cont);
                    PictureUtility.processRefugeePictureRequestWithEdit(activity, refugeePictureContainer, requestCode, data, refugeeImages, true);
                    isRefugeeImage = false;
                }else if (isActivationFormImage) {
                    View activationFormContainer = findViewById(R.id.activation_form_images_cont);
                    PictureUtility.processActivationFormPictureRequestWithEdit(activity, activationFormContainer, requestCode, data, activationFormImages, true);
                    isActivationFormImage = false;
                }else if (isVisaImage) {
                    View visaPictureContainer = findViewById(R.id.visa_images_cont);
                    PictureUtility.processVisaPictureRequestWithEdit(activity, visaPictureContainer, requestCode, data,visaImages, true);
                    isVisaImage = false;
                }

            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Crop.REQUEST_CROP)
                PictureUtility.editImageDone();
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


    private String buildProductPictureFilePrefix(UserRegistration userRegInfo) {
        String[] prodNameToks = userRegInfo.userName.split(" ");
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


}
