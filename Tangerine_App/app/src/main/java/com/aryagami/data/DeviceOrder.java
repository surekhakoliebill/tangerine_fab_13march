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

public class DeviceOrder implements DataModel, Serializable {

    public Long productListingId;

    public String productName;

    public String productDesc;

    public String priceCurrency;

    public Float productPrice;

    public String productCategory;

    public String productSubCategory;

    public Boolean isActive;

    public String productMakeInfo;

    public String productModelInfo;

    public Integer productQuantity;

    public Boolean pricePending;

    public String status;

    // check IMEI Availability Response

    public String serialNumber;

    public Long listingId;

    public Float listingPrice;

    public Float inventoryPrice;

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> requestByResellerList = new ArrayList<DataModel>();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();
            }else if(name.equals("availableProducts"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    DeviceOrder deviceOrder = readRequestsObject(reader);
                    requestByResellerList.add(deviceOrder);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();

        return requestByResellerList;
    }

    private static DeviceOrder readRequestsObject(JsonReader reader) throws IOException {
        DeviceOrder request = new DeviceOrder();

        while(reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("productListingId")&& reader.peek() != JsonToken.NULL) {
                request.productListingId = reader.nextLong();
            } else if(name.equals("productName")&& reader.peek() != JsonToken.NULL) {
                request.productName = reader.nextString();
            } else if(name.equals("status")&& reader.peek() != JsonToken.NULL) {
                request.status = reader.nextString();
            } else if(name.equals("productDesc")&& reader.peek() != JsonToken.NULL) {
                request.productDesc = reader.nextString();
            } else if(name.equals("priceCurrency")&& reader.peek() != JsonToken.NULL) {
                request.priceCurrency = reader.nextString();
            } else if(name.equals("productPrice")&& reader.peek() != JsonToken.NULL) {
                request.productPrice = Float.parseFloat(reader.nextString());
            } else if(name.equals("productCategory")&& reader.peek() != JsonToken.NULL) {
                request.productCategory = reader.nextString();
            }else if(name.equals("productSubCategory")&& reader.peek() != JsonToken.NULL) {
                request.productSubCategory = reader.nextString();
            }else if(name.equals("productMakeInfo")&& reader.peek() != JsonToken.NULL) {
                request.productMakeInfo = reader.nextString();
            }else if(name.equals("productModelInfo")&& reader.peek() != JsonToken.NULL) {
                request.productModelInfo = reader.nextString();
            }
            else{
                reader.skipValue();
            }
        }
        return request;

    }

    @Override
    public DataType getDataType() {
        return DataModel.DataType.DeviceOrder;
    }

    public static List<DataModel> parseIMEIResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> requestByResellerList = new ArrayList<DataModel>();
        DeviceOrder order = new DeviceOrder();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status")) {
                order.status = reader.nextString();
            }else if(name.equals("serialNumber")) {
                order.serialNumber = reader.nextString();
            }else if(name.equals("productListingId")) {
                order.productListingId =  reader.nextLong();
            }else if(name.equals("productPrice")) {
                order.productPrice = Float.parseFloat(reader.nextString());
            }else if(name.equals("inventoryPrice")) {
                order.inventoryPrice = Float.parseFloat(reader.nextString());
            }else {
                reader.skipValue();
            }
        }
        reader.endObject();
        requestByResellerList.add(order);

        return requestByResellerList;
    }

}
