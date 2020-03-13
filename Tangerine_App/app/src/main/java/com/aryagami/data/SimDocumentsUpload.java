package com.aryagami.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SimDocumentsUpload implements DataModel {

    public String docId;

    public String docType;

    public String docFiles;

    public String userId;

    public String docFormat;

    public String reviewStatus;

    public static List<DataModel> parseJSONResponse(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> simDocumentUpload = new ArrayList<DataModel>();
        reader.beginObject();

        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                String responseStatus = reader.nextString();
            }else if(name.equals("success"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    SimDocumentsUpload simDocument = readRequestsObject(reader);
                    simDocumentUpload.add(simDocument);
                    reader.endObject();
                }
                reader.endArray();
            }
        }
        reader.endObject();

        return simDocumentUpload;
    }


    private static SimDocumentsUpload readRequestsObject(JsonReader reader) throws IOException {
        SimDocumentsUpload simDocumentsUpload = new SimDocumentsUpload();

        while(reader.hasNext()){
            String name = reader.nextName();

            if (name.equals("docId")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.docId = reader.nextString();
            } else if(name.equals("docType")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.docType = reader.nextString();
            } else if(name.equals("docFiles")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.docFiles = reader.nextString();
            } else if(name.equals("userId")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.userId = reader.nextString();
            } else if(name.equals("docFormat")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.docFormat = reader.nextString();
            }else if(name.equals("reviewStatus")&& reader.peek() != JsonToken.NULL) {
                simDocumentsUpload.reviewStatus = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        return simDocumentsUpload;
    }

    public String getApproveResellerInfoJSON() throws IOException {
        StringWriter swriter = new StringWriter();
        JsonWriter jwriter = new JsonWriter(swriter);
        jwriter.setIndent(" ");

        jwriter.beginObject();

        jwriter.name("docId").value(docId);
        jwriter.name("docType").value(docType);
        jwriter.name("docFiles").value(docFiles);
        jwriter.name("userId").value(userId);
        jwriter.name("docFormat").value(docFormat);
        jwriter.name("reviewStatus").value(reviewStatus);

        jwriter.endObject();
        String json = swriter.toString();
        return json;
    }


    @Override
    public DataType getDataType() {
        return DataType.SimDocumentUpload
                ;
    }
}
