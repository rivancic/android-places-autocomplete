package com.developerhouse.googleplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    public ArrayList<String> loadSuggestions(String location) {

        return mPlaceAPI.autocomplete(location);
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }

    public void setResultList(ArrayList<String> resultList) {
        this.resultList = resultList;
    }

    private ArrayList<String> resultList;
    private Context mContext;
    private int mResource;
    private PlaceAPI mPlaceAPI = new PlaceAPI();

    public PlaceAPI getmPlaceAPI() {

        return mPlaceAPI;
    }

    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        //if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position != (resultList.size() - 1))
            view = inflater.inflate(R.layout.autocomplete_item, null);
        else
            view = inflater.inflate(R.layout.autocomplete_google_logo, null);
        //}
        //else {
        //    view = convertView;
        //}

        if (position != (resultList.size() - 1)) {
            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
            //  System.out.println(resultList.get(position).addressStreet);
            autocompleteTextView.setText(resultList.get(position));
        } else {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // not sure what to do <img class="emoji" draggable="false" alt="😀" src="http://s.w.org/images/core/emoji/72x72/1f600.png">
        }

        return view;
    }

    @Override
    public int getCount() {
        // Last item will be the footer
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };

        return filter;
    }

    public boolean hasResults() {

        return resultList.size() > 0;
    }

    public void addFooter(String footer) {

        resultList.add("footer");
    }

    public String getPlaceIdAt(int position) {

        return mPlaceAPI.getListAddress().get(position).placesId;
    }
}