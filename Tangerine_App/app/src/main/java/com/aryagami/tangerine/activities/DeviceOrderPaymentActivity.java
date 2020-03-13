package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.PdfDocumentData;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.FilePath;
import com.aryagami.util.MyToast;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceOrderPaymentActivity extends AppCompatActivity {
    Button placeDeviceOrder, paymentCopy;
    Activity activity = this;
    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();
    NewOrderCommand command = new NewOrderCommand();
    NewOrderCommand.PaymentCommand paymentCommand = new NewOrderCommand.PaymentCommand();
    String uuid;
    Account account;
    EditText name, emailId;
    Uri pdfUri;
    List<PdfDocumentData> pdfDocumentDataList = new ArrayList<PdfDocumentData>();
    PdfDocumentData docData = new PdfDocumentData();
    TextView notification;
    CheckBox fullfillmentCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_order_payment);
        placeDeviceOrder = (Button)findViewById(R.id.place_device_order);
        paymentCopy = (Button)findViewById(R.id.payment_copy_of_device_order);
        name = (EditText) findViewById(R.id.name_value);
        emailId = (EditText) findViewById(R.id.email_id_value);
        TextView emailText = (TextView) findViewById(R.id.email);
        notification = (TextView)findViewById(R.id.dis_notification);
        fullfillmentCheck = (CheckBox) findViewById(R.id.fulfillmentCheck);

        uuid = UUID.randomUUID().toString();
        if(NewOrderCommand.getOnDemandNewOrderCommand() != null){
            command = NewOrderCommand.getOnDemandNewOrderCommand();
        }

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

        placeDeviceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    serviceHandler.postDeviceNewOrder(command, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                   final UserLogin orderDetails = (UserLogin) data.get(0);
                                    if(orderDetails != null){
                                        if(orderDetails.status != null)
                                            if(orderDetails.status.equals("success")){
                                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                alertDialog.setCancelable(false);
                                                alertDialog.setIcon(R.drawable.success_icon);
                                                alertDialog.setTitle("Success");
                                                alertDialog.setMessage("New Order Placed Successfully.\n Order No:" + orderDetails.orderNo.toString());
                                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.dismiss();
                                                                uploadPaymentCopy(orderDetails.orderNo, orderDetails.userId);

                                                            }
                                                        });

                                                alertDialog.show();

                                            }else if(orderDetails.status.equals("System Error")){
                                                BugReport.postBugReport(activity, Constants.emailId,"Status: System Error, Reason:"+orderDetails.reason,"DeviceOrderPaymentActivity");

                                            }else if(orderDetails.status.equals("INVALID_SESSION")){
                                                ReDirectToParentActivity.callLoginActivity(activity);
                                            }else{
                                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                alertDialog.setCancelable(false);
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Device Order Not Placed Successfully. Please Retry!");
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

                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+"Cause"+e.getCause(),"DeviceOrderPaymentActivity");
                }

            }
        });
        paymentCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPreviousPdfData("payment_copy");
                selectAlertItem(activity, 599, "payment_copy");
            }
        });

    }

    private void uploadPaymentCopy(String orderNo, final String userId) {
        if (docData != null) {
            if (docData != null) {
                final String fileUrl = "reseller_documents/payment_documents/" + orderNo + "/,payment_copy";

                RestServiceHandler uploadImageServiceHandler = new RestServiceHandler();

                try{
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
                                BugReport.postBugReport(activity, Constants.emailId, "Document Not Uploaded\t" + docData.docType + "\t" + ("documents/" + docData.docType + "/" + userId + "/," + (docData.displayName.toString()).replace(" ", "_")) + "\t" + docData.pdfRwaData.toString(), "NewOrderPaymentActivity");
                            }
                        });

                }catch (Exception e){
                    BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+"Cause"+e.getCause(),"DeviceOrder PaymentCopy");
                }
            }
        }
    }


    private NewOrderCommand fetchAllDetails() {

        coordinates.longitudeValue = RegistrationData.getCurrentLongitude();
        coordinates.latitudeValue = RegistrationData.getCurrentLatitude();
        command.resellerLocation = coordinates;
        command.productListingIds = new ArrayList<Long>();

        command.resellerCode = UserSession.getResellerId(activity);


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

        paymentCommand.paymentMethod = "CASH";

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
        } else {
            MyToast.makeMyToast(activity, "Unable to set User Role.", Toast.LENGTH_SHORT);

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

                   // isPdfFile = true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 599 && resultCode == RESULT_OK) {

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

}
