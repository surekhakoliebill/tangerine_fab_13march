package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aryagami.R;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.RegistrationData;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.MyToast;

public class DeviceOrderScanIMEIActivity extends AppCompatActivity {

    LinearLayout scanImeiLayout, searchIMEILayout, imeiDetailsLayout;
    Button scanIMEIButton, backButton, saveAndContinue;
    ImageButton searchIMEIButton;
    EditText scannedIMEIText;
    ToggleButton imeiToggleButton;
    Activity activity = this;
    NewOrderCommand command = new NewOrderCommand();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_imei);

        if(NewOrderCommand.getOnDemandNewOrderCommand() != null){
            command = NewOrderCommand.getOnDemandNewOrderCommand();
        }

        scanImeiLayout = (LinearLayout)findViewById(R.id.imei_scan_layout);
        scanIMEIButton = (Button)findViewById(R.id.scan_imei_btn);

        searchIMEILayout = (LinearLayout)findViewById(R.id.imei_container_layout);
        searchIMEIButton = (ImageButton) findViewById(R.id.search_imei_details_btn);

        imeiDetailsLayout = (LinearLayout)findViewById(R.id.imei_details_layout);

        scannedIMEIText = (EditText)findViewById(R.id.scanned_imei_text);

        backButton = (Button)findViewById(R.id.imei_back_btn);
        saveAndContinue = (Button)findViewById(R.id.imei_save_and_continue_btn);

        imeiToggleButton = (ToggleButton) findViewById(R.id.imei_toggle);
        imeiToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RegistrationData.setIsScanICCID(false);
                    searchIMEILayout.setVisibility(View.VISIBLE);
                    scanImeiLayout.setVisibility(View.GONE);
                    imeiDetailsLayout.setVisibility(View.GONE);

                } else {
                    RegistrationData.setIsScanICCID(true);
                    searchIMEILayout.setVisibility(View.GONE);
                    scanImeiLayout.setVisibility(View.VISIBLE);
                    imeiDetailsLayout.setVisibility(View.GONE);
                }
            }
        });

        scanIMEIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceOrderScanIMEIActivity.this, BarcodeScannerActivity.class);
                startActivity(intent);
            }
        });

        if(!RegistrationData.getScanICCIDData().isEmpty()){
            searchIMEILayout.setVisibility(View.VISIBLE);
            scannedIMEIText.setText(RegistrationData.getScanICCIDData());
        }

        searchIMEIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!scannedIMEIText.getText().toString().isEmpty()){
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    //API FOR SEARCH IMEI NUMBER

                }else{
                    MyToast.makeMyToast(activity,"IMEI data should not be Empty.", Toast.LENGTH_SHORT);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DeviceOrderPaymentActivity.class);
                startActivity(intent);
            }
        });

    }
}
