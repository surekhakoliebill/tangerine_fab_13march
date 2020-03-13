package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class EditUserInformationActivity extends AppCompatActivity {
    UserRegistration userData;
    public Activity activity = this;

    RadioButton personalRadioBtn, companyRadioBtn, nationalBtn, foreignerBtn, refugeeBtn;
    LinearLayout personalRegContainer, companyRegContainer, nationalContainer, foreignerContainer, refugeeContainer;
    UserRegistration userRegistrationData, userRegistration;
    ProgressDialog progressDialog;
    UserLogin userLoginResult;
    int mYear, mMonth, mDay;
    private EditText mEditText;
    Button scan, backButton, saveAndContinue;
    TextInputEditText give_name, document_id, foreigner_nationality, surname, datePickerText, user_name, email_id, phone_number, national_id_number, dob, foreigner_passport_no, foreigner_identity_no, company_name, tin_number, certificate_number, c_user_name, c_email, c_phone_number, c_company, c_tin_number, c_certificate_number, c_primary_username, c_primary_phone_number, c_primary_email, c_alternate_user_name, c_alternate_phone_number, c_alternate_email, issuedDate;
    TextInputEditText refugeeId, visaExpiryDate, refugeeNationality, ugandaNationality;
    TextInputLayout surnameLayout, visaValidityLayout;
    Spinner passportTypeSpinner;
    String[] passportTypes = {"Validity Date", "Life time validity", "E. A Community – TZ", "E. A Community – KE", "E. A Community – BR", "E. A Community – SS", "E. A Community – RW"};

    public void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_personal_account1);
        give_name = (TextInputEditText) findViewById(R.id.give_name);
        surname = (TextInputEditText) findViewById(R.id.surname);
        email_id = (TextInputEditText) findViewById(R.id.email_id);
        phone_number = (TextInputEditText) findViewById(R.id.phone_no);
        national_id_number = (TextInputEditText) findViewById(R.id.national_id);
        document_id = (TextInputEditText) findViewById(R.id.document_ID);
        visaExpiryDate = (TextInputEditText) findViewById(R.id.visa_expiry_date);
        foreigner_passport_no = (TextInputEditText) findViewById(R.id.foreigner_passport_id);
        foreigner_nationality = (TextInputEditText) findViewById(R.id.foreigner_nationality_id);
        tin_number = (TextInputEditText) findViewById(R.id.c_tin_no);
        certificate_number = (TextInputEditText) findViewById(R.id.c_certi_innumber);
        c_user_name = (TextInputEditText) findViewById(R.id.c_user_name);
        c_email = (TextInputEditText) findViewById(R.id.c_email_id);
        c_phone_number = (TextInputEditText) findViewById(R.id.c_phone_no);
        c_company = (TextInputEditText) findViewById(R.id.c_company_name);
        c_tin_number = (TextInputEditText) findViewById(R.id.c_tin_no);
        c_certificate_number = (TextInputEditText) findViewById(R.id.c_certi_innumber);
        c_primary_username = (TextInputEditText) findViewById(R.id.c_primary_user_name);
        c_primary_phone_number = (TextInputEditText) findViewById(R.id.c_primary_user_phone_number);
        c_primary_email = (TextInputEditText) findViewById(R.id.c_primary_person_emailid);
        c_alternate_user_name = (TextInputEditText) findViewById(R.id.c_alternate_user_name);
        c_alternate_phone_number = (TextInputEditText) findViewById(R.id.c_alternate_user_phone_number);
        c_alternate_email = (TextInputEditText) findViewById(R.id.c_alternate_person_emailid);
        // for Refugee User
        issuedDate = (TextInputEditText) findViewById(R.id.issued_date);
        refugeeNationality = (TextInputEditText) findViewById(R.id.refugee_nationality);
        refugeeId = (TextInputEditText) findViewById(R.id.refugee_id);
        visaValidityLayout = (TextInputLayout) findViewById(R.id.visa_expiry_layout);
        passportTypeSpinner = (Spinner) findViewById(R.id.addressType_spinner);
        surnameLayout = (TextInputLayout) findViewById(R.id.surname_layout);
        ugandaNationality = (TextInputEditText) findViewById(R.id.ugandan_nationality_id);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup12);
        personalRegContainer = (LinearLayout) findViewById(R.id.reg_personal1_container);
        companyRegContainer = (LinearLayout) findViewById(R.id.reg_company1_container);
        personalRadioBtn = (RadioButton) findViewById(R.id.personal11_reg_radio_btn);
        companyRadioBtn = (RadioButton) findViewById(R.id.compnay11_reg_radio_btn);

        RadioGroup radio = (RadioGroup) findViewById(R.id.radiogrpid);
        nationalContainer = (LinearLayout) findViewById(R.id.reg_national_container);
        foreignerContainer = (LinearLayout) findViewById(R.id.reg_foreigner_container);
        refugeeContainer = (LinearLayout) findViewById(R.id.reg_refugee_container);
        nationalBtn = (RadioButton) findViewById(R.id.natid_btn);
        foreignerBtn = (RadioButton) findViewById(R.id.frnr_btn);
        refugeeBtn = (RadioButton) findViewById(R.id.refugee_btn);
        datePickerText = (TextInputEditText)findViewById(R.id.dob_edittext);

        backButton = (Button) findViewById(R.id.cancel_btn);
        saveAndContinue = (Button) findViewById(R.id.savecontinue12_btn);
        saveAndContinue.setVisibility(INVISIBLE);

        if(RegistrationData.getIsUgandanUserUpdate()) {
            nationalBtn.setChecked(true);
            foreignerBtn.setClickable(false);
            refugeeBtn.setClickable(false);
            nationalContainer.setVisibility(View.VISIBLE);
            foreignerContainer.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.VISIBLE);
            saveAndContinue.setVisibility(View.INVISIBLE);

        }else if(RegistrationData.getIsForeignerUserUpdate()) {
            foreignerBtn.setChecked(true);
            nationalBtn.setClickable(false);
            refugeeBtn.setClickable(false);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.VISIBLE);
            surnameLayout.setVisibility(View.VISIBLE);
            refugeeContainer.setVisibility(View.GONE);
            saveAndContinue.setVisibility(View.VISIBLE);

        }else if(RegistrationData.getIsRefugeeUserUpdate()){
            refugeeBtn.setChecked(true);
            nationalBtn.setClickable(false);
            foreignerBtn.setClickable(false);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.VISIBLE);
            saveAndContinue.setVisibility(View.VISIBLE);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.personal11_reg_radio_btn:
                        companyRadioBtn.setClickable(false);
                        personalRegContainer.setVisibility(View.VISIBLE);
                        companyRegContainer.setVisibility(View.GONE);
                        if(nationalBtn.isChecked()){
                            saveAndContinue.setVisibility(View.INVISIBLE);
                        }else{
                            saveAndContinue.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.compnay11_reg_radio_btn :
                        personalRadioBtn.setClickable(false);
                        personalRegContainer.setVisibility(View.GONE);
                        companyRegContainer.setVisibility(View.VISIBLE);
                        saveAndContinue.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.natid_btn:
                        foreignerBtn.setClickable(false);
                        refugeeBtn.setClickable(false);
                        RegistrationData.setIsForeignerUserUpdate(false);
                        RegistrationData.setIsUgandanUserUpdate(true);
                        RegistrationData.setIsRefugeeUserUpdate(false);
                        nationalContainer.setVisibility(View.VISIBLE);
                        foreignerContainer.setVisibility(View.GONE);
                        refugeeContainer.setVisibility(View.GONE);
                        surnameLayout.setVisibility(View.VISIBLE);
                        saveAndContinue.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.frnr_btn:
                        nationalBtn.setClickable(false);
                        refugeeBtn.setClickable(false);
                        RegistrationData.setIsForeignerUserUpdate(true);
                        RegistrationData.setIsUgandanUserUpdate(false);
                        RegistrationData.setIsRefugeeUserUpdate(false);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.VISIBLE);
                        refugeeContainer.setVisibility(View.GONE);
                        surnameLayout.setVisibility(View.VISIBLE);
                        saveAndContinue.setVisibility(View.VISIBLE);
                        break;

                    case R.id.refugee_btn:
                        nationalBtn.setClickable(false);
                        foreignerBtn.setClickable(false);
                        RegistrationData.setIsForeignerUserUpdate(false);
                        RegistrationData.setIsUgandanUserUpdate(false);
                        RegistrationData.setIsRefugeeUserUpdate(true);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.GONE);
                        refugeeContainer.setVisibility(View.VISIBLE);
                        surnameLayout.setVisibility(GONE);
                        saveAndContinue.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        if(RegistrationData.getEditUserProfile() != null){
            userData = RegistrationData.getEditUserProfile();
            clreaPreviousData();
            setUserInfo(userData);
        }

        scan = (Button)findViewById(R.id.scan_btn);
        scan.setVisibility(GONE);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationData.setIsUpdatedUserScanData(true);
                Intent intent = new Intent(activity, ScanNowPassportImageActivity.class);
                startActivity(intent);
            }
        });

            if (ScanData.getScanData() != null) {
                setScannedDataValues(ScanData.getScanData());
                RegistrationData.setIsUpdatedUserScanData(false);
            }

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegistration userRegistration = new UserRegistration();
                userRegistration = collectAllEditedUserData();
                if(userRegistration != null) {
                   RegistrationData.setUpdateUserProfileData(userRegistration);
                    Intent intent = new Intent(activity, EditBillingAddressInfoActivity.class);
                    startActivity(intent);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, passportTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        passportTypeSpinner.setAdapter(adapter);
        passportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Validity Date"))
                {
                    visaValidityLayout.setVisibility(View.VISIBLE);
                }else{
                    visaValidityLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        datePickerText.setInputType(InputType.TYPE_NULL);
        datePickerText.requestFocus();
        datePickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -18);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datePickerText.setText(((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) + "/"
                                        + ((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1)) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 365.25 * 10)));
                datePickerDialog.show();

            }
        });

        issuedDate.setInputType(InputType.TYPE_NULL);
        issuedDate.requestFocus();
        issuedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                issuedDate.setText(((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) + "/"
                                        + ((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1)) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                //Disable future dates in Android date picker
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        visaExpiryDate.setInputType(InputType.TYPE_NULL);
        visaExpiryDate.requestFocus();
        visaExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                visaExpiryDate.setText(((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) + "/"
                                        + ((monthOfYear) < 9 ? ("0" + (monthOfYear + 1)) : (monthOfYear + 1)) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                // Disable past dates in Android date picker
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        final Button verify_btn = (Button)findViewById(R.id.verifyidentitynumber_btn);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    userRegistration = collectAllEditedUserData();
                    if (userRegistration != null){
                        progressDialog = ProgressDialogUtil.startProgressDialog(activity,"Verifying NIRA......");
                        serviceHandler.postUserNiraVerification(userRegistration, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("NIRA verification Success!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    saveAndContinue.setVisibility(View.VISIBLE);
                                                }
                                            });
                                    alertDialog.show();
                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    Toast.makeText(activity, userLogin.status, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                Toast.makeText(activity, "NIRA VERIFICATION FAILED!"+ "Error: "+error, Toast.LENGTH_SHORT).show();
                                BugReport.postBugReport(activity, Constants.emailId,"Error:"+error+"STATUS:"+status,"EDIT USER - NIRA VERIFICATION");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(activity, Constants.emailId,"Message"+e.getMessage()+"\n ERROR:-"+e.getCause(),"EDIT USER - NIRA VERIFICATION");
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, EditUserMainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(EditUserInformationActivity.this);
    }

    private void clreaPreviousData() {
        give_name.setText("");
        surname.setText("");
        email_id.setText("");
        datePickerText.setText("");
        phone_number.setText("");
        foreigner_passport_no.setText("");
        document_id.setText("");
        foreigner_nationality.setText("");
        visaExpiryDate.setText("");
        national_id_number.setText("");
        document_id.setText("");
        ugandaNationality.setText("");
        refugeeId.setText("");
        refugeeNationality.setText("");
        issuedDate.setText("");

        c_user_name.setText("");
        c_email.setText("");
        c_company.setText("");
        c_tin_number.setText("");
        c_certificate_number.setText("");
        c_primary_username.setText("");
        c_primary_email.setText("");
        c_primary_phone_number.setText("");
        c_alternate_user_name.setText("");
        c_alternate_email.setText("");
        c_alternate_phone_number.setText("");
        c_phone_number.setText("");
    }

    private void setScannedDataValues(ScanData scanData) {

        if(scanData.getFirstName().toString() != null | scanData.getLastName().toString()!= null | scanData.getPassportNo().toString() != null | scanData.getCountry().toString() != null | scanData.getDateOfBirth() != null){

            give_name.setText(scanData.getFirstName().toString());

            surname.setText(scanData.getLastName().toString());
            document_id.setText(scanData.getPassportNo().toString());
            foreigner_nationality.setText(scanData.getCountry().toString());
            ugandaNationality.setText(scanData.getCountry().toString());

            if(scanData.getDateOfBirth() != null) {
                String dob = scanData.getDateOfBirth().toString();
                if (dob != null) {
                    datePickerText.setText(dob.replace("-", "/"));
                }
            }

            if(scanData.ugandanNationalId != null){
                national_id_number.setText(scanData.ugandanNationalId.toString());
            }

            if(scanData.getPassportNo()!= null){
                foreigner_passport_no.setText(scanData.getPassportNo().toString());
            }
        }
    }

    private UserRegistration collectAllEditedUserData() {
        UserRegistration data = new UserRegistration();
        data = userData;
        if (personalRadioBtn.isChecked()) {
            if(!give_name.getText().toString().isEmpty()){
                data.fullName = give_name.getText().toString();
            }else{
                MyToast.makeMyToast(activity,"Please Enter Given Name", Toast.LENGTH_SHORT);
                return null;
            }

            if(!email_id.getText().toString().isEmpty()){
                data.email = email_id.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Email Id", Toast.LENGTH_SHORT);
                return null;
            }

            if(!datePickerText.getText().toString().isEmpty()){
                data.dob = datePickerText.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Date of Birth", Toast.LENGTH_SHORT);
                return null;
            }

            data.phoneNumber = "256"+phone_number.getText().toString();

            if (foreignerBtn.isChecked()) {
                if(!surname.getText().toString().isEmpty()){
                    data.surname = surname.getText().toString();
                    data.surName = surname.getText().toString();
                }else{
                    MyToast.makeMyToast(activity,"Please Enter Surname", Toast.LENGTH_SHORT);
                    return null;
                }

                if(!foreigner_passport_no.getText().toString().isEmpty()){
                    data.identityNumber = foreigner_passport_no.getText().toString();
                }else{
                    MyToast.makeMyToast(activity,"Please Enter Passport Number", Toast.LENGTH_SHORT);
                    return null;
                }

                if(passportTypeSpinner.getSelectedItem().toString().equals("Validity Date") ){
                    if(!visaExpiryDate.getText().toString().isEmpty()){
                        data.visaValidityDate = visaExpiryDate.getText().toString();
                        data.visaValidityType = "Limited Date";

                    }else{
                        MyToast.makeMyToast(activity,"Please Enter Visa Validity Date.", Toast.LENGTH_SHORT);
                        return null;
                    }
                }else{
                    data.visaValidityType = passportTypeSpinner.getSelectedItem().toString();
                }

                if(!foreigner_nationality.getText().toString().isEmpty()){
                    data.nationality = foreigner_nationality.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Nationality", Toast.LENGTH_SHORT);
                    return null;
                }
                data.nationalIdentity = "Passport No";

            } else if (nationalBtn.isChecked()) {

                if(!surname.getText().toString().isEmpty()){
                    data.surname = surname.getText().toString();
                    data.surName = surname.getText().toString();
                }else{
                    MyToast.makeMyToast(activity,"Please Enter Surname", Toast.LENGTH_SHORT);
                    return null;
                }

                if(!national_id_number.getText().toString().isEmpty()){
                    data.identityNumber = national_id_number.getText().toString();
                    data.nin = national_id_number.getText().toString();
                }else{
                    MyToast.makeMyToast(activity,"Please Enter National ID Number", Toast.LENGTH_SHORT);
                    return null;
                }
                if(!document_id.getText().toString().isEmpty()){
                    data.documentId = document_id.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Document ID Number", Toast.LENGTH_SHORT);
                    return null;
                }
                if(!ugandaNationality.getText().toString().isEmpty()){
                    data.nationality = ugandaNationality.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Nationality", Toast.LENGTH_SHORT);
                    return null;
                }

                data.nationalIdentity = "Ugandan NationalID";

            } else if (refugeeBtn.isChecked()) {

                if(!issuedDate.getText().toString().isEmpty()){
                    data.refugeeIssueDate = issuedDate.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Issued Date", Toast.LENGTH_SHORT);
                    return null;
                }
                if(!refugeeNationality.getText().toString().isEmpty()){
                    data.nationality = refugeeNationality.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Nationality", Toast.LENGTH_SHORT);
                    return null;
                }
                if(!refugeeId.getText().toString().isEmpty()){
                    data.refugeeIdentityNumber = refugeeId.getText().toString();

                }else{
                    MyToast.makeMyToast(activity,"Please Enter Refugee ID Number", Toast.LENGTH_SHORT);
                    return null;
                }

                data.nationalIdentity = "Refugee";

            }
            data.registrationType = "personal";

        } else if (companyRadioBtn.isChecked()) {
            data.alternatePersonName = c_alternate_user_name.getText().toString();
            data.alternatePersonEmailId = c_alternate_email.getText().toString();
            data.alternatePhoneNumber = "256"+c_alternate_phone_number.getText().toString();

            if(!c_user_name.getText().toString().isEmpty()){
                data.userName = c_user_name.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Username", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_email.getText().toString().isEmpty()){
                data.email = c_email.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Email", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_company.getText().toString().isEmpty()){
                data.company = c_company.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Company Name", Toast.LENGTH_SHORT);
                return null;
            }
            if(!c_tin_number.getText().toString().isEmpty() || !c_certificate_number.getText().toString().isEmpty()){
                data.tinNumber = c_tin_number.getText().toString();
                data.coiNumber = c_certificate_number.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter TIN Number Or Certificate Number.", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_primary_username.getText().toString().isEmpty()){
                data.primaryPersonName = c_primary_username.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Primary Username", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_primary_email.getText().toString().isEmpty()){
                data.primaryPersonEmailId = c_primary_email.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Primary Person Email Id.", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_primary_phone_number.getText().toString().isEmpty()){
                data.primaryPersonMobileNumber = "256"+c_primary_phone_number.getText().toString();
                data.primaryPersonPhoneNumber = "256"+c_primary_phone_number.getText().toString();
            }else{
                MyToast.makeMyToast(activity,"Please Enter Primary Person Phone Number", Toast.LENGTH_SHORT);
                return null;
            }

            if(!c_phone_number.getText().toString().isEmpty()){
                data.phoneNumber = "256"+c_phone_number.getText().toString();

            }else{
                MyToast.makeMyToast(activity,"Please Enter Phone Number", Toast.LENGTH_SHORT);
                return null;
            }
            data.registrationType = "company";
        }

        return  data;
    }

    private void setUserInfo(UserRegistration userData) {

        if (userData.registrationType != null)
            if (userData.registrationType.toString().equals("personal")) {
                personalRadioBtn.setChecked(true);
                companyRadioBtn.setClickable(false);
                personalRegContainer.setVisibility(VISIBLE);
                companyRegContainer.setVisibility(GONE);
                if (nationalBtn.isChecked()) {
                    saveAndContinue.setVisibility(INVISIBLE);
                } else {
                    saveAndContinue.setVisibility(VISIBLE);
                }

            } else if (userData.registrationType.toString().equals("company")) {
                companyRadioBtn.setChecked(true);
                personalRadioBtn.setClickable(false);
                personalRegContainer.setVisibility(GONE);
                companyRegContainer.setVisibility(VISIBLE);
                saveAndContinue.setVisibility(VISIBLE);
            }

        if (personalRadioBtn.isChecked()) {

            if(userData.fullName!= null){
                give_name.setText(userData.fullName.toString());
            }

            if(userData.surname != null){
                surname.setText(userData.surname.toString());
            }

            if(userData.email != null){
                email_id.setText(userData.email.toString());
            }

            if (userData.dob != null) {
                datePickerText.setText(userData.dob.toString());
            }

            if(userData.phoneNumber != null){
                if(userData.phoneNumber.length() == 12) {
                    String ex = userData.phoneNumber.toString().substring(3, 12);
                    phone_number.setText(ex);
                }else if(userData.phoneNumber.length() < 12){
                    phone_number.setText(userData.phoneNumber);
                }
            }

            if (userData.nationalIdentity != null)
                if (userData.nationalIdentity.toString().equals("Passport No")) {
                    foreignerBtn.setChecked(true);
                    nationalContainer.setVisibility(View.GONE);
                    foreignerContainer.setVisibility(View.VISIBLE);
                    surnameLayout.setVisibility(View.VISIBLE);
                    refugeeContainer.setVisibility(View.GONE);
                    saveAndContinue.setVisibility(View.VISIBLE);


                    if(userData.identityNumber!= null){
                        foreigner_passport_no.setText(userData.identityNumber.toString());
                    }
                    if(userData.visaValidityType != null){
                        if(userData.visaValidityType.equals("Limited Date") || userData.visaValidityType.equals("Validity Date") ){
                            visaValidityLayout.setVisibility(View.VISIBLE);
                            if(userData.visaValidityDate!= null){
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String[] issueDate = userData.visaValidityDate.toString().split(" ");
                                try {
                                    Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(issueDate[0]);
                                    visaExpiryDate.setText(dateFormat.format(date2).toString());
                                    userData.visaValidityDate = visaExpiryDate.getText().toString();
                                    } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        }else{
                            // passportTypeSpinner
                            visaValidityLayout.setVisibility(View.GONE);
                        }
                    }
                    if(userData.nationality!= null){
                        foreigner_nationality.setText(userData.nationality.toString());
                    }

                } else if (userData.nationalIdentity.toString().equals("Ugandan NationalID")) {
                    nationalBtn.setChecked(true);
                    foreignerBtn.setClickable(false);
                    refugeeBtn.setClickable(false);
                    nationalContainer.setVisibility(View.VISIBLE);
                    foreignerContainer.setVisibility(View.GONE);
                    refugeeContainer.setVisibility(View.GONE);
                    surnameLayout.setVisibility(View.VISIBLE);
                    saveAndContinue.setVisibility(View.VISIBLE);

                    if(userData.identityNumber!= null){
                        national_id_number.setText(userData.identityNumber.toString());
                    }
                    if(userData.documentId!= null){
                        document_id.setText(userData.documentId.toString());
                    }
                    if(userData.nationality!= null){
                        ugandaNationality.setText(userData.nationality.toString());
                    }

                } else if (userData.nationalIdentity.toString().equals("Refugee")) {
                    refugeeBtn.setChecked(true);
                    nationalBtn.setClickable(false);
                    foreignerBtn.setClickable(false);
                    nationalContainer.setVisibility(View.GONE);
                    foreignerContainer.setVisibility(View.GONE);
                    surnameLayout.setVisibility(View.GONE);
                    refugeeContainer.setVisibility(View.VISIBLE);
                    saveAndContinue.setVisibility(View.VISIBLE);

                    if(userData.refugeeIdentityNumber!= null){
                        refugeeId.setText(userData.refugeeIdentityNumber.toString());
                    }

                    if(userData.refugeeIssueDate!= null){
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String[] issueDate = userData.visaValidityDate.toString().split(" ");
                        try {
                            Date date2=new SimpleDateFormat("yyyy-MM-dd").parse(issueDate[0]);
                            issuedDate.setText(dateFormat.format(date2).toString());
                            userData.refugeeIssueDate = issuedDate.getText().toString();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if(userData.nationality!= null){
                        refugeeNationality.setText(userData.nationality.toString());
                    }
                }

        } else if (companyRadioBtn.isChecked()) {
            if(userData.userName != null){
                c_user_name.setText(userData.userName.toString());
            }
            if(userData.email != null){
                c_email.setText(userData.email.toString());
            }
            if(userData.company != null){
                c_company.setText(userData.company.toString());
            }

            if(userData.tinNumber != null){
                c_tin_number.setText(userData.tinNumber.toString());
            }

            if(userData.coiNumber != null){
                c_certificate_number.setText(userData.coiNumber.toString());
            }

            if(userData.primaryPersonName != null){
                c_primary_username.setText(userData.primaryPersonName.toString());
            }

            if(userData.primaryPersonEmailId != null){
                c_primary_email.setText(userData.primaryPersonEmailId.toString());
            }

            if(userData.primaryPersonPhoneNumber != null){
                if(userData.primaryPersonPhoneNumber.length() == 12){
                    String ex = userData.primaryPersonPhoneNumber.toString().substring(3, 12);
                    c_primary_phone_number.setText(ex);
                }else {
                    c_primary_phone_number.setText(userData.primaryPersonPhoneNumber.toString());
                }
            }else if(userData.primaryPersonMobileNumber != null){
                if(userData.primaryPersonMobileNumber.length() == 12){
                    String ex = userData.primaryPersonMobileNumber.toString().substring(3, 12);
                    c_primary_phone_number.setText(ex);
                }else {
                    c_primary_phone_number.setText(userData.primaryPersonMobileNumber.toString());
                }
            }

            if(userData.alternatePersonName != null){
                c_alternate_user_name.setText(userData.alternatePersonName.toString());
            }

            if(userData.alternatePersonEmailId != null){
                c_alternate_email.setText(userData.alternatePersonEmailId.toString());
            }

            if(userData.alternatePhoneNumber != null){
                if(userData.alternatePhoneNumber.length() == 12){
                    String ex = userData.alternatePhoneNumber.toString().substring(3,12);
                    c_alternate_phone_number.setText(ex);
                }else{
                    c_alternate_phone_number.setText(userData.alternatePhoneNumber);
                }
            }

            if(userData.phoneNumber != null){
                if(userData.phoneNumber.length() == 12){
                    String ex = userData.phoneNumber.toString().substring(3, 12);
                    c_phone_number.setText(ex);
                }else {
                    c_phone_number.setText(userData.phoneNumber);
                }
            }

        }

    }
}
