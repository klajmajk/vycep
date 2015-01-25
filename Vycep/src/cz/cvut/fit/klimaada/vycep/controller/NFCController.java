package cz.cvut.fit.klimaada.vycep.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Date;

import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.entity.User;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;

public class NFCController extends AbstractController {


    private static final String LOG_TAG = "NFCController";
    private IRestFacade myRestFacade;

    public NFCController(Model model, IRestFacade myRestFacade) {
        super(model);
        this.myRestFacade = myRestFacade;
    }

    public void cardDetected(String data) {
        // view.setVolumeText("0,00 L");
        int consumerId = User.parseIdFromNFC(data);

        Log.d(LOG_TAG, "sending open");
        view.getContext().sendBroadcast(Arduino.sendOpen());
        // TODO upravit pro vice kohoutu
        Tap tap = model.getTap(0);
        tap.setActive(true);

        Log.d(LOG_TAG, "isActive: " + tap.isActive());
        tap.setUserId(consumerId);
        if (!isConnected())
            view.setStatusText("Chybí připojení záznamy o konzumaci budou nahrány až se připojení obnoví");
        else myRestFacade.getConsumer(consumerId,
                view.getContext());


        Log.d(LOG_TAG, "Tap detected:" + tap);
        view.pouring(true);

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void cardRemoved() {

        Tap tap = model.getTap(0);

        // view.setVolumeText("0,00 L");
        view.setStatusText(view.getContext().getString(
                R.string.waiting_for_card));
        Log.d(LOG_TAG, "sending close");

        view.getContext().sendBroadcast(Arduino.sendClose());
        // TODO upravit pro vice kohoutu
        if (tap.getKeg() != null) {
            if (tap.isActive() == true) {
                if (tap.getActivePoured() != 0) {
                    if(tap.getUserId()!=-1) {
                        myRestFacade.addDrinkRecord(
                                new DrinkRecord(tap.getActivePoured(), tap
                                        .getUserId(), tap.getKeg().getId(),
                                        new Date()), view.getContext());
                    }
                }



            }
        }
        tap.setUserId(-1);
        tap.setActivePoured(0);
        tap.setActive(false);

        // JEN PRO TESTOVACI UCELY
          /* myRestFacade.addDrinkRecord(
                    new DrinkRecord(100, tap
                            .getUserId(), tap.getKeg().getId(),
                            new Date()), view.getContext());*/



        ((IMyActivity) view.getContext()).notifyTapsChanged();
        view.pouring(false);
    }

    public void pouringConsumerRecieved(User user) {
        view.setStatusText(user.getName() + "    kredit: " + user.getBalance());
        // getBarrelsFromREST();


    }
}
