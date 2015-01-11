package com.jamesg.windforecast.manager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.jamesg.windforecast.MainActivity;
import com.jamesg.windforecast.R;
import com.jamesg.windforecast.WindFinderApplication;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.utils.Logger;
import com.squareup.otto.Bus;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by James on 17/10/2014.
 */
public class AppManager {

    @Inject
    Bus bus;

    private String checkAppURL = "appCheck.php";

    private boolean updateAvailable;
    private int latestVersion;
    private String versionName;
    private String sdkName;

    Context context;

    public AppManager(Context context) {
        this.context = context;
        ((WindFinderApplication) context).inject(this);
    }

    public void checkForUpdates(){
        CheckUpdateCallback callback = new CheckUpdateCallback();
        CheckUpdateTask getSpotDataTask = new CheckUpdateTask(callback);
        getSpotDataTask.execute();
    }

    public String getVersionName(){
        return versionName;
    }

    public boolean updateAvailable(){
        return updateAvailable;
    }

    public void updateCheck() {
        int currentVersion = getVersion();
        if (latestVersion > currentVersion) {
            updateAvailable = true;
            bus.post("NewAppAvailable");
            /*new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_action_cancel)
                    .setTitle("New Version of the app is available.")
                    .setMessage("Version " + versionName + " is available now. Would you like to update to this version now.")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Later", null)
                    .show();*/
        }
    }

    public interface CheckUpdateCallbackFunctions {
        public void updateFinished(JSONObject result);
    }

    class CheckUpdateCallback implements CheckUpdateCallbackFunctions{
        @Override
        public void updateFinished(JSONObject result) {
            if(result != null) {
                latestVersion = 0;
                versionName = "";
                try {
                    latestVersion = result.getInt("latest_version");
                    versionName = result.getString("latest_version_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateCheck();
            }else{
                Logger.d("Result = null");
            }
        }
    }

    private class CheckUpdateTask extends AsyncTask<Void, Void, JSONObject> {

        CheckUpdateCallback callback;

        CheckUpdateTask(CheckUpdateCallback callback){
            this.callback = callback;
        }

        protected JSONObject doInBackground(Void... voids) {

            JSONObject result = checkUpdateRequest();
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            callback.updateFinished(result);
        }
    }

    public JSONObject checkUpdateRequest(){
        JSONObject rawDataJson;

        String request = context.getString(R.string.base_url)+checkAppURL;
        request += "?appVersion=" + getVersion();

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
            return null;
        } catch (IOException e) {
            return null;
        }
        try{
            rawDataJson = new JSONObject(responseString);
        }catch(Exception e){
            return null;
        }
        return rawDataJson;
    }

    public int getVersion() {
        int v = 0;
        try {
            v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Huh? Really?
        }
        return v;
    }
}