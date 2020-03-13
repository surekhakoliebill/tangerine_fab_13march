package com.aryagami.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;


import com.aryagami.BuildConfig;
import com.aryagami.R;

import java.io.UnsupportedEncodingException;


public class Utils {

    public static final String CONTENT_URI = "content://com.accurascan.demoapp";
    public static final int REQUEST_CAMERA = 101;
    public static final int PERMISSION_CAMERA = 102;
    public static final String FILE_URI = "fileUri";

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getReverseCase(Context context) throws UnsupportedEncodingException {
        String string = context.getString(R.string.base);
        return new String(Base64.decode(string, Base64.NO_WRAP), "UTF-8");
    }

    public static void Log_e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg);
        }
    }
}
