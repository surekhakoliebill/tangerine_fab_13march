package com.docrecog.scan;

import android.app.Application;
import android.content.Context;

//import org.acra.ACRA;
//import org.acra.ReportingInteractionMode;
//import org.acra.annotation.ReportsCrashes;

/**
 * Created by royalone on 2017-03-03.
 */
/*@ReportsCrashes(mailTo = "tianzheng511@qq.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)*/
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
