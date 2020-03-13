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
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.ResellerRequestVo;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.ResellerStockRequestsActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class ResellerStockRequestsListAdapter extends ArrayAdapter {
    ResellerRequestVo[] resellerRequestVos;
    Activity activity = null;
    ProgressDialog progressDialog;


    public ResellerStockRequestsListAdapter(Activity context, ResellerRequestVo[] warehouseArray) {
        super(context, R.layout.item_stock_requests, warehouseArray);
        this.resellerRequestVos = warehouseArray;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_stock_requests, null, true);
        }
        TextView name= (TextView) rowView.findViewById(R.id.from_bin_name);
        TextView reqStatus = (TextView) rowView.findViewById(R.id.requested_status);
        TextView reqDate = (TextView) rowView.findViewById(R.id.requested_date);

        TextView type= (TextView) rowView.findViewById(R.id.type);
        TextView quantity = (TextView) rowView.findViewById(R.id.quantity_text);
        TextView amount = (TextView) rowView.findViewById(R.id.amount_text);

        Button cancelButton = (Button)rowView.findViewById(R.id.approve_button);

        final ResellerRequestVo rowItem = (ResellerRequestVo) getItem(position);

        if (rowItem.requestType != null) {
            name.setText(rowItem.requestType.toString());
        } else {
            name.setText("");
        }

        if (!rowItem.status.toString().isEmpty()) {
            reqStatus.setText(rowItem.status.toString());
        } else {
            reqStatus.setText("");
        }

        if (rowItem.creationDate != null) {
            reqDate.setText(rowItem.creationDate.toString());
        } else {
            reqDate.setText("");
        }

        if (rowItem.totalPrice != null) {
            amount.setText(rowItem.totalPrice.toString());
        } else {
            amount.setText("");
        }

        if(rowItem.resellerRequestEntities != null){

            if(rowItem.resellerRequestEntities.size() != 0){
                ResellerRequestVo.ResellerRequestEntityMapperVo entityMapperVo = rowItem.resellerRequestEntities.get(0);
                if (entityMapperVo.quantity != null) {
                    quantity.setText(entityMapperVo.quantity.toString());
                } else {
                    quantity.setText("");
                }
            }else{
                quantity.setText("");
            }
        }else{
            quantity.setText("");
        }

        if(rowItem.status!= null){
            if(rowItem.status.equalsIgnoreCase("REQUESTED")){
                cancelButton.setVisibility(View.VISIBLE);
            }else{
                cancelButton.setVisibility(View.GONE);
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             RestServiceHandler serviceHandler = new RestServiceHandler();
                if(rowItem.requestId != null) {

                    if(rowItem.requestType.equals("VoucherTransfer")){
                        try{
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                            serviceHandler.cancelResellerEVoucherOrAirtimeRequest(rowItem.requestId, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin) data.get(0);
                                    // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request Cancelled successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    } else if (userLogin.status.equals("INVALID_SESSION")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:" + userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"" );
                                }
                            });

                        }catch (IOException io){
                            BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                        }


                    }else if(rowItem.requestType.equals("eTopup")){

                        try{
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                            serviceHandler.cancelETopupRequest(rowItem.requestId, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin) data.get(0);
                                    // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request Cancelled successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    } else if (userLogin.status.equals("INVALID_SESSION")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:" + userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"" );
                                }
                            });

                        }catch (IOException io){
                            BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                        }
                    }else if(rowItem.requestType.equals("product")){
                        try{
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                            serviceHandler.cancelResellerProductRequest(rowItem.requestId, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin) data.get(0);
                                    // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request Cancelled successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    } else if (userLogin.status.equals("INVALID_SESSION")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:" + userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                callParentActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"" );
                                }
                            });

                        }catch (IOException io){
                            BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                        }
                    }
                }


            }
        });

        return rowView;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerStockRequestsActivity.class);
        activity.startActivity(intent);
    }
}
