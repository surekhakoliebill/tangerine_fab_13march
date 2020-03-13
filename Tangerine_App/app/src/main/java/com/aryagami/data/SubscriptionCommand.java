package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.aryagami.data.Subscription.readSubscription;
import static com.aryagami.data.UserRegistration.readUserInfoObject;

public class SubscriptionCommand implements DataModel, Serializable {

    public String subscriptionId;

    public String userId;

    public Long billingCycleId;

    public String servedMSISDN;

    public Long planGroupId;

    public Boolean active;

    public String deviceInfo;

    public String deviceType;

    public Float discount;

    public Boolean isFreeRecharge;

    public String freeRechargeUntil;

    public String billingFrequency;

    public Boolean skipInventoryCheck;

    public String subscriptionInfo;

    public String resellerId;

    public Long periodicPlanId;

    public String fixedIp;

    public String macAddress;

    public String accessRoute;

    public Boolean isAccessRoute;

    public Boolean isSingleIp;

    public Boolean isAutoSelectIp;
    public String status;
    public String subscriberName;
    public Float subscriberBalance;
    public String statusReason;
    public String serviceBundleSerialNumber;
    public UserRegistration userInfo;
    public Subscription subscription;
    public List<SubscriptionPlanAddon> subPlanAddonMappings;
    public float airtimeValue;
    public String discountType;
    public List<String> bundleList;
    public String basePlanName;
    public Float inventoryPrice;


    public static List<DataModel> parseSubscriptionJSONInput(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> subscriptionList = new ArrayList<DataModel>();
        reader.beginObject();
        SubscriptionCommand subscriptionCommand = new SubscriptionCommand();
        String status = null;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                subscriptionCommand.statusReason = reader.nextString().toString();
            } else if (name.equals("subscription") && reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                subscriptionCommand = readSubscriptionObject(reader);
                reader.endObject();

            } else if (name.equals("planGroupId") && reader.peek() != JsonToken.NULL) {
                subscriptionCommand.planGroupId = Long.parseLong(reader.nextString().toString());

            } else if (name.equals("serviceBundleSerialNumber") && reader.peek() != JsonToken.NULL) {

                subscriptionCommand.serviceBundleSerialNumber = reader.nextString();

            } else if (name.equals("airtimeValue") && reader.peek() != JsonToken.NULL) {

                subscriptionCommand.airtimeValue = Float.parseFloat(reader.nextString());

            }else if (name.equals("basePlanName") && reader.peek() != JsonToken.NULL) {

                subscriptionCommand.basePlanName =reader.nextString();

            }else if (name.equals("inventoryPrice") && reader.peek() != JsonToken.NULL) {

                subscriptionCommand.inventoryPrice = Float.parseFloat(reader.nextString());

            }else if (name.equals("bundleComponents") && reader.peek() != JsonToken.NULL) {
                List<String> bundleComponentList = new ArrayList<String>();
                reader.beginArray();
                while(reader.hasNext()){
                    try{
                        if(reader.peek() == JsonToken.STRING.NULL){
                            return null;
                        }else{
                            String subCat = reader.nextString();
                            bundleComponentList.add(subCat);
                        }

                    }catch (JsonIOException e){
                        return  null;
                    }catch (JsonParseException e){
                        return null;
                    }catch (IOException e){
                        return null;
                    }
                }
                reader.endArray();
                subscriptionCommand.bundleList = bundleComponentList;

            } else {
                reader.skipValue();
            }
            subscriptionList.add(subscriptionCommand);
        }
        reader.endObject();
        return subscriptionList;
    }

    public static List<DataModel> parseSubscriptionJSON(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> subscriptionList = new ArrayList<DataModel>();
        reader.beginObject();
        SubscriptionCommand subscriptionCommand = new SubscriptionCommand();
        String status = null;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                subscriptionCommand.statusReason = reader.nextString().toString();
            } else if (name.equals("subscription") && reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                Subscription subscription1 = readSubscription(reader);
                subscriptionCommand.subscription = subscription1;

                reader.endObject();
            } else if (name.equals("subscriptionUser") && reader.peek() != JsonToken.NULL) {

                reader.beginObject();
                UserRegistration userRegistration = readUserInfoObject(reader);
                subscriptionCommand.userInfo = userRegistration;
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        subscriptionList.add(subscriptionCommand);
        reader.endObject();
        return subscriptionList;
    }

    ;

    private static SubscriptionCommand readSubscriptionObject(JsonReader reader) throws IOException {
        SubscriptionCommand subCommand = new SubscriptionCommand();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                subCommand.userId = reader.nextString();
            } else if (name.equals("resellerId") && reader.peek() != JsonToken.NULL) {
                subCommand.resellerId = reader.nextString();
            } else if (name.equals("active") && reader.peek() != JsonToken.NULL) {
                subCommand.active = reader.nextBoolean();
            } else if (name.equals("servedMSISDN") && reader.peek() != JsonToken.NULL) {
                subCommand.servedMSISDN = reader.nextString();
            } else {
                reader.skipValue();
            }

        }

        return subCommand;
    }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> userInfoList = new ArrayList<DataModel>();

        reader.beginObject();
        String status = null;
        SubscriptionCommand command = new SubscriptionCommand();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                command.status = reader.nextString();
            } else if (name.equals("subscriberName")) {
                command.subscriberName = reader.nextString();
            } else if (name.equals("subscriberBalance")) {
                command.subscriberBalance = Float.parseFloat(reader.nextString());

            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        userInfoList.add(command);
        return userInfoList;
    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.SubscriptionCommand;
    }

    public static class SubscriptionPlanAddon implements Serializable {

        public String planId;
        public List<String> planAddonIds;
    }
}
