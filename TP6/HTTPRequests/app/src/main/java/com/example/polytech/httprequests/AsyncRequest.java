package com.example.polytech.httprequests;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncRequest extends AsyncTask<String, String, String> {
    private final TextView tv;

    public AsyncRequest(TextView tv) {
        this.tv = tv;
    }

    private static String request(URL url) {
        HttpURLConnection connex = null;
        try {
            connex = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(connex.getInputStream());
            return readStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connex != null)
                connex.disconnect();
        }
        return "";
    }

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = is.read()) != -1)
            sb.append((char) i);
        return sb.toString();
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings.length < 1)
            return "NO URL";
        try {
            return request(new URL(strings[0]));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject obs = new JSONObject(s);
            GeoIP geo = new GeoIP(obs.getString("status"), obs.getString("query"), getMaybe(obs, String.class, "message"), getMaybe(obs, String.class, "country"), getMaybe(obs, String.class, "countryCode"), getMaybe(obs, String.class, "org"), getMaybe(obs, String.class, "regionName"), getMaybe(obs, String.class, "zip"), getMaybe(obs, String.class, "isp"), getMaybe(obs,Double.class,  "lat"), getMaybe(obs, Double.class,"lon"));
            tv.setText(geo.forView());
            tv.setBackgroundColor(geo.succeeded() ? Color.GREEN : Color.RED);
        } catch (JSONException e) {
            e.printStackTrace();
            tv.setText(e.getMessage());
        }
    }

    private <T> T getMaybe(JSONObject obs, Class<? extends T> klass, String country) throws JSONException {
        return obs.has(country) ? klass.cast(obs.get(country)) : null;
    }
}
