package cz.cvut.fit.klimaada.vycep.controller;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.entity.Tap;

public class ArduinoController extends AbstractController {

    public ArduinoController(Model model) {
        super(model);
    }

    private static final String LOG_TAG = "ArduinoController";
    private MediaPlayer player;


    public void serialDataReceived(Intent intent) {
        // TODO upravit pro vice kohoutu
        double received = (double) model.getArduino().getPoured(intent);
        Log.d(LOG_TAG, "receved: " + received);
        double poured = (received * 1000) / model.getCalibration();
        Log.d(LOG_TAG, "poured:" + poured);
        Tap tap = model.getTap(0);
        tap.addPoured((int) poured);
        if (model.getTap(0).isActive()) {
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
}
