package cz.cvut.fit.klimaada.vycep.rest.old;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.Beer;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.rest.ICallback;

class BarrelKindGetterTask extends AsyncTask<URI, Void, List<Keg>> {
    private static final int TIMEOUT = 5;
    private Context mContext;
    private ProgressDialog dialog;

    public BarrelKindGetterTask(Context context) {
        super();
        this.mContext = context;


    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Prob�h� stahov�n� typ� sud�");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected List<Keg> doInBackground(URI... uris) {
        // TODO Auto-generated method stub
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
        Log.d("BarrelTypeGetter", "uri: " + uris[0]);
        HttpGet httpGet = new HttpGet(uris[0]);
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");

        try {
            HttpResponse response = client.execute(httpGet);
            Log.d("barrelsGetter", response.toString() + " :status: " + response.getStatusLine());
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 204) return new ArrayList<Keg>();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.d("Getter", "Goes to parser: " + builder.toString());
                Gson gson = new GsonBuilder().setDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ssZ").create();
                Date date = new Date();
                //Log.v("Getter","gson date test"+gson.toJson(date));
                Type collectionType = new TypeToken<List<Beer>>() {
                }.getType();
                List<Keg> barrels = gson.fromJson(builder.toString(),
                        collectionType);
                // Log.v("Getter", "Your data: " + barrels); //response data
                return barrels;
            } else {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.e("BarrelsGetterTask", "code: " + statusCode + "\nbody: " + line);
            }
        } catch (IOException e) {
            Log.e("BarrelsGetterTask", "IO exception");


        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Keg> result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            dialogBuilder.setMessage("Zkontrolujte p�ipojen� k serveru").setTitle(
                    "Chyba p�ipojen�");
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else {
            ((ICallback) mContext).doAfterReceive(result);
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}