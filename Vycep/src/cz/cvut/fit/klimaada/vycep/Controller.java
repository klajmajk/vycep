package cz.cvut.fit.klimaada.vycep;

import java.io.IOException;
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
import cz.cvut.fit.klimaada.vycep.exceptions.UpdateErrorException;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;
import cz.cvut.fit.klimaada.vycep.rest.MyRestFacade;
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

public class Controller {
	private static final String LOG_TAG = "CONTROLLER";
	private double calibration;
	private static final String PERSISTENT_TAPS = "Persistent taps";
	private IStatusView view;
	private static Controller instance;
	private IRestFacade myRestFacade;
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
		myRestFacade.getConsumer(consumerId,
				view.getContext());
		
	}
	
	public void consumerRecieved(Consumer consumer){
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
			if (tap.getActivePoured() != 0) {
				myRestFacade.addDrinkRecord(
						new DrinkRecord(tap.getActivePoured(), tap
								.getActiveConsumer(), tap.getBarrel(),
								new Date()), view.getContext());
			}

			tap.setActiveConsumer(null);
			tap.setActivePoured(0);

		}
		tap.setActive(false);

		((IMyActivity) view.getContext()).notifyTapsChanged();

	}

	public void setView(IStatusView view) {
		this.view = view;
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());
		String taps = sp.getString(PERSISTENT_TAPS, "");
		// String taps = "";
		String server = sp.getString("serverAddress", "");
		try{
		calibration = Double.parseDouble(sp.getString("calibration", ""));
		} catch (Exception e){
			Log.e(LOG_TAG, e.getMessage());
			calibration = 1;
		}
		myRestFacade.setServer(server);
		if (taps != "") {
			Type listType = new TypeToken<ArrayList<Tap>>() {
			}.getType();
			this.taps = new Gson().fromJson(taps, listType);
		}

	}

	public void persist() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(view.getContext());

		String taps = new Gson().toJson(this.taps);
		sp.edit().putString(PERSISTENT_TAPS, taps).apply();
	}

	public void serialDataReceived(Intent intent) {
		// TODO upravit pro vice kohoutu
		double received = (double) arduino.getPoured(intent);
		Log.d(LOG_TAG, "receved: "+received);
		double poured = (received*1000) / calibration;
		Log.d(LOG_TAG, "poured:" + poured);
		Tap tap = taps.get(0);
		tap.addPoured((int) poured);
		if (taps.get(0).isActive()) {
			tap.setActivePoured((int) (poured + tap.getActivePoured()));
		} else {
			Log.d(LOG_TAG, "odesilani odpiteho na sever spolecny ucet");
			playSound();

		}

		((IMyActivity) view.getContext()).notifyTapsChanged();
	}

	private void playSound() {
		if (player == null)
			mediaPlayerInit();
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
			updateBarrel(barrel, context, BarrelState.STOCK, BarrelState.TAPED,
					"Tento sud nelze narazit");
		} else {
			Log.e(LOG_TAG, "všechny pípy obsazeny");
		}
	}

	public void untapBarrel(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Odrazime sud : " + barrel);
		int index = getBarrelPosition(taps, barrel);
		if (index != -1) {
			updateBarrel(barrel, context, BarrelState.TAPED, BarrelState.STOCK,
					"Tento sud nelze odrazit");
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}

	public void finishBarrel(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Dopit sud : " + barrel);
		int index = getBarrelPosition(taps, barrel);
		if (index != -1) {
			updateBarrel(barrel, context, BarrelState.TAPED,
					BarrelState.FINISHED, "Tento sud nelze dopít");
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}

	public void barrelStateChanged(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Barrel: " + barrel);
		int index = getBarrelPosition(taps, barrel);
		if (barrel.getBarrelState() == BarrelState.FINISHED) {
			taps.get(index).setBarrel(null);
			taps.get(index).resetPoured();
		} else if (barrel.getBarrelState() == BarrelState.STOCK) {
			taps.get(index).setBarrel(null);
			taps.get(index).resetPoured();
		} else if (barrel.getBarrelState() == BarrelState.TAPED) {
			taps.get(0).setBarrel(barrel);
		}
		((IMyActivity) view.getContext()).notifyTapsChanged();
		((BarreslListActivity) context).notifyBarrelsReceived(barrels);
	}

	private int getBarrelPosition(List<Tap> taps, Barrel barrel) {
		for (Tap tap : taps) {
			if (tap.getBarrel() != null) {
				Log.d(LOG_TAG, "getBarrelPosition\n" + tap.getBarrel() + " \n"
						+ barrel);
				if (tap.getBarrel().getId() == barrel.getId())
					return taps.indexOf(tap);
			}
		}
		return -1;
	}

	private void updateBarrel(Barrel barrel, Context context,
			BarrelState conditionalState, BarrelState newState,
			String errMessage) {
		if (barrel.getBarrelState() == conditionalState) {
			Log.d(LOG_TAG, "Update sudu : " + barrel);
			myRestFacade.updateBarrel(barrel, newState, context);
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setMessage(errMessage).setTitle("Chyba");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
			// throw new NotTappedException("errMessage");
		}
	}

	public List<Tap> getTaps() {
		// TODO Auto-generated method stub
		return taps;
	}

	public void setServer(String server) {
		myRestFacade.setServer(server);

	}

	public void mediaPlayerInit() {
		Uri defaultRingtoneUri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		player = new MediaPlayer();

		try {
			player.setDataSource(view.getContext(), defaultRingtoneUri);
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			player.prepare();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public void setCalibration(double parseDouble) {
		// TODO Auto-generated method stub
		
	}

}
