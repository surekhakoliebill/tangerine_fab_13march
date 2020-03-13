package com.aryagami.tangerine.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.NavigationMainActivity;
import com.aryagami.tangerine.activities.OnDemandExistingAccountDetailsActivity;
import com.aryagami.tangerine.activities.OnDemandRegistrationActivity;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnDemandPrepaidNewOrderFragment extends Fragment {

    NewOrderCommand newOrderCommandData;
    RadioGroup radioGroup;
    RadioButton createNewAccountRadioButton, existingAccountRadioButton;
    SearchableSpinner accountSpinner;
    Account accounts[];
    List<String> accountList;
    String accountList1[];
    LinearLayout accountSetupLayout;
    Button cancel,saveAndContinue;
    ProgressDialog progressDialog;

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
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        createNewAccountRadioButton = (RadioButton)view.findViewById(R.id.create_new_acc_radio_btn);
        existingAccountRadioButton = (RadioButton)view.findViewById(R.id.existing_acc_radio_btn);
        accountSpinner = (SearchableSpinner)view.findViewById(R.id.item_one_spinner);
        accountSpinner.setTitle("Select Account");

        cancel = (Button)view.findViewById(R.id.cancel_btn);
        saveAndContinue = (Button)view.findViewById(R.id.save_and_continue);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.create_new_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.continue_btn));
                        accountSetupLayout.setVisibility(View.GONE);
                        saveAndContinue.setVisibility(View.VISIBLE);
                        newOrderCommandData.isNewAccount = true;
                        break;
                    case R.id.existing_acc_radio_btn:
                        saveAndContinue.setText(getString(R.string.save_and_continue));
                        accountSetupLayout.setVisibility(View.VISIBLE);
                        newOrderCommandData.isNewAccount = false;
                        setAccountDetails();
                        break;
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
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Please wait......");

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
    public  void onTrimMemory(int level) {
        System.gc();
    }
}
