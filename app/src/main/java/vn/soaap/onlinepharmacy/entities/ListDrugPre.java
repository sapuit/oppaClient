package vn.soaap.onlinepharmacy.entities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
        StringEntity entity = null;
        try {

            JSONArray jsonArray = new JSONArray();
            for (Drug d : drugs) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", d.getName());
                jsonObject.put("quantity", d.getQuatity());
                jsonArray.put(jsonObject);
            }

            params.put(context.getString(R.string.key_drugs), jsonArray);
            params.put(context.getString(R.string.key_name), user.getName());
            params.put(context.getString(R.string.key_phone), user.getPhone());
            params.put(context.getString(R.string.key_address), user.getAddress());
            params.put(context.getString(R.string.key_token), user.getToken());
            //  params.put(Config.KEY_EMAIL, "example@gmail.com");

            Log.i("request content", params.toString());
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
                        new AlertDialogWrapper.Builder(context)
                                .setTitle("Gửi thành công")
                                .setMessage("Đơn thuốc đang được xử lý. Vui lòng chờ đợi kết quả trong ít phút.")
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
                } catch (UnsupportedEncodingException e1) {
                    Log.i("sendRequest", e1.getMessage());
                    result = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                result = false;
            }
        });

        return result;
    }
}
