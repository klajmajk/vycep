package cz.cvut.fit.klimaada.vycep.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cz.cvut.fit.klimaada.vycep.MainActivity;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;

public class BarrelUpdateTask extends AsyncTask<Barrel, Void, Boolean> {
	private Context mContext;
	private ProgressDialog dialog;
	private URI uri;

	public BarrelUpdateTask(Context context, URI uri) {
		super();
		this.mContext = context;
		this.uri = uri;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("Probíhá naèítání");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Barrel...barrels) {
		HttpClient client = new DefaultHttpClient();
		HttpPut httpRequest = new HttpPut(uri);
		httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
		httpRequest.setHeader("Accept", "application/json; charset=utf-8");
		Gson gson = new GsonBuilder().setDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss").create();
		try {
			httpRequest.setEntity(new StringEntity(gson.toJson(barrels[0])));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			HttpResponse response = client.execute(httpRequest);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 204) {
				return true;
			} else {
				Log.e("Getter", "Status code: "+statusCode);
			}
		} catch (IOException e) {
			Log.e("Getter", e.getMessage());

		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		// TODO Auto-generated method stub
		super.onPostExecute(success);
		if (success == false) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					mContext);
			dialogBuilder.setMessage("Zkontrolujte pøipojení k serveru")
					.setTitle("Chyba pøipojení");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		} 
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
