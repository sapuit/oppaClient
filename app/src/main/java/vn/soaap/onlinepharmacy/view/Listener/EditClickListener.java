package vn.soaap.onlinepharmacy.view.Listener;

import android.view.View;

import vn.soaap.onlinepharmacy.view.Listener.DrugClickListener;

/**
 * Created by sapui on 6/13/2016.
 */
public class EditClickListener implements View.OnClickListener {

    int pos;
    DrugClickListener callback;

    public EditClickListener(int pos, DrugClickListener callback) {
        this.pos = pos;
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        callback.OnEditClick(v,pos);
    }

}
