package com.aryagami.tangerine.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aryagami.R;
import com.aryagami.data.RegistrationData;
import com.aryagami.data.Subscription;
import com.aryagami.data.UserRegistration;
import com.aryagami.tangerine.activities.EditUserInformationActivity;

import java.util.ArrayList;
import java.util.List;

public class UsersListingAdapter extends ArrayAdapter {
    UserRegistration[] userRegistrations;
    public Activity activity = null;


    public UsersListingAdapter(Activity activity, UserRegistration[] usersArray) {
        super(activity, R.layout.item_list_for_user,usersArray);
        this.activity = activity;
        this.userRegistrations = usersArray;
    }
    public  void onTrimMemory(int level) {
        System.gc();
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View rowView;

        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_list_for_user, null, true);
        }
        final UserRegistration rowItem = (UserRegistration) getItem(position);

        if(rowItem != null) {
            TextView username = (TextView) rowView.findViewById(R.id.username_value);
            if (rowItem.userName != null) {
                username.setText(rowItem.userName.toString());
            } else {
                username.setText("");
            }

            TextView fullname = (TextView) rowView.findViewById(R.id.fullname_value);
            if (rowItem.fullName != null) {
                fullname.setText( rowItem.fullName.toString());
            } else {
                fullname.setText("");
            }

            TextView email = (TextView) rowView.findViewById(R.id.email_value);

            if (rowItem.email!= null) {
                email.setText(rowItem.email.toString());
            } else {
                email.setText("");
            }

            TextView userGroup = (TextView) rowView.findViewById(R.id.usergroup_value);
            if (rowItem.userGroup != null) {
                userGroup.setText(rowItem.userGroup.toString());
            } else {
                userGroup.setText("");
            }

            TextView status = (TextView) rowView.findViewById(R.id.status_value);

            if(rowItem.isActive!= null){
                if(rowItem.isActive){
                    status.setText("Active");
                    status.setTextColor(Color.BLUE);
                }else{
                    status.setText("Inactive");
                    status.setTextColor(Color.RED);
                }
            }

            TextView surname = (TextView) rowView.findViewById(R.id.surname_value);
            TextView subscriptions = (TextView) rowView.findViewById(R.id.subscriptions_value);

            if(rowItem.surname != null){
                surname.setText(rowItem.surname.toString());
            }else{
                surname.setText("NA");
            }

            if(rowItem.userSubscriptions != null){
                List<String> userSubList = new ArrayList<String>();
               String userSubscriptionNames[];
               String subName = "";
                for(Subscription sub : rowItem.userSubscriptions){
                    subName = subName+ ","+sub.servedMSISDN;
                }

                subscriptions.setText(subName);
            }else{
                subscriptions.setText("NA");
            }

            Button edit = (Button) rowView.findViewById(R.id.edit_button);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity,EditUserInformationActivity.class);
                    RegistrationData.setEditUserProfile(rowItem);
                    activity.startActivity(intent);
                }
            });
        }


        return rowView;
    }
}
