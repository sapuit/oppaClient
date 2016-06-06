package vn.soaap.onlinepharmacy.util.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.activity.MainActivity;

/**
 * Created by Administrator on 3/21/2016.
 */
public class RequestHandler {
    private static String TAG = "RequestHandler";
    private static RequestHandler instance;

    private AsyncHttpClient client;
    private static final boolean SHOW_DEBUG_ALERT_DIALOG = true;

    private RequestHandler() {
        client = new AsyncHttpClient();
    }

    public static RequestHandler getInstance() {
        if (instance == null) {
            instance = new RequestHandler();
        }
        return instance;
    }


    public void make_get_Request(final Context context, final String url, final RequestListener listener) {
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.v(" GET ", url);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                listener.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                listener.onFailure(statusCode, headers, errorResponse,e);
                Log.e(TAG, url);
                Log.e(TAG, e.getLocalizedMessage());

                if (DUtils.isDebuggable(context) && SHOW_DEBUG_ALERT_DIALOG) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(" ERROR ");
                    String error_msg;
                    if (errorResponse != null) {
                        try {
                            error_msg = String.valueOf(new String(errorResponse, "UTF-8"));
                        } catch (UnsupportedEncodingException e1) {
                            error_msg = e.getLocalizedMessage();
                        }
                    } else {
                        error_msg = e.getLocalizedMessage();
                    }

                    builder.setMessage(context.getClass().getSimpleName() + " -> " + error_msg)
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                Log.e(" RETRYING ", "....." + String.valueOf(retryNo));

            }
        });
    }

    public void make_post_Request(final Activity context, final JSONObject jsonObject, final String url, final RequestListener listener) {
        StringEntity entity =  new StringEntity(jsonObject.toString(), "UTF-8");

        client.post(context, url,entity , "application/json", new AsyncHttpResponseHandler() {

            MaterialDialog dialog;
            @Override
            public void onStart() {
                Log.v(TAG, "Start");
                Log.v(TAG, url);
                 dialog =  new MaterialDialog.Builder(context)
                        .title(R.string.progress_dialog)
                        .content(R.string.please_wait)
                        .progress(true, 0)
                         .canceledOnTouchOutside(false)
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                listener.onSuccess(statusCode, headers, response);
                Log.v(TAG, "Success");
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(" POST FAILED ", context.getClass().getSimpleName() + " -> " + e.getLocalizedMessage());
                if (DUtils.isDebuggable(context) && SHOW_DEBUG_ALERT_DIALOG) {
                    dialog.dismiss();
                    new AlertDialogWrapper.Builder(context)
                            .setTitle("Gửi không thành công")
                            .setMessage("Vui lòng kiểm tra lại kết nối hoặc liên hệ trực tiếp với nhà thuốc.")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                Log.e("RETRYING ", "....." + String.valueOf(retryNo));
            }
        });
    }

}
