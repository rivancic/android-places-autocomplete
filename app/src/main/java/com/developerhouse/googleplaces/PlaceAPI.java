package com.developerhouse.googleplaces;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Providing known location data and wanting to get the city and province.
 */
public class PlaceAPI {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCSvftaXQfxoM3BJdqCqlTSh4yMGHvZzLo";
    private final String TAG = PlaceAPI.class.getSimpleName();

    /**
     * Providing Street, Postal, Code and Country
     *
     * @param street
     * @param postalCode
     * @param country
     *
     * @return Want to get City and Province
     */
    public ArrayList<Address> autocomplete(String street, String postalCode, String country) {

        return autocomplete(street + ", " + postalCode + ", " + country);
    }

    public ArrayList<Address> autocomplete(String input) {

        ArrayList<Address> listAddress = new ArrayList<>();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&types=address");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return listAddress;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return listAddress;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            Log.d(TAG, jsonResults.toString());
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Address add = new Address();
                add.placesId = predsJsonArray.getJSONObject(i).getString("place_id");
                add.addressStreet = predsJsonArray.getJSONObject(i).getString("description");
                listAddress.add(add);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
        return listAddress;
    }
}