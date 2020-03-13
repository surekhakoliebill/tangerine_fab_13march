package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.ResellerRequestVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ResellerStockRequestsListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ResellerStockRequestsActivity extends AppCompatActivity {

    ListView etopupListview;
    Activity activity = this;
    ResellerRequestVo[] transactionLogVos;
    String startDate, endDate;
    Button stockRequestButton;
    ImageButton backButton;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_requests);

        etopupListview = (ListView)findViewById(R.id.etopup_listview);
        stockRequestButton = (Button)findViewById(R.id.stock_req_button);
        backButton = (ImageButton)findViewById(R.id.back_imgbtn);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
         endDate= formatter.format(date);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, - (30));
        startDate = formatter.format(c.getTime());

        getResellerRequestsList();

        stockRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ResellerStockRequestActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                Intent intent = new Intent(activity, NavigationMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getResellerRequestsList() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        String resellerId = UserSession.getResellerId(activity);
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait, fetching stock requests!");

            serviceHandler.getResellerStockRequestsList(resellerId,startDate,endDate, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    ResellerRequestVo logVo = (ResellerRequestVo) data.get(0);
                    if(logVo != null){
                        if(logVo.status.equals("success")){
                            if(logVo.stockRequestsList != null)
                            if(logVo.stockRequestsList.size() !=0){
                                transactionLogVos = new ResellerRequestVo[logVo.stockRequestsList.size()];
                                logVo.stockRequestsList.toArray(transactionLogVos);

                               // ArrayAdapter
                                ArrayAdapter adapter = new ResellerStockRequestsListAdapter(activity, transactionLogVos);
                                etopupListview.setAdapter(adapter);

                                ProgressDialogUtil.stopProgressDialog(progressDialog);

                            }else{
                                ProgressDialogUtil.stopProgressDialog(progressDialog);

                                MyToast.makeMyToast(activity,"E-Topup Requests List is Empty.", Toast.LENGTH_SHORT);
                            }

                        }else{
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(activity,"Status"+logVo.status, Toast.LENGTH_SHORT);
                        }
                    }

                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {

                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                    BugReport.postBugReport(activity, Constants.emailId,"ERROR: "+error+"STATUS: "+status,"get etopup requests");
                }
            });

        }catch (IOException io){
            BugReport.postBugReport(activity, Constants.emailId,"Message: "+io.getMessage()+"Cause: "+io.getCause(),"");
        }
    }
}
