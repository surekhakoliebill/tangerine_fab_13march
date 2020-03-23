package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.widget.ListView;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
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
    public String newIccid;
    public String userId;

    public String status;

    public float accuracy;
    public List<String> msisdns;
    public List<String> specialNumbers;


    public String lastSmsSentNumber;

    public List<String> lastThreeCalledNumbers;

    public List<String> lastThreeSmsSentNumbers;

    public List<String> mostlyCalledNumbers;

    public Double airTimeAmountLoaded;

    public String latestBundleUsed;

    public Float availableMobileMoneyBalance;

    public String mobileMoneyMPin;

    public Boolean skipValidation;

    public String skipValidationReason;

    // SIM INFO FOR SPECIAL NUMBER
    public List<SimReplacementForm> specialNumberInfo;
    public String servedIMSI;

    public String servedMSISDN;

    public Boolean isAllocated;

    public String bulkId;

    public String staticIpAddress;

    public String lifecycleState;

    public Date lifecycleStateUpdatedOn;

    public String simHssState;

    public Boolean isSpecialNumber;

    public Float specialNumberPrice;




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

    public static SimReplacementForm readResponse(JsonReader reader) throws IOException {

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

        // jwriter.name("lastCallsLimit").value(lastCallsLimit);
        // jwriter.name("lastPaymentsLimit").value(lastPaymentsLimit);
        //  jwriter.name("lastCallDuration").value(lastCallDuration);
        //   jwriter.name("lastCalledParty").value(lastCalledParty);
        //  jwriter.name("lastPaymentAmout").value(lastPaymentAmout);
        //   jwriter.name("lastCalledPartyCheckLimit").value(lastCalledPartyCheckLimit);
        jwriter.name("ownerName").value(ownerName);
        jwriter.name("email").value(email);
        jwriter.name("msisdn").value(msisdn);
        jwriter.name("skipValidation").value(skipValidation);
        jwriter.name("airTimeAmountLoaded").value(airTimeAmountLoaded);

        jwriter.name("latestBundleUsed").value(latestBundleUsed);
        //jwriter.name("lastThreeCalledNumbers").value((Number) lastThreeCalledNumbers);

        jwriter.name("lastThreeCalledNumbers");
        getLastThreeCalledNumbersJSON(lastThreeCalledNumbers, jwriter);

        jwriter.name("lastThreeSmsSentNumbers");
        getLastThreeSmsNumbersJSON(lastThreeSmsSentNumbers, jwriter);

        jwriter.name("mostlyCalledNumbers");
        getMostlyCalledNumbersJSON(mostlyCalledNumbers, jwriter);


        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }


    public String getJSONICCIDData() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("newIccid").value(newIccid);
        jwriter.name("msisdn").value(msisdn);
        jwriter.name("userId").value(userId);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }
    @Override
    public DataType getDataType() {

        return DataType.SimReplacementForm;
    }

    public void getLastThreeCalledNumbersJSON(List<String> lastThreeCalledNumbers, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (String lastThreeCalledCount : lastThreeCalledNumbers) {
                jwriter.value(lastThreeCalledCount);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getLastThreeSmsNumbersJSON(List<String> lastThreeSmsSentNumbers, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (String lastThreeSmsNumberCount : lastThreeSmsSentNumbers) {
                jwriter.value(lastThreeSmsNumberCount);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMostlyCalledNumbersJSON(List<String> mostlyCalledNumbers, JsonWriter jwriter) throws IOException {
        try {
            jwriter.beginArray();
            for (String mostlyCalledNumberCount : mostlyCalledNumbers) {
                jwriter.value(mostlyCalledNumberCount);
            }
            jwriter.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            }else if (name.equals("specialNumbers") && reader.peek() != JsonToken.NULL) {
                List<String> specialList = new ArrayList<String>();
                reader.beginArray();
                while(reader.hasNext()){
                    try{
                        if(reader.peek() == JsonToken.STRING.NULL){
                            return null;
                        }else{
                            String subCat = reader.nextString();
                            specialList.add(subCat);
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
                simInfo.specialNumbers = specialList;

            }else{
                reader.skipValue();
            }
        }
        availableMSISDNList.add(simInfo);
        reader.endObject();

        return availableMSISDNList;
    }


    public static List<DataModel> parseSpecialNumberJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> availableMSISDNList = new ArrayList<DataModel>();
        SimReplacementForm simInfo = new SimReplacementForm();

        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                simInfo.status = reader.nextString();
            }else if (name.equals("relatedNumbers") && reader.peek() != JsonToken.NULL) {

                List<SimReplacementForm> simInfoList = new ArrayList<>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    SimReplacementForm simInfor = readSimInfo(reader);
                    simInfoList.add(simInfor);
                    reader.endObject();
                }
                reader.endArray();
                simInfo.specialNumberInfo = simInfoList;

            }else{
                reader.skipValue();
            }
        }
        availableMSISDNList.add(simInfo);
        reader.endObject();

        return availableMSISDNList;
    }

    private static SimReplacementForm readSimInfo(JsonReader reader) throws IOException{

        SimReplacementForm replacementForm = new SimReplacementForm();

        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("specialNumberPrice") && reader.peek() != JsonToken.NULL) {
                 replacementForm.specialNumberPrice = Float.parseFloat(reader.nextString());
            }else if (name.equals("servedMSISDN") && reader.peek() != JsonToken.NULL) {
                replacementForm.servedMSISDN = reader.nextString();
            } else {
                    reader.skipValue();
            }
        }
        return replacementForm;
    }

}