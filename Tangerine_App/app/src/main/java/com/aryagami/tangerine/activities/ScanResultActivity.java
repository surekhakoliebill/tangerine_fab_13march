package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*import com.accurascan.demo.R;
import com.accurascandemo.model.ScanData;
import com.docrecog.scan.CameraActivity;
import com.docrecog.scan.RecogEngine;*/

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.util.MyToast;
import com.docrecog.scan.CameraActivity;
import com.docrecog.scan.RecogEngine;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * This class used to display data after scanning Passport & ID MRZ document
 */

public class ScanResultActivity extends BaseActivity implements View.OnClickListener {

    private static final int RETURN_RESULT = 1;
    private ImageView ivBack, ivUserProfile;
    private TextView tvLastName, tvFirstName, tvPassportNo, tvCountry, tvNationality,
            tvSex, tvDOB, tvDateOfExpiry, tvDocumentType, tvMrz, tvOtherId, tvRet, tvDocumentNoCheck, tvDOBCheck, tvDateOfExpiryCheck, tvOtherIdCheck, tvSecondRowCheckNumber;
    private ScanData scanData;
    Activity activity = this;

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUi() {

        ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
        tvRet = (TextView)findViewById(R.id.tvRet);
        tvDocumentType = (TextView)findViewById(R.id.tvDocumentType);
        tvMrz = (TextView)findViewById(R.id.tvMrz);
        tvLastName = (TextView)findViewById(R.id.tvLastName);
        tvFirstName = (TextView)findViewById(R.id.tvFirstName);
        tvPassportNo = (TextView)findViewById(R.id.tvPassportNo);
        tvCountry = (TextView)findViewById(R.id.tvCountry);
        tvNationality = (TextView)findViewById(R.id.tvNationality);
        tvSex = (TextView)findViewById(R.id.tvSex);
        tvDOB = (TextView)findViewById(R.id.tvDOB);
        tvDateOfExpiry = (TextView)findViewById(R.id.tvDateOfExpiry);
        tvDocumentNoCheck = (TextView)findViewById(R.id.tvDocumentNoCheck);
        tvDOBCheck = (TextView)findViewById(R.id.tvDOBCheck);
        tvOtherId = (TextView)findViewById(R.id.tvOtherId);
        tvDateOfExpiryCheck = (TextView)findViewById(R.id.tvDateOfExpiryCheck);
        tvOtherIdCheck = (TextView)findViewById(R.id.tvOtherIdCheck);
        tvSecondRowCheckNumber = (TextView)findViewById(R.id.tvSecondRowCheckNumber);

        findViewById(R.id.tvSave).setOnClickListener(this);

        setUserPassportProfile();
    }

    private void setUserPassportProfile() {

        Log.d("Result", RecogEngine.g_recogResult.GetResultString());


        scanData = new ScanData();

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.surname)) {
            tvLastName.setText(RecogEngine.g_recogResult.surname);
            scanData.setLastName(RecogEngine.g_recogResult.surname);
        } else {
            tvLastName.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.givenname)) {
            tvFirstName.setText(RecogEngine.g_recogResult.givenname);
            scanData.setFirstName(RecogEngine.g_recogResult.givenname);
        } else {
            tvFirstName.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.docnumber)) {
            tvPassportNo.setText(RecogEngine.g_recogResult.docnumber);
            scanData.setPassportNo(RecogEngine.g_recogResult.docnumber);
        } else {
            tvPassportNo.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.country)) {
            tvCountry.setText(RecogEngine.g_recogResult.country);
            scanData.setCountry(RecogEngine.g_recogResult.country);
        } else {
            tvCountry.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.nationality)) {
            tvNationality.setText(RecogEngine.g_recogResult.nationality);
            scanData.setCountry(RecogEngine.g_recogResult.nationality);
        } else {
            tvNationality.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.sex)) {
            if (RecogEngine.g_recogResult.sex.equalsIgnoreCase("F")) {
                tvSex.setText("Female");
                scanData.setGender("Female");
            } else {
                tvSex.setText("Male");
                scanData.setGender("Male");
            }
        } else {
            tvSex.setText("");
        }

        scanData.setMrz(RecogEngine.g_recogResult.ret);
        tvMrz.setText(RecogEngine.g_recogResult.lines);

        if(RecogEngine.g_recogResult.ret == 0){
            tvRet.setText("failed");
            MyToast.makeMyToast(activity,"Incorrect Scanning, please Scan Doc",Toast.LENGTH_SHORT);
           activity.finish();
            Intent intent = new Intent(activity,ScanNowPassportImageActivity.class);
            startActivity(intent);

        }else if(RecogEngine.g_recogResult.ret == 1){
            tvRet.setText("Correct MRZ");
        }else if(RecogEngine.g_recogResult.ret == 0){
            tvRet.setText("Incorrect MRZ");
            MyToast.makeMyToast(activity,"Incorrect Scanning, please Scan Doc",Toast.LENGTH_SHORT);
            activity.finish();
            Intent intent = new Intent(activity,ScanNowPassportImageActivity.class);
            startActivity(intent);
        }

        if(!TextUtils.isEmpty(RecogEngine.g_recogResult.docchecksum)){
            tvDocumentNoCheck.setText(RecogEngine.g_recogResult.docchecksum);
        }

        if(!TextUtils.isEmpty(RecogEngine.g_recogResult.birthchecksum)){
            tvDOBCheck.setText(RecogEngine.g_recogResult.birthchecksum);
        }

        if(!TextUtils.isEmpty(RecogEngine.g_recogResult.expirationchecksum)){
            tvDateOfExpiryCheck.setText(RecogEngine.g_recogResult.expirationchecksum);
        }

        if(!TextUtils.isEmpty(RecogEngine.g_recogResult.otheridchecksum)){
            tvOtherIdCheck.setText(RecogEngine.g_recogResult.otheridchecksum);
        }

        if(!TextUtils.isEmpty(RecogEngine.g_recogResult.secondrowchecksum)){
            tvSecondRowCheckNumber.setText(RecogEngine.g_recogResult.secondrowchecksum);
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.docType)) {
            if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("P")) {
                scanData.setDocumentType("PASSPORT");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("V")) {
                scanData.setDocumentType("VISA");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("I")) {
                scanData.setDocumentType("ID");
            } else if (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("D")) {
                scanData.setDocumentType("DL");
            } else {
                scanData.setDocumentType("ID");
            }
        } else if (!TextUtils.isEmpty(RecogEngine.g_recogResult.lines) && RecogEngine.g_recogResult.lines.length() > 0) {
            if (RecogEngine.g_recogResult.lines.substring(0, 1).equalsIgnoreCase("D")) {
                scanData.setDocumentType("DL");
            } else {
                scanData.setDocumentType("ID");
            }
        }

        tvDocumentType.setText(scanData.getDocumentType());

        tvOtherId.setText(RecogEngine.g_recogResult.otherid);
        scanData.setUgandanNationalId(RecogEngine.g_recogResult.otherid);
