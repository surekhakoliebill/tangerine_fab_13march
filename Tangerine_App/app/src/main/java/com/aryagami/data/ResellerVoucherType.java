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

public class ResellerVoucherType implements DataModel {
    public String voucherType;
    public String voucherDescription;
    public Float voucherPrice;
    public String voucherName;

    public String subscriptionId;
    public String transactionId;
    public Float transactionAmount;
    public NewOrderCommand.LocationCoordinates resellerLocation;

    @Override
    public DataType getDataType() {
        return DataModel.DataType.ResellerVoucherType;
    }

    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> VoucherTypeList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                status = reader.nextString();
            }else if(name.equals("voucherTypes"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ResellerVoucherType voucherType = readVoucherTypeObject(reader);
                    VoucherTypeList.add(voucherType);
                    reader.endObject();
                }
                reader.endArray();

            }else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return VoucherTypeList;
    }

    public static ResellerVoucherType readVoucherTypeObject(JsonReader reader) {

        ResellerVoucherType voucherType = new ResellerVoucherType();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("voucherType") && reader.peek() != JsonToken.NULL) {
                    voucherType.voucherType = reader.nextString();
                } else if (name.equals("voucherPrice") && reader.peek() != JsonToken.NULL) {
                    voucherType.voucherPrice = Float.parseFloat(reader.nextString());
                }  else if (name.equals("voucherDescription") && reader.peek() != JsonToken.NULL) {
                    voucherType.voucherDescription = reader.nextString();
                } else if (name.equals("voucherName") && reader.peek() != JsonToken.NULL) {
                    voucherType.voucherName = reader.nextString();
                }else {
                    reader.skipValue();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return voucherType;
    }


    public String getUserInfoJSON() {

        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        try {
            jwriter.beginObject();
            jwriter.name("subscriptionId").value(subscriptionId);
            jwriter.name("voucherType").value(voucherType);
            jwriter.name("transactionAmount").value(transactionAmount);
            jwriter.name("transactionId").value(transactionId);

            jwriter.name("resellerLocation");
            if (resellerLocation != null) {
                getLocationCoordinatesJSON(jwriter);
            }
            jwriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = swriter.toString();
        return json;
    }

    public  void getLocationCoordinatesJSON(JsonWriter jwriter) throws IOException {
        if(resellerLocation != null){
            jwriter.beginObject();

            jwriter.name("latitudeValue").value(resellerLocation.latitudeValue);
            jwriter.name("longitudeValue").value(resellerLocation.longitudeValue);

            jwriter.endObject();
        }
    }
}
