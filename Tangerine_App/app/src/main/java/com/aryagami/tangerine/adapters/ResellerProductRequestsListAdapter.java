package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.ResellerProductRequestsActivity;
import com.aryagami.tangerine.activities.ResellerUploadDeliveryNoteActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;


public class ResellerProductRequestsListAdapter extends ArrayAdapter {
    WalletAccountTransactionLogVo[] walletAccountTransactionLogVos;
    Activity activity = null;
    ProgressDialog progressDialog, progressDialog1;
    LinearLayout linearlayout1;
    UserRegistration.UserDocCommand docCommand = new UserRegistration.UserDocCommand();
    private long downloadId;


    public ResellerProductRequestsListAdapter(Activity context, WalletAccountTransactionLogVo[] warehouseArray) {
        super(context, R.layout.item_prooduct_requests, warehouseArray);
        this.walletAccountTransactionLogVos = warehouseArray;
        this.activity = context;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {

            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_prooduct_requests, null, true);
        }
        TextView fromBinName = (TextView) rowView.findViewById(R.id.from_bin_name);
        TextView reqStatus = (TextView) rowView.findViewById(R.id.requested_status);
        TextView reqDate = (TextView) rowView.findViewById(R.id.requested_date);

        TextView toBinName = (TextView) rowView.findViewById(R.id.to_bin_name);
        TextView quantity = (TextView) rowView.findViewById(R.id.quantity);

        linearlayout1 = (LinearLayout)rowView.findViewById(R.id.linearlayout1);

        Button rejectButton = (Button)rowView.findViewById(R.id.reject_button);
        Button fulfillmentButton = (Button)rowView.findViewById(R.id.approve_button);
        Button deliveryNote = (Button)rowView.findViewById(R.id.delivery_note);
        Button uploadDeliveryNote = (Button)rowView.findViewById(R.id.upload_delivery_note);

        final WalletAccountTransactionLogVo rowItem = (WalletAccountTransactionLogVo) getItem(position);

        if (rowItem.fromBinName != null) {
            fromBinName.setText(rowItem.fromBinName.toString());
        } else {
            fromBinName.setText("");
        }

        if (rowItem.toBinName != null) {
            toBinName.setText(rowItem.toBinName.toString());
        } else {
            toBinName.setText("");
        }

        if (!rowItem.requestStatus.toString().isEmpty()) {
            reqStatus.setText(rowItem.requestStatus.toString());
        } else {
            reqStatus.setText("");
        }

        if (rowItem.requestedDate != null) {
            reqDate.setText(rowItem.requestedDate.toString());
        } else {
            reqDate.setText("");
        }

        if(rowItem.movementRequests != null){
            if(rowItem.movementRequests.size() != 0){
                WalletAccountTransactionLogVo.StockMovementLogVo logVo = new WalletAccountTransactionLogVo.StockMovementLogVo();
                logVo = rowItem.movementRequests.get(0);

                if(logVo.inwardQuantity!= null){
                    quantity.setText(logVo.inwardQuantity.toString());
                }else{
                    quantity.setText("");
                }

            }else{
                quantity.setText("");

            }

        }

        if(rowItem.requestStatus.equals("Requested")){
            fulfillmentButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
            deliveryNote.setVisibility(View.GONE);
            uploadDeliveryNote.setVisibility(View.GONE);
        }else if(rowItem.requestStatus.equals("Approved")){
            fulfillmentButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            deliveryNote.setVisibility(View.VISIBLE);
            uploadDeliveryNote.setVisibility(View.VISIBLE);
        }else if(rowItem.requestStatus.equals("Cancelled") || rowItem.requestStatus.equals("Rejected") ){
            fulfillmentButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            deliveryNote.setVisibility(View.GONE);
            uploadDeliveryNote.setVisibility(View.GONE);
        }else if(rowItem.requestStatus.equals("Completed")){
            fulfillmentButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            deliveryNote.setVisibility(View.VISIBLE);
            deliveryNote.setText("View Delivery Note");
            uploadDeliveryNote.setVisibility(View.GONE);
        }
        
        fulfillmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();

                if(rowItem.requestId != null) {
                    try{
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");
                        serviceHandler.fulfillMainGodownProductRequest(rowItem.requestId.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);

                                if (userLogin.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setIcon(R.drawable.success_icon);
                                    alertBuilder.setTitle("Success!");
                                    alertBuilder.setMessage("Request Fulfilled Successfully.");
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
                                BugReport.postBugReport(activity, Constants.emailId,"Status:"+status+"Error"+error,"");
                            }
                        });

                    }catch (IOException io){
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                    }
                }
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                if(rowItem.requestId != null) {
                    try{
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");

                        serviceHandler.rejectProductRequest(rowItem.requestId.toString(), new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                // ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (userLogin.status.equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                    alertBuilder.setIcon(R.drawable.success_icon);
                                    alertBuilder.setTitle("Success!");
                                    alertBuilder.setMessage("Request Rejected successfully.");
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
                                BugReport.postBugReport(activity, Constants.emailId,"Error"+error+",Status:"+status,"Reject Etopup Request" );
                            }
                        });

                    }catch (IOException io){
                        BugReport.postBugReport(activity, Constants.emailId,"Message"+io.getMessage()+",Cause:"+io.getCause(),"" );
                    }
                }
            }
        });


        uploadDeliveryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ResellerUploadDeliveryNoteActivity.class);
                intent.putExtra("requestId",rowItem.requestId);
                activity.startActivity(intent);
            }
        });

        deliveryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
               if(rowItem.deliveryNoteUrl != null){
                   String fileName = rowItem.requestId + "_" + rowItem.toBinName + ".pdf";
                   Uri uri = Uri.parse(rowItem.deliveryNoteUrl.toString());

                   DownloadManager.Request request = new DownloadManager.Request(uri);
                   request.setDescription("Downloading...");
                   request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, fileName);
                   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                   DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                   downloadManager.enqueue(request);
               }else{
                   MyToast.makeMyToast(activity,"URL not found", Toast.LENGTH_SHORT);
               }

            }
        });

        return rowView;
    }

    private void callParentActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerProductRequestsActivity.class);
        activity.startActivity(intent);
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }


}
