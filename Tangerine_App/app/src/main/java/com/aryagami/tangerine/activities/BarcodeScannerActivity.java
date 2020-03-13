package com.aryagami.tangerine.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.util.CheckNetworkConnection;

public class BarcodeScannerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(RegistrationData.getIsScanICCID()){
            setContentView(R.layout.scan_iccid);
        }else{
            setContentView(R.layout.activity_barcode_scanner);
        }
        initUI();

        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(BarcodeScannerActivity.this);
    }

    private void initUI() {
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        findViewById(R.id.ivBack).setOnClickListener(this);
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void launchFullActivity(View v) {
        launchActivity(FullScannerActivity.class);
        BarcodeScannerActivity.this.finish();
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(BarcodeScannerActivity.this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivBack:
                BarcodeScannerActivity.this.finish();
                break;
        }
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
}
