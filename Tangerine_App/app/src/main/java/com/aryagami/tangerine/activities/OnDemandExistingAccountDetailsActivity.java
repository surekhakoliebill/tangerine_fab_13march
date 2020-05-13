package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.UserSession;

public class OnDemandExistingAccountDetailsActivity extends AppCompatActivity {

    TextView currencyValue,billingFrequency,billingCycleId, discountType, discountValue, userName, primaryUserName;
    Button backButton, saveAndContinueButton;
    Activity activity = this;
    NewOrderCommand command = new NewOrderCommand();
    UserRegistration registration;
    RadioGroup discountTyepRadioGroup;
    RadioButton flatRadioButton, percentRadioButton;
    String[] accessTypes = {"Prepaid", "Reseller Access"};

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ondemand_existing_account_details);

        if(NewOrderCommand.getOnDemandNewOrderCommand()!= null){
            command = NewOrderCommand.getOnDemandNewOrderCommand();
        }

        if(RegistrationData.getOnDemandRegistrationData()!= null){
            registration = RegistrationData.getOnDemandRegistrationData();
        }else{
            registration = new UserRegistration();
        }

        currencyValue = (TextView)findViewById(R.id.currency_value);
        billingFrequency = (TextView)findViewById(R.id.billing_frequecy_value);
        billingCycleId = (TextView)findViewById(R.id.billing_cycle_id_value);
        discountType = (TextView)findViewById(R.id.discount_type_value);
        discountValue = (TextView)findViewById(R.id.discount_value);
        userName = (TextView)findViewById(R.id.username_value);
        primaryUserName = (TextView)findViewById(R.id.primary_username_value);

        discountTyepRadioGroup  = (RadioGroup)findViewById(R.id.discount_type_radiogroup);
        flatRadioButton = (RadioButton) findViewById(R.id.flat_rb);
        percentRadioButton = (RadioButton) findViewById(R.id.percent_rb);

        discountTyepRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.flat_rb:
                        //registration.discountType = "Flat";
                        break;
                    case R.id.percent_rb:
                       // registration.billingFrequency = "Percent";
                        break;
                }
            }
        });

        int position =  getIntent().getIntExtra("position",0);

        if(NewOrderCommand.getAccount()!= null ){
            Account account = NewOrderCommand.getAccount()[position];
            NewOrderCommand.setExistingAccountDetails(account);

            if(account!= null){
                if(account.currency != null){
                    currencyValue.setText(account.currency.toString());
                }else{
                    currencyValue.setText("");
                }
                if(account.billingFrequency != null){
                    billingFrequency.setText(account.billingFrequency.toString());
                }else{
                    billingFrequency.setText("");
                }

                if(account.discountType != null){
                    if(account.discountType.equalsIgnoreCase("Percent")){
                        percentRadioButton.setChecked(true);
                    }else {
                        flatRadioButton.setChecked(true);
                    }
                   // discountType.setText(account.discountType.toString());
                }else{
                   // discountType.setText("");
                    flatRadioButton.setChecked(true);
                }

                if(account.discountValue != null){
                    discountValue.setText(account.discountValue.toString());
                }else{
                    discountValue.setText("");
                }

                if(account.username != null){
                    userName.setText(account.username.toString());
                }else{
                    userName.setText("");
                }

                if(account.primaryAccountUsername != null){
                    primaryUserName.setText(account.primaryAccountUsername.toString());
                }else{
                    primaryUserName.setText("");
                }
                /*if(account.accountId != null){
                   command.userInfo.accountId = account.accountId;
                }*/

                registration.accountId  = account.accountId;
            }
        }else{
            Toast.makeText(this, "Unable to set the Account Details", Toast.LENGTH_SHORT).show();
        }

        backButton = (Button)findViewById(R.id.back_btn);
        saveAndContinueButton = (Button)findViewById(R.id.save_and_contionue);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

       // getUserRoleDetails();
        saveAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectAccountDetails();
                NewOrderCommand.setOnDemandNewOrderCommand(command);
                Intent intent = new Intent(activity, OnDemandAddSubscriptionActivity.class);
                intent.putExtra("parentActivity","account");
                startActivity(intent);
            }
        });
    }


    private void collectAccountDetails() {

        if(flatRadioButton.isChecked()){
                registration.discountType = "Flat";
        }else if(percentRadioButton.isChecked()){
            registration.discountType = "Percent";
        }

        if(!discountValue.getText().toString().isEmpty()){
            registration.discountValue = Float.parseFloat(discountValue.getText().toString());
        }else{
            registration.discountValue = 0f;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandExistingAccountDetailsActivity.this);
    }
    /*private void getUserRoleDetails() {
        if(RegistrationData.getRoles() != null && RegistrationData.getRoles().length != 0) {
            Roles[] rolesArray = new Roles[RegistrationData.getRoles().length];
            rolesArray = RegistrationData.getRoles();

            if (rolesArray.length != 0) {
                for (Roles role : rolesArray) {
                    if (role.roleName != null) {
                        if (role.roleName.equals("Consumer")) {
                            command.userInfo = new UserRegistration();
                            command.userInfo.roleId = role.roleId.longValue();
                            command.userInfo.roleName = role.roleName.toString();
                        }
                    }
                }
            }
        }else{
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    serviceHandler.getAllRoles(new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            rolesArray = new Roles[data.size()];
                            rolesArray = data.toArray(rolesArray);
                            if (rolesArray.length != 0) {
                                for (Roles role : rolesArray) {
                                    if (role.roleName != null) {
                                        if (role.roleName.equals("Consumer")) {
                                            command.userInfo = new UserRegistration();
                                            command.userInfo.roleId = role.roleId.longValue();
                                            command.userInfo.roleName = role.roleName.toString();
                                        }
                                    }
                                }
                            } else {
                              //  saveAndContinueButton.setVisibility(View.INVISIBLE);
                                MyToast.makeMyToast(activity, "Unable to set User Role.", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            MyToast.makeMyToast(activity, "STATUS: " + status + "\n ERROR" + error, Toast.LENGTH_SHORT);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }*/
}
