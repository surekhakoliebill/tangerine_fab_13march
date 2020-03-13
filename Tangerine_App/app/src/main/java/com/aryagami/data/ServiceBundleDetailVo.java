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

public class ServiceBundleDetailVo implements DataModel, Serializable {


    @Override
    public DataType getDataType() {
        return DataModel.DataType.ServiceBundleDetailVo;
    }

    public Boolean onDemandSimProvision = false;

    public String bundleInfo;

    public String planBundleName;

    public List<String> servedMsisdns;

    public List<String> simIccids;

    public static List<DataModel> parseSimIccidListJSONResponse(String response)throws IOException {
        List<DataModel> simIccidList = new ArrayList<DataModel>();


        /*InputStream in;
        in = new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        reader.beginObject();
              ServiceBundleDetailVo serviceBundleDetailVo = readSIMIccidInput(reader);
        reader.endObject();
        simIccidList.add(serviceBundleDetailVo);*/


        InputStream in;
        in =  new ByteArrayInputStream(response.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> aggregatorList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                status = reader.nextString();
            }else if(name.equals("serviceBundles"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    ServiceBundleDetailVo serviceBundleDetailVo = readSIMIccidInput(reader);
                    simIccidList.add(serviceBundleDetailVo);
                    reader.endObject();
                }
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();

        return simIccidList;
    }

    private static ServiceBundleDetailVo readSIMIccidInput(JsonReader reader)throws IOException {
        ServiceBundleDetailVo bundleDetailVo = new ServiceBundleDetailVo();

        while (reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("simIccids") && reader.peek() != JsonToken.NULL){
                List<String> simIccIdsList = new ArrayList<String>();
                reader.beginArray();
                while(reader.hasNext()){
                    String iccid = reader.nextString();
                    simIccIdsList.add(iccid);
                }
                reader.endArray();
                bundleDetailVo.simIccids = simIccIdsList;
            }else{
                reader.skipValue();
            }
        }

       return bundleDetailVo;
    }

}
