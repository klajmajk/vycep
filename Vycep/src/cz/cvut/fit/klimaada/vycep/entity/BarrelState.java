/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

/**
 *
 * @author Adam
 */
public enum BarrelState {

    STOCK, TAPED, FINISHED;

    @Override
    public String toString() {
        switch (this) {
            case FINISHED:
                return "Vypito";
            case STOCK:
                return "Skladem";
            case TAPED:
                return "Naraženo";
            default:
                return "";

        }
    }
    
    public String getToString() {
        switch (this) {
            case FINISHED:
                return "Vypito";
            case STOCK:
                return "Skladem";
            case TAPED:
                return "Naraženo";
            default:
                return "";

        }
    }
}
