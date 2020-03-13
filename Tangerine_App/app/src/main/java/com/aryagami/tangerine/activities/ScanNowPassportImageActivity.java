package com.aryagami.tangerine.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryagami.BuildConfig;

import com.aryagami.R;
import com.aryagami.util.Utils;
import com.docrecog.scan.CameraActivity;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * This class used to show preview for different options
 */

public class ScanNowPassportImageActivity extends BaseActivity implements View.OnClickListener {

    private static final int RETURN_RESULT = 1;
    private static final int REQUEST_CAMERA_FOR_RESULT = 100;
    private static final int REQUEST_PAN_AADHAAR_RESULT = 103;
    public boolean mPermissionRationaleDialogShown = false;
    public Uri fileUri;
    private int type = 1;
    private TextView tvTitle, tvLetsGo, niraDocScan, passportScanBtn;
    private ImageView ivScan;
    private File imageFile;
    private int card_type;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_passport_image);

        initUi();
    }

    private void initUi() {

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        tvLetsGo = findViewById(R.id.tvLetsGo);
        tvLetsGo.setOnClickListener(this);

        /*niraDocScan = findViewById(R.id.nira_scanning);
        niraDocScan.setOnClickListener(this);*/
        passportScanBtn = findViewById(R.id.foreinger_scan_btn);
        passportScanBtn.setOnClickListener(this);

        findViewById(R.id.ivBack).setOnClickListener(this);
        findViewById(R.id.tvLetsGo).setOnClickListener(this);
        findViewById(R.id.foreinger_scan_btn).setOnClickListener(this);
       // findViewById(R.id.nira_scanning).setOnClickListener(this);

      //  tvTitle = findViewById(R.id.tvTitle);
        tvLetsGo = findViewById(R.id.tvLetsGo);
        niraDocScan = findViewById(R.id.nira_scanning);

        ivScan = findViewById(R.id.ivScan);

        /*type is used to check which button click from first page and set title and preview image according to that*/
        type = getIntent().getIntExtra(TYPE, 1);

        switch (type) {
            case 1:
               // tvTitle.setText(getString(R.string.text_place_doc));
                //ivScan.setImageResource(R.mipmap.demoimage);
                tvLetsGo.setText("National Id Card");

                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivBack:
                ScanNowPassportImageActivity.this.finish();
                break;

            case R.id.tvLetsGo:

                ScanNowPassportImageActivity.this.finish();
                Intent intent = new Intent(getApplicationContext(),BarcodeScannerActivity.class);
                startActivity(intent);
                break;
            case R.id.foreinger_scan_btn:

                openSpecificActivity();
                break;
        }
    }

    public static void copyInputStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private void openSpecificActivity() {
        switch (type) {
            case 1: //passport
//                if (requestPermission()) {
//                    requestCameraActivity();
//                }
                card_type = 1;
                requestPermission(1);
                break;
            case 2: // driving license
                card_type = 4;
//                requestBarcode();
                requestPermission(4);
                break;
            case 3: // barcode
                card_type = 5;
//                requestBarcode();
                requestPermission(5);
                break;
            case 4: //pan card
                card_type = 2;
                requestPermission(2);
                break;
            case 5: //aadhaarcard
                card_type = 3;
                requestPermission(3);
                break;
        }
    }

    private void requestPermission(int type) {

        if (Build.VERSION.SDK_INT >= 23) {
            if(type == 4 || type == 5){
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    card_type = type;
                    //requestBarcode();
                }else {
                    ActivityCompat.requestPermissions(ScanNowPassportImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_FOR_RESULT);
                }
            }
            else {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (type == 1) {
                        requestCameraActivity();
                    } else {
                        takePicture();
                    }
                } else {
                    ActivityCompat.requestPermissions(ScanNowPassportImageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_FOR_RESULT);
                }
            }
        } else {
            if(type == 4 || type == 5){
                card_type = type;
              //  requestBarcode();
            }else {
                if (type == 1) {
                    requestCameraActivity();
                } else {
                    takePicture();
                }
            }
        }
    }

    public void showPermissionRequiredDialog() {
        mPermissionRationaleDialogShown = true;
        // Dialog to show why permission is required
        String msg = String.valueOf(Html.fromHtml("If you are not allowed com.com.scan.camera.scan.camera permission then your app may not working properly. please allow to this permission </br> <b> Follow this step: Permission -> Enable com.com.scan.camera.scan.camera permission </b>"));

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanNowPassportImageActivity.this);
        builder.setTitle(Html.fromHtml("<font color='#6DB0E4'> <b> EBW - Permission Necessary </b> </font>"));
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.RECEIVE_SMS}, 1);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_FOR_RESULT) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.v("EBW", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                if(card_type == 1){
                    requestCameraActivity();
                }else {
                    takePicture();
                }
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("EBW", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                requestPermission(card_type);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCameraActivity() {
        startActivityForResult(new Intent(ScanNowPassportImageActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
        overridePendingTransition(0, 0);
    }

    // Open barcode scan activity
    /*private void requestBarcode() {
        Intent intent = new Intent(ScanNowPassportImageActivity.this, BarcodeCameraActivity.class);
        intent.putExtra("card_type", card_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, RETURN_RESULT);
        overridePendingTransition(0, 0);
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* After clicked picture from camera, callback return here*/
        if (resultCode == RESULT_OK) {
            if (requestCode == RETURN_RESULT) {
                startActivity(new Intent(ScanNowPassportImageActivity.this, ScanResultActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                overridePendingTransition(0, 0);
            } else if (requestCode == Utils.REQUEST_CAMERA) {
                setImage();
            } else if (requestCode == REQUEST_PAN_AADHAAR_RESULT) {
                takePicture();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

//    private void checkCameraPermission() {
//        /*Check camera permission*/
//        if (isCameraPermissionGranted()) {
//            takePicture();
//        }
//    }

    private void takePicture() {
        /* open default camera app*/
        String state = Environment.getExternalStorageState();
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpeg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                } else {
                    fileUri = Uri.parse(Utils.CONTENT_URI);
                }
            } else {
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    fileUri = Uri.fromFile(imageFile);
                } else {
                    fileUri = Uri.parse(Utils.CONTENT_URI);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            startActivityForResult(intent, Utils.REQUEST_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private boolean isCameraPermissionGranted() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Utils.PERMISSION_CAMERA);
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(Utils.FILE_URI, fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            fileUri = savedInstanceState.getParcelable(Utils.FILE_URI);
    }

    private void setImage() {
        if (imageFile != null) {
            try {
                ExifInterface exif;
                exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int angle = 0;

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        angle = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        angle = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        angle = 270;
                        break;
                }

                Matrix mat = new Matrix();
                mat.postRotate(angle);

              //  requestPanAadhar();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  /*  @SuppressLint("StaticFieldLeak")
    private void requestPanAadhar() {
        *//* There is web api key in string.xml file named "api_key" .
        *   This api used to get data from Pan and Aadhaar card.
        * *//*

        showProgressDialog();
        new RequestTask(ScanNowPassportImageActivity.this, getString(R.string.header_default_key),
                GenerateRequest.requestPanAadharDetail(this, imageFile, card_type), getString(R.string.api)) {

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                *//*Get response from api*//*
                dismissProgressDialog();

                ParsedResponse p = HandleResponse.responsePanAadharDetail(ScanNowPassportImageActivity.this, response);
                if (!p.error) {
                    *//*If got proper data then display result*//*
                    PanAadharDetail panAadharDetail = (PanAadharDetail) p.o;

                    Intent intent = new Intent(ScanNowPassportImageActivity.this, PanAadharResultActivity.class);
                    intent.putExtra("panAadharDetail", panAadharDetail);
                    intent.putExtra("card_type", card_type);
                    intent.putExtra("imageFile", imageFile.getAbsolutePath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, REQUEST_PAN_AADHAAR_RESULT);
                    overridePendingTransition(0, 0);
                } else {
                    new AlertDialogAbstract(ScanNowPassportImageActivity.this, (String) p.o, getString(R.string.ok), "") {
                        @Override
                        public void positive_negativeButtonClick(int pos_neg_id) {
                        }
                    };
                }
            }
        }.execute();
    }*/
}
