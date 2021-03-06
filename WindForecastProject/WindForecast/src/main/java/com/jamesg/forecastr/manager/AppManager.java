package com.jamesg.forecastr.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.ForecastrApplication;
import com.jamesg.forecastr.utils.Logger;
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
        ((ForecastrApplication) context).inject(this);
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