package cz.cvut.fit.klimaada.vycep.entity;


import java.io.Serializable;
import java.util.Objects;

import android.util.Log;

/**
 *
 * @author Adam
 */
public class Consumer implements Serializable {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idConsumer;   
    private String name;  
    private double credit;

    public Consumer() {
    }

    public int getIdConsumer() {
        return idConsumer;
    }
    
    

    public Consumer(String name) {
        this.name = name;
        this.credit = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

  

    public String getName() {
        return name;
    }
    
    public void creditChange (double change){
        credit += change;
    }
    
    
    public double getCredit() {
        return credit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.idConsumer;
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
        final Consumer other = (Consumer) obj;
        if (this.idConsumer != other.idConsumer) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jméno: "+name+" kredit: "+credit+" Kè";
    }
    
    public String getToString(){
        return name;
    }

 
    public static int parseIdFromNFC(String data){
    	Log.d("COnsumer", "data: "+data+" replaced: "+ data.replace("kumpan: ", ""));
    	return Integer.parseInt(data.replace("kumpan: ", "").trim());
    }
    
    
    
}
