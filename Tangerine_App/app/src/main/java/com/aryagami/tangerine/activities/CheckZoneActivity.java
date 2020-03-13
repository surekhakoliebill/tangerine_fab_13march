package com.aryagami.tangerine.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.aryagami.R;
import com.aryagami.data.NewOrderCommand;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class CheckZoneActivity extends FragmentActivity implements OnMapReadyCallback {

    Activity activity = this;
    private GoogleMap mMap;
    Address address;
    FusedLocationProviderClient fusedLocationProviderClient;
    public double latitude;
    public double longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int Request_Code = 101;
    Button backbtn;
    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_zone_map);

        coordinates = (NewOrderCommand.LocationCoordinates) getIntent().getSerializableExtra("location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        backbtn = (Button)findViewById(R.id.back_button);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

       /* latitude = 17.460145;
        longitude = 78.398602;

        //latitude = RegistrationData.getPostLatitude();
        //longitude = RegistrationData.getPostLongitude();
        //GetlastLocation();*/

       checkLocation();
    }

    private void checkLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng latLng = new LatLng(coordinates.latitudeValue, coordinates.longitudeValue);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(""));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        }
                    }
                });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED);
            {
                checkLocation();
            }
            break;
        }
    }

}
