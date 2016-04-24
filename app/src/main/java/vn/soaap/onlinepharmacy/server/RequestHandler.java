package vn.soaap.onlinepharmacy.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.activity.DrugsInputActivity;

/**
 * Created by Administrator on 3/21/2016.
 */
public class RequestHandler {

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
                Log.e(" GET FAILED ", url);
                Log.e(" GET FAILED ", e.getLocalizedMessage());

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

    public void make_post_Request(final Activity context, final StringEntity entity, final String url, final RequestListener listener) {
        client.post(context, url, entity, "application/json", new AsyncHttpResponseHandler() {

            MaterialDialog dialog;
            @Override
            public void onStart() {
                Log.v(" POST ", "Start");
                Log.v(" POST ", url);
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
                Log.v(" POST ", "Success");
                dialog.dismiss();
                new AlertDialogWrapper.Builder(context)
                        .setTitle("Gửi thành công")
                        .setMessage("Đơn thuốc đang được xử lý. Vui lòng chờ đợi kết quả trong ít phút.")
                        .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(" POST FAILED ", url);
                Log.e(" POST FAILED ", context.getClass().getSimpleName() + " -> " + e.getLocalizedMessage());

                if (DUtils.isDebuggable(context) && SHOW_DEBUG_ALERT_DIALOG) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle(" ERROR ");
//                    String error_msg;
//                    if (errorResponse != null) {
//                        try {
//                            error_msg = String.valueOf(new String(errorResponse, "UTF-8"));
//                        } catch (UnsupportedEncodingException e1) {
//                            error_msg = e.getLocalizedMessage();
//                        }
//                    } else {
//                        error_msg = e.getLocalizedMessage();
//                    }
//
//                    builder.setMessage(context.getClass().getSimpleName() + " -> " + error_msg)
//                            .setCancelable(true)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();
                    dialog.dismiss();

                    new AlertDialogWrapper.Builder(context)
                            .setTitle("Gửi không thành công")
                            .setMessage("Vui lòng kiểm tra lại kết nối internet hoặc liên hệ với chúng tôi theo sô điện thọa 0987654321")
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
