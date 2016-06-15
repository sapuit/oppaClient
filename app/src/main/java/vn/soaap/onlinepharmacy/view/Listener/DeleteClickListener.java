package vn.soaap.onlinepharmacy.view.Listener;

import android.view.View;

/**
 * Created by sapui on 6/13/2016.
 */
public class DeleteClickListener implements View.OnClickListener {

    int pos;
    DrugClickListener callback;

    public DeleteClickListener(int pos, DrugClickListener callback) {
        this.pos = pos;
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        callback.OnDeleteClick(v,pos);
    }

}
