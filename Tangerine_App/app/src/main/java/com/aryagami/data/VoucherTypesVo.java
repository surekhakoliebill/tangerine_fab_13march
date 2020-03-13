package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import com.aryagami.util.BugReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VoucherTypesVo implements DataModel, Serializable {

    public List<VoucherBatchVo> voucherVos;
    public List<VoucherBatchVo> serviceBundleVouchers;
    public List<VoucherBatchVo> airtimeVouchers;
    public String responseStatus;

    @Override
    public DataType getDataType() {
        return DataModel.DataType.VoucherTypesVo;
    }

    public static class VoucherBatchVo{
        public String status;

        public String planGroupName;

        public String resellerId;

        public Long quantity;

        public String resellerName;

        public Long serialStart;
        public Date createdOn;

        public Long serialEnd;
        public Long voucherBatchId;

        public String voucherType;

        public Long usedQuantity;

        public Long voidedQuantity;

        public Float voucherValue;

        public Boolean isActive;

        public Date activatedOn;

        public Date approvedOn;
        public Long remainingVouchers;
        public List<QuotaVo> quotasVo;
        public Long subEntityId;
        public Long entityId;

        public class QuotaVo {
            public Float quota;
            public String quotaUnits;
            public String planName;
        }
    }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        VoucherTypesVo typesVo = new VoucherTypesVo();
        List<DataModel> resellerVouchersList = new ArrayList<DataModel>();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                typesVo.responseStatus = reader.nextString();
            }else if(name.equals("allVouchers")) {
                List<VoucherBatchVo> batchVoList= new ArrayList<VoucherBatchVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    VoucherBatchVo batchVoychersVo = readRequestsObject(reader);
                    batchVoList.add(batchVoychersVo);
                    reader.endObject();
                }
                typesVo.voucherVos = batchVoList;
                reader.endArray();
            }else if(name.equals("serviceBundleVouchers")) {
                List<VoucherBatchVo> batchVoList= new ArrayList<VoucherBatchVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    VoucherBatchVo typesVo1 = readRequestsObject(reader);
                    batchVoList.add(typesVo1);
                    reader.endObject();
                }
                typesVo.serviceBundleVouchers = batchVoList;
                reader.endArray();
            }else if(name.equals("airtimeVouchers")) {
                List<VoucherBatchVo> batchVoList= new ArrayList<VoucherBatchVo>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    VoucherBatchVo typesVo1 = readRequestsObject(reader);
                    batchVoList.add(typesVo1);
                    reader.endObject();
                }
                typesVo.airtimeVouchers = batchVoList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        resellerVouchersList.add(typesVo);
        reader.endObject();

        return resellerVouchersList;
    }

    private static VoucherBatchVo readRequestsObject(JsonReader reader) {
        VoucherBatchVo vo = new VoucherBatchVo();
        try{
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("planGroupName") && reader.peek() != JsonToken.NULL) {
                    vo.planGroupName = reader.nextString();
                } else if (name.equals("remainingVouchers") && reader.peek() != JsonToken.NULL) {
                    vo.remainingVouchers = Long.valueOf(reader.nextString());
                }else if (name.equals("resellerId") && reader.peek() != JsonToken.NULL) {
                    vo.resellerId= reader.nextString();
                }else if (name.equals("resellerName") && reader.peek() != JsonToken.NULL) {
                    vo.resellerName = reader.nextString();
                }else if (name.equals("voucherBatchId") && reader.peek() != JsonToken.NULL) {
                    vo.voucherBatchId = Long.valueOf(reader.nextString());
                }else if (name.equals("voucherValue") && reader.peek() != JsonToken.NULL) {
                    vo.voucherValue =  Float.valueOf(reader.nextString());
                }else if (name.equals("entityId") && reader.peek() != JsonToken.NULL) {
                    vo.entityId = Long.valueOf(reader.nextString());
                }else if (name.equals("subEntityId") && reader.peek() != JsonToken.NULL) {
                    vo.subEntityId = Long.valueOf(reader.nextString());
                }else{
                    reader.skipValue();
                }
            }
        }catch (Exception e){
            BugReport.postBugReportFromREST(Constants.emailId,"Cause: "+e.getCause()+"Message: "+e.getMessage(),"");
        }

        return vo;
    }




}
