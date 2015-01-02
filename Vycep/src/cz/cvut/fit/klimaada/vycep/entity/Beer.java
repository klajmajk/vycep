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
    private String name;
    private Brewery brewery;

    public Beer() {
    }

    public Beer(String name, Brewery brewery) {
        this.name = name;
        this.brewery = brewery;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!name.equals(beer.name)) return false;
        if (!brewery.equals(beer.brewery)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + brewery.hashCode();
        return result;
    }

    public String getToString() {
        return toString();
    }

    @Override
    public String toString() {
        return name;
    }


}
