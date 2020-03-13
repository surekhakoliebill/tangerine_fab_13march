package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.WalletAccountVo;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.CheckNetworkConnection;
import com.aryagami.util.MyToast;
import com.aryagami.util.UserSession;

import java.io.IOException;
import java.util.List;

public class ResellerWalletBalanceActivity extends AppCompatActivity {
     Activity activity = this;
     WalletAccountVo accountVo = new WalletAccountVo();
     TextView walletBalance, walletName, walletType;
     ImageButton backImageButton;

    public  void onTrimMemory(int level) {
        System.gc();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller_wallet_balance);
        walletBalance = (TextView)findViewById(R.id.balance_text);
        walletName = (TextView)findViewById(R.id.wallet_name);
        walletType = (TextView)findViewById(R.id.wallet_type);
        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);

           getResellerBalanceDetails();


           backImageButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   activity.finish();
               }
           });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(ResellerWalletBalanceActivity.this);
    }

    private void getResellerBalanceDetails() {

        RestServiceHandler serviceHandler = new RestServiceHandler();
        try {
            serviceHandler.getResellerWalletBalance(UserSession.getResellerId(activity), new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {

                    if(data != null){
                        if(data.size() != 0) {
                            accountVo = (WalletAccountVo) data.get(0);
                            setAccountBalanceDetails(accountVo);
                        }else{
                            MyToast.makeMyToast(activity,"EMPTY DATA", Toast.LENGTH_SHORT);
                           // ReDirectToParentActivity.callLoginActivity(activity);
                        }
                    }else{
                        MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    BugReport.postBugReport(activity, Constants.emailId,"Error"+error+"Status"+status,"ResellerWalletBalanceActivity");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.postBugReport(activity, Constants.emailId,"Error:"+e.getMessage()+" "+e.getCause(),"ResellerChangePasswordActivity");
        }
    }

    private void setAccountBalanceDetails(WalletAccountVo accountVo) {

        if(accountVo.walletType != null){
            walletType.setText(accountVo.walletType.toString());
        }else{
            walletType.setText("");
        }

        if(accountVo.walletName != null){
            walletName.setText(accountVo.walletName.toString());
        }else{
            walletName.setText("");
        }

        if(accountVo.walletBalance != null){
            walletBalance.setText(accountVo.walletBalance.toString());
        }else{
            walletBalance.setText("");
        }
    }
}
