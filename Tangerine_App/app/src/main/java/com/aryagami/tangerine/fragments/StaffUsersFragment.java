package com.aryagami.tangerine.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.activities.StaffBillingAddressActivity;
import com.aryagami.util.UserSession;

import java.util.Calendar;

public class StaffUsersFragment extends Fragment {


    UserRegistration userRegistration;
    int mYear, mMonth, mDay;
    TextInputEditText give_name,surname,datePickerText, email_id, phone_number;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.activity_staff_infomation, container, false);

        //  user_name = (EditText) view.findViewById(R.id.user_name);
        give_name = (TextInputEditText) view.findViewById(R.id.staff_give_name);
        surname = (TextInputEditText) view.findViewById(R.id.staff_surname);
        email_id = (TextInputEditText) view.findViewById(R.id.staff_email_id);
        phone_number = (TextInputEditText) view.findViewById(R.id.staff_phone_no);

        final Button btnsave=(Button)view.findViewById(R.id.staff_savecontinue12_btn);

        datePickerText = (TextInputEditText) view.findViewById(R.id.staff_dob_edittext);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegistration = collectRegistrationData(view,new UserRegistration());
                if(userRegistration != null) {
                    RegistrationData.setRegistrationData(userRegistration);
                    UserSession.setAllUserInformation(getActivity(),userRegistration);

                    Intent intent = new Intent(getActivity(), StaffBillingAddressActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });


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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datePickerText.setText(((dayOfMonth)<10?("0"+dayOfMonth):(dayOfMonth)) + "/"
                                        + ((monthOfYear)<9?("0"+(monthOfYear+1)):(monthOfYear+1)) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private UserRegistration collectRegistrationData(View view, UserRegistration userRegistration) {

        if(UserSession.getResellerId(getActivity())!= null)
        userRegistration.resellerCode = UserSession.getResellerId(getActivity());

            EditText email_id = (EditText) view.findViewById(R.id.staff_email_id);

            if (!give_name.getText().toString().isEmpty()) {
                userRegistration.fullName = give_name.getText().toString().replace(" ","_");
                userRegistration.givenNames = give_name.getText().toString();
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
                userRegistration.userName = give_name.getText().toString().toLowerCase().replace(" ","_")+"_"+surname.getText().toString().toLowerCase();
            }else{
                userRegistration.userName = give_name.getText().toString().toLowerCase().replace(" ","_");
            }
        EditText phone_noumber = (EditText) view.findViewById(R.id.staff_phone_no);
        userRegistration.phoneNumber = phone_noumber.getText().toString();

            userRegistration.dob = datePickerText.getText().toString();


                EditText surname = (EditText) view.findViewById(R.id.staff_surname);
                if(!surname.getText().toString().isEmpty()){
                    userRegistration.surname = surname.getText().toString();
                    userRegistration.surName = surname.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please enter Surname", Toast.LENGTH_SHORT).show();
                    return null;
                }




                if(!datePickerText.getText().toString().isEmpty()){
                    userRegistration.dob = datePickerText.getText().toString();
                }else{
                    Toast.makeText(getActivity(), "Please select Date of Birth", Toast.LENGTH_SHORT).show();
                    return null;
                }


        userRegistration.userGroup = "Staff User";
        userRegistration.password = "e01fa62b94da7b8c67c5c518793ea41464151b83196fd59c4bb8ba3753cd7203";


        return userRegistration;
    }

}
