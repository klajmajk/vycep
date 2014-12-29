package cz.cvut.fit.klimaada.vycep;

import android.app.ActionBar;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.adapter.TapsListAdapter;
import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.hardware.NFC;

public class MainActivity extends Activity implements IMyActivity, IStatusView {

    private static final String LOG = "MAIN_ACTIVITY";
    private final static String DATA_RECEIVED_INTENT = "primavera.arduino.intent.action.DATA_RECEIVED";
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private Activity mActivity;
    private TapsListAdapter tapAdapter;
    private Handler screenOFFHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            // do something
            // wake up phone
            Log.i(LOG_TAG, "Wake up the phone and disable keyguard");
                /*PowerManager powerManager = (PowerManager) MainActivity.this
		                .getSystemService(Context.POWER_SERVICE);
		        long l = SystemClock.uptimeMillis();
		        powerManager.userActivity(l, false);//false will bring the screen back as bright as it was, true - will dim it
		        WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
	            wakeLock.acquire();
	            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE); 
	            KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock("TAG");
	            keyguardLock.disableKeyguard();*/
            KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardLock keyguardLock = km.newKeyguardLock("TAG");
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            keyguardLock.disableKeyguard();
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyWakeLock");

            wakeLock.acquire();
        }
    };


    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private NFC nfc;
    private BroadcastReceiver receiver;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.d(LOG, "Is Connected: " + nfc.isConnected());
            if (nfc.isConnected())
                timerHandler.postDelayed(this, 500);
            else Controller.getInstanceOf().getNFCController().cardRemoved();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.fragment_main);

        mTitle = getTitle();

        // NFC
        nfc = new NFC();
        nfc.onCreate(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_RECEIVED_INTENT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                screenOFFHandler.sendEmptyMessage(0);
                Controller.getInstanceOf().getArduinoController().serialDataReceived(intent);
            }
        };
        this.registerReceiver(receiver, filter);

        Controller.getInstanceOf().setView(this);
        ListView tapListView = (ListView) findViewById(R.id.tapsListView);
        //Log.d(LOG_TAG, "tapList:" + tapListView);
        tapAdapter = new TapsListAdapter(this, R.layout.tap_item, Controller.getInstanceOf().getTaps());
        tapListView.setAdapter(tapAdapter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        nfc.onPause(this);
        timerHandler.removeCallbacks(timerRunnable);
        Controller.getInstanceOf().getNFCController().cardRemoved();
        Controller.getInstanceOf().persist();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        nfc.onResume(this);
        notifyTapsChanged();
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            screenOFFHandler.sendEmptyMessage(0);
            String nfcData = nfc.readNfcData(intent);
            Controller.getInstanceOf().getNFCController().cardDetected(nfcData);
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }


    @Override
    public void notifyKegsReceived(List<Keg> kegs) {
        Intent intent = new Intent(this,
                BarreslListActivity.class);
        startActivity(intent);

    }

    @Override
    public void notifyTapsChanged() {
        tapAdapter.notifyDataSetChanged();

    }

    @Override
    public void setStatusText(String text) {
        TextView textView = (TextView) this.findViewById(R.id.status_label);
        textView.setText(text);

    }

    @Override
    public void setVolumeText(String text) {


    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_tap) {
            Controller.getInstanceOf().getBarrelsFromREST(this);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this,
                    SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
