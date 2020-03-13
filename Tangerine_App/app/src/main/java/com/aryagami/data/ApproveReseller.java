package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ApproveReseller implements DataModel {

    public String status;

    public String reason;

    public String resellerVerifyId;

    public String code;

    public String resellerName;

    public String userId;

    public Boolean isActive;

    public String resellerId;

    public Boolean isAggregator;

    public Boolean isDistributor;

    public String aggregatorId;

    public Float creditLimit;

    public Float requestedRechargeCommission;

    public Float requestedPostPaidSignUpCommission;

    public Float requestedInvoiceCommission;

    public Float requestedSignUpCommission;

    // Account Approved
    public Float approvedRechargeCommission;

    public Float approvedPostPaidSignUpCommission;

    public Float approvedSignUpCommission;

    public Float approvedInvoiceCommission;

    public Boolean enableImmediateServiceCutoff;

    public Integer immedidateServiceCutoffDays;

    public String prepaidTemplateId;

    public String postpaidTemplateId;

    public String namType;


    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> approveReseller = new ArrayList<DataModel>();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();
            }else if(name.equals("success"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ApproveReseller approveReseller1 = readRequestsObject(reader);
                    approveReseller.add(approveReseller1);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();

        return approveReseller;
    }


    private static ApproveReseller readRequestsObject(JsonReader reader) throws IOException {
        ApproveReseller approveReseller = new ApproveReseller();

        while(reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("status")&& reader.peek() != JsonToken.NULL) {
                approveReseller.status = reader.nextString();
            } else if(name.equals("reason")&& reader.peek() != JsonToken.NULL) {
                approveReseller.reason = reader.nextString();
            } else if(name.equals("resellerVerifyId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.resellerVerifyId = reader.nextString();
            } else if(name.equals("code")&& reader.peek() != JsonToken.NULL) {
                approveReseller.code = reader.nextString();
            } else if(name.equals("resellerName")&& reader.peek() != JsonToken.NULL) {
                approveReseller.resellerName = reader.nextString();
            }else if(name.equals("userId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.userId = reader.nextString();
            }else if(name.equals("isActive")&& reader.peek() != JsonToken.NULL) {
                approveReseller.isActive = reader.nextBoolean();
            }else if(name.equals("resellerId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.resellerId = reader.nextString();
            }else if(name.equals("isAggregator")&& reader.peek() != JsonToken.NULL) {
                approveReseller.isAggregator = reader.nextBoolean();
            }else if(name.equals("isDistributor")&& reader.peek() != JsonToken.NULL) {
                approveReseller.isDistributor = reader.nextBoolean();
            }else if(name.equals("aggregatorId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.aggregatorId = reader.nextString();
            }else if(name.equals("creditLimit")&& reader.peek() != JsonToken.NULL) {
                approveReseller.creditLimit = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedRechargeCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.requestedRechargeCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedPostPaidSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.requestedPostPaidSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedInvoiceCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.requestedInvoiceCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("requestedSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.requestedSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedRechargeCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.approvedRechargeCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedPostPaidSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.approvedPostPaidSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedSignUpCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.approvedSignUpCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("approvedInvoiceCommission")&& reader.peek() != JsonToken.NULL) {
                approveReseller.approvedInvoiceCommission = Float.parseFloat(reader.nextString());
            }else if(name.equals("enableImmediateServiceCutoff")&& reader.peek() != JsonToken.NULL) {
                approveReseller.enableImmediateServiceCutoff = reader.nextBoolean();
            }else if(name.equals("immedidateServiceCutoffDays")&& reader.peek() != JsonToken.NULL) {
                approveReseller.immedidateServiceCutoffDays = reader.nextInt();
            }else if(name.equals("prepaidTemplateId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.prepaidTemplateId = reader.nextString();
            }else if(name.equals("postpaidTemplateId")&& reader.peek() != JsonToken.NULL) {
                approveReseller.postpaidTemplateId = reader.nextString();
            }else if(name.equals("namType")&& reader.peek() != JsonToken.NULL) {
                approveReseller.namType = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        return approveReseller;

    }


    public String getApproveResellerInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("status").value(status);
        jwriter.name("resellerId").value(resellerId);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.ApproveReseller;
    }
}
