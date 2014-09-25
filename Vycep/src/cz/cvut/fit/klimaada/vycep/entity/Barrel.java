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
    private int idBarrel;
    private Date bought;
    private Date taped;   
    private double price;
    private BarrelKind barrelKind;
    private int volume;
                
    private BarrelState barrelState;
    
    
    public Barrel() {
        this.barrelState = BarrelState.STOCK;
    }    
    

    public Barrel(Date bought, double price, BarrelKind kind) {
        this.bought = bought;
        this.price = price;
        this.barrelKind = kind;
    }

    public int getId() {
        return idBarrel;
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
        return barrelKind;
    }

    public void setKind(BarrelKind kind) {
        this.barrelKind = kind;
    }

    public BarrelState getBarrelState() {
        return barrelState;
    }

    public void setBarrelState(BarrelState state) {
        this.barrelState = state;
    }

    public int getVolume() {
		return volume;
	}


	public void setVolume(int volume) {
		this.volume = volume;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((barrelKind == null) ? 0 : barrelKind.hashCode());
		result = prime * result
				+ ((barrelState == null) ? 0 : barrelState.hashCode());
		result = prime * result + ((bought == null) ? 0 : bought.hashCode());
		result = prime * result + idBarrel;
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((taped == null) ? 0 : taped.hashCode());
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
		Barrel other = (Barrel) obj;
		if (barrelKind == null) {
			if (other.barrelKind != null)
				return false;
		} else if (!barrelKind.equals(other.barrelKind))
			return false;
		if (barrelState != other.barrelState)
			return false;
		if (bought == null) {
			if (other.bought != null)
				return false;
		} else if (!bought.equals(other.bought))
			return false;
		if (idBarrel != other.idBarrel)
			return false;
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price))
			return false;
		if (taped == null) {
			if (other.taped != null)
				return false;
		} else if (!taped.equals(other.taped))
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Barrel [id=" + idBarrel + ", bought=" + bought + ", taped=" + taped
				+ ", price=" + price + ", kind=" + barrelKind + ", barrelState="
				+ barrelState + "]";
	}

	public String getToString() {
        return toString();
    }
    
    
    
    
    
    
    
    
    
    
    
}
