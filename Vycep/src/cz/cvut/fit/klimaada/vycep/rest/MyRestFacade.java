package cz.cvut.fit.klimaada.vycep.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Brewery;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.KegState;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.rest.task.AbstractTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetBarrelsTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetBeersByBreweryTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetBreweriesTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetTapTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetUserTask;
import cz.cvut.fit.klimaada.vycep.rest.task.PostKegTask;
import cz.cvut.fit.klimaada.vycep.rest.task.PutKegTask;

public class MyRestFacade implements IRestFacade {

    private static final String LOG_TAG = "MY_REST_FACADE";
    private String Server;
    private RequestQueue mRequestQueue;
    protected final Gson gson = new GsonBuilder().setDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss").create();

    public MyRestFacade() {

    }

    @Override
    public void getConsumer(int id, Context context) {

        try {
            Log.d(LOG_TAG, "getting user: " + Server + "user/" + id);
            AbstractTask task = new GetUserTask(new URI(Server + "user/" + id), context);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            Log.e(LOG_TAG, "Error in getting consumer ");
            e.printStackTrace();
        }
    }

    @Override
    public List<Keg> getAllKegs(Context context) {
        URI uri;
        try {
            uri = new URI(Server + "keg");
            AbstractTask task = new GetBarrelsTask(uri, context);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            Log.e(LOG_TAG, "Error in getting kegs ");
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void getBreweries(Context context) {
        try {
            AbstractTask task = new GetBreweriesTask(new URI(Server + "brewery/"), context);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void initQueue(Context context) {
        // Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();
    }


    @Override
    public void updateBarrel(Keg keg, Tap tap, KegState newState, Context context) {
        try {
            AbstractTask task = new PutKegTask(new URI(Server + "keg/" + keg.getId() + "/tap/" + tap.getId()), context, keg, newState);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void addDrinkRecord(DrinkRecord record, Context context) {
        if (mRequestQueue == null) initQueue(context);
        final Queue<DrinkRecord> drinkrecordQueue = Controller.getInstanceOf().getDrinkrecordQueue();
        drinkrecordQueue.offer(record);

        Log.d(LOG_TAG, "Přidávám drinkrecord: " + gson.toJson(drinkrecordQueue.peek()));
        Log.d(LOG_TAG, "queeuse size: " + drinkrecordQueue.size());
        int sentRequests = 0;
        while (isConnected(context) && (drinkrecordQueue.size() > sentRequests)) {
            Log.d(LOG_TAG, "while");
            final JSONObject jsonBody;
            try {
                jsonBody = new JSONObject(gson.toJson(drinkrecordQueue.peek()));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Server + "consumption/", jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(LOG_TAG, "Drinkrecord sent");
                                drinkrecordQueue.remove();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                                Log.d(LOG_TAG, error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");
                        params.put("Content-Type", "application/json");

                        return params;
                    }
                };
                mRequestQueue.add(request);
                Log.d(LOG_TAG, "req. Sent");
                sentRequests++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setServer(String server) {
        Log.d(LOG_TAG, "Setting server: " + server);
        Server = server;
    }

    @Override
    public void addNewKegs(List<Keg> kegs, Context context) {
        for (Keg keg : kegs) {
            try {
                AbstractTask task = new PostKegTask(new URI(Server + "keg/"), keg, context);
                task.execute();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteBarrel(Keg keg, Context context) {
        /*try {
            HttpDelete httpRequest = new HttpDelete(new URI(
                    Server + "barrel/" + keg.getId()));
            httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
			httpRequest.setHeader("Accept", "application/json; charset=utf-8");
            Log.d(LOG_TAG, "Deleting barrel: " + keg);
            AddTask task = new AddTask(context, (ICallback)context);
			task.execute(httpRequest);
			//if(!task.get())throw new UpdateErrorException("Chyba p�i updatu zaznam nebyl zm�n�n"); 
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/

    }

    @Override
    public void getTapById(int tapId, Context context) {
        try {
            AbstractTask task = new GetTapTask(new URI(Server + "tap/" + tapId), context);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void getBeersByBrewery(Brewery brewery, Context context) {
        try {
            AbstractTask task = new GetBeersByBreweryTask(new URI(Server + "brewery/" + brewery.getId() + "/beer"), context);
            task.execute();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
