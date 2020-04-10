package com.aryagami.data;

/**
 * Created by aryagami on 29/06/16.
 */

public class Constants {

   // #Testing Server Url
   //public static final String serverURL = "http://10.20.15.3:4080/";
   //  public static final String serverURL = "http://41.217.232.48:4080/";

    // #Live Server Url
    //public static final String serverURL = "http://10.20.15.41/";
     public static final String serverURL = "http://41.217.232.48/";

    //for Pramod
   // public static final String serverURL = "http://41.217.232.92/";

    public static final String serviceUrl = serverURL + "/billing/";

    public static final String serviceUrlReport = serverURL + "billing_reports/";

    public static final String imagesUrl = serverURL + "/documents/";

    // to fetch doc Url from server
    public static final String ninImagesUrl = serverURL + "fetch_documents/documents/nin/";
    public static final String passportImagesUrl = serverURL + "fetch_documents/documents/passport/";
    public static final String visaImagesUrl = serverURL + "fetch_documents/documents/visa/";
    public static final String refugeeImagesUrl = serverURL + "fetch_documents/documents/refugee/";
    public static final String profileImagesUrl = serverURL + "fetch_documents/documents/profile/";
    public static final String activationImagesUrl = serverURL + "fetch_documents/documents/activation/";

    public static final int PRODUCT_PICTURE_FILE_PREFIX_TOKENS_MAX = 2;
    public final static String USERSESSIONINFO = "myPrefrnc";

    public static final String emailId = "Surekha.koli16@gmail.com;raj.tts009@gmail.com";
    public static final String sessionId = "0a8496a2-29a9-n-a889-e524c15cc642";
    public static final String subscriptionId = "667e069b-93a5-4c55-be29-f6e21def2c6f";

    public static final String case1 = "DIN";
    public static final String case2 = "tangerine_relation";
    public static final String case3 = "bank_statement";
    public static final String case4 = "MOA";
    public static final String case5 = "incorporation_certificate";
    public static final String case6 = "TIN";
    public static final String case7 = "vat_certificate";

}
