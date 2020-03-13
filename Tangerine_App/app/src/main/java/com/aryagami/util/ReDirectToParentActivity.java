package com.aryagami.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.aryagami.tangerine.activities.LoginActivity;
import com.aryagami.tangerine.activities.NavigationMainActivity;

public class ReDirectToParentActivity {

   public static void callLoginActivity(Activity activity){
       MyToast.makeMyToast(activity,"INVALID SESSION", Toast.LENGTH_SHORT);
       UserSession.setSessionKey(activity, null);
       UserSession.setAllUserInformation(activity, null);
       UserSession.setResellerName(activity,null);
       UserSession.setUserGroup(activity,null);
       Intent intent = new Intent(activity, LoginActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       activity.startActivity(intent);
       activity.finish();
    }

    public static void callNavigationActivity(Activity activity){
       activity.finish();
       Intent intent = new Intent(activity, NavigationMainActivity.class);
       activity.startActivity(intent);

    }

}
