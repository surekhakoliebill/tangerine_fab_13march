package com.aryagami.data;

import android.util.JsonWriter;

import java.io.Serializable;
import java.io.StringWriter;

public class ActivateCommand implements DataModel, Serializable {

    public String entityName;
    public String activationId;
    public String reason;
    public String servedMSISDN;


    public String getActivateJSON() throws Exception {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("entityName").value(entityName);
        jwriter.name("activationId").value(activationId);
        jwriter.name("reason").value(reason);
        jwriter.endObject();

        String json = swriter.toString();
        return json;
    }


    @Override
    public DataType getDataType() {
        return DataModel.DataType.ActivateCommand;
    }
}
