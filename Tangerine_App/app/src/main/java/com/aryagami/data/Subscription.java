package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 7/12/17.
 */

public class Subscription implements Serializable, DataModel {
    public Long planGroupId;

    public String subscriptionId;

    public String planType;

    public String userName;

    public String fullName;

    public String servedMSISDN;

    public String billingCycleName;

    public String createdDate;

    public String resellerName;

    public String activatedDate;

    public Boolean isActive;

    public String billingDate;

    public String groupName;

    public String deviceInfo;

    public String billingFrequency;

    public Long billingCycleId;

    public Long activeSpeed;

    public Float discount;

    public Boolean isFreeRecharge;

    public String freeRechargeUntil;

    public String invoiceId;

    public String subscriptionInfo;

    public Float price;

    public String surname;

    public String otherNames;

    private Long periodicPlanId;

    private String periodicPlanName;

    private String fixedIp;

    private String discountType;

    public String lastActiveIpaddress;

    public String lastUpdateReason;

    private String userId;

    private String activationState;

    public static Subscription readSubscription(JsonReader reader) throws IOException {
        Subscription subscription = new Subscription();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("invoiceId") && reader.peek() != JsonToken.NULL) {
                subscription.invoiceId = reader.nextString();
            }else if (name.equals("userName") && reader.peek() != JsonToken.NULL) {
                subscription.userName = reader.nextString();
            } else if (name.equals("servedMSISDN") && reader.peek() != JsonToken.NULL) {
                subscription.servedMSISDN = reader.nextString();
            } else if (name.equals("planGroupId") && reader.peek() != JsonToken.NULL) {
                subscription.planGroupId = reader.nextLong();
            } else if (name.equals("subscriptionId") && reader.peek() != JsonToken.NULL) {
                subscription.subscriptionId = reader.nextString();
            } else if (name.equals("planType") && reader.peek() != JsonToken.NULL) {
                subscription.planType = reader.nextString();
            } else if (name.equals("fullName") && reader.peek() != JsonToken.NULL) {
                subscription.fullName = reader.nextString();
            } else if (name.equals("billingCycleName") && reader.peek() != JsonToken.NULL) {
                subscription.billingCycleName = reader.nextString();
            } else if (name.equals("createdDate") && reader.peek() != JsonToken.NULL) {
                subscription.createdDate = reader.nextString();
            } else if (name.equals("resellerName") && reader.peek() != JsonToken.NULL) {
                subscription.resellerName = reader.nextString();
            } else if (name.equals("activatedDate") && reader.peek() != JsonToken.NULL) {
                subscription.activatedDate = reader.nextString();
            } else if (name.equals("billingDate") && reader.peek() != JsonToken.NULL) {
                subscription.billingDate = reader.nextString();
            } else if (name.equals("groupName") && reader.peek() != JsonToken.NULL) {
                subscription.groupName = reader.nextString();
            } else if (name.equals("deviceInfo") && reader.peek() != JsonToken.NULL) {
                subscription.deviceInfo = reader.nextString();
            } else if (name.equals("billingFrequency") && reader.peek() != JsonToken.NULL) {
                subscription.billingFrequency = reader.nextString();
            } else if (name.equals("freeRechargeUntil") && reader.peek() != JsonToken.NULL) {
                subscription.freeRechargeUntil = reader.nextString();
            }else if (name.equals("otherNames") && reader.peek() != JsonToken.NULL) {
                subscription.otherNames = reader.nextString();
            }else if (name.equals("periodicPlanName") && reader.peek() != JsonToken.NULL) {
                subscription.periodicPlanName = reader.nextString();
            }else if (name.equals("fixedIp") && reader.peek() != JsonToken.NULL) {
                subscription.fixedIp = reader.nextString();
            }else if (name.equals("discountType") && reader.peek() != JsonToken.NULL) {
                subscription.discountType = reader.nextString();
            }else if (name.equals("lastActiveIpaddress") && reader.peek() != JsonToken.NULL) {
                subscription.lastActiveIpaddress = reader.nextString();
            }else if (name.equals("lastUpdateReason") && reader.peek() != JsonToken.NULL) {
                subscription.lastUpdateReason = reader.nextString();
            }else if (name.equals("subscriptionInfo") && reader.peek() != JsonToken.NULL) {
                subscription.subscriptionInfo = reader.nextString();
            }else if (name.equals("periodicPlanId") && reader.peek() != JsonToken.NULL) {
                subscription.periodicPlanId = Long.parseLong(reader.nextString());
            }else if (name.equals("isActive") && reader.peek() != JsonToken.NULL) {
                subscription.isActive = reader.nextBoolean();
            }else if (name.equals("billingCycleId") && reader.peek() != JsonToken.NULL) {
                subscription.billingCycleId = Long.parseLong(reader.nextString());
            }else if (name.equals("activeSpeed") && reader.peek() != JsonToken.NULL) {
                subscription.activeSpeed = Long.parseLong(reader.nextString());
            }else if (name.equals("discount") && reader.peek() != JsonToken.NULL) {
                subscription.discount = Float.parseFloat(reader.nextString());
            }else if (name.equals("periodicPlanId") && reader.peek() != JsonToken.NULL) {
                subscription.periodicPlanId = Long.parseLong(reader.nextString());
            }else{
                reader.skipValue();
            }
        }

        return  subscription;
    }

    public static List<DataModel> getSubscriptionArray(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> subscriptionList = new ArrayList<DataModel>();
        reader.beginObject();
        String status = null;
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                status = reader.nextString();
            }else if(name.equals("subscriptionByuserId"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    Subscription subscription = readSubscription(reader);
                    subscriptionList.add(subscription);
                    reader.endObject();

                }
                reader.endArray();
            }else if(name.equals("subscriptionByResellerId"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    Subscription subscription = readSubscription(reader);
                    subscriptionList.add(subscription);
                    reader.endObject();

                }
                reader.endArray();
            }

        }
        reader.endObject();
        return subscriptionList;
    }

    @Override
    public DataModel.DataType getDataType() {
        return DataModel.DataType.Subscription;
    }
}

