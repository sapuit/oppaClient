package vn.soaap.onlinepharmacy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.gcm.GcmIntentService;
import vn.soaap.onlinepharmacy.helper.NetworkHelper;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final int INPUT_HAND = 0;
    private final int INPUT_IMAGE = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String token;
    @Bind(R.id.btnHandInput)
    Button btnHandInput;
    @Bind(R.id.btnImageInput)
    Button btnImageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        btnHandInput.setOnClickListener(this);
        btnHandInput.setEnabled(false);
        btnImageInput.setOnClickListener(this);
        btnImageInput.setEnabled(false);

        getToken();
    }

    @Override
    public void onClick(View v) {

        //  Kiểm tra kết nối
        if (!NetworkHelper.isNetworkConnected(getApplicationContext()))
            return;

        Log.e(TAG, "GCM registration token" + token);

        if (token == null)
            return;

        Intent intent = new Intent(this, InfoInputActivity.class);
        intent.putExtra("token", token);
        switch (v.getId()) {
            case R.id.btnHandInput:
                intent.putExtra("action", INPUT_HAND);
                break;
            case R.id.btnImageInput:
                intent.putExtra("action", INPUT_IMAGE);
                break;
        }
        startActivity(intent);
    }

    public void getToken() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    token = intent.getStringExtra("token");
//                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();
                    btnHandInput.setEnabled(true);
                    btnImageInput.setEnabled(true);
                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
//                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    showNotification(
                            intent.getIntExtra("flag", 0),
                            intent.getStringExtra("title"),
                            intent.getStringExtra("message"));
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }
    }


    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //  Check play service
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void showNotification(int flag, String title, String message) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);

        builder.title(title);
        //  xác nhận lấy toa thuốc
        if (flag == 2) {
            builder.content("Xử lý toa thuốc hoàn tất. Vui lòng xác nhận lại để nhận thuốc tại quầy.");
            builder.positiveText("Xác nhận")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_LONG).show();
                        }
                    })
                    .negativeText("Hủy bỏ");
        } else {
            builder.content(message)
                    .positiveText("Đóng");
        }

        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}
