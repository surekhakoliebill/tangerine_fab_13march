package com.aryagami.tangerine.activities;

/**
 * Created by aryagami on 11/10/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.aryagami.R;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.fragments.*;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;


/**
 * Created by aryagami on 14/3/17.
 */
public class RegistrationActivity extends AppCompatActivity {
   /* UserRegistration userRegistrationResponse;
    UserRegistration userRegistration;
    //LinearLayout personalRegContainer , companyRegContainer;
    //RadioButton personalRadioBtn, companyRadioBtn;
    RadioButton personalRadioBtn, companyRadioBtn, nationalBtn, foreignerBtn;
    LinearLayout personalRegContainer , companyRegContainer, nationalContainer, foreignerContainer;
    ProgressDialog progressDialog;
    Activity activity = this;
    //UserLogin userLoginResult;
    RadioGroup radioGroup;
    ArrayAdapter adapter, adapter1, adapter2;
    Spinner currencySpinner, citySpinner, countrySpinner;

    Button save;
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;


   *//* String[] currencyList = {"Select Currency","Uganda Shilling", "USD"};
    String[] cityList = {"Select City","Bombo","Entebbe","Jinja","Kampala","Lugazi","Mukono","Wakiso"};
    String[] countryList = {"Select Country","Uganda"};
*//*

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg_main);
       *//* if(RegistrationData.getRegistrationData()!= null){
            userRegistration = RegistrationData.getRegistrationData();
        }else {
            userRegistration = new UserRegistration();
        }*//*
        // RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup12);
        //personalRegContainer = (LinearLayout)findViewById(R.id.reg_personal1_container);
        //companyRegContainer = (LinearLayout)findViewById(R.id.reg_company1_container);
        //personalRadioBtn = (RadioButton)findViewById(R.id.personal11_reg_radio_btn);
        //companyRadioBtn = (RadioButton)findViewById(R.id.compnay11_reg_radio_btn);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_registration, new UserInformationFragment());
        fragmentTransaction.commit();


        //currencySpinner = (Spinner)findViewById(R.id.select_currency);
       // citySpinner = (Spinner) findViewById(R.id.uganda_shilling_spinner);
        //countrySpinner = (Spinner) findViewById(R.id.usd_spinner);




//save
      *//* Button save = (Button)findViewById(R.id.savecontinue1_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction f=getSupportFragmentManager().beginTransaction();
                f.replace(R.id.savecontinue1_btn, new BillingAddressFragment());
                f.commit();
            }
        });
*//*
*//*
        Button cancel = (Button)findViewById(R.id.savecontinue12_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, BillingAddressFragment.class);
                startActivity(intent);
            }
        });*//*


    }


*/
}