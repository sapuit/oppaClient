package vn.soaap.onlinepharmacy.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import vn.soaap.onlinepharmacy.entities.User;

/**
 * save data into SharePreference
 */
public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context context;

    //  shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "oppa_pref";

    // All Shared Preferences Keys
    //    private static final String KEY_USER_EMAIL = "user_email";
//    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ADDR = "user_address";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_NOTIFICATIONS = "notifications";

    //  Contructor
    public MyPreferenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //  save notifications from server
    public void addNotification(String notification) {
        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_ADDR, user.getAddress());
        editor.commit();
    }

    public User getUser() {
        if (pref.getString(KEY_USER_PHONE, null) != null) {
            String name, phone, addr;
            name = pref.getString(KEY_USER_NAME, null);
            phone = pref.getString(KEY_USER_PHONE, null);
            addr = pref.getString(KEY_USER_ADDR, null);

            User user = new User(name, phone, addr);
            return user;
        }
        return null;
    }

    public void storeToken(String token) {
        editor.putString(KEY_USER_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_USER_TOKEN, null);
    }


    public void clear() {
        editor.clear();
        editor.commit();
    }
}
