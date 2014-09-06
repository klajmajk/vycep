package cz.cvut.fit.klimaada.vycep.rest;

import java.util.List;

import android.content.Context;

import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.Consumer;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;

public interface IRestFacade {
	public Consumer getConsumer(int id, Context context);
	public List<Barrel> getAllBarrels(Context context) ;
	public void putDrinkRecord(Consumer constumer, double volume, Context context);
	public void updateBarrel (Barrel barrel, Context context);
	public void finishBarrel (Barrel barrel, Context context);
	public void addDrinkRecord(DrinkRecord record, Context context);
	
	
}
