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

public class Roles implements DataModel, Serializable {
    public Long roleId;
    public String roleName;
    public List<Roles> rolesList;
    public String status;


    public static List<DataModel> parseJSONResponse(String json) throws IOException {

        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        List<DataModel> rolesListResponse = new ArrayList<DataModel>();
        Roles role = new Roles();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                role.status = reader.nextString();
            }else if(name.equals("roles"))
            {
                List<Roles> rolesFinalList = new ArrayList<Roles>();
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    Roles role1 = readRolesObject(reader);
                    rolesFinalList.add(role1);
                    reader.endObject();
                }
                reader.endArray();

                role.rolesList = rolesFinalList;

            }
        }
        reader.endObject();

        rolesListResponse.add(role);

        return rolesListResponse;
    }

    private static Roles readRolesObject(JsonReader reader) throws IOException {
        Roles roles = new Roles();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("roleId") && reader.peek() != JsonToken.NULL) {
                roles.roleId = Long.parseLong(reader.nextString());
            } else if (name.equals("roleName") && reader.peek() != JsonToken.NULL) {
                roles.roleName = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        return roles;
    }


    @Override
    public DataType getDataType() {
        return DataModel.DataType.Roles;
    }
}
