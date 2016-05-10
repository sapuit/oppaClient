package vn.soaap.onlinepharmacy.entities;

import java.io.Serializable;

/**
 * Created by sapui on 4/11/2016.
 */
public class User implements Serializable {

    String name;
    String phone;
    String address;
    String token;

    public User() {
    }

    public User(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public User(String name, String phone, String address, String token) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
