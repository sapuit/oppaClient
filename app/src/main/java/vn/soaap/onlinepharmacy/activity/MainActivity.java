package vn.soaap.onlinepharmacy.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.Message;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.util.FolderHelper;
import vn.soaap.onlinepharmacy.util.download.RequestHandler;
import vn.soaap.onlinepharmacy.util.download.RequestListener;
import vn.soaap.onlinepharmacy.gcm.GcmIntentService;
import vn.soaap.onlinepharmacy.view.fragments.MainFragment;
import vn.soaap.onlinepharmacy.view.fragments.PrescriptionFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MAIN_FRAGMENT = "main_fragment";

    private final int EDIT_INFO = 2;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String mToken;

    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Bind(R.id.nav_view) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        addView();
        addEvent();
    }

    private void addView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_main);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fl_main, new MainFragment(getBaseContext()), MAIN_FRAGMENT).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG,"onResum");

        String oldNotification = MyApplication.getInstance().getPrefManager().getNotifications();

        if (oldNotification != null) {
            List<String> messages = Arrays.asList(oldNotification.split("\\|"));
            for (int i = messages.size() - 1; i >= 0; i--) {
                Log.d(TAG, messages.get(i));
            }
        }
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user != null) {
            addNavigation(user);
        } else {
            mNavigationView.setVisibility(View.GONE);
        }
    }

    private void addNavigation(User user) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        //  set data navigation
        View header = mNavigationView.getHeaderView(0);
        TextView txtName    = (TextView) header.findViewById(R.id.txtName);
        TextView txtPhone   = (TextView) header.findViewById(R.id.txtPhone);
        TextView txtAddress = (TextView) header.findViewById(R.id.txtAddress);
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
    }

    private void addEvent() {
        FolderHelper.makeAppFolderIfNotExist();
        receiveResponse();
    }

    public void receiveResponse() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    mToken = intent.getStringExtra("token");
                    MyApplication.getInstance().getPrefManager().storeToken(mToken);
                    Log.i(TAG, "GCM registration token : " + mToken);

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.d(TAG,"PUSH_NOTIFICATION");
                    handlePushNotification(intent);

                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }
    }

    /*
    *   starting the service to register with GCM
    */
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    /*
    *   Check play service
    */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                showNotice("This device is not supported. Google Play Services not installed!");
                finish();
            }
            return false;
        }
        return true;
    }

    private void handlePushNotification(Intent intent) {
        addPrescriptionFragment();

        final int flag = intent.getIntExtra("flag",0);
        String title   = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        String id = "";
        try {
            JSONObject obj = new JSONObject(message);
            id = obj.getString("_id");
        } catch (Throwable t) {
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(title);

        if (flag == 2) {    //  xác nhận lấy toa thuốc
            builder.content("Xử lý toa thuốc hoàn tất. Vui lòng xác nhận lại để nhận thuốc tại quầy.");
            final String finalId = id;
            builder.positiveText("Xác nhận")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            sendRequest("1", finalId, mToken);
                        }
                    })
                    .negativeText("Hủy bỏ")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            sendRequest("0", finalId, mToken);
                        }
                    });

        } else {    //  hiển thị thông báo khác
            builder.content(message)
                    .positiveText("Đóng");
        }

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void sendRequest(String flag, String finalId, String token) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", flag);
            jsonObject.put("id", finalId);
            jsonObject.put("mToken", token);
            Log.i(TAG, jsonObject.toString());

            RequestHandler handler = RequestHandler.getInstance();
            handler.make_post_Request(MainActivity.this, jsonObject,
                    Config.URL_CONFORM, new RequestListener() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            try {
                                String server_response = String.valueOf(new String(response, "UTF-8"));
                                if (server_response.equals("OK")) {
                                    new AlertDialogWrapper.Builder(getApplicationContext())
                                            .setTitle("Xác nhận thành công")
                                            .setMessage("Vui lòng đến quầy thuốc để nhận thuốc.")
                                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
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

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
            return;
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
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_main:
                addView();
                break;
//
//            case R.id.nav_handle:
//                break;
//
//            case R.id.nav_receive:
//                break;

            case R.id.nav_pre:
                addPrescriptionFragment();
                break;

            case R.id.nav_edit:
                Intent intent = new Intent(this, InfoInputActivity.class);
                intent.putExtra("action", EDIT_INFO);
                startActivity(intent);
                break;

            case R.id.nav_help:
                break;

            default:
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addPrescriptionFragment() {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Tình trạng đơn thuốc");

        ArrayList<Message> messageArrayList = new ArrayList<>();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fl_main,
                new PrescriptionFragment(getApplicationContext(),mNavigationView), "MAIN_FRAGMENT").commit();
    }

    private void showNotice(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
