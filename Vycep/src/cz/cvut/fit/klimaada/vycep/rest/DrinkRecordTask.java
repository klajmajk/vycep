package cz.cvut.fit.klimaada.vycep.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DrinkRecordTask extends AsyncTask<HttpUriRequest, Void, Boolean> {
	private static final int TIMEOUT = 5;
	private Context mContext;
	private ProgressDialog dialog;
	private URI uri;
	private int responseStatus;
	private String responseBody;

	public DrinkRecordTask(Context context) {
		super();
		this.mContext = context;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("Probíhá odesílání konzumace");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(HttpUriRequest... request) {
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
		StringBuilder builder = new StringBuilder();

		try {
			HttpResponse response = client.execute(request[0]);
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
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					mContext);
			dialogBuilder.setMessage(
					"Zkontrolujte pøipojení k serveru: " + responseStatus
							+ " : " + responseBody).setTitle("Chyba pøipojení");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		}
		if (dialog.isShowing()) {
			dialog.dismiss();
			Log.d("RequestTask", "calling dialog dismis");
		}
	}
	

}
