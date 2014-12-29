package cz.cvut.fit.klimaada.vycep.rest.old;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Keg;

public class UpdateBarrelTask extends AsyncTask<Void, Void, Boolean> {
	private static final int TIMEOUT = 5;
	private Context mContext;
	private ProgressDialog dialog;
	private URI uri;
	private int responseStatus;
	private String responseBody;
    private Keg keg;
    private BarrelState newState;
	private BarrelState oldState;

    public UpdateBarrelTask(Context context, URI uri, Keg keg, BarrelState newState) {
        super();
		this.mContext = context;
		this.uri = uri;
        this.keg = keg;
        this.newState = newState;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("Prov�d� se zm�na stavu sudu");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void ...voids) {
		
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
		StringBuilder builder = new StringBuilder();

		try {
			HttpPut httpRequest = new HttpPut(uri);
			httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
			httpRequest.setHeader("Accept", "application/json; charset=utf-8");
			Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ").create();
            keg.setBarrelState(newState);
            httpRequest.setEntity(new StringEntity(gson.toJson(keg), HTTP.UTF_8));
            HttpResponse response = client.execute(httpRequest);
			StatusLine statusLine = response.getStatusLine();
			responseStatus = statusLine.getStatusCode();
			if (response.getEntity() != null) {
				InputStream content = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));

				while ((responseBody = reader.readLine()) != null) {
					builder.append(responseBody);
				}
			}
			if ((responseStatus >= 200)&&(responseStatus <= 220)) {
				return true;
			} else {
				Log.e("Response", "Status code: " + responseStatus + " body: "
						+ responseBody);
			}
		} catch (IOException e) {
			Log.e("RequestTask", "IOMessage");

		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (success == false) {
            keg.setBarrelState(oldState);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					mContext);
			dialogBuilder.setMessage(
					"Zkontrolujte p�ipojen� k serveru: " + responseStatus
							+ " : " + responseBody).setTitle("Chyba p�ipojen�");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		}else{
            Controller.getInstanceOf().getBarrelController().barrelStateChanged(keg, mContext);
        }
		if (dialog.isShowing()) {
			dialog.dismiss();
			Log.d("RequestTask", "calling dialog dismis");
		}
	}

}
