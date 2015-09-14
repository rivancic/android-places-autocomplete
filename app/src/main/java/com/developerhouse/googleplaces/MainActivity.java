package com.developerhouse.googleplaces;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

@SuppressWarnings("Annotator")
public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();
    EditText cityEt;
    EditText countryEt;
    EditText postalCodeEt;
    AutoCompleteTextView autocompleteView;
    /**
     * UI thread handling autocomplete updates.
     */
    HandlerThread mHandlerThread;
    Handler uiThreadHandler;
    Handler backgroundThreadHandler;
    private static PlacesAutoCompleteAdapter autoCompleteAdapter;
    private PlacesTextWatcher placesTextWatcher;

    public MainActivity() {
        // Handler running on UI thread
        if (uiThreadHandler == null) {
            uiThreadHandler = new AutocompleteHandler();
        }
        // Required empty public constructor
        if (backgroundThreadHandler == null) {
            // Initialize and start the HandlerThread
            // which is basically a Thread with a Looper
            // attached (hence a MessageQueue)
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            // Initialize the Handler
            backgroundThreadHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    public static class AutocompleteHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                ArrayList<String> results = autoCompleteAdapter.getResultList();
                if (results != null && results.size() > 0) {
                    autoCompleteAdapter.notifyDataSetChanged();
                } else {
                    autoCompleteAdapter.notifyDataSetInvalidated();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEt = (EditText) findViewById(R.id.City);
        countryEt = (EditText) findViewById(R.id.Country);
        postalCodeEt = (EditText) findViewById(R.id.PostalCode);
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item));
        autoCompleteAdapter = (PlacesAutoCompleteAdapter) autocompleteView.getAdapter();
        placesTextWatcher = new PlacesTextWatcher();
        autocompleteView.addTextChangedListener(placesTextWatcher);
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                backgroundThreadHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        PlacesDetailAPI placeDetail = new PlacesDetailAPI();
                        final Address address = placeDetail.autocomplete(autoCompleteAdapter.getPlaceIdAt(position));
                        uiThreadHandler.post(new Runnable() {

                            @Override
                            public void run() {

                                displayAddress(address);
                            }
                        });
                    }
                });
            }
        });
    }

    private void displayAddress(Address address) {

        backgroundThreadHandler.removeCallbacksAndMessages(null);
        autocompleteView.removeTextChangedListener(placesTextWatcher);
        // TODO will be ok when the routAndStreetNumberWillBeParsed
        autocompleteView.setText(address.routeAndStreetNumber);
        cityEt.setText(address.city);
        countryEt.setText(address.country);
        postalCodeEt.setText(address.postalCode);
        autocompleteView.addTextChangedListener(placesTextWatcher);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        // Get rid of our Place API Handlers
        if (uiThreadHandler != null) {
            uiThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
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
            // Now add a new one
            backgroundThreadHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // Background thread
                    Log.d(TAG, "Suggestion Called");
                    //System.out.println(autoCompleteAdapter.mPlaceAPI.autocomplete(value)+"EE");
                    autoCompleteAdapter.setResultList(autoCompleteAdapter.loadSuggestions(value));
                    // Footer
                    if (autoCompleteAdapter.hasResults()) {
                        autoCompleteAdapter.addFooter("footer");
                    }
                    // Post to Main Thread
                    uiThreadHandler.sendEmptyMessage(1);
                }
            }, 1000);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
