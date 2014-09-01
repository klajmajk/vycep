package cz.cvut.fit.klimaada.vycep.hardware;

import java.io.IOException;

import cz.cvut.fit.klimaada.vycep.MainActivity;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

public class NFC {
	private static final String LOG = "NFC";
	private NfcAdapter mAdapter;
	private PendingIntent pendingIntent;
	private Ndef ndefTag;

	

	public String readNfcData(Intent intent) {
		Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		// get NDEF tag details
		ndefTag = Ndef.get(myTag);

		Log.d(LOG, "Tag is connected: " + ndefTag.isConnected());
		NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
		byte[] payload = ndefMesg.getRecords()[0].getPayload();
		try {
			ndefTag.connect();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
					: "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0077;
			String languageCode = new String(payload, 1, languageCodeLength,
					"US-ASCII");

			// Get the Text
			String text = new String(payload, languageCodeLength + 1,
					payload.length - languageCodeLength - 1, textEncoding);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("Record Parsing Failure!!");
		}

	}

	public void onCreate(Activity activity) {
		mAdapter = NfcAdapter.getDefaultAdapter(activity);

		if (mAdapter == null) {
			// Device does not support NFC
			Toast.makeText(activity, "zaøízení nepodporuje nfc",
					Toast.LENGTH_LONG).show();
		} else {
			if (!mAdapter.isEnabled()) {
				// NFC is disabled
				Toast.makeText(activity, "NFC je vypnuto", Toast.LENGTH_LONG)
						.show();
			} else {
				pendingIntent = PendingIntent.getActivity(activity, 0,
						new Intent(activity, MainActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}
		}

	}

	public void onPause(Activity activity) {
		mAdapter.disableForegroundDispatch(activity);
	}

	public void onResume(Activity activity) {
		mAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
	}

	public boolean isConnected() {
		if (ndefTag != null) {
			return ndefTag.isConnected();
		} else
			return false;
	}
	

}
