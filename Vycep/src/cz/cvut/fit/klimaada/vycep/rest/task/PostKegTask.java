package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import cz.cvut.fit.klimaada.vycep.NewBarrelActivity;
import cz.cvut.fit.klimaada.vycep.entity.Keg;

/**
 * Created by Adam on 31. 12. 2014.
 */
public class PostKegTask extends AbstractTask {
    public PostKegTask(URI uri, Keg keg, Context context) throws UnsupportedEncodingException {
        super(context);
        this.request = new HttpPost(uri);
        Log.d("PUT_KEG_TASK", "uri:" + uri.getPath() + " body: " + gson.toJson(keg));
        setRequestBody(gson.toJson(keg));
    }

    @Override
    public void onPostExecute() {
        if (result == null) {
            showErrorDialog();
        } else ((NewBarrelActivity) context).kegAdded();
    }

    @Override
    public String getName() {
        return "PutKegTask";
    }
}
