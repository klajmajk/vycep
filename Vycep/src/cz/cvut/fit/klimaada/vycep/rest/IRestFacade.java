package cz.cvut.fit.klimaada.vycep.rest;

import java.util.List;

import android.content.Context;

import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.exceptions.UpdateErrorException;

public interface IRestFacade {
	public void getConsumer(int id, Context context);
	public List<Barrel> getAllBarrels(Context context) ;
	public void addDrinkRecord(DrinkRecord record, Context context);
	public void setServer(String server);
	public void updateBarrel(Barrel barrel, BarrelState newState,
			Context context);
	void getBarrelKinds(Context context);
	public void addNewBarrels(List<Barrel> barrels, Context context);
	public void deleteBarrel(Barrel barrel, Context context);
	
	
}
