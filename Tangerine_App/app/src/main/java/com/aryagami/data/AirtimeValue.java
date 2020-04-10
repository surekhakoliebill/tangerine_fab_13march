package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AirtimeValue implements DataModel {
    @Override
    public DataType getDataType() {
        return DataType.AirtimeValue;
    }

    Long productId;

    public Float airtimeValue;

    public String productStatus;

    public Date createdOn;

    public String createdBy;

    public Date approvedOn;

    public String approvedBy;

    public Long airtimeToken;
    public String status;

    public Boolean ussdChannelEnabled = false;

    public Boolean webChannelEnabled = false;

    public Boolean smscChannelEnabled = false;

    public Boolean voucherChannelEnabled = false;

    public Boolean mobileMoneyEnabled = false;
    public List<AirtimeValue> airtimeProducts;

    public static List<DataModel> parseAirtimeValueListJSONResponse(String response)throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        AirtimeValue airtimeValue = new AirtimeValue();
        reader.beginObject();
        List<DataModel> airtimeValueList = new ArrayList<DataModel>();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status") && reader.peek() != JsonToken.NULL)
            {
               airtimeValue.status = reader.nextString();
            }else if(name.equals("airtimeProducts") && reader.peek() != JsonToken.NULL) {
                List<AirtimeValue> airtimeProductsList = new ArrayList<AirtimeValue>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    AirtimeValue airtimeValues = readAirtimeValueInput(reader);
                    airtimeProductsList.add(airtimeValues);
                    reader.endObject();
                }
                airtimeValue.airtimeProducts = airtimeProductsList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        airtimeValueList.add(airtimeValue);
        reader.endObject();

        return airtimeValueList;
    }

    private static AirtimeValue readAirtimeValueInput(JsonReader reader)throws IOException {
        AirtimeValue airtimeValue = new AirtimeValue();

        while (reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("airtimeValue") && reader.peek() != JsonToken.NULL){
                airtimeValue.airtimeValue = Float.valueOf(reader.nextString());
            }else if(name.equals("productStatus") && reader.peek() != JsonToken.NULL){
                airtimeValue.productStatus = reader.nextString();
            }else if(name.equals("mobileMoneyEnabled") && reader.peek() != JsonToken.NULL){
                airtimeValue.mobileMoneyEnabled = reader.nextBoolean();
            }else if(name.equals("ussdChannelEnabled") && reader.peek() != JsonToken.NULL){
                airtimeValue.ussdChannelEnabled = reader.nextBoolean();
            }else if(name.equals("voucherChannelEnabled") && reader.peek() != JsonToken.NULL){
                airtimeValue.voucherChannelEnabled = reader.nextBoolean();
            }else if(name.equals("webChannelEnabled") && reader.peek() != JsonToken.NULL){
                airtimeValue.webChannelEnabled = reader.nextBoolean();
            }else if(name.equals("smscChannelEnabled") && reader.peek() != JsonToken.NULL){
                airtimeValue.smscChannelEnabled = reader.nextBoolean();
            }else{
                reader.skipValue();
            }
        }
        return airtimeValue;
    }

}
