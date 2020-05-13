package com.aryagami.tangerine.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.EditUserMainActivity;
import com.aryagami.tangerine.activities.NavigationMainActivity;
import com.aryagami.tangerine.activities.OnDemandExistingAccountDetailsActivity;
import com.aryagami.tangerine.activities.OnDemandRegistrationActivity;
import com.aryagami.tangerine.adapters.ResellerETopupRequestsListAdapter;
import com.aryagami.tangerine.adapters.SearchedAccountsListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnDemandPrepaidNewOrderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    NewOrderCommand newOrderCommandData;
    RadioGroup radioGroup;
    RadioButton createNewAccountRadioButton, existingAccountRadioButton, existingAccountRadioButtonBySearch;
    SearchableSpinner accountSpinner;
    Spinner accountSearchSpinner;
    Account accounts[];
    List<String> accountList;
    String accountList1[];
    LinearLayout accountSetupLayout, accountSetupSearchLayout;
    Button cancel,saveAndContinue;
    ProgressDialog progressDialog;
    CheckBox mobileMoneyRegCheckbox, searchMobileMoneyRegCheckbox, resellerAccessCheckBox;
    // AccountDetails
    LinearLayout accountDetailsLayout;
    TextView firstName, surName, identity;
    CheckBox verifyUserId;
    Boolean isMobileMoneyAgent = false;
    Boolean isResellerAccess = false;
    String filterType, userValue;
    LinearLayout userNameLayout, msisdnLayout;
    String[] filterOptions = {"UserName", "FirstName", "Surname", "Existing Number(MSISDN)","Document Id"};
    ImageButton searchFilterTypeButton, searchMSISDNButton;
    TextInputEditText filterTypeText, msisdnFilterType;
    ListView searchedAccountsList;
    SearchableSpinner accessTypeSpinner;
    List<String> roleNames = new ArrayList<String>();
    String[] roleNamesArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ondemand_fragment_prepaid_new_order, container, false);
        RegistrationData.setIsPassportScan(false);
        RegistrationData.setUserThumbImageDrawable(null);
        RegistrationData.setUserIndexImageDrawable(null);


        if(NewOrderCommand.getOnDemandNewOrderCommand() != null){
            newOrderCommandData = NewOrderCommand.getOnDemandNewOrderCommand();
        }else{
            newOrderCommandData = new NewOrderCommand();
        }


        newOrderCommandData.isPostpaid = false;
        accountSetupLayout = (LinearLayout)view.findViewById(R.id.existing_account_container1);
        accountSetupSearchLayout = (LinearLayout)view.findViewById(R.id.existing_account_search_container);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        createNewAccountRadioButton = (RadioButton)view.findViewById(R.id.create_new_acc_radio_btn);
        existingAccountRadioButton = (RadioButton)view.findViewById(R.id.existing_acc_radio_btn);
        existingAccountRadioButtonBySearch = (RadioButton)view.findViewById(R.id.existing_acc_radio_btn_by_search);

        accountSpinner = (SearchableSpinner)view.findViewById(R.id.item_one_spinner);
        accountSpinner.setTitle("Select Account");
        accountSearchSpinner = (Spinner) view.findViewById(R.id.account_search_spinner);

        cancel = (Button)view.findViewById(R.id.cancel_btn);
        saveAndContinue = (Button)view.findViewById(R.id.save_and_continue);

        // Filter Changes according to username, fullname etc etc..
        userNameLayout = (LinearLayout)view. findViewById(R.id.username_layout);
        msisdnLayout = (LinearLayout)view. findViewById(R.id.msisdn_layout);
        msisdnLayout.setVisibility(View.GONE);

        searchFilterTypeButton = (ImageButton)view.findViewById(R.id.search_filtertype_button);
        searchMSISDNButton = (ImageButton)view.findViewById(R.id.search_msisdn_btn);

        filterTypeText = (TextInputEditText)view.findViewById(R.id.filter_text);
        msisdnFilterType = (TextInputEditText)view.findViewById(R.id.filter_msisdin_number);
        searchedAccountsList = (ListView)view.findViewById(R.id.accounts_search_list);

        // to show Account details
        firstName = (TextView)view.findViewById(R.id.acc_first_name);
        surName = (TextView)view.findViewById(R.id.acc_last_name);
        identity = (TextView)view.findViewById(R.id.acc_user_identity);
        verifyUserId = (CheckBox) view.findViewById(R.id.acc_verified_user_id);
        accountDetailsLayout = (LinearLayout)view.findViewById(R.id.account_details);


        mobileMoneyRegCheckbox = (CheckBox)view.findViewById(R.id.enableMobileMoneyReg);
        searchMobileMoneyRegCheckbox = (CheckBox)view.findViewById(R.id.enable_mobile_money_reg);
        resellerAccessCheckBox = (CheckBox)view.findViewById(R.id.reseller_access_check_box);
        accessTypeSpinner = (SearchableSpinner) view.findViewById(R.id.reseller_access_type_spinner);

        if(createNewAccountRadioButton.isChecked()){
            saveAndContinue.setText(getString(R.string.continue_btn));
            accountSetupLayout.setVisibility(View.GONE);
            accountSetupSearchLayout.setVisibility(View.GONE);
            saveAndContinue.setVisibility(View.VISIBLE);
            accountDetailsLayout.setVisibility(View.GONE);
            newOrderCommandData.isNewAccount = true;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.create_new_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.continue_btn));
                        accountSetupLayout.setVisibility(View.GONE);
                        accountSetupSearchLayout.setVisibility(View.GONE);
                        saveAndContinue.setVisibility(View.VISIBLE);
                        accountDetailsLayout.setVisibility(View.GONE);
                        newOrderCommandData.isNewAccount = true;
                        RegistrationData.setIsResellerAccess(false);
                        break;
                    case R.id.existing_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.save_and_continue));
                        saveAndContinue.setVisibility(View.INVISIBLE);
                        accountSetupLayout.setVisibility(View.VISIBLE);

                        if(RegistrationData.getEnableMobileMoneyReg() != null) {
                            if (RegistrationData.getEnableMobileMoneyReg()) {
                                mobileMoneyRegCheckbox.setVisibility(View.VISIBLE);
                                mobileMoneyRegCheckbox.setChecked(false);
                                setAccountDetails();
                               // setMobileMoneyAccountDetails();
                            } else {
                                mobileMoneyRegCheckbox.setVisibility(View.GONE);
                                mobileMoneyRegCheckbox.setChecked(false);
                                setAccountDetails();
                            }
                        }else {
                            mobileMoneyRegCheckbox.setVisibility(View.GONE);
                            mobileMoneyRegCheckbox.setChecked(false);
                            setAccountDetails();
                        }
                        newOrderCommandData.isNewAccount = false;

                        break;
                    case R.id.existing_acc_radio_btn_by_search:
                        newOrderCommandData.isNewAccount = false;
                        saveAndContinue.setText(getString(R.string.save_and_continue));
                        saveAndContinue.setVisibility(View.INVISIBLE);
                        accountSetupSearchLayout.setVisibility(View.VISIBLE);

                        getAndSetUserRoles();

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, filterOptions);
                        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        accountSearchSpinner.setAdapter(adapter1);
                        accountSearchSpinner.setOnItemSelectedListener(OnDemandPrepaidNewOrderFragment.this);

                        if(RegistrationData.getEnableMobileMoneyReg() != null) {
                            if (RegistrationData.getEnableMobileMoneyReg()) {
                                searchMobileMoneyRegCheckbox.setVisibility(View.VISIBLE);
                                searchMobileMoneyRegCheckbox.setChecked(false);

                            } else {
                                searchMobileMoneyRegCheckbox.setVisibility(View.GONE);
                                searchMobileMoneyRegCheckbox.setChecked(false);
                            }
                        }else {
                            searchMobileMoneyRegCheckbox.setVisibility(View.GONE);
                            searchMobileMoneyRegCheckbox.setChecked(false);
                        }

                        if(UserSession.getInternalHelpdeskRegistration(getActivity())){
                            resellerAccessCheckBox.setVisibility(View.VISIBLE);
                            resellerAccessCheckBox.setChecked(false);
                        }else{
                            resellerAccessCheckBox.setVisibility(View.GONE);
                            resellerAccessCheckBox.setChecked(false);
                        }
                        break;
                }

            }
        });

       /*
           use this for Old Existing Accounts for consumer & MobileMoney

       mobileMoneyRegCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    accountDetailsLayout.setVisibility(View.GONE);
                    saveAndContinue.setVisibility(View.INVISIBLE);
                    verifyUserId.setChecked(false);
                    setMobileMoneyAccountDetails();
                }else{
                    accountDetailsLayout.setVisibility(View.GONE);
                    saveAndContinue.setVisibility(View.INVISIBLE);
                    verifyUserId.setChecked(false);
                    setAccountDetails();
                }
            }
        });
*/

        searchMobileMoneyRegCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    isMobileMoneyAgent = true;
                    searchMobileMoneyRegCheckbox.setChecked(true);
                }else{
                    isMobileMoneyAgent = false;
                    searchMobileMoneyRegCheckbox.setChecked(false);
                }
            }
        });

        resellerAccessCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    isResellerAccess = true;
                    resellerAccessCheckBox.setChecked(true);
                    RegistrationData.setIsResellerAccess(true);
                }else{
                    isResellerAccess = false;
                    resellerAccessCheckBox.setChecked(false);
                    RegistrationData.setIsResellerAccess(false);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), NavigationMainActivity.class);
                startActivity(intent);
            }
        });

        searchFilterTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(accountSearchSpinner.getSelectedItem().equals("UserName")){
                    filterType = "username";
                }

                if(accessTypeSpinner.getSelectedItem() != null){
                    String selectedRole = accessTypeSpinner.getSelectedItem().toString();
                    if(!filterTypeText.getText().toString().isEmpty()){
                        String userValues[] = filterTypeText.getText().toString().split(" ");
                        userValue = userValues[0];
                        getAccountDetailsBySearch(isMobileMoneyAgent,filterType,userValue,selectedRole);

                    }else{
                        searchedAccountsList.setVisibility(View.GONE);
                        MyToast.makeMyToast(getActivity(),"Please Enter Text.", Toast.LENGTH_SHORT);
                    }
                }else{
                    searchedAccountsList.setVisibility(View.GONE);
                    MyToast.makeMyToast(getActivity(),"Please Select User Role.", Toast.LENGTH_SHORT);
                }

            }
        });

        searchMSISDNButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(accessTypeSpinner.getSelectedItem() != null){

                    if(!msisdnFilterType.getText().toString().isEmpty()){
                        userValue = msisdnFilterType.getText().toString().trim();
                        getAccountDetailsBySearch(isMobileMoneyAgent,filterType,userValue, accessTypeSpinner.getSelectedItem().toString());

                    }else{
                        searchedAccountsList.setVisibility(View.GONE);
                        MyToast.makeMyToast(getActivity(),"Please Enter MSISDN Number.", Toast.LENGTH_SHORT);
                    }
                }else{
                    searchedAccountsList.setVisibility(View.GONE);
                    MyToast.makeMyToast(getActivity(),"Please Select User Role.", Toast.LENGTH_SHORT);

                }
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newOrderCommandData.contract = new NewOrderCommand.ContractCommand();

                if(existingAccountRadioButton.isChecked()){
                    newOrderCommandData.isNewAccount= false;
                    NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);

                    Intent intent = new Intent(getActivity(), OnDemandExistingAccountDetailsActivity.class);
                    intent.putExtra("position", accountSpinner.getSelectedItemPosition());
                    startActivity(intent);
                }

                if(createNewAccountRadioButton.isChecked()){
                    newOrderCommandData.isNewAccount= true;
                    newOrderCommandData.isPostpaid = false;
                    NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);
                    RegistrationData.setRefugeeThumbEncodedData(null);

                    Intent intent = new Intent(getActivity(), OnDemandRegistrationActivity.class);
                    startActivity(intent);
                }

                if(existingAccountRadioButtonBySearch.isChecked()){
                    newOrderCommandData.isNewAccount= false;
                    NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);

                    Intent intent = new Intent(getActivity(), OnDemandExistingAccountDetailsActivity.class);
                    intent.putExtra("position", accountSpinner.getSelectedItemPosition());
                    startActivity(intent);
                }
            }
        });


        verifyUserId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    saveAndContinue.setVisibility(View.VISIBLE);
                }else{
                    saveAndContinue.setVisibility(View.INVISIBLE);
                }
            }
        });
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                if(accounts != null){
                    if(accounts.length > position){

                        Account accountDetails = new Account();
                        accountDetails = accounts[position];

                        if(accountDetails.userInfo != null)
                        if(accountDetails.userInfo.registrationType != null) {
                            accountDetailsLayout.setVisibility(View.VISIBLE);
                            if (accountDetails.userInfo.registrationType.equals("personal")) {

                                if (accountDetails.userInfo.nationalIdentity != null) {

                                    if (accountDetails.userInfo.nationalIdentity.equals("Passport No")) {

                                        if (accountDetails.userInfo.identityNumber != null) {
                                            identity.setText("Passport No : " + accountDetails.userInfo.identityNumber);
                                        } else {
                                            identity.setVisibility(View.GONE);
                                        }

                                    } else if (accountDetails.userInfo.nationalIdentity.equals("Refugee")) {

                                        if (accountDetails.userInfo.identityNumber != null) {
                                            identity.setText("Refugee ID: " + accountDetails.userInfo.identityNumber);
                                        } else {
                                            identity.setVisibility(View.GONE);
                                        }

                                    } else if (accountDetails.userInfo.nationalIdentity.equals("Ugandan NationalID")) {

                                        if (accountDetails.userInfo.identityNumber != null) {
                                            identity.setText("Ugandan National ID : " + accountDetails.userInfo.identityNumber);
                                        } else {
                                            identity.setVisibility(View.GONE);
                                        }
                                    }

                                    if (accountDetails.userInfo.fullName != null) {
                                        firstName.setText("FirstName : " + accountDetails.userInfo.fullName);
                                    } else {
                                        firstName.setVisibility(View.GONE);
                                    }
                                    if (accountDetails.userInfo.surname != null) {
                                        surName.setVisibility(View.VISIBLE);
                                        surName.setText("Surname : " + accountDetails.userInfo.surname);
                                    } else {
                                        surName.setVisibility(View.GONE);
                                    }


                                }

                            } else if (accountDetails.userInfo.registrationType.equals("company")) {

                                if(accountDetails.userInfo.userGroup != null)
                                if(accountDetails.userInfo.userGroup.equals("Mobile Money Agent")){

                                    if (accountDetails.userInfo.company != null) {
                                        firstName.setText("Company Name : " + accountDetails.userInfo.company);
                                    } else {
                                        firstName.setVisibility(View.GONE);
                                    }

                                    if (accountDetails.userInfo.mobileMoneyUserType != null) {
                                        surName.setText("UserType : " + accountDetails.userInfo.mobileMoneyUserType);
                                    } else {
                                        surName.setText("UserType : Not Found");
                                    }

                                    if (accountDetails.userInfo.tinNumber != null) {
                                        identity.setText("TIN Number : " + accountDetails.userInfo.tinNumber);
                                    } else {
                                        if(accountDetails.userInfo.coiNumber != null){
                                            identity.setText("COI Number : " + accountDetails.userInfo.coiNumber);
                                        }else{
                                            identity.setText("TIN Number : Not Found"  );
                                        }
                                    }

                                }else{
                                    surName.setVisibility(View.GONE);
                                    if (accountDetails.userInfo.company != null) {
                                        firstName.setText("Company Name : " + accountDetails.userInfo.company);
                                    } else {
                                        firstName.setVisibility(View.GONE);
                                    }

                                    if (accountDetails.userInfo.tinNumber != null) {
                                        identity.setText("TIN Number : " + accountDetails.userInfo.tinNumber);
                                    } else {
                                        if(accountDetails.userInfo.coiNumber != null){
                                            identity.setText("COI Number : " + accountDetails.userInfo.coiNumber);
                                        }else{
                                            identity.setText("TIN Number : Identity Not Found"  );
                                        }
                                    }
                                }

                            }
                        }else{
                            accountDetailsLayout.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void getAndSetUserRoles() {

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.getAllRoles(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    Roles role = (Roles)data.get(0);
                    if(role != null){
                        if(role.status != null){
                            if(role.status.equals("success")){
                                Roles[] rolesArray = new Roles[role.rolesList.size()];
                                rolesArray = role.rolesList.toArray(rolesArray);
                                if(rolesArray.length!= 0){
                                    RegistrationData.setRoles(rolesArray);
                                    for (Roles role1 : rolesArray) {
                                        if (role1.roleName != null) {
                                            roleNames.add(role1.roleName);
                                        }
                                    }

                                    roleNamesArray = new String[roleNames.size()];
                                    roleNames.toArray(roleNamesArray);

                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, roleNames);
                                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                    accessTypeSpinner.setAdapter(adapter1);

                                }else{
                                    MyToast.makeMyToast(getActivity(),"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                                }
                            }else if(role.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(getActivity());
                            }else{
                                MyToast.makeMyToast(getActivity(),"GET ROLES:"+role.status, Toast.LENGTH_SHORT);
                            }
                        }
                    }else{
                        MyToast.makeMyToast(getActivity(),"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    // MyToast.makeMyToast(getActivity(), "STATUS: "+status+"\n ERROR"+error, Toast.LENGTH_SHORT);
                    BugReport.postBugReport(getActivity(), Constants.emailId,"Error:"+error+"Status"+status,"");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(getActivity(), Constants.emailId,"Login UserName:"+UserSession.getResellerName(getActivity())+"Message: "+e.getMessage()+"Error: "+e.getCause(),"Get All UserRoles: Exception");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }
    private void setAccountDetails() {
        /*if(NewOrderCommand.getAccount() != null){

            accountList = new ArrayList<String>();
            for (Account acc : NewOrderCommand.getAccount()) {
                accountList.add(acc.username + "-" + acc.accountId);
            }

            accountList1 = new String[accountList.size()];
            accountList.toArray(accountList1);

            if (accountList1 != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                accountSpinner.setAdapter(adapter);
                saveAndContinue.setVisibility(View.VISIBLE);
            } else {
                saveAndContinue.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "EMPTY DATA", Toast.LENGTH_SHORT).show();
            }
        }else {*/
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Please wait, Fetching Active Accounts...");

                serviceHandler.getAccountDetails(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        if (data != null) {
                            /*accounts = new Account[data.size()];
                            data.toArray(accounts);

                            NewOrderCommand.setAccount(accounts);

                            accountList = new ArrayList<String>();
                            for (Account acc : accounts) {
                                accountList.add(acc.username + "-" + acc.accountId);
                            }

                            accountList1 = new String[accountList.size()];
                            accountList.toArray(accountList1);

                            if (accountList1 != null) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                accountSpinner.setAdapter(adapter);
                                saveAndContinue.setVisibility(View.VISIBLE);
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                            } else {
                                saveAndContinue.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), "Empty Data!", Toast.LENGTH_SHORT).show();
                            }*/

                            Account account = (Account)data.get(0);
                            if(account != null){
                                if(account.status != null)
                                    if(account.status.equals("success")){

                                        accounts = new Account[account.accountList.size()];
                                        account.accountList.toArray(accounts);

                                        NewOrderCommand.setAccount(accounts);

                                        accountList = new ArrayList<String>();
                                        for(Account acc : accounts){
                                            accountList.add(acc.username+"-"+acc.accountId);
                                        }

                                        accountList1 = new String[accountList.size()];
                                        accountList.toArray(accountList1);

                                        if(accountList1 != null) {

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                            accountSpinner.setAdapter(adapter);
                                            saveAndContinue.setVisibility(View.INVISIBLE);
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        }else{
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            saveAndContinue.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getActivity(),"Empty Data",Toast.LENGTH_SHORT).show();
                                        }
                                    }else if(account.status.equals("INVALID_SESSION")){
                                        ReDirectToParentActivity.callLoginActivity(getActivity());
                                    }else{
                                        MyToast.makeMyToast(getActivity(),"Status:"+account.status,Toast.LENGTH_SHORT);
                                    }


                            }
                        }
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        saveAndContinue.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    private void setMobileMoneyAccountDetails() {
        /*if(NewOrderCommand.getAccount() != null){

            accountList = new ArrayList<String>();
            for (Account acc : NewOrderCommand.getAccount()) {
                accountList.add(acc.username + "-" + acc.accountId);
            }

            accountList1 = new String[accountList.size()];
            accountList.toArray(accountList1);

            if (accountList1 != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                accountSpinner.setAdapter(adapter);
                saveAndContinue.setVisibility(View.VISIBLE);
            } else {
                saveAndContinue.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "EMPTY DATA", Toast.LENGTH_SHORT).show();
            }
        }else {*/
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Please wait, fetching Mobile Money Accounts");

            serviceHandler.getMobileMoneyAccountDetails(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    if (data != null) {
                            /*accounts = new Account[data.size()];
                            data.toArray(accounts);

                            NewOrderCommand.setAccount(accounts);

                            accountList = new ArrayList<String>();
                            for (Account acc : accounts) {
                                accountList.add(acc.username + "-" + acc.accountId);
                            }

                            accountList1 = new String[accountList.size()];
                            accountList.toArray(accountList1);

                            if (accountList1 != null) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                accountSpinner.setAdapter(adapter);
                                saveAndContinue.setVisibility(View.VISIBLE);
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                            } else {
                                saveAndContinue.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), "Empty Data!", Toast.LENGTH_SHORT).show();
                            }*/

                        Account account = (Account)data.get(0);
                        if(account != null){
                            if(account.status != null)
                                if(account.status.equals("success")){

                                    accounts = new Account[account.accountList.size()];
                                    account.accountList.toArray(accounts);

                                    NewOrderCommand.setAccount(accounts);

                                    accountList = new ArrayList<String>();
                                    for(Account acc : accounts){
                                        accountList.add(acc.username+"-"+acc.accountId);
                                    }

                                    accountList1 = new String[accountList.size()];
                                    accountList.toArray(accountList1);

                                    if(accountList1 != null) {

                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                        accountSpinner.setAdapter(adapter);
                                        saveAndContinue.setVisibility(View.INVISIBLE);
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        saveAndContinue.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getActivity(),"Empty Data",Toast.LENGTH_SHORT).show();
                                    }
                                }else if(account.status.equals("INVALID_SESSION")){
                                    ReDirectToParentActivity.callLoginActivity(getActivity());
                                }else{
                                    MyToast.makeMyToast(getActivity(),"Status:"+account.status,Toast.LENGTH_SHORT);
                                }


                        }
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    saveAndContinue.setVisibility(View.INVISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getAccountDetailsBySearch(Boolean isMobileMoneyAgent, String filterType, String value, String userRole){
        progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Please wait, Fetching Relative Accounts...");

        RestServiceHandler serviceHandler = new RestServiceHandler();

        try {

            serviceHandler.getAccountDetailsBySearch(isMobileMoneyAgent, filterType, value.trim(),userRole, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    if (data != null) {
                        Account account = (Account)data.get(0);
                        if(account != null){
                            if(account.status != null)
                                if(account.status.equals("success")){

                                    if(account.accountList != null){
                                        if(account.accountList.size() != 0){
                                            accounts = new Account[account.accountList.size()];
                                            account.accountList.toArray(accounts);

                                            NewOrderCommand.setAccount(accounts);

                                            searchedAccountsList.setVisibility(View.VISIBLE);
                                            ArrayAdapter adapter = new SearchedAccountsListAdapter(getActivity(), accounts);
                                            searchedAccountsList.setAdapter(adapter);
                                            searchedAccountsList.deferNotifyDataSetChanged();
                                        }else {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            searchedAccountsList.setVisibility(View.GONE);
                                            MyToast.makeMyToast(getActivity(),"EMPTY DATA", Toast.LENGTH_SHORT);
                                        }
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        searchedAccountsList.setVisibility(View.GONE);
                                        MyToast.makeMyToast(getActivity(),"EMPTY DATA", Toast.LENGTH_SHORT);
                                    }

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                }else if(account.status.equals("INVALID_SESSION")){
                                    ReDirectToParentActivity.callLoginActivity(getActivity());
                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    MyToast.makeMyToast(getActivity(),"Status: "+account.status,Toast.LENGTH_SHORT);
                                }


                        }
                    }else{
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        searchedAccountsList.setVisibility(View.GONE);
                        MyToast.makeMyToast(getActivity(),"EMPTY DATA", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    saveAndContinue.setVisibility(View.INVISIBLE);
                    BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR: "+error+"\t STATUS: "+status,"Getting Accounts List using Search: Failed.");

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(getActivity(), Constants.emailId,"Message: "+e.getMessage()+"Error: "+e.getCause(),"Getting Accounts List using Search: Exception");
        }
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        switch (adapterView.getId()) {
            case R.id.account_search_spinner:
//    String[] filterOptions = {"UserName", "FirstName", "Surname", "Existing Number(MSISDN)","Document Id"};
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                if (selectedItem.equals("UserName")) {
                    filterType = "username";
                    msisdnLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    filterTypeText.setHint("Username");

                   /* userNameLayout.setVisibility(View.VISIBLE);
                    msisdnLayout.setVisibility(View.GONE);
                    usersListView.setVisibility(View.GONE);*/

                } else if (selectedItem.equals("FirstName")) {
                    filterType = "fullname";
                    msisdnLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    filterTypeText.setHint("Firstname");

                }else if (selectedItem.equals("Surname")) {
                    filterType = "surname";
                    msisdnLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    filterTypeText.setHint("Surname");

                }else if (selectedItem.equals("Existing Number(MSISDN)")) {
                    filterType = "msisdn";
                    msisdnLayout.setVisibility(View.VISIBLE);
                    userNameLayout.setVisibility(View.GONE);

                }else if (selectedItem.equals("Document Id")) {
                    filterType = "docid";

                    msisdnLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    filterTypeText.setHint("Identity Number");
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
