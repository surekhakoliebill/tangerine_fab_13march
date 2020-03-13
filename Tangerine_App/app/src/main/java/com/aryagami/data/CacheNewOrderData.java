package com.aryagami.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheNewOrderData {
    public static List<NewOrderCommand> getSaveCacheData() {
        return saveCacheData;
    }

    public static void setSaveCacheData(List<NewOrderCommand> saveCacheData) {
        CacheNewOrderData.saveCacheData = saveCacheData;
    }

    public static List<NewOrderCommand> saveCacheData = null;

    public static void saveUpdateUserDataCache (Activity context, List<NewOrderCommand> orderDetailsCacheList) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);


        Gson gson = new Gson();
        String json = gson.toJson(orderDetailsCacheList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("userCache", json);
        editor.apply();
    }

    public static void savedOrderData (Context context, List<NewOrderCommand> orderDetailsCacheList) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(orderDetailsCacheList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("userCache", json);
        editor.apply();

    }

    public static List<NewOrderCommand> loadUpdateUserCacheList(Context context) {
        List<NewOrderCommand> orderCommandList = new ArrayList<NewOrderCommand>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USERSESSIONINFO, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("userCache", null);

        Type type = new TypeToken<ArrayList<NewOrderCommand>>() {}.getType();
        orderCommandList = gson.fromJson(json, type);

        if(orderCommandList == null){
            orderCommandList = new ArrayList<NewOrderCommand>();
        }

        return orderCommandList;
    }



    public static List<NewOrderCommand> loadNewOrderCacheList(Context context) {
        List<NewOrderCommand> orderCommandList = new ArrayList<NewOrderCommand>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USERSESSIONINFO, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("orderCache", null);

        Type type = new TypeToken<ArrayList<NewOrderCommand>>() {}.getType();
        orderCommandList = gson.fromJson(json, type);

        if(orderCommandList == null){
            orderCommandList = new ArrayList<NewOrderCommand>();
        }

        return orderCommandList;
    }

    public static void saveNewOrderDataCache (Activity context, List<NewOrderCommand> orderDetailsCacheList) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(orderDetailsCacheList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("orderCache", json);
        editor.apply();
    }

    public static void savedNewOrderData (Context context, List<NewOrderCommand> orderDetailsCacheList) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(orderDetailsCacheList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("orderCache", json);
        editor.apply();
    }

    public static void saveUnUploadedDocs(Context context, List<UserRegistration.UserDocCommand> userDocCommandList){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(userDocCommandList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("UnUploadedDocs", json);
        editor.apply();

    }

    public static List<UserRegistration.UserDocCommand> loadUnUploadedDocList(Context context) {
        List<UserRegistration.UserDocCommand> orderCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USERSESSIONINFO, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("UnUploadedDocs", null);

        Type type = new TypeToken<ArrayList<UserRegistration.UserDocCommand>>() {}.getType();
        orderCommandList = gson.fromJson(json, type);

        if(orderCommandList == null){
            orderCommandList = new ArrayList<UserRegistration.UserDocCommand>();
        }

        return orderCommandList;
    }


    public static Map<String, UserRegistration.UserDocCommand> loadDocs(Context context){
        Map<String, UserRegistration.UserDocCommand> docMapList = new HashMap<String, UserRegistration.UserDocCommand>();
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USERSESSIONINFO, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("DOCS", null);

        Type type = new TypeToken<Map<String, UserRegistration.UserDocCommand>>() {}.getType();
        docMapList = gson.fromJson(json, type);

        if(docMapList == null){
            docMapList =new HashMap<String, UserRegistration.UserDocCommand>();
        }
        return docMapList;
    }

    public static void saveDocs(Context context, Map<String, UserRegistration.UserDocCommand> userDocCommandList){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.USERSESSIONINFO, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(userDocCommandList);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("DOCS", json);
        editor.apply();

    }


}
