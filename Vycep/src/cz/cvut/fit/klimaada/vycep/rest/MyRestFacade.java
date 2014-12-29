package cz.cvut.fit.klimaada.vycep.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.rest.old.AddTask;
import cz.cvut.fit.klimaada.vycep.rest.task.AbstractTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetBarrelsTask;
import cz.cvut.fit.klimaada.vycep.rest.task.GetUserTask;
import cz.cvut.fit.klimaada.vycep.rest.task.PutKegTask;

public class MyRestFacade implements IRestFacade {

	private static final String LOG_TAG = "MY_REST_FACADE";
	private String Server;

    @Override
    public void getConsumer(int id, Context context) {
		
		try {
            Log.d(LOG_TAG, "getting user: " + Server + "user/" + id);
            AbstractTask task = new GetUserTask(new URI(Server + "user/" + id), context);
            task.execute();
		} catch ( URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Error in getting consumer ");
			e.printStackTrace();
		}
	}

	@Override
    public List<Keg> getAllKegs(Context context) {
        URI uri;
		try {
            uri = new URI(Server + "keg");
            AbstractTask task = new GetBarrelsTask(uri, context);
            task.execute();
        } catch ( URISyntaxException e) {
			// TODO Auto-generated catch block
            Log.e(LOG_TAG, "Error in getting kegs ");
            e.printStackTrace();
		}

		return null;
	}


    @Override
    public void getBarrelKinds(Context context) {
        /*BarrelKindGetterTask task = new BarrelKindGetterTask(context);
        URI uri;
		try {
			uri = new URI(Server+"BarrelKind");
			task.execute(uri);
			//return get.get();
		} catch ( URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Error in getting barrels ");
			e.printStackTrace();
		}*/
	}

	

	@Override
    public void updateBarrel(Keg keg, BarrelState newState, Context context) {
        try {
            AbstractTask task = new PutKegTask(new URI(Server + "keg/" + keg.getId()), context, keg, newState);
            task.execute();
        } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

	

	@Override
	public void addDrinkRecord(DrinkRecord record, Context context) {
			HttpPost httpRequest;
			try {
				httpRequest = new HttpPost(new URI(
						Server+"drinkrecord/"));
				httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
				httpRequest.setHeader("Accept", "application/json; charset=utf-8");
				Gson gson = new GsonBuilder().setDateFormat(
						"yyyy-MM-dd'T'HH:mm:ssZ").create();
				try {
					String json = gson.toJson(record);
					httpRequest.setEntity(new StringEntity(json, HTTP.UTF_8));
					Log.d(LOG_TAG, "Drinkrecord json: "+ json);
					AddTask task = new AddTask(context, null);
					task.execute(httpRequest);
					//if(!task.get())throw new UpdateErrorException("Chyba p�i updatu zaznam nebyl zm�n�n");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			} catch (URISyntaxException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	}

	@Override
	public void setServer(String server) {
		Log.d(LOG_TAG, "Setting server: "+server);
		Server = server;
	}

	@Override
    public void addNewBarrels(List<Keg> kegs, Context context) {
        HttpPost httpRequest;
        for (Keg keg : kegs) {
            try {
				httpRequest = new HttpPost(new URI(
						Server+"barrel/"));
				httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
				httpRequest.setHeader("Accept", "application/json; charset=utf-8");
				Gson gson = new GsonBuilder().setDateFormat(
						"yyyy-MM-dd'T'HH:mm:ssZ").create();
				try {
                    String json = gson.toJson(keg);
                    httpRequest.setEntity(new StringEntity(json, HTTP.UTF_8));
					Log.d(LOG_TAG, "New Barrel json: "+ json);
					AddTask task = new AddTask(context, (ICallback)context);
					task.execute(httpRequest);
					//if(!task.get())throw new UpdateErrorException("Chyba p�i updatu zaznam nebyl zm�n�n");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			} catch (URISyntaxException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		}
		
		
	}

	@Override
    public void deleteBarrel(Keg keg, Context context) {
        try {
			HttpDelete httpRequest = new HttpDelete(new URI(
                    Server + "barrel/" + keg.getId()));
            httpRequest.setHeader("Content-Type", "application/json; charset=utf-8");
			httpRequest.setHeader("Accept", "application/json; charset=utf-8");
            Log.d(LOG_TAG, "Deleting barrel: " + keg);
            AddTask task = new AddTask(context, (ICallback)context);
			task.execute(httpRequest);
			//if(!task.get())throw new UpdateErrorException("Chyba p�i updatu zaznam nebyl zm�n�n"); 
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	}

	
	
	

	

}
