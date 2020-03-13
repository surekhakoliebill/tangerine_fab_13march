package com.aryagami.tangerine.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.OnDemandExistingAccountDetailsActivity;
import com.aryagami.tangerine.activities.OnDemandRegistrationActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class OnDemandPostpaidNewOrderFragment extends Fragment {

    LinearLayout creditScoreLimitLayout,createNewAccountLayout, accountSetupLayout;
    Button cancel, saveAndContinue, checkCreditScoreBtn;
    EditText creditAmount, creditScore,requestedCredit,depositValue,contractPeriod,additionalTerms, additionalNotes, discountValue;
    CheckBox enterManualCreditCheckbox;
    RadioGroup radioGroup,billingFrequencyRadioGroup;
    RadioButton createNewAccountRadioButton, existingAccountRadioButton, monthly,quarterly, halfYearly, yearly;
    NewOrderCommand newOrderCommandData = new NewOrderCommand();
    Spinner billingCycleSpinner;
    SearchableSpinner accountSpinner;
    Account accounts[];
    TextInputLayout textInputLayout;
    List<String> accountList;
    String accountList1[];
    UserRegistration registrationData;
    String[] billingCycle = {"1st Week","2nd Week","3rd Week","4th Week"};
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ondemand_fragment_postpaid_new_order,container,false);

        registrationData = new UserRegistration();

        creditScoreLimitLayout  = (LinearLayout)view.findViewById(R.id.credit_limit_layout);
        createNewAccountLayout = (LinearLayout)view.findViewById(R.id.create_new_acc_main_layout);
        accountSetupLayout = (LinearLayout)view.findViewById(R.id.existing_account_container1);
        billingCycleSpinner = (Spinner)view.findViewById(R.id.biling_cycle_id_spinner);

        newOrderCommandData.isPostpaid = true;
        textInputLayout = (TextInputLayout)view.findViewById(R.id.requested_credit_text);

        cancel = (Button)view.findViewById(R.id.cancel_btn);
        saveAndContinue = (Button)view.findViewById(R.id.save_and_continue);
        checkCreditScoreBtn = (Button)view.findViewById(R.id.check_cedit_score_button);

        creditAmount = (EditText)view.findViewById(R.id.credit_amount_edittext);
        creditScore = (EditText)view.findViewById(R.id.crdit_score_edittext);
        requestedCredit = (EditText)view.findViewById(R.id.requested_credit_etext);
        depositValue = (EditText)view.findViewById(R.id.deposite_value_etext);
        contractPeriod = (EditText)view.findViewById(R.id.contract_period_etext);
        additionalTerms = (EditText)view.findViewById(R.id.additional_terms_etext);
        additionalNotes = (EditText)view.findViewById(R.id.addional_terms_notes);
        discountValue = (EditText)view.findViewById(R.id.discount_value);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        createNewAccountRadioButton = (RadioButton)view.findViewById(R.id.create_new_acc_radio_btn);
        existingAccountRadioButton = (RadioButton)view.findViewById(R.id.existing_acc_radio_btn);
        accountSpinner = (SearchableSpinner)view.findViewById(R.id.item_one_spinner);
        accountSpinner.setTitle("Select Account");

        billingFrequencyRadioGroup = (RadioGroup)view.findViewById(R.id.bilingfrequency_radiogroup);
        monthly = (RadioButton)view.findViewById(R.id.monthly_radiobutton);
        quarterly = (RadioButton)view.findViewById(R.id.quarterly_radiobutton);
        halfYearly = (RadioButton)view.findViewById(R.id.halfyearly_radiobutton);
        yearly =(RadioButton)view.findViewById(R.id.yearly_radiobutton);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, billingCycle);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        billingCycleSpinner.setAdapter(adapter1);

        enterManualCreditCheckbox = (CheckBox)view.findViewById(R.id.manual_credit_score_checkbox);

        enterManualCreditCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterManualCreditCheckbox.isChecked()){
                    creditScoreLimitLayout.setVisibility(View.GONE);
                }else{
                    creditScoreLimitLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        RegistrationData.setIsmatched(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.create_new_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.continue_btn));
                        accountSetupLayout.setVisibility(View.GONE);
                        createNewAccountLayout.setVisibility(View.GONE);
                        newOrderCommandData.isNewAccount = true;
                        saveAndContinue.setVisibility(View.VISIBLE);
                        break;
                    case R.id.existing_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.save_and_continue));
                        accountSetupLayout.setVisibility(View.VISIBLE);
                        createNewAccountLayout.setVisibility(View.GONE);
                        newOrderCommandData.isNewAccount = false;
                        setAccountDetails();
                        break;
                }

            }
        });

        billingFrequencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.monthly_radiobutton:
                        registrationData.billingFrequency = "Monthly";
                        billingCycleSpinner.setVisibility(View.VISIBLE);
                        break;
                    case R.id.quarterly_radiobutton:
                        registrationData.billingFrequency = "Quarterly";
                        billingCycleSpinner.setVisibility(View.GONE);
                        break;
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(existingAccountRadioButton.isChecked()){
                    newOrderCommandData.isNewAccount= false;
                    newOrderCommandData.isPostpaid = true;
                    newOrderCommandData.contract = new NewOrderCommand.ContractCommand();
                    NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);
                    RegistrationData.setOnDemandRegistrationData(registrationData);

                    Intent intent = new Intent(getActivity(), OnDemandExistingAccountDetailsActivity.class);
                    intent.putExtra("position", accountSpinner.getSelectedItemPosition());
                    startActivity(intent);
                }
                if(createNewAccountRadioButton.isChecked()){

                    newOrderCommandData.isNewAccount= true;
                    newOrderCommandData.isPostpaid = true;
                    // newOrderCommandData = collectAllData();

                    NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);
                    RegistrationData.setOnDemandRegistrationData(registrationData);

                    Intent intent = new Intent(getActivity(), OnDemandRegistrationActivity.class);
                    startActivity(intent);
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }
    private void setAccountDetails() {
        /*if(NewOrderCommand.getAccount()!= null){

            accounts = NewOrderCommand.getAccount();
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
                saveAndContinue.setVisibility(View.VISIBLE);
            }else{
                saveAndContinue.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(),"DATA EMPTY!",Toast.LENGTH_SHORT).show();
            }

        }else{*/
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Please wait......");

                serviceHandler.getAccountDetails(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        if(data != null) {
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
                                        saveAndContinue.setVisibility(View.VISIBLE);
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
                           /* accounts = new Account[data.size()];
                            data.toArray(accounts);

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
                                saveAndContinue.setVisibility(View.VISIBLE);
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                saveAndContinue.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(),"Empty Data",Toast.LENGTH_SHORT).show();
                            }*/
                        }else{
                            saveAndContinue.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        BugReport.postBugReport(getActivity(), Constants.emailId,"BUG REPORT:","OnDemandPostpaidNewOrderFragment.java");
                        saveAndContinue.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                BugReport.postBugReport(getActivity(), Constants.emailId,"BUG REPORT:"+errors.toString(),"OnDemandPostpaidNewOrderFragment.java");
            }

    }
}
