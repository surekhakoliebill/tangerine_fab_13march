package com.aryagami.data;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;

public class BugReportCommand implements DataModel {
    @Override
    public DataType getDataType() {
        return DataModel.DataType.BugReportCommand;
    }

    public String reportArea;
    public String emailIds;
    public String reportDescription;


    public String bugReportCommandJSON() throws IOException {

        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        try {
            jwriter.beginObject();
            jwriter.name("reportArea").value(reportArea);
            jwriter.name("emailIds").value(emailIds);
            jwriter.name("reportDescription").value(reportDescription);
            jwriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = swriter.toString();

        return json;
    }
}
