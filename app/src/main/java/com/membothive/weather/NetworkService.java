package com.membothive.weather;

import java.io.InputStream;
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

    private HashMap<String, String > params;
    private HashMap<String, String > headers;
    private RequestMethod method;
    private String url;
    public void sendGetRequest(){

    }
    public void sendPostRequest(){

    }
    public void sendPutRequest(){

    }
    public void sendDeleteRequest(){

    }

    public void AddParam(String name, String value)
    {

    }

    public void AddHeader(String name, String value)
    {

    }
    private static String convertStreamToString(InputStream is) {
        return null;
    }

}
