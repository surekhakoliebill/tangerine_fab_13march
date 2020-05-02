package com.aryagami.util;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.aryagami.data.BugReportCommand;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;

import java.io.IOException;
import java.util.List;

public class BugReport {
    public static void postBugReport(final Activity activity, String emailIds, String description, String reportArea) {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        BugReportCommand command = new BugReportCommand();
        command.emailIds = emailIds;
        command.reportDescription = description;
        command.reportArea = reportArea+ ",\t SERVER URL:"+ Constants.serverURL;

        try {
            serviceHandler.postBugReport(command, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin response = (UserLogin) data.get(0);
                    if (response.status.equals("success")) {

                        MyToast.makeMyToast(activity, "Bug Reported to the Application Developer.", Toast.LENGTH_SHORT);
                       // ReDirectToParentActivity.callNavigationActivity(activity);
                    } else {
                        if (response.status.equals("INVALID_SESSION")) {
                            ReDirectToParentActivity.callLoginActivity(activity);
                        } else {
                            MyToast.makeMyToast(activity, "Status:"+response.status, Toast.LENGTH_SHORT);
                          //  ReDirectToParentActivity.callNavigationActivity(activity);
                        }
                    }
                }
                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    //MyToast.makeMyToast(activity, "Error:" + error, Toast.LENGTH_SHORT);
                    //ReDirectToParentActivity.callNavigationActivity(activity);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postBugReportFromREST(String emailIds, String description, String reportArea) {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        BugReportCommand command = new BugReportCommand();
        command.emailIds = emailIds;
        command.reportDescription = description;
        command.reportArea = reportArea+ ",\t SERVER URL:"+ Constants.serverURL;

        try {
            serviceHandler.postBugReport(command, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin response = (UserLogin) data.get(0);
                   if(response.status != null)
                    if (response.status.equals("success")) {
                        Log.i("Status", response.status);

                    }else{
                        Log.i("Status", response.status);
                    }
                }
                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    Log.i("Status", status );
                    Log.i("ERROR", error.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
