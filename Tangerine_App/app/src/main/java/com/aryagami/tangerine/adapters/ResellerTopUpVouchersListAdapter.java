package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.ResellerVoucherType;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.AirtimeVoucherRechargeActivity;
import com.aryagami.tangerine.activities.EVoucherRechargeActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class ResellerTopUpVouchersListAdapter extends ArrayAdapter {
    Activity activity = null;
    ResellerVoucherType[] voucherTypes;
    ResellerVoucherType postVoucherData = new ResellerVoucherType();
    String servedMSISDN = "";
    String type;
    NewOrderCommand.LocationCoordinates coordinatesPost= new NewOrderCommand.LocationCoordinates();

    public ResellerTopUpVouchersListAdapter(Activity activity, ResellerVoucherType[] voucherTypesArray, String MSISDN, String type1, NewOrderCommand.LocationCoordinates coordinates){
        super(activity, R.layout.item_list_topup_voucher1, voucherTypesArray);
        this.activity = activity;
        this.voucherTypes = voucherTypesArray;
        this.servedMSISDN = MSISDN;
        this.type = type1;
        this.coordinatesPost = coordinates;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView;
        if(convertView != null){
            rowView = convertView;
        }else{
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_topup_voucher1, null, true);
        }


        final ResellerVoucherType rowItem = (ResellerVoucherType) getItem(position);

        TextView voucherType = (TextView)rowView.findViewById(R.id.voucher_type_value);
        TextView voucherDescription = (TextView)rowView.findViewById(R.id.description_value);
        TextView voucherPrice = (TextView)rowView.findViewById(R.id.price_value);
        TextView voucherName = (TextView)rowView.findViewById(R.id.voucher_name);


        Button recharge = (Button)rowView.findViewById(R.id.recharge_btn);

        if(rowItem != null){

            if(rowItem.voucherType != null){
                voucherType.setText(rowItem.voucherType.toString());

            }else{
                voucherType.setText("");
            }

            if(rowItem.voucherPrice != null){
                /*int price;
                float priceFloat = rowItem.voucherPrice;
                price = (int)priceFloat ;*/
                voucherPrice.setText("UGX "+ Math.round(rowItem.voucherPrice));

            }else{
                voucherPrice.setText("UGX 0");
            }

            if(rowItem.voucherDescription != null){
                voucherDescription.setText(rowItem.voucherDescription.toString());
            }else{
                voucherDescription.setText("");
            }

           if(rowItem.voucherName != null){
               voucherName.setText(rowItem.voucherName);
           }else{
               voucherName.setText("");
           }

           recharge.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

                   alertDialog.setCancelable(false);
                   alertDialog.setTitle("Alert!");
                   alertDialog.setMessage("Confirm Voucher Recharge?");
                   alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                   alertDialog.setPositiveButton("Proceed",
                           new DialogInterface.OnClickListener() {
                               public void onClick(final DialogInterface dialog, int id) {
                                   RestServiceHandler serviceHandler = new RestServiceHandler();
                                           collectData(position);
                                   try {
                                       serviceHandler.redeemVoucherDirect(postVoucherData, new RestServiceHandler.Callback() {
                                           @Override
                                           public void success(DataModel.DataType type, List<DataModel> data) {
                                               SubscriptionCommand command = (SubscriptionCommand)data.get(0);
                                               if(command.status.equals("success")) {
                                                   dialog.dismiss();
                                                   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                   alertDialog.setCancelable(false);
                                                   alertDialog.setIcon(R.drawable.success_icon);
                                                   alertDialog.setMessage("Your recharge was successful!");
                                                   alertDialog.setNeutralButton(activity.getString(R.string.ok),
                                                           new DialogInterface.OnClickListener() {
                                                               public void onClick(DialogInterface dialog, int id) {
                                                                   dialog.dismiss();
                                                                   callParentActivity();
                                                               }
                                                           });

                                                   alertDialog.show();


                                               }else if(command.status.equals("INVALID_SESSION")){
                                                  // MyToast.makeMyToast(activity, "Status :-"+command.status, Toast.LENGTH_SHORT);
                                                   ReDirectToParentActivity.callLoginActivity(activity);
                                               }else{
                                                   final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                   alertDialog.setCancelable(false);
                                                   alertDialog.setTitle("Message!");
                                                   alertDialog.setMessage("Status: "+command.status);
                                                   alertDialog.setNeutralButton(activity.getString(R.string.ok),
                                                           new DialogInterface.OnClickListener() {
                                                               public void onClick(DialogInterface dialog, int id) {
                                                                   dialog.dismiss();
                                                                   callParentActivity();
                                                               }
                                                           });

                                                   alertDialog.show();
                                               }
                                           }

                                           @Override
                                           public void failure(RestServiceHandler.ErrorCode error, String status) {
                                               BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");
                                           }
                                       });
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                       BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
                                   }

                               }
                           });
                   alertDialog.show();
               }
           });
        }
        return rowView;
    }

    private void callParentActivity() {
        if(type.equals("Airtime")) {
            activity.finish();
            Intent intent = new Intent(activity, AirtimeVoucherRechargeActivity.class);
            activity.startActivity(intent);

        }else  if(type.equals("Voucher")){
            activity.finish();
            Intent intent = new Intent(activity, EVoucherRechargeActivity.class);
            activity.startActivity(intent);
        }
    }

    private void collectData(int position) {

        postVoucherData.subscriptionId = servedMSISDN.toString();
        postVoucherData.transactionId = "reseller_"+(getAlphaNumericString(6));

        ResellerVoucherType voucherValue = voucherTypes[position];
        postVoucherData.transactionAmount = voucherValue.voucherPrice.floatValue();
        postVoucherData.voucherType = voucherValue.voucherType.toString();
        postVoucherData.resellerLocation = coordinatesPost;
    }

    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


}
