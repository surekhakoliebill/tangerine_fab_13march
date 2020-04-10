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
import java.util.List;

public class ActivateCommand implements DataModel, Serializable {

    public String entityName;
    public String activationId;
    public String reason;
    public String servedMSISDN;
    // For ResellerRechargeReport for GET & Cancel Request

    public String entityType;
    public String entityId;

    public String transactionId;
    public String planGroupName;
    public String planGroupId;
    public Float airtimeAmount;
    public String txnDate;
    public String channel;
    public String msisdn;
    public String userName;
    public String status;
    public List<ActivateCommand> resellerRechargesList;

    public static List<DataModel> parseResellerRechargesJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> resellerRechargesList = new ArrayList<DataModel>();

        ActivateCommand requestVo = new ActivateCommand();

        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                requestVo.status = reader.nextString();
            }else if (name.equals("salesInfo") && reader.peek() != JsonToken.NULL) {
                List<ActivateCommand> traList = new ArrayList<ActivateCommand>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ActivateCommand requestVo1 = readRequestObject(reader);
                    traList.add(requestVo1);
                    reader.endObject();
                }
                requestVo.resellerRechargesList = traList;
                reader.endArray();

            }else{
                reader.skipValue();
            }
        }
        resellerRechargesList.add(requestVo);
        reader.endObject();

        return resellerRechargesList;
    }

    private static ActivateCommand readRequestObject(JsonReader reader) throws IOException {
        ActivateCommand request = new ActivateCommand();
        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("entityName")&& reader.peek() != JsonToken.NULL) {
                request.entityName = reader.nextString();
            }else if (name.equals("transactionId")&& reader.peek() != JsonToken.NULL) {
                request.transactionId = reader.nextString();
            }else if (name.equals("txnDate")&& reader.peek() != JsonToken.NULL) {
                request.txnDate = reader.nextString();
            }else if (name.equals("airtimeAmount")&& reader.peek() != JsonToken.NULL) {
                request.airtimeAmount = Float.parseFloat(reader.nextString());
            }else if (name.equals("msisdn")&& reader.peek() != JsonToken.NULL) {
                request.msisdn = reader.nextString();
            }else if (name.equals("entityType")&& reader.peek() != JsonToken.NULL) {
                request.entityType = reader.nextString();
            }else if (name.equals("planGroupName")&& reader.peek() != JsonToken.NULL) {
                request.planGroupName = reader.nextString();
            }else {
                reader.skipValue();
            }
        }

        return request;
    }


    public String getActivateJSON() throws Exception {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("entityName").value(entityName);
        jwriter.name("activationId").value(activationId);
        jwriter.name("reason").value(reason);
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    public String getCancelRechargeRequestJSON() throws Exception {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("entityId").value(entityId);
        jwriter.name("entityType").value(entityType);
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.ActivateCommand;
    }
}
