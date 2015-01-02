package cz.cvut.fit.klimaada.vycep.entity;

/**
 * Created by Adam on 26. 12. 2014.
 */
public class Brewery {
    private int id;
    private String name;

    public Brewery() {
    }

    public Brewery(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brewery)) return false;

        Brewery brewery = (Brewery) o;

        if (id != brewery.id) return false;
        if (!name.equals(brewery.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
