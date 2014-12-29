/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

import java.io.Serializable;

/**
 * @author Adam
 */

public class Beer implements Serializable {

    private int id;
    private String beerName;
    private Brewery brewery;


    public Beer() {
    }

    public int getId() {
        return id;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public Brewery getBrewery() {
        return brewery;
    }

    public void setBrewery(Brewery brewery) {
        this.brewery = brewery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beer)) return false;

        Beer beer = (Beer) o;

        if (id != beer.id) return false;
        if (!beerName.equals(beer.beerName)) return false;
        if (!brewery.equals(beer.brewery)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + beerName.hashCode();
        result = 31 * result + brewery.hashCode();
        return result;
    }

    public String getToString() {
        return toString();
    }

    @Override
    public String toString() {
        return id + ": "
                + brewery.getName() + " - " + beerName;
    }


}
