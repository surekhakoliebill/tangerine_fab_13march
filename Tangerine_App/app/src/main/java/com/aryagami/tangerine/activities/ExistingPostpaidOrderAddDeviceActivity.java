package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.Product;
import com.aryagami.data.RegistrationData;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExistingPostpaidOrderAddDeviceActivity extends AppCompatActivity {
    Button saveAndContinue, backButton, addMoreDevicesButton;
    LinearLayout deviceContainer;
    Long containerProductListingID;
    List<NewOrderCommand.ProductListing> productListingList = new ArrayList<NewOrderCommand.ProductListing>();
    Activity activity = this;
    ProgressDialog progressDialog,progressDialog1;
    View currentview;
    public class DeviceContainer {
        public View container;
    }

    String deviceNamesArray[];
    List<DeviceOrder> deviceOrderList = new ArrayList<DeviceOrder>();
    DeviceOrder[] deviceOrdersArray;
    ArrayList<Product> products = new ArrayList<Product>();
    Button addMoreDeviceButton;
    List<DeviceContainer> deviceContainersList = new ArrayList<DeviceContainer>();
    NewOrderCommand command = new NewOrderCommand();
    Map<String, NewOrderCommand.ProductListing> mapProductListing = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondemand_add_device_prepaid);

        if(NewOrderCommand.getOnDemandNewOrderCommand() != null){
           command = NewOrderCommand.getOnDemandNewOrderCommand();
        }
        saveAndContinue = (Button)findViewById(R.id.device_continue);
        backButton = (Button)findViewById(R.id.device_back);
        addMoreDevicesButton = (Button)findViewById(R.id.add_more_device);
        deviceContainer = (LinearLayout)findViewById(R.id.add_device_container);

        getAllProducts();

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float deviceAmount = 0.0f;
                ArrayList<NewOrderCommand.ProductListing> valueList = new ArrayList<NewOrderCommand.ProductListing>(mapProductListing.values());
                if(valueList.size()!= 0){
                    command.productListings = valueList;

                    for(NewOrderCommand.ProductListing proList : valueList){
                        if(proList.inventoryPrice != null){
                            deviceAmount +=proList.inventoryPrice;
                        }
                    }
                    if(command.totalPlanPrice != null){
                        command.totalPlanPrice = command.totalPlanPrice+deviceAmount;
                    }
                }else{
                    command.productListings = new ArrayList<NewOrderCommand.ProductListing>();

                }

                if(command.productListings != null) {
                    NewOrderCommand.setOnDemandNewOrderCommand(command);
                    Intent intent = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderPaymentActivity.class);
                    intent.putExtra("ExistingOrder", command);
                    startActivity(intent);

                  /*  Intent intent = new Intent(getApplicationContext(), OnDemandExistingPostpaidOrderPaymentActivity.class);
                    intent.putExtra("ExistingOrder", newOrderCommand);
                    startActivity(intent);*/
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OnDemandAddSubscriptionActivity.class);
                startActivity(intent);
            }
        });

        if(currentview != null){

            if(!RegistrationData.getScanICCIDData().isEmpty()){

                final LinearLayout cSearchIMEILayout = (LinearLayout)currentview.findViewById(R.id.c_imei_container_layout);
                final EditText cScannedIMEIText = (EditText)currentview.findViewById(R.id.c_scanned_imei_text);

                cSearchIMEILayout.setVisibility(View.VISIBLE);
                cScannedIMEIText.setText(RegistrationData.getScanICCIDData());
            }
        }
        addMoreDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = layoutInflater.inflate(R.layout.item_new_device_order, deviceContainer, false);
                deviceContainer.addView(layout);

                final SearchableSpinner deviceContainerSpinner = (SearchableSpinner)layout.findViewById(R.id.c_select_product);

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, deviceNamesArray);
                adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                deviceContainerSpinner.setAdapter(adapter1);

                final Button cScanIMEIButton = (Button)layout.findViewById(R.id.c_scan_imei_btn);

                final LinearLayout imeiDetailsLayout = (LinearLayout)layout.findViewById(R.id.imei_details_layout);
                final TextView productPrice = (TextView)layout.findViewById(R.id.product_price);
                final TextView serialNumber = (TextView)layout.findViewById(R.id.serial_num);


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
                        RegistrationData.setIsScanICCID(true);
                        currentview = layout;
                        Intent intent = new Intent(activity, BarcodeScannerActivity.class);
                        startActivity(intent);
                    }
                });

                final EditText cScannedIMEIText = (EditText)layout.findViewById(R.id.c_scanned_imei_text);

                /*if(!RegistrationData.getScanICCIDData().isEmpty()){
                    cSearchIMEILayout.setVisibility(View.VISIBLE);
                    cScannedIMEIText.setText(RegistrationData.getScanICCIDData());
                }*/
                cSearchIMEIButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!cScannedIMEIText.getText().toString().isEmpty()){
                            RestServiceHandler serviceHandler = new RestServiceHandler();
                            containerProductListingID = deviceOrdersArray[deviceContainerSpinner.getSelectedItemPosition()].productListingId;
                            String binRef = UserSession.getResellerId(activity);
                            final String imeiNumberContainer = cScannedIMEIText.getText().toString().trim();

                           try {
                                serviceHandler.getCheckIMEIAvailability(binRef, containerProductListingID, imeiNumberContainer, new RestServiceHandler.Callback() {
                                    @Override
                                    public void success(DataModel.DataType type, List<DataModel> data) {
                                        if(data != null){
                                            DeviceOrder deviceOrder = (DeviceOrder) data.get(0);
                                            if(deviceOrder != null){
                                                if(deviceOrder.status != null)
                                                    if(deviceOrder.status.equals("success")){
                                                        addMoreDevicesButton.setVisibility(View.VISIBLE);
                                                        saveAndContinue.setVisibility(View.VISIBLE);
                                                        imeiDetailsLayout.setVisibility(View.VISIBLE);

                                                        if(deviceOrder.serialNumber != null){
                                                            serialNumber.setText(deviceOrder.serialNumber);
                                                        }else{
                                                            serialNumber.setText("NULL");
                                                        }
                                                        if(deviceOrder.inventoryPrice != null){
                                                            productPrice.setText(deviceOrder.inventoryPrice.toString());
                                                        }else{
                                                            productPrice.setText("NULL");
                                                        }

                                                        NewOrderCommand.ProductListing listing = new NewOrderCommand.ProductListing();
                                                        listing.listingId = deviceOrder.productListingId;
                                                        listing.listingPrice = deviceOrder.inventoryPrice;
                                                        listing.serialNumber = deviceOrder.serialNumber;
                                                        listing.inventoryPrice = deviceOrder.inventoryPrice;
                                                        listing.imei = imeiNumberContainer;
                                                        //productListingList.add(listing);

                                                        if(!mapProductListing.containsKey(imeiNumberContainer)){
                                                            mapProductListing.put(imeiNumberContainer, listing);
                                                        }else {
                                                            MyToast.makeMyToast(activity,"Product already added, please scan another product!", Toast.LENGTH_SHORT);
                                                            addMoreDevicesButton.setVisibility(View.GONE);
                                                            saveAndContinue.setVisibility(View.GONE);
                                                        }
                                                    }else if(deviceOrder.status.equals("INVALID_SESSION")){
                                                        ReDirectToParentActivity.callLoginActivity(activity);
                                                    }else{
                                                        addMoreDevicesButton.setVisibility(View.GONE);
                                                        saveAndContinue.setVisibility(View.GONE);

                                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                                        alertDialog.setCancelable(false);
                                                        alertDialog.setTitle("Alert!");
                                                        alertDialog.setMessage("Status: "+deviceOrder.status);
                                                        alertDialog.setNeutralButton(activity.getResources().getString(R.string.ok),
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                        alertDialog.show();
                                                    }
                                            }else{
                                                addMoreDevicesButton.setVisibility(View.GONE);
                                                saveAndContinue.setVisibility(View.GONE);

                                                MyToast.makeMyToast(activity,"Empty Details!", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }

                                    @Override
                                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                                        addMoreDevicesButton.setVisibility(View.GONE);
                                        saveAndContinue.setVisibility(View.GONE);

                                        BugReport.postBugReport(activity, Constants.emailId,"ERROR: "+error+", STATUS:"+status,"DEVICE ORDER");
                                    }
                                });

                            }catch (Exception io){

                                BugReport.postBugReport(activity, Constants.emailId,"Cause "+io.getCause()+", Message:"+io.getMessage(),"DEVICE ORDER");
                            }
                        }else{
                            MyToast.makeMyToast(activity,"IMEI data should not be Empty.", Toast.LENGTH_SHORT);
                        }

                    }
                });

                ImageView deleteAddress = (ImageView) layout.findViewById(R.id.delete_button);
                deleteAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (ExistingPostpaidOrderAddDeviceActivity.DeviceContainer subContainer : deviceContainersList) {
                            if (subContainer.container == layout) {
                                final EditText cScannedIMEIText1 = (EditText)layout.findViewById(R.id.c_scanned_imei_text);

                                deviceContainersList.remove(subContainer);

                                String imei = cScannedIMEIText1.getText().toString().trim();
                                mapProductListing.remove(imei);
                                break;
                            }
                        }
                        deviceContainer.removeView(layout);
                    }
                });
                final ExistingPostpaidOrderAddDeviceActivity.DeviceContainer sContainer = new ExistingPostpaidOrderAddDeviceActivity.DeviceContainer();
                sContainer.container = layout;
                deviceContainersList.add(sContainer);
            }
        });

    }

    private void getAllProducts() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        progressDialog1 = ProgressDialogUtil.startProgressDialog(activity, "please wait, Fetching All products..");

        serviceHandler.getResellerGodownStock(UserSession.getResellerId(activity), new RestServiceHandler.Callback() {
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

                           /* ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, deviceNamesArray);
                            adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            deviceSpinner.setAdapter(adapter1);*/

                        }else{
                            MyToast.makeMyToast(activity,"EMPTY DEVICE LIST", Toast.LENGTH_SHORT);
                        }
                    }




                /*listAdapter = new ListAdapter(getContext(), products);
                deviceRequestListview.setAdapter(listAdapter);*/



                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                }else{
                    MyToast.makeMyToast(activity,"EMPTY DEVICE LIST", Toast.LENGTH_SHORT);
                }

            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"status:"+status,"DEVICE ORDER");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentview != null){

            if(!RegistrationData.getScanICCIDData().isEmpty()){

                final LinearLayout cSearchIMEILayout = (LinearLayout)currentview.findViewById(R.id.c_imei_container_layout);
                final EditText cScannedIMEIText = (EditText)currentview.findViewById(R.id.c_scanned_imei_text);

                cSearchIMEILayout.setVisibility(View.VISIBLE);
                cScannedIMEIText.setText(RegistrationData.getScanICCIDData());
            }
        }
    }
}
