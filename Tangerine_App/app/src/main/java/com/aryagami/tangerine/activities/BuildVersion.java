package com.aryagami.tangerine.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aryagami.BuildConfig;
import com.aryagami.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BuildVersion extends AppCompatActivity {

    TextView resellerName, resellerNameText, date, version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.build_version);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        TextView txtView = new TextView(this);
        version = (TextView) findViewById(R.id.version_id);
        date = (TextView) findViewById(R.id.version_date);
        String vName = BuildConfig.VERSION_NAME;
        version.setText(vName);
        //version.setText(formattedDate+"_"+vName);
        date.setText("06-03-2020");
    }
}
