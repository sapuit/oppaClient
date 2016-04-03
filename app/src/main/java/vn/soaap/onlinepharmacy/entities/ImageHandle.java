package vn.soaap.onlinepharmacy.entities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
;import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.server.RequestHandler;
import vn.soaap.onlinepharmacy.server.RequestListener;

/**
 * Created by Administrator on 2/27/2016.
 */
public class ImageHandle {
    String TAG = "ImageHandle";
    Bitmap bitmap;
    Context context;

    public ImageHandle(Context context, Intent data, int type) {
        this.context = context;
        if (type == 1)
            this.bitmap = onSelectFromGalleryResult(context, data);
        else if (type == 2)
            this.bitmap = onCaptureImageResult(data);

    }

    public ImageHandle(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap onSelectFromGalleryResult(Context context, Intent data) {

        Uri filePath = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, Bitmap.DENSITY_NONE, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumbnail;
    }

    public void uploadImage(String UPLOAD_URL) {
        final String KEY_IMAGE = "image";
        final String KEY_EMAIL = "email";
        final String KEY_PHONE = "phone";
        final String KEY_NAME = "name";
        final String KEY_ADDR = "addr";
        final String KEY_STATUS = "status";
        final String KEY_TOTAL = "total";

        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {

            String image = getStringImage(bitmap);
            params.put(KEY_NAME, "Sap");
            params.put(KEY_PHONE, "123");
            params.put(KEY_ADDR, "1324");
            params.put(KEY_NAME, "sdfsd");
            params.put(KEY_EMAIL, "gsfdg");
            params.put(KEY_IMAGE, image);
            params.put(KEY_STATUS, "asfd");
            params.put(KEY_TOTAL, "total");

            Log.d(TAG,"image : " + image);
            entity = new StringEntity(params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestHandler handler = RequestHandler.getInstance();
        handler.make_post_Request(context, entity, UPLOAD_URL, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String server_response = String.valueOf(new String(response, "UTF-8"));
                        Log.v("Server response", server_response);
                } catch (UnsupportedEncodingException e1) {}
            }
        });
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //  láy hình thumbnail tối ưu bộ nhớ
    public Bitmap getThumbnail(String pathHinh)
    {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathHinh, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;
        int originalSize = (bounds.outHeight > bounds.outWidth) ?
                bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 500;
        return BitmapFactory.decodeFile(pathHinh, opts);
    }
}
