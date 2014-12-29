package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;

import org.apache.http.client.methods.HttpGet;

import java.net.URI;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.User;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class GetUserTask extends AbstractTask {

    public GetUserTask(URI uri, Context context) {
        super(context);
        this.request = new HttpGet(uri);
    }

    @Override
    public void onPostExecute() {
        User user = gson.fromJson(result, User.class);
        if (user == null) {
            showErrorDialog();
        } else {
            Controller.getInstanceOf().getNFCController().pouringConsumerRecieved(user);
        }
    }

    @Override
    public String getName() {
        return "GetUserTask";
    }
}
