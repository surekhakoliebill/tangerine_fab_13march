package com.aryagami.tangerine.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.gemalto.wsq.WSQEncoder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FingerPrintScannerActivity extends Activity implements FM220_Scanner_Interface {
            private acpl_FM220_SDK FM220SDK;
            private Button Capture_No_Preview,Capture_PreView,Capture_BackGround,Capture_match,btnsetF,btngetF,btnREsetF;
            private TextView textMessage,toastMsg;
            private ImageView imageView;
            Activity activity = this;
            String message;
            UserRegistration userRegistration = new UserRegistration();
            Button done;
            ProgressDialog progressDialog;
            ImageButton backImageButton;
            AlertDialog.Builder alertDialogMatched, alertDialogNotMatched, alertDialog;

            private static final String Telecom_Device_Key = "";
            private byte[] t1,t2;

            //region USB intent and functions
            private UsbManager manager;
            private PendingIntent mPermissionIntent;
            private UsbDevice usb_Dev;
            private static final String ACTION_USB_PERMISSION = "com.access.testappfm220.USB_PERMISSION";

            private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        int pid, vid;
                        pid = device.getProductId();
                        vid = device.getVendorId();
                        if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                            FM220SDK.stopCaptureFM220();
                            FM220SDK.unInitFM220();
                            usb_Dev=null;
                            DisableCapture();
                        }
                    }
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        synchronized (this) {
                            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                if (device != null) {
                                    // call method to set up device communication
                                    int pid, vid;
                                    pid = device.getProductId();
                                    vid = device.getVendorId();
                                    if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                                        fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                        if (res.getResult()) {
                                           // textMessage.setText("Device ready. "+res.getSerialNo());
                                           textMessage.setText("Device Ready.");
                                            EnableCapture();
                                        }
                                        else {
                                            DisableCapture();
                                        }
                                    }
                                }
                            } else {
                                textMessage.setText("Device Ready.");
                                DisableCapture();
                            }
                        }
                    }
                    if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                        synchronized (this) {
                            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (device != null) {
                                // call method to set up device communication
                                int pid, vid;
                                pid = device.getProductId();
                                vid = device.getVendorId();
                                if ((pid == 0x8225)  && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                                    finish();
                                }
                                if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                                    finish();
                                }

                                if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                                    if (!manager.hasPermission(device)) {
                                        manager.requestPermission(device, mPermissionIntent);
                                    } else {
                                        fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                        if (res.getResult()) {
                                            textMessage.setText("Device Ready.");
                                            EnableCapture();
                                        }
                                        else {
                                            DisableCapture();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            };

            @Override
            protected void onNewIntent(Intent intent) {
                if (getIntent() != null) {
                    return;
                }
                super.onNewIntent(intent);
                setIntent(intent);
                try {
                    if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev==null) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device != null) {
                            // call method to set up device communication & Check pid
                            int pid, vid;
                            pid = device.getProductId();
                            vid = device.getVendorId();
                            if ((pid == 0x8225)  && (vid == 0x0bca)) {
                                if (manager != null) {
                                    if (!manager.hasPermission(device)) {
                                        manager.requestPermission(device, mPermissionIntent);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


            @Override
            protected void onDestroy() {
                try {
                    unregisterReceiver(mUsbReceiver);
                    FM220SDK.unInitFM220();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
                super.onDestroy();
            }


            //endregion
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_finger_print);
                done = (Button) findViewById(R.id.done);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, BarcodeScanResultActivity.class);
                        startActivity(intent);
                        activity.finish();
                    }
                });

                backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
                backImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });

                Bundle bundle = getIntent().getExtras();

                message = bundle.getString("ScanningType");
                userRegistration = (UserRegistration) bundle.getSerializable("refugeeData");

                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
                textMessage = (TextView) findViewById(R.id.textMessage);
                toastMsg = (TextView) findViewById(R.id.toast_msg_display);
                Capture_PreView = (Button) findViewById(R.id.button2);
                Capture_No_Preview = (Button) findViewById(R.id.button);
                Capture_BackGround= (Button) findViewById(R.id.button3);
                Capture_match =(Button) findViewById(R.id.button4);
                imageView = (ImageView)  findViewById(R.id.imageView);

                btnsetF =(Button) findViewById(R.id.setflag);
                btngetF =(Button) findViewById(R.id.getflag);
                btnREsetF=(Button) findViewById(R.id.resetflag);

                if(ScanData.getScannedBarcodeData() != null){
                    if(ScanData.getScannedBarcodeData().scannedFingerIndex != null){
                        switch(ScanData.getScannedBarcodeData().scannedFingerIndex){
                            case 1:
                                toastMsg.setText("Please Scan Your Right Thumb");
                                break;
                            case 2: toastMsg.setText("Please Scan Your Right Index Finger");
                                break;
                            case 3:
                                toastMsg.setText("Please Scan Your Right Middle Finger");
                                break;
                            case 4:
                                toastMsg.setText("Please Scan Your Right Ring Finger");
                                break;
                            case 5:
                                toastMsg.setText("Please Scan Your Right Ladies Finger ");
                                break;
                            case 6:
                                toastMsg.setText("Please Scan Your Left Ladies Finger");
                                break;
                            case 7:
                                toastMsg.setText("Please Scan Your Left Ring Finger");
                                break;
                            case 8:
                                toastMsg.setText("Please Scan Your Left Middle Finger");
                                break;
                            case 9:
                                toastMsg.setText("Please Scan Your Left Index Finger");
                                break;
                            case 10:
                                toastMsg.setText("Please Scan Your Right Thumb");
                                break;
                        }
                    }
                }

                //Region USB initialisation and Scanning for device
                SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
                boolean oldDevType = sp.getBoolean("FM220type", true);

                manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
                if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);

                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                filter.addAction(ACTION_USB_PERMISSION);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                registerReceiver(mUsbReceiver, filter);
                UsbDevice device = null;
                for ( UsbDevice mdevice : manager.getDeviceList().values()) {
                    int pid, vid;
                    pid = mdevice.getProductId();
                    vid = mdevice.getVendorId();
                    boolean devType;
                    if ((pid == 0x8225) && (vid == 0x0bca)) {
                        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,true);
                        devType=true;
                    }
                    else if ((pid == 0x8220) && (vid == 0x0bca)) {
                        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,false);
                        devType=false;
                    } else {
                        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                        devType=oldDevType;
                    }
                    if (oldDevType != devType) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("FM220type", devType);
                        editor.apply();
                    }
                    if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                        device  = mdevice;
                        if (!manager.hasPermission(device)) {
                            //textMessage.setText("FM220 requesting permission");
                            manager.requestPermission(device, mPermissionIntent);
                        } else {
                            Intent intent = this.getIntent();
                            if (intent != null) {
                            }
                            fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                            if (res.getResult()) {
                               textMessage.setText("Device Ready.");
                                EnableCapture();
                            }
                            else {
                                DisableCapture();
                            }
                        }
                        break;
                    }
                }
                if (device == null) {
                   FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                }

                //endregion
                btnsetF.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val=  FM220SDK.SetRegistration(2);
                        if(val==0){
                            Toast.makeText(getApplicationContext(),"set reg", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btngetF.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean val=  FM220SDK.GetRegistration(2);
                        if(val){
                            Toast.makeText(getApplicationContext(),"true", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"false", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnREsetF.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val=  FM220SDK.ResetRegistration(2);
                        if(val==0){
                            Toast.makeText(getApplicationContext(),"reset reg", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Capture_BackGround.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisableCapture();
                        imageView.setImageBitmap(null);
                        FM220SDK.CaptureFM220(2);
                    }
                });

                Capture_No_Preview.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisableCapture();
                        FM220SDK.CaptureFM220(2,true,false);
                    }
                });

                Capture_PreView.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisableCapture();
                        FM220SDK.CaptureFM220(2,true,true);
                    }
                });
                Capture_match.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (t1 != null && t2 != null) {
                            if (FM220SDK.MatchFM220(t1, t2)) {
                                textMessage.setText("Finger matched");
                                t1 = null;
                                t2 = null;
                            } else {
                                textMessage.setText("Finger not matched");
                            }
                        } else {
                            textMessage.setText("Pl capture first");
                        }
                  FunctionBase64();
                    }
                });
            }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(FingerPrintScannerActivity.this);
    }

            private void DisableCapture() {
                Capture_BackGround.setEnabled(false);
                Capture_No_Preview.setEnabled(false);
                Capture_PreView.setEnabled(false);
                Capture_match.setEnabled(false);
                imageView.setImageBitmap(null);
            }

            private void EnableCapture() {
                Capture_BackGround.setEnabled(true);
                Capture_No_Preview.setEnabled(true);
                Capture_PreView.setEnabled(true);
                Capture_match.setEnabled(true);
            }

            private void FunctionBase64() {
                try {
                    String t1base64, t2base64;
                    if (t1 != null && t2 != null) {
                        t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                        t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                        if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
                            Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
                FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (DisplayText) {
                            textMessage.setText(statusMessage);
                            textMessage.invalidate();
                        }
                        if (DisplayImage) {
                            imageView.setImageBitmap(ScanImage);
                            imageView.invalidate();
                        }
                    }
                });
            }

            private void compareUgandaNINFingerPrint(final fm220_Capture_Result result) {


                if(ScanData.getScannedBarcodeData().getScannedFingerData() != null){
                    byte[] fpBytes = ScanData.getScannedBarcodeData().getScannedFingerData();

                    int fpBytesSize = fpBytes.length;

                    int numMinutiaePoints = fpBytesSize/5;

                    System.out.println("Size of array is " + numMinutiaePoints);
                    int maxX = 0;
                    int maxY = 0;
                    int byteNum = 0;
                    int xValues[] = new int[numMinutiaePoints];
                    int yValues[] = new int[numMinutiaePoints];

                    for (byteNum = 0; byteNum < fpBytesSize; byteNum = byteNum + 5) {
                        int xVal = ((fpBytes[byteNum] & 0x3F) << 8) + (fpBytes[byteNum + 1] & 0xFF);
                        xVal = (int)(((float)xVal/1000f) * 196.85f);

                        int minNum = byteNum / 5;
                        if (minNum >= numMinutiaePoints)
                            break;

                        xValues[minNum] = xVal;
                        if (xVal > maxX) {
                            maxX = xVal;
                        }

                        int yVal = ((fpBytes[byteNum + 2] & 0x3F) << 8) + (fpBytes[byteNum + 3] & 0xFF);
                        yVal = (int)(((float)yVal/1000f) * 196.85f);
                        yValues[minNum] = yVal;
                        if (yVal > maxY) {
                            maxY = yVal;
                        }

                        System.out.println(Integer.toHexString(fpBytes[byteNum] & 0x3F) + " " + Integer.toHexString(fpBytes[byteNum + 1] & 0xFF) + " " + xVal);
                        System.out.println(Integer.toHexString(fpBytes[byteNum + 2] & 0x3F) + " " + Integer.toHexString(fpBytes[byteNum + 3] & 0xFF) + " " + yVal);
                    }
                    System.out.println("Max x and y values are:  " + maxX + "," + maxY);
                    byte[] isoform = new byte[24 + 4 + (numMinutiaePoints * 6) + 2];
//magic
                    isoform[0] = 0x46;
                    isoform[1] = 0x4d;
                    isoform[2] = 0x52;
                    isoform[3] = 0x00;

//version
                    isoform[4] = 0x20;
                    isoform[5] = 0x32;
                    isoform[6] = 0x30;
                    isoform[7] = 0x00;

                    //num bytes
                    int numBytes = isoform.length;
                    isoform[8] = 0x00;
                    isoform[9] = 0x00;
                    isoform[10] = (byte)((numBytes >> 8) & 0xFF);
                    isoform[11] = (byte)(numBytes & 0xFF);

//mystery
                    isoform[12] = 0x00;
                    isoform[13] = 0x00;

//Width
                    ++maxX;
                    isoform[14] = (byte)((maxX >> 8) & 0xFF);
                    isoform[15] = (byte)(maxX & 0xFF);

//height
                    ++maxY;
                    isoform[16] = (byte)((maxY >> 8) & 0xFF);
                    isoform[17] = (byte)(maxY & 0xFF);

//dpi-x
                    isoform[18] = 0x00;
                    isoform[19] = (byte)0xc5;

//dpi-y
                    isoform[20] = 0x00;
                    isoform[21] = (byte)0xc5;

//fpcount
                    isoform[22] = 0x01;

//mystery
                    isoform[23] = 0x00;

                    isoform[24] = 0x00;

                    isoform[25] = 0x00;

//quality
                    isoform[26] = 0x64;

                    isoform[27] = (byte)(numMinutiaePoints & 0xFF);

                    int minOffset = 28;
                    byte xyCopyMask = (byte)0xC0;
                    for (int i=0; i < numMinutiaePoints; i++) {
                        int xVal = xValues[i];
                        System.out.println("Xval " + xVal);

                        int yVal = yValues[i];

                        isoform[minOffset + (i * 6)] = (byte)((fpBytes[(i * 5)] & xyCopyMask) | (xVal >> 8) & 0xFF);
                        isoform[minOffset + (i * 6) + 1] = (byte)(xVal & 0xFF);
                        isoform[minOffset + (i * 6) + 2] = (byte)((fpBytes[(i * 5) + 2] & xyCopyMask) | (yVal >> 8) & 0xFF);
                        isoform[minOffset + (i * 6) + 3] = (byte)(yVal & 0xFF);
                        isoform[minOffset + (i * 6) + 4] = fpBytes[(i * 5) + 4];
                        isoform[minOffset + (i * 6) + 5] = 0x64;
                    }

                    String bytedump = "";
                    for (byte abyt : isoform) {
                        bytedump += Integer.toHexString(0xff & abyt) + " ";
                    }

                    boolean match = FM220SDK.MatchFM220(isoform, result.getISO_Template());

                    if (match ) {
                        textMessage.setText("Fingerprint matched Successfully.");
                        imageView.setImageBitmap(result.getScanImage());
                        RegistrationData.setIsmatched(true);
                        RegistrationData.setCapturedFingerprintDrawable(imageView.getDrawable());
                        //activity.finish();
                        showAlert("Success","Fingerprint matched Successfully.");
                    } else {
                        RegistrationData.setIsmatched(false);
                        imageView.setImageBitmap(null);
                        RegistrationData.setCapturedFingerprintDrawable(null);

                        FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialogNotMatched = new AlertDialog.Builder(FingerPrintScannerActivity.this);
                                alertDialogNotMatched.setCancelable(false)
                                        .setTitle("Failed")
                                        .setMessage("Last Fingerprint did not Matching, please retry!")
                                        .setPositiveButton(activity.getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        DisableCapture();
                                                        FM220SDK.CaptureFM220(2, true, false);
                                                        dialog.dismiss();
                                                    }
                                                });
                                AlertDialog alert = alertDialogNotMatched.create();
                                alert.show();
                            }
                        });

                    }


                }else{
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please, rescan the NIN Document. Reason: Empty Fingerprint Data.");
                    alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    activity.finish();
                                    RegistrationData.setIsScanICCID(false);
                                    ScanData.setScannedBarcodeData(null);
                                    Intent intent = new Intent(activity,BarcodeScannerActivity.class);
                                    startActivity(intent);
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            public void ScanCompleteFM220(final fm220_Capture_Result result) {
                FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (FM220SDK.FM220Initialized())  EnableCapture();
                        if (result.getResult()) {
                            byte[] wsqData = new WSQEncoder(result.getScanImage())
                                    .setBitrate(WSQEncoder.BITRATE_15_TO_1)
                                    .encode();

                            RegistrationData.setByteData(wsqData);
                            imageView.setImageBitmap(result.getScanImage());
                            byte [] isotem  = result.getISO_Template();   // ISO TEMPLET of FingerPrint.....
                            if (t1 == null) {
                                t1 = result.getISO_Template();
                            } else {
                                t2 = result.getISO_Template();
                            }

                            if (message.equals("ugandanFinger")){
                                try {
                                    compareUgandaNINFingerPrint(result);
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " +
                                                    ex.getCause() + "Stack: " + Log.getStackTraceString(ex) + " " +
                                            ScanData.getScannedBarcodeData().getScannedFingerData( )+ " "
                                                    + ScanData.getScannedBarcodeData().getEncodedFingerData(),
                                            "UgandanNINMatching");
                                }
                            }else if(message.equals("Thumb")) {
                                try {
                                    RegistrationData.setSubscriberThumbImageDrawable(imageView.getDrawable());
                                    callParentActivity();
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintThumb");
                                }
                               // callParentActivity();

                            }else if(message.equals("Index")) {
                                try {
                                    RegistrationData.setSubscriberIndexImageDrawable(imageView.getDrawable());
                                    callParentActivity();
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintIndex");
                                }


                            }else if(message.equals("userFinger")){
                                try {
                                    RegistrationData.setImageDrawable(imageView.getDrawable());
                                    callParentActivity();
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintUserFinger");
                                }
                            }else if(message.equals("userThumb")){
                                try {
                                    RegistrationData.setUserThumbImageDrawable(imageView.getDrawable());
                                    callParentActivity();
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintUserThumb");
                                }


                            }else if(message.equals("userIndex")){
                                try {
                                    RegistrationData.setUserIndexImageDrawable(imageView.getDrawable());
                                   callParentActivity();
                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintUserIndex");
                                }
                            }else if(message.equals("refugeeUser")){

                                try {
                                     byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(wsqData);
                                     final String encodedString = new String(encoded);

                                    if (isStoragePermissionGranted()) { // check or ask permission

                                        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"Tangerine/";
                                        File folder = new File(folderPath);
                                        if (!folder.exists()) {
                                            folder.mkdirs();
                                           // MyToast.makeMyToast(activity,"Tangerine Directory Created", Toast.LENGTH_SHORT);
                                        }
                                       // String root = Environment.getExternalStorageDirectory().toString();
                                        File myDir = new File(folder, "Temp");
                                        if (!myDir.exists()) {
                                            myDir.mkdirs();
                                        }
                                        String fname = "Thumbprint.txt";
                                        File file = new File(myDir, fname);
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                        String status = "";
                                        try {
                                            if (file.createNewFile()) {

                                                FileWriter writer = new FileWriter(file);
                                                writer.append(encodedString);
                                                writer.flush();
                                                writer.close();
                                               // textMessage.setText("Created file " + file.getAbsolutePath());
                                            } else {
                                              //  textMessage.setText("Created file " + file.getAbsolutePath() + " failed");
                                            }
                                        } catch (Exception ex) {
                                           // textMessage.setText("Exception creating file " + file.getAbsolutePath() + e.getMessage() + " " + e.getCause());
                                            BugReport.postBugReport(activity, Constants.emailId,
                                                    "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                                    "FingerPrintUserIndex");
                                        }

                                    } else {
                                       // textMessage.setText("Not allowed to write file");
                                    }

                                    RegistrationData.setRefugeeThumbImageDrawable(imageView.getDrawable());
                                    RegistrationData.setRefugeeThumbEncodedData(encodedString);

                                    verifyRefugeeIdentity(encodedString);
/*

                                    alertDialogMatched = new AlertDialog.Builder(FingerPrintScannerActivity.this);
                                    alertDialogMatched.setCancelable(false);
                                    alertDialogMatched.setTitle("Message!");
                                    alertDialogMatched.setMessage("Fingerprint Captured, you want to retry?");
                                    alertDialogMatched.setPositiveButton(activity.getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    RegistrationData.setRefugeeThumbImageDrawable(null);
                                                    RegistrationData.setRefugeeThumbEncodedData(null);
                                                    dialog.dismiss();

                                                    DisableCapture();
                                                    FM220SDK.CaptureFM220(2, true, false);

                                                }
                                            })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            RegistrationData.setRefugeeThumbImageDrawable(imageView.getDrawable());
                                            RegistrationData.setRefugeeThumbEncodedData(encodedString);

                                            dialog.dismiss();
                                            activity.finish();
                                            Intent intent = new Intent();
                                            setResult(Activity.RESULT_OK, intent);
                                        }
                                    });
                                    AlertDialog alert = alertDialogMatched.create();
                                    alert.show();

*/

                                } catch(Exception ex) {
                                    BugReport.postBugReport(activity, Constants.emailId,
                                            "Message: " + ex.getMessage() + ", Cause: " + ex.getCause() + "Stack: " + Log.getStackTraceString(ex),
                                            "FingerPrintUserIndex");
                                }

                            }

                        } else {
                            imageView.setImageBitmap(null);
                        }
                        imageView.invalidate();
                        textMessage.invalidate();
                    }
                });
            }

    private void verifyRefugeeIdentity(String encodedString) {

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {

            if(RegistrationData.getRefugeeThumbEncodedData() != null){

                if (userRegistration != null){

                    userRegistration.fingerprint = RegistrationData.getRefugeeThumbEncodedData();


                    progressDialog = ProgressDialogUtil.startProgressDialog(activity,"Verifying Refugee......");

                    serviceHandler.postRefugeeVerification(userRegistration, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin userLogin = (UserLogin) data.get(0);
                            if (userLogin.status.equals("Match")) {

                                ProgressDialogUtil.stopProgressDialog(progressDialog);

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("Refugee Identity Verified Successfully!");
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                RegistrationData.setIsRefugeeMatched(true);

                                                activity.finish();
                                                Intent intent = new Intent();
                                                setResult(Activity.RESULT_OK, intent);

                                            }
                                        });
                                alertDialog.show();
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if(userLogin.status.equals("INVALID_SESSION")){
                                    ReDirectToParentActivity.callLoginActivity(activity);

                                }else{


                                    View dialogView = getLayoutInflater().inflate(R.layout.custom_layout, null);
                                    ImageView pic = (ImageView) dialogView.findViewById(R.id.dialog_pic);
                                    pic.setImageDrawable(RegistrationData.getRefugeeThumbImageDrawable());
                                    TextView desc = (TextView) dialogView.findViewById(R.id.desc);
                                    desc.setText("Status: "+userLogin.status);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Message!");
                                    alertDialog.setView(dialogView);
                                    /* alertDialog.setMessage("Status: "+userLogin.status);*/
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    RegistrationData.setIsRefugeeMatched(false);

                                                    DisableCapture();
                                                    FM220SDK.CaptureFM220(2, true, false);

                                                }
                                            });
                                    alertDialog.show();
                                }

                                // MyToast.makeMyToast(getActivity(), userLogin.status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+",\t STATUS"+status,"REFUGEE VERIFICATION");
                        }

                    });

                }


            }else{
                MyToast.makeMyToast(activity,"Please Capture Fingerprint.", Toast.LENGTH_SHORT);
            }

        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"MESSAGE:"+e.getMessage()+",\t CAUSE:"+e.getCause(),"NIRA VERIFICATION");
        }
    }

    private void callParentActivity() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void showAlert(String title, String message) {
        alertDialogMatched = new AlertDialog.Builder(FingerPrintScannerActivity.this);
        alertDialogMatched.setCancelable(false);
        alertDialogMatched.setTitle(title);
        alertDialogMatched.setMessage(message);
        alertDialogMatched.setPositiveButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        activity.finish();
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                    }
                });
        AlertDialog alert = alertDialogMatched.create();
        alert.show();
    }

    @Override
            public void ScanMatchFM220(final fm220_Capture_Result _result) {
                FingerPrintScannerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (FM220SDK.FM220Initialized()) EnableCapture();
                        if (_result.getResult()) {
                            textMessage.setText("Finger matched\n" + "Success NFIQ:" + Integer.toString(_result.getNFIQ()));
                        } else {
                            imageView.setImageBitmap(null);
                            textMessage.setText("Finger not matched\n" + _result.getError());
                        }
                        imageView.invalidate();
                        textMessage.invalidate();
                    }
                });
            }

    public  void onTrimMemory(int level) {
        System.gc();
    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }
        }
