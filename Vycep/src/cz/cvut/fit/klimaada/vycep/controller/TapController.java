package cz.cvut.fit.klimaada.vycep.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONObject;

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
            showDialog("Všechny pípy obsazeny", context);
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
            showDialog("Tentosud není naražen", context);
        }

    }

    private void showDialog(String text, Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(text).setTitle(
                "Chyba").setPositiveButton("OK", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void finishBarrel(Keg keg, Context context) {
        Log.d(LOG_TAG, "Dopit sud : " + keg);
        Tap tap = model.getKegTap(keg);
        if (tap != null) {
            if (keg.getDateTap() == null) keg.setDateEnd(new Date());
            updateBarrel(keg, context, KegState.TAPPED,
                    KegState.FINISHED, "Tento sud nelze dopít");
        } else {
            showDialog("Tentosud není naražen", context);
        }

    }

    public void deleteKeg(Keg keg, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Context context) {
        // TODO Auto-generated method stub
        myRestFacade.deleteRequest(keg, listener, errorListener, context);

    }

    public void barrelStateChanged(Keg keg, Context context) {
        Log.d(LOG_TAG, "Barrel: " + keg);
        Tap tap = model.getTap(0);

        refreshTap();
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

    public void refreshTap(Tap tap) {
        List taps = new ArrayList<Tap>();
        tap.copyNonserverAttributes(model.getTaps().get(0));
        taps.add(tap);
        model.setTaps(taps);
        ((IMyActivity) view.getContext()).notifyTapsChanged();
    }

    public boolean showNewKegNotification() {
        Keg keg = model.getTap(0).getKeg();
        if (keg != null) {
            int progress = getProgress(model.getTap(0));
            if (progress <= 15) return true;

        }
        return false;
    }

    public int getProgress(Tap tap) {
        int result = (int) Math.round((1 - ((double) tap.getPoured() / (double) tap.getKeg().getVolume())) * 100);
        if (result < 0) return 0;

        return result;
    }
}
