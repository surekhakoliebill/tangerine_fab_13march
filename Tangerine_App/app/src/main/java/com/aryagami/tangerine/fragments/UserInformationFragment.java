package com.aryagami.tangerine.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.aryagami.data.RegistrationData;
import com.aryagami.data.ScanData;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.activities.ScanNowPassportImageActivity;
import com.aryagami.util.MyToast;
import com.aryagami.util.UserSession;

import java.util.Calendar;


public class UserInformationFragment extends Fragment {


    RadioButton personalRadioBtn, companyRadioBtn, nationalBtn, foreignerBtn, refugeeBtn;
    LinearLayout personalRegContainer , companyRegContainer, nationalContainer, foreignerContainer, refugeeContainer;
    Spinner currencySpinner, citySpinner, countrySpinner;
    UserRegistration userRegistrationData, userRegistration;
    ProgressDialog progressDialog;
    UserInformationFragment activity = this;
    //UserLogin userLoginResult;
    int mYear, mMonth, mDay;
    private EditText mEditText;
    Button scan, backButton;
    Boolean isUgandan = false, isForeigner= false, isRefugee= false;

    TextView passportNumText, issuedDateText;

    LinearLayout container, datePikerLayout,issuedDateLayout;
    TextInputLayout surnameLayout;
    //  EditText give_name,document_id,foreigner_nationality,surname,datePickerText,user_name, email_id, phone_number, national_id_number, dob, foreigner_passport_no, foreigner_identity_no, company_name, tin_number, certificate_number, c_user_name, c_email, c_phone_number, c_company, c_tin_number, c_certificate_number, c_primary_username, c_primary_phone_number, c_primary_email, c_alternate_user_name, c_alternate_phone_number, c_alternate_email,issuedDate ;
    TextInputEditText give_name,document_id,foreigner_nationality,surname,datePickerText,user_name, email_id, phone_number, national_id_number, dob, foreigner_passport_no, foreigner_identity_no, company_name, tin_number, certificate_number, c_user_name, c_email, c_phone_number, c_company, c_tin_number, c_certificate_number, c_primary_username, c_primary_phone_number, c_primary_email, c_alternate_user_name, c_alternate_phone_number, c_alternate_email,issuedDate ;
    TextInputEditText refugeeId, visaExpiryDate, refugeeNationality, ugandaNationality;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_personal_account1, container, false);

        //  user_name = (EditText) view.findViewById(R.id.user_name);
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

       // backButton = (Button)view.findViewById(R.id.cancel_btn);
       // final Button btnsave=(Button)view.findViewById(R.id.savecontinue12_btn);

        if(UserSession.getAllUserInformation(getContext())!= null){
            setDataOnBackActivity();
        }

       // btnsave.setVisibility(View.INVISIBLE);
        datePickerText = (TextInputEditText) view.findViewById(R.id.dob_edittext);

        datePikerLayout = (LinearLayout) view.findViewById(R.id.date_picker_layout);
        if(RegistrationData.getRegistrationData() != null) {
            userRegistration =RegistrationData.getRegistrationData();
        }else{
            userRegistration = new UserRegistration();
        }
       /* btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegistration = collectRegistrationData(view,new UserRegistration());
                if(userRegistration != null) {
                    RegistrationData.setRegistrationData(userRegistration);
                    UserSession.setAllUserInformation(getActivity(),userRegistration);
                    //UserSession.setAllUserInformation(getActivity(), null);
                    android.support.v4.app.FragmentTransaction fr=getFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_container_registration,new BillingAddressFragment());
                    fr.commit();
                }
            }
        });
*/

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup12);
        personalRegContainer = (LinearLayout) view.findViewById(R.id.reg_personal1_container);
        companyRegContainer = (LinearLayout) view.findViewById(R.id.reg_company1_container);
        personalRadioBtn = (RadioButton) view.findViewById(R.id.personal11_reg_radio_btn);
        companyRadioBtn = (RadioButton)view.findViewById(R.id.compnay11_reg_radio_btn);

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

        datePickerText.setInputType(InputType.TYPE_NULL);
        datePickerText.requestFocus();
        datePickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -10);
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
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanNowPassportImageActivity.class);
                getActivity().startActivity(intent);
            }
        });


        if(ScanData.getScanData()!= null){
            setScannedDataValues(ScanData.getScanData());

        }

       /* backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), NewOrderActivity.class);
                startActivity(intent);
            }
        });*/

        final Button verify_btn = (Button) view.findViewById(R.id.verifyidentitynumber_btn);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // RestServiceHandler serviceHandler = new RestServiceHandler();
                /*try {

                    // UserRegistration userRegistration = new UserRegistration();
                    // Toast.makeText(getApplicationContext(), "Document ID verified", Toast.LENGTH_SHORT).show();
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
                                    alertDialog.setMessage("NIRA verification Succuss!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    // RegistrationData.setRegistrationData(userRegistration);
                                                    //btnsave.setVisibility(View.VISIBLE);

                                                   *//* android.support.v4.app.FragmentTransaction fr = getFragmentManager().beginTransaction();
                                                    fr.replace(R.id.fragment_container_registration, new BillingAddressFragment());
                                                    fr.commit();*//*

                                                }
                                            });
                                    alertDialog.show();
                                }else{
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    Toast.makeText(getActivity(), userLogin.status, Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                Toast.makeText(getActivity(), "NIRA VERIFICATION FAILED!"+ "Error: "+error+"/n Status: "+status, Toast.LENGTH_SHORT).show();
                            }

                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        });





        /*Button submit = (Button)view.findViewById(R.id.savecontinue_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                      userRegistration = collectRegistrationData();
                    progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(),"please wait......");
                    serviceHandler.postUserRegisteration(userRegistration, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            userLoginResult = (UserLogin) data.get(0);
                            if (userLoginResult.status.equals("success")) {
                                MyToast.makeMyToast(getActivity(),"Successfully added...", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        }




                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(getActivity(),"Error"+error +"Status"+ status, Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        });*/


        if(RegistrationData.getIsUgandan()) {
            nationalBtn.setChecked(true);
            nationalContainer.setVisibility(View.VISIBLE);
            foreignerContainer.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.VISIBLE);
           // btnsave.setVisibility(View.INVISIBLE);

        }else if(RegistrationData.getIsForeigner()) {
            foreignerBtn.setChecked(true);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.VISIBLE);
            surnameLayout.setVisibility(View.VISIBLE);
            refugeeContainer.setVisibility(View.GONE);
           // btnsave.setVisibility(View.VISIBLE);

        }else if(RegistrationData.getIsRefugee()){
            refugeeBtn.setChecked(true);
            nationalContainer.setVisibility(View.GONE);
            foreignerContainer.setVisibility(View.GONE);
            surnameLayout.setVisibility(View.GONE);
            refugeeContainer.setVisibility(View.VISIBLE);
           // btnsave.setVisibility(View.VISIBLE);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.personal11_reg_radio_btn:
                        personalRegContainer.setVisibility(View.VISIBLE);
                        companyRegContainer.setVisibility(View.GONE);
                        /*if(nationalBtn.isChecked()){
                            btnsave.setVisibility(View.INVISIBLE);
                        }else{
                            btnsave.setVisibility(View.VISIBLE);
                        }*/

                        break;
                    case R.id.compnay11_reg_radio_btn :
                        personalRegContainer.setVisibility(View.GONE);
                        companyRegContainer.setVisibility(View.VISIBLE);
                       // btnsave.setVisibility(View.VISIBLE);
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
                      //  btnsave.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.frnr_btn:
                        RegistrationData.setIsForeigner(true);
                        RegistrationData.setIsUgandan(false);
                        RegistrationData.setIsRefugee(false);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.VISIBLE);
                        surnameLayout.setVisibility(View.VISIBLE);
                        refugeeContainer.setVisibility(View.GONE);
                       // btnsave.setVisibility(View.VISIBLE);
                        break;

                    case R.id.refugee_btn:
                        RegistrationData.setIsForeigner(false);
                        RegistrationData.setIsUgandan(false);
                        RegistrationData.setIsRefugee(true);
                        nationalContainer.setVisibility(View.GONE);
                        foreignerContainer.setVisibility(View.GONE);
                        surnameLayout.setVisibility(View.GONE);
                        refugeeContainer.setVisibility(View.VISIBLE);
                       // btnsave.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
        return view;
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

        // give_name = (EditText) view.findViewById(R.id.give_name);
        // surname = (EditText) view.findViewById(R.id.surname);

    }

    private void setScannedDataValues(ScanData scanData) {
        /*  give_name,document_id,foreigner_nationality,surname,datePickerText*/

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
        }else{
            MyToast.makeMyToast(getActivity(),"Please Scan your Document.", Toast.LENGTH_SHORT);

        }


        // user_name.setText(scanData.getLastName().toString()+"_"+scanData.getFirstName().toString());

    }


    private UserRegistration collectRegistrationData(View view, UserRegistration userRegistration) {

        if( personalRadioBtn.isChecked()) {

            EditText email_id = (EditText) view.findViewById(R.id.email_id);

            if (!give_name.getText().toString().isEmpty()) {
                userRegistration.fullName = give_name.getText().toString();
                userRegistration.givenNames = give_name.getText().toString();
            } else {
                Toast.makeText(getActivity(), "Please enter Given Name", Toast.LENGTH_SHORT).show();
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

            if(!email_id.getText().toString().isEmpty() && email_id.getText().toString().contains("@gmail.com")){
                userRegistration.email = email_id.getText().toString();

            }else{
                Toast.makeText(getActivity(), "Please enter Correct Email Id", Toast.LENGTH_SHORT).show();
                return null;
            }

            if(!give_name.getText().toString().isEmpty() && !surname.getText().toString().isEmpty()){
                userRegistration.userName = give_name.getText().toString().toLowerCase().replace(" ","_")+"_"+surname.getText().toString().toLowerCase();
            }

            /*if(!user_name.getText().toString().isEmpty()){
                userRegistration.userName = user_name.getText().toString();
            }else{
                Toast.makeText(getActivity(), "Please Enter UserName", Toast.LENGTH_SHORT).show();
              //  user_name.setFocusable(true);
                return null;
            }*/

           // EditText other_name = (EditText) view.findViewById(R.id.other_name);
            // userRegistration.otherNames = other_name.getText().toString();
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

                userRegistration.nationalIdentity = "Ugandan NationalID";

            } else if(foreignerBtn.isChecked()){


                // EditText refugee = (EditText) view.findViewById(R.id.refugee_identity_ID);
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

                if(!visaExpiryDate.getText().toString().isEmpty()){
                    userRegistration.visaValidityDate = visaExpiryDate.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please Enter Visa Expiry Date", Toast.LENGTH_SHORT).show();
                    return null;
                }
                userRegistration.nationalIdentity = "Passport No";

            }else if(refugeeBtn.isChecked()){

                userRegistration.nationalIdentity = "Refugee";

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


            }

            //EditText user_group = (EditText) findViewById(R.id.user_group);
            //userRegistration.userGroup = user_group.getText().toString();

            EditText phone_noumber = (EditText) view.findViewById(R.id.phone_no);
            userRegistration.phoneNumber = phone_noumber.getText().toString();

            userRegistration.registrationType = "personal";

        }


        if(companyRadioBtn.isChecked()){

            EditText user_name = (EditText) view.findViewById(R.id.c_user_name);
            EditText email_id = (EditText) view.findViewById(R.id.c_email_id);

            if(!user_name.getText().toString().isEmpty()){
                userRegistration.userName = user_name.getText().toString();
                userRegistration.fullName = user_name.getText().toString();

            }else{

                Toast.makeText(getActivity(), "Please Enter UserName", Toast.LENGTH_SHORT).show();
                // user_name.setFocusable(true);
                return null;
            }

            if(!email_id.getText().toString().isEmpty() && email_id.getText().toString().contains("@gmail.com")){
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

            if(!primary_user_phone_number.getText().toString().isEmpty()){
                userRegistration.primaryPersonPhoneNumber = primary_user_phone_number.getText().toString();
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
            userRegistration.alternatePhoneNumber = alternate_user_phone_number.getText().toString();

            EditText alternate_person_emailid = (EditText) view.findViewById(R.id.c_alternate_person_emailid);
            userRegistration.alternatePersonEmailId = alternate_person_emailid.getText().toString();

            EditText phone_no = (EditText) view.findViewById(R.id.c_phone_no);
            userRegistration.phoneNumber = phone_no.getText().toString();

           /* EditText email_id = (EditText) view.findViewById(R.id.email_id);
            userRegistration.email = email_id.getText().toString();*/

            userRegistration.registrationType = "company";
            userRegistration.nationalIdentity = "Passport No";
        }
        userRegistration.userGroup = "Consumer";
        userRegistration.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";


        return userRegistration;
    }
}
