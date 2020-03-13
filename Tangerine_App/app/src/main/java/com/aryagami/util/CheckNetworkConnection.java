package com.aryagami.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckNetworkConnection {

    public static void cehckNetwork(final Activity activity) {

     if(haveNetwork(activity)){

     }else if (!haveNetwork(activity)) {
         //MyToast.makeMyToast(activity, "Network connection is not available", Toast.LENGTH_SHORT).show();

             AlertDialog.Builder builder = new AlertDialog.Builder(activity);
             builder.setTitle("No Internet Connection!");
             builder.setMessage("You need to have Mobile Data or Wifi to access this. press ok to Exit");

             builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             });
              builder.create().show();

     }
    }

    public static boolean haveNetwork( Activity activity){
        boolean have_WIFI=false;
        boolean have_MobileData=false;

        ConnectivityManager connectivityManager=(ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos=connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfos)
        {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    have_MobileData=true;
        }
        return have_MobileData||have_WIFI;
    }



}
