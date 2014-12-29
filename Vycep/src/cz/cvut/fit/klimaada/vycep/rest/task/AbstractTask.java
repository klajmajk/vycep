package cz.cvut.fit.klimaada.vycep.rest.task;

import android.app.AlertDialog;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

import cz.cvut.fit.klimaada.vycep.rest.MyAsyncTask;

/**
 * Created by Adam on 26. 12. 2014.
 */
public abstract class AbstractTask {
    protected HttpRequestBase request;
    protected Context context;
    protected String result;
    protected final Gson gson = new GsonBuilder().setDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss").create();


    protected AbstractTask(Context context) {
        this.context = context;
    }

    public HttpRequestBase getRequest() {
        return request;
    }

    public void setRequest(HttpRequestBase request) {
        this.request = request;
    }

    public void setRequestBody(String body) throws UnsupportedEncodingException {
        if (request instanceof HttpEntityEnclosingRequestBase)
            ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body, HTTP.UTF_8));
    }

    public abstract void onPostExecute();

    public abstract String getName();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (result == null) result = null;
        else this.result = new String(result);
    }

    protected void showErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage("Zkontrolujte připojení k serveru").setTitle(
                "Chyba připojení");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void execute() {
        new MyAsyncTask(context).execute(this);
    }
}
