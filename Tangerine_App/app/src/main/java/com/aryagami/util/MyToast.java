package com.aryagami.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by aryagami on 18/1/16.
 */
public class MyToast {

    public static Toast makeMyToast(Activity activity, String toastText, int toastLength) {
        /*if(activity.isFinishing()) {
            return null;
        }*/
        toastLength = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(activity, toastText, toastLength);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }
    public static Toast makeMyToast(Context activity, String toastText, int toastLength) {

        toastLength = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(activity, toastText, toastLength);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }
}
