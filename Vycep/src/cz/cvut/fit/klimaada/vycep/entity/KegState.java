/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.klimaada.vycep.entity;

/**
 * @author Adam
 */
public enum KegState {

    STOCKED, TAPPED, FINISHED;

    @Override
    public String toString() {
        switch (this) {
            case FINISHED:
                return "Vypito";
            case STOCKED:
                return "Skladem";
            case TAPPED:
                return "Naraženo";
            default:
                return "";

        }
    }

    public String getToString() {
        switch (this) {
            case FINISHED:
                return "Vypito";
            case STOCKED:
                return "Skladem";
            case TAPPED:
                return "Naraženo";
            default:
                return "";

        }
    }
}
