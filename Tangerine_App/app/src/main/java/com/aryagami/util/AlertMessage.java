package com.aryagami.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.aryagami.R;

public class AlertMessage {

    public static AlertDialog.Builder alertDialogMessage(Activity activity, String title, String message){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        return alertDialog;
    }

    public void onTrimMemory(int level) {
        System.gc();
    }
}
