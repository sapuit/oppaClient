package vn.soaap.onlinepharmacy.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.gcm.GcmIntentService;
import vn.soaap.onlinepharmacy.helper.ImageHelper;
import vn.soaap.onlinepharmacy.util.PermissionUtils;

public class InfoInputActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GALLERY_IMAGE_REQUEST = 1;

    public static final int CAMERA_IMAGE_REQUEST = 2;

    public static final int CAMERA_PERMISSIONS_REQUEST = 3;

    private final int INPUT_HAND = 0;

    private final int INPUT_IMAGE = 1;

    private final int EDIT_INFO = 2;

    private int action;

    private Bitmap image;

    private User user;

    private File imageFile;

    //  View
    @Bind(R.id.tvTitle) TextView tvTitle;

    @Bind(R.id.footer_next) Button footer_next;

    @Bind(R.id.etAddress) MaterialEditText etAddress;

    @Bind(R.id.etPhoneNum) MaterialEditText etPhoneNum;

    @Bind(R.id.etName) MaterialEditText etName;

    @BindColor(R.color.colorPrimaryDark) int colorPrimaryDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  ẩn status bar
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        setContentView(R.layout.activity_info_input);
        ButterKnife.bind(this);

        addEvent();
    }


    private void addEvent() {
        imageFile = getCameraFile();
        action = getIntent().getIntExtra("action", 0);
        user = MyApplication.getInstance().getPrefManager().getUser();

        switch (action) {
            case INPUT_HAND:
                init();
                break;
            case INPUT_IMAGE:
                startCamera();
                break;
            case EDIT_INFO:
                footer_next.setText("Hoàn Tất");
                tvTitle.setText("Chỉnh sửa thông tin cá nhân");
                etName.setText(user.getName());
                etPhoneNum.setText(user.getPhone());
                etAddress.setText(user.getAddress());
                etName.setSelection(etName.getText().length());

                break;
        }
    }
    private void startCamera() {

        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    //  lấy hình ảnh trả về
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setInitialRotation(90)
                        .start(this);

            } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
                Uri uri = Uri.fromFile(imageFile);
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri uri = result.getUri();
                    image = ImageHelper.scaleBitmapDown(MediaStore.Images.Media
                            .getBitmap(getApplicationContext().getContentResolver(), uri), 300);
                    init();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

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
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(colorPrimaryDark);

        //  Nếu user đã nhập thông tin thì
        //  không cần hiển thị lại form
        if (user == null)
            user = new User();
        else
            moveDrugsActivity();
    }


    @OnClick(R.id.footer_next)
    public void clickNext() {
        if (saveUserToSharePr())
            moveDrugsActivity();
    }

    //  chuyển activity
    private void moveDrugsActivity() {
        try {
            if (action == EDIT_INFO) {
                finish();
                return;
            }

            final Intent intent = new Intent(this, DrugsInputActivity.class);
            intent.putExtra("action", action);
            if (action == INPUT_IMAGE)
                intent.putExtra("image", image);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  Kiểm tra thông tin nhập
    private boolean checkInfo(String name, String address, String phoneNum) {

        if (name.length() < 3 || address.length() < 3 || phoneNum.length() < 8) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //  Kiểm tra permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
    }

    //  Lưu thông tin user trong sharePr
    private boolean saveUserToSharePr() {
        try {
            String name = etName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phoneNum = etPhoneNum.getText().toString().trim();

            boolean check = checkInfo(name, address, phoneNum);
            if (!check)
                return false;
            user.setName(name);
            user.setAddress(address);
            user.setPhone(phoneNum);

            MyApplication.getInstance().getPrefManager().clear();
            MyApplication.getInstance().getPrefManager().storeUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //  lấy file hình
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "prescription_" + timeStamp + ".jpg";
        return new File(dir, fileName);
    }

    //  Lấy hình từ gallery
    public void startGalleryChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

}
