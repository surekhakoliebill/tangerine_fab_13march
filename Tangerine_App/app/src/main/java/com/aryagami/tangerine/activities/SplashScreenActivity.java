package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aryagami.R;

/**
 * Created by aryagami on 26/8/17.
 */

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*try {
            CheckNetworkConnection.cehckNetwork(SplashScreenActivity.this);
        }catch (Exception io){
            BugReport.postBugReport(SplashScreenActivity.this, Constants.emailId,"Cause:"+io.getCause()+"  "+io.getMessage(),"Network Connection Check");
        }*/

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
}
