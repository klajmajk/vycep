package cz.cvut.fit.klimaada.vycep.rest;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Queue;

import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;

/**
 * Created by novot_000 on 25. 1. 2015.
 */
public class MyResponseListener implements Response.Listener<JSONObject> {
    private DrinkRecord record;
    private Queue<DrinkRecord> queue;
    public MyResponseListener(DrinkRecord record, Queue<DrinkRecord> queue) {
        this.record = record;
        this.queue = queue;
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Log.d("MyResponseListener", "Drinkrecord sent: " + queue.remove(record));
    }
}
