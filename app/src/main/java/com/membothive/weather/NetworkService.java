package com.membothive.weather;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Pranay Pradhan on 11-06-2015.
 */
public class NetworkService {

    public static enum RequestMethod
    {
        GET,
        POST,
        PUT,
        DELETE
    }
    private final String LOG_TAG = NetworkService.class.getSimpleName();
    //Private variables
    private HashMap<String, String > params;
    private HashMap<String, String > headers;
    private RequestMethod method;
    private URL url;
    private HttpURLConnection urlConnection = null;
    private int responseCode;



    private String responseMessage;
    private String errorMessage;
    private String content;

    // getter and setter methods
    public URL getUrl() {
        return url;
    }
    public int getResponseCode() {
        return responseCode;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public void setUrl(URL url) {
        this.url = url;
    }

    public NetworkService(URL url) {
        this.url = url;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public String sendGetRequest(){
        try{
            disableConnectionReuseIfNecessary();
            urlConnection=(HttpURLConnection)this.url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            responseCode=urlConnection.getResponseCode();

            responseMessage=urlConnection.getResponseMessage();
            Log.d(LOG_TAG,responseMessage);
            content=convertStreamToString(urlConnection.getInputStream());
            Log.d(LOG_TAG,content);
        }
        catch (IOException e){
            e.printStackTrace();
            errorMessage=convertStreamToString(urlConnection.getErrorStream());
            Log.e(LOG_TAG,errorMessage,e);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    return content;
    }
    public void sendPostRequest(){

    }
    public void sendPutRequest(){

    }
    public void sendDeleteRequest(){

    }

    public void AddParam(String key, String value) {
        params.put(key,value);
    }

    public void AddHeader(String key, String value){
        headers.put(key,value);
    }

    private static String convertStreamToString(InputStream  inputStream) {

        if(inputStream==null){
            return  null;
        }
        StringBuffer buffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try{
            while ((line = bufferedReader.readLine()) != null) {
                 // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                 // But it does make debugging a *lot* easier if you print out the completed
                 // buffer for debugging.
                 buffer.append(line).append("\n");
            }
            return buffer.toString();
        }catch (IOException e){
            e.printStackTrace();

        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ioexeption",e.getMessage(),e);
            }
        }
        return null;
    }


    private void disableConnectionReuseIfNecessary() {
    // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

}
