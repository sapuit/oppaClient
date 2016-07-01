package vn.soaap.onlinepharmacy.entities;

/**
 * Created by sapui on 2/15/2016.
 */
public class Drug {

    int id;
    String name;
    int quantity;

    public Drug(int id, String name) {
        this.id = id;
        this.name = name;
        this.quantity = 0;
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

    public int getQuatity() {
        return quantity;
    }

    public void setQuatity(int quantity) {
        this.quantity = quantity;
    }

}
