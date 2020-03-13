package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import com.aryagami.data.CacheNewOrderData;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DocumentTypes;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.PdfOpenHelper;
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
import java.util.List;

import static com.aryagami.util.PictureUtility.selectImage;

public class EditMandatoryDocsActivity extends AppCompatActivity {

    public String filepath;
    public String encodeData;
    Button upload_nin_file, upload_profile_File, upload_passport_file;
    UserRegistration userRegistration, data;
    ProgressDialog progressDialog, progressDialog1, uploadProgressDialog;
    int imageUploadSuccessCount = 0;
    int uploadedCommands = 0;
    int passportUploadSuccessCount = 0;
    String filePrefix = "";
    Long randomNumber;
    Boolean isProfileImage = false;
    Boolean isPassportImage = false;
    Boolean isNinImage = false;
    Boolean isVisaImage = false;
    Boolean isActivationFormImage = false;
    Boolean isRefugeeImage = false;
    Activity activity = this;
    Spinner spinner;
    int mandatoryDocCount = 0;
    int actualDocCount = 0;
    int pdfDocUploadedCount = 0;
    String filename1 = "";
    String filename = "";
    TextView notification, notification1, notification2, notification3, notification4, notification5, notification6, notification7;
    Button certifiedcompany, Certifiedformfile, copiesdirectories, auditedfinancial, URAcertified, previousbusiness, bankstatement;
    Button cmaButton, captureRefugee;
    Uri pdfUri;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;
    LinearLayout defaultAccountContainer, associateAccountContainer, accountsetupPersonal, accountsetupCompany;
    RadioButton defaultAccount, associateAccount;
    List<View> idImages = new ArrayList<View>();
    List<View> passportImages = new ArrayList<View>();
    List<View> profileImages = new ArrayList<View>();
    List<View> refugeeImages = new ArrayList<View>();
    List<View> activationFormImages = new ArrayList<View>();
    List<View> visaImages = new ArrayList<View>();
    ImageView fingerPrintImage;
    DocumentTypes[] pdfUpload, companyDocs, personalDocs;
    List<DocumentTypes> companyDocList = new ArrayList<DocumentTypes>();
    List<DocumentTypes> personalDocList = new ArrayList<DocumentTypes>();
    Bitmap bitmap;
    List<PdfDocumentData> pdfDocumentList;
    List<PdfDocumentData> pdfDocumentDataList;
    PdfDocumentData[] uploadedDocArray;
    Button captureThumbFingerprint, captureIndexFingerprint, captureActicationFormBtn, captureVisaBtn;
    ImageView thumbFingerImage, indexFingerImage;
    List<Drawable> userFingerprintsList = new ArrayList<Drawable>();
    int fingerprintUploadSuccessCount = 0;
    String fingerprintDocsJson = "";
    String allDocsJson = "";
    List<String> tempVisaUrl = new ArrayList<String>();
    List<String> tempPassportUrl = new ArrayList<String>();
    List<String> tempActivationUrl = new ArrayList<String>();
    List<String> tempRefugeeUrl = new ArrayList<String>();
    List<String> tempNinUrl = new ArrayList<String>();
    List<String> tempProfileUrl = new ArrayList<String>();
    List<UserRegistration.UserDocCommand> tempRejectedVisa = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedPassport = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedNin = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedRefugee = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedProfile = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedActivation = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedThumbFingerprints = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedIndexFingerprints = new ArrayList<UserRegistration.UserDocCommand>();
    List<UserRegistration.UserDocCommand> tempRejectedCompanyDocs = new ArrayList<UserRegistration.UserDocCommand>();

