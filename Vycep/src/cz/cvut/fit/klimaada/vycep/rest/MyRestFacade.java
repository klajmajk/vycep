package cz.cvut.fit.klimaada.vycep.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;

public class MyRestFacade implements IRestFacade {

	private static final String LOG_TAG = "MY_REST_FACADE";
	private static final String SERVER = "http://192.168.2.105:30195/KumpaniEE/webresources/";

	@Override
	public Consumer getConsumer(int id, Context context) {
		
		try {
			ConsumerGetterTask task = new ConsumerGetterTask(context, new URI(SERVER+"consumer/"+id));
			task.execute();
			return task.get();
		} catch ( URISyntaxException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Error in getting consumer ");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Barrel> getAllBarrels(Context context) {
		BarrelsGetterTask task = new BarrelsGetterTask(context);
		URI uri;
		try {
			uri = new URI(SERVER+"barrel");
			task.execute(uri);
			//return get.get();
		} catch ( URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Error in getting barrels ");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void putDrinkRecord(Consumer constumer, double volume, Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBarrel(Barrel barrel, Context context) {
		try {
			Log.d(LOG_TAG, "Editace: "+barrel.getId());
			BarrelUpdateTask task = new BarrelUpdateTask(context, new URI(
					SERVER+"barrel/"+barrel.getId()));
			task.execute(barrel);
			//return get.get();
		} catch ( URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Error in getting barrels ");
			e.printStackTrace();
		}

	}

	@Override
	public void finishBarrel(Barrel barrel, Context context) {
		// TODO Auto-generated method stub

	}

	

}
