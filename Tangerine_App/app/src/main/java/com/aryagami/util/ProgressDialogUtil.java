package com.aryagami.util;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by cloudkompare eson 27/9/15.
 */
public class ProgressDialogUtil {
    static public ProgressDialog startProgressDialog(Activity activity, String message) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }

    static public void stopProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    static public void updateProgressDialog(ProgressDialog progressDialog, String text) {
        progressDialog.setMessage(text);
    }
}
