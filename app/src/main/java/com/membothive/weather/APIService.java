package com.membothive.weather;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pranay Pradhan on 11-06-2015.
 */
public class APIService {

    private final String LOG_TAG = APIService.class.getSimpleName();
    private NetworkService networkService;
    private URL url;
    private Context context;
    private ArrayAdapter<String> mForecastAdapter;
    private FetchWeatherData fetchWeatherData;


    public APIService( ArrayAdapter<String> mForecastAdapter) {
        context= App.context;
        this.mForecastAdapter=mForecastAdapter;

    }
    public void fetchWeatherDataTask(String... params){
        fetchWeatherData=new FetchWeatherData();
        fetchWeatherData.execute(params);
    }
    /* The date/time conversion code is going to be moved outside the asynctask later,
       * so for convenience we're breaking it out into its own method now.
       */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        Log.d(LOG_TAG,forecastJsonStr);
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultantString = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultantString[i] = day + " - " + description + " - " + highAndLow;
        }
        return resultantString;

    }

    private  class  FetchWeatherData extends AsyncTask<String,Void,String[]>{

       // private ProgressDialog progressDialog= new ProgressDialog(context);


      /*  protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait ...");
            progressDialog.show();

        }*/

        @Override
        protected String[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();
            try {
                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG,url.toString());
                networkService=new NetworkService(url);
                forecastJsonStr =networkService.sendGetRequest();
                return getWeatherDataFromJson(forecastJsonStr,numDays);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }catch (JSONException e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }

         @Override
         protected void onPostExecute(String[] result) {
             super.onPostExecute(result);
             if (result != null) {
                 mForecastAdapter.clear();
                 for(String dayForecastStr : result) {
                     mForecastAdapter.add(dayForecastStr);
                 }
                 // New data is back from the server.  Hooray!
             }
             /*if(progressDialog.isShowing()){
                 progressDialog.dismiss();
             }*/
         }
    }
}
