package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.OnDemandExistingAccountDetailsActivity;
import com.aryagami.tangerine.activities.ResellerETopupRequestsActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class SearchedAccountsListAdapter extends ArrayAdapter {
    Account[] accounts;
    Activity activity = null;
    ProgressDialog progressDialog, progressDialog1;
    NewOrderCommand newOrderCommandData = new NewOrderCommand();


    public SearchedAccountsListAdapter(Activity context, Account[] accounts1) {
        super(context, R.layout.item_of_searched_account, accounts1);
        this.accounts = accounts1;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_of_searched_account, null, true);
        }
        final TextView username = (TextView) rowView.findViewById(R.id.ac_uname_value);
        TextView fullname = (TextView) rowView.findViewById(R.id.ac_fullname_value);
        TextView surname = (TextView) rowView.findViewById(R.id.ac_surname_value);
        TextView accountNumber = (TextView) rowView.findViewById(R.id.ac_account_value);
        TextView identity = (TextView) rowView.findViewById(R.id.ac_identity_value);
        TextView userType = (TextView) rowView.findViewById(R.id.ac_usertype_value);

        Button selectButton = (Button)rowView.findViewById(R.id.select_button);

        final Account rowItem = (Account) getItem(position);

        if(rowItem.userInfo != null){

            if(rowItem.username != null){
                username.setText(rowItem.username);
            }else{
                username.setText("NA");
            }
            if(rowItem.accountId != null){
                accountNumber.setText(rowItem.accountId);
            }else{
                accountNumber.setText("NA");
            }

            /*if(rowItem.userInfo.fullName != null){
                fullname.setText(rowItem.userInfo.fullName);
            }else{
                fullname.setText("NA");
            }

            if(rowItem.userInfo.surname != null){
                surname.setText(rowItem.userInfo.surname);
            }else{
                surname.setText("NA");
            }*/

            if(rowItem.userInfo.registrationType != null) {
                if (rowItem.userInfo.registrationType.equals("personal") || rowItem.userInfo.registrationType.equals("retailer")) {

                    if (rowItem.userInfo.nationalIdentity != null) {

                        if (rowItem.userInfo.nationalIdentity.equals("Passport No")) {

                            if (rowItem.userInfo.identityNumber != null) {
                                identity.setText(rowItem.userInfo.identityNumber);
                            } else {
                                identity.setText("NA");
                            }

                        } else if (rowItem.userInfo.nationalIdentity.equals("Refugee")) {

                            if (rowItem.userInfo.refugeeIdentityNumber != null) {
                                identity.setText(rowItem.userInfo.refugeeIdentityNumber);
                            } else {
                                identity.setText("NA");
                            }

                        } else if (rowItem.userInfo.nationalIdentity.equals("Ugandan NationalID")) {

                            if (rowItem.userInfo.identityNumber != null) {
                                identity.setText(rowItem.userInfo.identityNumber);
                            } else {
                                identity.setText("NA");
                            }

                        }
                        if(rowItem.userInfo.userName != null){
                            username.setText(rowItem.userInfo.userName);
                        }else{
                            username.setText("NA");
                        }

                        if (rowItem.userInfo.fullName != null) {
                            fullname.setText(rowItem.userInfo.fullName);
                        } else {
                            fullname.setText("NA");
                        }
                        if (rowItem.userInfo.surname != null) {
                            surname.setText(rowItem.userInfo.surname);
                        } else {
                            surname.setText("NA");
                        }
                        if (rowItem.userInfo.nationalIdentity != null) {
                            userType.setText(rowItem.userInfo.nationalIdentity);
                        } else {
                            userType.setText("NA");
                        }

                    }

                } else if (rowItem.userInfo.registrationType.equals("company")) {

                    if(rowItem.userInfo.userGroup != null)
                        if(rowItem.userInfo.userGroup.equals("Mobile Money Agent")){

                            if(rowItem.userInfo.userName != null){
                                username.setText(rowItem.userInfo.userName);
                            }else{
                                username.setText("NA");
                            }
                            if (rowItem.userInfo.company != null) {
                                fullname.setText(rowItem.userInfo.company);
                            } else {
                                fullname.setText("NA");
                            }
                            surname.setText("NA");

                            if (rowItem.userInfo.mobileMoneyUserType != null) {
                                userType.setText(rowItem.userInfo.mobileMoneyUserType);
                            } else {
                                userType.setText("NA");
                            }

                            if (rowItem.userInfo.tinNumber != null) {
                                identity.setText("TIN Number-" + rowItem.userInfo.tinNumber);
                            } else {
                                if(rowItem.userInfo.coiNumber != null){
                                    identity.setText("COI Number-" + rowItem.userInfo.coiNumber);
                                }else{
                                    identity.setText("Identity Not Found"  );
                                }
                            }

                        }else{
                            surname.setText("NA");
                            if (rowItem.userInfo.company != null) {
                                fullname.setText(rowItem.userInfo.company);
                            } else {
                                fullname.setText("NA");
                            }

                            if (rowItem.userInfo.tinNumber != null) {
                                identity.setText("TIN Number-" + rowItem.userInfo.tinNumber);
                            } else {
                                if(rowItem.userInfo.coiNumber != null){
                                    identity.setText("COI Number-" + rowItem.userInfo.coiNumber);
                                }else{
                                    identity.setText("Identity Not Found"  );
                                }
                            }

                            if (rowItem.userInfo.registrationType != null) {
                                userType.setText(rowItem.userInfo.registrationType);
                            } else {
                                userType.setText("NA");
                            }

                        }

                }
            }
        }

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newOrderCommandData.isNewAccount= false;
                newOrderCommandData.isPostpaid = false;
                newOrderCommandData.contract = new NewOrderCommand.ContractCommand();
                NewOrderCommand.setOnDemandNewOrderCommand(newOrderCommandData);

                Intent intent = new Intent(activity, OnDemandExistingAccountDetailsActivity.class);
                intent.putExtra("position", position);
                activity.startActivity(intent);
            }
        });

        return rowView;
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerETopupRequestsActivity.class);
        activity.startActivity(intent);
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
