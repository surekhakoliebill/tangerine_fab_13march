package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.util.CheckNetworkConnection;


public class BarcodeScanResultActivity extends AppCompatActivity {
    ScanData scannedData = new ScanData();
    TextView surname, givenname, dob, nin, otherName, documentId;
    Button verifybtn, verifiedBtn;
    Activity activity = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan_result);
        verifybtn = (Button) findViewById(R.id.verify);
        verifiedBtn = (Button) findViewById(R.id.verified_btn);
        otherName = (TextView) findViewById(R.id.othername);

        if(RegistrationData.getIsmatched() != null)
        if(RegistrationData.getIsmatched()){
            verifybtn.setVisibility(View.GONE);
            verifiedBtn.setVisibility(View.VISIBLE);
        }else{
            verifybtn.setVisibility(View.VISIBLE);
            verifiedBtn.setVisibility(View.GONE);
        }

        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FingerPrintScannerActivity.class);
                intent.putExtra("ScanningType", "ugandanFinger");
                startActivityForResult(intent, 199);
            }
        });

        verifiedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, OnDemandRegistrationActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });

        if(ScanData.getScannedBarcodeData() != null){
            scannedData = ScanData.getScannedBarcodeData();
            if(scannedData != null)
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(BarcodeScanResultActivity.this);
    }

    private void initView() {
        surname = (TextView)findViewById(R.id.surnamebarcode);
        givenname = (TextView) findViewById(R.id.givennamebarcode);
        dob = (TextView) findViewById(R.id.DOBbarcode);
        nin = (TextView) findViewById(R.id.ninbarcode);
        verifybtn = (Button) findViewById(R.id.verify);
        documentId = (TextView) findViewById(R.id.document_id);

        if(scannedData.getLastName() != null ){
            surname.setText(scannedData.getLastName().toString());
        }else{
            surname.setText("");
        }

        if(scannedData.getFirstName() != null ){
            givenname.setText(scannedData.getFirstName().toString());
        }else{
            givenname.setText("");
        }

        if(scannedData.getDateOfBirth() != null ){
            dob.setText(scannedData.getDateOfBirth().toString());
        }else{
            dob.setText("");
        }

        if(scannedData.getPassportNo() != null ){
            nin.setText(scannedData.getPassportNo().toString());
        }else{
            nin.setText("");
        }

        if(scannedData.getOtherName() != null ){
            otherName.setText(scannedData.getOtherName().toString());
        }else{
            otherName.setText("");
        }

        if(scannedData.getDocumentId() != null){
            documentId.setText(scannedData.getDocumentId().toString());
        }else{
            documentId.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 199 ) {

            if (RegistrationData.getCapturedFingerprintDrawable() != null) {
                if(RegistrationData.getIsmatched()) {
                    verifiedBtn.setVisibility(View.VISIBLE);
                    verifybtn.setVisibility(View.GONE);
                }else{
                    verifiedBtn.setVisibility(View.GONE);
                    verifybtn.setVisibility(View.VISIBLE);
                }
                 }
        }
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
