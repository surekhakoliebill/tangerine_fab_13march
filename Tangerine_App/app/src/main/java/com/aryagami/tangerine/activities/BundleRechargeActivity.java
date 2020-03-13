package com.aryagami.tangerine.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.Constants;
import com.aryagami.data.DataModel;
import com.aryagami.data.PlanGroup;
import com.aryagami.data.UserLogin;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.tangerine.adapters.ViewPagerAdapter;
import com.aryagami.tangerine.fragments.DataPlansFragment;
import com.aryagami.tangerine.fragments.JuiceUpPlansFragment;
import com.aryagami.tangerine.fragments.SmsPlansFragment;
import com.aryagami.tangerine.fragments.StarterPlansFragment;
import com.aryagami.tangerine.fragments.VoiceBundleFragment;
import com.aryagami.util.BugReport;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;
import com.aryagami.util.ReDirectToParentActivity;

import java.io.IOException;
import java.util.List;

public class BundleRechargeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Activity activity = this;
    ProgressDialog progressDialog;
    PlanGroup[] planGroups,planGroups1;
    VoiceBundleFragment voceBundleFragment;
    DataPlansFragment dataPlansFragment;
    JuiceUpPlansFragment juiceUpPlansFragment;
    StarterPlansFragment starterPlansFragment;
    SmsPlansFragment smsPlansFragment;
    Button findSubscriptionbtn;
    TextView msisdnText;
    TextInputEditText msisdnEditText;
    ImageButton backImageButton;
    String MSISDN = "";
    LinearLayout bundleLinearLayout;

    String[] tabTitle={"Voice Bundles","Data Plans","Juice Up", "Starter Plans", "Sms Plans"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab_without_bundle_icon);
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        setupViewPager(viewPager);
        findSubscriptionbtn = (Button) findViewById(R.id.check_subscription_btn);
        msisdnEditText = (TextInputEditText) findViewById(R.id.serverdMSISDN_eText);
        bundleLinearLayout = (LinearLayout) findViewById(R.id.browse_plan_layout);

        backImageButton = (ImageButton) findViewById(R.id.back_imgbtn);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        findSubscriptionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msisdnEditText.getText().toString().isEmpty() | msisdnEditText.getText().length() != 9) {
                    MyToast.makeMyToast(activity, "Please enter correct MSISDN Number", Toast.LENGTH_SHORT);
                }else {
                    MSISDN = "256" + msisdnEditText.getText().toString().trim();
                    progressDialog = ProgressDialogUtil.startProgressDialog(activity, "please wait......");

                    RestServiceHandler serviceHandler = new RestServiceHandler();
                    try {
                        serviceHandler.checkSubscription(MSISDN, new RestServiceHandler.Callback() {
                            @Override
                            public void success(DataModel.DataType type, List<DataModel> data) {
                                final UserLogin userLogin = (UserLogin) data.get(0);

                                if (userLogin.status.equals("success")) {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                    alertDialog.setCancelable(false);
                                    alertDialog.setIcon(R.drawable.success_icon);
                                    alertDialog.setMessage("Subscription Found!");
                                    alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                    bundleLinearLayout.setVisibility(View.VISIBLE);
                                                }
                                            });
                                    alertDialog.show();

                                } else {
                                    ProgressDialogUtil.stopProgressDialog(progressDialog);
                                    if (userLogin.status.equals("INVALID_SESSION")) {
                                        ReDirectToParentActivity.callLoginActivity(activity);
                                    } else {

                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("STATUS:" + userLogin.status.toString());
                                        alertDialog.setNeutralButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                }
                            }

                            @Override
                            public void failure(RestServiceHandler.ErrorCode error, String status) {
                                ProgressDialogUtil.stopProgressDialog(progressDialog);
                                BugReport.postBugReport(activity, Constants.emailId,"ERROR"+error+"STATUS:"+status,"Activity");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        BugReport.postBugReport(activity, Constants.emailId,"STATUS:"+e.getMessage(),"Activity");
                    }

                }
            }
        });


        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }


    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        voceBundleFragment=new VoiceBundleFragment();
        dataPlansFragment=new DataPlansFragment();
        juiceUpPlansFragment=new JuiceUpPlansFragment();
        starterPlansFragment=new StarterPlansFragment();
        smsPlansFragment=new SmsPlansFragment();
        adapter.addFragment(voceBundleFragment,"Voice Bundles");
        adapter.addFragment(dataPlansFragment,"Data Plans");
        adapter.addFragment(juiceUpPlansFragment,"Juice Up");
        adapter.addFragment(starterPlansFragment, "Starter Plans");
        adapter.addFragment(smsPlansFragment, "Sms Plans");
        viewPager.setAdapter(adapter);
    }

}
