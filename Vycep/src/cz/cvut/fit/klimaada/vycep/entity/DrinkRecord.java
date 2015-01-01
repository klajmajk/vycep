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

    private int id;
    private int volume;
    private int user;
    private int keg;
    private Date dateAdd;

    public DrinkRecord() {
    }

    public DrinkRecord(int volume, int user, int keg, Date dateAdd) {
        this.volume = volume;
        this.user = user;
        this.keg = keg;
        this.dateAdd = dateAdd;
    }

    public int getId() {
        return id;
    }

    public int getVolume() {
        return volume;
    }

    public int getUser() {
        return user;
    }

    public int getKeg() {
        return keg;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setUser(int userId) {
        this.user = userId;
    }

    public void setKeg(int keg) {
        this.keg = keg;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    void created() {
        this.dateAdd = new Date();
    }

    @Override
    public String toString() {
        return "DrinkRecord{" + "id=" + id + ", volume=" + volume + ", consumer=" + user + ", barrel=" + keg + '}';
    }
}
