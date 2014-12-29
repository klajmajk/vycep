package cz.cvut.fit.klimaada.vycep.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.util.Date;

import cz.cvut.fit.klimaada.vycep.BarreslListActivity;
import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;

public class BarrelController extends AbstractController {
    private static final String LOG_TAG = "BarrelController";
    private IRestFacade myRestFacade;

    public BarrelController(Model model, IRestFacade myRestFacade) {
        super(model);
        this.myRestFacade = myRestFacade;
    }

    public void tapBarrel(Keg keg, Context context) {
        // TODO tady mus� b�t kontrola v�ech p�p
        if (model.getTap(0).getKeg() == null) {
            if (keg.getDateTap() == null) keg.setDateTap(new Date());
            updateBarrel(keg, context, BarrelState.STOCK, BarrelState.TAPED,
                    "Tento sud nelze narazit");
        } else {
            Log.e(LOG_TAG, "v�echny p�py obsazeny");
        }
    }

    public void untapBarrel(Keg keg, Context context) {
        Log.d(LOG_TAG, "Odrazime sud : " + keg);
        Tap tap = model.getBarrelsTap(keg);
        if (tap != null) {
            updateBarrel(keg, context, BarrelState.TAPED, BarrelState.STOCK,
                    "Tento sud nelze odrazit");
        } else {
            Log.e(LOG_TAG, "Tentosud nen� nara�en");
        }

    }

    public void finishBarrel(Keg keg, Context context) {
        Log.d(LOG_TAG, "Dopit sud : " + keg);
        Tap tap = model.getBarrelsTap(keg);
        if (tap != null) {
            updateBarrel(keg, context, BarrelState.TAPED,
                    BarrelState.FINISHED, "Tento sud nelze dop�t");
        } else {
            Log.e(LOG_TAG, "Tentosud nen� nara�en");
        }

    }

    public void deleteBarrel(Keg keg, Context context) {
        // TODO Auto-generated method stub
        myRestFacade.deleteBarrel(keg, context);

    }

    public void barrelStateChanged(Keg keg, Context context) {
        Log.d(LOG_TAG, "Barrel: " + keg);
        Tap tap = model.getTap(0);
        if (keg.getBarrelState() == BarrelState.FINISHED) {
            tap.setKeg(null);
            tap.resetPoured();
        } else if (keg.getBarrelState() == BarrelState.STOCK) {
            tap.setKeg(null);
            tap.resetPoured();
        } else if (keg.getBarrelState() == BarrelState.TAPED) {
            tap.setKeg(keg);
        }
        ((IMyActivity) view.getContext()).notifyTapsChanged();
        ((BarreslListActivity) context).notifyKegsReceived(model.getKegs());
        Controller.getInstanceOf().persist();
    }


    private void updateBarrel(Keg keg, Context context,
                              BarrelState conditionalState, BarrelState newState,
                              String errMessage) {
        if (keg.getBarrelState() == conditionalState) {
            Log.d(LOG_TAG, "Update sudu : " + keg);
            myRestFacade.updateBarrel(keg, newState, context);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setMessage(errMessage).setTitle("Chyba");
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            // throw new NotTappedException("errMessage");
        }
    }

}
