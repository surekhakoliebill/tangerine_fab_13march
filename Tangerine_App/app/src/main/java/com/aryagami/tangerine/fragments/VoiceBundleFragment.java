package com.aryagami.tangerine.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aryagami.R;
import com.aryagami.data.DataModel;
import com.aryagami.data.PlanGroup;
import com.aryagami.data.RegistrationData;
import com.aryagami.restapis.RestServiceHandler;
import com.aryagami.util.MyToast;
import com.aryagami.util.ProgressDialogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VoiceBundleFragment extends Fragment {

    ListView bundlerechargelist;
    ProgressDialog progressDialog;
    PlanGroup[] planGroups,planGroups1;
    List<PlanGroup> planGroupsList = new ArrayList<>();

    public VoiceBundleFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bundle_plans, container, false);
        bundlerechargelist = (ListView) view.findViewById(R.id.bundleplansList);
        showBundlePlans();
        return view;
    }

   /* private void showBundlePlans() {
        if( RegistrationData.getBundleList() != null){
            int count = 0 ;

            progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(), "Please wait...");

            for(PlanGroup plan : RegistrationData.getBundleList() )
            {
                if(plan.groupType.equals("Voice Bundles") && plan.enableVoucherChannel) {
                    planGroupsList.add(plan);
                   // planGroups1[count] = plan;
                  //  count++;
                }
            }
            if (planGroupsList.size() != 0){
                planGroups1 = new PlanGroup[planGroupsList.size()];
                planGroupsList.toArray(planGroups1);

                ArrayAdapter adapter = new BundlePlansListingAdapter(getContext(), planGroups1);
                bundlerechargelist.setAdapter(adapter);
                ProgressDialogUtil.stopProgressDialog(progressDialog);
            }else {
                ProgressDialogUtil.stopProgressDialog(progressDialog);
                MyToast.makeMyToast(getActivity(), "Data Empty...", Toast.LENGTH_SHORT);
            }

        }else{
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(), "Please wait, Fetching plans details...");
                serviceHandler.getPlanGroup(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {

                        planGroups = new PlanGroup[data.size()];
                        planGroups1 = new PlanGroup[data.size()];
                        List<PlanGroup> planGroupList =new ArrayList<>();
                        if (data.size() != 0){
                            data.toArray(planGroups);
                            for (PlanGroup pg : planGroups){
                                planGroupList.add(pg);
                            }
                            RegistrationData.setBundleList(planGroupList);
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                        }else {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(getActivity(), "data empty", Toast.LENGTH_SHORT);

                        }

                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        // MyToast.makeMyToast(activity, "Error" + error + " Status" + status, Toast.LENGTH_SHORT);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void showBundlePlans() {
        if( RegistrationData.getBundleList() != null && RegistrationData.getBundleList().size() !=0){
            setPlanDetailsInList(RegistrationData.getBundleList());

        }else{
            RestServiceHandler serviceHandler = new RestServiceHandler();
            try {
                progressDialog = ProgressDialogUtil.startProgressDialog(getActivity(), "Please wait, Fetching plans details...");
                serviceHandler.getPlanGroup(new RestServiceHandler.Callback() {
                    @Override
                    public void success(DataModel.DataType type, List<DataModel> data) {

                        planGroups = new PlanGroup[data.size()];
                        planGroups1 = new PlanGroup[data.size()];
                        List<PlanGroup> planGroupList =new ArrayList<>();
                        if (data.size() != 0){
                            data.toArray(planGroups);
                            for (PlanGroup pg : planGroups){
                                planGroupList.add(pg);
                            }
                            RegistrationData.setBundleList(planGroupList);
                            setPlanDetailsInList(planGroupList);
                            ProgressDialogUtil.stopProgressDialog(progressDialog);

                        }else {
                            ProgressDialogUtil.stopProgressDialog(progressDialog);
                            MyToast.makeMyToast(getActivity(), "data empty", Toast.LENGTH_SHORT);

                        }

                    }

                    @Override
                    public void failure(RestServiceHandler.ErrorCode error, String status) {
                        ProgressDialogUtil.stopProgressDialog(progressDialog);
                        // MyToast.makeMyToast(activity, "Error" + error + " Status" + status, Toast.LENGTH_SHORT);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPlanDetailsInList(List<PlanGroup> planGroups) {
        for(PlanGroup plan : planGroups)
        {
            if(plan.groupType.equals("Voice Bundles") && plan.enableVoucherChannel) {
                planGroupsList.add(plan);
                // planGroups1[count] = plan;
                //  count++;
            }
        }
        if (planGroupsList.size() != 0){
            planGroups1 = new PlanGroup[planGroupsList.size()];
            planGroupsList.toArray(planGroups1);

            ArrayAdapter adapter = new BundlePlansListingAdapter(getContext(), planGroups1);
            bundlerechargelist.setAdapter(adapter);

        }else {

            MyToast.makeMyToast(getActivity(), "Voice plan data empty...", Toast.LENGTH_SHORT);
        }
    }


}
