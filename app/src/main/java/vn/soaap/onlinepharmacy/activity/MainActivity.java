package vn.soaap.onlinepharmacy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.download.RequestHandler;
import vn.soaap.onlinepharmacy.download.RequestListener;
import vn.soaap.onlinepharmacy.gcm.GcmIntentService;
import vn.soaap.onlinepharmacy.helper.NetworkHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final int INPUT_HAND = 0;
    private final int INPUT_IMAGE = 1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String token;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    @Bind(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        addEvent();
        getToken();
    }

    private void addEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onClick(View v) {

        //  Kiểm tra kết nối
        if (!NetworkHelper.isNetworkConnected(getApplicationContext()))
            return;

        Log.e(TAG, "GCM registration token" + token);
        if (token == null) {
            notice("Not get token !");
            return;
        }
        final Intent intent = new Intent(this, InfoInputActivity.class);
        intent.putExtra("token", token);

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

    public void getToken() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    token = intent.getStringExtra("token");
//                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

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
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("flag", "1");
                                StringEntity entity = new StringEntity(jsonObject.toString());

                                RequestHandler handler = RequestHandler.getInstance();
                                handler.make_post_Request(MainActivity.this, entity,
                                        vn.soaap.onlinepharmacy.util.Config.CONFORM_URL, new RequestListener() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                                try {
                                                    String server_response = String.valueOf(new String(response, "UTF-8"));
                                                    if (server_response.equals("0")) {

                                                    }
                                                } catch (UnsupportedEncodingException e1) {
                                                    Log.e(TAG, e1.getMessage());

                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {

                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            notice("gallery");
        } else if (id == R.id.nav_slideshow) {
            notice("slideshow");
        } else if (id == R.id.nav_manage) {
            notice("manage");
        } else if (id == R.id.nav_share) {
            notice("share");
        } else if (id == R.id.nav_send) {
            notice("Send");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void notice(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
