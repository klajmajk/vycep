package cz.cvut.fit.klimaada.vycep.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.BarreslListActivity;
import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.KegState;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;

public class TapController extends AbstractController {
    private static final String LOG_TAG = "TapController";
    private IRestFacade myRestFacade;

    public TapController(Model model, IRestFacade myRestFacade) {
        super(model);
        this.myRestFacade = myRestFacade;
    }

    public void tapBarrel(Keg keg, Context context) {
        // TODO tady mus� b�t kontrola v�ech p�p
        if (model.getTap(0).getKeg() == null) {
            if (keg.getDateTap() == null) keg.setDateTap(new Date());
            updateBarrel(keg, context, KegState.STOCKED, KegState.TAPPED,
                    "Tento sud nelze narazit");
        } else {
            Log.e(LOG_TAG, "všechny pípy obsazeny");
        }
    }

    public void untapBarrel(Keg keg, Context context) {
        Log.d(LOG_TAG, "Odrazime sud : " + keg);
        Tap tap = model.getKegTap(keg);
        Log.d("TAPCONTROLLER TAP", tap.toString());
        if (tap != null) {
            updateBarrel(keg, context, KegState.TAPPED, KegState.STOCKED,
                    "Tento sud nelze odrazit");
        } else {
            Log.e(LOG_TAG, "Tentosud není naražen");
        }

    }

    public void finishBarrel(Keg keg, Context context) {
        Log.d(LOG_TAG, "Dopit sud : " + keg);
        Tap tap = model.getKegTap(keg);
        if (tap != null) {
            updateBarrel(keg, context, KegState.TAPPED,
                    KegState.FINISHED, "Tento sud nelze dopít");
        } else {
            Log.e(LOG_TAG, "Tentosud není naražen");
        }

    }

    public void deleteBarrel(Keg keg, Context context) {
        // TODO Auto-generated method stub
        myRestFacade.deleteBarrel(keg, context);

    }

    public void barrelStateChanged(Keg keg, Context context) {
        Log.d(LOG_TAG, "Barrel: " + keg);
        Tap tap = model.getTap(0);
        ((IMyActivity) view.getContext()).notifyTapsChanged();
        ((BarreslListActivity) context).notifyKegsReceived(model.getKegs());
        Controller.getInstanceOf().persist();
    }


    private void updateBarrel(Keg keg, Context context,
                              KegState conditionalState, KegState newState,
                              String errMessage) {
        if (keg.getState() == conditionalState) {
            Log.d(LOG_TAG, "Update sudu : " + keg);
            myRestFacade.updateBarrel(keg, model.getTap(0), newState, context);
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setMessage(errMessage).setTitle("Chyba");
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            // throw new NotTappedException("errMessage");
        }
    }


    public void refreshTap() {
        //TODO předělat na víca tapů

        Log.d(LOG_TAG + "bez id", "id " + model.getTap(0).getId());
        myRestFacade.getTapById(model.getTapId(), view.getContext());
    }

    public void refershTap(Tap tap) {
        Log.d(LOG_TAG, "initing: " + tap);
        List taps = new ArrayList<Tap>();
        taps.add(tap);
        model.setTaps(taps);
        ((IMyActivity) view.getContext()).notifyTapsChanged();
    }
}