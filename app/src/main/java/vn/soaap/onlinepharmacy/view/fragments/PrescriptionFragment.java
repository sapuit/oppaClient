package vn.soaap.onlinepharmacy.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.soaap.onlinepharmacy.R;

/**
 * Created by sapui on 6/5/2016.
 */
public class PrescriptionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pre, container,false);

        return root;
    }
}
