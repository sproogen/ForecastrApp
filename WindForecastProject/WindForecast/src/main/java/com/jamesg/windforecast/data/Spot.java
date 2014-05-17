package com.jamesg.windforecast.data;

import android.util.Log;
import java.text.DateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Spot {

    private String rawData;
    private String name;
    private int id;
    private int bs_id;
    private String latitude;
    private String longitude;
    private long updateTime;

    private String label;
    private int type = 0; // 0 = spot / 1 == header / 2 == other menu item
    private SunriseSunsetCalculation sunriseSunsetCalculation;

    private ArrayList<TimestampData> today = new ArrayList<TimestampData>();
    private ArrayList<TimestampData> tomorrow = new ArrayList<TimestampData>();
    private ArrayList<TimestampData> sevenDay = new ArrayList<TimestampData>();

    public Spot(String header, String type){
        this.label = header;
        if(type.equals("header")) this.type = 1;
        else this.type = 2;
    }

    public Spot(String name, int id){
        this.name = name;
        this.id = id;
        this.bs_id = 0;
        this.latitude = "";
        this.longitude = "";
        this.rawData = "";
        this.updateTime = 0;
    }

    public Spot(String name, int id, String rawData){
        this.name = name;
        this.id = id;
        this.bs_id = 0;
        this.latitude = "";
        this.longitude = "";
        this.rawData = rawData;
        this.updateTime = 0;
    }

    public Spot(String name, int id, String latitude, String longitude){
        this.name = name;
        this.id = id;
        this.bs_id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rawData = "";
        this.updateTime = 0;
    }

    public Spot(String name, int id, String latitude, String longitude, String rawData){
        this.name = name;
        this.id = id;
        this.bs_id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rawData = rawData;
        this.updateTime = 0;
    }

    public Spot(String name, int id, String latitude, String longitude, String rawData, Long updateTime){
        this.name = name;
        this.id = id;
        this.bs_id = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rawData = rawData;
        this.updateTime = updateTime;
    }

    public Spot(String name, int id, int bs_id, String latitude, String longitude, String rawData, Long updateTime){
        this.name = name;
        this.id = id;
        this.bs_id = bs_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rawData = rawData;
        this.updateTime = updateTime;
    }

    public int getType(){
        return type;
    }

    public void setRawData(String data){
        rawData = data;
    }

    public void setUpdateTime(Long date){ updateTime = date; }

    public String getName(){
        return name;
    }

    public String getLabel(){
        return label;
    }

    public int getId(){
        return id;
    }

    public int getBS_Id(){
        return bs_id;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getRawData(){
        try{
            if(rawData.equals("")) return null;
        }catch(NullPointerException e){
            return null;
        }
        return rawData;
    }

    public Long getUpdateTime(){ return updateTime; }

    public void parseRawData(){
        Log.e("WINDFINDER APP", "Parsing Raw Data - "+this.name);
        today = new ArrayList<TimestampData>();
        tomorrow = new ArrayList<TimestampData>();
        sevenDay = new ArrayList<TimestampData>();
        JSONArray todayJsonArray = null;
        JSONArray tomorrowJsonArray = null;
        JSONArray sevenDayJsonArray = null;
        JSONObject spotJsonArray = null;
        try{
            spotJsonArray = new JSONObject(rawData);
            JSONObject todayJson = spotJsonArray.getJSONObject("today");
            todayJsonArray = todayJson.getJSONArray("Rep");
            JSONObject tomorrowJson = spotJsonArray.getJSONObject("tomorrow");
            tomorrowJsonArray = tomorrowJson.getJSONArray("Rep");
            JSONObject sevenDayJson = spotJsonArray.getJSONObject("sevenDay");
            sevenDayJsonArray = sevenDayJson.getJSONArray("Rep");
        }catch(Exception e){
            Log.e("WINDFINDER APP", "Error with initial JSON Parse");
            Log.e("WINDFINDER APP", e.toString());
            return;
        }

        for(int i=0;i<todayJsonArray.length();i++){
            TimestampData thisInterval = new TimestampData();
            try{
                JSONObject timeJson = todayJsonArray.getJSONObject(i);

                thisInterval.time = (timeJson.getInt("$") / 60);

                thisInterval.windDirection = directionToDegree(timeJson.getString("D"));
                thisInterval.windSpeed = timeJson.getInt("S");
                thisInterval.windGust = timeJson.getInt("G");

                thisInterval.swellDirection = directionToDegree(timeJson.getString("swell-direction"));
                thisInterval.swellHeight = timeJson.getDouble("swell-height");
                thisInterval.swellPeriod = timeJson.getDouble("swell-period");

                thisInterval.weather = timeJson.getInt("W");
                thisInterval.temp = timeJson.getInt("T");
            }catch(Exception e){
                Log.e("WINDFINDER APP", "Error with getting data from today JSON");
                Log.e("WINDFINDER APP", e.toString());
                return;
            }
            today.add(thisInterval);
        }

        for(int i=0;i<tomorrowJsonArray.length();i++){
            TimestampData thisInterval = new TimestampData();
            try{
                JSONObject timeJson = tomorrowJsonArray.getJSONObject(i);

                thisInterval.time = (timeJson.getInt("$") / 60);

                thisInterval.windDirection = directionToDegree(timeJson.getString("D"));
                thisInterval.windSpeed = timeJson.getInt("S");
                thisInterval.windGust = timeJson.getInt("G");

                thisInterval.swellDirection = directionToDegree(timeJson.getString("swell-direction"));
                thisInterval.swellHeight = timeJson.getDouble("swell-height");
                thisInterval.swellPeriod = timeJson.getDouble("swell-period");

                thisInterval.weather = timeJson.getInt("W");
                thisInterval.temp = timeJson.getInt("T");
            }catch(Exception e){
                Log.e("WINDFINDER APP", "Error with getting data from tomorrow JSON");
                Log.e("WINDFINDER APP", e.toString());
                return;
            }
            tomorrow.add(thisInterval);
        }

        for(int i=0;i<sevenDayJsonArray.length();i++){
            TimestampData thisInterval = new TimestampData();
            try{
                JSONObject timeJson = sevenDayJsonArray.getJSONObject(i);

                thisInterval.windDirection = directionToDegree(timeJson.getString("D"));
                thisInterval.windSpeed = timeJson.getInt("S");
                thisInterval.windGust = timeJson.getInt("G");

                thisInterval.swellDirection = directionToDegree(timeJson.getString("swell-direction"));
                thisInterval.swellHeight = timeJson.getDouble("swell-height");
                thisInterval.swellPeriod = timeJson.getDouble("swell-period");

                thisInterval.weather = timeJson.getInt("W");
                thisInterval.temp = timeJson.getInt("T");
            }catch(Exception e){
                Log.e("WINDFINDER APP", "Error with getting data from tomorrow JSON");
                Log.e("WINDFINDER APP", e.toString());
                return;
            }
            sevenDay.add(thisInterval);
        }
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        try{
            sunriseSunsetCalculation = new SunriseSunsetCalculation(day, month,
                year, Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
            sunriseSunsetCalculation.calculateOfficialSunriseSunset();
        }catch(Exception e){}
    }

    public int directionToDegree(String direction){
        direction = direction.toUpperCase();
        int degrees[] = {180, 202, 225, 247, 270, 292, 315, 337, 0, 22, 45, 67, 90, 112, 135, 157};
        if(direction.equals("N")) return degrees[0];
        else if(direction.equals("NNE")) return degrees[1];
        else if(direction.equals("NE")) return degrees[2];
        else if(direction.equals("ENE")) return degrees[3];
        else if(direction.equals("E")) return degrees[4];
        else if(direction.equals("ESE")) return degrees[5];
        else if(direction.equals("SE")) return degrees[6];
        else if(direction.equals("SSE")) return degrees[7];
        else if(direction.equals("S")) return degrees[8];
        else if(direction.equals("SSW")) return degrees[9];
        else if(direction.equals("SW")) return degrees[10];
        else if(direction.equals("WSW")) return degrees[11];
        else if(direction.equals("W")) return degrees[12];
        else if(direction.equals("WNW")) return degrees[13];
        else if(direction.equals("NW")) return degrees[14];
        else if(direction.equals("NNW")) return degrees[15];
        return 0;
    }

    public TimestampData getTodayTimestamp(int interval){
        if(today.size() == 0) return null;
        TimestampData first =  today.get(0);
        int id = (interval - first.time)/3;
        if(id >= 0) return today.get((interval - first.time)/3);
        else return null;
    }

    public int getTodayTimestampCount(){
        return today.size();
    }

    public TimestampData getTomorrowTimestamp(int interval){
        if(tomorrow.size() == 0) return null;
        TimestampData first =  tomorrow.get(0);
        int id = (interval - first.time)/3;
        if(id >= 0) return tomorrow.get((interval - first.time)/3);
        else return null;
    }

    public int getTomorrowTimestampCount(){
        return tomorrow.size();
    }

    public TimestampData getSevenDayDay(int interval){
        if(sevenDay.size() == 0) return null;
        return sevenDay.get(interval);
    }

    public int getSevenDayDayCount(){
        return sevenDay.size();
    }

    public String getSunRise(){
        final DateFormat dateFormat = new SimpleDateFormat("hh:mm a zzz");
        try{
            return dateFormat.format(sunriseSunsetCalculation.getOfficialSunrise());
        }catch(Exception e){
            return "";
        }
    }

    public String getSunSet(){
        final DateFormat dateFormat = new SimpleDateFormat("hh:mm a zzz");
        try{
            return dateFormat.format(sunriseSunsetCalculation.getOfficialSunset());
        }catch(Exception e){
            return "";
        }
    }

    public Boolean hasSwell(){
        TimestampData t;
        try{
            t = today.get(0);
        }catch(Exception e){
            return false;
        }
        if(t.swellPeriod == -1) return false;
        return true;
    }
}
