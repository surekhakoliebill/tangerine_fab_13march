package com.aryagami.restapis;

/**
 * Created by cloudkompare on 26/4/15.
 */

import android.util.Log;

import com.aryagami.data.Constants;
import com.aryagami.util.UserSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpHandler {
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int POST_MULTIPART = 3;
    public static KeyStore trusted = null;


    public HttpHandler() {

    }

    /*public static void loadKey(Context context) {
        // Get an instance of the Bouncy Castle KeyStore format
        try {
            trusted = KeyStore.getInstance("BKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        // Get the raw resource, which contains the keystore with
        // your trusted certificates (root and any intermediate certs)
        InputStream in = context.getResources().openRawResource(R.raw.smartcache); //name of your keystore file here
        try {
            trusted.load(in, "smartcache123".toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
                                  String params) {
        return doMakeServiceCall(url, method, params, null);
    }


    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
                                  String params, String data) {
        return doMakeServiceCall(url, method, params, data);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */

    public String makeServicePdfCall(String url, int method,
                                     String params, String params1, String data) {
        return doMakeServicePdfCall(url, method, params, params1, data);
    }


    public class MyHttpClient extends DefaultHttpClient {
        public MyHttpClient() {

        }
        public MyHttpClient(HttpParams params) {
            super(params);
        }

        public MyHttpClient(ClientConnectionManager httpConnectionManager, HttpParams params) {
            super(httpConnectionManager, params);
        }

        @Override
        protected ClientConnectionManager createClientConnectionManager() {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            // Register for port 443 our SSLSocketFactory with our keystore
            // to the ConnectionManager
            registry.register(new Scheme("https", newSslSocketFactory(), 443));
            return new SingleClientConnManager(getParams(), registry);
        }

        public class MySSLSocketFactory extends SSLSocketFactory {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
                super(truststore);

                TrustManager tm = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };

                sslContext.init(null, new TrustManager[] { tm }, null);
            }

            @Override
            public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
                return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
            }

            @Override
            public Socket createSocket() throws IOException {
                return sslContext.getSocketFactory().createSocket();
            }
        }
        private SSLSocketFactory newSslSocketFactory() {
            try {

                // Pass the keystore to the SSLSocketFactory. The factory is responsible
                // for the verification of the server certificate.
                SSLSocketFactory sf = new MySSLSocketFactory(trusted);
                // Hostname verification from certificate
                // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // This can be changed to less stricter verifiers, according to need
                return sf;
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }

    public String doMakeServiceCall(String url, int method,
                                    String params, String data) {
        try {
            // http client
            DefaultHttpClient httpClient = new MyHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            response = null;

            // Checking http request method type
            if (method == POST) {
                CookieStore cookieStore = new BasicCookieStore();
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                HttpPost httpPost = new HttpPost(url);

                if(UserSession.getSessionKey()!= null){
                    httpPost.setHeader(new BasicHeader("SESSIONID",UserSession.getSessionKey()));
                }else{
                    httpPost.setHeader(new BasicHeader("SESSIONID",Constants.sessionId));

                }

               /* if(Constants.API_KEY != null){
                    httpPost.setHeader(new BasicHeader("API_KEY",  Constants.API_KEY));
                }*/

                //    httpPost.setHeader(new BasicHeader("SESSIONID", UserSession.getSessionKey()));

                // adding post params
                if (params != null) {
                    StringEntity se = new StringEntity(params);
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    httpPost.setEntity(se);
                }

                httpResponse = httpClient.execute(httpPost, localContext);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    url += "?" + params;
                }
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                if(UserSession.getSessionKey()!= null){
                    httpGet.setHeader(new BasicHeader("SESSIONID",UserSession.getSessionKey()));
                }else{
                    httpGet.setHeader(new BasicHeader("SESSIONID",Constants.sessionId));

                }
/*

                if(Constants.API_KEY != null){
                    httpGet.setHeader(new BasicHeader("API_KEY",  Constants.API_KEY));
                }
*/

                httpResponse = httpClient.execute(httpGet);

            } else if (method == POST_MULTIPART) {
                String fileName = params;
                // String fileformat = params1;
                HttpPost httpPost = new HttpPost(url);

                //httpPost.setHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                if(UserSession.getSessionKey()!= null){
                    httpPost.setHeader(new BasicHeader("SESSIONID",UserSession.getSessionKey()));
                }else{
                    httpPost.setHeader(new BasicHeader("SESSIONID",Constants.sessionId));
                }


                ByteArrayBody bab = new ByteArrayBody(data.getBytes(), fileName);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                // reqEntity.addPart("fileformat", new StringBody(fileformat));
                reqEntity.addPart("filename", new StringBody(fileName));
                reqEntity.addPart("file", bab);

                httpPost.setEntity(reqEntity);
                httpResponse = httpClient.execute(httpPost);
            }
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
            } else {
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                response = "Error-Code: " + httpResponse.getStatusLine().getStatusCode() + " $ " + response;
            }
        } catch (UnsupportedEncodingException e) {
            response = "Error-Code: " + "Unsupported Encoding"+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (ClientProtocolException e) {
            response = "Error-Code: " + "Client Protocol Exception" +"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (IOException e) {
            response = "Error-Code: " + "Unknown IO Exception" +"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (Exception e) {
            response = "Error-Code: " + "Unhandled exception " + e.getClass().getName()+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (Error e) {
            response = "Error-Code: " + "Unhandled error " + e.getClass().getName()+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        }

        return response;
    }


    public String doMakeServicePdfCall(String url, int method,
                                       String params, String params1, String data) {
        try {
            // http client
            DefaultHttpClient httpClient = new MyHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            response = null;

            if (method == POST_MULTIPART) {
                String fileName = params;
                String fileformat = params1;
                HttpPost httpPost = new HttpPost(url);

                ByteArrayBody bab = new ByteArrayBody(data.getBytes(), fileName);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("fileformat", new StringBody(fileformat));
                reqEntity.addPart("filename", new StringBody(fileName));
                reqEntity.addPart("file", bab);

                httpPost.setEntity(reqEntity);
                httpResponse = httpClient.execute(httpPost);
            }
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
            } else {
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                response = "Error-Code: " + httpResponse.getStatusLine().getStatusCode() + " $ " + response;
            }
        } catch (UnsupportedEncodingException e) {
            response = "Error-Code: " + "Unsupported Encoding"+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (ClientProtocolException e) {
            response = "Error-Code: " + "Client Protocol Exception"+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (IOException e) {
            response = "Error-Code: " + "Unknown IO Exception"+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (Exception e) {
            response = "Error-Code: " + "Unhandled exception " + e.getClass().getName()+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        } catch (Error e) {
            response = "Error-Code: " + "Unhandled error " + e.getClass().getName()+"Cause: "+e.getCause()+"Message: "+e.getMessage()+"Stack: " + Log.getStackTraceString(e);
        }

        return response;
    }


}