package vn.soaap.onlinepharmacy.entities;

import android.content.Context;

/**
 * Created by sapui on 4/11/2016.
 */
public abstract class Prescription implements Action {

    public User user;

    public Prescription(User user) {
        this.user = user;
    }

    @Override
    public abstract boolean send(Context context);
}
