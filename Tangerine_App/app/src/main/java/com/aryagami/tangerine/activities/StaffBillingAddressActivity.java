package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.fragments.StaffUsersFragment;
import com.aryagami.util.CheckNetworkConnection;

import java.util.ArrayList;
import java.util.List;

public class StaffBillingAddressActivity extends AppCompatActivity {


    Spinner addressTypeSpinner, citySpinner;
    ProgressDialog progressDialog;
    Activity activity = this;
    UserRegistration userRegistration, data;

    ArrayAdapter adapter;
    String[] cityList = {"Select City", "Kampala", "Entebbe"};
    String[] addressTypes = {"Select Address Type", "Home", "Work"};
    TextInputEditText land_line_number, door_flat_number, complete_address, postal_address, country;

    List<UserRegistration.UserAddressCommand> commandList = new ArrayList<UserRegistration.UserAddressCommand>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_billing_address_fragment);

        userRegistration = RegistrationData.getRegistrationData();


        land_line_number = (TextInputEditText) findViewById(R.id.staff_landline_no);
        complete_address = (TextInputEditText) findViewById(R.id.staff_complete_address);
        postal_address = (TextInputEditText) findViewById(R.id.staff_postal_address);

        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        citySpinner.setAdapter(adapter);

        addressTypeSpinner = (Spinner) findViewById(R.id.addressType_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, addressTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        addressTypeSpinner.setAdapter(adapter1);


        Button back = (Button) findViewById(R.id.staff_backbilling_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.billing_container, new StaffUsersFragment());
                fr.commit();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(StaffBillingAddressActivity.this);
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
