package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;

import org.apache.http.client.methods.HttpPut;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Keg;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class PutKegTask extends AbstractTask {
    private Keg keg;

    private BarrelState newState;
    private BarrelState oldState;

    public PutKegTask(URI uri, Context context, Keg keg, BarrelState newState) throws UnsupportedEncodingException {
        super(context);
        this.request = new HttpPut(uri);
        this.keg = keg;
        this.oldState = keg.getBarrelState();
        this.newState = newState;
        this.keg.setBarrelState(newState);
        this.request = new HttpPut(uri);
        setRequestBody(gson.toJson(keg));
    }

    @Override
    public void onPostExecute() {
        if (result == null) {
            showErrorDialog();
            keg.setBarrelState(oldState);
        } else Controller.getInstanceOf().getBarrelController().barrelStateChanged(keg, context);
    }

    @Override
    public String getName() {
        return "PutKegTask";
    }
}
