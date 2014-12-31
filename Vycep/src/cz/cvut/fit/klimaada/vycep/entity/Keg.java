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
public class Keg implements Serializable {
    private int id;
    private Date dateAdd;
    private Date dateTap;
    private Date dateEnd;
    private double price;
    private Beer beer;
    private int volume;

    private KegState state;


    public Keg() {
        this.state = KegState.STOCKED;
    }


    public Keg(Date dateAdd, double price, Beer beer, int volume) {
        this.dateAdd = dateAdd;
        this.price = price;
        this.beer = beer;
        this.state = KegState.STOCKED;
        this.volume = volume;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public Date getDateTap() {
        return dateTap;
    }

    public void setDateTap(Date dateTap) {
        this.dateTap = dateTap;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Beer getKind() {
        return beer;
    }

    public void setKind(Beer kind) {
        this.beer = kind;
    }

    public KegState getState() {
        return state;
    }

    public void setState(KegState state) {
        this.state = state;
    }

    public int getVolume() {
        return volume;
    }


    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((beer == null) ? 0 : beer.hashCode());
        result = prime * result
                + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((dateAdd == null) ? 0 : dateAdd.hashCode());
        result = prime * result + id;
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((dateTap == null) ? 0 : dateTap.hashCode());
        result = prime * result + volume;
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Keg other = (Keg) obj;
        if (beer == null) {
            if (other.beer != null)
                return false;
        } else if (!beer.equals(other.beer))
            return false;
        if (state != other.state)
            return false;
        if (dateAdd == null) {
            if (other.dateAdd != null)
                return false;
        } else if (!dateAdd.equals(other.dateAdd))
            return false;
        if (id != other.id)
            return false;
        if (Double.doubleToLongBits(price) != Double
                .doubleToLongBits(other.price))
            return false;
        if (dateTap == null) {
            if (other.dateTap != null)
                return false;
        } else if (!dateTap.equals(other.dateTap))
            return false;
        if (volume != other.volume)
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Barrel [id=" + id + ", dateAdd=" + dateAdd + ", dateTap=" + dateTap
                + ", price=" + price + ", kind=" + beer + ", state="
                + state + "]";
    }

    public String getToString() {
        return toString();
    }


}
