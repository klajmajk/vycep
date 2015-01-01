package cz.cvut.fit.klimaada.vycep.entity;

import android.util.Log;

public class Tap {
    private int id;
    private Keg keg;
    private boolean active;
    private int poured;
    private int activePoured;
    private int userId;
    private String note;

    public Tap(Keg keg) {
        super();
        this.keg = keg;
        this.active = false;
        this.poured = 0;
    }

    public Tap() {
        super();
        this.keg = null;
        this.active = false;
        this.poured = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Keg getKeg() {
        return keg;
    }

    public void setKeg(Keg keg) {
        this.keg = keg;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        Log.d("TAP", "Setting active:" + active);
        this.active = active;
    }

    public void addPoured(int poured) {
        Log.d("tap", "adding poured: " + this.poured);
        this.poured += poured;
    }

    public void resetPoured() {
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Tap{" +
                "id=" + id +
                ", keg=" + keg +
                ", active=" + active +
                ", poured=" + poured +
                ", activePoured=" + activePoured +
                ", userId=" + userId +
                ", note='" + note + '\'' +
                '}';
    }

    public void copyNonserverAttributes(Tap tap) {
        this.active = tap.isActive();
        this.userId = tap.getUserId();
        this.activePoured = tap.getActivePoured();
        this.poured = tap.getPoured();
    }
}
