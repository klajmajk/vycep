package cz.cvut.fit.klimaada.vycep.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.IStatusView;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.entity.Beer;
import cz.cvut.fit.klimaada.vycep.entity.Brewery;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;
import cz.cvut.fit.klimaada.vycep.rest.MyRestFacade;

public class Controller extends AbstractController {
    private static final String LOG_TAG = "CONTROLLER";
    private static final String PERSISTENT_TAPS = "Persistent taps";
    private static Controller instance;
    private IRestFacade myRestFacade;

    private TapController tapController;
    private NFCController nfcController;
    private ArduinoController arduinoController;

    public Controller(Model model) {
        super(model);
        myRestFacade = new MyRestFacade();
        tapController = new TapController(model, myRestFacade);
        arduinoController = new ArduinoController(model);
        nfcController = new NFCController(model, myRestFacade);

    }

    public static Controller getInstanceOf() {
        if (instance == null) {
            instance = new Controller(new Model());
        }
        return instance;
    }

    public void setView(IStatusView view) {
        this.view = view;
        tapController.setView(view);
        arduinoController.setView(view);
        nfcController.setView(view);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(view.getContext());
        // String taps = "";
        String server = sp.getString("serverAddress", "");
        //TODO předělat na více tapů
        int tapId = Integer.parseInt(sp.getString("tapId", "0"));
        try {
            model.setCalibration(Double.parseDouble(sp.getString("calibration",
                    "")));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            model.setCalibration(1);
        }
        myRestFacade.setServer(server);
        model.setTapId(tapId);


    }

    public void persist() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(view.getContext());
        String taps = new Gson().toJson(model.getTaps());
        sp.edit().putString(PERSISTENT_TAPS, taps).apply();
    }

    public void getBarrelsFromREST(Context context) {
        myRestFacade.getAllKegs(context);
    }

    public List<Keg> getBarrels() {
        return model.getKegs();
    }

    public void setBarrels(List<Keg> kegs) {
        model.setKegs(kegs);
    }

    public List<Tap> getTaps() {
        // TODO Auto-generated method stub
        return model.getTaps();
    }

    public void setServer(String server) {
        myRestFacade.setServer(server);

    }

    public void setCalibration(double calibration) {
        model.setCalibration(calibration);
    }

    public void getBreweries(Activity activity) {
        myRestFacade.getBreweries(activity);

    }

    public void newBarrels(Beer kind, int volume, int count,
                           double price, Context context) {
        List<Keg> kegs = new ArrayList<>();
        //TODO předělat že se dodělá až doběhne task
        for (int i = 0; i < count; i++) {
            // Objem musí být v mililitrech
            kegs.add(new Keg(new Date(), price, kind, volume * 1000));
        }
        myRestFacade.addNewKegs(kegs, context);
    }

    public TapController getTapController() {
        // TODO Auto-generated method stub
        return tapController;
    }

    public ArduinoController getArduinoController() {
        // TODO Auto-generated method stub
        return arduinoController;
    }

    public NFCController getNFCController() {
        // TODO Auto-generated method stub
        return nfcController;
    }

    public void setTapId(int i) {
        model.getTap(0).setId(i);
    }

    public void getBeersByBrewery(Brewery brewery, Activity activity) {

        myRestFacade.getBeersByBrewery(brewery, activity);

    }
}
