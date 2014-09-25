/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Adam
 */
public class DrinkRecord implements Serializable {

    private int idDrinkRecord;
    private int volume;
    private Consumer consumer;
    private Barrel barrel;
    private double price;
    private Date created;

    public DrinkRecord() {
    }

    public DrinkRecord(int volume, Consumer consumer, Barrel barrel, Date created) {
        this.volume = volume;
        this.consumer = consumer;
        this.barrel = barrel;
        this.created = created;
    }   

    public int getId() {
        return idDrinkRecord;
    }

    public int getVolume() {
        return volume;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Barrel getBarrel() {
        return barrel;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public void setBarrel(Barrel barrel) {
        this.barrel = barrel;
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
        return "DrinkRecord{" + "id=" + idDrinkRecord + ", volume=" + volume + ", consumer=" + consumer + ", barrel=" + barrel + '}';
    }
}