    UserRegistration.UserDocCommand[] rejectedVisaDocs, rejectedPassportDocs, rejectedNinDocs, rejectedRefugeeDocs, rejectedProfileDocs, rejectedActivationDocs, rejectedThumbFingerprint, rejectedIndexFingerprint;
    LinearLayout thumbnailsLayoutNin, thumbnailsLayoutPassport, thumbnailsLayoutRefugee, thumbnailsLayoutActivationForm, thumbnailsLayoutVisa, thumbnailsLayoutProfile;
    boolean isThumbPrint = false;
    boolean isIndexPrint = false;
    private boolean isFingerPrint = false;
    private boolean isPdfFile = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mandatory_documents);
        Button updateUser = (Button) findViewById(R.id.save_and_continue_btn);
        updateUser.setText("Update");
        data = RegistrationData.getEditUserProfile();
        userRegistration = RegistrationData.getUpdateUserProfileData();

        certifiedcompany = (Button) findViewById(R.id.certified_company);
        Certifiedformfile = (Button) findViewById(R.id.Certified_form_file);
        copiesdirectories = (Button) findViewById(R.id.copies_directories);
        //copiesdirectories.setVisibility(View.GONE);
        auditedfinancial = (Button) findViewById(R.id.audited_financial);
        URAcertified = (Button) findViewById(R.id.URA_certified);
        previousbusiness = (Button) findViewById(R.id.previous_business);
        bankstatement = (Button) findViewById(R.id.bank_statement);
        cmaButton = (Button) findViewById(R.id.cma_doc);

        captureThumbFingerprint = (Button) findViewById(R.id.capture_thumb_print);
        captureIndexFingerprint = (Button) findViewById(R.id.capture_index_finger_print);

        captureThumbFingerprint.setEnabled(true);
        captureIndexFingerprint.setEnabled(false);

        thumbFingerImage = (ImageView) findViewById(R.id.thumb_image);
        indexFingerImage = (ImageView) findViewById(R.id.index_finger_image);

        thumbnailsLayoutNin = (LinearLayout) findViewById(R.id.nin_picture_thumbnails);
        thumbnailsLayoutPassport = (LinearLayout) findViewById(R.id.passport_picture_thumbnails);
        thumbnailsLayoutRefugee = (LinearLayout) findViewById(R.id.refugee_picture_thumbnails);
        thumbnailsLayoutActivationForm = (LinearLayout) findViewById(R.id.activation_form_picture_thumbnails);
        thumbnailsLayoutVisa = (LinearLayout) findViewById(R.id.visa_picture_thumbnails);
        thumbnailsLayoutProfile = (LinearLayout) findViewById(R.id.profile_picture_thumbnails);

        thumbnailsLayoutNin.removeAllViews();
        thumbnailsLayoutPassport.removeAllViews();
        thumbnailsLayoutRefugee.removeAllViews();
        thumbnailsLayoutActivationForm.removeAllViews();
        thumbnailsLayoutVisa.removeAllViews();
        thumbnailsLayoutProfile.removeAllViews();

        notification = findViewById(R.id.notification);
        notification1 = findViewById(R.id.notification1);
        notification2 = findViewById(R.id.notification2);
        notification3 = findViewById(R.id.notification3);
        notification4 = findViewById(R.id.notification4);
        notification5 = findViewById(R.id.notification5);
        notification6 = findViewById(R.id.notification6);
        notification7 = findViewById(R.id.notification7);

        btn1 = (Button) findViewById(R.id.cc_btn1);
        btn2 = (Button) findViewById(R.id.cc_btn2);
        btn3 = (Button) findViewById(R.id.cc_btn3);
        btn4 = (Button) findViewById(R.id.cc_btn4);
        btn5 = (Button) findViewById(R.id.cc_btn5);
        btn6 = (Button) findViewById(R.id.cc_btn6);
        btn7 = (Button) findViewById(R.id.cc_btn7);
        btn8 = (Button) findViewById(R.id.cc_btn8);

        upload_profile_File = (Button) findViewById(R.id.upload_profile_file);
        upload_passport_file = (Button) findViewById(R.id.capture_passport);
        upload_nin_file = (Button) findViewById(R.id.upload_nin_file);
        captureRefugee = (Button) findViewById(R.id.capture_refugee);
        captureVisaBtn = (Button) findViewById(R.id.capture_visa);
        captureActicationFormBtn = (Button) findViewById(R.id.capture_activation_form);

        upload_profile_File.setEnabled(true);
        upload_passport_file.setEnabled(true);
        upload_nin_file.setEnabled(true);
        captureRefugee.setEnabled(true);
        captureVisaBtn.setEnabled(true);
        captureActicationFormBtn.setEnabled(true);

        if (DocumentTypes.getCompanyDocArray() != null) {
            if (DocumentTypes.getCompanyDocArray().length != 0) {
                companyDocs = new DocumentTypes[DocumentTypes.getCompanyDocArray().length];
                uploadedDocArray = new PdfDocumentData[DocumentTypes.getCompanyDocArray().length];
                companyDocs = DocumentTypes.getCompanyDocArray();
                certifiedcompany.setText(companyDocs[0].displayName);
                Certifiedformfile.setText(companyDocs[1].displayName);
                copiesdirectories.setText(companyDocs[2].displayName);
                auditedfinancial.setText(companyDocs[3].displayName);
                URAcertified.setText(companyDocs[4].displayName);
                previousbusiness.setText(companyDocs[5].displayName);
                bankstatement.setText(companyDocs[6].displayName);

                for (DocumentTypes types : DocumentTypes.getCompanyDocArray()) {
                    if (types.isMandatory) {
                        mandatoryDocCount++;
                    }
                }
            }
        } else {
            getDocumentTypes();
        }
        accountsetupPersonal = (LinearLayout) findViewById(R.id.personal_mandatory);
        accountsetupCompany = (LinearLayout) findViewById(R.id.company_mandatory);
        LinearLayout passportDocLayout = (LinearLayout) findViewById(R.id.passport_document_layout);
        final LinearLayout refugeeDocLayout = (LinearLayout) findViewById(R.id.refugee_document_layout);
        LinearLayout ninDocLayout = (LinearLayout) findViewById(R.id.nin_document_layout);
        LinearLayout activationDocLayout = (LinearLayout) findViewById(R.id.activation_form_layout);
        LinearLayout visaDocLayout = (LinearLayout) findViewById(R.id.visa_document_layout);
        final LinearLayout profileDocLayout = (LinearLayout) findViewById(R.id.upload_profile_layout);

        if (userRegistration.registrationType.equals("personal")) {
            accountsetupPersonal.setVisibility(View.VISIBLE);
            accountsetupCompany.setVisibility(View.GONE);
            if (userRegistration.userDocs != null && userRegistration.userDocs.size() != 0) {
                List<UserRegistration.UserDocCommand> command = new ArrayList<UserRegistration.UserDocCommand>();
                if (userRegistration.userDocs.size() != 0)
                    command = userRegistration.userDocs;

                for (UserRegistration.UserDocCommand uDoc : command) {
                    if (uDoc.docType.equals("profile")) {
                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempProfileUrl.add(Constants.profileImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempProfileUrl.add(Constants.profileImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        upload_profile_File.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            upload_profile_File.setEnabled(true);
                            upload_profile_File.setText("Reupload Profile");
                            tempRejectedProfile.add(uDoc);
                        }
                    } else if (uDoc.docType.equals("nin")) {
                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempNinUrl.add(Constants.ninImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempNinUrl.add(Constants.ninImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        upload_nin_file.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            upload_nin_file.setEnabled(true);
                            upload_nin_file.setText("Reupload Nin Doc");
                            tempRejectedNin.add(uDoc);
                        }

                    } else if (uDoc.docType.equals("passport")) {

                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempPassportUrl.add(Constants.passportImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempPassportUrl.add(Constants.passportImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        upload_passport_file.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            upload_passport_file.setEnabled(true);
                            upload_passport_file.setText("Reupload Passport");
                            tempRejectedPassport.add(uDoc);
                        }

                    } else if (uDoc.docType.equals("refugee")) {

                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempRefugeeUrl.add(Constants.refugeeImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempRefugeeUrl.add(Constants.refugeeImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        captureRefugee.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            captureRefugee.setEnabled(true);
                            captureRefugee.setText("Reupload Refugee ID");
                            tempRejectedRefugee.add(uDoc);
                        }

                    } else if (uDoc.docType.equals("activation")) {

                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempActivationUrl.add(Constants.activationImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempActivationUrl.add(Constants.activationImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        captureActicationFormBtn.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            captureActicationFormBtn.setEnabled(true);
                            captureActicationFormBtn.setText("Reupload Activation");
                            tempRejectedActivation.add(uDoc);
                        }

                    } else if (uDoc.docType.equals("visa")) {

                        String ninDoc[] = uDoc.docFiles.toString().split(";");

                        if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                            tempVisaUrl.add(Constants.visaImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                        } else {
                            tempVisaUrl.add(Constants.visaImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                        }
                        captureVisaBtn.setEnabled(false);
                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            captureVisaBtn.setEnabled(true);
                            captureVisaBtn.setText("Reupload Visa Doc");
                            tempRejectedVisa.add(uDoc);
                        }

                    } else if (uDoc.docType.equals("fingerprints")) {

                        if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                            if (uDoc.docFiles.contains("thumb")) {
                                tempRejectedThumbFingerprints.add(uDoc);
                                captureThumbFingerprint.setText("Reupload Thumb Fingerprint");
                                captureThumbFingerprint.setEnabled(true);
                            } else if (uDoc.docFiles.contains("index")) {
                                tempRejectedIndexFingerprints.add(uDoc);
                                captureIndexFingerprint.setText("Reupload Index Fingerprint");
                                captureIndexFingerprint.setEnabled(true);
                            }
                        }
                    }
                }

                if (userRegistration.nationalIdentity != null) {
                    if (userRegistration.nationalIdentity.equals("Ugandan NationalID")) {

                        ninDocLayout.setVisibility(View.VISIBLE);
                        profileDocLayout.setVisibility(View.VISIBLE);
                        passportDocLayout.setVisibility(View.GONE);
                        refugeeDocLayout.setVisibility(View.GONE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.GONE);
                        setUgadanDocuments();
                        setProfileDocuments();
                        setActivationFormDocument();

                    } else if (userRegistration.nationalIdentity.equals("Passport No")) {
                        ninDocLayout.setVisibility(View.GONE);
                        profileDocLayout.setVisibility(View.VISIBLE);
                        passportDocLayout.setVisibility(View.VISIBLE);
                        refugeeDocLayout.setVisibility(View.GONE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.VISIBLE);

                        setProfileDocuments();
                        setActivationFormDocument();
                        setPassportDocument();
                        setVisaDocument();

                    } else if (userRegistration.nationalIdentity.equals("Refugee")) {
                        profileDocLayout.setVisibility(View.VISIBLE);
                        ninDocLayout.setVisibility(View.GONE);
                        passportDocLayout.setVisibility(View.GONE);
                        refugeeDocLayout.setVisibility(View.VISIBLE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.GONE);

                        setProfileDocuments();
                        setActivationFormDocument();
                        setRefugeeDocument();
                    }
                }
            } else {

                upload_profile_File.setEnabled(true);
                upload_passport_file.setEnabled(true);
                upload_nin_file.setEnabled(true);
                captureRefugee.setEnabled(true);
                captureVisaBtn.setEnabled(true);
                captureActicationFormBtn.setEnabled(true);

                if (userRegistration.nationalIdentity != null) {
                    if (userRegistration.nationalIdentity.equals("Ugandan NationalID")) {
                        ninDocLayout.setVisibility(View.VISIBLE);
                        profileDocLayout.setVisibility(View.VISIBLE);
                        passportDocLayout.setVisibility(View.GONE);
                        refugeeDocLayout.setVisibility(View.GONE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.GONE);

                    } else if (userRegistration.nationalIdentity.equals("Passport No")) {
                        ninDocLayout.setVisibility(View.GONE);
                        profileDocLayout.setVisibility(View.VISIBLE);
                        passportDocLayout.setVisibility(View.VISIBLE);
                        refugeeDocLayout.setVisibility(View.GONE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.VISIBLE);

                    } else if (userRegistration.nationalIdentity.equals("Refugee")) {
                        profileDocLayout.setVisibility(View.VISIBLE);
                        ninDocLayout.setVisibility(View.GONE);
                        passportDocLayout.setVisibility(View.GONE);
                        refugeeDocLayout.setVisibility(View.VISIBLE);
                        activationDocLayout.setVisibility(View.VISIBLE);
                        visaDocLayout.setVisibility(View.GONE);
                    }
                }
            }

        } else if (userRegistration.registrationType.equals("company")) {
            accountsetupPersonal.setVisibility(View.GONE);
            accountsetupCompany.setVisibility(View.VISIBLE);

            certifiedcompany.setEnabled(false);
            Certifiedformfile.setEnabled(false);
            copiesdirectories.setEnabled(false);;
            auditedfinancial.setEnabled(false);
            URAcertified.setEnabled(false);
            previousbusiness.setEnabled(false);
            bankstatement.setEnabled(false);

            if (userRegistration.userDocs != null && userRegistration.userDocs.size() != 0) {
                List<UserRegistration.UserDocCommand> command = new ArrayList<UserRegistration.UserDocCommand>();
                if (userRegistration.userDocs.size() != 0)
                    command = userRegistration.userDocs;

                for (UserRegistration.UserDocCommand uDoc : command) {
                    String docs[] = uDoc.docFiles.toString().split(";");
                    String url = "";
                    if (docs[0].contains(".pdf")) {
                        url = Constants.imagesUrl + uDoc.docType + "/" + userRegistration.userId.toString() + "/" + docs[0];

                    } else {
                        url = Constants.imagesUrl + uDoc.docType + "/" + userRegistration.userId.toString() + "/" + docs[0] + ".pdf";
                    }

                    switch (uDoc.docType) {

                        case Constants.case1:

                            notification.setText(docs[0].toString());
                            final String finalUrl = url;
                            final String name = docs[0].toString();
                            btn1.setEnabled(true);
                            btn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl, activity);
                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                certifiedcompany.setEnabled(true);
                                String text = certifiedcompany.getText().toString();
                                certifiedcompany.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }

                            break;

                        case Constants.case2:
                            notification1.setText(docs[0].toString());
                            btn2.setEnabled(true);
                            final String finalUrl1 = url;
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl1, activity);
                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                Certifiedformfile.setEnabled(true);
                                String text = Certifiedformfile.getText().toString();
                                Certifiedformfile.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }
                            break;

                        case Constants.case3:
                            notification2.setText(docs[0].toString());
                            btn3.setEnabled(true);
                            final String finalUrl2 = url;
                            btn3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl2, activity);
                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                copiesdirectories.setEnabled(true);
                                String text = copiesdirectories.getText().toString();
                                copiesdirectories.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }
                            break;

                        case Constants.case4:

                            notification3.setText(docs[0].toString());
                            final String finalUrl3 = url;
                            btn4.setEnabled(true);
                            btn4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl3, activity);

                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                auditedfinancial.setEnabled(true);
                                String text = auditedfinancial.getText().toString();
                                auditedfinancial.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }
                            break;



                        case Constants.case5:

                            notification4.setText(docs[0].toString());
                            final String finalUrl4 = url;
                            btn5.setEnabled(true);
                            btn5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl4, activity);
                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                URAcertified.setEnabled(true);
                                String text = URAcertified.getText().toString();
                                URAcertified.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }
                            break;

                        case Constants.case6:

                            notification5.setText(docs[0].toString());
                            final String finalUrl5 = url;
                            btn6.setEnabled(true);
                            btn6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl5, activity);
                                }
                            });

                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                previousbusiness.setEnabled(true);
                                String text = previousbusiness.getText().toString();
                                previousbusiness.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }
                            break;

                        case Constants.case7:
                            notification6.setText(docs[0].toString());
                            final String finalUrl6 = url;
                            btn7.setEnabled(true);
                            btn7.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl6, activity);
                                }
                            });
                            if (uDoc.reviewStatus != null && uDoc.reviewStatus.equals("Rejected")) {
                                bankstatement.setEnabled(true);
                                String text = bankstatement.getText().toString();
                                bankstatement.setText("Reupload: " + text);
                                tempRejectedCompanyDocs.add(uDoc);
                            }

                            break;

                        /*case "TIN":
                            notification7.setText(docs[0].toString());

                            final String finalUrl7 = url;
                            btn8.setEnabled(true);
                            btn8.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PdfOpenHelper.openPdfFromUrl(finalUrl7, activity);
                                }
                            });
                            if (uDoc.reviewStatus.equals("Rejected")) {
                                cmaButton.setEnabled(true);
                                String text = cmaButton.getText().toString();
                                cmaButton.setText("Reupload: " + text);
                            }
                            break;*/
                        case "activation":

                            String ninDoc[] = uDoc.docFiles.toString().split(";");

                            if (ninDoc[0].toString().contains(".jpeg") | ninDoc[0].toString().contains("jpg")) {
                                tempActivationUrl.add(Constants.activationImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0]);
                            } else {
                                tempActivationUrl.add(Constants.activationImagesUrl + userRegistration.userId.toString() + "/" + ninDoc[0] + ".jpeg");
                            }
                            break;


                    }
                }
                setActivationFormDocument();

            } else {
                certifiedcompany.setEnabled(true);
                Certifiedformfile.setEnabled(true);
                copiesdirectories.setEnabled(true);;
                auditedfinancial.setEnabled(true);
                URAcertified.setEnabled(true);
                previousbusiness.setEnabled(true);
                bankstatement.setEnabled(true);
            }
        }

        captureThumbFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isFingerPrint = true;
                    isThumbPrint = true;
                    RegistrationData.setIsFingerPrint(true);
                    Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "userThumb");
                    startActivityForResult(intent, 198);
                } catch (Exception e) {
                } catch (Error error) {
                }
            }
        });

        captureIndexFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isFingerPrint = true;
                    isIndexPrint = true;
                    RegistrationData.setIsFingerPrint(true);
                    Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "userIndex");
                    startActivityForResult(intent, 199);
                } catch (Exception e) {
                } catch (Error error) {
                }
            }
        });

        fingerPrintImage = (ImageView) findViewById(R.id.finger_image);
        Button saveForLater = (Button) findViewById(R.id.submitfinal_btn);
        saveForLater.setVisibility(View.VISIBLE);
        Button cancel = (Button) findViewById(R.id.cancelfinal_btn);
        saveForLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInCache();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, EditBillingAddressInfoActivity.class);
                startActivity(intent);
            }
        });


        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRegistration.registrationType.equals("company")) {
                    collectPdfDocs();
                } else if (userRegistration.registrationType.equals("personal")) {
                    collectUserDocs();
                }
                uploadDocumentCommand(userRegistration.userDocs);


            }
        });

       /* // update Mandatory Documents
        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRegistration.registrationType.equals("company")) {
                    collectPdfDocs();
                } else if (userRegistration.registrationType.equals("personal")) {
                    collectUserDocs();
                }

                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                    serviceHandler.postUpdateUser(userRegistration, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin orderDetails = (UserLogin) data.get(0);
                            if (orderDetails.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (userRegistration.registrationType.equals("company")) {
                                    pdfDocumentList = Arrays.asList(uploadedDocArray);
                                    for (PdfDocumentData docData : pdfDocumentList) {
                                        if (docData != null) {
                                            actualDocCount++;
                                        }
                                    }

                                    if (pdfDocumentList != null) {
                                        for (PdfDocumentData docData : pdfDocumentList) {
                                            if (docData != null) {
                                                String fileUrl = "documents/" + docData.docType + "/" + orderDetails.userId + "/," + (docData.displayName.toString()).replace(" ", "_") + ("_" + randomNumber);
                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("pdf", fileUrl, docData.pdfRwaData.toString(), new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin login1 = (UserLogin) data.get(0);
                                                        if (login1.status.equals("success")) {
                                                            if (++pdfDocUploadedCount == actualDocCount) {
                                                                PdfDocumentData.setFinalDocsList(null);
                                                                MyToast.makeMyToast(activity, "Company Documents Uploaded Successfully.", Toast.LENGTH_LONG);
                                                            }
                                                        } else {
                                                            MyToast.makeMyToast(activity, "Company Documents not Uploaded.", Toast.LENGTH_LONG);
                                                        }

                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                        //  BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");

                                                        //activity.finish();
                                                        // startNavigationActivity();
                                                    }
                                                });
                                            }

                                        }
                                    } else {
                                        Toast.makeText(activity, "DocumentList is Empty.", Toast.LENGTH_SHORT).show();
                                    }


                                    if (activationFormImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/activation/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (activationFormImages.size() != 0)
                                            for (View view : activationFormImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_activation_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "activation_" + (randomNumber + passportDocsNum++);
                                                }
                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++passportUploadSuccessCount == activationFormImages.size()) {
                                                                activationFormImages.clear();
                                                                MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                    }
                                                });
                                            }
                                    }

                                } else if (userRegistration.registrationType.equals("personal")) {

                                    //Upload Nin Document
                                    if (idImages.size() != 0) {

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

                                                if (filePrefix != null) {
                                                    filename1 = filePrefix + "_nin_" + (randomNumber + imageNum++);
                                                } else {
                                                    filename1 = "nin_" + (randomNumber + imageNum++);
                                                }
                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", prodPicDir + "," + filename1, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++imageUploadSuccessCount == idImages.size()) {
                                                                idImages.clear();
                                                                MyToast.makeMyToast(activity, "Documents uploaded.", Toast.LENGTH_LONG);
                                                            }

                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                    }
                                                });
                                            }

                                    }

                                    //Upload Profile
                                    if (profileImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/profile/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (profileImages.size() != 0)
                                            for (View view : profileImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_profile_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "profile_" + (passportDocsNum++);
                                                }

                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++passportUploadSuccessCount == profileImages.size()) {
                                                                profileImages.clear();
                                                                MyToast.makeMyToast(activity, "Documents uploaded.", Toast.LENGTH_LONG);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                    }
                                                });
                                            }
                                    }

                                    //Upload Passport Images
                                    if (passportImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/passport/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (passportImages.size() != 0)
                                            for (View view : passportImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_passport_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "passport_" + (passportDocsNum++);
                                                }

                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {
                                                            if (++passportUploadSuccessCount == passportImages.size()) {
                                                                passportImages.clear();
                                                                MyToast.makeMyToast(activity, "Passport Documents uploaded.", Toast.LENGTH_LONG);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                    }
                                                });
                                            }
                                    }
                                    // upload Refugee Images

                                    if (refugeeImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/refugee/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (refugeeImages.size() != 0)
                                            for (View view : refugeeImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_refugee_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "refugee_" + (passportDocsNum++);
                                                }

                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++passportUploadSuccessCount == refugeeImages.size()) {
                                                                refugeeImages.clear();
                                                                MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                    }
                                                });
                                            }
                                    }

                                    // upload Visa Documents
                                    if (visaImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/visa/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (visaImages.size() != 0)
                                            for (View view : visaImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_visa_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "visa_" + (passportDocsNum++);
                                                }

                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++passportUploadSuccessCount == visaImages.size()) {
                                                                visaImages.clear();
                                                                MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                        // MyToast.makeMyToast(activity, getResources().getString(R.string.Product_uploading_Failed) + ", " + status, Toast.LENGTH_LONG);

                                                    }
                                                });
                                            }
                                    }

                                    // Activation Form Images upload
                                    if (activationFormImages.size() != 0) {

                                        String passportPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!passportPicDir.isEmpty())
                                                passportPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            passportPicDir += "documents/activation/" + user.userId + "/";
                                        }
                                        int passportDocsNum = 0;
                                        passportUploadSuccessCount = 0;
                                        if (activationFormImages.size() != 0)
                                            for (View view : activationFormImages) {
                                                ImageView imageView = (ImageView) view;
                                                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                                                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                                                if (filePrefix != null) {
                                                    filename = filePrefix + "_activation_" + (randomNumber + passportDocsNum++);
                                                } else {
                                                    filename = "activation_" + (passportDocsNum++);
                                                }

                                                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();
                                                uploadImageServiceHandler.uploadPdf("jpeg", passportPicDir + "," + filename, imgData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userLogin = (UserLogin) data.get(0);
                                                        if (userLogin.status.equals("success")) {

                                                            if (++passportUploadSuccessCount == activationFormImages.size()) {
                                                                activationFormImages.clear();
                                                                MyToast.makeMyToast(activity, "Images uploaded.", Toast.LENGTH_LONG);
                                                            }

                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {

                                                    }
                                                });
                                            }
                                    }

                                    if (RegistrationData.getIsFingerPrint()) {

                                        int imageCount = 0;
                                        String fileName = "";

                                        String fingerPicDir = "";
                                        for (DataModel prodData : data) {
                                            if (!fingerPicDir.isEmpty())
                                                fingerPicDir += ":";
                                            UserLogin user = (UserLogin) prodData;
                                            fingerPicDir += "fingerprints/" + user.userId + "/";
                                        }
                                        if (userFingerprintsList != null)
                                            for (Drawable dbImage : userFingerprintsList) {

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
                                                        fileName = filePrefix + "_thumb_fingerPrint_" + imageCount;
                                                    } else {
                                                        fileName = filePrefix + "_index_fingerPrint_" + imageCount;
                                                    }
                                                }

                                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                                serviceHandler.uploadPdf("jpeg", fingerPicDir + "," + fileName, imageData, new RestServiceHandler.Callback() {
                                                    @Override
                                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                                        UserLogin userInfo = (UserLogin) data.get(0);
                                                        if (userInfo.status.equals("success")) {
                                                            if (++fingerprintUploadSuccessCount == userFingerprintsList.size()) {
                                                                Toast.makeText(activity, getResources().getString(R.string.Successfully_uploaded_fingerprint), Toast.LENGTH_SHORT).show();
                                                                RegistrationData.setUserThumbImageDrawable(null);
                                                                RegistrationData.setUserIndexImageDrawable(null);
                                                                RegistrationData.setUserFingerprintsList(null);
                                                                System.gc();
                                                            }

                                                        } else {
                                                            Toast.makeText(activity, "Status" + userInfo.status, Toast.LENGTH_SHORT).show();
                                                            startNavigationActivity();
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RestServiceHandler.ErrorCode error, String status) {

                                                    }
                                                });
                                                imageCount++;
                                            }
                                    }
                                }
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("User Updated Successfully.");
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                startNavigationActivity();
                                            }
                                        });
                                alertDialog.show();
                            } else {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                                if (orderDetails.status.equals("INVALID_SESSION")) {
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                } else {
                                    Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                                    startNavigationActivity();
                                }
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity,Constants.emailId,"Error:"+error+"\n Status:"+status,"Update User");
                            saveInCache();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        certifiedcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String documentName = certifiedcompany.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 71, documentName);
            }
        });

        Certifiedformfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = Certifiedformfile.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 72, documentName);
            }
        });

        copiesdirectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = copiesdirectories.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 73, documentName);
            }
        });
        auditedfinancial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = auditedfinancial.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 74, documentName);
            }
        });
        URAcertified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = URAcertified.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 75, documentName);
            }
        });
        previousbusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = previousbusiness.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 76, documentName);
            }
        });

        bankstatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = bankstatement.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 77, documentName);
            }
        });
        /*cmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String documentName = cmaButton.getText().toString().replace(" ", "_");
                clearPreviousPdfData(documentName);
                selectAlertItem(activity, 78, documentName);
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

        spinner = (Spinner) findViewById(R.id.item_one_spinner);
        Button finger_btn = (Button) findViewById(R.id.finger_print);
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
                activity.finish();
            }
        });

        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        defaultAccountContainer = (LinearLayout) findViewById(R.id.default_account_container);
        associateAccountContainer = (LinearLayout) findViewById(R.id.existing_account_container);
        defaultAccount = (RadioButton) findViewById(R.id.create_default_btn);
        associateAccount = (RadioButton) findViewById(R.id.associate_existing_account_btn);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.create_default_btn:
                        defaultAccountContainer.setVisibility(View.VISIBLE);
                        associateAccountContainer.setVisibility(View.GONE);
                        break;
                    case R.id.associate_existing_account_btn:
                        defaultAccountContainer.setVisibility(View.VISIBLE);
                        associateAccountContainer.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void uploadDocuments(final UserRegistration userRegistration) {

        final int totalDocs = userRegistration.userDocs.size();
        final UserRegistration.UserDocCommand[] docCommand = new UserRegistration.UserDocCommand[userRegistration.userDocs.size()];
        userRegistration.userDocs.toArray(docCommand);

        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please Wait, Uploading Documents!");

        for (  int i=0; i<totalDocs; i++) {
            String prodPicDir = "documents/" + docCommand[i].docType +"/"+ userRegistration.userId + "/";

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
                            updateUserDetails();
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
                                   uploadDocuments(userRegistration);

                              }

                          }else if(userLogin.status.equals("INVALID_SESSION")){
                              ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                            ReDirectToParentActivity.callLoginActivity(activity);
                          }else if(!userLogin.status.isEmpty()){
                              ProgressDialogUtil.stopProgressDialog(uploadProgressDialog);
                              AlertDialog.Builder alert = new AlertDialog.Builder(EditMandatoryDocsActivity.this);
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

    private void updateUserDetails() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, updating user information!");
            serviceHandler.postUpdateUser(userRegistration, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin orderDetails = (UserLogin) data.get(0);
                    if (orderDetails.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("User Updated Successfully.");
                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        startNavigationActivity();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                        if (orderDetails.status.equals("INVALID_SESSION")) {
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {
                            Toast.makeText(activity, "Status:" + orderDetails.status, Toast.LENGTH_SHORT).show();
                            startNavigationActivity();
                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+"\n Status:"+status,"Update User");
                    saveInCache();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(EditMandatoryDocsActivity.this);
    }

    private void saveInCache() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert:");
        alertDialog.setMessage("Want to save data in Cache?");
        alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, Saving Data in Cache!");

                if (userRegistration.registrationType.equals("company")) {
                    collectPdfDocs();

                } else if (userRegistration.registrationType.equals("personal")) {
                    collectUserDocs();
                }

                List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
                commandList = CacheNewOrderData.loadUpdateUserCacheList(activity);
                if (commandList != null) {

                    NewOrderCommand order = new NewOrderCommand();
                    order.userInfo = userRegistration;
                    commandList.add(order);
                    CacheNewOrderData.saveUpdateUserDataCache(activity, commandList);

                    alertMessageAfterSuccess();

                } else {
                    List<NewOrderCommand> commandList1 = new ArrayList<NewOrderCommand>();
                    NewOrderCommand order1 = new NewOrderCommand();
                    order1.userInfo = userRegistration;
                    commandList1.add(order1);
                    CacheNewOrderData.saveUpdateUserDataCache(activity, commandList1);

                    alertMessageAfterSuccess();
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
        alertDialog.setTitle("Alert");
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

    private void setUgadanDocuments() {

        if (tempNinUrl != null)
            for (String url : tempNinUrl) {
                ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutNin.addView(thumbNail);
                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempNinUrl.clear();
    }

    private void setProfileDocuments() {
        int index = 0;
        if (tempProfileUrl != null)
            for (String url : tempProfileUrl) {
                ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutProfile.addView(thumbNail);
                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempProfileUrl.clear();
    }

    private void setActivationFormDocument() {
        int index = 0;
        if (tempActivationUrl != null)
            for (String url : tempActivationUrl) {
                ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutActivationForm.addView(thumbNail);
                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempActivationUrl.clear();
    }

    private void setPassportDocument() {
        if (tempPassportUrl != null)
            for (String url : tempPassportUrl) {
                ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutPassport.addView(thumbNail);
                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempPassportUrl.clear();
    }

    private void setVisaDocument() {
        int index = 0;
        if (tempVisaUrl != null)
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

    private void setRefugeeDocument() {
        int index = 0;
        if (tempRefugeeUrl != null)
            for (String url : tempRefugeeUrl) {
                ImageView thumbNail = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                layoutParams.setMargins(5, 0, 5, 0);
                thumbNail.setLayoutParams(layoutParams);
                thumbnailsLayoutRefugee.addView(thumbNail);
                final String pictureUrl = url;
                PictureUtility.loadImage(this, url.trim(), thumbNail, false);
            }
        tempRefugeeUrl.clear();
    }

    private void selectAlertItem(final Activity activity, final int requestCode, final String documentName) {
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

    private void setPdfView(String finalUrl) {

        File file = new File(finalUrl);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setData(Uri.fromFile(file));
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            MyToast.makeMyToast(activity, "Please Install PDF reader.", Toast.LENGTH_SHORT);
        }
    }

    private void setRefugee(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutRefugee.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void setVisa(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutVisa.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void setPassport(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutPassport.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void setNin(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutNin.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void setActivationForm(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutActivationForm.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void setProfile(String url) {
        ImageView thumbNail = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(5, 0, 5, 0);
        thumbNail.setLayoutParams(layoutParams);
        thumbnailsLayoutProfile.addView(thumbNail);
        final String pictureUrl = url;
        PictureUtility.loadImage(this, url.trim(), thumbNail, false);
    }

    private void startNavigationActivity() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        startActivity(intent);
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


                        } else {
                            certifiedcompany.setText("OK");
                            Certifiedformfile.setText("OK");
                            copiesdirectories.setText("OK");
                            auditedfinancial.setText("OK");
                            URAcertified.setText("OK");
                            previousbusiness.setText("OK");
                            bankstatement.setText("OK");
                        }
                        personalDocList.toArray(personalDocs);

                    } else {
                        MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"STATUS:"+status,"EDIT_MANDATORY_DOCUMENT");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserRegistration collectPdfDocs() {
        filePrefix = buildProductPictureFilePrefix(userRegistration);
        randomNumber = generateRandomNumber();
        String aFormDocuments = "";
        String aFormDocuments1 = "";

        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        for (PdfDocumentData documentData : uploadedDocArray) {
            if (documentData != null) {
                for(UserRegistration.UserDocCommand docCommand: tempRejectedCompanyDocs){
                    if(docCommand.docType.equals(documentData.docType)){

                        UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();
                        uDocCommand.docFiles = documentData.displayName.toString().replace(" ", "_") + ("_" + randomNumber) + ";";
                        uDocCommand.docType = documentData.docType.toString();
                        uDocCommand.docFormat = "pdf";
                        uDocCommand.reviewStatus = "Submitted";
                        uDocCommand.docId = docCommand.docId;
                        uDocCommand.userId = userRegistration.userId;
                        allDocsJson += uDocCommand.docFiles + ".pdf;";
                        uDocCommand.pdfRwaData = documentData.pdfRwaData;
                        userDocCommandList.add(uDocCommand);
                    }
                }

            }

        }

        if (activationFormImages.size() != 0) {
            for (int numFiles = 0; numFiles < activationFormImages.size(); numFiles++) {
                aFormDocuments += filePrefix + "_activation_" + (randomNumber + numFiles) + ";";
                aFormDocuments1 += filePrefix + "_activation_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
            uDocCommand1.docFiles = aFormDocuments;
            uDocCommand1.docType = "activation";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;

            View[] idImagesArray = new View[activationFormImages.size()];
            activationFormImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

            uDocCommand1.imageData = imgData;
            userDocCommandList.add(uDocCommand1);

            allDocsJson += aFormDocuments1;
        }
        userRegistration.userDocs = userDocCommandList;
        return userRegistration;
    }

    private long generateRandomNumber() {
        double n = Math.random();
        long n3 = Math.round(Math.random() * 1000);
        return n3;
    }

    private UserRegistration collectUserDocs() {
        filePrefix = buildProductPictureFilePrefix(userRegistration);
        randomNumber = generateRandomNumber();
        if (filePrefix != null) {

        }
        String docImages = "";
        String passportDocuments = "";
        String refugeeDocuments = "";
        String profileDocuments = "";
        String docImages1 = "";
        String passportDocuments1 = "";
        String refugeeDocuments1 = "";
        String profileDocuments1 = "";
        String visaDocuments1 = "";
        String aFormDocuments1 = "";
        String visaDocuments = "";
        String aFormDocuments = "";

        String nin = "";
        String refugee = "";
        String passport = "";
        String visa = "";
        String profile = "";
        String activationForm = "";


        UserRegistration.UserDocCommand uDocCommand = new UserRegistration.UserDocCommand();
        List<UserRegistration.UserDocCommand> userDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();

        if (idImages.size() != 0) {
            View[] idImagesArray = new View[idImages.size()];
            idImages.toArray(idImagesArray);

            if (idImages.size() == 1) {
                docImages += filePrefix + "_nin_" + (randomNumber) + ";";
                uDocCommand.docFiles = docImages;
                uDocCommand.docType = "nin";
                uDocCommand.docFormat = "jpeg";
                uDocCommand.reviewStatus = "Submitted";
                uDocCommand.userId = userRegistration.userId;

                if (tempRejectedNin.size() != 0) {
                    rejectedNinDocs = new UserRegistration.UserDocCommand[tempRejectedNin.size()];
                    tempRejectedNin.toArray(rejectedNinDocs);
                    uDocCommand.docId = rejectedNinDocs[0].docId;
                }

                ImageView imageView = (ImageView) idImagesArray[0];
                Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                uDocCommand.imageData = imgData;

                userDocCommandList.add(uDocCommand);
            } else if (idImages.size() == 2) {
                int count = 0;
                for (View idView : idImages) {
                    docImages += filePrefix + "_nin_" + (randomNumber + count) + ";";
                    uDocCommand.docFiles = docImages;
                    uDocCommand.docType = "nin";
                    uDocCommand.docFormat = "jpeg";
                    uDocCommand.reviewStatus = "Submitted";
                    uDocCommand.userId = userRegistration.userId;

                    if (tempRejectedNin.size() != 0) {
                        rejectedNinDocs = new UserRegistration.UserDocCommand[tempRejectedNin.size()];
                        tempRejectedNin.toArray(rejectedNinDocs);
                        if (rejectedNinDocs != null) {
                            if (rejectedNinDocs.length == 2) {
                                uDocCommand.docId = rejectedNinDocs[count].docId;
                            } else if (rejectedNinDocs.length == 1) {
                                if (count == 0) {
                                    uDocCommand.docId = rejectedNinDocs[0].docId;
                                } else {
                                    uDocCommand.docId = null;
                                }
                            }
                        }
                    }

                    ImageView imageView = (ImageView) idView;
                    Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
                    String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                    uDocCommand.imageData = imgData;

                    userDocCommandList.add(uDocCommand);
                    count++;
                }
            }

        }


        if (passportImages.size() != 0) {
            for (int numFiles = 0; numFiles < passportImages.size(); numFiles++) {
                passportDocuments += filePrefix + "_passport_" + (randomNumber + numFiles) + ";";
                passportDocuments1 += filePrefix + "_passport_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
            uDocCommand1.docFiles = passportDocuments;
            uDocCommand1.docType = "passport";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;

            if (tempRejectedPassport.size() != 0) {
                rejectedPassportDocs = new UserRegistration.UserDocCommand[tempRejectedPassport.size()];
                tempRejectedPassport.toArray(rejectedPassportDocs);
                uDocCommand1.docId = rejectedPassportDocs[0].docId;
            }
            View[] idImagesArray = new View[passportImages.size()];
            passportImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);
            uDocCommand1.imageData = imgData;
            userDocCommandList.add(uDocCommand1);
            allDocsJson += passportDocuments1;
        }


        if (refugeeImages.size() != 0) {
            for (int numFiles = 0; numFiles < refugeeImages.size(); numFiles++) {
                refugeeDocuments += filePrefix + "_refugee_" + (randomNumber + numFiles) + ";";
                refugeeDocuments1 += filePrefix + "_refugee_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
            uDocCommand1.docFiles = refugeeDocuments;
            uDocCommand1.docType = "refugee";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;

            if (tempRejectedRefugee.size() != 0) {
                rejectedRefugeeDocs = new UserRegistration.UserDocCommand[tempRejectedRefugee.size()];
                tempRejectedRefugee.toArray(rejectedRefugeeDocs);
                uDocCommand1.docId = rejectedRefugeeDocs[0].docId;
            }

            View[] idImagesArray = new View[refugeeImages.size()];
            refugeeImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);
            uDocCommand1.imageData = imgData;
            userDocCommandList.add(uDocCommand1);
            allDocsJson += refugeeDocuments1;
        }

        if (profileImages.size() != 0) {
            for (int numFiles = 0; numFiles < profileImages.size(); numFiles++) {
                profileDocuments += filePrefix + "_profile_" + (randomNumber + numFiles) + ";";
                profileDocuments1 += filePrefix + "_profile_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand3 = new UserRegistration.UserDocCommand();
            uDocCommand3.docFiles = profileDocuments;
            uDocCommand3.docType = "profile";
            uDocCommand3.docFormat = "jpeg";
            uDocCommand3.reviewStatus = "Submitted";
            uDocCommand3.userId = userRegistration.userId;

            if (tempRejectedProfile.size() != 0) {
                rejectedProfileDocs = new UserRegistration.UserDocCommand[tempRejectedProfile.size()];
                tempRejectedProfile.toArray(rejectedProfileDocs);
                uDocCommand3.docId = rejectedProfileDocs[0].docId;
            }

            View[] idImagesArray = new View[profileImages.size()];
            profileImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);
            uDocCommand3.imageData = imgData;
            userDocCommandList.add(uDocCommand3);
            allDocsJson += profileDocuments1;
        }

        if (activationFormImages.size() != 0) {
            for (int numFiles = 0; numFiles < activationFormImages.size(); numFiles++) {
                aFormDocuments += filePrefix + "_activation_" + (randomNumber + numFiles) + ";";
                aFormDocuments1 += filePrefix + "_activation_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
            uDocCommand1.docFiles = aFormDocuments;
            uDocCommand1.docType = "activation";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;

            if (tempRejectedActivation.size() != 0) {
                rejectedActivationDocs = new UserRegistration.UserDocCommand[tempRejectedActivation.size()];
                tempRejectedActivation.toArray(rejectedActivationDocs);
                uDocCommand1.docId = rejectedActivationDocs[0].docId;
            }

            View[] idImagesArray = new View[activationFormImages.size()];
            activationFormImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);
            uDocCommand1.imageData = imgData;
            userDocCommandList.add(uDocCommand1);
            allDocsJson += aFormDocuments1;
        }

        if (visaImages.size() != 0) {
            for (int numFiles = 0; numFiles < visaImages.size(); numFiles++) {
                visaDocuments += filePrefix + "_visa_" + (randomNumber + numFiles) + ";";
                visaDocuments1 += filePrefix + "_visa_" + (randomNumber + numFiles) + ".jpeg;";
            }

            UserRegistration.UserDocCommand uDocCommand1 = new UserRegistration.UserDocCommand();
            uDocCommand1.docFiles = visaDocuments;
            uDocCommand1.docType = "visa";
            uDocCommand1.docFormat = "jpeg";
            uDocCommand1.reviewStatus = "Submitted";
            uDocCommand1.userId = userRegistration.userId;

            if (tempRejectedVisa.size() != 0) {
                rejectedVisaDocs = new UserRegistration.UserDocCommand[tempRejectedVisa.size()];
                tempRejectedVisa.toArray(rejectedVisaDocs);
                uDocCommand1.docId = rejectedVisaDocs[0].docId;
            }

            View[] idImagesArray = new View[visaImages.size()];
            visaImages.toArray(idImagesArray);
            ImageView imageView = (ImageView) idImagesArray[0];
            Bitmap bitMap = PictureUtility.drawableToBitmap(imageView.getDrawable());
            String imgData = PictureUtility.encodePicutreBitmap(bitMap);

            uDocCommand1.imageData = imgData;

            userDocCommandList.add(uDocCommand1);

            allDocsJson += visaDocuments1;
        }

      /*
       This is just Temporry Removed from POst Request but we can upload direct FingerPrint to the server.
*/
        if (isFingerPrint) {
            if (userFingerprintsList != null) {
                if (userFingerprintsList.size() == 2) {
                    if (isThumbPrint) {
                        UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                        uDocCommand2.docFiles = filePrefix + "_thumb_fingerPrint_0;";
                        fingerprintDocsJson += filePrefix + "_thumb_fingerPrint_0;";
                        uDocCommand2.docType = "fingerprints";
                        uDocCommand2.reviewStatus = "Accepted";
                        uDocCommand2.docFormat = "jpeg";
                        uDocCommand2.userId = userRegistration.userId;

                        if (tempRejectedThumbFingerprints.size() != 0) {
                            rejectedThumbFingerprint = new UserRegistration.UserDocCommand[tempRejectedThumbFingerprints.size()];
                            tempRejectedThumbFingerprints.toArray(rejectedThumbFingerprint);
                            uDocCommand.docId = rejectedThumbFingerprint[0].docId;
                        }

                        Bitmap bitMap = PictureUtility.drawableToBitmap(userFingerprintsList.get(0));
                        String imgData = PictureUtility.encodePicutreBitmap(bitMap);

                        uDocCommand2.imageData = imgData;

                        userDocCommandList.add(uDocCommand2);
                    }
                    if (isIndexPrint) {
                        UserRegistration.UserDocCommand uDocComnd = new UserRegistration.UserDocCommand();
                        uDocComnd.docFiles = filePrefix + "_index_fingerPrint_1;";
                        fingerprintDocsJson += filePrefix + "_index_fingerPrint_1;";

                        uDocComnd.docType = "fingerprints";
                        uDocComnd.docFormat = "jpeg";
                        uDocComnd.reviewStatus = "Accepted";
                        uDocComnd.userId = userRegistration.userId;

                        if (tempRejectedIndexFingerprints.size() != 0) {
                            rejectedIndexFingerprint = new UserRegistration.UserDocCommand[tempRejectedIndexFingerprints.size()];
                            tempRejectedIndexFingerprints.toArray(rejectedIndexFingerprint);
                            uDocCommand.docId = rejectedIndexFingerprint[0].docId;
                        }

                        Bitmap bitMap1 = PictureUtility.drawableToBitmap(userFingerprintsList.get(1));
                        String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                        uDocCommand.imageData = imgData1;
                        userDocCommandList.add(uDocComnd);
                    }
                } else {
                    if (isThumbPrint) {
                        UserRegistration.UserDocCommand uDocCommand2 = new UserRegistration.UserDocCommand();
                        uDocCommand2.docFiles = filePrefix + "_thumb_fingerPrint_0;";
                        fingerprintDocsJson += filePrefix + "_thumb_fingerPrint_1;";
                        if (tempRejectedThumbFingerprints.size() != 0) {
                            rejectedThumbFingerprint = new UserRegistration.UserDocCommand[tempRejectedThumbFingerprints.size()];
                            tempRejectedThumbFingerprints.toArray(rejectedThumbFingerprint);
                            uDocCommand.docId = rejectedThumbFingerprint[0].docId;
                        }

                        Bitmap bitMap1 = PictureUtility.drawableToBitmap(userFingerprintsList.get(0));
                        String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                        uDocCommand2.imageData = imgData1;
                        uDocCommand2.docType = "fingerprints";
                        uDocCommand2.docFormat = "jpeg";
                        uDocCommand2.reviewStatus = "Accepted";
                        uDocCommand2.userId = userRegistration.userId;
                        userDocCommandList.add(uDocCommand2);
                    } else if (isIndexPrint) {
                        UserRegistration.UserDocCommand uDocComnd = new UserRegistration.UserDocCommand();
                        uDocComnd.docFiles = filePrefix + "_index_fingerPrint_1;";
                        fingerprintDocsJson += filePrefix + "_index_fingerPrint_1;";

                        uDocComnd.docType = "fingerprints";
                        uDocComnd.docFormat = "jpeg";
                        uDocComnd.reviewStatus = "Accepted";
                        uDocComnd.userId = userRegistration.userId;

                        if (tempRejectedIndexFingerprints.size() != 0) {
                            rejectedIndexFingerprint = new UserRegistration.UserDocCommand[tempRejectedIndexFingerprints.size()];
                            tempRejectedIndexFingerprints.toArray(rejectedIndexFingerprint);
                            uDocCommand.docId = rejectedIndexFingerprint[0].docId;
                        }

                        Bitmap bitMap1 = PictureUtility.drawableToBitmap(userFingerprintsList.get(0));
                        String imgData1 = PictureUtility.encodePicutreBitmap(bitMap1);

                        uDocCommand.imageData = imgData1;
                        userDocCommandList.add(uDocComnd);
                    }
                }

            }

        }
        userRegistration.userDocs = userDocCommandList;
        return userRegistration;

    }

    private UserRegistration CollectDummyData(UserRegistration user) {

        if (associateAccount.isChecked()) {
            user.accountId = spinner.getSelectedItem().toString();
        }

        user.resellerCode = UserSession.getResellerId(activity);
        user.currency = "UGX";
        user.userGroup = "Consumer";
        return user;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                PdfDocumentData docData = new PdfDocumentData();
                docData.displayName = companyDocs[1].displayName;
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
                if (encodeData!= null) {

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
                }

            }
        }

        if (requestCode == 74 && resultCode == RESULT_OK) {
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
                }

            }
        }

        if (requestCode == 75 && resultCode == RESULT_OK) {
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
                }
            }
        }

        if (requestCode == 76 && resultCode == RESULT_OK) {
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
                }

            }
        }

        if (requestCode == 77 && resultCode == RESULT_OK) {
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

        /*if (requestCode == 78 && resultCode == RESULT_OK) {
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
                docData1.displayName = cmaButton.getText().toString();
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
                } else if (isRefugeeImage) {
                    View refugeePictureContainer = findViewById(R.id.refugee_images_cont);
                    PictureUtility.processRefugeePictureRequestWithEdit(activity, refugeePictureContainer, requestCode, data, refugeeImages, true);
                    isRefugeeImage = false;
                } else if (isActivationFormImage) {
                    View activationFormContainer = findViewById(R.id.activation_form_images_cont);
                    PictureUtility.processActivationFormPictureRequestWithEdit(activity, activationFormContainer, requestCode, data, activationFormImages, true);
                    isActivationFormImage = false;
                } else if (isVisaImage) {
                    View visaPictureContainer = findViewById(R.id.visa_images_cont);
                    PictureUtility.processVisaPictureRequestWithEdit(activity, visaPictureContainer, requestCode, data, visaImages, true);
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
        return userRegInfo.userName.toString();
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
