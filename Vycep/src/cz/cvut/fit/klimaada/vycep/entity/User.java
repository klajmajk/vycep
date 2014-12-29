package cz.cvut.fit.klimaada.vycep.entity;


import android.util.Log;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Adam
 */
public class User implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private double balance;

    public User() {
    }

    public int getId() {
        return id;
    }


    public User(String name) {
        this.name = name;
        this.balance = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public String getName() {
        return name;
    }

    public void creditChange(double change) {
        balance += change;
    }


    public double getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jméno: " + name + " kredit: " + balance + " Kč";
    }

    public String getToString() {
        return name;
    }


    public static int parseIdFromNFC(String data) {
        Log.d("COnsumer", "data: " + data + " replaced: " + data.replace("kumpan: ", ""));
        return Integer.parseInt(data.replace("kumpan: ", "").trim());
    }


}
