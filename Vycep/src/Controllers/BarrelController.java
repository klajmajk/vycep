package Controllers;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import cz.cvut.fit.klimaada.vycep.BarreslListActivity;
import cz.cvut.fit.klimaada.vycep.IMyActivity;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import cz.cvut.fit.klimaada.vycep.rest.IRestFacade;

public class BarrelController extends AbstractController{
	private static final String LOG_TAG = "BarrelController";
	private IRestFacade myRestFacade;

	public BarrelController(Model model, IRestFacade myRestFacade) {
		super(model);
		this.myRestFacade = myRestFacade;
	}

	public void tapBarrel(Barrel barrel, Context context) {
		// TODO tady musí být kontrola všech píp
		if (model.getTap(0).getBarrel() == null) {
			if(barrel.getTaped()==null) barrel.setTaped(new Date());
			updateBarrel(barrel, context, BarrelState.STOCK, BarrelState.TAPED,
					"Tento sud nelze narazit");
		} else {
			Log.e(LOG_TAG, "všechny pípy obsazeny");
		}
	}

	public void untapBarrel(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Odrazime sud : " + barrel);
		Tap tap = model.getBarrelsTap(barrel);
		if (tap != null) {
			updateBarrel(barrel, context, BarrelState.TAPED, BarrelState.STOCK,
					"Tento sud nelze odrazit");
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}

	public void finishBarrel(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Dopit sud : " + barrel);
		Tap tap = model.getBarrelsTap(barrel);
		if (tap != null) {
			updateBarrel(barrel, context, BarrelState.TAPED,
					BarrelState.FINISHED, "Tento sud nelze dopít");
		} else {
			Log.e(LOG_TAG, "Tentosud není naražen");
		}

	}
	public void deleteBarrel(Barrel barrel, Context context) {
		// TODO Auto-generated method stub
		myRestFacade.deleteBarrel(barrel, context);
		
	}

	public void barrelStateChanged(Barrel barrel, Context context) {
		Log.d(LOG_TAG, "Barrel: " + barrel);
		Tap tap = model.getTap(0);
		if (barrel.getBarrelState() == BarrelState.FINISHED) {
			tap.setBarrel(null);
			tap.resetPoured();
		} else if (barrel.getBarrelState() == BarrelState.STOCK) {
			tap.setBarrel(null);
			tap.resetPoured();
		} else if (barrel.getBarrelState() == BarrelState.TAPED) {
			tap.setBarrel(barrel);
		}
		((IMyActivity) view.getContext()).notifyTapsChanged();
		((BarreslListActivity) context).notifyBarrelsReceived(model.getBarrels());
		Controller.getInstanceOf().persist();
	}

	

	private void updateBarrel(Barrel barrel, Context context,
			BarrelState conditionalState, BarrelState newState,
			String errMessage) {
		if (barrel.getBarrelState() == conditionalState) {
			Log.d(LOG_TAG, "Update sudu : " + barrel);
			myRestFacade.updateBarrel(barrel, newState, context);
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setMessage(errMessage).setTitle("Chyba");
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
			// throw new NotTappedException("errMessage");
		}
	}	
	
}
