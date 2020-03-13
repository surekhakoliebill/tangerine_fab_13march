package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.aryagami.tangerine.adapters.AddedDevicesAdapter;
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

public class AddDeviceOrderActivity extends AppCompatActivity {
    ListView addedListView;
    Activity activity = this;
    SearchableSpinner deviceContainerSpinner;
    Button saveAndContinue, backButton, addMoreDevicesButton,cScanIMEIButton;
    LinearLayout deviceContainer;
    Long containerProductListingID;
    List<NewOrderCommand.ProductListing> productListingList = new ArrayList<NewOrderCommand.ProductListing>();
    NewOrderCommand.ProductListing currentListingItem;

    ProgressDialog progressDialog,progressDialog1;
    String deviceNamesArray[];
    List<DeviceOrder> deviceOrderList = new ArrayList<DeviceOrder>();
    DeviceOrder[] deviceOrdersArray;
    ArrayList<Product> products = new ArrayList<Product>();
    LinearLayout cScanImeiLayout,cSearchIMEILayout;
    ImageButton cSearchIMEIButton;
    EditText cScannedIMEIText;
    List<OnDemandAddDevicePrepaidActivity.DeviceContainer> deviceContainersList = new ArrayList<OnDemandAddDevicePrepaidActivity.DeviceContainer>();
    NewOrderCommand command = new NewOrderCommand();
    Map<String, NewOrderCommand.ProductListing> mapProductListing = new HashMap<>();
    List<NewOrderCommand.ProductListing> addedProductList = new ArrayList<NewOrderCommand.ProductListing>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_order);

        if(NewOrderCommand.getOnDemandNewOrderCommand() != null){
            command = NewOrderCommand.getOnDemandNewOrderCommand();
        }

        addedListView = (ListView)findViewById(R.id.listview_added_devices);
        saveAndContinue = (Button)findViewById(R.id.device_continue);
        backButton = (Button)findViewById(R.id.device_back);

        /*addedListView.setVisibility(View.VISIBLE);
        ArrayAdapter adapter = new AddedDevicesAdapter(activity, resellerStaffArray);
        addedListView.setAdapter(adapter);*/

        deviceContainerSpinner = (SearchableSpinner)findViewById(R.id.c_select_product);

        getAllProducts();

        cScanIMEIButton = (Button)findViewById(R.id.c_scan_imei_btn);

        //final LinearLayout imeiDetailsLayout = (LinearLayout)findViewById(R.id.imei_details_layout);

        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.c_imei_toggle);
        addMoreDevicesButton = (Button)findViewById(R.id.add_more_device);
         cSearchIMEILayout = (LinearLayout)findViewById(R.id.c_imei_container_layout);
         cSearchIMEIButton = (ImageButton)findViewById(R.id.c_search_imei_details_btn);
         cScanImeiLayout = (LinearLayout)findViewById(R.id.c_imei_scan_layout);

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

        if(RegistrationData.getMapList() != null)
            if(RegistrationData.getMapList().size() != 0){

                mapProductListing = RegistrationData.getMapList();
                for (NewOrderCommand.ProductListing productListing : mapProductListing.values()) {
                    productListingList.add(productListing);
                }

                if (productListingList.size() != 0) {

                    addedListView.setVisibility(View.VISIBLE);
                    ArrayAdapter adapter = new AddedDevicesAdapter(activity, productListingList);
                    addedListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
                    addedListView.setVisibility(View.GONE);
                }
            }

       /* backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OnDemandAddSubscriptionActivity.class);
                startActivity(intent);
            }
        });*/

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Float deviceAmount = 0.0f;

                Map<String, NewOrderCommand.ProductListing> sendMapList = new HashMap<>();
                List<NewOrderCommand.ProductListing> finalProductList = new ArrayList<>();
                if(RegistrationData.getMapList() != null) {
                    sendMapList = RegistrationData.getMapList();
                   if(RegistrationData.getMapList().size() != 0){
                       for (NewOrderCommand.ProductListing productListing : sendMapList.values()) {
                           finalProductList.add(productListing);

                           if(productListing.inventoryPrice != null){
                               deviceAmount +=productListing.inventoryPrice;
                           }
                       }
                       command.productListings = finalProductList;
                       if(command.totalPlanPrice != null){
                           command.totalPlanPrice = command.totalPlanPrice+deviceAmount;
                       }
                   }else{
                       command.productListings = finalProductList;
                   }

                }else{
                    command.productListings = finalProductList;
                }

                MyToast.makeMyToast(activity, "Size"+command.productListings.size(), Toast.LENGTH_SHORT);

                NewOrderCommand.setOnDemandNewOrderCommand(command);
                Intent intent = new Intent(getApplicationContext(), OnDemandNewOrderPaymentActivity.class);
                startActivity(intent);

            }
        });

        cScanIMEIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationData.setIsScanICCID(true);
                Intent intent = new Intent(activity, BarcodeScannerActivity.class);
                startActivity(intent);
            }
        });

        addMoreDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RegistrationData.getMapList() != null){
                    mapProductListing = RegistrationData.getMapList();
                }

                if (currentListingItem != null){

                    if (!mapProductListing.containsKey(currentListingItem.imei)) {
                        productListingList.clear();
                        mapProductListing.put(currentListingItem.imei, currentListingItem);
                        RegistrationData.setMapList(mapProductListing);
                        for (NewOrderCommand.ProductListing productListing : mapProductListing.values()) {
                            productListingList.add(productListing);
                        }

                        if (productListingList.size() != 0) {

                            addedListView.setVisibility(View.VISIBLE);
                            ArrayAdapter adapter = new AddedDevicesAdapter(activity, productListingList);
                            addedListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            addedListView.setVisibility(View.GONE);
                        }

                    } else {
                        MyToast.makeMyToast(activity, "Product already added, please scan another product!", Toast.LENGTH_SHORT);
                        addMoreDevicesButton.setVisibility(View.GONE);
                        saveAndContinue.setVisibility(View.GONE);
                    }
            }else{
                    MyToast.makeMyToast(activity, "Please Select Product.", Toast.LENGTH_SHORT);
                    cScannedIMEIText.setText("");
                }
            }
        });


        cScannedIMEIText = (EditText)findViewById(R.id.c_scanned_imei_text);

        cSearchIMEIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cScannedIMEIText.getText().toString().trim().isEmpty()){
                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    if(deviceNamesArray.length != 0){
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
                                                    MyToast.makeMyToast(activity,"Product is Available", Toast.LENGTH_SHORT);
                                                    addMoreDevicesButton.setVisibility(View.VISIBLE);
                                                    saveAndContinue.setVisibility(View.VISIBLE);

                                                    currentListingItem = new NewOrderCommand.ProductListing();
                                                    currentListingItem.listingId = deviceOrder.productListingId;
                                                    currentListingItem.listingPrice = deviceOrder.inventoryPrice;
                                                    currentListingItem.serialNumber = deviceOrder.serialNumber;
                                                    //currentListingItem.productName = deviceOrder.productName;
                                                    currentListingItem.inventoryPrice = deviceOrder.inventoryPrice;
                                                    currentListingItem.imei = imeiNumberContainer;

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
                        MyToast.makeMyToast(activity,"Please select product.", Toast.LENGTH_SHORT);
                    }
                }else{
                    MyToast.makeMyToast(activity,"IMEI data should not be Empty.", Toast.LENGTH_SHORT);
                }

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

                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, deviceNamesArray);
                            adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            deviceContainerSpinner.setAdapter(adapter1);

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

        if(!RegistrationData.getScanICCIDData().isEmpty()){
            cSearchIMEILayout.setVisibility(View.VISIBLE);
            cScannedIMEIText.setText(RegistrationData.getScanICCIDData());
        }
    }
}
