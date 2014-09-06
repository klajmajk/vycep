package cz.cvut.fit.klimaada.vycep;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.exceptions.NotTappedException;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;
import cz.cvut.fit.klimaada.vycep.rest.MyRestFacade;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class Controller {
	private static final String LOG_TAG = "CONTROLLER";
	private static final double CALIBRATION = 500;
	private static final String PERSISTENT_TAPS = "Persistent taps";
	private IStatusView view;
	private static Controller instance;
	private MyRestFacade myRestFacade;
	private List<Tap> taps;
	private List<Barrel> barrels;
	private Arduino arduino;
	private MediaPlayer player;

	public Controller() {
		super();
		myRestFacade = new MyRestFacade();
		taps = new ArrayList<>();
		taps.add(new Tap());
		arduino = new Arduino();

	}

	public static Controller getInstanceOf() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	public void cardDetected(String data) {
		// view.setVolumeText("0,00 L");
		int consumerId = Consumer.parseIdFromNFC(data);
		Consumer consumer = myRestFacade.getConsumer(consumerId,
				view.getContext());
		view.setStatusText(view.getContext().getString(R.string.pouring) + ": "
				+ consumer);
		// getBarrelsFromREST();

		Log.d(LOG_TAG, "sending open");
		view.getContext().sendBroadcast(Arduino.sendOpen());
		// TODO upravit pro vice kohoutu
		Tap tap = taps.get(0);
		tap.setActive(true);
		tap.setActiveConsumer(consumer);
	}

	public void cardRemoved() {
		// view.setVolumeText("0,00 L");
		view.setStatusText(view.getContext().getString(
				R.string.waiting_for_card));
		Log.d(LOG_TAG, "sending close");
		view.getContext().sendBroadcast(Arduino.sendClose());
		// TODO upravit pro vice kohoutu
		Tap tap = taps.get(0);
		if (tap.isActive() == true) {
			myRestFacade.addDrinkRecord(new DrinkRecord(tap.getActivePoured(),
					tap.getActiveConsumer(), tap.getBarrel(), new Date()), view
					.getContext());

			tap.setActiveConsumer(null);
			tap.setActivePoured(0);
		}
		tap.setActive(false);

	}

	public void setView(IStatusView view) {
		this.view = view;
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());
		String taps = sp.getString(PERSISTENT_TAPS, "");
		String server = sp.getString("serverAddress", "");
		myRestFacade.setServer(server);
		if (taps != "") {
			Type listType = new TypeToken<ArrayList<Tap>>() {
			}.getType();
			this.taps = new Gson().fromJson(taps, listType);
		}

	}

	public void persist(){
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());
		
		String taps = new Gson().toJson(this.taps);
		sp.edit().putString(PERSISTENT_TAPS, taps)
				.apply();
	}

	public void serialDataReceived(Intent intent) {
		// TODO upravit pro vice kohoutu
		double poured = arduino.getPoured(intent) / CALIBRATION;
		Tap tap = taps.get(0);
		tap.addPoured(poured);
		if (taps.get(0).isActive()) {
			tap.setActivePoured(poured + tap.getActivePoured());
		} else {
			Log.d(LOG_TAG, "odesilani odpiteho na sever spolecny ucet");
			playSound();

		}

		((IMyActivity) view.getContext()).notifyTapsChanged();
	}

	private void playSound() {
		if (!player.isPlaying()) {
			player.start();
		}
	}

	

	public void getBarrelsFromREST(Context context) {
		myRestFacade.getAllBarrels(context);
	}

	public List<Barrel> getBarrels() {
		return barrels;
	}

	public void setBarrels(List<Barrel> barrels) {
		this.barrels = barrels;
	}

	public void tapBarrel(Barrel barrel, Context context) {
		// TODO tady musí být kontrola všech píp
		if (taps.get(0).getBarrel() == null) {
			try {
				updateBarrel(barrel, context, BarrelState.STOCK,
						BarrelState.TAPED, "Tento sud nelze narazit");
				taps.get(0).setBarrel(barrel);

			} catch (NotTappedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "všechny pípy obsazeny");
		}
	}

	public void untapBarrel(Barrel barrel, Context mContext) {
		Log.d(LOG_TAG, "Odrazime sud : " + barrel);
		// TODO tohle se bude muset upravit aby to podporovalo vic taps
		if ((taps.get(0).getBarrel() != null)
				&& (taps.get(0).getBarrel().equals(barrel))) {
			try {
				updateBarrel(barrel, mContext, BarrelState.TAPED,
						BarrelState.STOCK, "Tento sud nelze odrazit");
				Log.d(LOG_TAG, "reseting");
				taps.get(0).setBarrel(null);
				taps.get(0).resetPoured();

			} catch (NotTappedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}

	public void finishBarrel(Barrel barrel, Context mContext) {
		Log.d(LOG_TAG, "Dopit sud : " + barrel);
		if (taps.get(0).getBarrel() == barrel) {

			try {
				updateBarrel(barrel, mContext, BarrelState.TAPED,
						BarrelState.FINISHED,
						"Tento sud není naražen nemùže bt tedy dopit");
				int index = taps.indexOf(barrel);
				if (index != -1) {
					taps.get(index).setBarrel(null);
					taps.get(index).resetPoured();
				} else {
					Log.e(LOG_TAG, "Sud enbyl nalezen v listu taps");
				}
			} catch (NotTappedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}

	private void updateBarrel(Barrel barrel, Context context,
			BarrelState conditionalState, BarrelState newState,
			String errMessage) throws NotTappedException {
		if (barrel.getBarrelState() == conditionalState) {
			Log.d(LOG_TAG, "Narazime sud : " + barrel);
			barrel.setBarrelState(newState);
			myRestFacade.updateBarrel(barrel, context);
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setMessage(errMessage).setTitle("Chyba");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
			throw new NotTappedException("errMessage");
		}
		getBarrelsFromREST(context);
	}

	public List<Tap> getTaps() {
		// TODO Auto-generated method stub
		return taps;
	}
	
	
	public void setServer(String server) {
		myRestFacade.setServer(server);
		
	}

}
