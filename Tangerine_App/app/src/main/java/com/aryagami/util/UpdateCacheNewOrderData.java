package com.aryagami.util;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.aryagami.data.NewOrderCommand;
import com.aryagami.restapis.RestServiceHandler;

public class UpdateCacheNewOrderData extends IntentService {
    ProgressDialog progressDialog;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateCacheNewOrderData() {
        super("UpdateCacheNewOrderData");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NewOrderCommand[] command = new NewOrderCommand[0];
        MyAsyncTask task = new MyAsyncTask();
        task.doInBackground(command);

    }

    public class MyAsyncTask extends AsyncTask<NewOrderCommand, Void, Void> {

        @Override
        protected Void doInBackground(NewOrderCommand... newOrderCommands) {
              updateNewOrderCacheData(newOrderCommands);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void updateNewOrderCacheData(NewOrderCommand[] newOrderCommands) {
        RestServiceHandler serviceHandler = new RestServiceHandler();

    }
}
