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
 * Created by aryagami on 22/5/16.
 */
public class UserInfo implements DataModel, Serializable {

    public String userId;
    public String userName;
    public String fullName;
    public String email;
    public String company;
    public String phoneNumber;
    public String password;
    public String oldPassword;
    public String currency;
    public String profilePicture;
    public String resellerCode;
    public String physicalAddress;
    public String physicalAddress2;
    public String physicalAddressCity;
    public String physicalAddressCountry;
    public String correspondenceAddressCity;
    public String correspondenceAddressCountry;
    public String correspondenceAddress;
    public Boolean isActive;
    public String status;
    public String userGroup;
    public String surname;
    public String aggregator;

    public String postJSONInfoForChangePassword() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("userId").value(userId);
        jwriter.name("password").value(password);
        jwriter.name("oldPassword").value(oldPassword);

        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }


    @Override
    public DataType getDataType() {
        return DataModel.DataType.UserInfo;
    }


    public static UserInfo readUserInfoObject(JsonReader reader)throws IOException {
        UserInfo userInfo = new UserInfo();
        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("userId")&& reader.peek() != JsonToken.NULL) {
                userInfo.userId = reader.nextString();
            } else if(name.equals("fullName")&& reader.peek() != JsonToken.NULL) {
                userInfo.fullName = reader.nextString();
            } else if (name.equals("userName")&& reader.peek() != JsonToken.NULL) {
                userInfo.userName = reader.nextString();
            } else if (name.equals("surname")&& reader.peek() != JsonToken.NULL) {
                userInfo.surname = reader.nextString();
            } else if (name.equals("company")&& reader.peek() != JsonToken.NULL) {
                userInfo.company = reader.nextString();
            } else if (name.equals("email")&& reader.peek() != JsonToken.NULL) {
                userInfo.email = reader.nextString();
            } else if (name.equals("password")&& reader.peek() != JsonToken.NULL) {
                userInfo.password = reader.nextString();
            }else if (name.equals("oldPassword")&& reader.peek() != JsonToken.NULL) {
                userInfo.oldPassword = reader.nextString();
            } else if (name.equals("profilePicture")&& reader.peek() != JsonToken.NULL) {
                userInfo.profilePicture = reader.nextString();
            } else if (name.equals("phoneNumber") && reader.peek() != JsonToken.NULL) {
                userInfo.phoneNumber = reader.nextString();
            } else if (name.equals("physicalAddress") && reader.peek() != JsonToken.NULL) {
                userInfo.physicalAddress = reader.nextString();
            } else if (name.equals("physicalAddress2") && reader.peek() != JsonToken.NULL) {
                userInfo.physicalAddress2 = reader.nextString();
            } else if (name.equals("physicalAddressCity") && reader.peek() != JsonToken.NULL) {
                userInfo.physicalAddressCity = reader.nextString();
            } else if (name.equals("physicalAddressCountry") && reader.peek() != JsonToken.NULL) {
                userInfo.physicalAddressCountry = reader.nextString();
            } else if (name.equals("correspondenceAddress") && reader.peek() != JsonToken.NULL) {
                 userInfo.correspondenceAddress = reader.nextString();
            } else if (name.equals("correspondenceAddressCity") && reader.peek() != JsonToken.NULL) {
                 userInfo.correspondenceAddressCity = reader.nextString();
            } else if (name.equals("correspondenceAddressCountry") && reader.peek() != JsonToken.NULL) {
                userInfo.correspondenceAddressCountry = reader.nextString();
            } else if (name.equals("isActive") && reader.peek() != JsonToken.NULL) {
                userInfo.isActive = reader.nextBoolean();
            } else if (name.equals("aggregator") && reader.peek() != JsonToken.NULL) {
                userInfo.aggregator = reader.nextString();
            }
                else {
                    reader.skipValue();
                }
            }
            return userInfo;
        }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> userInfoList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;
        UserInfo userInfo = new UserInfo();
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                userInfo.status = reader.nextString();
            }else if(name.equals("userById"))
            {
                reader.beginObject();
                userInfo = readUserInfoObject(reader);
                reader.endObject();

            }
        }
        reader.endObject();
        userInfoList.add(userInfo);
        return userInfoList;
    }

    public static List<DataModel> parseJSONResponseOfChangePassword(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> userInfoList = new ArrayList<DataModel>();
        reader.beginObject();
        UserInfo userInfo = new UserInfo();
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                userInfo.status = reader.nextString();
            }else if(name.equals("userId"))
            {
                userInfo.userId = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        userInfoList.add(userInfo) ;
        reader.endObject();

        return userInfoList;
    }
}
