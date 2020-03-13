package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.OrderNumberDetails;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.util.List;

public class OnDemandExistingPostpaidOrderActivity extends AppCompatActivity {

    Activity activity = this;
    OrderNumberDetails orderNumberDetails = new OrderNumberDetails();
    //Variables
    TextInputEditText registrationNumber, depositValue;
    TextView creditLimit, creditScore, approvedDate, registrationStatus;
    Button saveAndContinue, backButton;
    ImageButton searchButton;
    LinearLayout layout;
    ProgressDialog progressDialog;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ondemand_existing_postpaid_order_activity);

        registrationNumber = (TextInputEditText) findViewById(R.id.register_num);
        depositValue = (TextInputEditText) findViewById(R.id.deposite_value);

        creditLimit = (TextView) findViewById(R.id.credit_limit);
        creditScore = (TextView) findViewById(R.id.credit_score);
        approvedDate = (TextView) findViewById(R.id.approved_date);
        registrationStatus = (TextView) findViewById(R.id.registration_status);

        saveAndContinue = (Button) findViewById(R.id.save_and_continue);
        backButton = (Button) findViewById(R.id.back_btn);
        searchButton = (ImageButton) findViewById(R.id.search_btn);
        layout = (LinearLayout)findViewById(R.id.plan_group_details_layout);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registrationNumber.getText().toString().isEmpty()) {
                    String orderNumber = registrationNumber.getText().toString();
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                    serviceHandler.getOrderNumber(orderNumber, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            if (data.size() != 0) {
                                orderNumberDetails = (OrderNumberDetails) data.get(0);
                                if (orderNumberDetails != null) {
                                    if(orderNumberDetails.status.equals("success")){
                                        if(!orderNumberDetails.currentStatus.equals("Documents Approval Pending")) {
                                            layout.setVisibility(View.VISIBLE);
                                            saveAndContinue.setVisibility(View.VISIBLE);
                                            setOrderDetail(orderNumberDetails);
                                        }else{
                                            layout.setVisibility(View.GONE);
                                            saveAndContinue.setVisibility(View.GONE);
                                            MyToast.makeMyToast(activity,"Status:"+orderNumberDetails.currentStatus.toString(), Toast.LENGTH_SHORT);
                                        }
                                    }else if(orderNumberDetails.status.equals("INVALID_SESSION")){
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    }else{
                                        MyToast.makeMyToast(activity,"Status:"+orderNumberDetails.status, Toast.LENGTH_SHORT);
                                    }
                                }else{
                                    layout.setVisibility(View.GONE);
                                    saveAndContinue.setVisibility(View.GONE);
                                    MyToast.makeMyToast(activity,"Order Details are Empty.", Toast.LENGTH_SHORT);
                                }
                            }else{
                                layout.setVisibility(View.GONE);
                                saveAndContinue.setVisibility(View.GONE);
                                MyToast.makeMyToast(activity,"Data Empty.", Toast.LENGTH_SHORT);
                            }

                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"\n STATUS"+status,"Existing Postpaid Order");
                        }
                    });
                } else {
                    MyToast.makeMyToast(activity, "Please Enter Registration Number", Toast.LENGTH_SHORT);
                }
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!depositValue.getText().toString().isEmpty()){
                    orderNumberDetails.depositValue = Float.parseFloat(depositValue.getText().toString());
                }
                Intent i = new Intent(activity, OnDemandExistingPostpaidOrderAddSubscriptionActivity.class);
                i.putExtra("OrderDetails",orderNumberDetails);
                startActivity(i);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnDemandExistingPostpaidOrderActivity.this, NavigationMainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(OnDemandExistingPostpaidOrderActivity.this);
    }

    public void setOrderDetail(OrderNumberDetails orderDetail) {
        if (orderDetail.currentStatus != null) {
            registrationStatus.setText(orderDetail.currentStatus.toString());
        } else {
            registrationStatus.setText("");
        }

        if (orderDetail.creditScore != null) {
            creditScore.setText(orderDetail.creditScore.toString());
        } else {
            creditScore.setText("");
        }

        if (orderDetail.creditLimit != null) {
            creditLimit.setText(orderDetail.creditLimit.toString());
        } else {
            creditLimit.setText("");
        }

        if (orderDetail.approvedOn != null) {
            approvedDate.setText(orderDetail.approvedOn.toString());
        } else {
            approvedDate.setText("");
        }

        if (orderDetail.depositValue != null) {
            depositValue.setText(orderDetail.depositValue.toString());
        } else {
            depositValue.setText("");
        }
    }
}
