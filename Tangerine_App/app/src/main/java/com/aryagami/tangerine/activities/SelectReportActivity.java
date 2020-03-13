package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;
import com.aryagami.util.UserSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SelectReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner filterSpinner;
    String[] filterOptions = {"E-Topup Sales Report", "Voucher Sales Report", "Voucher Airtime Sales Report", "SIM Sales Report"};
    Activity activity = this;
    TextInputEditText startDate, endDate;

    Button back, download;
    DatePickerDialog datePickerDialog;

    int year;
    int month;
    int dayofMonth;
    Calendar calendar;
    DownloadManager dm;
    String FileName;
    long queueid;
    ProgressDialog progressDialog;
    private long downloadId;
    Date date1, date2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_report_activity);

        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, filterOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        filterSpinner.setAdapter(adapter1);
        filterSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) SelectReportActivity.this);

        back = findViewById(R.id.back);
        download = findViewById(R.id.download);
        startDate = (TextInputEditText)findViewById(R.id.btnDate);
        endDate = (TextInputEditText)findViewById(R.id.btnDate1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long broadcastedDownloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (broadcastedDownloadID == downloadId){
                        if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
                            Toast.makeText(SelectReportActivity.this, "Download complete.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SelectReportActivity.this, "Download not complete.", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        }, filter);



            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(SelectReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //startDate.setText(year + "-" + month + "-" + dayOfMonth);

                            startDate.setText(year + "-"
                                    + ((month) < 9 ? ("0" + (month + 1)) : (month + 1)) + "-" + ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)));

                        }
                    }, year, month, dayofMonth);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }
            });


        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(SelectReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //endDate.setText(year + "-" + month + "-" + dayOfMonth);

                        endDate.setText(year + "-"
                                + ((month)<9?("0"+(month+1)):(month+1)) + "-" + ((dayOfMonth)<10?("0"+dayOfMonth):(dayOfMonth)));
                    }
                },year,month,dayofMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterSpinner.getSelectedItem() != null){
                    if (!startDate.getText().toString().isEmpty()&&!endDate.getText().toString().isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                             date1 = sdf.parse(startDate.getText().toString());
                             date2 = sdf.parse(endDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(date1.compareTo(date2) < 0 || date1.compareTo(date2) == 0){
                            if(filterSpinner.getSelectedItem().equals("E-Topup Sales Report")){
                                getEtopupSalesReports();
                            }else if (filterSpinner.getSelectedItem().equals("Voucher Sales Report")){
                                getVouchersSalesReport();
                            }else if (filterSpinner.getSelectedItem().equals("SIM Sales Report")){
                                getSimSalesReport();
                            }else if (filterSpinner.getSelectedItem().equals("Voucher Airtime Sales Report")){
                                getVoucherAirtimeSalesReport();
                            }
                        }else {
                            Toast.makeText(SelectReportActivity.this, "Please select the start date before end date.", Toast.LENGTH_SHORT).show();

                        }

                    }else {
                        Toast.makeText(SelectReportActivity.this, "Please select the start date or end date.", Toast.LENGTH_SHORT).show();


                    }

                }
            }

            private void getVoucherAirtimeSalesReport() {
                RestServiceHandler restServiceHandler = new RestServiceHandler();
                final Boolean generateReport = true;

                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
                    restServiceHandler.getVoucherAirtimeSalesReports(UserSession.getResellerId(activity),startDate.getText().toString(),endDate.getText().toString(),generateReport,new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin etopupReport = (UserLogin) data.get(0);
                            if(etopupReport.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (etopupReport.reportStatus.equals("success")) {
                                    String fileName = "voucher_airtime_sales_"+ endDate.getText().toString()+".csv";
                                    if (etopupReport.docPath != null) {
                                        String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
                                        //Uri uri = Uri.parse("http://41.217.232.48:4080/fetch_documents/reseller_reports/640aa381-6a30-4af3-919f-71524d533086/sim_sales_report_2020-02-01.csv");

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                        //DownloadManager.Request request = new DownloadManager.Request(uri);
                                        //request.setTitle("Report Downloading");
                                        request.setDescription("Downloaded...");
                                        request.setDestinationInExternalFilesDir(SelectReportActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        downloadId = downloadManager.enqueue(request);
                                    } else {
                                        MyToast.makeMyToast(activity, "EMPTY DATA", Toast.LENGTH_SHORT);
                                    }
                                    MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);

                                } else {
                                    MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                                }
                            }else if(etopupReport.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity, "Downloading..."+etopupReport.status, Toast.LENGTH_SHORT);
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(activity, error+""+status, Toast.LENGTH_SHORT);
                            BugReport.postBugReport(activity, Constants.emailId,"status"+status+error,"");

                        }
                    });
                }catch (Exception io){

                }


            }

            private void getSimSalesReport() {
                RestServiceHandler restServiceHandler = new RestServiceHandler();
                final Boolean generateReport = true;

                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
                    restServiceHandler.getSimSalesReports(UserSession.getResellerId(activity),startDate.getText().toString(),endDate.getText().toString(),generateReport,new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin etopupReport = (UserLogin) data.get(0);
                            if(etopupReport.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (etopupReport.reportStatus.equals("success")) {
                                    String fileName = "sim_sales_report_"+ endDate.getText().toString()+".csv";
                                    if (etopupReport.docPath != null) {
                                        String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
                                        //Uri uri = Uri.parse("http://41.217.232.48:4080/fetch_documents/reseller_reports/640aa381-6a30-4af3-919f-71524d533086/sim_sales_report_2020-02-01.csv");

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                        //DownloadManager.Request request = new DownloadManager.Request(uri);
                                        //request.setTitle("Report Downloading");
                                        request.setDescription("Downloaded...");
                                        request.setDestinationInExternalFilesDir(SelectReportActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        downloadId = downloadManager.enqueue(request);
                                    } else {
                                        MyToast.makeMyToast(activity, "EMPTY DATA", Toast.LENGTH_SHORT);
                                    }
                                    MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);

                                } else {
                                    MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                                }
                            }else if(etopupReport.status.equals("INVALID_SESSION")){
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity, "Downloading..."+etopupReport.status, Toast.LENGTH_SHORT);
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(activity, error+""+status, Toast.LENGTH_SHORT);
                            BugReport.postBugReport(activity, Constants.emailId,"status"+status+error,"");

                        }
                    });
                }catch (Exception io){

                }

            }

            private void getVouchersSalesReport()  {
                RestServiceHandler restServiceHandler = new RestServiceHandler();
                final Boolean generateReport = true;

                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
                    restServiceHandler.getVouchersSalesReports(UserSession.getResellerId(activity),startDate.getText().toString(),endDate.getText().toString(),generateReport,new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin etopupReport = (UserLogin) data.get(0);
                            if(etopupReport.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                if (etopupReport.reportStatus.equals("success")) {
                                    String fileName = "voucher_sales_report_"+ endDate.getText().toString()+".csv";
                                    if (etopupReport.docPath != null) {
                                        String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
                                        //Uri uri = Uri.parse("http://41.217.232.48:4080/fetch_documents/reseller_reports/640aa381-6a30-4af3-919f-71524d533086/sim_sales_report_2020-02-01.csv");

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                        //DownloadManager.Request request = new DownloadManager.Request(uri);
                                        //request.setTitle("Report Downloading");
                                        request.setDescription("Downloaded...");
                                        request.setDestinationInExternalFilesDir(SelectReportActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        downloadId = downloadManager.enqueue(request);
                                    } else {
                                        MyToast.makeMyToast(activity, "EMPTY DATA", Toast.LENGTH_SHORT);
                                    }
                                    MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);

                                } else {
                                    MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                                }
                            }else if(etopupReport.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity, "Downloading..."+etopupReport.status, Toast.LENGTH_SHORT);
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            MyToast.makeMyToast(activity, error+""+status, Toast.LENGTH_SHORT);
                            BugReport.postBugReport(activity, Constants.emailId,"status"+status+error,"");

                        }
                    });
                }catch (Exception io){

                }

            }

            private void getEtopupSalesReports() {
                RestServiceHandler restServiceHandler = new RestServiceHandler();
                final Boolean generateReport = true;

                try {
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
                    restServiceHandler.getEtopupSalesReports(UserSession.getResellerId(activity),startDate.getText().toString(),endDate.getText().toString(),generateReport,new RestServiceHandler.Callback() {
                        @Override
                        public void success(DataModel.DataType type, List<DataModel> data) {
                            UserLogin etopupReport = (UserLogin) data.get(0);
                            if(etopupReport.status.equals("success")) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                String fileName = "etopup_sales_report_"+ endDate.getText().toString()+".csv";
                                if (etopupReport.reportStatus.equals("success")) {
                                    if (etopupReport.docPath != null) {
                                        String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
                                        //Uri uri = Uri.parse("http://41.217.232.48:4080/fetch_documents/reseller_reports/640aa381-6a30-4af3-919f-71524d533086/sim_sales_report_2020-02-01.csv");

                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                        //DownloadManager.Request request = new DownloadManager.Request(uri);
                                        //request.setTitle("Report Downloading");
                                        request.setDescription("Downloaded...");
                                        request.setDestinationInExternalFilesDir(SelectReportActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                        downloadId = downloadManager.enqueue(request);
                                    } else {
                                        MyToast.makeMyToast(activity, "EMPTY DATA", Toast.LENGTH_SHORT);
                                    }
                                    MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);

                                } else {
                                    MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                                }
                            }else if(etopupReport.status.equals("INVALID_SESSION")){
                                ReDirectToParentActivity.callLoginActivity(activity);
                            }else{
                                MyToast.makeMyToast(activity, "Downloading..."+etopupReport.status, Toast.LENGTH_SHORT);
                            }

                        }

                        @Override
                        public void failure(RestServiceHandler.ErrorCode error, String status) {
                            MyToast.makeMyToast(activity, error+""+status, Toast.LENGTH_SHORT);
                            BugReport.postBugReport(activity, Constants.emailId,"status"+status+error,"");

                        }
                    });
                }catch (Exception io){

                }

            }
        });
    }

    private int getDownloadStatus(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            return status;
        }
        return DownloadManager.ERROR_UNKNOWN;
    }




    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.filter_spinner:

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Reseller E-topup Reports")) {


                } else if (selectedItem.equals("Sim Usage Report")) {


                }
                break;
        }
    }

    private void getResellerReports() {
        RestServiceHandler restServiceHandler = new RestServiceHandler();
        final Boolean generateReport = true;
        try {
            progressDialog = ProgressDialogUtil.startProgressDialog(activity, "Please Wait.....");
            restServiceHandler.getResellerEtopupReports(UserSession.getResellerId(activity),startDate.getText().toString(),endDate.getText().toString(),generateReport,new RestServiceHandler.Callback() {
                @Override
                public void success(DataModel.DataType type, List<DataModel> data) {
                    UserLogin etopupReport = (UserLogin) data.get(0);
                    if(etopupReport.status.equals("success")) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        if (etopupReport.reportStatus.equals("success")) {
                            String fileName = "etopup_reseller_report_"+ endDate.getText().toString()+".csv";
                            if (etopupReport.docPath != null) {
                                String fileUrl = Constants.serverURL +"fetch_documents"+"/"+"reports"+etopupReport.docPath+".csv";
                                //Uri uri = Uri.parse("http://41.217.232.48:4080/fetch_documents/reseller_reports/640aa381-6a30-4af3-919f-71524d533086/sim_sales_report_2020-02-01.csv");

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                //DownloadManager.Request request = new DownloadManager.Request(uri);
                                //request.setTitle("Report Downloading");
                                request.setDescription("Downloaded...");
                                request.setDestinationInExternalFilesDir(SelectReportActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadId = downloadManager.enqueue(request);
                            } else {
                                MyToast.makeMyToast(activity, "EMPTY DATA", Toast.LENGTH_SHORT);
                            }
                            MyToast.makeMyToast(activity, "", Toast.LENGTH_SHORT);

                        } else {
                            MyToast.makeMyToast(activity,"Unable to fetch details", Toast.LENGTH_SHORT);
                        }
                    }else if(etopupReport.status.equals("INVALID_SESSION")){
                        ReDirectToParentActivity.callLoginActivity(activity);
                    }else{
                        MyToast.makeMyToast(activity, "Downloading..."+etopupReport.status, Toast.LENGTH_SHORT);
                    }

                }

                @Override
                public void failure(RestServiceHandler.ErrorCode error, String status) {
                    MyToast.makeMyToast(activity, error+""+status, Toast.LENGTH_SHORT);
                    BugReport.postBugReport(activity, Constants.emailId,"status"+status+error,"");

                }
            });
        }catch (Exception io){

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
