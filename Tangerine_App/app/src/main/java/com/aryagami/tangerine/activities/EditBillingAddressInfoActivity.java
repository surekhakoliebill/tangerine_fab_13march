package com.aryagami.tangerine.activities;

import android.app.Activity;
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
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;

import java.util.ArrayList;
import java.util.List;

public class EditBillingAddressInfoActivity extends AppCompatActivity {

    ArrayAdapter adapter;
    String[] cityList = {"Kampala", "Entebbe"};
    String[] addressTypes = {"Home", "Work"};
    TextInputEditText land_line_number, door_flat_number, complete_address, postal_address, country;
    Activity activity = this;
    Spinner addressTypeSpinner, citySpinner;
    UserRegistration registration;
    UserRegistration data, uploadedData;
    Button backButton;
    Long postAddressId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_billing_address);

        Button saveAndContinue = (Button) findViewById(R.id.savecontinue_btn);
        Button add = (Button) findViewById(R.id.add_more);
        add.setVisibility(View.GONE);

        land_line_number = (TextInputEditText) findViewById(R.id.landline_no);
        door_flat_number = (TextInputEditText) findViewById(R.id.door_flat_no);
        complete_address = (TextInputEditText) findViewById(R.id.complete_address);
        postal_address = (TextInputEditText) findViewById(R.id.postal_address);
        country = (TextInputEditText) findViewById(R.id.country_value);
        backButton = (Button) findViewById(R.id.backbilling_btn);

        if (RegistrationData.getEditUserProfile() != null) {
            registration = RegistrationData.getEditUserProfile();
            setBillingAddressInfo(registration);
        }

        data = RegistrationData.getUpdateUserProfileData();
        if(data != null){
            uploadedData = data;
        }

        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        citySpinner.setAdapter(adapter);

        addressTypeSpinner = (Spinner) findViewById(R.id.addressType_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, addressTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        addressTypeSpinner.setAdapter(adapter1);

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = collectEditedData();
                if(data != null){
                    RegistrationData.setUpdateUserProfileData(data);
                    Intent intent = new Intent(activity, EditMandatoryDocsActivity.class);
                    startActivity(intent);
                }else{
                    data = uploadedData;
                }
            }

        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(EditBillingAddressInfoActivity.this);
    }

    private UserRegistration collectEditedData() {
        List<UserRegistration.UserAddressCommand> userAddressCommandList = new ArrayList<UserRegistration.UserAddressCommand>();
        UserRegistration.UserAddressCommand command = new UserRegistration.UserAddressCommand();
        command.doorNumber = door_flat_number.getText().toString();

        if(!complete_address.getText().toString().isEmpty()) {
            command.address = complete_address.getText().toString();
        }else{
            MyToast.makeMyToast(activity,"Please Enter Physical Address", Toast.LENGTH_SHORT);
            return null;
        }
        command.country = country.getText().toString();
        command.city = citySpinner.getSelectedItem().toString();
        command.addressType = addressTypeSpinner.getSelectedItem().toString();

        if(!postal_address.getText().toString().isEmpty()) {
            command.postalAddress = postal_address.getText().toString();
        }else{
            MyToast.makeMyToast(activity,"Please Enter Postal Address", Toast.LENGTH_SHORT);
            return null;
        }
        command.addressId = postAddressId;
        userAddressCommandList.add(command);
        data.userAddressList = userAddressCommandList;
        return data;
    }

    private void setBillingAddressInfo(UserRegistration data) {
        UserRegistration.UserAddressCommand command = new UserRegistration.UserAddressCommand();
        if (data.userAddressList != null)
        if (data.userAddressList.size() != 0)
        {
            command = data.userAddressList.get(0);
            if(command.doorNumber != null) {
                door_flat_number.setText(command.doorNumber.toString());
            }else{
                door_flat_number.setText("");
            }
            if(command.address != null) {
                complete_address.setText(command.address.toString());
            }else{
                complete_address.setText("");
            }

            postAddressId = command.addressId;
            if(command.postalAddress != null) {
                postal_address.setText(command.postalAddress.toString());
            }else{
                postal_address.setText("");
            }

            if(command.country != null) {
                country.setText(command.country.toString());
            }else{
                country.setText("");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}

