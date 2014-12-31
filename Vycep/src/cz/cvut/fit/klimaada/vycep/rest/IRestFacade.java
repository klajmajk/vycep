package cz.cvut.fit.klimaada.vycep.rest;

import android.content.Context;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.Brewery;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.KegState;
import cz.cvut.fit.klimaada.vycep.entity.Tap;

public interface IRestFacade {
    public void getConsumer(int id, Context context);

    public List<Keg> getAllKegs(Context context);

    public void addDrinkRecord(DrinkRecord record, Context context);

    public void setServer(String server);

    public void updateBarrel(Keg keg, Tap tap, KegState newState,
                             Context context);

    void getBreweries(Context context);

    public void addNewKegs(List<Keg> kegs, Context context);

    public void deleteBarrel(Keg keg, Context context);


    public void getTapById(int tapId, Context context);

    void getBeersByBrewery(Brewery brewery, Context context);
}
