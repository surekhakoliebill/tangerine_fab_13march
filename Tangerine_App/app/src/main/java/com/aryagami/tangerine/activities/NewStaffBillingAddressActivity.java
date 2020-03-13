package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewStaffBillingAddressActivity extends AppCompatActivity {

    TextInputEditText physical_address, postal_code;
    String[] cityList = {"Select City","Kampala","Entebbe"};
    Spinner citySpinner;
    Activity activity = this;
    UserRegistration userRegistration, data;
    NewOrderCommand command;
    ArrayAdapter adapter;
    List<UserRegistration.UserAddressCommand> postAddressCommandList = new ArrayList<UserRegistration.UserAddressCommand>();
    ProgressDialog progressDialog;


    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_staff_billing_address);

        command = RegistrationData.getPostStaffOrder();
        physical_address = (TextInputEditText) findViewById(R.id.new_staff_complete_address);
        postal_code = (TextInputEditText) findViewById(R.id.new_staff_postal_address);
        Button btnsave=(Button) findViewById(R.id.new_staff_billing_savecontinue_btn);
        Button back = (Button) findViewById(R.id.staff_backbilling_btn);

        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        citySpinner.setAdapter(adapter);


        if( command.userInfo.userGroup.equals("Reseller Distributor") || command.userInfo.userGroup.equals("Reseller Retailer"))  {
           btnsave.setText("Save & Continue");
        }else if (command.userInfo.userGroup.equals("Reseller Staff")){
            btnsave.setText("Submit");
        }
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAddressCommandList = collectRegistrationData();
                if(postAddressCommandList != null )
                if( command.userInfo.userGroup.equals("Reseller Distributor") || command.userInfo.userGroup.equals("Reseller Retailer"))  {
                    command.userInfo.userAddressList= postAddressCommandList;
                    RegistrationData.setPostStaffOrder(command);
                    Intent intent = new Intent(activity, NewCopyStaffDocumentUploadActivity.class);
                    activity.startActivity(intent);
                }else if (command.userInfo.userGroup.equals("Reseller Staff")){
                    //command.userInfo.documentsUploadPending = false;
                    command.userInfo.userAddressList= postAddressCommandList;
                    command.userInfo.userDocs = new ArrayList<UserRegistration.UserDocCommand>();
                   // RegistrationData.setPostStaffOrder(command);
                    addResellerStaff();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void addResellerStaff(){
        collectResellerStaffData();
        RestServiceHandler serviceHandler = new RestServiceHandler();

        //List<UserRegistration.UserDocCommand> postDocCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        //postDocCommandList = collectPdfDocs();

        //if(postDocCommandList != null){
            //command.userInfo.userDocs = postDocCommandList;
            //PdfDocumentData.setStaffDocsList(Arrays.asList(uploadedDocArray));
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait..., Adding Reseller Staff User...!");
                serviceHandler.postStaffUserRegistration(command.userInfo, new RestServiceHandler.Callback() {
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
        //}
    }

    private void collectResellerStaffData() {

        command.userInfo.resellerCode = UserSession.getResellerId(activity) ;
        command.userInfo.registrationType = "company";
        command.userInfo.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";
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

    private void startNavigationMenuActivity() {
        activity.finish();
        Intent i = new Intent(activity, NavigationMainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(NewStaffBillingAddressActivity.this);
    }

    private List<UserRegistration.UserAddressCommand> collectRegistrationData() {
        List<UserRegistration.UserAddressCommand> commandList = new ArrayList<UserRegistration.UserAddressCommand>();
        UserRegistration.UserAddressCommand userAddressCommand =  new UserRegistration.UserAddressCommand();

        TextInputEditText complete_address = (TextInputEditText) findViewById(R.id.new_staff_complete_address);
        TextInputEditText postal_address = (TextInputEditText) findViewById(R.id.new_staff_postal_address);

        if(!complete_address.getText().toString().isEmpty()) {
            userAddressCommand.address = complete_address.getText().toString();
        }else{
            Toast.makeText(activity, "Please Enter Complete Address", Toast.LENGTH_SHORT).show();
            return null;
        }

        userAddressCommand.postalAddress= postal_address.getText().toString();

        if(citySpinner.getSelectedItem().toString().equals("Select City")){
            MyToast.makeMyToast(activity,"Please Select City", Toast.LENGTH_SHORT);
            return null;
        }else {
            userAddressCommand.city = citySpinner.getSelectedItem().toString();
        }
        userAddressCommand.country = "Uganda";

        commandList.add(userAddressCommand);
        return commandList;
    }

}
