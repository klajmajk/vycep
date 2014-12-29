/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Adam
 */
public class DrinkRecord implements Serializable {

    private int idDrinkRecord;
    private int volume;
    private User user;
    private Keg keg;
    private double price;
    private Date created;

    public DrinkRecord() {
    }

    public DrinkRecord(int volume, User user, Keg keg, Date created) {
        this.volume = volume;
        this.user = user;
        this.keg = keg;
        this.created = created;
    }

    public int getId() {
        return idDrinkRecord;
    }

    public int getVolume() {
        return volume;
    }

    public User getUser() {
        return user;
    }

    public Keg getKeg() {
        return keg;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setKeg(Keg keg) {
        this.keg = keg;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    void created() {
        this.created = new Date();
    }

    @Override
    public String toString() {
        return "DrinkRecord{" + "id=" + idDrinkRecord + ", volume=" + volume + ", consumer=" + user + ", barrel=" + keg + '}';
    }
}
