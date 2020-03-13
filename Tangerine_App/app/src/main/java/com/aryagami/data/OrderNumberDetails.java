package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderNumberDetails implements DataModel, Serializable {

    @Override
    public DataType getDataType() {
        return DataType.GetOrderNumberDetails;
    }

    public String orderId;

    public String contractId;

    public String creditScore;

    public Float creditLimit;

    public Float totalValue;

    public Float depositValue;

    public String currentStatus;

    public String createdOn;

    public String approvedOn;

    public String approvedBy;

    public Boolean fulfillmentPending;

    public Boolean approvalPending;

    public String orderNo;

    public String userId;

    public String resellerCode;

    public String status;

    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> orderDetailsList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;
        OrderNumberDetails orderDetails = new OrderNumberDetails();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
               orderDetails.status = reader.nextString();
            } else if (name.equals("order")) {

                while (reader.hasNext()) {
                    reader.beginObject();
                     orderDetails = readOrderDetailsObject(reader, orderDetails);
                    reader.endObject();
                }
            } else {
                reader.skipValue();
            }
        }
        orderDetailsList.add(orderDetails);
        reader.endObject();

        return orderDetailsList;
    }

    public static OrderNumberDetails readOrderDetailsObject(JsonReader reader, OrderNumberDetails orderNumberDetails) throws IOException {

       // OrderNumberDetails orderNumberDetails = new OrderNumberDetails();


        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("approvalPending") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.approvalPending = reader.nextBoolean();
            } else if (name.equals("approvedBy") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.approvedBy = reader.nextString();
            } else if (name.equals("approvedOn") && reader.peek() != JsonToken.NULL) {
                //orderNumberDetails.approvedOn = Nereader.nextString();
                try {
                   Date date2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString());
                  // orderNumberDetails.approvedOn = date2;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                   orderNumberDetails.approvedOn = format.format(date2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else if (name.equals("contractId") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.contractId = reader.nextString();
            } else if (name.equals("createdOn") && reader.peek() != JsonToken.NULL) {
                // orderNumberDetails.createdOn = reader.nextString();
                try {
                    Date date2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(reader.nextString());
                    // orderNumberDetails.approvedOn = date2;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    orderNumberDetails.createdOn = format.format(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (name.equals("creditLimit") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.creditLimit = Float.parseFloat(reader.nextString());
            } else if (name.equals("creditScore") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.creditScore = reader.nextString();
            } else if (name.equals("currentStatus") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.currentStatus = reader.nextString();
            } else if (name.equals("depositValue") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.depositValue = Float.parseFloat(reader.nextString());
            } else if (name.equals("fulfillmentPending") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.fulfillmentPending = reader.nextBoolean();
            } else if (name.equals("orderId") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.orderId = reader.nextString();
            } else if (name.equals("orderNo") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.orderNo = reader.nextString();
            } else if (name.equals("totalValue") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.totalValue = Float.parseFloat(reader.nextString());
            } else if (name.equals("userId") && reader.peek() != JsonToken.NULL) {
                orderNumberDetails.userId = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        return orderNumberDetails;
    }


}
