package cz.cvut.fit.klimaada.vycep.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cz.cvut.fit.klimaada.vycep.Controller;
import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.MainActivity;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;

class BarrelsGetterTask extends AsyncTask<URI, Void, List<Barrel>> {
	private Context mContext;
	private ProgressDialog dialog;

	public BarrelsGetterTask(Context context) {
		super();
		this.mContext = context;
		
		
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = new ProgressDialog(mContext);
        dialog.setMessage("Prob�h� na��t�n�");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
		super.onPreExecute();
	}

	@Override
	protected List<Barrel> doInBackground(URI... uris) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uris[0]);
		httpGet.setHeader("Content-Type", "application/json");
		httpGet.setHeader("Accept", "application/json");

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
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
						"yyyy-MM-dd'T'HH:mm:ss").create();
				Date date = new Date();
				// Log.v("Getter","gson date test"+gson.toJson(date));
				Type collectionType = new TypeToken<List<Barrel>>() {
				}.getType();
				List<Barrel> barrels = gson.fromJson(builder.toString(),
						collectionType);
				// Log.v("Getter", "Your data: " + barrels); //response data
				return barrels;
			} else {
				Log.e("Getter", "Failed to download file");
			}
		} catch (IOException e) {
			Log.e("Getter", e.getMessage());			
			
			
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(List<Barrel> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result==null){
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
			dialogBuilder.setMessage("Zkontrolujte p�ipojen� k serveru").setTitle(
					"Chyba p�ipojen�");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		}else{
			Controller.getInstanceOf().setBarrels(result);
			((IMyActivity) mContext).notifyBarrelsReceived(result);
		}
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}
}