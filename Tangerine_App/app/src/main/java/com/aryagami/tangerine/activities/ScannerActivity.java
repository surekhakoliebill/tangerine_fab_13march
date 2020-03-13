/*
 * Copyright (c) 2018. softotalss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.aryagami.tangerine.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.util.ActivityUtil;
import com.aryagami.util.Constants;
import com.aryagami.util.DialogFactory;
import com.aryagami.util.MyToast;
import com.github.softotalss.barcodescanner.view.BarcodeScannerView;
import com.google.zxing.Result;

import org.apache.commons.codec.binary.Base64;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScannerActivity extends AppCompatActivity implements BarcodeScannerView.ActivityCallback {

    private static final String TAG = ScannerActivity.class.getSimpleName();

    private ViewGroup mContentFrame;
    private BarcodeScannerView mScannerView;
    ScanData scannedResult = new ScanData();
    DateFormat formatter = null;
    Date convertedDate = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isLandscapeSimulated()) {
            setContentView(R.layout.activity_scanner_rotate);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Toast.makeText(this, R.string.text_landscape_simulated, Toast.LENGTH_LONG).show();
        } else {
            setContentView(R.layout.activity_scanner);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Toast.makeText(this, R.string.text_landscape, Toast.LENGTH_LONG).show();
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        stopCamera();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.btn_flash:
                if(!mScannerView.isFlashOn()) {
                    item.setIcon(R.drawable.ic_flash_on);
                    item.setTitle(R.string.text_flash_on);
                } else {
                    item.setIcon(R.drawable.ic_flash_off);
                    item.setTitle(R.string.text_flash_off);
                }
                mScannerView.toggleFlash();
                return true;
            case R.id.btn_rotate:
                Intent intent = new Intent(this, ScannerActivity.class);
                intent.putExtra("landscapeSimulated", !isLandscapeSimulated());
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };

                    DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this, R.string.text_alert,
                            R.string.msg_camera_permission_denied, ok));
                }
                break;
        }
    }

    private void initView() {
        mContentFrame = findViewById(R.id.layout_content);
        if (ActivityUtil.solicitarPermisos(this, Manifest.permission.CAMERA,
                R.string.text_alert, R.string.msg_camera_permission,
                Constants.PERMISSION_CAMERA)) {
            initCamera();
        }
    }

    private void initCamera() {
        mScannerView = new BarcodeScannerView(this);
        mScannerView.setLandscapeSimulated(isLandscapeSimulated());
        mContentFrame.addView(mScannerView);
    }

    private void startCamera() {
        if (mScannerView != null) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    private void stopCamera() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    private boolean isLandscapeSimulated() {
        return getIntent().getBooleanExtra("landscapeSimulated", false);
    }

    @Override
    public void onResult(Result result) {

        if (result != null) {
            if (!result.getText().isEmpty()) {

                if (RegistrationData.getIsScanICCID()) {
                    ScannerActivity.this.finish();
                    String iccidData[] = result.toString().split(",");
                    if (iccidData[0] != null) {
                        if (!iccidData[0].isEmpty()) {
                            RegistrationData.setScanICCIDData(iccidData[0].toString());
                            ScannerActivity.this.finish();
                        } else {
                            MyToast.makeMyToast(ScannerActivity.this, "Please Rescan!", Toast.LENGTH_SHORT);
                            /*Intent intent = getIntent();
                            finish();
                            startActivity(intent);*/
                            /*ScannerActivity.this.finish();
                            Intent intent = new Intent(ScannerActivity.this, BarcodeScannerActivity.class);
                            startActivity(intent);*/
                        }
                    } else {
                        MyToast.makeMyToast(ScannerActivity.this, "Please Rescan, Details are Empty!", Toast.LENGTH_SHORT);
                        /*Intent intent = getIntent();
                        finish();
                        startActivity(intent);*/
                        //ScannerActivity.this.finish();
                        //Intent intent = new Intent(ScannerActivity.this, BarcodeScannerActivity.class);
                        //startActivity(intent);
                    }

                } else {
                    decodeScanResult(result.getText());
                }
            } else {
                MyToast.makeMyToast(ScannerActivity.this, "Data Empty.", Toast.LENGTH_SHORT).show();
            }
        } else {
            MyToast.makeMyToast(ScannerActivity.this, "Please...", Toast.LENGTH_SHORT).show();
        }

       /* Log.d(TAG, result.getText());
        DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this,
                getString(R.string.app_name), result.getText(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerView.restartCamera(); // Use it for read another barcode without close camera
                    }
                }));*/
    }

    private void decodeScanResult(String rawResult) {

        String[] ninResult = rawResult.split(";");
        if (ninResult != null) {
            byte[] byteArray;
            if (ninResult.length > 9) {
                for (int i = 0; i < 9; i++) {
                    switch (i) {
                        case 0:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setLastName(decodedString);
                            }
                            break;
                        case 1:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setFirstName(decodedString);
                            }
                            break;
                        case 2:
                            byteArray = Base64.decodeBase64(ninResult[i].trim().getBytes());
                            if (byteArray != null) {
                                String decodedString = new String(byteArray);
                                scannedResult.setOtherName(decodedString);
                            }
                            break;

                        case 3:
                            String ddMMyyyy = ninResult[3].toString();
                            SimpleDateFormat dt = new SimpleDateFormat("ddmmyyyy");
                            try {
                                convertedDate = dt.parse(ddMMyyyy);
                                formatter = new SimpleDateFormat("dd/mm/yyyy");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            scannedResult.setDateOfBirth(formatter.format(convertedDate).toString());
                            break;

                        case 6:
                            scannedResult.setPassportNo(ninResult[6].toString());
                            break;

                        case 7:
                            scannedResult.setDocumentId(ninResult[7].toString());
                            break;

                        case 8:
                            byteArray = Base64.decodeBase64(ninResult[8].trim().getBytes());
                            scannedResult.setScannedFingerData(byteArray);
                            scannedResult.setEncodedFingerData(ninResult[i].trim().getBytes());
                            scannedResult.scannedFingerIndex = 1;
                            break;

                        default:
                    }

                }
                scannedResult.setMatched(false);
                ScanData.setScannedBarcodeData(scannedResult);
                Intent intent = new Intent(ScannerActivity.this, BarcodeScanResultActivity.class);
                startActivity(intent);
                ScannerActivity.this.finish();
            } else {
                alertMessage();
            }
        } else {
            alertMessage();
        }
    }

    private void alertMessage() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScannerActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Message!");
        alertDialog.setMessage("Please, rescan the document. Reason: Empty Data.");
        alertDialog.setNeutralButton(ScannerActivity.this.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        RegistrationData.setIsScanICCID(false);
                        ScanData.setScannedBarcodeData(null);

                        /*Intent intent = new Intent(ScannerActivity.this, BarcodeScannerActivity.class);
                        startActivity(intent);
                        ScannerActivity.this.finish();*/
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onErrorExit(Exception e) {
        Log.d(TAG, "onErrorExit");
        DialogFactory.showNoCancelable(DialogFactory.createSimpleOkDialog(this,
                R.string.app_name, R.string.msg_camera_framework_bug,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }));
    }
}
