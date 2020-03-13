package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.ActivateCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.Subscription;
import com.aryagami.data.UserInfo;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.NavigationMainActivity;
import com.aryagami.tangerine.activities.UpdateVisaActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForeignUsersListingAdapter extends ArrayAdapter {

    UserRegistration[] userRegistrations;
    public Activity activity = null;
    LinearLayout linearlayout1;
    int mYear,mMonth,mDay;
    ProgressDialog progressDialog;
    Subscription[] subscriptionArray,inactiveSubscriptionsArray;
    UserLogin userLoginDetails;
    List<Subscription> inactiveSubscriptionVisaExpire = new ArrayList<Subscription>();
    int activatedCount = 0;
    Date date2;
    DateFormat dateFormat;

    public ForeignUsersListingAdapter(Activity activity, UserRegistration[] usersArray) {
        super(activity, R.layout.item_list_for_foreigner_user,usersArray);
        this.activity = activity;
        this.userRegistrations = usersArray;
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //  final PlanGroupDetail.PlanGroups item = (PlanGroupDetail.PlanGroups) getItem(position);
        final View rowView;

        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_for_foreigner_user, null, true);
        }
        final UserRegistration rowItem = (UserRegistration) getItem(position);
        linearlayout1 = (LinearLayout)rowView.findViewById(R.id.linearlayout1);

        if(rowItem != null) {
            TextView username = (TextView) rowView.findViewById(R.id.username_value);
            if (rowItem.userName != null) {
                username.setText(rowItem.userName.toString());
            } else {
                username.setText("");
            }

            TextView fullname = (TextView) rowView.findViewById(R.id.fullname_value);
            if (rowItem.fullName != null) {
                fullname.setText( rowItem.fullName.toString());
            } else {
                fullname.setText("");
            }

            //  speedText.setText(""+planGroup.planSpeed);
            TextView email = (TextView) rowView.findViewById(R.id.email_value);


            if (rowItem.email!= null) {
                email.setText(rowItem.email.toString());
            } else {
                email.setText("");
            }

            TextView userGroup = (TextView) rowView.findViewById(R.id.usergroup_value);
            if (rowItem.userGroup != null) {
                userGroup.setText(rowItem.userGroup.toString());
            } else {
                userGroup.setText("");
            }

            TextView validityDate = (TextView) rowView.findViewById(R.id.visa_validity_value);
            if (rowItem.visaValidityDate != null) {

                 dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                String[] issueDate = rowItem.visaValidityDate.toString().split(" ");
                try {
                     date2=new SimpleDateFormat("yyyy-MM-dd").parse(issueDate[0]);
                    validityDate.setText(dateFormat.format(date2).toString());
                    // System.out.println(dateFormat.format(date2).toString());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               // validityDate.setText(rowItem.visaValidityDate.toString());
            } else {
                validityDate.setText("");
            }

            TextView status = (TextView) rowView.findViewById(R.id.status_value);

            if(rowItem.isActive!= null){
                if(rowItem.isActive){
                    status.setText("Active");
                }else{
                    status.setText("Inactive");
                }
            }

            Button updateVisa = (Button) rowView.findViewById(R.id.update_visa);

            /*DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = new Date();


            if (dateFormat1.format(date1).compareTo(dateFormat.format(date2)) > 0) {
               // System.out.println("Date1 is after Date2");
               // updateVisa.setText("AFTER");
                updateVisa.setVisibility(View.GONE);

            } else if (dateFormat1.format(date1).compareTo(dateFormat.format(date2)) < 0) {
                //System.out.println("Date1 is before Date2");
                updateVisa.setVisibility(View.VISIBLE);
            } else if (dateFormat1.format(date1).compareTo(dateFormat.format(date2)) == 0) {
                updateVisa.setVisibility(View.VISIBLE);
            }*/

            updateVisa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(activity, UpdateVisaActivity.class);
                    i.putExtra("REGISTRATIOIN",rowItem);
                    activity.startActivity(i);



                   /* LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View customView = layoutInflater.inflate(R.layout.popup_window_for_update_visa_validity,null);

                    Button cancel = (Button) customView.findViewById(R.id.cancel_btn);
                    Button submit = (Button) customView.findViewById(R.id.submit_btn);
                    Button captureVisa = (Button)customView.findViewById(R.id.capture_visa);
                    final TextInputEditText visaExpiryDate = (TextInputEditText) customView.findViewById(R.id.validity_date);

                    visaExpiryDate.setInputType(InputType.TYPE_NULL);
                    visaExpiryDate.requestFocus();
                    visaExpiryDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);
                            mMonth = c.get(Calendar.MONTH);
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            // set day of month , month and year value in the edit text
                                            visaExpiryDate.setText(((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) + "/"
                                                    + ((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1)) + "/" + year);
                                        }
                                    }, mYear, mMonth, mDay);
                            // Disable past dates in Android date picker
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                            datePickerDialog.show();

                        }
                    });

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

                    captureVisa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage(activity);
                        }
                    });
*/
                    /*submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(visaExpiryDate.getText().toString().contains("Validity")){
                                MyToast.makeMyToast(activity,"Please Select Validity Date.", Toast.LENGTH_SHORT);
                                return;
                            }else{
                                rowItem.visaValidityDate = visaExpiryDate.getText().toString();
                            }

                            RestServiceHandler serviceHandler = new RestServiceHandler();
                            try {

                                serviceHandler.postUpdateVisaValidity(rowItem, new RestServiceHandler.Callback() {
                                    @Override
                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                        UserLogin orderDetails = (UserLogin) data.get(0);
                                        if (orderDetails.status.equals("success")) {
                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setMessage("Visa Validity Date Updated Successfully.");
                                            alertDialog.setNeutralButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            popupWindow.dismiss();
                                                            dialog.dismiss();
                                                           getAllSubscriptionDetailsByUser(rowItem.userId);
                                                           // startResellerActivity();
                                                        }
                                                    });

                                            alertDialog.show();
                                        }
                                    }

                                    @Override
                                    public void failure(RestServiceHandler.ErrorCode error, String status) {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });*/
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }


        return rowView;
    }


    public void getAllSubscriptionDetailsByUser(String userID) {
        final List<String> list = new ArrayList<String>();
        RestServiceHandler service = new RestServiceHandler();
        UserInfo userInfo = new UserInfo();
        userInfo.userId = userID.toString();
        //userInfo.userId = "023315e5-39dc-4160-aa83-e9e74ead6dc6";
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity,"please wait......");
            service.getUserAllSubscriptions(userInfo, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    subscriptionArray = new Subscription[data.size()];
                    data.toArray(subscriptionArray);
                   if(subscriptionArray.length != 0){

                       for(Subscription sub : subscriptionArray){
                           if(sub.lastUpdateReason!= null)
                           if(sub.lastUpdateReason.contains("Visa validity expired")){
                             inactiveSubscriptionVisaExpire.add(sub);
                           }
                       }

                            inactiveSubscriptionsArray = new Subscription[inactiveSubscriptionVisaExpire.size()];

                              activateSubscription(inactiveSubscriptionVisaExpire.toArray(inactiveSubscriptionsArray));
                   }else{
                       MyToast.makeMyToast(activity,"Subscription Not Found.", Toast.LENGTH_SHORT);
                   }
                }
                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"Activity");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Activity");
        }

    }

    private void activateSubscription(Subscription[] subscriptionArray) {

     final int count = subscriptionArray.length;

        for(Subscription subscription : subscriptionArray){
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(activity,"please wait...!");

                // for(Subscription subscription = subscriptionArray)
                ActivateCommand command = new ActivateCommand();
                if(subscription.subscriptionId != null){
                    command.activationId = subscription.subscriptionId.toString();
                    command.reason = "Visa Validity Date Updated";
                    command.entityName = "subscription";

                }else{
                    command = null;
                    return;
                }

                if(command != null)
                    serviceHandler.activateSubscriptionStatus(command, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            userLoginDetails = (UserLogin)data.get(0);
                            if(userLoginDetails.status.equals("success")){

                                if(++activatedCount == count){

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Subscription Activated Successfully. \n Current Status: "+userLoginDetails.activationState);
                                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    startResellerActivity();
                                                }
                                            });
                                    alertDialog.show();
                                }

                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                MyToast.makeMyToast(activity,"Subscription not Activated."+userLoginDetails.status.toString(), Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"Activity");
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
                BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Activity");
            }
        }

    }

    private void startResellerActivity() {
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        activity.startActivity(intent);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       *//* Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.vendor_fragment);
        fragment.onActivityResult(requestCode, resultCode, data);*//*
        super.onActivityResult(requestCode, resultCode, data);



    }*/
}
