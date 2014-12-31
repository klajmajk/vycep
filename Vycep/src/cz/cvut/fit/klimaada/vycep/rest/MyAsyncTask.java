package cz.cvut.fit.klimaada.vycep.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.cvut.fit.klimaada.vycep.rest.task.AbstractTask;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class MyAsyncTask extends AsyncTask<AbstractTask, Void, AbstractTask> {
    private static final int TIMEOUT = 5;
    private Context mContext;

    private ProgressDialog dialog;

    public MyAsyncTask(Context context) {
        super();
        this.mContext = context;


    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        System.out.println();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Probíhá komunikace se serverem");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected AbstractTask doInBackground(AbstractTask... task) {
        // TODO Auto-generated method stub
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
        HttpRequestBase request = task[0].getRequest();
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        try {

            Log.d("MY_ASYNC_TASK " + task[0].getName(), task[0].getRequest().getURI().getPath());
            HttpResponse response = client.execute(request);
            Log.d("MY_ASYNC_TASK " + task[0].getName(), response.toString() + " :status: " + response.getStatusLine());
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            task[0].setHttpResponceCode(statusCode);

            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            Log.d("MY_ASYNC_TASK " + task[0].getName(), "code: " + statusCode + "\nbody: " + builder.toString());

            if ((statusCode >= 200) && (statusCode < 300) && (statusCode != 200)) {
                task[0].setResult("");
            } else if (statusCode == 200) {

                Log.d(task[0].getName(), " : " + builder.toString());
                task[0].setResult(builder.toString());
            } else {

                task[0].setHttpErrMsg(builder.toString());
            }

        } catch (IOException e) {
            Log.e(task[0].getName(), "IO exception");


        }

        task[0].setResult(null);
        return task[0];
    }

    @Override
    protected void onPostExecute(AbstractTask task) {
        // TODO Auto-generated method stub
        super.onPostExecute(task);
        task.onPostExecute();

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
