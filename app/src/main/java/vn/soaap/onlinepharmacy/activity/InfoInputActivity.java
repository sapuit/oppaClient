package vn.soaap.onlinepharmacy.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.entities.ImagePre;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.util.PermissionUtils;

public class InfoInputActivity extends AppCompatActivity {


    private static final int GALLERY_IMAGE_REQUEST = 1;
    public  static final int CAMERA_IMAGE_REQUEST  = 2;
    public  static final int CAMERA_PERMISSIONS_REQUEST = 3;
    private static final String TAG = MainActivity.class.getSimpleName();
    private int    action;
    public  String FILE_NAME = "image.jpg";
    private ImagePre imagePre;

//    View
    @Bind(R.id.footer_next)
    Button footer_next;
    @Bind(R.id.etAddress)
    MaterialEditText etAddress;
    @Bind(R.id.etPhoneNum)
    MaterialEditText etPhoneNum;
    @Bind(R.id.etName)
    MaterialEditText etName;
    @BindColor(R.color.colorPrimaryDark) int colorPrimaryDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);
        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(colorPrimaryDark);

        action = getIntent().getIntExtra("action", 0);
        if (action == 0)    //  select input prescription by hand
            initView();
        else startCamera();
//        startGalleryChooser();
    }

    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setEnabled(false);
//        fab.setBackgroundColor(getResources().getColor(R.color.colorDivider));

        etName.setText("Nguyen kien phuoc ");
        etPhoneNum.setText("3242355435");
        etAddress.setText("123 tan phu");
        etAddress.setEnabled(etPhoneNum.getText().length() > 3);
        etPhoneNum.setEnabled(etName.getText().length() > 3);
        footer_next.setEnabled(etAddress.getText().length() > 3);


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
                footer_next.setEnabled(s.toString().trim().length() > 6);
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
    }

    @OnClick(R.id.footer_next)
    public void clickNext(){
        final Intent intent = new Intent(this, DrugsInputActivity.class);

        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String phoneNum = etPhoneNum.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("name",     name);
        bundle.putString("address",  address);
        bundle.putString("phoneNum", phoneNum);
        bundle.putInt("action", action);

        if (action == 1)
            intent.putExtra("BitmapImage", imagePre.getImage());
        intent.putExtra("info", bundle);
        startActivity(intent);
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    private void startCamera() {

        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {

        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
             imagePre = new ImagePre(
                    getBaseContext(),
                    new User(),
                    data.getData());
            initView();
//            imageHandle = new ImageHandle(this, data, 1);
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {

             imagePre = new ImagePre(
                    getBaseContext(),
                    new User(),
                    Uri.fromFile(getCameraFile()));
             initView();
//            imageHandle = new ImageHandle(this, data, 2);
        } else {
            RelativeLayout rl_info = (RelativeLayout) findViewById(R.id.rl_info);
            final Snackbar snackbar = Snackbar
                    .make(rl_info, "Chụp hình bị lỗi", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startCamera();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
    }
}
