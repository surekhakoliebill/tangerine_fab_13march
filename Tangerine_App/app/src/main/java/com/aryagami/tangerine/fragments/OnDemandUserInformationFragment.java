package com.aryagami.tangerine.fragments;

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
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Roles;
import com.aryagami.data.ScanData;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.FingerPrintScannerActivity;
import com.aryagami.tangerine.activities.OnDemandNewOrderActivity;
import com.aryagami.tangerine.activities.ScanNowPassportImageActivity;
import com.aryagami.tangerine.activities.ScannerActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class OnDemandUserInformationFragment extends Fragment {

    RadioButton personalRadioBtn, retailerRadioBtn,  companyRadioBtn, nationalBtn, foreignerBtn, refugeeBtn;
    LinearLayout personalRegContainer , companyRegContainer, nationalContainer, foreignerContainer, refugeeContainer, genderLayout;
    UserRegistration userRegistrationData, userRegistration;
    ProgressDialog progressDialog;
    int mYear, mMonth, mDay;
    Button scan, backButton, btnsave;

    TextView verifyText;
    Boolean isVerified = false;

    LinearLayout datePikerLayout;
    TextInputLayout surnameLayout, visaValidityLayout;
    Spinner passportTypeSpinner, selectGenderSpinner, companyRegTypeSpinner;
    TextInputEditText give_name,document_id,foreigner_nationality,surname,datePickerText,user_name, email_id, phone_number, national_id_number, dob, foreigner_passport_no, foreigner_identity_no, company_name, tin_number, certificate_number, c_user_name, c_email, c_phone_number, c_company, c_tin_number, c_certificate_number, c_primary_username, c_primary_phone_number, c_primary_email, c_alternate_user_name, c_alternate_phone_number, c_alternate_email,issuedDate ;
    TextInputEditText refugeeId, visaExpiryDate, refugeeNationality, ugandaNationality;
    String[] passportTypes = {"Validity Date","Life time validity", "E. A Community – TZ","E. A Community – KE","E. A Community – BR","E. A Community – SS","E. A Community – RW"};
    String[] genderTypes = {"Select Gender", "Male","Female","Others"};
    String[] companyRegTypes = {"General", "NGO"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        final View view= inflater.inflate(R.layout.ondemand_userinformation_account_fragment, container, false);

        give_name = (TextInputEditText) view.findViewById(R.id.give_name);
        surname = (TextInputEditText) view.findViewById(R.id.surname);
        email_id = (TextInputEditText) view.findViewById(R.id.email_id);
        phone_number = (TextInputEditText) view.findViewById(R.id.phone_no);
        national_id_number = (TextInputEditText) view.findViewById(R.id.national_id);
        document_id = (TextInputEditText) view.findViewById(R.id.document_ID);
        // dob = (EditText) view.findViewById(R.id.dob_edittext);
        visaExpiryDate = (TextInputEditText) view.findViewById(R.id.visa_expiry_date);
        foreigner_passport_no = (TextInputEditText) view.findViewById(R.id.foreigner_passport_id);
        foreigner_nationality = (TextInputEditText) view.findViewById(R.id.foreigner_nationality_id);
        tin_number = (TextInputEditText) view.findViewById(R.id.c_tin_no);
        certificate_number = (TextInputEditText) view.findViewById(R.id.c_certi_innumber);
        c_user_name = (TextInputEditText) view.findViewById(R.id.c_user_name);
        c_email = (TextInputEditText) view.findViewById(R.id.c_email_id);
        c_phone_number = (TextInputEditText) view.findViewById(R.id.c_phone_no);
        c_company = (TextInputEditText) view.findViewById(R.id.c_company_name);
        c_tin_number = (TextInputEditText) view.findViewById(R.id.c_tin_no);
        c_certificate_number = (TextInputEditText) view.findViewById(R.id.c_certi_innumber);
        c_primary_username = (TextInputEditText) view.findViewById(R.id.c_primary_user_name);
        c_primary_phone_number = (TextInputEditText) view.findViewById(R.id.c_primary_user_phone_number);
        c_primary_email = (TextInputEditText) view.findViewById(R.id.c_primary_person_emailid);
        c_alternate_user_name = (TextInputEditText) view.findViewById(R.id.c_alternate_user_name);
        c_alternate_phone_number = (TextInputEditText) view.findViewById(R.id.c_alternate_user_phone_number);
        c_alternate_email = (TextInputEditText) view.findViewById(R.id.c_alternate_person_emailid);
        surnameLayout = (TextInputLayout)view.findViewById(R.id.surname_layout);
        // for Refugee User
        issuedDate = (TextInputEditText)view.findViewById(R.id.issued_date);
        refugeeNationality = (TextInputEditText)view.findViewById(R.id.refugee_nationality);
        refugeeId = (TextInputEditText)view.findViewById(R.id.refugee_id);
        ugandaNationality = (TextInputEditText)view.findViewById(R.id.ugandan_nationality_id);
        visaValidityLayout = (TextInputLayout)view.findViewById(R.id.visa_expiry_layout);
        passportTypeSpinner = (Spinner)view.findViewById(R.id.addressType_spinner);
        selectGenderSpinner = (Spinner)view.findViewById(R.id.select_gender_spinner);
        selectGenderSpinner.setPrompt("");
        genderLayout = (LinearLayout)view.findViewById(R.id.gender_layout);

        companyRegTypeSpinner = (Spinner)view.findViewById(R.id.company_reg_type_spinner);

        verifyText = (TextView)view.findViewById(R.id.verify_text);

        backButton = (Button)view.findViewById(R.id.cancel_btn);
         btnsave=(Button)view.findViewById(R.id.savecontinue12_btn);

        if(UserSession.getAllUserInformation(getContext())!= null){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Want to proceed with previously entered data?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setDataOnBackActivity();
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UserSession.setAllUserInformation(getActivity(),null);
                    // setEmptyData();
                }
            });

            alertDialog.show();
        }

        btnsave.setVisibility(View.INVISIBLE);
        datePickerText = (TextInputEditText) view.findViewById(R.id.dob_edittext);

        if (ScanData.getScannedBarcodeData() != null) {
            setScannedData(ScanData.getScannedBarcodeData());
        }

        checkFingerprintVerified();
        datePikerLayout = (LinearLayout) view.findViewById(R.id.date_picker_layout);
        if(RegistrationData.getOnDemandRegistrationData() != null) {
            userRegistration = RegistrationData.getOnDemandRegistrationData();
            userRegistrationData =  RegistrationData.getOnDemandRegistrationData();

        }else{
            userRegistration = new UserRegistration();
            userRegistrationData = new UserRegistration();
        }


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idNumber, userType;
                userRegistrationData = collectRegistrationData(view,userRegistration);
                if(userRegistrationData != null){

                    if(userRegistrationData.registrationType.equals("company")){
                        idNumber = userRegistration.tinNumber.trim();
                        userType = userRegistration.registrationType;
                    }else{
                        if(userRegistrationData.nationalIdentity.equals("Refugee")){
                            idNumber = userRegistration.refugeeIdentityNumber.trim();
                            userType =   userRegistrationData.nationalIdentity.trim().replace(" ","_");
                        }else{
                            idNumber = userRegistration.identityNumber.trim();
                            userType =   userRegistrationData.nationalIdentity.trim().replace(" ","_");
                        }
                    }
                    progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Checking User Details......");

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        serviceHandler.checkExistingUser(userType, idNumber, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                        UserLogin response = (UserLogin)data.get(0);
                                        if(response.status.equals("success")){
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);

                                            userRegistration = userRegistrationData;
                                            RegistrationData.setOnDemandRegistrationData(userRegistration);
                                            UserSession.setAllUserInformation(getActivity(),userRegistration);

                                            android.support.v4.app.FragmentTransaction fr=getFragmentManager().beginTransaction();
                                            fr.replace(R.id.fragment_container_registration,new OnDemandBillingAddressFragment());
                                            fr.commit();

                                        }else if(response.status.equals("INVALID_SESSION")){
                                                ReDirectToParentActivity.callLoginActivity(getActivity());
                                        }else{
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Alert!");
                                            alertDialog.setMessage("Status: "+response.status);
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();

                                                            Intent intent = new Intent(getActivity(), OnDemandNewOrderActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                            alertDialog.show();
                                        }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(getActivity(),Constants.emailId,"ERROR:"+error+"STATUS:"+status,"Checking User");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(getActivity(),Constants.emailId,"MESSAGE:"+e.getMessage()+"Cause:"+e.getCause(),"Checking User");
                    }

                }else{
                    userRegistrationData = userRegistration;
                }

                /*if(userRegistrationData != null) {
                    userRegistration = userRegistrationData;
                    RegistrationData.setOnDemandRegistrationData(userRegistration);
                    UserSession.setAllUserInformation(getActivity(),userRegistration);

                    android.support.v4.app.FragmentTransaction fr=getFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_container_registration,new OnDemandBillingAddressFragment());
                    fr.commit();
                }else{
                    userRegistrationData = userRegistration;
                }*/
            }
        });

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup12);
        personalRegContainer = (LinearLayout) view.findViewById(R.id.reg_personal1_container);
        companyRegContainer = (LinearLayout) view.findViewById(R.id.reg_company1_container);
        personalRadioBtn = (RadioButton) view.findViewById(R.id.personal11_reg_radio_btn);
        companyRadioBtn = (RadioButton)view.findViewById(R.id.compnay11_reg_radio_btn);
        retailerRadioBtn = (RadioButton)view.findViewById(R.id.retailer11_reg_radio_btn);

        RadioGroup radio = (RadioGroup) view.findViewById(R.id.radiogrpid);
        nationalContainer = (LinearLayout) view.findViewById(R.id.reg_national_container);
        foreignerContainer = (LinearLayout) view.findViewById(R.id.reg_foreigner_container);
        refugeeContainer = (LinearLayout) view.findViewById(R.id.reg_refugee_container);
        nationalBtn = (RadioButton) view.findViewById(R.id.natid_btn);
        foreignerBtn = (RadioButton) view.findViewById(R.id.frnr_btn);
        refugeeBtn = (RadioButton) view.findViewById(R.id.refugee_btn);

        give_name = (TextInputEditText) view.findViewById(R.id.give_name);
        document_id = (TextInputEditText) view.findViewById(R.id.document_ID);
        foreigner_nationality = (TextInputEditText) view.findViewById(R.id.foreigner_nationality_id);
        surname = (TextInputEditText) view.findViewById(R.id.surname);

        if(UserSession.getUserGroup(getContext()).equals("Reseller Distributor")){
            retailerRadioBtn.setVisibility(View.VISIBLE);
        }else {
            retailerRadioBtn.setVisibility(View.GONE);
        }

        if(nationalBtn.isChecked()){
            RegistrationData.setIsForeigner(false);
            RegistrationData.setIsUgandan(true);
            RegistrationData.setIsRefugee(false);
        }

        if(RegistrationData.getIsPassportScan()){
            RegistrationData.setIsForeigner(true);
            RegistrationData.setIsUgandan(false);
            RegistrationData.setIsRefugee(false);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, passportTypes);
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

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, genderTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        selectGenderSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, companyRegTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        companyRegTypeSpinner.setAdapter(adapter2);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datePickerText.setText(((dayOfMonth)<10?("0"+dayOfMonth):(dayOfMonth)) + "/"
                                        + ((monthOfYear)<9?("0"+(monthOfYear+1)):(monthOfYear+1)) + "/" + year);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                issuedDate.setText(((dayOfMonth)<10?("0"+dayOfMonth):(dayOfMonth)) + "/"
                                        + ((monthOfYear)<9?("0"+(monthOfYear+1)):(monthOfYear+1)) + "/" + year);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                visaExpiryDate.setText(((dayOfMonth)<10?("0"+dayOfMonth):(dayOfMonth)) + "/"
                                        + ((monthOfYear)<9?("0"+(monthOfYear+1)):(monthOfYear+1)) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                // Disable past dates in Android date picker
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        scan = (Button)view.findViewById(R.id.scan_btn);
        scan.setVisibility(View.VISIBLE);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nationalBtn.isChecked()){
                    RegistrationData.setIsScanICCID(false);
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    getActivity().startActivity(intent);
                }

                if(RegistrationData.getIsForeigner()){
                    RegistrationData.setIsPassportScan(true);
                    Intent intent = new Intent(getActivity(), ScanNowPassportImageActivity.class);
                    getActivity().startActivity(intent);

                }/*else if(RegistrationData.getIsRefugee()){

                    Intent intent = new Intent(getActivity(), FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "refugeeUser");
                    startActivityForResult(intent, 100);
                  //  MyToast.makeMyToast(getActivity(),"Please Fill Manually",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        if(ScanData.getScanData()!= null){
            setScannedDataValues(ScanData.getScanData());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), OnDemandNewOrderActivity.class);
                startActivity(intent);
            }
        });

        final Button verify_btn = (Button) view.findViewById(R.id.verifyidentitynumber_btn);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {

                    userRegistration = collectRegistrationData(view, new UserRegistration());

                    if (userRegistration != null){
                        progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Verifying NIRA......");

                        serviceHandler.postUserNiraVerification(userRegistration, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                UserLogin userLogin = (UserLogin) data.get(0);
                                if (userLogin.status.equals("success")) {

                                    ProgressDialogUtil.stopProgressDialog(progressDialog);

                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("NIRA Verification Success!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    isVerified = true;

                                                    btnsave.setVisibility(View.VISIBLE);

                                                }
                                            });
                                    alertDialog.show();
                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if(userLogin.status.equals("INVALID_SESSION")){
                                        ReDirectToParentActivity.callLoginActivity(getActivity());

                                    }else{
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("NIRA Verification Status:"+userLogin.status);
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        isVerified = false;
                                                        btnsave.setVisibility(View.INVISIBLE);

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
                                BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR:"+error+",\t STATUS"+status,"NIRA VERIFICATION");
                            }

                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(getActivity(), Constants.emailId,"MESSAGE:"+e.getMessage()+",\t CAUSE:"+e.getCause(),"NIRA VERIFICATION");
                }

            }
        });

        final Button verify_refugee = (Button) view.findViewById(R.id.verify_refugee_btn);

        verify_refugee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRegistration = collectRegistrationData(view, new UserRegistration());
                if(userRegistration != null){
                    userRegistration.sex = selectGenderSpinner.getSelectedItem().toString();
                    String[] dob = datePickerText.getText().toString().split("/");
                    userRegistration.yearOfBirth = Integer.valueOf(dob[2].toString());
                    userRegistration.individualId = userRegistration.refugeeIdentityNumber;
                    userRegistration.fingerprint = RegistrationData.getRefugeeThumbEncodedData();

                    Intent intent = new Intent(getActivity(), FingerPrintScannerActivity.class);
                    intent.putExtra("ScanningType", "refugeeUser");
                    intent.putExtra("refugeeData", userRegistration);
                    startActivityForResult(intent, 101);
                }




               /* RestServiceHandler serviceHandler = new RestServiceHandler();
                try {

                    if(RegistrationData.getRefugeeThumbEncodedData() != null){

                        userRegistration = collectRegistrationData(view, new UserRegistration());


                        if (userRegistration != null){

                            userRegistration.sex = selectGenderSpinner.getSelectedItem().toString();
                            String[] dob = datePickerText.getText().toString().split("/");
                            userRegistration.yearOfBirth = Integer.valueOf(dob[2].toString());
                            userRegistration.individualId = userRegistration.refugeeIdentityNumber;
                            userRegistration.fingerprint = RegistrationData.getRefugeeThumbEncodedData();


                            progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"Verifying Refugee......");

                            serviceHandler.postRefugeeVerification(userRegistration, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin) data.get(0);
                                    if (userLogin.status.equals("Match")) {

                                        ProgressDialogUtil.stopProgressDialog(progressDialog);

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("Refugee Identity Verified Successfully!");
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        isVerified = true;
                                                        btnsave.setVisibility(View.VISIBLE);

                                                    }
                                                });
                                        alertDialog.show();
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        if(userLogin.status.equals("INVALID_SESSION")){
                                            ReDirectToParentActivity.callLoginActivity(getActivity());

                                        }else{


                                            View dialogView = getLayoutInflater().inflate(R.layout.custom_layout, null);
                                            ImageView pic = (ImageView) dialogView.findViewById(R.id.dialog_pic);
                                            pic.setImageDrawable(RegistrationData.getRefugeeThumbImageDrawable());
                                            TextView desc = (TextView) dialogView.findViewById(R.id.desc);
                                            desc.setText("Status: "+userLogin.status);

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Message!");
                                            alertDialog.setView(dialogView);
                                           *//* alertDialog.setMessage("Status: "+userLogin.status);*//*
                                            alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                                isVerified = false;
                                                            btnsave.setVisibility(View.INVISIBLE);

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
                                    BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR:"+error+",\t STATUS"+status,"REFUGEE VERIFICATION");
                                }

                            });

                        }


                    }else{
                        MyToast.makeMyToast(getActivity(),"Please Capture Fingerprint.", Toast.LENGTH_SHORT);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    BugReport.postBugReport(getActivity(), Constants.emailId,"MESSAGE:"+e.getMessage()+",\t CAUSE:"+e.getCause(),"NIRA VERIFICATION");
                }*/

            }
        });


        if(RegistrationData.getIsUgandan()) {
            nationalBtn.setChecked(true);
            nationalContainer.setVisibility(View.VISIBLE);
            foreignerContainer.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.VISIBLE);
            genderLayout.setVisibility(View.GONE);
            btnsave.setVisibility(View.INVISIBLE);
            scan.setText("Scan NIN Document");

            checkFingerprintVerified();

        }else if(RegistrationData.getIsForeigner()) {
            foreignerBtn.setChecked(true);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.VISIBLE);
            surnameLayout.setVisibility(View.VISIBLE);
            refugeeContainer.setVisibility(View.GONE);
            btnsave.setVisibility(View.VISIBLE);
            verifyText.setVisibility(View.GONE);
            genderLayout.setVisibility(View.GONE);
            scan.setText("Scan Passport Document");

        }else if(RegistrationData.getIsRefugee()){
            refugeeBtn.setChecked(true);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.VISIBLE);
            btnsave.setVisibility(View.VISIBLE);
            verifyText.setVisibility(View.GONE);
            genderLayout.setVisibility(View.VISIBLE);
            scan.setText(" ");
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.personal11_reg_radio_btn:
                        personalRegContainer.setVisibility(View.VISIBLE);
                        companyRegContainer.setVisibility(View.GONE);
                        if(nationalBtn.isChecked()){
                            RegistrationData.setIsForeigner(false);
                            RegistrationData.setIsUgandan(true);
                            RegistrationData.setIsRefugee(false);
                            btnsave.setVisibility(View.INVISIBLE);
                        }else{
                            btnsave.setVisibility(View.VISIBLE);
                        }

                        break;
                    case R.id.compnay11_reg_radio_btn :
                        personalRegContainer.setVisibility(View.GONE);
                        companyRegContainer.setVisibility(View.VISIBLE);
                        btnsave.setVisibility(View.VISIBLE);
                        break;

                    case R.id.retailer11_reg_radio_btn:
                        personalRegContainer.setVisibility(View.VISIBLE);
                        companyRegContainer.setVisibility(View.GONE);
                        if(nationalBtn.isChecked()){
                            RegistrationData.setIsForeigner(false);
                            RegistrationData.setIsUgandan(true);
                            RegistrationData.setIsRefugee(false);
                            btnsave.setVisibility(View.INVISIBLE);
                        }else{
                            btnsave.setVisibility(View.VISIBLE);
                        }

                        break;
                }
            }
        });


        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.natid_btn:
                        RegistrationData.setIsForeigner(false);
                        RegistrationData.setIsUgandan(true);
                        RegistrationData.setIsRefugee(false);
                        nationalContainer.setVisibility(View.VISIBLE);
                        foreignerContainer.setVisibility(View.GONE);
                        refugeeContainer.setVisibility(View.GONE);
                        surnameLayout.setVisibility(View.VISIBLE);
                        genderLayout.setVisibility(View.GONE);
                        btnsave.setVisibility(View.INVISIBLE);
                        scan.setText("Scan NIN Document");
                        checkFingerprintVerified();
                        break;
                    case R.id.frnr_btn:
                        RegistrationData.setIsForeigner(true);
                        RegistrationData.setIsUgandan(false);
                        RegistrationData.setIsRefugee(false);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.VISIBLE);
                        surnameLayout.setVisibility(View.VISIBLE);
                        refugeeContainer.setVisibility(View.GONE);
                        btnsave.setVisibility(View.VISIBLE);
                        verifyText.setVisibility(View.GONE);
                        genderLayout.setVisibility(View.GONE);
                        scan.setText("Scan Passport Document");
                        break;

                    case R.id.refugee_btn:
                        RegistrationData.setIsForeigner(false);
                        RegistrationData.setIsUgandan(false);
                        RegistrationData.setIsRefugee(true);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.GONE);
                        surnameLayout.setVisibility(View.GONE);
                        refugeeContainer.setVisibility(View.VISIBLE);
                        btnsave.setVisibility(View.VISIBLE);
                        verifyText.setVisibility(View.GONE);
                        genderLayout.setVisibility(View.VISIBLE);
                        scan.setText(" ");
                        break;

                }
            }
        });

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.getAllRoles(new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    /*Roles[] rolesArray = new Roles[data.size()];
                    rolesArray = data.toArray(rolesArray);
                    if(rolesArray.length!= 0){
                        for(Roles role : rolesArray){
                            if(role.roleName != null){
                                if(role.roleName.equals("Consumer")){
                                    userRegistration.roleId = role.roleId.longValue();
                                    userRegistration.roleName = role.roleName.toString();
                                }
                            }
                        }

                    }else{
                        MyToast.makeMyToast(getActivity(), "Unable to set User Role.", Toast.LENGTH_SHORT);
                    }*/

                    Roles role = (Roles)data.get(0);
                    if(role != null){
                        if(role.status != null){
                            if(role.status.equals("success")){
                                Roles[] rolesArray = new Roles[role.rolesList.size()];
                                rolesArray = role.rolesList.toArray(rolesArray);
                                if(rolesArray.length!= 0){
                                    RegistrationData.setRoles(rolesArray);
                                    for (Roles role1 : rolesArray) {
                                        if (role1.roleName != null) {
                                            if (role1.roleName.equals("Consumer")) {
                                                userRegistration.roleId = role1.roleId.longValue();
                                                userRegistration.roleName = role1.roleName.toString();
                                            }
                                        }
                                    }
                                }
                            }else if(role.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(getActivity());
                            }else{
                                MyToast.makeMyToast(getActivity(),"GET ROLES:"+role.status, Toast.LENGTH_SHORT);
                            }
                        }
                    }else{
                        MyToast.makeMyToast(getActivity(),"EMPTY ROLES DETAILS", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                   // MyToast.makeMyToast(getActivity(), "STATUS: "+status+"\n ERROR"+error, Toast.LENGTH_SHORT);
                    BugReport.postBugReport(getActivity(), Constants.emailId,"Error:"+error+"Status"+status,"");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }

    private void setScannedData(ScanData scannedBarcodeData) {

        if (scannedBarcodeData.getLastName() != null) {
            surname.setText(scannedBarcodeData.getLastName());
        } else {
            surname.setText("");
        }

        if (scannedBarcodeData.getFirstName() != null) {
            give_name.setText(scannedBarcodeData.getFirstName());
        } else {
            give_name.setText("");
        }

        if (scannedBarcodeData.getDateOfBirth() != null) {
            datePickerText.setText(scannedBarcodeData.getDateOfBirth().toString());
        } else {
            datePickerText.setText("");
        }

        if (scannedBarcodeData.getPassportNo() != null) {
            national_id_number.setText(scannedBarcodeData.getPassportNo().toString());
        } else {
            national_id_number.setText("");
        }

        if (scannedBarcodeData.getDocumentId() != null) {
            document_id.setText(scannedBarcodeData.getDocumentId().toString());
        } else {
            document_id.setText("");
        }

        ugandaNationality.setText("UGA");
    }

    public void checkFingerprintVerified(){

        if(RegistrationData.getIsmatched()){
            verifyText.setVisibility(View.VISIBLE);

        }else{
            verifyText.setVisibility(View.GONE);
        }

    }

    private void setDataOnBackActivity() {
        UserRegistration reg = UserSession.getAllUserInformation(getContext());

        surname.setText(reg.surName);
        give_name.setText(reg.givenNames);
        email_id.setText(reg.email);
        phone_number.setText(reg.phoneNumber);
        national_id_number.setText(reg.nin);
        document_id.setText(reg.documentId);
        foreigner_passport_no.setText(reg.identityNumber);
        foreigner_nationality.setText(reg.nationality);

        c_user_name.setText(reg.userName);
        c_email.setText(reg.email);
        c_phone_number.setText(reg.phoneNumber);
        c_company.setText(reg.company);
        c_tin_number.setText(reg.tinNumber);
        c_certificate_number.setText(reg.coiNumber);
        c_primary_username.setText(reg.primaryPersonName);
        c_primary_phone_number.setText(reg.primaryPersonPhoneNumber);
        c_primary_email.setText(reg.primaryPersonEmailId);
        c_alternate_user_name.setText(reg.alternatePersonName);
        c_alternate_phone_number.setText(reg.alternatePhoneNumber);
        c_alternate_email.setText(reg.alternatePersonEmailId);
 }


    private void setScannedDataValues(ScanData scanData) {

        if(scanData.getFirstName() != null){
            if(scanData.getFirstName().trim().contains("<")){
                String givenName = scanData.getFirstName().replace("<"," ");
                give_name.setText(givenName);
            }else if(scanData.getFirstName().trim().contains(">")){
                String givenName = scanData.getFirstName().replace(">"," ");
                give_name.setText(givenName);
            }else{
                give_name.setText(scanData.getFirstName());
            }

        }else{
            give_name.setText("");
        }

        if(scanData.getLastName() != null){
            if(scanData.getLastName().trim().contains("<")){
                String lastName = scanData.getLastName().replace("<"," ");
                surname.setText(lastName);
            }else if(scanData.getLastName().trim().contains(">")){
                String lastName = scanData.getLastName().replace(">"," ");
                surname.setText(lastName);
            }else{
                surname.setText(scanData.getLastName());
            }

        }else{
            surname.setText("");
        }

        if(scanData.getPassportNo() != null){
            document_id.setText(scanData.getPassportNo());
            foreigner_passport_no.setText(scanData.getPassportNo());

        }else{
            document_id.setText("");
            foreigner_passport_no.setText("");
        }

        if(scanData.getCountry() != null){
            foreigner_nationality.setText(scanData.getCountry());
            ugandaNationality.setText(scanData.getCountry());
        }else{
            foreigner_nationality.setText("");
            ugandaNationality.setText("");
        }

        if(scanData.ugandanNationalId != null){
            national_id_number.setText(scanData.ugandanNationalId);
        }

        if(scanData.getDateOfBirth() != null) {
            String dob = scanData.getDateOfBirth();
            if (dob != null) {
                datePickerText.setText(dob.replace("-", "/"));
            }
        }

       /* if(scanData.getFirstName() != null | scanData.getLastName()!= null | scanData.getPassportNo() != null | scanData.getCountry() != null | scanData.getDateOfBirth() != null){



            surname.setText(scanData.getLastName().toString());
            document_id.setText(scanData.getPassportNo().toString());
            foreigner_nationality.setText(scanData.getCountry().toString());
            ugandaNationality.setText(scanData.getCountry().toString());

            if(scanData.getDateOfBirth() != null) {
                String dob = scanData.getDateOfBirth();
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
        }else{
            MyToast.makeMyToast(getActivity(),"Please Scan your Document.", Toast.LENGTH_SHORT);
        }*/
    }

    private UserRegistration collectRegistrationData(View view, UserRegistration userRegistration) {

        userRegistration.resellerCode = UserSession.getResellerId(getContext());

        if( personalRadioBtn.isChecked() || retailerRadioBtn.isChecked()  ) {

            EditText email_id = (EditText) view.findViewById(R.id.email_id);

            if (!give_name.getText().toString().isEmpty()) {
                String uName = give_name.getText().toString().replaceAll("\n","").replaceAll("\r","").trim();

                userRegistration.fullName = uName;
                userRegistration.givenNames = uName;
            } else {
                Toast.makeText(getActivity(), "Please enter Given Name", Toast.LENGTH_SHORT).show();
                return null;
            }


            if(!email_id.getText().toString().isEmpty() && email_id.getText().toString().contains("@")){
                userRegistration.email = email_id.getText().toString();

            }else{
                Toast.makeText(getActivity(), "Please enter Correct Email Id", Toast.LENGTH_SHORT).show();
                return null;
            }

            if(!give_name.getText().toString().isEmpty() && !surname.getText().toString().isEmpty()){
                String givenName = give_name.getText().toString().replaceAll("\n","").replaceAll("\r","").trim();
                String surName = surname.getText().toString().replaceAll("\n","").replaceAll("\r","").trim();

                userRegistration.userName = givenName.toLowerCase().replace(" ","_")+"_"+surName.toLowerCase().replace(" ","_");
            }else{
                String givenName = give_name.getText().toString().replaceAll("\n","").replaceAll("\r","").trim();

                userRegistration.userName = givenName.toLowerCase().replace(" ","_");
            }

            userRegistration.dob = datePickerText.getText().toString();

            if(nationalBtn.isChecked()){

                EditText national_id_number = (EditText) view.findViewById(R.id.national_id);

                if(!national_id_number.getText().toString().isEmpty()){
                    userRegistration.identityNumber = national_id_number.getText().toString();
                    userRegistration.nin = national_id_number.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please enter National Id Number", Toast.LENGTH_SHORT).show();
                    return null;
                }

                EditText documrntid = (EditText) view.findViewById(R.id.document_ID);
                if(!documrntid.getText().toString().isEmpty()){
                    userRegistration.documentId = documrntid.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please enter National Id Number", Toast.LENGTH_SHORT).show();
                    return null;
                }


                if(!datePickerText.getText().toString().isEmpty()){
                    userRegistration.dob = datePickerText.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please select Date of Birth", Toast.LENGTH_SHORT).show();
                    return null;
                }


                EditText surname = (EditText) view.findViewById(R.id.surname);
                if(!surname.getText().toString().isEmpty()){
                    userRegistration.surname = surname.getText().toString();
                    userRegistration.surName = surname.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please enter Surname", Toast.LENGTH_SHORT).show();
                    return null;
                }

                userRegistration.nationalIdentity = "Ugandan NationalID";

                userRegistration.fingerprintVerifed = isVerified;

            } else if(foreignerBtn.isChecked()){

                EditText foreigner_nationality_id = (EditText) view.findViewById(R.id.foreigner_nationality_id);
                EditText passport_number = (EditText) view.findViewById(R.id.foreigner_passport_id);

                if(passport_number.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please enter Passport Number", Toast.LENGTH_SHORT).show();
                    return null;
                }else{

                    userRegistration.identityNumber = passport_number.getText().toString();
                }

                if(!foreigner_nationality_id.getText().toString().isEmpty()){
                    userRegistration.nationality = foreigner_nationality_id.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please Enter Nationality", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(passportTypeSpinner.getSelectedItem().toString().equals("Validity Date"))
                    if(!visaExpiryDate.getText().toString().isEmpty()){
                        userRegistration.visaValidityDate = visaExpiryDate.getText().toString();
                    }else{
                        Toast.makeText(getActivity(), "Please Enter Visa Expiry Date", Toast.LENGTH_SHORT).show();
                        return null;
                    }


                EditText surname = (EditText) view.findViewById(R.id.surname);
                if(!surname.getText().toString().isEmpty()){
                    userRegistration.surname = surname.getText().toString();
                    userRegistration.surName = surname.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please enter Surname", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(passportTypeSpinner.getSelectedItem().toString().equals("Validity Date")){
                    userRegistration.visaValidityType = "Limited Date";
                }else {
                    userRegistration.visaValidityType = passportTypeSpinner.getSelectedItem().toString();
                }
                userRegistration.nationalIdentity = "Passport No";
                userRegistration.fingerprintVerifed = false;

            }else if(refugeeBtn.isChecked()){

                userRegistration.nationalIdentity = "Refugee";
                userRegistration.surname = "";
                userRegistration.surName = "";

                if(refugeeId.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please enter Identity Number", Toast.LENGTH_SHORT).show();
                    return null;
                }else{
                    userRegistration.refugeeIdentityNumber = refugeeId.getText().toString();

                }

                if(!refugeeNationality.getText().toString().isEmpty()){
                    userRegistration.nationality = refugeeNationality.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please Enter Nationality", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(!issuedDate.getText().toString().isEmpty()){
                    userRegistration.refugeeIssueDate = issuedDate.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please Enter Issued Date", Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(selectGenderSpinner.getSelectedItem().equals("Select Gender")){
                    MyToast.makeMyToast(getActivity(),"Please select gender.", Toast.LENGTH_SHORT);
                    return null;
                }

                if(RegistrationData.getIsRefugeeMatched() != null){
                    userRegistration.fingerprintVerifed = RegistrationData.getIsRefugeeMatched();
                }else{
                    userRegistration.fingerprintVerifed = false;
                }

            }

            EditText phone_noumber = (EditText) view.findViewById(R.id.phone_no);

            if(!phone_noumber.getText().toString().isEmpty() && phone_noumber.getText().length()==9){
                userRegistration.phoneNumber = "256"+phone_noumber.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary Phone Number", Toast.LENGTH_SHORT).show();
                return null;
            }

            if(personalRadioBtn.isChecked()) {
                userRegistration.registrationType = "personal";
            }else if(retailerRadioBtn.isChecked()){
                userRegistration.registrationType = "retailer";
            }

        }


        if(companyRadioBtn.isChecked()){

            EditText user_name = (EditText) view.findViewById(R.id.c_user_name);
            EditText email_id = (EditText) view.findViewById(R.id.c_email_id);

            if(!user_name.getText().toString().isEmpty()){
                String uName = user_name.getText().toString().replaceAll("\n","").replaceAll("\r","").trim();
                userRegistration.userName = uName;
                userRegistration.fullName = uName;

            }else{

                Toast.makeText(getActivity(), "Please Enter UserName", Toast.LENGTH_SHORT).show();
                return null;
            }

            if(!email_id.getText().toString().isEmpty() && email_id.getText().toString().contains("@")){
                userRegistration.email = email_id.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please enter Correct Email Id", Toast.LENGTH_SHORT).show();
                return null;
            }

            EditText company_name = (EditText) view.findViewById(R.id.c_company_name);


            if(!company_name.getText().toString().isEmpty()){
                userRegistration.company = company_name.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please enter Company Name", Toast.LENGTH_SHORT).show();
                return null;
            }


            EditText tin_no = (EditText) view.findViewById(R.id.c_tin_no);

            EditText certi_innumber = (EditText) view.findViewById(R.id.c_certi_innumber);

            if(!tin_no.getText().toString().isEmpty() || !certi_innumber.getText().toString().isEmpty()){
                userRegistration.tinNumber = tin_no.getText().toString();
                userRegistration.coiNumber = certi_innumber.getText().toString();

            }else{
                Toast.makeText(getActivity(), "Please enter either TIN/ COI Number", Toast.LENGTH_SHORT).show();
                return null;
            }

            EditText primary_user_name = (EditText) view.findViewById(R.id.c_primary_user_name);

            if(!primary_user_name.getText().toString().isEmpty()){
                userRegistration.primaryPersonName = primary_user_name.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary User Name", Toast.LENGTH_SHORT).show();
                return null;
            }


            EditText primary_user_phone_number = (EditText) view.findViewById(R.id.c_primary_user_phone_number);

            if(!primary_user_phone_number.getText().toString().isEmpty() && primary_user_phone_number.getText().length()==9){
                userRegistration.primaryPersonPhoneNumber = "256"+primary_user_phone_number.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary Phone Number", Toast.LENGTH_SHORT).show();
                return null;
            }

            EditText primary_person_emailid = (EditText) view.findViewById(R.id.c_primary_person_emailid);

            if(!primary_person_emailid.getText().toString().isEmpty()){
                userRegistration.primaryPersonEmailId = primary_person_emailid.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary Email Id", Toast.LENGTH_SHORT).show();
                return null;
            }

            EditText alternate_user_name = (EditText) view.findViewById(R.id.c_alternate_user_name);
            userRegistration.alternatePersonName = alternate_user_name.getText().toString();

            EditText alternate_user_phone_number = (EditText) view.findViewById(R.id.c_alternate_user_phone_number);

            if(!alternate_user_phone_number.getText().toString().isEmpty() && alternate_user_phone_number.getText().length()==9){
                userRegistration.alternatePhoneNumber = "256"+alternate_user_phone_number.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary Phone Number", Toast.LENGTH_SHORT).show();
                return null;
            }

            EditText alternate_person_emailid = (EditText) view.findViewById(R.id.c_alternate_person_emailid);
            userRegistration.alternatePersonEmailId = alternate_person_emailid.getText().toString();

            EditText phone_no = (EditText) view.findViewById(R.id.c_phone_no);

            if(!phone_no.getText().toString().isEmpty() && phone_no.getText().length()==9){
                userRegistration.phoneNumber = "256"+phone_no.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter Primary Phone Number", Toast.LENGTH_SHORT).show();
                return null;
            }
            userRegistration.registrationType = "company";
            userRegistration.nationalIdentity = "";
            userRegistration.fingerprintVerifed = false;
        }
        userRegistration.userGroup = "Consumer";
        userRegistration.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";


        return userRegistration;
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {

            if (RegistrationData.getIsRefugeeMatched() != null) {
                if(RegistrationData.getIsRefugeeMatched()){
                    btnsave.setVisibility(View.VISIBLE);
                }else{
                    btnsave.setVisibility(View.VISIBLE);
                }

            }
        }
    }
}
