package vn.soaap.onlinepharmacy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import vn.soaap.onlinepharmacy.R;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Button btnHandInput = (Button) findViewById(R.id.btnHandInput);
        Button btnImageInput = (Button) findViewById(R.id.btnImageInput);

        btnHandInput.setOnClickListener(this);
        btnImageInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,InfoInputActivity.class);

        switch (v.getId()) {
            case R.id.btnHandInput:
                intent.putExtra("action", 0);
                break;
            case R.id.btnImageInput:
                intent.putExtra("action",1);
                break;
        }

        startActivity(intent);
    }
}
