package vn.soaap.onlinepharmacy.entities;

/**
 * Created by sapui on 2/15/2016.
 */
public class Drug {

    int id;
    String name;
    int quatities;

    public Drug(int id, String name) {
        this.id = id;
        this.name = name;
        this.quatities = 0;
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

    public int getQuatities() {
        return quatities;
    }

    public void setQuatities(int quatities) {
        this.quatities = quatities;
    }
}
