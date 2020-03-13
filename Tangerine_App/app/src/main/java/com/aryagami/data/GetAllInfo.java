package com.aryagami.data;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetAllInfo implements DataModel, Serializable {

    public String status;
    public List<UserRegistration> userRegistrationList;

    @Override
    public DataType getDataType() {
        return DataModel.DataType.GetAllInfo;
    }

    public static List<DataModel> parseJSONResponse(String json) throws IOException {

        InputStream in;
        in = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> getAllInfoList = new ArrayList<DataModel>();
        reader.beginObject();

        GetAllInfo getAllInfo = new GetAllInfo();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                getAllInfo.status = reader.nextString();
            } else if (name.equals("allUsers")) {
                reader.beginArray();
                List<UserRegistration> registrationsList = new ArrayList<UserRegistration>();
                while (reader.hasNext()) {
                    reader.beginObject();
                    UserRegistration userRegistration = UserRegistration.readUserInfoObject(reader);
                    registrationsList.add(userRegistration);
                    reader.endObject();
                }
                getAllInfo.userRegistrationList = registrationsList;
                reader.endArray();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        getAllInfoList.add(getAllInfo);
        return getAllInfoList;
    }

}
