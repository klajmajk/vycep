package cz.cvut.fit.klimaada.vycep.entity;

import android.util.Log;

public class Tap {
	private Barrel barrel;
	private boolean active;
	private int poured;
	private int activePoured;
	private Consumer activeConsumer;
	public Tap(Barrel barrel) {
		super();
		this.barrel = barrel;
		this.active = false;
		this.poured = 0;
	}
	public Tap() {
		super();
		this.barrel = null;
		this.active = false;
		this.poured = 0;
	}
	public Barrel getBarrel() {
		return barrel;
	}
	public void setBarrel(Barrel barrel) {
		this.barrel = barrel;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}		
	
	public void addPoured(int poured){
		Log.d("tap","adding poured: "+ this.poured);
		this.poured += poured;
	}
	
	public void resetPoured(){
		this.poured = 0;
	}
	public int getPoured() {
		return poured;
	}
	
	public int getActivePoured() {
		return activePoured;
	}
	public void setActivePoured(int activePoured) {
		this.activePoured = activePoured;
	}
	
	public Consumer getActiveConsumer() {
		return activeConsumer;
	}
	public void setActiveConsumer(Consumer activeConsumer) {
		this.activeConsumer = activeConsumer;
	}
	@Override
	public String toString() {
		return "Tap [barrel=" + barrel + ", active=" + active + ", poured="
				+ poured + "]";
	}
	
	
	
	
	
}
