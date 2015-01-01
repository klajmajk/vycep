package cz.cvut.fit.klimaada.vycep;

import android.content.Context;

public interface IStatusView {
	public void setStatusText(String text);
	public void setVolumeText(String text);
	public Context getContext();

    public void pouring(boolean pouring);
}
