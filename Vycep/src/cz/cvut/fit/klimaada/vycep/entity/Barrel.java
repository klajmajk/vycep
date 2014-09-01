/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fit.klimaada.vycep.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import android.os.Parcelable;

/**
 *
 * @author Adam
 */
public class Barrel implements Serializable {
    private int id;
    private Date bought;
    private Date taped;   
    private double price;
    private BarrelKind kind;
                
    private BarrelState barrelState;
    
    
    public Barrel() {
        this.barrelState = BarrelState.STOCK;
    }    
    

    public Barrel(Date bought, double price, BarrelKind kind) {
        this.bought = bought;
        this.price = price;
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public Date getBought() {
        return bought;
    }

    public void setBought(Date bought) {
        this.bought = bought;
    }

    public Date getTaped() {
        return taped;
    }

    public void setTaped(Date taped) {
        this.taped = taped;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BarrelKind getKind() {
        return kind;
    }

    public void setKind(BarrelKind kind) {
        this.kind = kind;
    }

    public BarrelState getBarrelState() {
        return barrelState;
    }

    public void setBarrelState(BarrelState state) {
        this.barrelState = state;
    }
    
    
    
    
    
    

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((barrelState == null) ? 0 : barrelState.hashCode());
		result = prime * result + ((bought == null) ? 0 : bought.hashCode());
		result = prime * result + id;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((taped == null) ? 0 : taped.hashCode());
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
		Barrel other = (Barrel) obj;
		if (barrelState != other.barrelState)
			return false;
		if (bought == null) {
			if (other.bought != null)
				return false;
		} else if (!bought.equals(other.bought))
			return false;
		if (id != other.id)
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price))
			return false;
		if (taped == null) {
			if (other.taped != null)
				return false;
		} else if (!taped.equals(other.taped))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Barrel [id=" + id + ", bought=" + bought + ", taped=" + taped
				+ ", price=" + price + ", kind=" + kind + ", barrelState="
				+ barrelState + "]";
	}


	public String getToString() {
        return toString();
    }
    
    
    
    
    
    
    
    
    
    
    
}
