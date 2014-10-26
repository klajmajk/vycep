/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Adam
 */

public class BarrelKind implements Serializable {

    private int idBarrelKind;
    private String breweryName;
    private String beerName;
    

    public BarrelKind() {
    }

    public int getId() {
        return idBarrelKind;
    }
    

    public BarrelKind(int volume, String name) {
        this.breweryName = name;
    }

   

    public String getBreweryName() {
        return breweryName;
    }

    public void setBreweryName(String name) {
        this.breweryName = name;
    }
    
    
    
    

    public String getBeerName() {
		return beerName;
	}

	public void setBeerName(String beerName) {
		this.beerName = beerName;
	}

	
	
   
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beerName == null) ? 0 : beerName.hashCode());
		result = prime * result
				+ ((breweryName == null) ? 0 : breweryName.hashCode());
		result = prime * result + idBarrelKind;
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
		BarrelKind other = (BarrelKind) obj;
		if (beerName == null) {
			if (other.beerName != null)
				return false;
		} else if (!beerName.equals(other.beerName))
			return false;
		if (breweryName == null) {
			if (other.breweryName != null)
				return false;
		} else if (!breweryName.equals(other.breweryName))
			return false;
		if (idBarrelKind != other.idBarrelKind)
			return false;
		return true;
	}

	public String getToString(){
        return toString();
    }

	@Override
	public String toString() {
		return idBarrelKind + ": "
				+ breweryName + " - " + beerName;
	}
    
    

}
