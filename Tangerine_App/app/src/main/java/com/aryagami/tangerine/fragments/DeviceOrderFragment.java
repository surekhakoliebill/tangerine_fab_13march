package com.aryagami.tangerine.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Account;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.DeviceOrder;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.data.Product;
import com.aryagami.data.SubscriptionCommand;
import com.aryagami.data.UserLogin;
import com.aryagami.data.UserRegistration;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.activities.NavigationMainActivity;
import com.aryagami.tangerine.adapters.ListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeviceOrderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Account accounts[];
    List<String> accountList;
    String accountList1[];
    SearchableSpinner accountSpinner;
    Button place_order_device;
    ListView deviceRequestListview;
    List<DeviceOrder> deviceOrderList = new ArrayList<DeviceOrder>();
    DeviceOrder[] deviceOrdersArray;
    String accountId;
    ProgressDialog progressDialog, progressDialog1,progressDialog3;


    private ListAdapter listAdapter;
    ArrayList<Product> products = new ArrayList<Product>();
    ArrayList<Product> productOrders = new ArrayList<Product>();
    NewOrderCommand postOrderCommand = new NewOrderCommand();

    List<Long> productListingIds = new ArrayList<Long>();
    List<Integer> productListingIdCounts = new ArrayList<Integer>();
    List<Float> productListingPrice = new ArrayList<Float>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_order, container, false);
        place_order_device = (Button) view.findViewById(R.id.place_order_device);

        accountSpinner = (SearchableSpinner) view.findViewById(R.id.poduct_type_spinner);
        accountSpinner.setTitle("Select Account");

        deviceRequestListview = (ListView) view.findViewById(R.id.request_listview);

        setAccountDetails();
        getAllProducts();

        place_order_device.setVisibility(View.GONE);
        place_order_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();

                for (Product product : productOrders) {
                    // MyToast.makeMyToast(getActivity(), "Entered Quantity" + product.CartQuantity, Toast.LENGTH_SHORT);
                    productListingIdCounts.add(product.CartQuantity);
                    productListingIds.add(product.ProductListingId);
                    productListingPrice.add(product.ProductPrice);
                }

                collectAllData();

                RestServiceHandler serviceHandler = new RestServiceHandler();
                try {
                    progressDialog3 = ProgressDialogUtil.startProgressDialog(getActivity(), "please wait, placing Order.");

                    serviceHandler.postDeviceNewOrder(postOrderCommand, new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin orderDetails = (UserLogin) data.get(0);

                            if(orderDetails.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog3);

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("Device Order Placed Successfully.\n Order No:" + orderDetails.orderNo);
                                alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(getActivity(), NavigationMainActivity.class);
                                                startActivity(intent);
                                            }
                                        });

                                alertDialog.show();
                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog3);
                                if(orderDetails.status.equals("SESSION_INVALID")){
                                    ReDirectToParentActivity.callLoginActivity(getActivity());
                                }else {
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                    alertDialog.setCancelable(false);
                                    alertDialog.setTitle("Status:\t" + orderDetails.status);
                                    alertDialog.setMessage("Reason: "+orderDetails.reason);
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(getActivity(), NavigationMainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });

                                    alertDialog.show();
                                }

                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog3);
                            BugReport.postBugReport(getActivity(), Constants.emailId,"Error" + error + "Status" + status,"DeviceOrderFragment.java");

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    BugReport.postBugReport(getActivity(), Constants.emailId,"BUG REPORT:"+e.getCause()+"  "+e.getMessage(),"DeviceOrderFragment.java");

                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //CheckNetworkConnection.cehckNetwork(getActivity());
    }

    private void collectAllData() {

        postOrderCommand.productListingIdCounts = productListingIdCounts;
        postOrderCommand.productListingIds = productListingIds;
        postOrderCommand.productListingPrice = productListingPrice;
        postOrderCommand.subscriptions = new ArrayList<SubscriptionCommand>();
        postOrderCommand.contract = new NewOrderCommand.ContractCommand();
        postOrderCommand.fulfillmentDone = true;
        postOrderCommand.resellerCode = UserSession.getResellerId(getActivity());
        postOrderCommand.paymentInfo = new NewOrderCommand.PaymentCommand();

        UserRegistration registration = new UserRegistration();
        registration.userId = UserSession.getUserId(getActivity());
        if(accounts != null){
            accountId = accounts[accountSpinner.getSelectedItemPosition()].accountId;
            registration.accountId = accountId;
        }
        postOrderCommand.userInfo = registration;
    }

    private void placeOrder() {
        productOrders.clear();
        for (int i = 0; i < listAdapter.listProducts.size(); i++) {
            if (listAdapter.listProducts.get(i).CartQuantity > 0) {
                Product products = new Product(listAdapter.listProducts.get(i).ProductName, listAdapter.listProducts.get(i).ProductCategory, listAdapter.listProducts.get(i).ProductPrice, listAdapter.listProducts.get(i).ProductImage, listAdapter.listProducts.get(i).PriceCurrency, listAdapter.listProducts.get(i).ProductListingId);
                products.CartQuantity = listAdapter.listProducts.get(i).CartQuantity;
                productOrders.add(products);
            }
        }
    }


    private void getAllProducts() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        progressDialog1 = ProgressDialogUtil.startProgressDialog(getActivity(), "please wait, Fetching All products..");

        serviceHandler.getResellerGodownStock(UserSession.getResellerId(getContext()), new RestServiceHandler.Callback() {
            @Override
            public void success(DataModel.DataType type, List<DataModel> data) {
                deviceOrdersArray = new DeviceOrder[data.size()];
                data.toArray(deviceOrdersArray);

                for (DeviceOrder order : deviceOrdersArray) {
                    deviceOrderList.add(order);
                    if(order.productCategory.equals("Bundled Service Products")) {
                        products.add(new Product(order.productName, "", order.productPrice, R.drawable.circle, order.priceCurrency, order.productListingId));
                    }else{
                        products.add(new Product(order.productName, order.productCategory, order.productPrice, R.drawable.circle, order.priceCurrency, order.productListingId));
                    }
                }

                listAdapter = new ListAdapter(getContext(), products);
                deviceRequestListview.setAdapter(listAdapter);
                ProgressDialogUtil.stopProgressDialog(progressDialog1);
            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                ProgressDialogUtil.stopProgressDialog(progressDialog1);
                BugReport.postBugReport(getActivity(), Constants.emailId,"ERROR:"+error+"status:"+status,"DEVICE ORDER");
            }
        });
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
                          /*  account = new Account[data.size()];
                            data.toArray(account);

                            NewOrderCommand.setAccount(account);

                            accountList = new ArrayList<String>();
                            for (Account acc : account) {
                                accountList.add(acc.username + "-" + acc.accountId);
                            }

                            accountList1 = new String[accountList.size()];
                            accountList.toArray(accountList1);

                            if (accountList1 != null) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountList1);
                                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                accountSpinner.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(), "EMPTY DATA!", Toast.LENGTH_SHORT).show();

                            }*/

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()) {
            case R.id.poduct_type_spinner:
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                accountId = accounts[position].accountId;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
}