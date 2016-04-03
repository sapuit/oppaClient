package vn.soaap.onlinepharmacy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.entities.ImageHandle;

public class InfoInputActivity extends AppCompatActivity {

    MaterialEditText etAddress;
    MaterialEditText etPhoneNum;
    MaterialEditText etName;
    Button footer_next;
    int action;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    ImageHandle imageHandle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        action = getIntent().getIntExtra("action", 0);
        if (action == 0)
            initView();
        else clickTakePhoto();
    }


    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        etName = (MaterialEditText) findViewById(R.id.etName);
        etAddress = (MaterialEditText) findViewById(R.id.etAddress);
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
                etPhoneNum.setEnabled(s.toString().trim().length() > 3);
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
                int minimum = 6;
                etAddress.setEnabled(s.toString().trim().length() > minimum);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        etAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int minimum = 6;
                footer_next.setEnabled(s.toString().trim().length() > minimum);
                if (s.toString().trim().length() > minimum) {
                    footer_next.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_black, 0);
                    footer_next.setTextColor(Color.BLACK);
                } else {
                    footer_next.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_gray, 0);
                    footer_next.setTextColor(Color.parseColor("#d7d7d7"));
                }
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
                bundle.putInt("action", action);
                if (action == 1)
                    intent.putExtra("BitmapImage", imageHandle.getBitmap());
                intent.putExtra("info", bundle);
                startActivity(intent);
            }
        });

    }


    private void clickTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == SELECT_FILE)
                imageHandle = new ImageHandle(this, data, 1);
            else if (requestCode == REQUEST_CAMERA)
                imageHandle = new ImageHandle(this, data, 2);

            initView();
//            bitmap = imageHandle.getBitmap();
//            ivImage.setImageBitmap(bitmap);
        } else {
            RelativeLayout rl_info = (RelativeLayout) findViewById(R.id.rl_info);
            final Snackbar snackbar = Snackbar
                    .make(rl_info, "Chụp hình bị lỗi", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickTakePhoto();
                        }
                    });

            snackbar.show();
        }

    }
}
