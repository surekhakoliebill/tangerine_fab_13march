package com.aryagami.data;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class PdfDocumentData implements Serializable {

    public String docType;
    public String pdfRwaData;
    public String displayName;
    public Uri imageData;
    public String docFiles;


    public static List<PdfDocumentData> getFinalDocsList() {
        return finalDocsList;
    }

    public static void setFinalDocsList(List<PdfDocumentData> finalDocsList) {
        PdfDocumentData.finalDocsList = finalDocsList;
    }

    public static List<PdfDocumentData> finalDocsList = null;

    public static List<PdfDocumentData> getStaffDocsList() {
        return staffDocsList;
    }

    public static void setStaffDocsList(List<PdfDocumentData> staffDocsList) {
        PdfDocumentData.staffDocsList = staffDocsList;
    }

    public static List<PdfDocumentData> staffDocsList = null;

}
