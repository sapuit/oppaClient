package vn.soaap.onlinepharmacy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.adapter.PrescriptionAdapter;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.recyclerview.ItemTouchListenerAdapter;
import vn.soaap.onlinepharmacy.recyclerview.SwipeToDismissTouchListener;

public class InfoInputActivity extends AppCompatActivity {

    MaterialEditText etAddress;
    MaterialEditText etPhoneNum;
    MaterialEditText etName;
    Button footer_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);

        initView();
    }

    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        etName     = (MaterialEditText) findViewById(R.id.etName);
        etAddress  = (MaterialEditText) findViewById(R.id.etAddress);
        etPhoneNum = (MaterialEditText) findViewById(R.id.etPhoneNum);
        footer_next = (Button) findViewById(R.id.footer_next);

//        fab.setEnabled(false);
//        fab.setBackgroundColor(getResources().getColor(R.color.colorDivider));

        etAddress.setEnabled(false);
        etPhoneNum.setEnabled(false);
        footer_next.setEnabled(false);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etAddress.setEnabled(s.toString().trim().length() > 3);
                etPhoneNum.setEnabled(s.toString().trim().length() > 5);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etAddress.setEnabled(s.toString().trim().length() > 3);
                footer_next.setEnabled(s.toString().trim().length() > 3);
                if(s.toString().trim().length() > 3)
                    footer_next.setTextColor(Color.BLACK);
                else
                    footer_next.setTextColor(Color.parseColor("#d7d7d7"));

//                fab.setEnabled(s.toString().trim().length() > 3);
//                fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final Intent intent = new Intent(this, DrugsInputActivity.class);


        footer_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String address = etAddress.getText().toString();
                String phoneNum = etPhoneNum.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("address", address);
                bundle.putString("phoneNum", phoneNum);
                intent.putExtra("info", bundle);
                startActivity(intent);

            }
        });


    }
}
