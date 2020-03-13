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

public class ResellerLoginInfo implements DataModel, Serializable {

    public UserRegistration userInfo;
    public String aggregator;
    public String status;

    @Override
    public DataType getDataType() {
        return DataType.ResellerLoginInfo;
    }


    public static List<DataModel> parseJSONResponseforResellerId(String json) throws IOException {
        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> userInfoList = new ArrayList<DataModel>();

        reader.beginObject();
        ResellerLoginInfo loginInfo  = readResellerJSONInput(reader);
        reader.endObject();
        userInfoList.add(loginInfo);


        return userInfoList;

    }

    private static ResellerLoginInfo readResellerJSONInput(JsonReader reader) throws IOException {

        ResellerLoginInfo resellerLoginInfo = new ResellerLoginInfo();

        while (reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("UserInfo")&& reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                resellerLoginInfo.userInfo = readResellerObject(reader);
                reader.endObject();

            }else if (name.equals("aggregator")&& reader.peek() != JsonToken.NULL) {

                resellerLoginInfo.aggregator = reader.nextString();

            }else if (name.equals("status")&& reader.peek() != JsonToken.NULL) {

                resellerLoginInfo.status = reader.nextString();

            }else {
                reader.skipValue();
            }

        }

        return resellerLoginInfo;
    }

    private static UserRegistration readResellerObject(JsonReader reader)throws IOException {
        UserRegistration userRegistration = new UserRegistration();
        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("userName")&& reader.peek() != JsonToken.NULL) {
                userRegistration.userName = reader.nextString();
            } else if (name.equals("email")&& reader.peek() != JsonToken.NULL) {
                userRegistration.email = reader.nextString();
            } else if (name.equals("company")&& reader.peek() != JsonToken.NULL) {
                userRegistration.company = reader.nextString();
            } else if (name.equals("primaryPersonMobileNumber")&& reader.peek() != JsonToken.NULL) {
                userRegistration.primaryPersonMobileNumber = reader.nextString();
            } else if (name.equals("currency")&& reader.peek() != JsonToken.NULL) {
                userRegistration.currency = reader.nextString();
            } else if (name.equals("resellerId")&& reader.peek() != JsonToken.NULL) {
                userRegistration.resellerId = reader.nextString();
            } else if (name.equals("accountId")&& reader.peek() != JsonToken.NULL) {
                userRegistration.accountId = reader.nextString();
            }else if (name.equals("resellerCode")&& reader.peek() != JsonToken.NULL) {
                userRegistration.resellerCode = reader.nextString();
            } else if (name.equals("physicalAddress")&& reader.peek() != JsonToken.NULL) {
                userRegistration.physicalAddress = reader.nextString();
            } else if (name.equals("phoneNumber")&& reader.peek() != JsonToken.NULL) {
                userRegistration.phoneNumber = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        return userRegistration;

    }


}
