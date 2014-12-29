package cz.cvut.fit.klimaada.vycep;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;

public class Model {


    private static final String LOG_TAG = "Model";
    private List<Tap> taps;
    private List<Keg> kegs;
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

    public Tap getBarrelsTap(Keg keg) {
        for (Tap tap : taps) {

            Log.d(LOG_TAG, "getBarrelPosition\n" + tap.getKeg() + " \n" + keg);
            if (tap.getKeg() != null) {

                if (tap.getKeg().getId() == keg.getId())
                    return tap;
            }
        }
        return null;
    }

    public List<Keg> getKegs() {
        return kegs;
    }

    public void setKegs(List<Keg> kegs) {
        //je to tak pro to, abys po��d fungoval pointer na list z adapt�ru
        if (this.kegs == null) this.kegs = kegs;
        else {
            this.kegs.clear();
            this.kegs.addAll(kegs);
        }
    }

    public void setTaps(List<Tap> taps) {
        //je to tak pro to, abys po��d fungoval pointer na list z adapt�ru
        if (this.taps == null) this.taps = taps;
        else {
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
