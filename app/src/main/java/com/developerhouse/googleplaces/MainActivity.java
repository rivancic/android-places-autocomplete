package com.developerhouse.googleplaces;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();
    EditText streetEt;
    EditText postalCodeEt;
    EditText countryEt;
    TextView cityEt;
    TextView provinceEt;
    /**
     * UI thread handling autocomplete updates.
     */
    HandlerThread mHandlerThread;
    /**
     * Thread handler for making google api requests in the background.
     * It is set in the TextWatcher.
     */
    Handler backgroundThreadHandler;
    private PlaceAPI mPlaceAPI = new PlaceAPI();
    private PlacesTextWatcher placesTextWatcher;

    public MainActivity() {
        // Required empty public constructor
        if (backgroundThreadHandler == null) {
            // Initialize and start the HandlerThread which is basically a Thread with a Looper
            // attached (hence a MessageQueue)
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            // Initialize the Handler
            backgroundThreadHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        streetEt = (EditText) findViewById(R.id.street);
        postalCodeEt = (EditText) findViewById(R.id.PostalCode);
        countryEt = (EditText) findViewById(R.id.Country);
        cityEt = (TextView) findViewById(R.id.city);
        provinceEt = (TextView) findViewById(R.id.province);
        placesTextWatcher = new PlacesTextWatcher();
        countryEt.addTextChangedListener(placesTextWatcher);
    }

    private void displayAddress(Address address) {

        backgroundThreadHandler.removeCallbacksAndMessages(null);
        countryEt.removeTextChangedListener(placesTextWatcher);
        cityEt.setText(address.city);
        provinceEt.setText(address.province);
        countryEt.addTextChangedListener(placesTextWatcher);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    class PlacesTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            final String value = charSequence.toString();
            // Remove all callbacks and messages
            backgroundThreadHandler.removeCallbacksAndMessages(null);
            if (streetEt.getText().toString().length() > 0 && postalCodeEt.getText().toString()
                    .length() > 0 && countryEt.getText().toString().length() > 0) {
                // Now add a new one
                backgroundThreadHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Background thread
                        Log.d(TAG, "Suggestion Called");
                        final List<Address> listAddresses = mPlaceAPI.autocomplete(streetEt.getText()
                                        .toString(),
                                postalCodeEt.getText().toString(), countryEt.getText().toString());
                        if (listAddresses != null && listAddresses.size() > 0) {
                            PlacesDetailAPI placeDetail = new PlacesDetailAPI();
                            final Address address = placeDetail.autocomplete(listAddresses.get(0).placesId);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    displayAddress(address);
                                }
                            });
                        }
                    }
                }, 1000);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
