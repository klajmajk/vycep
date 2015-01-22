package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Tap;

/**
 * Created by Adam on 30. 12. 2014.
 */
public class GetTapTask extends AbstractTask {
    public GetTapTask(URI uri, Context context) {
        super(context);
        this.request = new HttpGet(uri);
    }

    @Override
    public void onPostExecute() {
        Tap tap = gson.fromJson(result, Tap.class);
        if (tap == null) {
            showErrorDialog();
        } else {
            Controller.getInstanceOf().getTapController().refreshTap(tap);

        }
    }

    @Override
    public String getName() {
        return "GetTapTask";
    }
}
