package co.edu.unal.reto10_webservices;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class getDataTask extends AsyncTask<String,Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL("https://www.datos.gov.co/resource/hied-2f87.json?bcoestado=Activo");
            //url = new URL("https://www.datos.gov.co/resource/hied-2f87.json?$where=UPPER(bconombre)%20like%20%27%25"+strings[0]+"%25%27AND%20UPPER(bconomloc)%20like%20%27%25"+strings[1]+"%25%27");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */ );
            urlConnection.setConnectTimeout(15000 /* milliseconds */ );
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            //System.out.println("JSON: " + jsonString);

            JSONArray jArray = new JSONArray(jsonString);
            return jArray;

        } catch (MalformedURLException | JSONException | ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
