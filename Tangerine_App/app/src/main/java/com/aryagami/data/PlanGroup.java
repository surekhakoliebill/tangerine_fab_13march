package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aryagami on 1/7/17.
 */

public class PlanGroup implements DataModel, Serializable {

    public String planGroupId;
    public String groupName;
    public String planName;
    public List<String> gplanIds;
    public Boolean isActive;
    public String groupType;
    public String planType;
    public String planGroupCode;
    public Boolean adminPlans;
    public Boolean topupPlans;
    public Float price;
    public Long monthlyPlanGroupSubscriptions;
    public Long monthlyPlanGroupInvoices;
    public Long monthlyPlanGroupRecharges;
    public String currencyType;
    public Float planPrice;
    public Float planQuota;
    public String validity;
    public String planDescription;
    public Float planSpeed;
    public String planToken;
    public Boolean singUps;
    public Float plansAdditionalPrice;
    public Float plansSetupPrice;
    public String status;
    public Float setupPrice;
    public List<PlanBundleOptionVo> bundleOptions;
    public Date createdOn;
    public Boolean isFallBackBundle;
    public String quotaUnits;
    public Boolean enableUssdChannel;
    public Boolean enableSmscChannel;
    public Boolean enableWebportalChannel;
    public Boolean enableVoucherChannel;
    public String ussdRechargeCode;
    public String smsRechargeCode;
    public Boolean isAutoRenewal;
    public String renewalCycle;
    public Integer bundleRechargeLimit;
    //public List<PlanVo> pgPlans;
    public Boolean enableMobileMoneyChannel;
    public Float activationMinBalance;
    public Float roamingMinBalance;
    public Long autoRenewalPlanGroup;
    public String autoRenewalPgName;
    public Boolean isPrimaryBundle = true;
    public Long ussdSequenceId;
    public Boolean enableStaffRechargeChannel;
    //public List<PlanGroupAddonMappingVo> childPgMappings;



    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> PlanGroupList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;
        PlanGroup planGroup1 = new PlanGroup();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                planGroup1.status = reader.nextString();
            }else if(name.equals("allPlanGroups"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    PlanGroup planGroup = readPlanGroupObject(reader);
                    PlanGroupList.add(planGroup);
                    reader.endObject();
                }
                reader.endArray();

            }else if(name.equals("planGroupById"))
            {
                    reader.beginObject();
                      planGroup1 = readPlanGroupObject(reader);
                    PlanGroupList.add(planGroup1);
                    reader.endObject();
            }else{
                reader.skipValue();
            }

        }
        reader.endObject();

        return PlanGroupList;
    }

    public static PlanGroup readPlanGroupObject(JsonReader reader) {

        PlanGroup planGroup = new PlanGroup();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("planGroupId") && reader.peek() != JsonToken.NULL) {
                    planGroup.planGroupId = reader.nextString();
                } else if (name.equals("groupName") && reader.peek() != JsonToken.NULL) {
                    planGroup.groupName = reader.nextString();
                } else if (name.equals("planName") && reader.peek() != JsonToken.NULL) {
                    planGroup.planName = reader.nextString();
                } else if (name.equals("isActive") && reader.peek() != JsonToken.NULL) {
                    planGroup.isActive = reader.nextBoolean();
                } else if (name.equals("groupType") && reader.peek() != JsonToken.NULL) {
                    planGroup.groupType = reader.nextString();
                }else if(name.equals("gplanIds") && reader.peek() != JsonToken.NULL)
                {
                    planGroup.gplanIds = new ArrayList<String>();
                    reader.beginArray();
                    while(reader.hasNext())
                    {
                        planGroup.gplanIds.add(reader.nextString());
                    }reader.endArray();

                }
                else if (name.equals("planType") && reader.peek() != JsonToken.NULL) {
                    planGroup.planType = reader.nextString();
                } else if (name.equals("planGroupCode") && reader.peek() != JsonToken.NULL) {
                    planGroup.planGroupCode = reader.nextString();
                }else if(name.equals("adminPlans")&& reader.peek() != JsonToken.NULL) {
                    planGroup.adminPlans = reader.nextBoolean();
                }else if(name.equals("topupPlans")&& reader.peek() != JsonToken.NULL) {
                    planGroup.topupPlans = reader.nextBoolean();
                }else if(name.equals("price")&& reader.peek() != JsonToken.NULL) {
                    planGroup.price = (float)reader.nextDouble();
                }else if(name.equals("monthlyPlanGroupSubscriptions")&& reader.peek() != JsonToken.NULL) {
                    planGroup.monthlyPlanGroupSubscriptions = reader.nextLong();
                }else if(name.equals("monthlyPlanGroupInvoices")&& reader.peek() != JsonToken.NULL) {
                    planGroup.monthlyPlanGroupInvoices = reader.nextLong();
                }else if(name.equals("monthlyPlanGroupRecharges")&& reader.peek() != JsonToken.NULL) {
                    planGroup.monthlyPlanGroupRecharges = reader.nextLong();
                }else if(name.equals("currencyType")&& reader.peek() != JsonToken.NULL) {
                    planGroup.currencyType = reader.nextString();
                }else if(name.equals("planPrice")&& reader.peek() != JsonToken.NULL) {
                    planGroup.planPrice = (float)reader.nextDouble();
                }else if(name.equals("planQuota")&& reader.peek() != JsonToken.NULL) {
                    planGroup.planQuota = (float)reader.nextDouble();
                }else if(name.equals("planDescription")&& reader.peek() != JsonToken.NULL) {
                    planGroup.planDescription = reader.nextString();
                }else if(name.equals("planSpeed")&& reader.peek() != JsonToken.NULL) {
                    planGroup.planSpeed = (float)reader.nextDouble();
                }else if(name.equals("planToken")&& reader.peek() != JsonToken.NULL) {
                    planGroup.planToken = reader.nextString();
                }else if(name.equals("plansAdditionalPrice")&& reader.peek() != JsonToken.NULL) {
                    planGroup.plansAdditionalPrice = Float.parseFloat(reader.nextString());
                }else if(name.equals("planToken")&& reader.peek() != JsonToken.NULL) {
                    planGroup.plansSetupPrice = Float.parseFloat(reader.nextString());
                }else if(name.equals("setupPrice")&& reader.peek() != JsonToken.NULL) {
                    planGroup.setupPrice = Float.parseFloat(reader.nextString());
                }else if(name.equals("enableUssdChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableUssdChannel = reader.nextBoolean();
                }else if(name.equals("isFallBackBundle")&& reader.peek() != JsonToken.NULL) {
                    planGroup.isFallBackBundle = reader.nextBoolean();
                }else if(name.equals("enableSmscChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableSmscChannel = reader.nextBoolean();
                }else if(name.equals("enableWebportalChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableWebportalChannel = reader.nextBoolean();
                }else if(name.equals("enableVoucherChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableVoucherChannel = reader.nextBoolean();
                }else if(name.equals("isAutoRenewal")&& reader.peek() != JsonToken.NULL) {
                    planGroup.isAutoRenewal = reader.nextBoolean();
                }else if(name.equals("enableMobileMoneyChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableMobileMoneyChannel = reader.nextBoolean();
                }else if(name.equals("isPrimaryBundle")&& reader.peek() != JsonToken.NULL) {
                    planGroup.isPrimaryBundle = reader.nextBoolean();
                }else if(name.equals("enableStaffRechargeChannel")&& reader.peek() != JsonToken.NULL) {
                    planGroup.enableStaffRechargeChannel = reader.nextBoolean();
                }else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planGroup;
    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.PlanGroup;
    }

    public static class PlanBundleOptionVo implements Serializable {
        public String bundleOptionId;
        public List<String> planIds;
        public List<String> planNames;
        public Float setupPrice;
        public Float price;
    }

}
