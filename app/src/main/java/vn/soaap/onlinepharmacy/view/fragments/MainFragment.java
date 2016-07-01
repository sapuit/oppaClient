package vn.soaap.onlinepharmacy.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.activity.InfoInputActivity;
import vn.soaap.onlinepharmacy.helper.NetworkHelper;

/**
 * Created by sapui on 6/5/2016.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private final int INPUT_HAND = 0;

    private final int INPUT_IMAGE = 1;

    private Context mContext;

    private Button btnHandInput;
    private Button btnImageInput;

    public MainFragment() {
    }

    public MainFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        addView(rootView);
        return rootView;
    }

    private void addView(View rootView) {
        btnHandInput = (Button) rootView.findViewById(R.id.btnHandInput);
        btnImageInput = (Button) rootView.findViewById(R.id.btnImageInput);
        btnHandInput.setOnClickListener(this);
        btnImageInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!NetworkHelper.isNetworkConnected(mContext))
            return;

        final Intent intent = new Intent(mContext, InfoInputActivity.class);
        switch (v.getId()) {
            case R.id.btnHandInput:
                intent.putExtra("action", INPUT_HAND);
                break;
            case R.id.btnImageInput:
                intent.putExtra("action", INPUT_IMAGE);
                break;
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 400);
    }

    private void showNotice(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }


}
