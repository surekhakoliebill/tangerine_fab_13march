package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SimReplacementForm implements DataModel {

    public Integer lastCallsLimit;

    public Integer lastPaymentsLimit;

    public Long lastCallDuration;

    public String lastCalledParty;

    public Float lastPaymentAmout;

    public Integer lastCalledPartyCheckLimit;

    public String ownerName;

    public String email;

    public String msisdn;

    public String status;

    public float accuracy;
    public List<String> msisdns;

    public static List<DataModel> parseSimJSONResponse(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> login = new ArrayList<DataModel>();
        reader.beginObject();
        SimReplacementForm simResponse = readResponse(reader);
        reader.endObject();
        login.add(simResponse);
        return login;
    }

    private static SimReplacementForm readResponse(JsonReader reader) throws IOException {

        SimReplacementForm simReplacementForm = new SimReplacementForm();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                simReplacementForm.status = reader.nextString();
            } else if (name.equals("accuracy")) {
                simReplacementForm.accuracy = Float.parseFloat(reader.nextString());
            }else{
                reader.skipValue();
            }
        }

        return simReplacementForm;
    }


    public String getApproveResellerInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("lastCallsLimit").value(lastCallsLimit);
        jwriter.name("lastPaymentsLimit").value(lastPaymentsLimit);
        jwriter.name("lastCallDuration").value(lastCallDuration);
        jwriter.name("lastCalledParty").value(lastCalledParty);
        jwriter.name("lastPaymentAmout").value(lastPaymentAmout);
        jwriter.name("lastCalledPartyCheckLimit").value(lastCalledPartyCheckLimit);
        jwriter.name("ownerName").value(ownerName);
        jwriter.name("email").value(email);
        jwriter.name("msisdn").value(msisdn);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }

    @Override
    public DataType getDataType() {

        return DataType.SimReplacementForm;
    }


    public static List<DataModel> parseAvailableMSISDNListJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> availableMSISDNList = new ArrayList<DataModel>();
        List<String> msisdnList = new ArrayList<String>();
        SimReplacementForm simInfo = new SimReplacementForm();

        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                simInfo.status = reader.nextString();
            }else if (name.equals("msisdns") && reader.peek() != JsonToken.NULL) {

                reader.beginArray();
                while(reader.hasNext()){
                    try{
                        if(reader.peek() == JsonToken.STRING.NULL){
                            return null;
                        }else{
                            String subCat = reader.nextString();
                            msisdnList.add(subCat);
                        }

                    }catch (JsonIOException e){
                        return  null;
                    }catch (JsonParseException e){
                        return null;
                    }catch (IOException e){
                        return null;
                    }
                }
                reader.endArray();
                simInfo.msisdns = msisdnList;

            }else{
                reader.skipValue();
            }
        }
        availableMSISDNList.add(simInfo);
        reader.endObject();

        return availableMSISDNList;
    }

}
