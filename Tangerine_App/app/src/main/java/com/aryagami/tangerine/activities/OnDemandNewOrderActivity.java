package com.aryagami.tangerine.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.AppConstants;
import com.aryagami.data.RegistrationData;
import com.aryagami.tangerine.adapters.ViewPagerAdapter;
import com.aryagami.tangerine.fragments.DeviceOrderFragment;
import com.aryagami.tangerine.fragments.OnDemandPostpaidNewOrderFragment;
import com.aryagami.tangerine.fragments.OnDemandPrepaidNewOrderFragment;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.GPSTracker;
import com.aryagami.util.GpsUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class OnDemandNewOrderActivity extends AppCompatActivity {

    ViewPagerAdapter adapter;
    RelativeLayout bottonTabsLayout;
    TextView headerName;
    TabLayout tabLayout;
    //This is our viewPager
    private ViewPager viewPager;
    Activity activity = this;
    Boolean isGPS= true;
    OnDemandPostpaidNewOrderFragment postpaidNewOrderFragment;
    OnDemandPrepaidNewOrderFragment onDemandPrepaidNewOrderFragment;
    DeviceOrderFragment deviceOrderFragment;
    GPSTracker gps;
    boolean isBound = false;

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code = 107;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 108;

    // for GPS Enable & Capture Location coordinates

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isContinue = false;


    public void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ondemand_new_order_activity);

        RegistrationData.setRefugeeThumbImageDrawable(null);
        RegistrationData.setCapturedFingerprintDrawable(null);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        bottonTabsLayout = (RelativeLayout)findViewById(R.id.bottom_layout);

        headerName = (TextView)findViewById(R.id.header_text);
        headerName.setText("Ondemand New Order");

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OnDemandNewOrderActivity.this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        turnOnGpsLocation();
        getLocation();
        locationCallback();

      //  getCurrentLocation();

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
                    RegistrationData.setCurrentLatitude(mlocation.getLatitude());
                    RegistrationData.setCurrentLongitude(mlocation.getLongitude());

                }
            }
        });

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        turnOnGpsLocation();
        getLocation();
        CheckNetworkConnection.cehckNetwork(OnDemandNewOrderActivity.this);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        postpaidNewOrderFragment=new OnDemandPostpaidNewOrderFragment();
        onDemandPrepaidNewOrderFragment = new OnDemandPrepaidNewOrderFragment();
        // deviceOrderFragment = new DeviceOrderFragment();

       adapter.addFragment(postpaidNewOrderFragment,"POSTPAID");
        adapter.addFragment(onDemandPrepaidNewOrderFragment,"PREPAID");
       // adapter.addFragment(deviceOrderFragment,"DEVICE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
         activity.finish();
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            GPSTracker.MyLocalBinder binder = (GPSTracker.MyLocalBinder) service;
            gps = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, GPSTracker.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(myConnection);
            isBound = false;
        }
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

                        RegistrationData.setCurrentLatitude(location.getLatitude());
                        RegistrationData.setCurrentLongitude(location.getLongitude());

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

                        RegistrationData.setCurrentLatitude(location.getLatitude());
                        RegistrationData.setCurrentLongitude(location.getLongitude());

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

                                    RegistrationData.setCurrentLatitude(location.getLatitude());
                                    RegistrationData.setCurrentLongitude(location.getLongitude());

                                 //   MyToast.makeMyToast(activity, coordinates.latitudeValue+" p "+coordinates.longitudeValue, Toast.LENGTH_SHORT);

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
