package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.Keg;

public interface IMyActivity {
    public void notifyKegsReceived(List<Keg> kegs);

    public void notifyTapsChanged();
}
