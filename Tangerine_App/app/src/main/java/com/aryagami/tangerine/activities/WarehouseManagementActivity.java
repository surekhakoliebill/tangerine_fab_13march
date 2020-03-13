package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.WarehouseBinLotVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.WarehouseBinListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.util.List;

public class WarehouseManagementActivity extends AppCompatActivity {
ListView warehouseStockList;
Activity activity = this;

WarehouseBinLotVo[] warehouseBinLotVosArray;
    ProgressDialog progressDialog;
    ImageButton backImageButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_management);

        warehouseStockList = (ListView)findViewById(R.id.warehouse_stock_list);
        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);

        RestServiceHandler serviceHandler = new RestServiceHandler();

        String resellerId = UserSession.getResellerId(activity);
        progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait...");

        serviceHandler.getResellerWarehouseStock(resellerId, new RestServiceHandler.Callback() {
            @Override
            public void success(DataModel.DataType type, List<DataModel> data) {
                warehouseBinLotVosArray = new WarehouseBinLotVo[data.size()];
                data.toArray(warehouseBinLotVosArray);
                ProgressDialogUtil.stopProgressDialog(progressDialog);
                 if(warehouseBinLotVosArray.length !=0) {
                     ArrayAdapter adapter = new WarehouseBinListAdapter(activity, warehouseBinLotVosArray);
                     warehouseStockList.setAdapter(adapter);
                 }else{
                     MyToast.makeMyToast(activity,"Empty Data. Reason: INVALID_SESSION", Toast.LENGTH_SHORT);
                     ReDirectToParentActivity.callLoginActivity(activity);
                 }
            }

            @Override
            public void failure(RestServiceHandler.ErrorCode error, String status) {
                ProgressDialogUtil.stopProgressDialog(progressDialog);
                BugReport.postBugReport(activity, Constants.emailId,"ERROR:"+error+"STATUS:"+status,"WAREHOUSE ");
            }
        });

        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                activity.finish();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(WarehouseManagementActivity.this);
    }
}
