package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.ResellerStaff;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.CheckZoneActivity;
import com.aryagami.tangerine.activities.MapsActivity;
import com.aryagami.tangerine.activities.StaffByResellerActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.util.List;

public class StaffByResellerAdapter extends ArrayAdapter {
    Activity activity = null;
    ResellerStaff[] resellerStaffArray;
    ProgressDialog progressDialog;
    LinearLayout linearlayout1;
    Button bckBtn;

    public StaffByResellerAdapter(Activity context, ResellerStaff[] resellerStaffs) {
        super(context, R.layout.item_list_for_staff_by_reseller,resellerStaffs);
        this.resellerStaffArray = resellerStaffs;
        this.activity = context;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;

        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_for_staff_by_reseller, null, true);
        }
        final ResellerStaff rowItem = (ResellerStaff) getItem(position);

        linearlayout1 = (LinearLayout)rowView.findViewById(R.id.linearlayout1);

        bckBtn = (Button) rowView.findViewById(R.id.back_btn);

         TextView name = (TextView)rowView.findViewById(R.id.name_text);
        TextView code = (TextView)rowView.findViewById(R.id.code_text);
        TextView createdDate = (TextView)rowView.findViewById(R.id.created_date);
        TextView status = (TextView)rowView.findViewById(R.id.status_value);
        Button statusChange = (Button)rowView.findViewById(R.id.activate_btn);
        Button checkZone = (Button)rowView.findViewById(R.id.check_zone);
        Button changeZone = (Button)rowView.findViewById(R.id.change_zone);

        if(rowItem.fullName != null && !rowItem.fullName.isEmpty()){
            name.setText(rowItem.fullName.toString());
        }else{
            name.setText("");
        }
        if(rowItem.userGroup != null && !rowItem.userGroup.isEmpty()){
            code.setText(rowItem.userGroup.toString());
        }else{
            code.setText("");
        }

        if(rowItem.createdOn != null){
            createdDate.setText(rowItem.createdOn.toString());
        }else{
            createdDate.setText("");
        }


        if(rowItem.isActive != null ){

            if(rowItem.isActive){
                status.setText("Active");
                status.setTextColor(Color.GREEN);
                statusChange.setText("DeActivate");
            }else{

                status.setText("DeActive");
                status.setTextColor(Color.RED);
                statusChange.setText("Activate");
            }

        }

        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)view.getContext()).finish();
            }
        });

        checkZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rowItem.resellerSalesZone != null){
                    Intent intent = new Intent(activity, CheckZoneActivity.class);
                    intent.putExtra("location", rowItem.resellerSalesZone);
                    activity.startActivity(intent);
                }else {
                    MyToast.makeMyToast(activity, "Location Coordinates are not found, please Update!", Toast.LENGTH_SHORT);
                }

            }
        });

        changeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rowItem.resellerId != null) {
                    Intent intent = new Intent(activity, MapsActivity.class);
                    intent.putExtra("Activity", "staff");
                    intent.putExtra("resellerId", rowItem.resellerId.toString());
                    activity.startActivity(intent);
                }else{
                    MyToast.makeMyToast(activity,"Unable to update Reseller Staff Location!", Toast.LENGTH_SHORT);
                }
            }
        });


        statusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View customView = layoutInflater.inflate(R.layout.popup_window_for_subscription_status_change,null);

                    Button cancel = (Button) customView.findViewById(R.id.cancel_btn);
                    Button submit = (Button) customView.findViewById(R.id.submit_btn);
                    final EditText reason = (EditText)customView.findViewById(R.id.reason);

                    TextView reasonText = (TextView)customView.findViewById(R.id.reason_text);
                    if(rowItem.isActive){
                        reasonText.setText(" Deactivation  Reason");
                        reasonText.setTextColor(Color.RED);
                    }else{
                        reasonText.setText("Activation Reason");
                        reasonText.setTextColor(Color.GREEN);
                    }

                    //instantiate popup window
                    final PopupWindow popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    //display the popup window
                    popupWindow.showAtLocation(linearlayout1, Gravity.CENTER, 0, 0);
                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.update();

                    //close the popup window on button click

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                             ActivateCommand command = new ActivateCommand();

                            RestServiceHandler serviceHandler = new RestServiceHandler();

                            if(rowItem.isActive){

                                if(rowItem.roleName.equals("Reseller Staff")){
                                    command.entityName = "User";
                                    command.activationId = rowItem.userId.toString();
                                    command.reason = reason.getText().toString();
                                }else if(rowItem.roleName.equals("Reseller Distributor")){
                                    command.entityName = "reseller";
                                    command.activationId = rowItem.resellerId.toString();
                                    command.reason = reason.getText().toString();
                                }else if(rowItem.roleName.equals("Reseller Retailer")){
                                    command.entityName = "reseller";
                                    command.activationId = rowItem.resellerId.toString();
                                    command.reason = reason.getText().toString();
                                }

                                try {

                                    if(!command.reason.isEmpty()) {
                                        progressDialog = ProgressDialogUtil.startProgressDialog(activity,"please wait..!");
                                        serviceHandler.deactivateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                                            @Override
                                            public void success(DataModel.DataType type, List<DataModel> data) {
                                                UserLogin userLoginDetails = (UserLogin) data.get(0);
                                                if (userLoginDetails.status.equals("success")) {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setMessage("Status Updated Successfully.");
                                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.dismiss();
                                                                    Intent intent = new Intent(activity, StaffByResellerActivity.class);
                                                                    activity.startActivity(intent);
                                                                }
                                                            });
                                                    alertDialog.show();

                                                } else {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    if(userLoginDetails.status.equals("INVALID_SESSION")){
                                                        ReDirectToParentActivity.callLoginActivity(activity);
                                                    }else{
                                                        MyToast.makeMyToast(activity, "Status:" + userLoginDetails.status.toString(), Toast.LENGTH_SHORT);

                                                    }
                                                }
                                            }

                                            @Override
                                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"STATUS:"+status,"STAFF USER DE-ACTIVATION");
                                            }
                                        });
                                    }else{
                                        MyToast.makeMyToast(activity,"Please Enter Deactivation Reason.", Toast.LENGTH_SHORT);
                                    }
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+e.getCause()+"MESSAGE:"+e.getMessage(),"STAFF USER DE-ACTIVATION");
                                }

                            }else {

                                if(rowItem.roleName.equals("Reseller Staff")){
                                    command.entityName = "User";
                                    command.activationId = rowItem.userId.toString();
                                    command.reason = reason.getText().toString();
                                }else if(rowItem.roleName.equals("Reseller Distributor")){
                                    command.entityName = "reseller";
                                    command.activationId = rowItem.resellerId.toString();
                                    command.reason = reason.getText().toString();
                                }else if(rowItem.roleName.equals("Reseller Retailer")){
                                    command.entityName = "reseller";
                                    command.activationId = rowItem.resellerId.toString();
                                    command.reason = reason.getText().toString();
                                }


                                try {
                                    if(!command.reason.isEmpty()) {
                                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...!");
                                        serviceHandler.activateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                                            @Override
                                            public void success(DataModel.DataType type, List<DataModel> data) {
                                                UserLogin userLogin = (UserLogin) data.get(0);
                                                if (userLogin.status.equals("success")) {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setMessage("Status Updated Successfully");
                                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.dismiss();
                                                                    Intent intent = new Intent(activity, StaffByResellerActivity.class);
                                                                    activity.startActivity(intent);
                                                                }
                                                            });
                                                    alertDialog.show();
                                                } else {
                                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                    if(userLogin.status.equals("INVALID_SESSION")){
                                                            ReDirectToParentActivity.callLoginActivity(activity);
                                                    }else {
                                                        MyToast.makeMyToast(activity, "STATUS:" + userLogin.status.toString(), Toast.LENGTH_SHORT);
                                                    }
                                                    }
                                            }

                                            @Override
                                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                                BugReport.postBugReport(activity, Constants.emailId,"Error:-" + error+"Status:-" + status,"STAFF USER ACTIVATION");
                                            }
                                        });

                                    }else{
                                        MyToast.makeMyToast(activity,"Please Enter Activation Reason.", Toast.LENGTH_SHORT);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId,"Error:-" + e.getCause()+"Status:-" + e.getMessage(),"STAFF USER ACTIVATION");
                                }
                            }

                        }
                    });

                }
        });

        return rowView;
    }
}


