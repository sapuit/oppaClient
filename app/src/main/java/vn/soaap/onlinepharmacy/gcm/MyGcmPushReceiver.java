package vn.soaap.onlinepharmacy.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import vn.soaap.onlinepharmacy.activity.MainActivity;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.app.MyApplication;

/**
 * method will be triggered whenever device receives new push notification
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils mNotification;

    /*
    *   Called when message is received
    *
    *   @param bundle Data bundle containing message data as key/value pairs.
    *               For Set of keys use data.keySet().
    * */
    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        try {
            int flag  = Integer.parseInt(bundle.getString("flag"));
            String title = bundle.getString("title");
            String messages = bundle.getString("message");
            String timestamp = bundle.getString("created_at");
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "flag: " + flag);
            Log.d(TAG, "title: " + title);
            Log.d(TAG, "message: " + messages);
            Log.d(TAG, "timestamp: " + timestamp);
//            Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
            Boolean isBackground = false;
            Log.d(TAG, "isBackground: " + isBackground);

            if (flag == 2) {
                String data = bundle.getString("data");
                Log.d(TAG, "data: " + data);
                processUserMessage(flag,title, messages, timestamp,isBackground, data);

            }else {
                processUserMessage(flag,title, messages, timestamp,isBackground,null);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processUserMessage(int flag, String title, String message, String timestamp,boolean isBackground,String data) {

        // store the notification in shared pref first
        saveMessagePref(flag,title,message,timestamp);

        if (!isBackground) {

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("flag", flag);
                pushNotification.putExtra("title", title);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("flag", flag);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("message", message);

//            if (TextUtils.isEmpty(image)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//            } else {
//                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
//            }
            }
        }else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     *  store the notification in shared pref first
     */
    private void saveMessagePref(int flag, String title, String message, String timestamp) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Config.MESSAGE_TYPE, Config.MESSAGE_SERVER);
            jsonObject.put(Config.MESSAGE_FLAG,flag);
            jsonObject.put(Config.MESSAGE_TITLE,title);
            jsonObject.put(Config.MESSAGE_MESSAGE, message);
            jsonObject.put(Config.MESSAGE_TIMESTAMP,timestamp);

            Log.d(TAG,jsonObject.toString());
            MyApplication.getInstance().getPrefManager().addNotification(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        mNotification = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotification.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        mNotification = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotification.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}
