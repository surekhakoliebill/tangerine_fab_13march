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
import android.widget.Button;
import android.widget.EditText;
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
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.GpsUtils;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class ETopupRechargeActivity extends AppCompatActivity {

    Button findSubscriptionBtn, rechargeButton;
    TextView msisdnText, nameText, balanceText, statusText;
    TextInputEditText msisdnEditText;
    Activity activity = this;
    ProgressDialog progressDialog;
    ListView vouchersList;
    String MSISDN = "";
    LinearLayout amountLayout;
    String amountValue;
    String subscriptionId;
    ImageButton backImageButton;
    Button currentLocation;
    Boolean isGPS = false;

    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();

    // for GPS Enable & Capture Location coordinates

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isContinue = false;



    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        findSubscriptionBtn = (Button)findViewById(R.id.check_subscription_btn);
        rechargeButton = (Button) findViewById(R.id.place_order_device);
        msisdnText = (TextView)findViewById(R.id.msisdn_value);
        nameText = (TextView)findViewById(R.id.name_value);
        statusText = (TextView)findViewById(R.id.status_value);
        msisdnEditText = (TextInputEditText)findViewById(R.id.serverdMSISDN_eText);
        vouchersList = (ListView)findViewById(R.id.vouchers_list);
        amountLayout = (LinearLayout)findViewById(R.id.amount_layout);
        final EditText amount = (EditText) findViewById(R.id.E_topup_amount);
        currentLocation = (Button)findViewById(R.id.current_location);
       // this.txtLocation = (TextView) findViewById(R.id.txtLocation);

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

        amountLayout.setVisibility(View.GONE);
        rechargeButton.setVisibility(View.GONE);

        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coordinates!= null){
                    if(coordinates.longitudeValue != null && coordinates.latitudeValue != null){
                        if(amount.getText().toString().isEmpty()){
                            MyToast.makeMyToast(activity,"Please Enter The Amount", Toast.LENGTH_SHORT);
                        }else{
                            amountValue  = amount.getText().toString();
                            RestServiceHandler serviceHandler = new RestServiceHandler();
                            try {
                                progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
                                serviceHandler.postAggregatorTopup(coordinates, UserSession.getResellerId(activity), subscriptionId, amountValue, new RestServiceHandler.Callback() {
                                    @Override
                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                        if (isFinishing()) {
                                            return;
                                        }

                                        UserLogin amountCredit = (UserLogin) data.get(0);
                                        if (amountCredit.status.equals("success")) {

                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setIcon(R.drawable.success_icon);
                                            alertDialog.setMessage(getResources().getString(R.string.amount_credit));
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            startParentActivity();
                                                        }
                                                    });
                                            alertDialog.show();

                                        } else {
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            if (amountCredit.status.equals("INVALID_SESSION")) {
                                                ReDirectToParentActivity.callLoginActivity(activity);
                                            } else {

                                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                alertDialog.setCancelable(false);
                                                alertDialog.setTitle("Message!");
                                                alertDialog.setMessage("STATUS:" + amountCredit.status.toString());
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
                                BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Activity");
                            }
                        }
                    }
                }else{
                    MyToast.makeMyToast(activity,"Please turn on GPS Location.", Toast.LENGTH_SHORT);
                }



            }
        });


        findSubscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(msisdnEditText.getText().toString().isEmpty() | msisdnEditText.getText().length() != 9){
                    MyToast.makeMyToast(activity,"Please enter correct MSISDN Number", Toast.LENGTH_SHORT);
                }else {

                    if (!isGPS) {
                        MyToast.makeMyToast(ETopupRechargeActivity.this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                        return;
                    }

                        MSISDN = "256"+msisdnEditText.getText().toString().trim();
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");
                        RestServiceHandler serviceHandler = new RestServiceHandler();

                        try {
                            serviceHandler.checkSubscription(MSISDN, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    final UserLogin userLogin = (UserLogin) data.get(0);
                                    subscriptionId = userLogin.subscriptionId;
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

                                                        amountLayout.setVisibility(View.VISIBLE);
                                                        rechargeButton.setVisibility(View.VISIBLE);

                                                    }
                                                });
                                        alertDialog.show();
                                    } else {
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
                                                            amountLayout.setVisibility(View.INVISIBLE);
                                                            rechargeButton.setVisibility(View.INVISIBLE);
                                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
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
                            BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"Activity");
                        }

                }
            }
        });



        /*locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        coordinates.latitudeValue = location.getLatitude();
                        coordinates.longitudeValue = location.getLongitude();
               MyToast.makeMyToast(activity, coordinates.latitudeValue+"L"+coordinates.longitudeValue, Toast.LENGTH_SHORT);
                      //  txtLocation.setText(String.format(Locale.US, "%s L %s", coordinates.latitudeValue, coordinates.longitudeValue));

                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };*/
    }



   /* private void getCurrentLocation() {

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
                       // MyToast.makeMyToast(activity,"Latitude"+mlocation.getLatitude()+", Long:"+mlocation.getLongitude(),Toast.LENGTH_SHORT);

                        amountLayout.setVisibility(View.VISIBLE);
                        rechargeButton.setVisibility(View.VISIBLE);
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
        CheckNetworkConnection.cehckNetwork(ETopupRechargeActivity.this);
    }

    private void startParentActivity() {
        Intent intent = new Intent(activity, ETopupRechargeActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activity.finish();
        Intent intent = new Intent(activity, ETopupRechargeActivity.class);
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
        if (ActivityCompat.checkSelfPermission(ETopupRechargeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ETopupRechargeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ETopupRechargeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
            return;

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {

                mFusedLocationClient.getLastLocation().addOnSuccessListener(ETopupRechargeActivity.this, location -> {
                    if (location != null) {
                        coordinates.latitudeValue = location.getLatitude();
                        coordinates.longitudeValue = location.getLongitude();

                     //   MyToast.makeMyToast(activity, coordinates.latitudeValue+" * "+coordinates.longitudeValue, Toast.LENGTH_SHORT);

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
                        //  txtLocation.setText(String.format(Locale.US, "%s L %s", coordinates.latitudeValue, coordinates.longitudeValue));

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
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(ETopupRechargeActivity.this, location -> {
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