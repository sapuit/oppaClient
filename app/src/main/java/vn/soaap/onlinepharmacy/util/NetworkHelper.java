package vn.soaap.onlinepharmacy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by Administrator on 3/21/2016.
 */
public class NetworkHelper {

    static String TAG = "NetworkHelper";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        boolean b = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!b)
            Toast.makeText(context, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();

        return b;
    }

}
