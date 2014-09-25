package cz.cvut.fit.klimaada.vycep.rest;



	import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

import cz.cvut.fit.klimaada.vycep.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;

	public class ConsumerGetterTask extends AsyncTask<Void, Void, Consumer> {
		private Context mContext;
		private ProgressDialog dialog;
		private URI uri;

		public ConsumerGetterTask(Context context, URI uri) {
			super();
			this.mContext = context;
			this.uri = uri;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = new ProgressDialog(mContext);
			dialog.setMessage("Probíhá stahování informací o kumpánovi");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Consumer doInBackground(Void...voids) {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet(uri);
			httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
			httpRequest.setHeader("Accept", "application/json; charset=utf-8");
			Gson gson = new GsonBuilder().setDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss").create();			

			try {
				HttpResponse response = client.execute(httpRequest);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {

					StringBuilder builder = new StringBuilder();
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					Log.d("Getter", "Goes to parser: " + builder.toString());
					return gson.fromJson(builder.toString(), Consumer.class);					
				} else {
					Log.e("Getter", "Status code: "+statusCode);
				}
			} catch (IOException e) {
				Log.e("Getter", e.getMessage());

			}

			return null;
		}

		@Override
		protected void onPostExecute(Consumer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result== null) {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						mContext);
				dialogBuilder.setMessage("Zkontrolujte pøipojení k serveru")
						.setTitle("Chyba pøipojení");
				AlertDialog alertDialog = dialogBuilder.create();
				alertDialog.show();
			} else {
				Controller.getInstanceOf().consumerRecieved(result);
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}


