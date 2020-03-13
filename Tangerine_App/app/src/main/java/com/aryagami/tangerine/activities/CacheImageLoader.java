package com.aryagami.tangerine.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.aryagami.util.MyCameraApplicationUtil;

import java.lang.ref.WeakReference;

public class CacheImageLoader extends AsyncTask<String, Integer, Bitmap> {

    private final WeakReference<ImageView> viewReference;
    private Context mContext;

    public CacheImageLoader(Context c, ImageView view) {
      viewReference = new WeakReference<ImageView>(view);
      mContext = c;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
    	Log.i("gridview","params[0] in cache loader "+params[0]);
    	Bitmap bitmap = null;
        
       if(params[0]!=""){
        	Log.i("gridview","Bitmap in cache loader before::  "+bitmap);
        	Log.i("gridview","param "+params[0]);
        	Log.i("gridview","width "+params[1]);
        	int width = Integer.parseInt(params[1]);
        	bitmap = MyCameraApplicationUtil.getThumbnailFromImage(params[0],width,width);
        	
        	if(bitmap!=null){
        		ImageGridViewActivity.addBitmapToMemoryCache(params[0], bitmap);
            }
       }
        return bitmap;
    }
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            ImageView imageView = viewReference.get();
            if(imageView != null) {
              imageView.setImageBitmap(result);
            }
        }

    }

}
