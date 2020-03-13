package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResellerRequestVo implements Serializable, DataModel {

    public String requestId;

    public String requestInfo;

    public String resellerName;

    public String resellerId;
    public String creationDate;

    public String verifiedBy;

    public String verifiedDate;

    public String reason;

    public String approvedBy;

    public String approvedDate;

    public Float currentCredits;

    public Float totalPrice;

    public String paymentId;

    public String invoiceId;

    public String proformaInvoiceId;

    public List<ResellerRequestEntityMapperVo> resellerRequestEntities;

    public String invoiceNumber;

    public Long doNumber;

    public String accountNumber;

    public String resellerAddress;

    public String requestType;

    public Float discountPercent;

    public Float discountAmount;

    public String transactionId;

    public Float commissionAmount;

    public Float commissionPercent;

    public String walletId;

    public Boolean cancelEligible = false;

    public List<ResellerRequestVo> vouchersRequestsList;
    public List<ResellerRequestVo> stockRequestsList;
    public String resellerRequestId;
    public String status;



    @Override
    public DataType getDataType() {
        return DataType.ResellerRequestVo;
    }
    public static List<DataModel> parseVouchersRequestsJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> availableList = new ArrayList<DataModel>();

        ResellerRequestVo requestVo = new ResellerRequestVo();

        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                requestVo.status = reader.nextString();
            }else if (name.equals("resellerRequest") && reader.peek() != JsonToken.NULL) {
                List<ResellerRequestVo> traList = new ArrayList<ResellerRequestVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerRequestVo requestVo1 = readRequestsObject(reader);
                    traList.add(requestVo1);
                    reader.endObject();
                }
                requestVo.vouchersRequestsList = traList;
                reader.endArray();

            }else if (name.equals("unverifiedResellerRequests") && reader.peek() != JsonToken.NULL) {
                List<ResellerRequestVo> traList = new ArrayList<ResellerRequestVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerRequestVo requestVo1 = readRequestsObject(reader);
                    traList.add(requestVo1);
                    reader.endObject();
                }
                requestVo.stockRequestsList = traList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        availableList.add(requestVo);
        reader.endObject();

        return availableList;
    }

    public static ResellerRequestVo readRequestsObject(JsonReader reader) throws IOException {
        ResellerRequestVo request = new ResellerRequestVo();

        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("requestId")&& reader.peek() != JsonToken.NULL) {
                request.requestId = reader.nextString();
            }else if (name.equals("status")&& reader.peek() != JsonToken.NULL) {
                request.status = reader.nextString();
            } else if (name.equals("totalPrice")&& reader.peek() != JsonToken.NULL) {
                request.totalPrice = Float.parseFloat(reader.nextString());
            }else if (name.equals("creationDate")&& reader.peek() != JsonToken.NULL) {
                request.creationDate = reader.nextString();
            }else if (name.equals("requestType")&& reader.peek() != JsonToken.NULL) {
                request.requestType = reader.nextString();
            }else if (name.equals("resellerId")&& reader.peek() != JsonToken.NULL) {
                request.resellerId = reader.nextString();
            }else if (name.equals("resellerName")&& reader.peek() != JsonToken.NULL) {
                request.resellerName = reader.nextString();
            }else if (name.equals("resellerRequestEntities")&& reader.peek() != JsonToken.NULL) {

                reader.beginArray();
                List<ResellerRequestEntityMapperVo> traList = new ArrayList<ResellerRequestEntityMapperVo>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerRequestEntityMapperVo command = readEntityObject(reader);
                    traList.add(command);
                    reader.endObject();
                }
                request.resellerRequestEntities  = traList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        return request;
    }

    private static ResellerRequestEntityMapperVo readEntityObject(JsonReader reader) throws IOException {
        ResellerRequestEntityMapperVo vo = new ResellerRequestEntityMapperVo();
        while (reader.hasNext()){
            String name= reader.nextName();
            if (name.equals("entityType") && reader.peek() != JsonToken.NULL) {
                vo.entityType = reader.nextString();
            } else if (name.equals("quantity") && reader.peek() != JsonToken.NULL) {
                vo.quantity = Long.parseLong(reader.nextString());
            }else if (name.equals("price") && reader.peek() != JsonToken.NULL) {
                vo.price = Float.parseFloat(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        return vo;
    }

    public static ResellerRequestEntityMapperVo readRequestEntityObject(JsonReader reader)throws IOException {
         ResellerRequestEntityMapperVo vo = new ResellerRequestEntityMapperVo();
        while(reader.hasNext()){
            if(reader.nextName() != null) {
                String name = reader.nextName();
                if (name.equals("entityType") && reader.peek() != JsonToken.NULL) {
                    vo.entityType = reader.nextString();
                } else if (name.equals("quantity") && reader.peek() != JsonToken.NULL) {
                    vo.quantity = Long.parseLong(reader.nextString());
                }else if (name.equals("price") && reader.peek() != JsonToken.NULL) {
                    vo.price = Float.parseFloat(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
        }

       return vo;
    }

    public String postApproveVoucherRequestJson() throws IOException {


        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("resellerRequestId").value(resellerRequestId);
        jwriter.name("status").value(status);

        jwriter.endObject();

        String json = swriter.toString();
        return json;

    }

    public static class ResellerRequestEntityMapperVo implements Serializable {
        public String reqMapperId;

        public String entityType;

        public String entityId;

        public String entityName;

        public Long quantity;

        public Long serialStart;

        public Long serialEnd;

        public String entitySubId;

        public Boolean isFulfilled;

        public Date fulfilledOn;

        public String fulfillError;

        public Float price;

        public Float airTimeVoucherValue;

        public Float airTimeVoucherDiscountPercent = 0f;

        public String lotId;

        public String voucherBatchId;

        public String voucherSerialStart;

        public String voucherSerialEnd;

        public Boolean isVouchersCreated;

        public String vouchersCreationError;

        public String voucherBatchStatus;

        public String vouchersCreationRef;

        public String additionalStarterBundlePlan;

        public String additionalStarterBundleOption;

        public Float additionalStarterBundleDiscountPercent = 0f;

        public Long blockingReturnCount;

        public Float entityDiscountPercent = 0f;
    }
}
