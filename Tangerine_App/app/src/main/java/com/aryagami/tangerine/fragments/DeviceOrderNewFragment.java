package com.aryagami.tangerine.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.Product;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.BarcodeScannerActivity;
import com.aryagami.tangerine.activities.DeviceOrderPaymentActivity;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeviceOrderNewFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    LinearLayout scanImeiLayout, searchIMEILayout, imeiDetailsLayout;
    Button scanIMEIButton, backButton, saveAndContinue;
    ImageButton searchIMEIButton;
    EditText scannedIMEIText;
    ToggleButton imeiToggleButton;
    SearchableSpinner accountSpinner, deviceSpinner;
    List<String> accountList;
    ProgressDialog progressDialog, progressDialog1;
    String accountList1[], deviceNamesArray[];
    Account accounts[];
    List<DeviceOrder> deviceOrderList = new ArrayList<DeviceOrder>();
    DeviceOrder[] deviceOrdersArray;
    ArrayList<Product> products = new ArrayList<Product>();
    Button addMoreDeviceButton;
    List<AddDeviceContainer> deviceContainersList = new ArrayList<AddDeviceContainer>();
    String accountId;
    Long postProductListingID, containerProductListingID;
    List<NewOrderCommand.ProductListing> productListingList = new ArrayList<NewOrderCommand.ProductListing>();

    public class AddDeviceContainer {
        public View container;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.device_order_fragment, container, false);

        scanImeiLayout = (LinearLayout)view.findViewById(R.id.imei_scan_layout);
        scanIMEIButton = (Button)view.findViewById(R.id.scan_imei_btn);

        searchIMEILayout = (LinearLayout)view.findViewById(R.id.imei_container_layout);
        searchIMEIButton = (ImageButton)view.findViewById(R.id.search_imei_details_btn);

      //  imeiDetailsLayout = (LinearLayout)view.findViewById(R.id.imei_details_layout);

        scannedIMEIText = (EditText)view.findViewById(R.id.scanned_imei_text);

        backButton = (Button)view.findViewById(R.id.device_back);
        saveAndContinue = (Button)view.findViewById(R.id.device_continue);
        addMoreDeviceButton = (Button)view.findViewById(R.id.add_more);

        accountSpinner = (SearchableSpinner)view.findViewById(R.id.poduct_type_spinner);
        accountSpinner.setTitle("Select Account");

        deviceSpinner = (SearchableSpinner)view.findViewById(R.id.select_product);
        deviceSpinner.setTitle("Select Device");

        imeiToggleButton = (ToggleButton)view.findViewById(R.id.imei_toggle);

        setAccountDetails();
        getAllProducts();

        imeiToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    RegistrationData.setIsScanICCID(true);
                    searchIMEILayout.setVisibility(View.GONE);
                    scanImeiLayout.setVisibility(View.VISIBLE);
                } else {
                    RegistrationData.setIsScanICCID(false);
                    searchIMEILayout.setVisibility(View.VISIBLE);
                    scanImeiLayout.setVisibility(View.GONE);
                }
            }
        });

        scanIMEIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
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
                    postProductListingID = deviceOrdersArray[deviceSpinner.getSelectedItemPosition()].productListingId;
                    String binRef = UserSession.getResellerId(getContext());
                    String imeiNumber = scannedIMEIText.getText().toString().trim();

                    MyToast.makeMyToast(getActivity(),"  "+imeiNumber+"  "+binRef+ " post"+postProductListingID,Toast.LENGTH_SHORT);

                    try {
                        serviceHandler.getCheckIMEIAvailability(binRef, postProductListingID, imeiNumber, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                            if(data != null){
                                DeviceOrder deviceOrder = (DeviceOrder) data.get(0);
                                if(deviceOrder != null){
                                    if(deviceOrder.status != null)
                                        if(deviceOrder.status.equals("success")){
                                            addMoreDeviceButton.setVisibility(View.VISIBLE);
                                            saveAndContinue.setVisibility(View.VISIBLE);

                                            NewOrderCommand.ProductListing listing = new NewOrderCommand.ProductListing();
                                            listing.listingId = deviceOrder.listingId;
                                            listing.listingPrice = deviceOrder.listingPrice;
                                            listing.serialNumber = deviceOrder.serialNumber;
                                            productListingList.add(listing);

                                            MyToast.makeMyToast(getActivity(),"Device Added Successfully.", Toast.LENGTH_SHORT);

                                            /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Success!");
                                            alertDialog.setMessage("Device Added Successfully.");
                                            alertDialog.setNeutralButton(getActivity().getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();*/

                                        }else if(deviceOrder.status.equals("INVALID_SESSION")){
                                            ReDirectToParentActivity.callLoginActivity(getActivity());
                                        }else{
                                            addMoreDeviceButton.setVisibility(View.GONE);
                                            saveAndContinue.setVisibility(View.GONE);

                                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Alert!");
                                            alertDialog.setMessage("Status: "+deviceOrder.status);
                                            alertDialog.setNeutralButton(getActivity().getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }
                                }else{
                                    addMoreDeviceButton.setVisibility(View.GONE);
                                    saveAndContinue.setVisibility(View.GONE);

                                    MyToast.makeMyToast(getActivity(),"Empty Details!",Toast.LENGTH_SHORT);
                                }
                            }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                addMoreDeviceButton.setVisibility(View.GONE);
                                saveAndContinue.setVisibility(View.GONE);

                                BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR: "+error+", STATUS:"+status,"DEVICE ORDER");
                            }
                        });

                    }catch (Exception io){

                        BugReport.postBugReport(getActivity(), Constants.emailId,"Cause "+io.getCause()+", Message:"+io.getMessage(),"DEVICE ORDER");
                    }
                }else{
                    MyToast.makeMyToast(getActivity(),"IMEI data should not be Empty.", Toast.LENGTH_SHORT);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewOrderCommand command = collectAllRequiredData();
                Intent intent = new Intent(getActivity(), DeviceOrderPaymentActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout deviceContainer = (LinearLayout)view.findViewById(R.id.add_imei_container);

        addMoreDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.item_new_device_order, deviceContainer, false);
                deviceContainer.addView(layout);

               final SearchableSpinner deviceContainerSpinner = (SearchableSpinner)layout.findViewById(R.id.c_select_product);

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, deviceNamesArray);
                adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                deviceContainerSpinner.setAdapter(adapter1);

               final Button cScanIMEIButton = (Button)layout.findViewById(R.id.c_scan_imei_btn);

                ToggleButton toggleButton = (ToggleButton)layout.findViewById(R.id.c_imei_toggle);
                final LinearLayout cSearchIMEILayout = (LinearLayout)layout.findViewById(R.id.c_imei_container_layout);
                 ImageButton cSearchIMEIButton = (ImageButton)layout.findViewById(R.id.c_search_imei_details_btn);
                 final LinearLayout cScanImeiLayout = (LinearLayout)layout.findViewById(R.id.c_imei_scan_layout);
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            RegistrationData.setIsScanICCID(true);
                            cSearchIMEILayout.setVisibility(View.GONE);
                            cScanImeiLayout.setVisibility(View.VISIBLE);
                        } else {
                            RegistrationData.setIsScanICCID(false);
                            cSearchIMEILayout.setVisibility(View.VISIBLE);
                            cScanImeiLayout.setVisibility(View.GONE);
                        }
                    }
                });

                cScanIMEIButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
                        startActivity(intent);
                    }
                });
               final EditText cScannedIMEIText = (EditText)layout.findViewById(R.id.c_scanned_imei_text);

                cSearchIMEIButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!cScannedIMEIText.getText().toString().isEmpty()){
                            RestServiceHandler serviceHandler = new RestServiceHandler();
                            containerProductListingID = deviceOrdersArray[deviceContainerSpinner.getSelectedItemPosition()].productListingId;
                            String binRef = UserSession.getResellerId(getContext());
                            String imeiNumberContainer = cScannedIMEIText.getText().toString().trim();

                            MyToast.makeMyToast(getActivity(),""+imeiNumberContainer+"   "+containerProductListingID,Toast.LENGTH_SHORT);

                            try {
                                serviceHandler.getCheckIMEIAvailability(binRef, containerProductListingID, imeiNumberContainer, new RestServiceHandler.Callback() {
                                    @Override
                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                        if(data != null){
                                            DeviceOrder deviceOrder = (DeviceOrder) data.get(0);
                                            if(deviceOrder != null){
                                                if(deviceOrder.status != null)
                                                    if(deviceOrder.status.equals("success")){
                                                        addMoreDeviceButton.setVisibility(View.VISIBLE);
                                                        saveAndContinue.setVisibility(View.VISIBLE);

                                                        NewOrderCommand.ProductListing listing = new NewOrderCommand.ProductListing();
                                                        listing.listingId = deviceOrder.listingId;
                                                        listing.listingPrice = deviceOrder.listingPrice;
                                                        listing.serialNumber = deviceOrder.serialNumber;
                                                        productListingList.add(listing);

                                                        MyToast.makeMyToast(getActivity(),"Device Added Successfully.", Toast.LENGTH_SHORT);

                                            /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                            alertDialog.setCancelable(false);
                                            alertDialog.setTitle("Success!");
                                            alertDialog.setMessage("Device Added Successfully.");
                                            alertDialog.setNeutralButton(getActivity().getResources().getString(R.string.ok),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();*/

                                                    }else if(deviceOrder.status.equals("INVALID_SESSION")){
                                                        ReDirectToParentActivity.callLoginActivity(getActivity());
                                                    }else{
                                                        addMoreDeviceButton.setVisibility(View.GONE);
                                                        saveAndContinue.setVisibility(View.GONE);

                                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                                        alertDialog.setCancelable(false);
                                                        alertDialog.setTitle("Alert!");
                                                        alertDialog.setMessage("Status: "+deviceOrder.status);
                                                        alertDialog.setNeutralButton(getActivity().getResources().getString(R.string.ok),
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                        alertDialog.show();
                                                    }
                                            }else{
                                                addMoreDeviceButton.setVisibility(View.GONE);
                                                saveAndContinue.setVisibility(View.GONE);

                                                MyToast.makeMyToast(getActivity(),"Empty Details!",Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                        addMoreDeviceButton.setVisibility(View.GONE);
                                        saveAndContinue.setVisibility(View.GONE);

                                        BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR: "+error+", STATUS:"+status,"DEVICE ORDER");
                                    }
                                });

                            }catch (Exception io){

                                BugReport.postBugReport(getActivity(), Constants.emailId,"Cause "+io.getCause()+", Message:"+io.getMessage(),"DEVICE ORDER");
                            }
                        }else{
                            MyToast.makeMyToast(getActivity(),"IMEI data should not be Empty.", Toast.LENGTH_SHORT);
                        }

                    }
                });

                ImageView deleteAddress = (ImageView) layout.findViewById(R.id.delete_button);
                deleteAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (DeviceOrderNewFragment.AddDeviceContainer subContainer : deviceContainersList) {
                            if (subContainer.container == layout) {
                                deviceContainersList.remove(subContainer);
                                break;
                            }
                        }
                        deviceContainer.removeView(layout);
                    }
                });
                final DeviceOrderNewFragment.AddDeviceContainer sContainer = new DeviceOrderNewFragment.AddDeviceContainer();
                sContainer.container = layout;
                deviceContainersList.add(sContainer);
            }
        });

        return view;

    }

    private NewOrderCommand collectAllRequiredData() {
        NewOrderCommand command = new NewOrderCommand();


        command.subscriptions = (List<SubscriptionCommand>) new SubscriptionCommand();
        command.contract = new NewOrderCommand.ContractCommand();
        command.productListings  = productListingList;
        command.productListingPrice = new ArrayList<>();
        command.productListingIds = new ArrayList<>();
        command.productListingIdCounts = new ArrayList<>();
        command.resellerCode = UserSession.getResellerId(getContext());

      //  command


        return command;
    }

    private void setAccountDetails() {

        if (NewOrderCommand.getAccount() != null) {
            accountList = new ArrayList<String>();
            for (Account acc : NewOrderCommand.getAccount()) {
                accountList.add(acc.username + "-" + acc.accountId);
            }

            accountList1 = new String[accountList.size()];
            accountList.toArray(accountList1);

            if (accountList1 != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                accountSpinner.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "DATA EMPTY!", Toast.LENGTH_SHORT).show();

            }
        } else {
            RestServiceHandler serviceHandler = new RestServiceHandler();

            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(), "please wait, Fetching All Accounts");
                serviceHandler.getAccountDetails(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {
                        if (data != null) {
                            Account account = (Account)data.get(0);
                            if(account != null){
                                if(account.status != null)
                                    if(account.status.equals("success")){

                                        accounts = new Account[account.accountList.size()];
                                        account.accountList.toArray(accounts);

                                        NewOrderCommand.setAccount(accounts);

                                        accountList = new ArrayList<String>();
                                        for(Account acc : accounts){
                                            accountList.add(acc.username+"-"+acc.accountId);
                                        }

                                        accountList1 = new String[accountList.size()];
                                        accountList.toArray(accountList1);

                                        if(accountList1 != null) {

                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                            accountSpinner.setAdapter(adapter);
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        }else{
                                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                                            Toast.makeText(getActivity(),"Empty Data",Toast.LENGTH_SHORT).show();
                                        }
                                    }else if(account.status.equals("INVALID_SESSION")){
                                        ReDirectToParentActivity.callLoginActivity(getActivity());
                                    }else{
                                        MyToast.makeMyToast(getActivity(),"Status:"+account.status,Toast.LENGTH_SHORT);
                                    }


                            }
                        }else {
                            Toast.makeText(getActivity(), "Empty Data", Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR"+error+"Status:"+status,"DEVICE ORDER");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR"+e.getCause()+"Status:"+e.getMessage(),"DEVICE ORDER");

            }
        }

    }

    private void getAllProducts() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        progressDialog1 = ProgressDialogUtil.startProgressDialog(getActivity(), "Please wait, Fetching All products..");

        serviceHandler.getResellerGodownStock(UserSession.getResellerId(getContext()), new RestServiceHandler.Callback() {
            @Override
            public void success(DataModel.DataType type, List<DataModel> data) {
                if(data != null){
                    deviceOrdersArray = new DeviceOrder[data.size()];
                    data.toArray(deviceOrdersArray);

                       if(deviceOrdersArray != null){
                           if(deviceOrdersArray.length != 0){
                               for (DeviceOrder order : deviceOrdersArray) {
                                   deviceOrderList.add(order);
                                   if(order.productCategory.equals("Bundled Service Products")) {
                                       products.add(new Product(order.productName, "", order.productPrice, R.drawable.circle, order.priceCurrency, order.productListingId));
                                   }else{
                                       products.add(new Product(order.productName, order.productCategory, order.productPrice, R.drawable.circle, order.priceCurrency, order.productListingId));
                                   }
                               }
                               deviceNamesArray = new String[deviceOrderList.size()];
                               List<String> namesList = new ArrayList<String>();
                               for(DeviceOrder order : deviceOrderList){
                                   namesList.add(order.productName.toString());
                               }
                               namesList.toArray(deviceNamesArray);

                               ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, deviceNamesArray);
                               adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                               deviceSpinner.setAdapter(adapter1);

                           }else{
                               MyToast.makeMyToast(getActivity(),"EMPTY DEVICE LIST",Toast.LENGTH_SHORT);
                           }
                       }




                /*listAdapter = new ListAdapter(getContext(), products);
                deviceRequestListview.setAdapter(listAdapter);*/



                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                }else{
                    MyToast.makeMyToast(getActivity(),"EMPTY DEVICE LIST",Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR:"+error+"status:"+status,"DEVICE ORDER");
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.poduct_type_spinner:
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                accountId = accounts[position].accountId;
                break;
            case R.id.select_product:
                postProductListingID = deviceOrdersArray[position].productListingId;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
