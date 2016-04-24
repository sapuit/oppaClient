package vn.soaap.onlinepharmacy.entities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.server.RequestHandler;
import vn.soaap.onlinepharmacy.server.RequestListener;
import vn.soaap.onlinepharmacy.util.GlobalParams;

/**
 * Created by sapui on 4/11/2016.
 */
public class ListDrugPre extends Prescription {

    List<Drug> drugs;

    public ListDrugPre(User user) {
        super(user);
    }

    public ListDrugPre(User user,List<Drug> drugs) {
        super(user);
        this.drugs = drugs;
    }

    public List<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }

    boolean result =false;
    @Override
    public boolean send(Context context) {
        if (user == null || drugs == null)
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

            params.put(GlobalParams.KEY_DRUGS, jsonArray);
            params.put(GlobalParams.KEY_NAME,  user.getName());
            params.put(GlobalParams.KEY_PHONE, user.getPhone());
            params.put(GlobalParams.KEY_ADDR,  user.getAddress());
            //  params.put(GlobalParams.KEY_EMAIL, "example@gmail.com");

            entity = new StringEntity(params.toString());
            Log.i("request content",params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestHandler handler = RequestHandler.getInstance();
        handler.make_post_Request(context, entity, GlobalParams.UPLOAD_LIST_URL, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String server_response = String.valueOf(new String(response, "UTF-8"));
                    Log.i("sendRequest", server_response);
                    if (server_response.equals("OK")) {
                        result = true;
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
