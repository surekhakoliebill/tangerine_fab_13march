package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.View.ExpandableNavigationListView;
import com.aryagami.data.ChildModel;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.HeaderModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyBroadCastReceiver;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.List;


public class NavigationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ExpandableNavigationListView navigationExpandableListView;
    private Context context;
    Activity activity = this;
    String staffRegistration;
    TextView resellerName, resellerNameText, userGroupName, version;
    private SharedPreferences sharedPreferences;
    public SharedPreferences.OnSharedPreferenceChangeListener listener;
    ProgressDialog progressDialog,progressDialog1;

    public  void onTrimMemory(int level) {
        System.gc();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_menu_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar12);
        setSupportActionBar(toolbar);
        context = NavigationMainActivity.this;

        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        TextView txtView = new TextView(this);
        version = (TextView) findViewById(R.id.version_id);
        String vName = BuildConfig.VERSION_NAME;
        version.setText(formattedDate+"_"+vName);*/
        //version.setText(vName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab12);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button eVoucherBtn = (Button)findViewById(R.id.e_voucher_btn);
        Button eTopupBtn = (Button)findViewById(R.id.e_topup_btn);
        Button airtimeBtn = (Button)findViewById(R.id.airtime_btn);

        eVoucherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getApplicationContext(), EVoucherRechargeActivity.class);
                startActivity(intent4);
            }
        });
        eTopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getApplicationContext(), ETopupRechargeActivity.class);
                startActivity(intent4);
            }
        });
        airtimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getApplicationContext(), AirtimeVoucherRechargeActivity.class);
                startActivity(intent3);
            }
        });

        navigationExpandableListView = (ExpandableNavigationListView) findViewById(R.id.expandable_navigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout12);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view12);
        navigationView.setNavigationItemSelectedListener(this);

        resellerName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.reseller_name);
        resellerNameText = (TextView)findViewById(R.id.reseller_name_np);
        userGroupName = (TextView)findViewById(R.id.user_group_np);

        if(RegistrationData.getNavigationData() != null){
            resellerNameText.setText(RegistrationData.getNavigationData().company.toString()+" ");
            resellerName.setText(RegistrationData.getNavigationData().company.toString()+" ");
            userGroupName.setText(RegistrationData.getNavigationData().userGroup.toString());
        }else{
            setDisplayValuesFromSharedPreferences();
        }

        try {
                availableMemory();
               // CheckMemory();
            }catch (Exception e){
                e.getMessage();
                BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+"ERROR:"+e.getCause(),"Check Memory!");
            }
        if(UserSession.getUserGroup(activity) != null)
        if(UserSession.getUserGroup(activity).equals("Reseller Distributor")){
              staffRegistration = "Retailer Channel Registration";
        }else{
            staffRegistration = "Distributor Channel Registration";
        }

