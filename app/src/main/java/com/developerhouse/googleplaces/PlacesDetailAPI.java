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
import java.util.ArrayList;

public class PlacesDetailAPI {

    private final String TAG = PlacesDetailAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCSvftaXQfxoM3BJdqCqlTSh4yMGHvZzLo";

    public Address autocomplete(String input) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        Address finalAddress = null;

        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&placeid=" + input);
            System.out.println(sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {

                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {

            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {

            Log.e(TAG, "Error connecting to Places API", e);
        } finally {

            if (conn != null) {

                conn.disconnect();
            }

        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject resultJsonArray = jsonObj.getJSONObject("result");

            // TODO: Parse also "route" and "street_number" and save them to finalAddress in field routeAndStreetNumber
            Log.d("Result address", resultJsonArray.toString());
            JSONArray addJsonArray = resultJsonArray.getJSONArray("address_components");

            // Extract the Place descriptions from the results
            finalAddress = new Address();
            for (int i = 0; i < addJsonArray.length(); i++) {

                JSONArray typesArray = addJsonArray.getJSONObject(i).getJSONArray("types");
                for (int j = 0; j < typesArray.length(); j++) {

                    if (typesArray.getString(j).equals("locality")) {

                        finalAddress.city = addJsonArray.getJSONObject(i).getString("long_name");
                    } else if (typesArray.getString(j).equals("country")) {

                        finalAddress.country = addJsonArray.getJSONObject(i).getString("long_name");
                    } else if (typesArray.getString(j).equals("postal_code")) {

                        finalAddress.postalCode = addJsonArray.getJSONObject(i).getString("long_name");
                    }

                }

                finalAddress.placesId = input;
            }

        } catch (JSONException e) {

            Log.e(TAG, "Cannot process JSON results", e);
        }

        return finalAddress;
    }

}