//        String line1 = RecogEngine.g_recogResult.lines.substring(0, RecogEngine.g_recogResult.lines.indexOf("<"));
//        if (line1.length() >= 9) {
//            llOtherId.setVisibility(View.VISIBLE);
//            String line = line1.substring(RecogEngine.g_recogResult.lines.substring(0, RecogEngine.g_recogResult.lines.indexOf("<")).length() - 9);
//            String lastEightChar = line.length() > 9 ? line.substring(line.length() - 9) : line;
//            tvOtherId.setText(lastEightChar);
//        }
//        else {
//            llOtherId.setVisibility(View.GONE);
//        }

        if (RecogEngine.facepick == 1) {
            if (RecogEngine.g_recogResult.faceBitmap != null) {
                Bitmap bitmap = RecogEngine.g_recogResult.faceBitmap;
                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                if (RecogEngine.g_recogResult != null && !TextUtils.isEmpty(RecogEngine.g_recogResult.docType) && (RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("P") || RecogEngine.g_recogResult.docType.substring(0, 1).equalsIgnoreCase("V"))) {
                    scanData.setUserPicture(getBytes(bitmap));
                    ivUserProfile.setImageBitmap(bitmap);
                } else {
                    ivUserProfile.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
                }
            }
        }

        DateFormat date = new SimpleDateFormat("yymmdd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.expirationdate)) {
            try {
                Date expiryDate = date.parse(RecogEngine.g_recogResult.expirationdate);
                tvDateOfExpiry.setText(newDateFormat.format(expiryDate));
                scanData.setDateOfExpiry(newDateFormat.format(expiryDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvDateOfExpiry.setText("");
        }

        if (!TextUtils.isEmpty(RecogEngine.g_recogResult.birth)) {
            try {
                Date birthDate = date.parse(RecogEngine.g_recogResult.birth.replace("<", ""));
                tvDOB.setText(newDateFormat.format(birthDate));
                scanData.setDateOfBirth(newDateFormat.format(birthDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvDOB.setText("");
        }

               ScanData.setScanData(scanData);
//        if (!TextUtils.isEmpty(scanData.getDocumentType()) && scanData.getDocumentType().equalsIgnoreCase("DL")) {
//            llFirstName.setVisibility(View.GONE);
//            llGender.setVisibility(View.GONE);
//        } else {
//            llFirstName.setVisibility(View.VISIBLE);
//            llGender.setVisibility(View.VISIBLE);
//        }


    }

    @Override
    public void onBackPressed() {
        startActivityForResult(new Intent(ScanResultActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), RETURN_RESULT);
        overridePendingTransition(0, 0);
        ScanResultActivity.this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvSave:
                if(tvRet.getText().equals("Correct MRZ")) {
                    if(RegistrationData.getIsUpdatedUserScanData()){
                        ScanResultActivity.this.finish();
                        Intent intent = new Intent(ScanResultActivity.this, EditUserInformationActivity.class);
                        startActivity(intent);
                    }else {
                        RegistrationData.setIsPassportScan(true);
                        ScanResultActivity.this.finish();
                        Intent intent = new Intent(ScanResultActivity.this, OnDemandRegistrationActivity.class);
                        startActivity(intent);
                    }
                }else{
                    MyToast.makeMyToast(activity,"Please re-scan your document.", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(ScanResultActivity.this, ScanNowPassportImageActivity.class);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == RETURN_RESULT && resultCode == RESULT_OK) {
                setUserPassportProfile();
            }
        }
    }


}
