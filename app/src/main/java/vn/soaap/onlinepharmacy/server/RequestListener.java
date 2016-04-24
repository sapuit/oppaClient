package vn.soaap.onlinepharmacy.server;

import cz.msebera.android.httpclient.Header;

public interface RequestListener {
    public void onSuccess(int statusCode, Header[] headers, byte[] response);
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e);
}
