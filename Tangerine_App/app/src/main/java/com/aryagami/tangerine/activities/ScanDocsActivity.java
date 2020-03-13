package com.aryagami.tangerine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aryagami.R;
import com.aryagami.util.CheckNetworkConnection;

public class ScanDocsActivity extends AppCompatActivity {
LinearLayout foreignerLayout, ugandanLayout;
Button niraScanDocBtn, passportScanBtn;
RadioGroup radioGroup;
RadioButton foreignRadioButton, ugandanRadioButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }

@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scandocs);
        foreignerLayout = (LinearLayout)findViewById(R.id.foreigner_layout);
        ugandanLayout = (LinearLayout)findViewById(R.id.ugandan_layout);

        niraScanDocBtn = (Button)findViewById(R.id.nira_scan_btn);
        passportScanBtn = (Button)findViewById(R.id.tvLetsGo);

        radioGroup = (RadioGroup)findViewById(R.id.scan_radio_group);
        ugandanRadioButton = (RadioButton)findViewById(R.id.ugandan_radio_btn);
        foreignRadioButton = (RadioButton)findViewById(R.id.foreigner_radio_btn);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.ugandan_radio_btn:
                        foreignerLayout.setVisibility(View.GONE);
                        ugandanLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.foreigner_radio_btn:
                        ugandanLayout.setVisibility(View.GONE);
                        foreignerLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        niraScanDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        passportScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ScanDocsActivity.this, ScanNowPassportImageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(ScanDocsActivity.this);
    }
}
