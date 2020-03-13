package com.aryagami.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by cloudkompare on 2/10/15.
 */
public class DeviceImageMetrics {

    public static class Metrics {
      public int numImages;
      public int imageWidth;
    }

    public static int getDeviceWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getDeviceHeight(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static Metrics getImageMetrics(Activity context, int imagesPerRow) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int screen_height = outMetrics.heightPixels;
        int screen_width = outMetrics.widthPixels;
        Metrics metrics = new Metrics();
        metrics.imageWidth = screen_width / imagesPerRow;
        int numRows = (screen_height / metrics.imageWidth) + 1;
        metrics.numImages = numRows * imagesPerRow;
        return metrics;
    }
}
