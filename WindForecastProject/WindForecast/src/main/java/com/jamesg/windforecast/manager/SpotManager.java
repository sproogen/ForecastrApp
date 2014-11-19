package com.jamesg.windforecast.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.common.collect.Lists;
import com.jamesg.windforecast.data.Spot;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by James on 17/10/2014.
 */
public class SpotManager extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "spotsTable";

    // Contacts table name
    private static final String TABLE_SPOTS = "spots";

    // Contacts Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_ID = "id";
    private static final String KEY_DATA = "data";
    private static final String KEY_LAT = "lattitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_UPDATE_TIME = "updateTime";

    private ArrayList<Spot> all_spots = new ArrayList<Spot>();
    private Spot search_temp_spot = null;

    //String metoffice_base_url = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/";
    String base_url = "http://jwgmedia.co.uk/windAppAPI/getForcastData.php";

    Context context;

    public SpotManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SPOTS + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_ID + " INTEGER,"
                + KEY_LAT + " TEXT,"
                + KEY_LONG + " TEXT,"
                + KEY_DATA + " TEXT,"
                + KEY_UPDATE_TIME + " NUMERIC" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addSpot(Spot spot) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, spot.getName());
        values.put(KEY_ID, spot.getId());
        values.put(KEY_LAT, spot.getLatitude());
        values.put(KEY_LONG, spot.getLongitude());
        values.put(KEY_DATA, spot.getRawData());
        values.put(KEY_UPDATE_TIME, spot.getUpdateTime());

        // Inserting Row
        try{
            db.insert(TABLE_SPOTS, null, values);
            db.close(); // Closing database connection
        }catch(NullPointerException e){
            //DO Nothing
        }
        Log.d("WINDFINDER APP", "DATA ADD Spot - " + spot.getName());
    }

    public void searchSpot(Spot spot){
        Log.d("WINDFINDER APP", "DATA Search Spot - "+spot.getName());
        search_temp_spot = spot;
    }

    public boolean isFavourite(String name){
        for(Spot s : all_spots){
            if(s.getName().equals(name))return true;
        }
        return false;
    }

    // Getting single spot
    public Spot getSpot(String name) {
        for(Spot s : all_spots){
            if(s.getName().equals(name))return s;
        }
        if(search_temp_spot != null) {
            if (search_temp_spot.getName().equals(name)) return search_temp_spot;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.query(TABLE_SPOTS, new String[] {
                            KEY_NAME, KEY_ID, KEY_LAT, KEY_LONG, KEY_DATA, KEY_UPDATE_TIME }, KEY_NAME + "=?",
                    new String[] { name }, null, null, null, null);
        }catch(NullPointerException e){
            //Do Nothing
        }
        if (cursor != null && cursor.moveToFirst()) {

            Spot spot = new Spot(cursor.getString(0),cursor.getInt(1),
                    cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getLong(5));
            cursor.close();
            return spot;
        }
        try{
            cursor.close();
        }catch(NullPointerException e){
            //Do Nothing
        }
        return null;
    }

    // Getting single contact
    public Spot getSpot(int i) {
        return all_spots.get(i);
    }

    // Getting single contact
    public Boolean loadedSpot(String name) {
        for(int i=0; i<all_spots.size();i++){
            Spot s = all_spots.get(i);
            Log.d("WINDFINDER APP", i+" - "+name+" "+s.getName()+ " "+all_spots.size());
            try{
                if(s.getName().equals(name))return true;
            }catch(Exception e){
                //Do Nothing
            }
        }
        return false;
    }

    // Getting single contact
    public void parseSpotsData() {
        for(int i=0; i<all_spots.size();i++){
            Spot s = all_spots.get(i);
            s.parseRawData();
            Log.d("WINDFINDER APP", "Parse Spot - "+s.getName());
            all_spots.set(i, s);
        }
    }

    // Getting single contact
    public void parseSpotData(String name) {
        for(int i=0; i<all_spots.size();i++){
            Spot s = all_spots.get(i);
            if(s.getName().equals(name)){
                s.parseRawData();
                Log.d("WINDFINDER APP", "Parse Spot - "+s.getName());
                all_spots.set(i, s);
            }
        }
    }

    // Getting All Contacts
    public List<Spot> getAllSpots(int parse) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SPOTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(selectQuery, null);
        }catch(NullPointerException e){
            //Do Nothing
        }
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Spot spot = new Spot(cursor.getString(0),cursor.getInt(1),
                        cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getLong(5));
                // Adding contact to list
                if(!loadedSpot(spot.getName())){
                    all_spots.add(spot);
                    Log.d("WINDFINDER APP", "ADD Spot - "+spot.getName());
                }
            } while (cursor.moveToNext());
        }
        if(parse == 1){
            parseSpotsData();
        }
        // return contact list
        return all_spots;
    }

    // Updating single contact
    public int updateSpot(Spot spot) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, spot.getName());
        values.put(KEY_ID, spot.getId());
        values.put(KEY_LAT, spot.getLatitude());
        values.put(KEY_LONG, spot.getLongitude());
        values.put(KEY_DATA, spot.getRawData());
        values.put(KEY_UPDATE_TIME, spot.getUpdateTime());

        // updating row
        int result = -1;
        try{
            result = db.update(TABLE_SPOTS, values, KEY_NAME + " = ?",
                    new String[] { spot.getName() });
        }catch(NullPointerException e){
            //Do Nothing
        }
        int i = 0;
        for(Spot s : all_spots){
            if(s.getName().equals(spot.getName())){
                all_spots.set(i, spot);
                break;
            }
            i++;
        }
        return result;
    }

    // Deleting single contact
    public void deleteSpot(Spot spot) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLE_SPOTS, KEY_NAME + " = ?",
                    new String[] { spot.getName() });
            db.close();
        }catch(NullPointerException e){
            //Do Nothing
        }
        int i = 0;
        for(Spot s : all_spots){
            if(s.getName().equals(spot.getName())){
                all_spots.remove(i);
                break;
            }
            i++;
        }

    }


    // Getting contacts Count
    public int getSpotsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SPOTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(countQuery, null);
            cursor.close();
        }catch(NullPointerException e){
            //Do Nothing
        }
        // return count
        try{
            return cursor.getCount();
        }catch(NullPointerException e){
            return 0;
        }
    }

    public interface SpotDataTaskCallback {
        public void spotUpdated(Spot spot);
    }

    class AllSpotsDataTaskCallback implements SpotDataTaskCallback{
        @Override
        public void spotUpdated(Spot spot) {
            updateSpot(spot);
            Intent intent = new Intent();
            intent.setAction("com.jamesg.windforecast.UPDATE_DATA");
            //intent.putExtra("url",uri.toString());
            context.sendBroadcast(intent);
        }
    }

    public void getDataForSpot(Spot spot, SpotDataTaskCallback callback){
        ArrayList<Spot> spots = new ArrayList<Spot>();
        spots.add(spot);
        GetSpotDataTask getSpotDataTask = new GetSpotDataTask(true, callback);
        getSpotDataTask.execute(spots);
    }


    public void checkForUpdates(boolean force){
        List<Spot> spots = getAllSpots(0);
        AllSpotsDataTaskCallback callback = new AllSpotsDataTaskCallback();
        GetSpotDataTask getSpotDataTask = new GetSpotDataTask(force, callback);
        getSpotDataTask.execute(spots);
    }

    private class GetSpotDataTask extends AsyncTask<List<Spot>, Spot, String> {

        boolean forceUpdate;
        SpotDataTaskCallback callback;

        GetSpotDataTask(boolean forceUpdate, SpotDataTaskCallback callback){
            this.forceUpdate = forceUpdate;
            this.callback = callback;
        }

        protected String doInBackground(List<Spot>... spotsList) {
            List<Spot> spots = spotsList[0];
            for(Spot s : spots){

                Log.d("WINDFINDER APP", "Checking Spot - "+s.getName());
                if(s.getRawData() == null || (System.currentTimeMillis()-s.getUpdateTime()) > 3600000 || forceUpdate){ //3600000
                    Spot updated = get_data_for_location(s);
                    updated.parseRawData();
                    publishProgress(updated);
                }else{
                    SimpleDateFormat formatter = new SimpleDateFormat("D");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(s.getUpdateTime());
                    String updatedDay = formatter.format(calendar.getTime());
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    String nowDay = formatter.format(calendar.getTime());
                    if(!updatedDay.equals(nowDay)){
                        Spot updated = get_data_for_location(s);
                        updated.parseRawData();
                        publishProgress(updated);
                    }
                }
            }
            return null;
        }

        protected void onProgressUpdate(Spot... spot) {
            if(spot[0] != null) {
                Log.d("WINDFINDER APP", "Updated Spot - " + spot[0].getName());
                callback.spotUpdated(spot[0]);
            }
        }

        protected void onPostExecute(String result) {
            Log.d("WINDFINDER APP", "Updated Finished ");

        }
    }


    public Spot get_data_for_location(Spot current){

        Log.d("WINDFINDER APP", "Start of get data");

        String latitude = null; String longitude = null;

        if(current == null){
            return null;
        }

        String request = base_url;
        request += "?id=" + current.getId();
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
            return null;
        } catch (IOException e) {
            Log.e("WINDFINDER APP", e.toString());
            return null;
        }
        try{
            JSONObject rawDataJson = new JSONObject(responseString);
            latitude = rawDataJson.getString("lat");
            longitude = rawDataJson.getString("lon");
        }catch(Exception e){
            Log.e("WINDFINDER APP", "Error with initial JSON Parse");
            Log.e("WINDFINDER APP", e.toString());
            return null;
        }

        current.setRawData(responseString);
        current.setLatitude(latitude);
        current.setLongitude(longitude);
        current.setUpdateTime(System.currentTimeMillis());
        return current;
    }
}