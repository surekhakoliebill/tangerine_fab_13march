package com.aryagami.tangerine.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.ReDirectToParentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Activity activity = this;

    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code = 101;
    public double latitude;
    public double longitude;
    Button continueBtton;
    Address address;
    Boolean isSearch = false;
    String contentText = "", resellerID;
    NewOrderCommand.LocationCoordinates coordinates = new NewOrderCommand.LocationCoordinates();
    NewOrderCommand.LocationCoordinates coordinates1 = new NewOrderCommand.LocationCoordinates();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);

        searchView = findViewById(R.id.sv_lication);
        continueBtton = (Button)findViewById(R.id.continue_button);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        Bundle bundle = getIntent().getExtras();
        contentText =bundle.getString("Activity");
        resellerID = bundle.getString("resellerId");
        coordinates1 = (NewOrderCommand.LocationCoordinates) getIntent().getSerializableExtra("location");


        if(contentText.contains("map")) {
        continueBtton.setText("Continue");
        }else  if(contentText.contains("staff")){
            continueBtton.setText("Update");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        GetlastLocation();

        continueBtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contentText.contains("map")) {
                    activity.finish();
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                }else  if(contentText.contains("staff")){
                    RestServiceHandler serviceHandler = new RestServiceHandler();

                    coordinates.latitudeValue =  RegistrationData.getPostLatitude();
                    coordinates.longitudeValue =  RegistrationData.getPostLongitude();

                    try {
                        serviceHandler.updateResellerLocation(coordinates, resellerID, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin updateDetails = (UserLogin) data.get(0);

                                if(updateDetails.status.equals("success")){
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Reseller Location Updated Successfully!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    reloadActivity();
                                                }
                                            });
                                    alertDialog.show();

                                }else if(updateDetails.status.equals("INVALID_SESSION")){
                                    ReDirectToParentActivity.callLoginActivity(activity);
                                }else {
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Status: "+updateDetails.status.toString());
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    reloadActivity();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                BugReport.postBugReport(activity, Constants.emailId,"STATUS: "+status+",\t Error: "+error,"Update Reseller Location");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"Message:"+e.getMessage()+",\t Cause:"+e.getCause(),"Update Reseller Location");
                    }

                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addressList.size() !=0) {
                        isSearch=true;
                         address = addressList.get(0);

                        RegistrationData.setPostLatitude(address.getLatitude());
                        RegistrationData.setPostLongitude(address.getLongitude());

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title(location));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }else {

                        RegistrationData.setPostLatitude(mlocation.getLatitude());
                        RegistrationData.setPostLongitude(mlocation.getLongitude());
                        Toast.makeText(activity,"Address not found! please retry", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }

    public void GetlastLocation() {
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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mlocation = location;
                   // Toast.makeText(activity, mlocation.getLatitude()+""+mlocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapsActivity.this);

                    RegistrationData.setPostLatitude(mlocation.getLatitude());
                    RegistrationData.setPostLongitude(mlocation.getLongitude());
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(mlocation!= null) {

            LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            googleMap.addMarker(markerOptions);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED);
                {
                  GetlastLocation();
               }
            break;
        }
    }

  public void reloadActivity(){
        Intent intent = new Intent(activity,StaffByResellerActivity.class);
        activity.startActivity(intent);
  }
}



