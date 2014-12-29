package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.apache.http.client.methods.HttpGet;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Keg;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class GetBarrelsTask extends AbstractTask {

    public GetBarrelsTask(URI uri, Context context) {
        super(context);
        this.request = new HttpGet(uri);
    }

    @Override
    public void onPostExecute() {
        Log.d("Getter", "Goes to parser: " + this.getResult());
        Date date = new Date();
        Type collectionType = new TypeToken<List<Keg>>() {
        }.getType();
        List<Keg> kegs = gson.fromJson(result,
                collectionType);
        if (result == null) {
            showErrorDialog();
        } else {
            Controller.getInstanceOf().setBarrels(kegs);
            if (context != null) ((IMyActivity) context).notifyKegsReceived(kegs);
        }
    }

    @Override
    public String getName() {
        return "BarrelsTask";
    }
}
