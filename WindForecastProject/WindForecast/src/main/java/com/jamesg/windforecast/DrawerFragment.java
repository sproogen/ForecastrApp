package com.jamesg.windforecast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.data.TimestampData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DrawerFragment extends Fragment {

    private DrawMenuAdapter adapter;
    private SearchMenuAdapter searchAdapter;
    private String openSpot = "";
    private int selected = 0;

    String search_url = "http://jwgmedia.co.uk/windAppAPI/SearchSpots.php";
    String search_data = "";
    ArrayList<Spot> all_spots;
    Boolean searching = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, null);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        final ListView listView = (ListView)getView().findViewById(R.id.list);
		adapter = new DrawMenuAdapter(getActivity(), MainActivity.data.getAllSpots(0));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(null);
        listView.setItemChecked(0, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                selected = position;
                Spot item = adapter.getItem(position);
                if (position == 0) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).closeSpot(false);
                        ((MainActivity) getActivity()).toggle();
                    }
                } else if (item.getType() == 2) {
                    if (item.getLabel().equals("Check for updates")) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).checkForUpdates(true);
                            ((MainActivity) getActivity()).toggle();
                        }
                    }else if(item.getLabel().equals("About")) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).about();
                            ((MainActivity) getActivity()).toggle();
                        }
                    }
                } else if (item.getType() == 0) {
                    listView.setItemChecked(position, true);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadSpot(item.getName(), 1);
                    }
                }
            }
        });
        final ListView searchList = (ListView)getView().findViewById(R.id.searchList);
        final ImageView clearButton = (ImageView)getView().findViewById(R.id.clearButton);
        EditText searchBox = (EditText)getView().findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                int length = s.length();
                if(before == 0 && length >= 1){
                    listView.setVisibility(View.GONE);
                    searchList.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.VISIBLE);

                }else if(before >= 1 && length == 0){
                    listView.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.GONE);
                    clearButton.setVisibility(View.GONE);
                    searchAdapter.setData(new ArrayList<Spot>());
                    all_spots = null;
                }
                if(length >= 1){
                    performSearch(s.toString(), before);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearSearch();
            }
        });
    }

    public void performSearch(String search, int before){
        if(search.length() == 1 && before == 0){
            getSpotsFromWeb(search);
        }else{
            if(searching == false) {
                updateSearchResults(search);
            }
        }
    }

    public void getSpotsFromWeb(String search){
        final String searchTerm = search;
        Thread searchThread = new Thread(new Runnable() {

            public void run() {
                Log.d("WINDFINDER APP", "Search - " + searchTerm);
                String request = search_url;
                request += "?search=" + searchTerm;
                Log.d("WINDFINDER APP", request);

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(request));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    Log.e("WINDFINDER APP", e.toString());
                    return;
                } catch (IOException e) {
                    Log.e("WINDFINDER APP", e.toString());
                    return;
                }
                /*try{
                    JSONObject rawSearchJson = new JSONObject(responseString);
                }catch(Exception e){
                    Log.e("WINDFINDER APP", "Error with search JSON Parse");
                    Log.e("WINDFINDER APP", e.toString());
                    return;
                }*/

                search_data = responseString;

                threadMsg("finished");
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            private final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    Log.d("WINDFINDER APP", "Search Successful");
                    updateSearchResults("");
                    searching = false;
                }
            };
        });
        searching = true;
        searchThread.start();
    }

    public ArrayList<Spot> parseSearchSpots(){
        ArrayList<Spot> searchSpots = new ArrayList<Spot>();

        JSONObject locationObject = null;
        JSONArray locationArray = null;
        try{
            locationObject = new JSONObject(search_data);
            locationArray = locationObject.getJSONArray("Locations");
        }catch(Exception e){
            Log.e("WINDFINDER APP", "Error with search JSON Parse");
            Log.e("WINDFINDER APP", e.toString());
            return null;
        }
        for(int i=0;i<locationArray.length();i++){
            try{
                JSONObject searchSpot = locationArray.getJSONObject(i);
                searchSpots.add(new Spot(searchSpot.getString("name"),searchSpot.getInt("id")));
            }catch(Exception e){
                Log.e("WINDFINDER APP", "Error with getting data from search JSON");
                Log.e("WINDFINDER APP", e.toString());
                return null;
            }
        }
        return searchSpots;
    }

    public void updateSearchResults(String search){
        ArrayList<Spot> spots;
        if(search == "") {
            spots = parseSearchSpots();
            all_spots = spots;
            EditText searchBox = (EditText)getView().findViewById(R.id.searchBox);
            if(searchBox.getText().length() > 1){
                search = searchBox.getText().toString();
                spots = new ArrayList<Spot>();
                if(all_spots != null){
                    for (Spot spot : all_spots) {
                        if(spot.getName().toLowerCase().startsWith(search.toLowerCase())){
                            spots.add(spot);
                        }
                    }
                }
            }
        }else{
            spots = new ArrayList<Spot>();
            if(all_spots != null){
                for (Spot spot : all_spots) {
                    if(spot.getName().toLowerCase().startsWith(search.toLowerCase())){
                        spots.add(spot);
                    }
                }
            }
        }

        ListView searchListView = (ListView)getView().findViewById(R.id.searchList);
        final ListView listView = (ListView)getView().findViewById(R.id.list);
        if(searchAdapter == null) {
            searchAdapter = new SearchMenuAdapter(getActivity(), spots);
            searchListView.setAdapter(searchAdapter);
            searchListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            searchListView.setDivider(null);
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    Spot item = searchAdapter.getItem(position);
                    if (getActivity() instanceof MainActivity) {
                        listView.setItemChecked(position, true);
                        selected = -1;
                        ((MainActivity) getActivity()).loadSearchSpot(item.getName(), item.getId());
                    }
                }
            });
        }else{
            searchAdapter.setData(spots);
        }
    }

    public void clearSearch(){
        ListView listView = (ListView)getView().findViewById(R.id.list);
        ListView searchList = (ListView)getView().findViewById(R.id.searchList);
        ImageView clearButton = (ImageView)getView().findViewById(R.id.clearButton);
        EditText searchBox = (EditText)getView().findViewById(R.id.searchBox);
        if(searchBox.getText().length() >= 1){
            listView.setVisibility(View.VISIBLE);
            searchAdapter.setData(new ArrayList<Spot>());
            all_spots = null;
            searchList.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            searchBox.setText("");
        }
    }

    public void setSpot(String name){
        //closeSpot();
        this.openSpot = name;
        ListView listView = (ListView)getView().findViewById(R.id.list);
        for(int i=0;i<listView.getCount();i++){
            if(adapter.getItem(i).getType() == 0){
                if(adapter.getItem(i).getName().equals(name)){
                    listView.setItemChecked(i, true);
                    selected = i;
                    break;
                }
            }
        }
        adapter.setSpot(name);
    }

    public void refreshSpots(){
        adapter.setData(MainActivity.data.getAllSpots(0));
    }

    public void closeSpot(){
        this.openSpot = "";
        selected = 0;
        adapter.closeSpot();
        ListView listView = (ListView)getView().findViewById(R.id.list);
        /*for(int i=0;i<listView.getCount();i++){
            listView.setItemChecked(i, false);
        }*/
        listView.setItemChecked(0, true);
    }

	public class DrawMenuAdapter extends ArrayAdapter<Spot> {

        private String openSpot = "";

		public DrawMenuAdapter(Context context, List data) {
			super(context, 0, data);
		}

        public void setData(List data){
            //this.clear();
            //this.addAll(data);
            this.notifyDataSetChanged();
        }

        public int getCount(){
            return super.getCount()+5;
        }

        public Spot getItem(int position){
            if(position == 0){
                return new Spot("All Spots","menu");
            }else if(position == 1){
                return new Spot("Favourites","header");
            }else if(position == getCount()-3){
                return new Spot("Settings","header");
            /*}else if(position == getCount()-3){
                return new Spot("App Settings","menu");*/
            }else if(position == getCount()-2){
                return new Spot("Check for updates","menu");
            }else if(position == getCount()-1){
                return new Spot("About","menu");
            }else{
                return super.getItem(position-2);
            }
        }

		public View getView(int position, View convertView, ViewGroup parent) {

			if (getItem(position).getType() == 1) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_header, null);
                TextView title = (TextView) convertView.findViewById(R.id.header);
                title.setText(getItem(position).getLabel());
            }else if(getItem(position).getType() == 2){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).getLabel());
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spot, null);
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                final Spot s = getItem(position);
                title.setText(s.getName());
                ImageView delete = (ImageView)convertView.findViewById(R.id.deleteButton);
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_action_cancel)
                                .setTitle("Remove from favourites")
                                .setMessage("Are you sure you want to remove this spot from favourites.")
                                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).removeSpot(s);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
            }

			return convertView;
		}

        @Override
        public boolean isEnabled(int position) {

            if (getItem(position).getType() == 1) {
                return false;
            }else if(selected == position){
                return false;
            }else if(getItem(position).getType() == 2){
                if(getItem(position).getLabel().equals("App Settings")){
                    return false;
                }
            }
            /*(openSpot.equals("") && position == 0){
                return false;
            }else if (getItem(position).getType() == 2){
                return true;
            }else if(getItem(position).getName().equals(openSpot)){
                return false;
            }*/
            return true;
        }

        public void setSpot(String name){
            this.openSpot = name;
        }

        public void closeSpot(){
            this.openSpot = "";
        }
	}

    public class SearchMenuAdapter extends ArrayAdapter<Spot> {

        private String openSpot = "";

        public SearchMenuAdapter(Context context, List data) {
            super(context, 0, data);
        }

        /*public void clearSearchData(){
            this.clear();
            this.notifyDataSetChanged();
        }*/

        public void setData(List data){
            this.clear();
            this.addAll(data);
            this.notifyDataSetChanged();
        }

        public int getCount(){
            return super.getCount();
        }

        public Spot getItem(int position){
            return super.getItem(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_search_spot, null);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            final Spot s = getItem(position);
            title.setText(s.getName());

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public void setSpot(String name){
            this.openSpot = name;
        }

        public void closeSpot(){
            this.openSpot = "";
        }
    }
}
