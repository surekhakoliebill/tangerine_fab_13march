package com.aryagami.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aryagami.data.Constants;
import com.aryagami.data.PlanGroup;
import com.aryagami.data.UserRegistration;
import com.google.gson.Gson;

/**
 * Created by aryagami on 6/6/17.
 */
public class UserSession   {
    static private String userId = null;
    static private String userGroup = null;
    static private String sessionKey = null;
    static private String servedMSISDN = null;
    static private String resellerId = null;
    static private String aggregator = null;
    static private String resellerName = null;
    static private Boolean enableMobileMoneyReg = null;
    static private String subscriptionId = null;
    static private UserRegistration allUserInformation= null;

    static SharedPreferences sharedpreferences;

    public static UserRegistration getAllUserInformation(Context context) {
        sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedpreferences.getString("allUserInformation", null);
        allUserInformation = gson.fromJson(json,UserRegistration.class);
        return allUserInformation;
    }

    public static void setAllUserInformation(Context context,UserRegistration allUserInformation) {
        UserSession.allUserInformation = allUserInformation;
        if(allUserInformation != null){
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(allUserInformation);
            editor.putString("allUserInformation", json);
            editor.commit();
        }else{
            UserSession.allUserInformation = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("allUserInformation");
            editor.commit();
        }
    }

    public static void deleteAllUserInformation(Context context) {
        sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("allUserInformation");
        editor.clear();
        editor.commit();
    }


    static public void setUserId(Context context, String userId) {
        UserSession.userId = userId;
        if (userId != null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("UserID", userId);
            editor.commit();
        } else {
            UserSession.userId = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("UserID");
            editor.commit();
        }
    }

    static public String getUserId(Context context) {
        if (userId == null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            userId = sharedpreferences.getString("UserID", null);
        }
        return userId;
    }


    // Set & Get userGroup

    static public void setUserGroup(Context context, String userGroup) {
        UserSession.userGroup = userGroup;
        if (userGroup != null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("UserGroup", userGroup);
            editor.commit();
        } else {
            UserSession.userGroup = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("UserGroup");
            editor.commit();
        }
    }

    static public String getUserGroup(Context context) {
        if (userGroup == null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            userGroup = sharedpreferences.getString("UserGroup", null);
        }

        return userGroup;
    }

    static public void setSessionKey(Context context, String sessionKey) {
        UserSession.sessionKey = sessionKey;
        if (sessionKey != null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("sessionKey", sessionKey);
            editor.commit();
        } else {
            UserSession.sessionKey = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("sessionKey");
            editor.commit();
        }
    }

    static public String getSessionKey() {
        return sessionKey;
    }

    public static String getServedMSISDN(Context context) {

        if (servedMSISDN == null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            servedMSISDN = sharedpreferences.getString("servedMSISDN", null);
        }
        return servedMSISDN;
    }

    public static void setServedMSISDN(Context context, String servedMSISDN) {
        UserSession.servedMSISDN = servedMSISDN;
        if (servedMSISDN != null) {

            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("servedMSISDN", servedMSISDN);
            editor.commit();
        } else {
            UserSession.servedMSISDN = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("servedMSISDN");
            editor.commit();
        }
    }


    public static String getResellerId(Context context) {

        if (resellerId == null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            resellerId = sharedpreferences.getString("resellerId", null);
        }

        return resellerId;
    }

    public static void setResellerId(Context context, String resellerId) {
        UserSession.resellerId = resellerId;
        if (resellerId != null) {

            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("resellerId", resellerId);
            editor.commit();
        } else {
            UserSession.resellerId = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("resellerId");
            editor.commit();
        }
    }

    public static String getAggregator(Context context){
        if (aggregator == null){
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            aggregator = sharedpreferences.getString("aggregator", null);
        }
        return aggregator;
    }

    public static void setAggregator(Context context, String aggregator){
        UserSession.aggregator = aggregator;
        if (aggregator != null){
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("aggregator", aggregator);
            editor.commit();
        } else {
            UserSession.aggregator = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("aggregator");
            editor.commit();
        }
    }




    public static String getResellerName(Context context) {

        if (resellerName == null) {
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            resellerName = sharedpreferences.getString("resellerName", null);
        }

        return resellerName;
    }

    public static void setResellerName(Context context, String resellerName) {
        UserSession.resellerName = resellerName;
        if (resellerName != null) {

            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("resellerName", resellerName);
            editor.commit();
        } else {
            UserSession.resellerName = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("resellerName");
            editor.commit();
        }
    }


    public static  void setSubcriptionId(Context context ,String subscriptionId){
        UserSession.subscriptionId = subscriptionId;
        if (subscriptionId != null) {

            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("subscriptionId", subscriptionId);
            editor.commit();
        } else {
            UserSession.subscriptionId = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("subscriptionId");
            editor.commit();
        }

    }

   /* public static Boolean getEnableMobileMoneyReg(Context context) {

        if (enableMobileMoneyReg == null){
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            enableMobileMoneyReg = sharedpreferences.getBoolean("enableMobileMoneyReg", null);
        }

        return enableMobileMoneyReg;
    }

    public static void setEnableMobileMoneyReg(Context context, Boolean enableMobileMoneyReg) {
        UserSession.enableMobileMoneyReg = enableMobileMoneyReg;

        if (enableMobileMoneyReg != null) {

            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
           // editor.putString("enableMobileMoneyReg", enableMobileMoneyReg);
            editor.putBoolean("enableMobileMoneyReg", enableMobileMoneyReg);
            editor.commit();
        } else {
            UserSession.enableMobileMoneyReg = null;
            sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("enableMobileMoneyReg");
            editor.commit();
        }
    }

*/
}
