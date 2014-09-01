package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.Barrel;

public interface IMyActivity {
	public void notifyBarrelsReceived(List<Barrel> barrels);
	public void notifyTapsChanged();
}
