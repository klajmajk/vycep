package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.adapter.TapsListAdapter;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.hardware.NFC;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks, IMyActivity {

	private static final String LOG = "MAIN_ACTIVITY";

	private final static String DATA_RECEIVED_INTENT = "primavera.arduino.intent.action.DATA_RECEIVED";

	private static final String LOG_TAG = "MAIN_ACTIVITY";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private NFC nfc;

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			//Log.d(LOG, "Is Connected: " + nfc.isConnected());
			if (nfc.isConnected())
				timerHandler.postDelayed(this, 500);
			else Controller.getInstanceOf().cardRemoved();
		}
	};

	private PlaceholderFragment fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		// NFC
		nfc = new NFC();
		nfc.onCreate(this);
		IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_RECEIVED_INTENT);
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Controller.getInstanceOf().serialDataReceived(intent);
            }
        }, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		nfc.onPause(this);
		timerHandler.removeCallbacks(timerRunnable);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		nfc.onResume(this);
		notifyTapsChanged();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragment = PlaceholderFragment.newInstance(position + 1);
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						fragment).commit();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			String nfcData = nfc.readNfcData(intent);
			Controller.getInstanceOf().cardDetected(nfcData);
			timerHandler.postDelayed(timerRunnable, 0);
		}
	}
	
	

	@Override
	public void notifyBarrelsReceived(List<Barrel> barrels) {
		Intent intent = new Intent(this,
				BarreslListActivity.class);
		startActivity(intent);
		
	}

	@Override
	public void notifyTapsChanged() {
		fragment.notifyTapsChanged();
		
	}


}
