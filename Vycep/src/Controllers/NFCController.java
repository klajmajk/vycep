package Controllers;

import java.util.Date;

import android.util.Log;
import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.hardware.Arduino;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;

public class NFCController extends AbstractController{
	
	
	private static final String LOG_TAG = "NFCController";
	private IRestFacade myRestFacade;

	public NFCController(Model model, IRestFacade myRestFacade) {
		super(model);
		this.myRestFacade = myRestFacade;
	}
	
	public void cardDetected(String data) {
		// view.setVolumeText("0,00 L");
		int consumerId = Consumer.parseIdFromNFC(data);
		myRestFacade.getConsumer(consumerId,
				view.getContext());
		
	}

	public void cardRemoved() {
		// view.setVolumeText("0,00 L");
		view.setStatusText(view.getContext().getString(
				R.string.waiting_for_card));
		Log.d(LOG_TAG, "sending close");
		view.getContext().sendBroadcast(Arduino.sendClose());
		// TODO upravit pro vice kohoutu
		Tap tap = model.getTap(0);
		if (tap.isActive() == true) {
			if (tap.getActivePoured() != 0) {
				myRestFacade.addDrinkRecord(
						new DrinkRecord(tap.getActivePoured(), tap
								.getActiveConsumer(), tap.getBarrel(),
								new Date()), view.getContext());
			}

			tap.setActiveConsumer(null);
			tap.setActivePoured(0);

		}
		tap.setActive(false);

		((IMyActivity) view.getContext()).notifyTapsChanged();

	}

	public void pouringConsumerRecieved(Consumer consumer){
		view.setStatusText(view.getContext().getString(R.string.pouring) + ": "
				+ consumer);
		// getBarrelsFromREST();

		Log.d(LOG_TAG, "sending open");
		view.getContext().sendBroadcast(Arduino.sendOpen());
		// TODO upravit pro vice kohoutu
		Tap tap = model.getTap(0);
		tap.setActive(true);
		tap.setActiveConsumer(consumer);
		
	}
}
