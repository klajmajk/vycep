package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.methods.HttpPut;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.KegState;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class PutKegTask extends AbstractTask {
    private Keg keg;

    private KegState newState;
    private KegState oldState;

    public PutKegTask(URI uri, Context context, Keg keg, KegState newState) throws UnsupportedEncodingException {
        super(context);
        this.keg = keg;
        this.oldState = keg.getState();
        this.newState = newState;
        this.keg.setState(newState);
        this.request = new HttpPut(uri);
        Log.d("PUT_KEG_TASK", "uri:" + uri.getPath() + " body: " + gson.toJson(keg));
        setRequestBody(gson.toJson(keg));
    }

    @Override
    public void onPostExecute() {
        if (result == null) {
            showErrorDialog();
            keg.setState(oldState);
        } else Controller.getInstanceOf().getTapController().barrelStateChanged(keg, context);
    }

    @Override
    public String getName() {
        return "PutKegTask";
    }
}
