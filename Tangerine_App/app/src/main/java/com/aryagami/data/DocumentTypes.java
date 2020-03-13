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

public class DocumentTypes implements DataModel, Serializable {
    public Long docTypeId;

    public String docType;

    public String userType;

    public Boolean isMandatory;

    public String displayName;

    public List<UserPdfDocCommand> userPdfDocs ;

    public static DocumentTypes[] getCompanyDocArray() {
        return companyDocArray;
    }

    public static void setCompanyDocArray(DocumentTypes[] companyDocArray) {
        DocumentTypes.companyDocArray = companyDocArray;
    }

    public static DocumentTypes[] companyDocArray = null;

    public static DocumentTypes[] getPersonalDocArray() {
        return personalDocArray;
    }

    public static void setPersonalDocArray(DocumentTypes[] personalDocArray) {
        DocumentTypes.personalDocArray = personalDocArray;
    }

    public static DocumentTypes[] personalDocArray = null;

    public static DocumentTypes[] getSimSwapDocArray() {
        return simSwapDocArray;
    }

    public static void setSimSwapDocArray(DocumentTypes[] simSwapDocArray) {
        DocumentTypes.simSwapDocArray = simSwapDocArray;
    }

    public static DocumentTypes[] simSwapDocArray = null;


    public static List<DataModel> parseJSONResponseArray(String json) throws IOException {
        InputStream in;
        in =  new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        List<DataModel> PdfUploadList = new ArrayList<DataModel>();
        reader.beginObject();

        String status = null;
       // DocumentTypes pdfUpload
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals("status"))
            {
                status = reader.nextString();
            }else if(name.equals("docTypes"))
            {
                reader.beginArray();
                while(reader.hasNext()) {
                    reader.beginObject();
                    DocumentTypes pdfUpload = readPdfObject(reader);
                    PdfUploadList.add(pdfUpload);
                    reader.endObject();
                }
                reader.endArray();

            }
        }
        reader.endObject();

        return PdfUploadList;
    }

    private static DocumentTypes readPdfObject(JsonReader reader) {

        DocumentTypes pdfUpload = new DocumentTypes();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("docTypeId") && reader.peek() != JsonToken.NULL) {
                    pdfUpload.docTypeId = reader.nextLong();
                } else if (name.equals("docType") && reader.peek() != JsonToken.NULL) {
                    pdfUpload.docType = reader.nextString();
                } else if (name.equals("userType") && reader.peek() != JsonToken.NULL) {
                    pdfUpload.userType = reader.nextString();
                } else if (name.equals("isMandatory") && reader.peek() != JsonToken.NULL) {
                    pdfUpload.isMandatory = reader.nextBoolean();
                } else if (name.equals("displayName") && reader.peek() != JsonToken.NULL) {
                    pdfUpload.displayName = reader.nextString();
                }else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pdfUpload;
    }

    @Override
    public DataModel.DataType getDataType() {
        return DataType.DocumentTypes;
    }

    public class UserPdfDocCommand implements Serializable {

        public String filename;

    }

}
