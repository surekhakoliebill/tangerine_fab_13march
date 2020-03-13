package com.aryagami.util;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aryagami.data.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class FilePath {
    // SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        uri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        //  return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static float getFileSize(String fileName) {
        File file = new File(fileName);

        if(!file.exists() || !file.isFile()){
            return -1;
        }
        float bytes = file.length();
        float kiloBytes = (bytes/1024);
        float megaBytes = (kiloBytes/1024);
        return megaBytes;
    }

    public static String getEncodeData(Activity activity, Uri pdfUri){
        String filepath = null;
        String encodeData = null;

        try {
            //  filepath = FilePath.getPath(activity,pdfUri);
            filepath = getFilePath(activity, pdfUri);

            if(filepath != null){
                float filesize  = getFileSize(filepath);
                if(filesize <= 10) {
                    encodeData = PictureUtility.encodeFileToBase64Binary(filepath);
                }else if(filesize ==-1){
                    encodeData = "File Not Found";
                }else {
                    encodeData = "File size is too Large";
                }
            }else{
                MyToast.makeMyToast(activity,"Unable to Encode it, please upload different Pdf.", Toast.LENGTH_SHORT);

            }
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Cause:"+e.getCause()+"Message"+e.getMessage()+"Stack"+ Log.getStackTraceString(e),"EncodeFile");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Cause:"+e.getCause()+"Message"+e.getMessage()+"Stack"+ Log.getStackTraceString(e),"EncodeFile");
        }

        return encodeData;
    }



}