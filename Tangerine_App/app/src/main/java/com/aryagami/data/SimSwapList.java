package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimSwapList implements DataModel {

    public String simSwapLogId;

    public String oldImsi;

    public String newImsi;

    public String reason;

    public String status;

    public String msisdn;

    public String subscriptionId;

    public String swapDate;

    public String state;

    public String userId;


    public static List<DataModel> parseJSONResponse(String response) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> simSwapList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;


        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();
            }else if(name.equals("simSwaps"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    SimSwapList simList = readResellerStockBinObject(reader);
                    simSwapList.add(simList);
                    reader.endObject();
                }
                reader.endArray();

            }
        }
        reader.endObject();

        return simSwapList;

    }

    public static SimSwapList readResellerStockBinObject(JsonReader reader) throws IOException {
        SimSwapList simSwapList = new SimSwapList();

        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("simSwapLogId") && reader.peek() != JsonToken.NULL){
                simSwapList.simSwapLogId = reader.nextString();
            }else if(name.equals("oldImsi") && reader.peek() != JsonToken.NULL) {
                simSwapList.oldImsi = reader.nextString();
            }else if(name.equals("newImsi") && reader.peek() != JsonToken.NULL){
                simSwapList.newImsi = reader.nextString();
            }else if(name.equals("reason") && reader.peek() != JsonToken.NULL){
                simSwapList.reason =  reader.nextString();
            }else if(name.equals("status") && reader.peek() != JsonToken.NULL){
                simSwapList.status = reader.nextString();
            }else if(name.equals("msisdn") && reader.peek() != JsonToken.NULL){
                simSwapList.msisdn = reader.nextString();
            }else if(name.equals("subscriptionId") && reader.peek() != JsonToken.NULL){
                simSwapList.subscriptionId = reader.nextString();
            }else if(name.equals("swapDate") && reader.peek() != JsonToken.NULL){
                simSwapList.swapDate = reader.nextString();
            }else if(name.equals("state") && reader.peek() != JsonToken.NULL){
                simSwapList.state = reader.nextString();
            }else if(name.equals("userId") && reader.peek() != JsonToken.NULL){
                simSwapList.userId = reader.nextString();
            }else{
                reader.skipValue();
            }
        }

        return simSwapList;
    }


    @Override
    public DataType getDataType() {
        return DataModel.DataType.SimSwapList;
    }
}
