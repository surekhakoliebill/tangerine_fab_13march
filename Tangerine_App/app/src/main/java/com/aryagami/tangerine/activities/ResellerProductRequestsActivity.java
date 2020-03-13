package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.WalletAccountTransactionLogVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ResellerProductRequestsListAdapter;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResellerProductRequestsActivity extends AppCompatActivity {

    ListView etopupListview;
    Activity activity = this;
    List<WalletAccountTransactionLogVo> eTopupRequestsList = new ArrayList<>();
    WalletAccountTransactionLogVo[] transactionLogVos;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etopup_requests);

        etopupListview = (ListView)findViewById(R.id.etopup_listview);
        TextView headText = (TextView)findViewById(R.id.header_text);
        headText.setText("Product Requests");

        getEtopupList();

        ImageButton backButton = (ImageButton)findViewById(R.id.back_imgbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                Intent intent = new Intent(activity, NavigationMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEtopupList() {
        RestServiceHandler serviceHandler = new RestServiceHandler();
        String resellerId = UserSession.getResellerId(activity);
        try {

            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please wait...");
            serviceHandler.getProductRequestsFromReseller(resellerId, new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    WalletAccountTransactionLogVo logVo = (WalletAccountTransactionLogVo)data.get(0);
                    if(logVo != null){
                        if(logVo.status.equals("success")){
                            if(logVo.productRequests != null)
                            if(logVo.productRequests.size() !=0){
                                transactionLogVos = new WalletAccountTransactionLogVo[logVo.productRequests.size()];
                                logVo.productRequests.toArray(transactionLogVos);

                               // ArrayAdapter
                                ArrayAdapter adapter = new ResellerProductRequestsListAdapter(activity, transactionLogVos);
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
