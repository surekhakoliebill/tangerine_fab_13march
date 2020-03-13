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

/**
 * Created by cloudkompare on 28/8/15.
 */
public class UserLogin implements DataModel, Serializable {

    public String userName;
    public String password;
    public String status;
    public String servedMSISDN;
    public String userId;
    public String sessionKey;
    public String subscriptionId;
    public String gcmId;
    public String userGroup;
    public String resellerId;
    public String orderNo;
    public String reason;
    public String username;
    public String fileName;

    public String oldImsi;
    public String newImsi;
    public String msisdn;
    public String state;
    public String serviceType;
    public String message;
    public Boolean forcePwdUpdate;
    public List<String> subscriptions;
    public List<Subscription> subscriptionList;
    public String activationState;
    public String paymentId;

    //FOR GENERATE REPORT
     public String reportStatus;
     public String docPath;



    private static UserLogin readUserInput(JsonReader reader) throws IOException {
        UserLogin login = new UserLogin();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                login.status = reader.nextString();
            } else if (name.equals("userName")) {
                login.userName = reader.nextString();
            } else if (name.equals("userGroup")) {
                login.userGroup = reader.nextString();
            } else if (name.equals("password")) {
                login.password = reader.nextString();
            } else if (name.equals("servedMSISDN")) {
                login.servedMSISDN = reader.nextString();
            } else if (name.equals("sessionKey")) {
                login.sessionKey = reader.nextString();
            } else if (name.equals("fileName")) {
                login.fileName = reader.nextString();
            } else if (name.equals("userId")) {
                login.userId = reader.nextString();
            } else if (name.equals("gcmId") && reader.peek() != JsonToken.NULL) {
                login.gcmId = reader.nextString();
            } else if (name.equals("subscriptionId")) {
                login.subscriptionId = reader.nextString();
            }else if (name.equals("forcePwdUpdate") && reader.peek() != JsonToken.NULL) {
                login.forcePwdUpdate = reader.nextBoolean();
            }else if (name.equals("orderNo") && reader.peek() != JsonToken.NULL) {
                login.orderNo = reader.nextString();
            } else if (name.equals("subscriptionIds") && reader.peek() != JsonToken.NULL) {
                List<String> subscriptionList = new ArrayList<String>();

                reader.beginArray();
                while(reader.hasNext()){
                    String subscriptionId = reader.nextString();
                    subscriptionList.add(subscriptionId);
                }
                reader.endArray();

                login.subscriptions = subscriptionList;

            } else if (name.equals("subscriptions") && reader.peek() != JsonToken.NULL) {

                reader.beginArray();
                List<Subscription> subscriptionList = new ArrayList<Subscription>();
                while(reader.hasNext()) {
                    reader.beginObject();
                    Subscription subscriptionCommand = UserRegistration.readSubscriptionObject(reader);
                    subscriptionList.add(subscriptionCommand);
                    reader.endObject();
                }
                login.subscriptionList  = subscriptionList;
                reader.endArray();

            } else if (name.equals("reason") && reader.peek() != JsonToken.NULL) {
                login.reason = reader.nextString();
            }else if (name.equals("msisdn") && reader.peek() != JsonToken.NULL) {
                login.msisdn = reader.nextString();
            } else if (name.equals("username") && reader.peek() != JsonToken.NULL) {
                login.username = reader.nextString();
            }else if (name.equals("serviceType") && reader.peek() != JsonToken.NULL) {
                login.serviceType = reader.nextString();
            }else if (name.equals("oldImsi") && reader.peek() != JsonToken.NULL) {
                login.oldImsi = reader.nextString();
            }else if (name.equals("newImsi") && reader.peek() != JsonToken.NULL) {
                login.newImsi = reader.nextString();
            }else if (name.equals("resellerId")&& reader.peek() != JsonToken.NULL) {
                login.resellerId = reader.nextString();
            }else if (name.equals("activationState")&& reader.peek() != JsonToken.NULL) {
                login.activationState = reader.nextString();
            }else if (name.equals("paymentId")&& reader.peek() != JsonToken.NULL) {
                login.paymentId = reader.nextString();
            } else if (name.equals("message")) {
                login.message = reader.nextString();
            } else if (name.equals("reportStatus")) {
                login.reportStatus = reader.nextString();
            } else if (name.equals("docPath")) {
                login.docPath = reader.nextString();
            }else {
                reader.skipValue();
            }
        }

        return login;
    }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> login = new ArrayList<DataModel>();
        reader.beginObject();
        UserLogin userLogin = readUserInput(reader);
        reader.endObject();
        login.add(userLogin);
        return login;
    }

    public String getUserRequestJSON() throws IOException {
        StringWriter sWriter = new StringWriter();
        JsonWriter jWriter = new JsonWriter(sWriter);

        jWriter.setIndent(" ");

        jWriter.beginObject();
        jWriter.name("userName").value(userName);
        jWriter.name("gcmId").value(gcmId);
        jWriter.name("password").value(password);
        jWriter.name("servedMSISDN").value(servedMSISDN);
        jWriter.name("subscriptionId").value(subscriptionId);
        jWriter.name("userGroup").value(userGroup);
        jWriter.name("oldImsi").value(oldImsi);
        jWriter.name("newImsi").value(newImsi);
        jWriter.name("msisdn").value(msisdn);
        jWriter.name("state").value(state);
        jWriter.name("reason").value(reason);
        jWriter.name("status").value(status);
        jWriter.endObject();
        String json = sWriter.toString();
        sWriter.close();

        return json;
    }

    public String getSimRequestJSON() throws IOException {
        StringWriter sWriter = new StringWriter();
        JsonWriter jWriter = new JsonWriter(sWriter);

        jWriter.setIndent(" ");

        jWriter.beginObject();
        jWriter.name("newImsi").value(newImsi);
        jWriter.name("msisdn").value(msisdn);
        jWriter.name("state").value(state);
        jWriter.name("reason").value(reason);
        jWriter.name("userId").value(userId);
        jWriter.endObject();
        String json = sWriter.toString();
        sWriter.close();

        return json;
    }

    @Override
    public DataType getDataType() {
        return null;
    }


}
