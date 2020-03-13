package com.aryagami.tangerine.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.AppConstants;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.ResellerVoucherType;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ResellerTopUpVouchersListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.GpsUtils;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EVoucherRechargeActivity extends AppCompatActivity {
    Button findSubscriptionBtn;
    TextView msisdnText, nameText, balanceText, statusText;
    TextInputEditText msisdnEditText;
    Activity activity = this;
    ProgressDialog progressDialog,progressDialog1;
    ListView vouchersList;
    ResellerVoucherType[] vouchersArray, eVouchersArray;
    List<ResellerVoucherType> eVouchersList = new ArrayList<ResellerVoucherType>();
    String MSISDN = "";
    LinearLayout subscriberLayout, listviewLayout;
    TextView voucherTypeList, headerName;
    ImageButton backImageButton;
    Boolean isGPS = false;

    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();

    public  void onTrimMemory(int level) {
        System.gc();
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isContinue = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_voucher);
        findSubscriptionBtn = (Button)findViewById(R.id.check_subscription_btn);
        msisdnText = (TextView)findViewById(R.id.msisdn_value);
        nameText = (TextView)findViewById(R.id.name_value);
        statusText = (TextView)findViewById(R.id.status_value);
        msisdnEditText = (TextInputEditText)findViewById(R.id.serverdMSISDN_eText);
        vouchersList = (ListView)findViewById(R.id.vouchers_list);

        headerName = (TextView)findViewById(R.id.header_name);
        voucherTypeList = (TextView)findViewById(R.id.vouchers_type);
        headerName.setText("E-Voucher Recharge");
        voucherTypeList.setText("E-Vouchers List");

        subscriberLayout = (LinearLayout)findViewById(R.id.subscriber_layout);
        listviewLayout = (LinearLayout)findViewById(R.id.listview_layout);
        subscriberLayout.setVisibility(View.GONE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        turnOnGpsLocation();
        getLocation();
        locationCallback();

        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        findSubscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msisdnEditText.getText().toString().isEmpty() | msisdnEditText.getText().length() != 9){
                    MyToast.makeMyToast(activity,"Please enter correct MSISDN Number", Toast.LENGTH_SHORT);
                }else {
                    MSISDN = "256"+msisdnEditText.getText().toString().trim();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                    RestServiceHandler serviceHandler = new RestServiceHandler();

                    try {
                        serviceHandler.checkSubscription(MSISDN, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                final UserLogin userLogin = (UserLogin) data.get(0);

                                if (userLogin.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Subscription Found!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    setSubscriberData(userLogin);

                                                    setVouchersList();
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("STATUS:" + userLogin.status.toString());
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();

                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"MESSAGE"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Activity");
                    }
                }
            }
        });
    }

 /*   private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mlocation = location;
                    coordinates.latitudeValue = mlocation.getLatitude();
                    coordinates.longitudeValue = mlocation.getLongitude();

                    setVouchersList();
                }
            }
        });


    }*/

    @Override
    protected void onResume() {
        super.onResume();
        turnOnGpsLocation();
        getLocation();
       // locationCallback();
        CheckNetworkConnection.cehckNetwork(EVoucherRechargeActivity.this);
    }

    private void setVouchersList() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, Fetching Vouchers List.");
            serviceHandler.getListVoucherTypes(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    vouchersArray = new ResellerVoucherType[data.size()];
                    vouchersArray = data.toArray(vouchersArray);
                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                    if(vouchersArray.length != 0) {
                        for(ResellerVoucherType voucherType : vouchersArray){
                            if(voucherType.voucherDescription.contains("Airtime") | voucherType.voucherDescription.contains("airtime")){
                              continue;
                            }else{
                                eVouchersList.add(voucherType);
                            }
                        }
                        eVouchersArray = new ResellerVoucherType[eVouchersList.size()];
                        eVouchersList.toArray(eVouchersArray);
                        if(eVouchersArray.length != 0) {
                            listviewLayout.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = new ResellerTopUpVouchersListAdapter(activity, eVouchersArray, MSISDN,"Voucher",coordinates);
                            vouchersList.setAdapter(adapter);
                        }else{
                            MyToast.makeMyToast(activity,"E-Vouchers Are Not Found.", Toast.LENGTH_SHORT);
                        }
                    }else{
                        MyToast.makeMyToast(activity,"Vouchers List is Empty.", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                    BugReport.postBugReport(activity, Constants.emailId,"STATUS"+status+"\n ERROR:-"+error,"E-Voucher Recharge");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"E-Voucher Recharge");
        }

    }

    private void setSubscriberData(UserLogin command) {
        subscriberLayout.setVisibility(View.VISIBLE);
        if(command.status != null){
          statusText.setText(command.status.toString());
        }
        if(command.username != null){
            nameText.setText(command.username.toString());
        }
            msisdnText.setText("256"+msisdnEditText.getText().toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activity.finish();
        Intent intent = new Intent(activity, NavigationMainActivity.class);
        startActivity(intent);
    }


    public void turnOnGpsLocation(){
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
            return;

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {

                mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        coordinates.latitudeValue = location.getLatitude();
                        coordinates.longitudeValue = location.getLongitude();

                      //  MyToast.makeMyToast(activity, coordinates.latitudeValue+" * "+coordinates.longitudeValue, Toast.LENGTH_SHORT);

                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    public void locationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        coordinates.latitudeValue = location.getLatitude();
                        coordinates.longitudeValue = location.getLongitude();

                      //  MyToast.makeMyToast(activity, coordinates.latitudeValue+"L"+coordinates.longitudeValue, Toast.LENGTH_SHORT);

                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {


                        if (isContinue) {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        } else {
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                                if (location != null) {
                                    coordinates.latitudeValue = location.getLatitude();
                                    coordinates.longitudeValue = location.getLongitude();

                                   // MyToast.makeMyToast(activity, coordinates.latitudeValue+" p "+coordinates.longitudeValue, Toast.LENGTH_SHORT);

                                } else {
                                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
}
