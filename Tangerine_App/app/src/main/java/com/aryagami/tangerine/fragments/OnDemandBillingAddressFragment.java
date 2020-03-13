package com.aryagami.tangerine.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.activities.OnDemandAccountSetupActivity;
import com.aryagami.util.MyToast;
import com.aryagami.util.UserSession;

import java.util.ArrayList;
import java.util.List;

public class OnDemandBillingAddressFragment extends Fragment {

    Spinner addressTypeSpinner, citySpinner;
    Spinner containerAddressTypeSpinner, containerCitySpinner;

    Button back_btn;
    Button addMoreAddressButton;
    UserRegistration userRegistration, data;
    LinearLayout landlineLayout;
    public class AddressContainer {
        public View container;
    }

    List<AddressContainer> addressContainersList = new ArrayList<AddressContainer>();

   // List<BillingAddressFragment.AddressContainer> addressContainersList = new ArrayList<BillingAddressFragment.AddressContainer>();

    ArrayAdapter adapter, adapter1, adapter2;
    String[] cityList = {"Select City","Kampala","Entebbe"};
    String[] addressTypes = {"Select Address Type", "Home","Work"};
    TextInputEditText land_line_number, door_flat_number, complete_address, postal_address, country;

    List<UserRegistration.UserAddressCommand> commandList = new ArrayList<UserRegistration.UserAddressCommand>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userRegistration = RegistrationData.getOnDemandRegistrationData();
        final View view = inflater.inflate(R.layout.ondemand_fragment_billing_address, container, false);
        Button btnsave=(Button)view.findViewById(R.id.savecontinue_btn);

        land_line_number = (TextInputEditText) view.findViewById(R.id.landline_no);
        door_flat_number = (TextInputEditText) view.findViewById(R.id.door_flat_no);
        complete_address = (TextInputEditText) view.findViewById(R.id.complete_address);
        postal_address = (TextInputEditText) view.findViewById(R.id.postal_address);
        // country = (TextInputEditText)view.findViewById(R.id.country);
        landlineLayout = (LinearLayout)view.findViewById(R.id.landline_layout);

        if(userRegistration.registrationType!= null){
            if(userRegistration.registrationType.equals("company")){
                landlineLayout.setVisibility(view.VISIBLE);
            }else{
                landlineLayout.setVisibility(view.GONE);
            }
        }else{
            landlineLayout.setVisibility(view.GONE);
        }

        if(UserSession.getAllUserInformation(getContext())!= null){
            setDataOnBackActivity();
        }


        addMoreAddressButton = (Button)view.findViewById(R.id.add_more);
        final LinearLayout addressContainer = (LinearLayout)view.findViewById(R.id.address_container_layout);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = collectRegistrationData(view, userRegistration);
                if(data != null) {
                    RegistrationData.setOnDemandRegistrationData(data);
                    UserSession.setAllUserInformation(getActivity(),data);
                    Intent intent = new Intent(getActivity(), OnDemandAccountSetupActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        citySpinner = (Spinner) view.findViewById(R.id.city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        citySpinner.setAdapter(adapter);


        addressTypeSpinner = (Spinner) view.findViewById(R.id.addressType_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, addressTypes);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        addressTypeSpinner.setAdapter(adapter1);

        Button back = (Button) view.findViewById(R.id.backbilling_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.billing_container, new OnDemandUserInformationFragment() );
                fr.commit();

            }
        });

        addMoreAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.ondemand_item_new_address_container, addressContainer, false);
                addressContainer.addView(layout);

                containerCitySpinner = (Spinner) layout.findViewById(R.id.city_spinner);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityList);
                adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                containerCitySpinner.setAdapter(adapter2);

                containerAddressTypeSpinner = (Spinner) layout.findViewById(R.id.addressType_spinner);
                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, addressTypes);
                adapter3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                containerAddressTypeSpinner.setAdapter(adapter3);

                ImageView deleteAddress = (ImageView) layout.findViewById(R.id.delete_button);
                deleteAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (OnDemandBillingAddressFragment.AddressContainer subContainer : addressContainersList) {
                            if (subContainer.container == layout) {
                                addressContainersList.remove(subContainer);
                                break;
                            }
                        }
                        addressContainer.removeView(layout);
                    }
                });

                final OnDemandBillingAddressFragment.AddressContainer sContainer = new OnDemandBillingAddressFragment.AddressContainer();
                sContainer.container = layout;
                addressContainersList.add(sContainer);
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }

    private void setDataOnBackActivity() {
        UserRegistration reg = UserSession.getAllUserInformation(getContext());
        //land_line_number.setText(reg.landLineNumber);
        door_flat_number.setText(reg.doorNumber);
        complete_address.setText(reg.address);
        postal_address.setText(reg.postalAddress);

    }

    private UserRegistration collectRegistrationData(View view, UserRegistration userRegistration) {
        TextInputEditText land_line_number = (TextInputEditText) view.findViewById(R.id.landline_no);
        userRegistration.landLineNumber = "256"+land_line_number.getText().toString();
        TextInputEditText door = (TextInputEditText) view.findViewById(R.id.door_flat_no);
        TextInputEditText complete_address = (TextInputEditText) view.findViewById(R.id.complete_address);
        TextInputEditText postal_address = (TextInputEditText) view.findViewById(R.id.postal_address);

        UserRegistration.UserAddressCommand userAddressCommand =  new UserRegistration.UserAddressCommand();


        if(addressTypeSpinner.getSelectedItem().toString().equals("Select Address Type")){
            MyToast.makeMyToast(getActivity(),"Please Select Address Type", Toast.LENGTH_SHORT);
            return null;
        }else {
            userAddressCommand.addressType = addressTypeSpinner.getSelectedItem().toString();
        }

        if(!complete_address.getText().toString().isEmpty()) {
            userAddressCommand.address = complete_address.getText().toString();
        }else{
            Toast.makeText(getActivity(), "Please Enter Complete Address", Toast.LENGTH_SHORT).show();
            return null;
        }

        userAddressCommand.postalAddress= postal_address.getText().toString();
        userAddressCommand.doorNumber = door.getText().toString();
        if(citySpinner.getSelectedItem().toString().equals("Select City")){
            MyToast.makeMyToast(getActivity(),"Please Select City",Toast.LENGTH_SHORT);
            return null;
        }else {
            userAddressCommand.city = citySpinner.getSelectedItem().toString();
        }

        userAddressCommand.country = "Uganda";

        commandList.add(userAddressCommand);

        for (OnDemandBillingAddressFragment.AddressContainer subContainer : addressContainersList) {
            UserRegistration.UserAddressCommand userAddressCommand1 =  new UserRegistration.UserAddressCommand();

            userAddressCommand1 = getAllAddressContainersDetails(subContainer);
            if(userAddressCommand1 != null){
                commandList.add(userAddressCommand1);
            }
        }


        userRegistration.userAddressList = commandList;
        return userRegistration;
    }

    private UserRegistration.UserAddressCommand getAllAddressContainersDetails(OnDemandBillingAddressFragment.AddressContainer subContainer) {

        UserRegistration.UserAddressCommand addressCommand =  new UserRegistration.UserAddressCommand();

        EditText complete_address = (EditText)subContainer.container.findViewById(R.id.complete_address);
        EditText postal_address = (EditText)subContainer.container.findViewById(R.id.postal_address);
        EditText door= (EditText)subContainer.container.findViewById(R.id.door_flat_no);

        if(containerAddressTypeSpinner.getSelectedItem().toString().equals("Select Address Type")){
            MyToast.makeMyToast(getActivity(),"Please Select Address Type",Toast.LENGTH_SHORT);
            return null;
        }else {
            addressCommand.addressType = containerAddressTypeSpinner.getSelectedItem().toString();
        }

        if(!complete_address.getText().toString().isEmpty()) {
            addressCommand.address = complete_address.getText().toString();
        }else{
            Toast.makeText(getActivity(), "Please Enter Complete Address", Toast.LENGTH_SHORT).show();
            return null;
        }

        addressCommand.postalAddress= postal_address.getText().toString();
        addressCommand.doorNumber = door.getText().toString();

        if(containerCitySpinner.getSelectedItem().toString().equals("Select City")){
            MyToast.makeMyToast(getActivity(),"Please Select City",Toast.LENGTH_SHORT);
            return null;
        }else {
            addressCommand.city = containerCitySpinner.getSelectedItem().toString();
        }

        addressCommand.country = "Uganda";


        return addressCommand;
    }

    public  void onTrimMemory(int level) {
        System.gc();
    }
}
