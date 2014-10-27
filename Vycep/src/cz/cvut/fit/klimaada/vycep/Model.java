package cz.cvut.fit.klimaada.vycep;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;

public class Model {


	private static final String LOG_TAG = "Model";
	private List<Tap> taps;
	private List<Barrel> barrels;
	private Arduino arduino;
	private double calibration;
	
	public Model() {
		super();
		taps = new ArrayList<>();
		taps.add(new Tap());
		arduino = new Arduino();
	}

	public Tap getTap(int i) {
		return taps.get(i);
	}

	public double getCalibration() {
		return calibration;
	}

	public void setCalibration(double calibration) {
		this.calibration = calibration;
	}
	
	public Tap getBarrelsTap(Barrel barrel) {
		for (Tap tap : taps) {

			Log.d(LOG_TAG, "getBarrelPosition\n" + tap.getBarrel() + " \n"+ barrel);
			if (tap.getBarrel() != null) {
						
				if (tap.getBarrel().getId() == barrel.getId())
					return tap;
			}
		}
		return null;
	}

	public List<Barrel> getBarrels() {
		return barrels;
	}
	
	public void setBarrels(List<Barrel> barrels) {
		//je to tak pro to, abys poøád fungoval pointer na list z adaptéru
		if(this.barrels == null) this.barrels = barrels;
		else{
			this.barrels.clear();
			this.barrels.addAll(barrels);
		}
	}
	
	public void setTaps(List<Tap> taps) {
		//je to tak pro to, abys poøád fungoval pointer na list z adaptéru
		if(this.taps == null) this.taps = taps;
		else{
			this.taps.clear();
			this.taps.addAll(taps);
		}
	}

	public List<Tap> getTaps() {
		return taps;
	}

	public Arduino getArduino() {
		return arduino;
	}
	
	
	
	
	
	

	
}
