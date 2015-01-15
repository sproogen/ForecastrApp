package com.jamesg.forecastr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.jamesg.forecastr.base.BaseFragment;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.manager.AppManager;
import com.jamesg.forecastr.manager.SpotManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DrawerFragment extends BaseFragment {

    @Inject
    SpotManager spotManager;

    @Inject
    AppManager appManager;

    @Inject
    Bus bus;

    private DrawMenuAdapter adapter;
    private SearchMenuAdapter searchAdapter;
    private String openSpot = "";
    private int selected = 0;

    private ListView listView;
    ProgressBarCircularIndeterminate searchProgress;
    private ListView searchList;
    private ImageView clearButton;
    private EditText searchBox;

    String search_url = "SearchSpots.php";
    String search_data = "";
    ArrayList<Spot> all_spots;
    Boolean searching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if(mListener.openSpot().equals("")){
            closeSpot();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list, null);
        listView = (ListView)view.findViewById(R.id.list);
        searchProgress = (ProgressBarCircularIndeterminate) view.findViewById(R.id.searchProgress);
        searchList = (ListView)view.findViewById(R.id.searchList);
        clearButton = (ImageView)view.findViewById(R.id.clearButton);

        adapter = new DrawMenuAdapter(getActivity(), spotManager.getAllSpots(0));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(null);
        listView.setItemChecked(0, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Spot item = adapter.getItem(position);
                if (position == 0) {
                    if (getActivity() instanceof MainActivity) {
                        mListener.allSpots(true);
                        //mListener.toggle();
                    }
                    selected = position;
                } else if (item.getType() == 2) {
                    if (item.getLabel().equals("Check for updates")) {
                        if (getActivity() instanceof MainActivity) {
                            spotManager.checkForUpdates(true);
                            mListener.toggle();
                        }
                        listView.setItemChecked(selected, true);
                    } else if (item.getLabel().equals("About")) {
                        if (getActivity() instanceof MainActivity) {
                            AboutFragment aboutFragment = new AboutFragment();
                            mListener.transitionToFragment(aboutFragment, MainActivity.ABOUT_FRAGMENT, true);
                            mListener.toggle();
                        }
                        selected = position;
                    }else if (item.getLabel().equals("App Update Available")) {
                        appManager.updateCheck();
                        listView.setItemChecked(selected, true);
                    }
                } else if (item.getType() == 0) {
                    //listView.setItemChecked(position, true);
                    selected = position;
                    if (getActivity() instanceof MainActivity) {
                        mListener.loadSpot(item.getName(), 1);
                    }
                }
            }
        });
        searchBox = (EditText)view.findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                int length = s.length();
                if(before == 0 && length >= 1){
                    listView.setVisibility(View.GONE);
                    searchList.setVisibility(View.VISIBLE);
                    searchList.clearChoices();
                    clearButton.setVisibility(View.VISIBLE);

                }else if(before >= 1 && length == 0){
                    listView.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.GONE);
                    clearButton.setVisibility(View.GONE);
                    searchProgress.setVisibility(View.GONE);
                    try {
                        searchAdapter.setData(new ArrayList<Spot>());
                    }catch(Exception e){
                        //DO NOTHING
                    }
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
        return view;
	}

    public void updateStarted(){
        adapter.setUpdating(true);
        int updatingPos = adapter.getUpdatePotision();
        if(updatingPos >= listView.getFirstVisiblePosition() && updatingPos <= listView.getLastVisiblePosition()){
            View v = listView.getChildAt(updatingPos);
            ProgressBarCircularIndeterminate updatingStatus = (ProgressBarCircularIndeterminate) v.findViewById(R.id.updatingStatus);
            updatingStatus.setVisibility(View.VISIBLE);
        }
    }

    public void updateFinished(){
        adapter.setUpdating(false);
        int updatingPos = adapter.getUpdatePotision();
        if(updatingPos >= listView.getFirstVisiblePosition() && updatingPos <= listView.getLastVisiblePosition()){
            View v = listView.getChildAt(updatingPos);
            ProgressBarCircularIndeterminate updatingStatus = (ProgressBarCircularIndeterminate) v.findViewById(R.id.updatingStatus);
            updatingStatus.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getMessage(String s) {
        //Logger.d("BUS MESSAGE drawerFragment - "+s);
        if(s.equals("Update Started")){
            try {
                updateStarted();
            }catch(Exception e){
                //DO NOTHING
            }
        }else if(s.equals("Update Finished")){
            try {
                updateFinished();
            }catch(Exception e){
                //DO NOTHING
            }
        }else if(s.equals("NewAppAvailable")){
            refreshSpots();
        }
    }

    public void dataSetUpdated(){
        adapter.notifyDataSetChanged();
    }

    public void performSearch(String search, int before){
        if(search.length() == 1 && before == 0){
            searchProgress.setVisibility(View.VISIBLE);
            getSpotsFromWeb(search);
        }else{
            if(!searching) {
                updateSearchResults(search);
            }
        }
    }

    public void getSpotsFromWeb(String search){
        final String searchTerm = search;
        Thread searchThread = new Thread(new Runnable() {

            public void run() {
                Log.d("WINDFINDER APP", "Search - " + searchTerm);
                String request = getString(R.string.base_url)+search_url;
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
        if(search.equals("")) {
            spots = parseSearchSpots();

            all_spots = spots;
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
        searchProgress.setVisibility(View.GONE);
        if(searchAdapter == null) {
            searchAdapter = new SearchMenuAdapter(getActivity(), spots);
            searchList.setAdapter(searchAdapter);
            searchList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            searchList.setDivider(null);
            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    Spot item = searchAdapter.getItem(position);
                    searchList.setItemChecked(position, true);
                    selected = -1;
                    mListener.loadSearchSpot(item.getName(), item.getId());
                }
            });
        }else{
            searchAdapter.setData(spots);
        }
    }

    public void clearSearch(){
        if(searchBox.getText().length() >= 1){
            listView.setVisibility(View.VISIBLE);
            searchAdapter.setData(new ArrayList<Spot>());
            spotManager.searchSpot(null);
            all_spots = null;
            searchList.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            searchProgress.setVisibility(View.GONE);
            searchBox.setText("");
        }
    }

    public void setSpot(String name){
        //closeSpot();
        this.openSpot = name;
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
        adapter.setData(spotManager.getAllSpots(0));
    }

    public void closeSpot(){
        this.openSpot = "";
        selected = 0;
        adapter.closeSpot();
        listView.setItemChecked(0, true);
    }

	public class DrawMenuAdapter extends ArrayAdapter<Spot> {

        private String openSpot = "";
        private Boolean updating = false;

		public DrawMenuAdapter(Context context, List data) {
			super(context, 0, data);
		}

        public void setData(List data){
            //this.clear();
            //this.addAll(data);
            this.notifyDataSetChanged();
        }

        public int getCount(){
            int extra = 5;
            if(appManager.updateAvailable()) extra += 1;
            return super.getCount()+extra;
        }

        public int getSettingsHeaderPotision(){
            int extra = 3;
            if(appManager.updateAvailable()) extra += 1;
            return getCount()-extra;
        }

        public int getUpdatePotision(){
            int extra = 2;
            if(appManager.updateAvailable()) extra += 1;
            return getCount()-extra;
        }

        public int getAboutPotision(){
            int extra = 1;
            if(appManager.updateAvailable()) extra += 1;
            return getCount()-extra;
        }

        public int getAppUpdatePotision(){
            return getCount()-1;
        }

        public void setUpdating(boolean updating){
            this.updating = updating;
        }

        public Spot getItem(int position){
            if(position == 0){
                return new Spot("All Spots","menu");
            }else if(position == 1){
                return new Spot("Favourites","header");
            }else if(position == getSettingsHeaderPotision()){
                return new Spot("Settings","header");
            }else if(position == getUpdatePotision()){
                return new Spot("Check for updates","menu");
            }else if(position == getAboutPotision()) {
                return new Spot("About", "menu");
            }else if(appManager.updateAvailable() && position == getAppUpdatePotision()){
                return new Spot("App Update Available", "menu");
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
                if(getItem(position).getLabel().equals("Check for updates") && updating) {
                    ProgressBarCircularIndeterminate updatingStatus = (ProgressBarCircularIndeterminate) convertView.findViewById(R.id.updatingStatus);
                    updatingStatus.setVisibility(View.VISIBLE);
                }
                if(getItem(position).getLabel().equals("App Update Available")){
                    convertView.setBackgroundResource(R.drawable.fragment_listselector_update);
                }
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
