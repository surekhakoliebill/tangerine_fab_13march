package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.ProductRequestCommand;
import com.aryagami.data.ResellerRequestCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.VoucherTypesVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class ResellerStockRequestActivity extends AppCompatActivity {
    Spinner productSpinner;
    SearchableSpinner productCatSpinner, airtimeSpinner, evoucherSpinner;
    EditText quantity;
    Button cancelButton, submitButton;
    Activity activity = this;
    DeviceOrder deviceOrdersArray[];
    List<DeviceOrder> deviceOrderList = new ArrayList<>();
    String deviceNamesArray[], airtimeVouchersArray[], eVouchersArray[];
    ProgressDialog progressDialog, progressDialog1, progressDialog3, progressDialog4;
    String[] productTypes = {"Products","E-Voucher","Airtime Voucher","E-Topup"};
    LinearLayout productLayout, evoucherLayout, airtimeLayout;

    List<VoucherTypesVo.VoucherBatchVo> airtimeVouchersList = new ArrayList<VoucherTypesVo.VoucherBatchVo>();
    List<VoucherTypesVo.VoucherBatchVo> eVouchersList = new ArrayList<VoucherTypesVo.VoucherBatchVo>();
    TextView totalValue;
    LinearLayout totalValueLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_request);

        productSpinner = (Spinner)findViewById(R.id.select_product_spinner);
        productCatSpinner = (SearchableSpinner)findViewById(R.id.select_product_category);
        airtimeSpinner = (SearchableSpinner)findViewById(R.id.airtime_spinner);
        evoucherSpinner = (SearchableSpinner)findViewById(R.id.evoucher_spinner);
        quantity = (EditText)findViewById(R.id.quantity_text);

        cancelButton = (Button)findViewById(R.id.cancel_btn);
        submitButton = (Button)findViewById(R.id.submit_request);

        productLayout = (LinearLayout)findViewById(R.id.product_layout);
        evoucherLayout = (LinearLayout)findViewById(R.id.evoucher_layout);
        airtimeLayout = (LinearLayout)findViewById(R.id.airtime_layout);

        totalValue = (TextView)findViewById(R.id.total_value);
        totalValueLayout = (LinearLayout) findViewById(R.id.total_value_layout);
        totalValueLayout.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, productTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        productSpinner.setAdapter(adapter);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();

                if(selectedItem.equals("Products")){
                    productLayout.setVisibility(view.VISIBLE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("Stock Quantity");
                    quantity.setText("");
                    totalValue.setText("");
                    totalValueLayout.setVisibility(View.GONE);

                }else if(selectedItem.equals("E-Voucher")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.VISIBLE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("Stock Quantity");
                    quantity.setText("");
                    totalValue.setText("");
                    totalValueLayout.setVisibility(View.GONE);

                }else if(selectedItem.equals("Airtime Voucher")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.VISIBLE);
                    quantity.setHint("Stock Quantity");
                    quantity.setText("");
                    totalValue.setText("");
                    totalValueLayout.setVisibility(View.GONE);

                }else if(selectedItem.equals("E-Topup")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("E-Topup Balance Value");
                    quantity.setText("");
                    totalValue.setText("");
                    totalValueLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getAllProductDetails();
        getAllVouchers();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Float productValue = 0.0f;
                if(!quantity.getText().toString().isEmpty()){
                    int qun = Integer.valueOf(quantity.getText().toString());
                    if(qun == 0){
                        totalValue.setText(0+"");
                    }else{
                        if(productSpinner.getSelectedItem().equals("Products")) {

                            totalValueLayout.setVisibility(View.VISIBLE);

                            if(productCatSpinner.getSelectedItem() != null) {
                                if(deviceOrderList.get(productCatSpinner.getSelectedItemPosition()).productPrice != null){
                                    productValue = deviceOrderList.get(productCatSpinner.getSelectedItemPosition()).productPrice;
                                    int total = (int) (productValue * qun);
                                    totalValue.setText(total+"");
                                }else{
                                    totalValueLayout.setVisibility(View.GONE);
                                }
                            }else{
                                MyToast.makeMyToast(activity,"Select Product Category!", Toast.LENGTH_SHORT);
                            }


                        }else if(productSpinner.getSelectedItem().equals("E-Voucher")){
                            totalValueLayout.setVisibility(View.VISIBLE);

                            if(evoucherSpinner.getSelectedItem() != null) {
                                if(eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).voucherValue != null){
                                    productValue = eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).voucherValue;

                                    int total = (int) (productValue * qun);
                                    totalValue.setText(total+"");
                                }else{
                                    totalValue.setText("");
                                    totalValueLayout.setVisibility(View.GONE);
                                }
                            }else{
                                MyToast.makeMyToast(activity,"Select Voucher.", Toast.LENGTH_SHORT);
                            }

                        }else if( productSpinner.getSelectedItem().equals("Airtime Voucher")){
                            totalValueLayout.setVisibility(View.VISIBLE);

                            if(airtimeSpinner.getSelectedItem() != null) {
                                if (airtimeVouchersList.get(airtimeSpinner.getSelectedItemPosition()).voucherValue != null)
                                    productValue = airtimeVouchersList.get(airtimeSpinner.getSelectedItemPosition()).voucherValue;

                                int total = (int) (productValue * qun);
                                totalValue.setText(total+"");
                            }else{
                                totalValueLayout.setVisibility(View.GONE);
                            }
                        }else if(productSpinner.getSelectedItem().equals("E-Topup")){
                            totalValueLayout.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ResellerRequestCommand requestCommand = new ResellerRequestCommand();

                RestServiceHandler serviceHandler = new RestServiceHandler();

                if(productSpinner.getSelectedItem().equals("Products")){
                    ProductRequestCommand proRequestCommand = new ProductRequestCommand();
                    proRequestCommand = collectProductRequestData();

                    if(proRequestCommand != null){
                        List<ProductRequestCommand> productRequestCommandList = new ArrayList<>();
                        productRequestCommandList.add(proRequestCommand);
                        try{
                            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...!");

                            serviceHandler.postProductRequest(productRequestCommandList, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin)data.get(0);

                                    if(userLogin.status.equals("success")){
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request submitted successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    }else if(userLogin.status.equals("INVALID_SESSION")){
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:"+userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }

                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error: "+error+", Status: "+status,"Reseller Products List");
                                }
                            });

                        }catch (Exception io){
                            BugReport.postBugReport(activity, Constants.emailId,"MESSAGE: "+io.getMessage()+", Cause: "+io.getCause(),"Reseller Products List");
                        }
                    }

                }else if(productSpinner.getSelectedItem().equals("E-Voucher") || productSpinner.getSelectedItem().equals("Airtime Voucher")){

                    requestCommand = collectRequestData();
                    if(requestCommand != null){
                        try {
                            progressDialog1 = ProgressDialogUtil.startProgressDialog(activity,"Submitting Request..");
                            serviceHandler.postResellerRequest(requestCommand, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin)data.get(0);

                                    if(userLogin.status.equals("success")){
                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request submitted successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    }else if(userLogin.status.equals("INVALID_SESSION")){
                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    }else{
                                        ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:"+userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }
                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog1);
                                    BugReport.postBugReport(activity, Constants.emailId,"Error: "+error+", Status: "+status,"Reseller Products List");
                                }
                            });
                        }catch (Exception io){
                            BugReport.postBugReport(activity, Constants.emailId,"MESSAGE: "+io.getMessage()+", Cause: "+io.getCause(),"Reseller Products List");
                        }

                    }
                }else if(productSpinner.getSelectedItem().equals("E-Topup")){
                    try{
                        String resellerId = UserSession.getResellerId(activity);
                        requestCommand = collectRequestData();

                        Float transferAmount = Float.parseFloat(quantity.getText().toString());
                        if(requestCommand != null) {
                            progressDialog3 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, fetching stock requests!");

                            serviceHandler.postETopupRequest(resellerId, transferAmount, new RestServiceHandler.Callback() {
                                @Override
                                public void success(DataModel.DataType type, List<DataModel> data) {
                                    UserLogin userLogin = (UserLogin) data.get(0);

                                    if (userLogin.status.equals("success")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog3);

                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setIcon(R.drawable.success_icon);
                                        alertBuilder.setTitle("Success!");
                                        alertBuilder.setMessage("Request submitted successfully.");
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();

                                    } else if (userLogin.status.equals("INVALID_SESSION")) {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {
                                        ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                                        alertBuilder.setTitle("Alert");
                                        alertBuilder.setMessage("Status:" + userLogin.status);
                                        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        });
                                        alertBuilder.create().show();
                                    }

                                }

                                @Override
                                public void failure(RestServiceHandler.ErrorCode error, String status) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                    BugReport.postBugReport(activity, Constants.emailId, "Error: " + error + ", Status: " + status, "Reseller Products List");
                                }
                            });
                        }
                    }catch (Exception io){
                        BugReport.postBugReport(activity, Constants.emailId,"MESSAGE: "+io.getMessage()+", Cause: "+io.getCause(),"Reseller Products List");
                    }
                }


            }
        });

    }

    private void startMainActivity() {
        activity.finish();
        Intent intent = new Intent(activity, ResellerStockRequestsActivity.class);
        startActivity(intent);
    }

    private ProductRequestCommand collectProductRequestData() {
        ProductRequestCommand command = new ProductRequestCommand();

        command.fromBinRef = UserSession.getAggregator(activity);
        command.inboundStock = false;
        command.isResellerTransfer = true;

        if(productCatSpinner.getSelectedItem() != null) {
            command.productListingId = deviceOrderList.get(productCatSpinner.getSelectedItemPosition()).productListingId;
        }else{
            MyToast.makeMyToast(activity,"Select Product Category!", Toast.LENGTH_SHORT);
           return null;
        }

        if(!quantity.getText().toString().isEmpty()){
            command.inwardQuantity = Integer.valueOf(quantity.getText().toString());
        }else{
            MyToast.makeMyToast(activity,"Enter Stock Quantity!", Toast.LENGTH_SHORT);
            return null;
        }

        command.toBinRef = UserSession.getResellerId(activity);


        /*fromBinRef: "ca9f1cc2-90de-4a8e-97b0-42547f8e48a4"
                inboundStock: false
                inwardQuantity: 2
                isResellerTransfer: true
                productListingId: 5733032
                toBinRef: "c13e5e28-01a3-4976-b26f-d42e1fff415b"*/

        return command;

    }

    private ResellerRequestCommand collectRequestData() {
        ResellerRequestCommand resellerRequestCommand = new ResellerRequestCommand();
        List<ResellerRequestCommand.ResellerRequestMapperCommand> requestMapperCommandsList = new ArrayList<ResellerRequestCommand.ResellerRequestMapperCommand>();

        // resellerRequestCommand.resellerId = UserSession.getResellerId(activity);


        if(productSpinner.getSelectedItem() != null){
           if(productSpinner.getSelectedItem().equals("E-Voucher")){

                resellerRequestCommand.fromResellerId = UserSession.getAggregator(activity);
                resellerRequestCommand.isResellerTransfer = true;
                resellerRequestCommand.requestType= "VoucherTransfer";
                resellerRequestCommand.resellerId = UserSession.getResellerId(activity);

               ResellerRequestCommand.ResellerRequestMapperCommand requestMapperCommand = new ResellerRequestCommand.ResellerRequestMapperCommand();

               requestMapperCommand.entityType = "VoucherTransfer";

                if(evoucherSpinner.getSelectedItem() != null) {
                    if(eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).voucherBatchId != null){
                        requestMapperCommand.voucherBatchId = eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).voucherBatchId.toString();
                    }
                    if(eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).entityId != null)
                    requestMapperCommand.entityId = eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).entityId.toString();

                    if(eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).subEntityId != null)
                    requestMapperCommand.entitySubId = eVouchersList.get(evoucherSpinner.getSelectedItemPosition()).subEntityId.toString();

                }else{
                    MyToast.makeMyToast(activity,"Select Voucher.", Toast.LENGTH_SHORT);
                    return  null;
                }

               if(!quantity.getText().toString().isEmpty()){
                   requestMapperCommand.quantity = Long.valueOf(quantity.getText().toString());
               }else{
                   MyToast.makeMyToast(activity,"Enter Stock Quantity!", Toast.LENGTH_SHORT);
                   return null;
               }

               requestMapperCommandsList.add(requestMapperCommand);
               resellerRequestCommand.requestedVouchers = requestMapperCommandsList;


                /*
                fromResellerId: "ca9f1cc2-90de-4a8e-97b0-42547f8e48a4" // Aggregator
                isResellerTransfer: true
                requestType: "VoucherTransfer"
                requestedVouchers: [{entityType: "VoucherTransfer", entityId: 46820, voucherBatchId: 5732990, quantity: 2,…}]
                0: {entityType: "VoucherTransfer", entityId: 46820, voucherBatchId: 5732990, quantity: 2,…}
                entityId: 46820
                entitySubId: 264428
                entityType: "VoucherTransfer"
                quantity: 2
                voucherBatchId: 5732990
                resellerId: "c13e5e28-01a3-4976-b26f-d42e1fff415b" // UserSession ResellerId
           */

            }else if(productSpinner.getSelectedItem().equals("Airtime Voucher")){

               resellerRequestCommand.fromResellerId = UserSession.getAggregator(activity);
               resellerRequestCommand.isResellerTransfer = true;
               resellerRequestCommand.requestType= "VoucherTransfer";
               resellerRequestCommand.resellerId = UserSession.getResellerId(activity);

               ResellerRequestCommand.ResellerRequestMapperCommand requestMapperCommand = new ResellerRequestCommand.ResellerRequestMapperCommand();

               requestMapperCommand.entityType = "VoucherTransfer";

               if(airtimeSpinner.getSelectedItem() != null) {

                   if(airtimeVouchersList.get(airtimeSpinner.getSelectedItemPosition()).voucherBatchId != null)
                   requestMapperCommand.voucherBatchId = airtimeVouchersList.get(airtimeSpinner.getSelectedItemPosition()).voucherBatchId.toString();

                   // requestMapperCommand.entityId = eVouchersList.get(airtimeSpinner.getSelectedItemPosition()).entityId.toString();
                  // requestMapperCommand.entitySubId = eVouchersList.get(airtimeSpinner.getSelectedItemPosition()).subEntityId.toString();

               }else{
                   MyToast.makeMyToast(activity,"Select Voucher.", Toast.LENGTH_SHORT);
                   return  null;
               }

               if(!quantity.getText().toString().isEmpty()){
                   requestMapperCommand.quantity = Long.valueOf(quantity.getText().toString());
               }else{
                   MyToast.makeMyToast(activity,"Enter Stock Quantity!", Toast.LENGTH_SHORT);
                   return null;
               }

               requestMapperCommandsList.add(requestMapperCommand);
               resellerRequestCommand.requestedVouchers = requestMapperCommandsList;

                /*fromResellerId: "ca9f1cc2-90de-4a8e-97b0-42547f8e48a4"
                    isResellerTransfer: true
                    requestType: "VoucherTransfer"
                    requestedVouchers: [{entityType: "VoucherTransfer", voucherBatchId: 5732986, quantity: 2}]
                    0: {entityType: "VoucherTransfer", voucherBatchId: 5732986, quantity: 2}
                    entityType: "VoucherTransfer"
                    quantity: 2
                    voucherBatchId: 5732986
                    resellerId: "c13e5e28-01a3-4976-b26f-d42e1fff415b"*/

            }else if(productSpinner.getSelectedItem().equals("E-Topup")){

               if(!quantity.getText().toString().isEmpty()){

               }else{
                   MyToast.makeMyToast(activity,"Enter E-Topup Balance!", Toast.LENGTH_SHORT);
                   return null;
               }
            }
        }
        return resellerRequestCommand;
    }

    public void getAllProductDetails(){
        RestServiceHandler serviceHandler = new RestServiceHandler();
        String aggregator = UserSession.getAggregator(activity);

        progressDialog4 = ProgressDialogUtil.startProgressDialog(activity, "Please wait, fetching products..");

        serviceHandler.getResellerProductStock(aggregator, new RestServiceHandler.Callback() {
            @Override
            public void success(DataModel.DataType type, List<DataModel> data) {
                if(data != null){
                    deviceOrdersArray = new DeviceOrder[data.size()];
                    data.toArray(deviceOrdersArray);

                    if(deviceOrdersArray != null){
                        if(deviceOrdersArray.length != 0){
                            for (DeviceOrder order : deviceOrdersArray) {
                                deviceOrderList.add(order);

                            }
                            deviceNamesArray = new String[deviceOrderList.size()];
                            List<String> namesList = new ArrayList<String>();
                            for(DeviceOrder order : deviceOrderList){
                                namesList.add(order.productName.toString()+"/"+order.productCategory);
                            }
                            namesList.toArray(deviceNamesArray);

                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, deviceNamesArray);
                            adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            productCatSpinner.setAdapter(adapter1);

                        }else{
                            MyToast.makeMyToast(activity,"EMPTY DEVICE LIST", Toast.LENGTH_SHORT);
                        }
                    }

                    ProgressDialogUtil.stopProgressDialog(progressDialog4);
                }else{
                    ProgressDialogUtil.stopProgressDialog(progressDialog4);
                    MyToast.makeMyToast(activity,"EMPTY DEVICE LIST", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                ProgressDialogUtil.stopProgressDialog(progressDialog4);
                BugReport.postBugReport(activity, Constants.emailId,"Error: "+error+", Status: "+status,"Reseller Products List");
            }
        });
    }

    public void getAllVouchers(){
        RestServiceHandler serviceHandler = new RestServiceHandler();
        String aggregator = UserSession.getAggregator(activity);

        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait, fetching Vouchers!");

        serviceHandler.getResellerVouchersStock(aggregator, new RestServiceHandler.Callback() {
            @Override
            public void success(DataModel.DataType type, List<DataModel> data) {
                 if(data != null){
                     VoucherTypesVo voucherTypesVo = (VoucherTypesVo) data.get(0);
                     if(voucherTypesVo.responseStatus.equals("success")){

                         if(voucherTypesVo.airtimeVouchers != null){
                             airtimeVouchersArray = new String[voucherTypesVo.airtimeVouchers.size()];
                             List<String> namesList = new ArrayList<String>();
                            for(VoucherTypesVo.VoucherBatchVo batchVo: voucherTypesVo.airtimeVouchers){
                                namesList.add(batchVo.planGroupName.toString()+"/Value="+batchVo.voucherValue+"/ Remaining Quantity "+batchVo.remainingVouchers);
                                airtimeVouchersList.add(batchVo);
                            }
                             namesList.toArray(airtimeVouchersArray);
                             ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, airtimeVouchersArray);
                             adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                             airtimeSpinner.setAdapter(adapter1);

                         }
                         if(voucherTypesVo.serviceBundleVouchers != null){
                             eVouchersArray = new String[voucherTypesVo.serviceBundleVouchers.size()];
                             List<String> namesList = new ArrayList<String>();
                             for(VoucherTypesVo.VoucherBatchVo batchVo: voucherTypesVo.serviceBundleVouchers){
                                 namesList.add(batchVo.planGroupName.toString()+"/Value="+batchVo.voucherValue+"/ Remaining Quantity "+batchVo.remainingVouchers);
                                 eVouchersList.add(batchVo);
                             }
                             namesList.toArray(eVouchersArray);
                             ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, eVouchersArray);
                             adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                             evoucherSpinner.setAdapter(adapter1);

                         }
                         ProgressDialogUtil.stopProgressDialog(progressDialog);
                     }else{
                         ProgressDialogUtil.stopProgressDialog(progressDialog);
                         MyToast.makeMyToast(activity,"Status:"+voucherTypesVo.responseStatus, Toast.LENGTH_SHORT);
                     }
                 }else{
                     ProgressDialogUtil.stopProgressDialog(progressDialog);
                     MyToast.makeMyToast(activity,"Vouchers list is empty.", Toast.LENGTH_SHORT);
                 }
            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                BugReport.postBugReport(activity, Constants.emailId,"Error: "+error+", Status: "+status,"Reseller Available Vouchers List");

            }
        });

    }


    /*@Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.select_product_spinner:
                String selectedItem = adapterView.getItemAtPosition(position).toString();

                if(selectedItem.equals("Products")){
                    productLayout.setVisibility(view.VISIBLE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("Stock Quantity");

                }else if(selectedItem.equals("E-Voucher")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.VISIBLE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("Stock Quantity");

                }else if(selectedItem.equals("Airtime Voucher")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.VISIBLE);
                    quantity.setHint("Stock Quantity");

                }else if(selectedItem.equals("E-Topup")){
                    productLayout.setVisibility(view.GONE);
                    evoucherLayout.setVisibility(view.GONE);
                    airtimeLayout.setVisibility(view.GONE);
                    quantity.setHint("E-Topup Balance Value");
                }

                break;
            case R.id.select_product_category:
               // postProductListingID = deviceOrdersArray[position].productListingId;
                break;
            case R.id.evoucher_spinner:
               // postProductListingID = deviceOrdersArray[position].productListingId;
                break;
            case R.id.airtime_spinner:
                //postProductListingID = deviceOrdersArray[position].productListingId;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }*/
}
