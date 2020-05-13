package com.aryagami.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.aryagami.R;


/**
 * Created by aryagami on 21/2/17.
 */
public class MarshMallowPermission {

    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int INTERNAL_STORAGE_PERMISSION_REQUEST_CODE= 4;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /*public boolean checkPermissionForInternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_INTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }*/

    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForExternalStorage(){

        final MarshMallowPermission permission = new MarshMallowPermission(activity);
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("External Storage permission needed. Please allow in App Settings for additional functionality.");
            alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    });
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
           // Toast.makeText(activity, activity.getResources().getString(R.string.marshmallow_Camera_permission), Toast.LENGTH_LONG).show();

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Alert");
            alertDialog.setMessage(activity.getResources().getString(R.string.marshmallow_Camera_permission));
            alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                        }
                    });
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }
}
