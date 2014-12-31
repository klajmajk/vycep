package cz.cvut.fit.klimaada.vycep.rest.task;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.apache.http.client.methods.HttpGet;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.NewBarrelActivity;
import cz.cvut.fit.klimaada.vycep.entity.Beer;

/**
 * Created by Adam on 31. 12. 2014.
 */
public class GetBeersByBreweryTask extends AbstractTask {
    public GetBeersByBreweryTask(URI uri, Context context) {
        super(context);
        this.request = new HttpGet(uri);
    }

    @Override
    public void onPostExecute() {
        Log.d("Getter", "Goes to parser: " + this.getResult());
        Date date = new Date();
        Type collectionType = new TypeToken<List<Beer>>() {
        }.getType();
        List<Beer> beers = gson.fromJson(result,
                collectionType);
        if (result == null) {
            showErrorDialog();
        } else {
            ((NewBarrelActivity) context).doAfterBeersReceive(beers);
        }
    }

    @Override
    public String getName() {
        return "GetBeersByBrewery";
    }
}