// Subscriber Registration,
        if(UserSession.getUserGroup(activity).equals("ResellerStaff")){
            navigationExpandableListView.init(this).addHeaderModel(new HeaderModel("Change Password"))
                    .addHeaderModel(new HeaderModel("Registration", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("New Registration"))
                            .addChildModel(new ChildModel("Existing Postpaid Registration"))
                            .addChildModel(new ChildModel("Update Subscriber Information"))
                            .addChildModel(new ChildModel("Update Fingerprint Information"))
                            .addChildModel(new ChildModel("Update Visa Validity")))

                    .addHeaderModel(new HeaderModel("Sim Swap"))
                    .addHeaderModel(new HeaderModel("Logout")).build()
                    .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            if (id == 0) {
                                Intent intent1 = new Intent(getApplicationContext(), ResellerChangePasswordActivity.class);
                                startActivity(intent1);
                            }else if (id == 2) {
                                Intent intent1 = new Intent(getApplicationContext(), NewSimSwapActivity.class);
                                startActivity(intent1);
                            }else if (id == 3) {
                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                try {

                                    progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "please wait, just logging out!");
                                    serviceHandler.logout(new RestServiceHandler.CacheCallback() {
                                        @Override
                                        public void cacheResponse(String responseString) {
                                            if (responseString.contains("success")) {
                                                UserSession.setSessionKey(activity, null);
                                                /*UserSession.setResellerName(activity,null);
                                                UserSession.setUserGroup(activity,null);*/
                                                RegistrationData.setNavigationData(null);
                                                UserSession.setAllUserInformation(activity,null);
                                                Intent intent = new Intent(activity, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                activity.finish();
                                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                            } else {
                                                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                                MyToast.makeMyToast(activity, "Unable to clear Cache, please try again.", Toast.LENGTH_SHORT);
                                            }
                                        }

                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {

                                        }
                                        @Override
                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                            MyToast.makeMyToast(activity,"ERROR:"+error+" "+status, Toast.LENGTH_SHORT);
                                            BugReport.postBugReport(activity, Constants.emailId, "ERROR:"+error+"STATUS:"+status, "Logout: Navigation Activity");
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId, "Cause:"+e.getCause()+"Message:"+e.getMessage(), "NavigationMainActivity.java");
                                }
                            }
                            return false;
                        }
                    })

                    .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition, childPosition);

                            switch (groupPosition) {
                                case 1:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent = new Intent(getApplicationContext(), OnDemandNewOrderActivity.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            Intent inten1 = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderActivity.class);
                                            startActivity(inten1);
                                            break;
                                        case 2:
                                            Intent inten2 = new Intent(getApplicationContext(), EditUserMainActivity.class);
                                            startActivity(inten2);
                                            break;
                                        case 3:
                                            Intent intent3 = new Intent(getApplicationContext(), UploadSubscriberFingerprint.class);
                                            startActivity(intent3);
                                            break;
                                        case 4:
                                            Intent intent4 = new Intent(getApplicationContext(), UpdateVisaValidityActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            return false;
                        }
                    });
        }else if(UserSession.getUserGroup(activity).equals("Reseller Retailer")){

            navigationExpandableListView
                    .init(this)
                    .addHeaderModel(new HeaderModel("Change Password"))
                    .addHeaderModel(new HeaderModel("Registration", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("New Registration"))
                            .addChildModel(new ChildModel("Existing Postpaid Registration"))
                            .addChildModel(new ChildModel("Update Subscriber Information"))
                            .addChildModel(new ChildModel("Update Fingerprint Information"))
                            .addChildModel(new ChildModel("Update Visa Validity")))
                    .addHeaderModel(
                            new HeaderModel("Recharge", R.drawable.navplus, true)
                                    .addChildModel(new ChildModel("Airtime Vouchers"))
                                    .addChildModel(new ChildModel("E-vouchers"))
                    ).addHeaderModel(
                    new HeaderModel("E-TopUps", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("My Wallet"))
                            .addChildModel(new ChildModel("E-TopUp Recharge"))
            ).addHeaderModel(
                    new HeaderModel("Requests", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("Stock Request"))

            )
                    .build()
                    .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition);
                            if (id == 0) {
                                Intent intent1 = new Intent(getApplicationContext(), ResellerChangePasswordActivity.class);
                                startActivity(intent1);
                            } else if (id == 7) {
                                Intent intent1 = new Intent(getApplicationContext(), StaffByResellerActivity.class);
                                startActivity(intent1);
                            } else if (id == 5) {
                                Intent intent = new Intent(getApplicationContext(), WarehouseManagementActivity.class);
                                startActivity(intent);
                            } else if (id == 6) {
                                Intent intent1 = new Intent(getApplicationContext(), NewStaffUserRegistration.class);
                                startActivity(intent1);
                            }else if (id == 8) {
                                Intent intent1 = new Intent(getApplicationContext(), BuildVersion.class);
                                startActivity(intent1);
                            } else if (id == 9) {

                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                try {
                                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, just logging out!");

                                    serviceHandler.logout(new RestServiceHandler.CacheCallback() {
                                        @Override
                                        public void cacheResponse(String responseString) {
                                            if (responseString.contains("success")) {
                                                UserSession.setSessionKey(activity, null);
                                                RegistrationData.setNavigationData(null);
                                                UserSession.setAllUserInformation(activity,null);
                                                Intent intent = new Intent(activity, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                activity.finish();
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            } else {
                                                MyToast.makeMyToast(activity, "Please try again!", Toast.LENGTH_SHORT);
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            }
                                        }

                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {

                                        }

                                        @Override
                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                            // MyToast.makeMyToast(activity,"ERROR:"+error+" "+status,Toast.LENGTH_SHORT);
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            BugReport.postBugReport(activity, Constants.emailId, "ERROR:"+error+",STATUS:"+status, "NavigationMainActivity.java");

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId, "Cause:"+e.getCause()+"Message:"+e.getMessage(), "NavigationMainActivity.java");
                                }
                            }
                            return false;
                        }
                    })
                    .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition, childPosition);

                            switch (groupPosition) {
                                case 1:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent = new Intent(getApplicationContext(), OnDemandNewOrderActivity.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            Intent inten1 = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderActivity.class);
                                            startActivity(inten1);
                                            break;
                                        case 2:
                                            Intent inten2 = new Intent(getApplicationContext(), EditUserMainActivity.class);
                                            startActivity(inten2);
                                            break;
                                        case 3:
                                            Intent intent3 = new Intent(getApplicationContext(), UploadSubscriberFingerprint.class);
                                            startActivity(intent3);
                                            break;
                                        case 4:
                                            Intent intent4 = new Intent(getApplicationContext(), UpdateVisaValidityActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 2:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), AirtimeVoucherRechargeActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), EVoucherRechargeActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 3:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), ResellerWalletBalanceActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), ETopupRechargeActivity.class);
                                            startActivity(intent4);
                                            break;

                                    }
                            }
                            switch (groupPosition) {
                                case 4:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), ResellerStockRequestsActivity.class);
                                            startActivity(intent3);
                                            break;
                                    }
                            }
                            return false;
                        }
                    })
                    .addHeaderModel(new HeaderModel("My Bin"))
                    .addHeaderModel(new HeaderModel(staffRegistration))
                    .addHeaderModel(new HeaderModel("Staff Users"))
                    .addHeaderModel(new HeaderModel("App Version"))
                    .addHeaderModel(new HeaderModel("Logout"));

        } else if(UserSession.getUserGroup(activity).equals("Reseller Distributor")){
            navigationExpandableListView
                    .init(this)
                    .addHeaderModel(new HeaderModel("Change Password"))
                    .addHeaderModel(new HeaderModel("Registration", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("New Registration"))
                            .addChildModel(new ChildModel("Existing Postpaid Registration"))
                            .addChildModel(new ChildModel("Update Subscriber Information"))
                            .addChildModel(new ChildModel("Update Fingerprint Information"))
                            .addChildModel(new ChildModel("Update Visa Validity")))
                    .addHeaderModel(
                            new HeaderModel("Recharge", R.drawable.navplus, true)
                                    .addChildModel(new ChildModel("Airtime Vouchers"))
                                    .addChildModel(new ChildModel("E-vouchers"))
                    ).addHeaderModel(
                    new HeaderModel("E-TopUps", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("My Wallet"))
                            .addChildModel(new ChildModel("E-TopUp Recharge"))
                    ).addHeaderModel(
                    new HeaderModel("Requests", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("Stock Request"))
                            .addChildModel(new ChildModel("Retailer E-Topup Wallet Requests"))
                            .addChildModel(new ChildModel("Retailer Physical Product Requests"))
                            .addChildModel(new ChildModel("Retailer Voucher Requests"))
                     )
                    .build()
                    .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition);
                            if (id == 0) {
                                Intent intent1 = new Intent(getApplicationContext(), ResellerChangePasswordActivity.class);
                                startActivity(intent1);
                            } else if (id == 7) {
                                Intent intent1 = new Intent(getApplicationContext(), StaffByResellerActivity.class);
                                startActivity(intent1);
                            } else if (id == 5) {
                                Intent intent = new Intent(getApplicationContext(), WarehouseManagementActivity.class);
                                startActivity(intent);
                            } else if (id == 6) {
                                Intent intent1 = new Intent(getApplicationContext(), NewStaffUserRegistration.class);
                                startActivity(intent1);
                            }else if (id == 8) {
                                Intent intent1 = new Intent(getApplicationContext(), BuildVersion.class);
                                startActivity(intent1);
                            } else if (id == 9) {

                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                try {
                                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, just logging out!");

                                    serviceHandler.logout(new RestServiceHandler.CacheCallback() {
                                        @Override
                                        public void cacheResponse(String responseString) {
                                            if (responseString.contains("success")) {
                                                UserSession.setSessionKey(activity, null);
                                                RegistrationData.setNavigationData(null);
                                                UserSession.setAllUserInformation(activity,null);
                                                Intent intent = new Intent(activity, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                activity.finish();
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            } else {
                                                MyToast.makeMyToast(activity, "Please try again!", Toast.LENGTH_SHORT);
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            }
                                        }

                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {

                                        }

                                        @Override
                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                            // MyToast.makeMyToast(activity,"ERROR:"+error+" "+status,Toast.LENGTH_SHORT);
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            BugReport.postBugReport(activity, Constants.emailId, "ERROR:"+error+",STATUS:"+status, "NavigationMainActivity.java");

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId, "Cause:"+e.getCause()+"Message:"+e.getMessage(), "NavigationMainActivity.java");
                                }
                            }
                            return false;
                        }
                    })
                    .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition, childPosition);

                            switch (groupPosition) {
                                case 1:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent = new Intent(getApplicationContext(), OnDemandNewOrderActivity.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            Intent inten1 = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderActivity.class);
                                            startActivity(inten1);
                                            break;
                                        case 2:
                                            Intent inten2 = new Intent(getApplicationContext(), EditUserMainActivity.class);
                                            startActivity(inten2);
                                            break;
                                        case 3:
                                            Intent intent3 = new Intent(getApplicationContext(), UploadSubscriberFingerprint.class);
                                            startActivity(intent3);
                                            break;
                                        case 4:
                                            Intent intent4 = new Intent(getApplicationContext(), UpdateVisaValidityActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 2:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), AirtimeVoucherRechargeActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), EVoucherRechargeActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 3:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), ResellerWalletBalanceActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), ETopupRechargeActivity.class);
                                            startActivity(intent4);
                                            break;

                                    }
                            }
                            switch (groupPosition) {
                                case 4:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), ResellerStockRequestsActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), ResellerETopupRequestsActivity.class);
                                            startActivity(intent4);
                                            break;
                                        case 2:
                                            Intent intent5 = new Intent(getApplicationContext(), ResellerProductRequestsActivity.class);
                                            startActivity(intent5);
                                            break;
                                        case 3:
                                            Intent intent6 = new Intent(getApplicationContext(), ResellerVoucherRequestsActivity.class);
                                            startActivity(intent6);
                                            break;

                                    }
                            }
                            return false;
                        }
                    })
                    .addHeaderModel(new HeaderModel("My Bin"))
                    .addHeaderModel(new HeaderModel(staffRegistration))
                    .addHeaderModel(new HeaderModel("Staff Users"))
                    .addHeaderModel(new HeaderModel("App Version"))
                    .addHeaderModel(new HeaderModel("Logout"));

        } else if(UserSession.getUserGroup(activity).equals("Reseller Aggregator")) {
            navigationExpandableListView
                    .init(this)
                    .addHeaderModel(new HeaderModel("Change Password"))
                    .addHeaderModel(new HeaderModel("Registration", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("New Registration"))
                            .addChildModel(new ChildModel("Existing Postpaid Registration"))
                            .addChildModel(new ChildModel("Update Subscriber Information"))
                            .addChildModel(new ChildModel("Update Fingerprint Information"))
                            .addChildModel(new ChildModel("Update Visa Validity")))
                    .addHeaderModel(
                            new HeaderModel("Recharge", R.drawable.navplus, true)
                                    .addChildModel(new ChildModel("Airtime Vouchers"))
                                    .addChildModel(new ChildModel("E-vouchers"))
                    ).addHeaderModel(
                    new HeaderModel("E-TopUps", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("My Wallet"))
                            .addChildModel(new ChildModel("E-TopUp Recharge"))
                    ).addHeaderModel(
                    new HeaderModel("Requests", R.drawable.navplus, true)
                            .addChildModel(new ChildModel("Distributor E-Topup Wallet Requests"))
                            .addChildModel(new ChildModel("Distributor Physical Product Requests"))
                            .addChildModel(new ChildModel("Distributor Voucher Requests"))
                    )
                    .build()
                    .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition);
                            if (id == 0) {
                                Intent intent1 = new Intent(getApplicationContext(), ResellerChangePasswordActivity.class);
                                startActivity(intent1);
                            } else if (id == 7) {
                                Intent intent1 = new Intent(getApplicationContext(), StaffByResellerActivity.class);
                                startActivity(intent1);
                            } else if (id == 5) {
                                Intent intent = new Intent(getApplicationContext(), WarehouseManagementActivity.class);
                                startActivity(intent);
                            } else if (id == 6) {
                                Intent intent1 = new Intent(getApplicationContext(), NewStaffUserRegistration.class);
                                startActivity(intent1);
                            }else if (id == 8) {
                                Intent intent1 = new Intent(getApplicationContext(), SelectReportActivity.class);
                                startActivity(intent1);
                            } else if (id == 9) {
                                Intent intent1 = new Intent(getApplicationContext(), BuildVersion.class);
                                startActivity(intent1);
                            }else if (id == 10) {
                                Intent intent1 = new Intent(getApplicationContext(), BundleRechargeActivity.class);
                                startActivity(intent1);
                            }else if (id == 10) {

                                RestServiceHandler serviceHandler = new RestServiceHandler();
                                try {
                                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait, just logging out!");

                                    serviceHandler.logout(new RestServiceHandler.CacheCallback() {
                                        @Override
                                        public void cacheResponse(String responseString) {
                                            if (responseString.contains("success")) {
                                                UserSession.setSessionKey(activity, null);
                                                RegistrationData.setNavigationData(null);
                                                UserSession.setAllUserInformation(activity,null);
                                                Intent intent = new Intent(activity, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                activity.finish();
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            } else {
                                                MyToast.makeMyToast(activity, "Please try again!", Toast.LENGTH_SHORT);
                                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            }
                                        }

                                        @Override
                                        public void success(DataModel.DataType type, List<DataModel> data) {

                                        }

                                        @Override
                                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                                           // MyToast.makeMyToast(activity,"ERROR:"+error+" "+status,Toast.LENGTH_SHORT);
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            BugReport.postBugReport(activity, Constants.emailId, "ERROR:"+error+",STATUS:"+status, "NavigationMainActivity.java");

                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    BugReport.postBugReport(activity, Constants.emailId, "Cause:"+e.getCause()+"Message:"+e.getMessage(), "NavigationMainActivity.java");
                                }
                            }
                            return false;
                        }
                    })
                    .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            navigationExpandableListView.setSelected(groupPosition, childPosition);

                            switch (groupPosition) {
                                case 1:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent = new Intent(getApplicationContext(), OnDemandNewOrderActivity.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            Intent inten1 = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderActivity.class);
                                            startActivity(inten1);
                                            break;
                                        case 2:
                                            Intent inten2 = new Intent(getApplicationContext(), EditUserMainActivity.class);
                                            startActivity(inten2);
                                            break;
                                        case 3:
                                            Intent intent3 = new Intent(getApplicationContext(), UploadSubscriberFingerprint.class);
                                            startActivity(intent3);
                                            break;
                                        case 4:
                                            Intent intent4 = new Intent(getApplicationContext(), UpdateVisaValidityActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 2:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), AirtimeVoucherRechargeActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), EVoucherRechargeActivity.class);
                                            startActivity(intent4);
                                            break;
                                    }
                            }
                            switch (groupPosition) {
                                case 3:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent3 = new Intent(getApplicationContext(), ResellerWalletBalanceActivity.class);
                                            startActivity(intent3);
                                            break;
                                        case 1:
                                            Intent intent4 = new Intent(getApplicationContext(), ETopupRechargeActivity.class);
                                            startActivity(intent4);
                                            break;

                                    }
                            }
                            switch (groupPosition) {
                                case 4:
                                    switch (childPosition) {
                                        case 0:
                                            Intent intent4 = new Intent(getApplicationContext(), ResellerETopupRequestsActivity.class);
                                            startActivity(intent4);
                                            break;
                                        case 1:
                                            Intent intent5 = new Intent(getApplicationContext(), ResellerProductRequestsActivity.class);
                                            startActivity(intent5);
                                            break;
                                        case 2:
                                            Intent intent6 = new Intent(getApplicationContext(), ResellerVoucherRequestsActivity.class);
                                            startActivity(intent6);
                                            break;

                                    }
                            }
                            return false;
                        }
                    })
                    .addHeaderModel(new HeaderModel("My Bin"))
                    .addHeaderModel(new HeaderModel(staffRegistration))
                    .addHeaderModel(new HeaderModel("Staff Users"))
                    .addHeaderModel(new HeaderModel("Sales Report"))
                    .addHeaderModel(new HeaderModel("App Version"))
                    .addHeaderModel(new HeaderModel("Bundle Recharge"))
                    .addHeaderModel(new HeaderModel("Logout"));
        }
        getAllRoles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(NavigationMainActivity.this);
    }

    private void setDisplayValuesFromSharedPreferences() {

         sharedPreferences = this.getSharedPreferences(Constants.USERSESSIONINFO,MODE_PRIVATE);

         listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Implementation
                if(prefs.contains("resellerName")){
                    resellerName.setText(UserSession.getResellerName(activity)+" ");
                    resellerNameText.setText(UserSession.getResellerName(activity)+" ");
                }
                if(prefs.contains("UserGroup")){
                    userGroupName.setText(UserSession.getUserGroup(activity)+" ");
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void availableMemory() {
        try {
            ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
            if (!memoryInfo.lowMemory) {
            } else {
                MyToast.makeMyToast(activity, "Low Memory, Please clear the memory for Tangerine Android Application.", Toast.LENGTH_LONG).show();
                Thread.sleep(5000);
                ReDirectToParentActivity.callLoginActivity(activity);
            }

        } catch (Exception e) {
            Toast.makeText(activity, "Low Memory", Toast.LENGTH_LONG).show();
            BugReport.postBugReport(activity, Constants.emailId,"Message:"+ e.getMessage() + " " + e.getCause() + " " + e.getStackTrace(),"MAIN ACTIVITY");
           // userName.setText("Error Crash--- " + e.getMessage() + " " + e.getCause() + " " + e.getStackTrace());
        } catch (Error error) {
            BugReport.postBugReport(activity, Constants.emailId,"Message:"+ error.getMessage() + " " + error.getCause() + " " + error.getStackTrace(),"MAIN ACTIVITY");
        }
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private void getAllRoles() {
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
                                }
                            }else if(role.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity,"GET ROLES:"+role.status, Toast.LENGTH_SHORT);
                            }
                        }
                    }else{
                        MyToast.makeMyToast(activity,"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                  //  MyToast.makeMyToast(activity, "STATUS: "+status+"\n ERROR"+error, Toast.LENGTH_SHORT);
                    BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+" \n STATUS"+status,"NAVIGATION MENU \t getRoleDetails/");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getMessage()+" "+e.getCause(),"NAVIGATION MENU \t getRoleDetails/");
        }
    }

    private void uploadCacheDataInTheBackground() {
        Intent intent = new Intent(activity, MyBroadCastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity, 98765, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + (1000), pendingIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout12);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout12);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        uploadCacheDataInTheBackground();
        super.onStart();
    }
}
