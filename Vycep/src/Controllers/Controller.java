package Controllers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.IStatusView;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.R.string;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelKind;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.exceptions.NotTappedException;
import cz.cvut.fit.klimaada.vycep.exceptions.UpdateErrorException;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;
import cz.cvut.fit.klimaada.vycep.rest.MyRestFacade;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class Controller extends AbstractController {
	private static final String LOG_TAG = "CONTROLLER";
	private static final String PERSISTENT_TAPS = "Persistent taps";
	private static Controller instance;
	private IRestFacade myRestFacade;

	private BarrelController barrelController;
	private NFCController nfcController;
	private ArduinoController arduinoController;

	public Controller(Model model) {
		super(model);
		myRestFacade = new MyRestFacade();
		barrelController = new BarrelController(model, myRestFacade);
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
		barrelController.setView(view);
		arduinoController.setView(view);
		nfcController.setView(view);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());
		String taps = sp.getString(PERSISTENT_TAPS, "");
		// String taps = "";
		String server = sp.getString("serverAddress", "");
		try {
			model.setCalibration(Double.parseDouble(sp.getString("calibration",
					"")));
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			model.setCalibration(1);
		}
		myRestFacade.setServer(server);
		if (taps != "") {
			Type listType = new TypeToken<ArrayList<Tap>>() {
			}.getType();
			model.setTaps((List<Tap>) new Gson().fromJson(taps, listType));
		}
	}

	public void persist() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());
		String taps = new Gson().toJson(model.getTaps());
		sp.edit().putString(PERSISTENT_TAPS, taps).apply();
	}

	public void getBarrelsFromREST(Context context) {
		myRestFacade.getAllBarrels(context);
	}

	public List<Barrel> getBarrels() {
		return model.getBarrels();
	}

	public void setBarrels(List<Barrel> barrels) {
		model.setBarrels(barrels);
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

	public void getBarrelKinds(Activity activity) {
		myRestFacade.getBarrelKinds(activity);

	}

	public void newBarrels(BarrelKind kind, int volume, int count,
			double price, Context context) {
		List<Barrel> barrels = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			// Objem musí být v mililitrech
			barrels.add(new Barrel(new Date(), price, kind, volume * 1000));
		}
		myRestFacade.addNewBarrels(barrels, context);
	}

	public BarrelController getBarrelController() {
		// TODO Auto-generated method stub
		return barrelController;
	}

	public ArduinoController getArduinoController() {
		// TODO Auto-generated method stub
		return arduinoController;
	}

	public NFCController getNFCController() {
		// TODO Auto-generated method stub
		return nfcController;
	}

}
