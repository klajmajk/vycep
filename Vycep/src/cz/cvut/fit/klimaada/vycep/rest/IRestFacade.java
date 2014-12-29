package cz.cvut.fit.klimaada.vycep.rest;

import android.content.Context;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.DrinkRecord;
import cz.cvut.fit.klimaada.vycep.entity.Keg;

public interface IRestFacade {
    public void getConsumer(int id, Context context);

    public List<Keg> getAllKegs(Context context);

    public void addDrinkRecord(DrinkRecord record, Context context);

    public void setServer(String server);

    public void updateBarrel(Keg keg, BarrelState newState,
                             Context context);

    void getBarrelKinds(Context context);

    public void addNewBarrels(List<Keg> kegs, Context context);

    public void deleteBarrel(Keg keg, Context context);


}
