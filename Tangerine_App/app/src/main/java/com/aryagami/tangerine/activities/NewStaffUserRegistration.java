package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

public class NewStaffUserRegistration extends AppCompatActivity {

    Activity activity = this;
    TextInputEditText given_name, email_id, phone_number, postpaid_signup_commision, recharge_commission, invoice_commission,
            signup_commission, company_name, tin_number, certificate_incorporation_number, primary_user_name,
            primary_user_phone_number, primary_person_emailid, landline_number, reseller_company_name,
            reseller_tin_number, reseller_certificate_incorporation_number, reseller_primary_user_name,
            reseller_primary_user_phone_number, reseller_person_emailid, reseller_landline_number;
    SearchableSpinner staffRoleSpinner;
    String[] aggregatorStaffRole = {"Reseller Distributor", "Reseller Staff"};
    String[] distributorStaffRole = {"Reseller Retailer", "Reseller Staff"};
    String[] retailerStaffRole = {"Reseller Staff"};
    LinearLayout resellerDistributorLayout, resellerStaffLayout;
    Button save, back, googleMap;
    TextInputEditText areaInKM, ussd;

    NewOrderCommand command = new NewOrderCommand();
    Roles[] rolesArray;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_staff_user_registration);

        given_name = (TextInputEditText) findViewById(R.id.new_staff_give_name);
        email_id = (TextInputEditText) findViewById(R.id.new_staff_email_id);
        phone_number = (TextInputEditText) findViewById(R.id.new_staff_phone_no);
        postpaid_signup_commision = (TextInputEditText) findViewById(R.id.new_staff_postpaid_signup_commission);
        company_name = (TextInputEditText) findViewById(R.id.new_staff_company_name);
        tin_number = (TextInputEditText) findViewById(R.id.new_staff_tin_number);
        certificate_incorporation_number = (TextInputEditText) findViewById(R.id.new_staff_incorporation_number);
        primary_user_name = (TextInputEditText) findViewById(R.id.new_staff_primary_user_name);
        primary_user_phone_number = (TextInputEditText) findViewById(R.id.new_staff_primary_user_phone_number);
        primary_person_emailid = (TextInputEditText) findViewById(R.id.new_staff_primary_person_email_id);
        landline_number = (TextInputEditText) findViewById(R.id.new_staff_landline_number);
        reseller_company_name = (TextInputEditText) findViewById(R.id.new_staff_reseller_company_name);
        reseller_tin_number = (TextInputEditText) findViewById(R.id.new_staff_reseller_tin_number);
        reseller_certificate_incorporation_number = (TextInputEditText) findViewById(R.id.new_staff_reseller_certificate_of_incorporation_number);
        reseller_primary_user_name = (TextInputEditText) findViewById(R.id.new_staff_reseller_primary_user_name);
        reseller_primary_user_phone_number = (TextInputEditText) findViewById(R.id.new_staff_reseller_primary_user_phone_number);
        reseller_person_emailid = (TextInputEditText) findViewById(R.id.new_staff_reseller_primary_person_email_id);
        reseller_landline_number = (TextInputEditText) findViewById(R.id.new_staff_reseller_landline_number);

        areaInKM = (TextInputEditText) findViewById(R.id.region_area);
        ussd = (TextInputEditText) findViewById(R.id.ussd_number);

        staffRoleSpinner = (SearchableSpinner) findViewById(R.id.new_staff_role_spinner);

        resellerDistributorLayout = (LinearLayout) findViewById(R.id.new_staff_commssion);
        resellerStaffLayout = (LinearLayout) findViewById(R.id.new_reseller_staff);

        save = (Button) findViewById(R.id.new_staff_savecontinue_btn);
        back = (Button) findViewById(R.id.staff_back_button_);
        googleMap = (Button)findViewById(R.id.google_map);

        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(NewStaffUserRegistration.this, MapsActivity.class);
                intent1.putExtra("Activity","map");
                startActivityForResult(intent1,1000);
            }
        });

        command.userInfo = new UserRegistration();
        getRoleOfStaffUser();
        if (UserSession.getUserGroup(activity).equals("Reseller Aggregator")) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, aggregatorStaffRole);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            staffRoleSpinner.setAdapter(adapter);
        } else if (UserSession.getUserGroup(activity).equals("Reseller Distributor")) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, distributorStaffRole);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            staffRoleSpinner.setAdapter(adapter);
        } else if (UserSession.getUserGroup(activity).equals("Reseller Retailer")){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, retailerStaffRole);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            staffRoleSpinner.setAdapter(adapter);
        }

        staffRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (selectedItem.equals("Reseller Distributor") ||selectedItem.equals("Reseller Retailer")  ) {
                    resellerDistributorLayout.setVisibility(View.VISIBLE);
                    resellerStaffLayout.setVisibility(View.GONE);
                    getRoleofSelectedItem();

                } else if (selectedItem.equals("Reseller Staff")) {
                    resellerDistributorLayout.setVisibility(View.GONE);
                    resellerStaffLayout.setVisibility(View.VISIBLE);
                    getRoleofSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewOrderCommand postOrder = new NewOrderCommand();
                postOrder = collectRegistrationData();
                if (postOrder != null) {
                    command = postOrder;
                    RegistrationData.setPostStaffOrder(command);
                    Intent intent = new Intent(NewStaffUserRegistration.this, NewStaffBillingAddressActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(NewStaffUserRegistration.this);
    }

    private void getRoleofSelectedItem() {
        if(rolesArray != null){
            if (rolesArray.length != 0)
                for (Roles role : rolesArray) {
                    if (role.roleName != null) {
                        String role1 = staffRoleSpinner.getSelectedItem().toString();
                        if (role.roleName.equals(role1)) {
                            command.userInfo.roleId = role.roleId.longValue();
                            command.userInfo.roleName = role.roleName.toString();
                        }
                    }
                }
        }
    }

    private void getRoleOfStaffUser() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.getAllRoles(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                   /* rolesArray = new Roles[data.size()];
                    rolesArray = data.toArray(rolesArray);
                    if (rolesArray.length != 0) {

                    } else {
                        MyToast.makeMyToast(activity, "Unable to set User Role.", Toast.LENGTH_SHORT);
                    }*/

                    Roles role = (Roles)data.get(0);
                    if(role != null){
                        if(role.status != null){
                            if(role.status.equals("success")){
                               rolesArray = new Roles[role.rolesList.size()];
                                rolesArray = role.rolesList.toArray(rolesArray);
                                if(rolesArray.length!= 0){
                                    RegistrationData.setRoles(rolesArray);
                                }
                            }else if(role.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity,"GET ROLES:"+role.status, Toast.LENGTH_SHORT);
                            }
                        }
                    }else{
                        MyToast.makeMyToast(activity,"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS: " + status + "\n ERROR" + error,"STAFF REGISTRATION ");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"ERROR: " + e.getCause() + "\n MESSAGE:" + e.getMessage(),"STAFF REGISTRATION ");
        }
    }

    private NewOrderCommand collectRegistrationData() {
        if (!given_name.getText().toString().isEmpty()) {
            command.userInfo.fullName = given_name.getText().toString();
        } else {
            Toast.makeText(activity, "Please enter Full Name", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (!email_id.getText().toString().isEmpty() && email_id.getText().toString().contains("@")) {
            command.userInfo.email = email_id.getText().toString();

        } else {
            Toast.makeText(activity, "Please enter Correct Email Id", Toast.LENGTH_SHORT).show();
            return null;
        }
        NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();
        if(RegistrationData.getPostLatitude()!= null){
            coordinates.latitudeValue = RegistrationData.getPostLatitude();
        }else{
            MyToast.makeMyToast(activity,"Please Select Your Location!", Toast.LENGTH_SHORT);
            return null;
        }

        if(RegistrationData.getPostLongitude()!= null){
           coordinates.longitudeValue = RegistrationData.getPostLongitude();
        }else{
            MyToast.makeMyToast(activity,"Please Select Your Location!", Toast.LENGTH_SHORT);
            return null;
        }
        if(coordinates.latitudeValue!=null && coordinates.longitudeValue != null){
            command.resellerLocation = coordinates;
        }else{
            MyToast.makeMyToast(activity,"Please Select Your Location!", Toast.LENGTH_SHORT);
            return null;
        }
        command.userInfo.phoneNumber = "256"+phone_number.getText().toString();
        if(staffRoleSpinner.getSelectedItem() != null) {
            if (staffRoleSpinner.getSelectedItem().toString().equals("Reseller Staff")) {
                command.userInfo.company = reseller_company_name.getText().toString();
                command.userInfo.tinNumber = reseller_tin_number.getText().toString();
                command.userInfo.coiNumber = reseller_certificate_incorporation_number.getText().toString();
                command.userInfo.primaryPersonName = reseller_primary_user_name.getText().toString();

                if (!reseller_primary_user_phone_number.getText().toString().isEmpty() && reseller_primary_user_phone_number.getText().length() == 9) {
                    command.userInfo.primaryPersonPhoneNumber = "256"+reseller_primary_user_phone_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Please, Enter Phone Number.", Toast.LENGTH_SHORT);
                    return null;
                }
                command.userInfo.primaryPersonEmailId = reseller_person_emailid.getText().toString();

                if (!reseller_landline_number.getText().toString().isEmpty() && reseller_landline_number.getText().length() == 9) {
                    command.userInfo.landLineNumber = "256" + reseller_landline_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Please, Enter LandLine Number.", Toast.LENGTH_SHORT);
                    return null;
                }
                command.userInfo.userGroup = "Reseller Staff";
                command.userInfo.currency = "UGX";
                command.userInfo.userGroup = staffRoleSpinner.getSelectedItem().toString();

            } else if (staffRoleSpinner.getSelectedItem().toString().equals("Reseller Distributor") || staffRoleSpinner.getSelectedItem().toString().equals("Reseller Retailer")) {

                if (!company_name.getText().toString().isEmpty()) {
                    command.userInfo.company = company_name.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Company Name field should not be empty.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!tin_number.getText().toString().isEmpty()) {
                    command.userInfo.tinNumber = tin_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Tin Number field should not be empty.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!certificate_incorporation_number.getText().toString().isEmpty()) {
                    command.userInfo.coiNumber = certificate_incorporation_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "COI field should not be empty.", Toast.LENGTH_SHORT);
                    return null;
                }
                if (!areaInKM.getText().toString().isEmpty()) {
                    command.resellerZoneRadiusKM = Float.parseFloat(areaInKM.getText().toString());
                } else {
                    MyToast.makeMyToast(activity, "Please Enter Area in KM.", Toast.LENGTH_SHORT);
                    return null;
                }
                if (!ussd.getText().toString().isEmpty()) {
                    command.ussd = ussd.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Please Enter USSD Number.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!primary_user_name.getText().toString().isEmpty()) {
                    command.userInfo.primaryPersonName = primary_user_name.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Primary User Name should not be empty.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!primary_user_phone_number.getText().toString().isEmpty() && primary_user_phone_number.getText().toString().length() == 9) {
                    command.userInfo.primaryPersonPhoneNumber = "256"+primary_user_phone_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Please, Enter 9 Digit Primary User Phone Number.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!primary_person_emailid.getText().toString().isEmpty()) {
                    command.userInfo.primaryPersonEmailId = primary_person_emailid.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Primary Person Email Id field should not be empty.", Toast.LENGTH_SHORT);
                    return null;
                }

                if (!landline_number.getText().toString().isEmpty() && landline_number.getText().toString().length() == 9) {
                    command.userInfo.landLineNumber = "256" + landline_number.getText().toString();
                } else {
                    MyToast.makeMyToast(activity, "Please, Enter LandLine Number.", Toast.LENGTH_SHORT);
                    return null;
                }

                command.userInfo.currency = "UGX";
                command.userInfo.userGroup = staffRoleSpinner.getSelectedItem().toString();
                command.userInfo.resellerCode = UserSession.getResellerId(activity);
            }

        }else{
            Toast.makeText(activity, "Please select Staff Role.", Toast.LENGTH_SHORT).show();
            return null;
        }
        String password = "Tangerine123".replace(" ", "");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            command.userInfo.password = LoginActivity.bytesToHex(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return command;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
