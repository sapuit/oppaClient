package vn.soaap.onlinepharmacy.entities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.activity.MainActivity;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.util.download.RequestHandler;
import vn.soaap.onlinepharmacy.util.download.RequestListener;

/**
 * Created by sapui on 4/11/2016.
 */
public class ListDrugPre extends Prescription {

    List<Drug> drugs;

    public ListDrugPre(User user, List<Drug> drugs) {
        super(user);
        this.drugs = drugs;
    }

    boolean result = false;

    @Override
    public boolean send(final Activity context) {

        user = MyApplication.getInstance().getPrefManager().getUser();
        if (user == null || drugs == null && drugs.size() == 0)
            return false;

        JSONObject params = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i < drugs.size(); i++) {
                Drug d = drugs.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", d.getName());
                jsonObject.put("quantity", d.getQuatity());
                jsonArray.put(jsonObject);
            }

            params.put(context.getString(R.string.key_drugs), jsonArray);
            params.put(context.getString(R.string.key_name), user.getName());
            params.put(context.getString(R.string.key_phone), user.getPhone());
            params.put(context.getString(R.string.key_address), user.getAddress());
            params.put(context.getString(R.string.key_token),
                    MyApplication.getInstance().getPrefManager().getToken());

            saveMessagePref(Config.MESSAGE_USER, Config.PRESCRIPTION_LIST, "Toa thuốc ", params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestHandler handler = RequestHandler.getInstance();
        handler.make_post_Request(context, params, Config.UPLOAD_LIST_URL, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String server_response = String.valueOf(new String(response, "UTF-8"));
                    Log.i("sendRequest", server_response);
                    if (server_response.equals("OK")) {
                        showDialog(context,
                                context.getResources().getString(R.string.title_send_pre_success),
                                context.getResources().getString(R.string.message_send_pre_success));
                        saveMessagePref(Config.MESSAGE_SERVER, 100,
                                context.getResources().getString(R.string.title_send_pre_success),
                                context.getResources().getString(R.string.message_send_pre_success));
                    }
                } catch (UnsupportedEncodingException e1) {
                    Log.i("sendRequest", e1.getMessage());
                    result = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d("sendRequest", "Gửi không thành công");
                saveMessagePref(Config.MESSAGE_SERVER, 101,
                        context.getResources().getString(R.string.title_send_pre_fail),
                        context.getResources().getString(R.string.message_send_pre_fail));

                showDialog(context,
                        context.getResources().getString(R.string.title_send_pre_fail),
                        context.getResources().getString(R.string.message_send_pre_fail));
                result = false;
            }
        });

        return result;
    }

    private void showDialog(final Context context, String title, String message) {
        new AlertDialogWrapper.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //  back to first activity
                        Intent i = new Intent(context.getApplicationContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
                }).show();
    }

    /**
     * store the notification in shared pref first
     */
    private void saveMessagePref(String type, int flag, String title, String message) {
        try {

            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            String timestamp = s.format(new Date());
            Log.d("date", timestamp);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Config.MESSAGE_TYPE, type);
            jsonObject.put(Config.MESSAGE_FLAG, flag);
            jsonObject.put(Config.MESSAGE_TITLE, title);
            jsonObject.put(Config.MESSAGE_MESSAGE, message);
            jsonObject.put(Config.MESSAGE_TIMESTAMP, timestamp);

            MyApplication.getInstance().getPrefManager().addNotification(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
