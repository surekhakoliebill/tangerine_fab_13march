package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.aryagami.R;
import com.aryagami.data.CacheNewOrderData;
import com.aryagami.data.NewOrderCommand;
import com.aryagami.tangerine.adapters.CacheUpdateUserDataAdapter;
import com.aryagami.util.AlertMessage;
import com.aryagami.util.CheckNetworkConnection;

import java.util.ArrayList;
import java.util.List;

public class CacheUpdateUserDataActivity extends AppCompatActivity {
    Activity activity=this;
    NewOrderCommand[] orderCommands;
    ListView allCacheList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_update_user);
        allCacheList = (ListView)findViewById(R.id.cache_listview);

        List<NewOrderCommand> commandList = new ArrayList<NewOrderCommand>();
       commandList= CacheNewOrderData.loadUpdateUserCacheList(activity);

        if(commandList != null)
        if(commandList.size() != 0){
            orderCommands = new NewOrderCommand[commandList.size()];
            commandList.toArray(orderCommands);
            CacheUpdateUserDataAdapter adapter = new CacheUpdateUserDataAdapter(activity,orderCommands);
            allCacheList.setAdapter(adapter);
        }else{
            AlertMessage.alertDialogMessage(activity,"Alert:","Empty Data.");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        CheckNetworkConnection.cehckNetwork(CacheUpdateUserDataActivity.this);
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
}
