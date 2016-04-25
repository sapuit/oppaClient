package vn.soaap.onlinepharmacy.entities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.download.RequestHandler;
import vn.soaap.onlinepharmacy.download.RequestListener;
import vn.soaap.onlinepharmacy.util.Config;

/**
 * Created by sapui on 4/11/2016.
 */
public class ImagePre extends Prescription {

    private Bitmap image;

    public ImagePre(User user) {
        super(user);
    }

    public ImagePre(Context context, User user, Uri uri) {
        super(user);

        try {
            this.image = scaleBitmapDown(MediaStore.Images.Media
                    .getBitmap(context.getContentResolver(), uri), 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    boolean result = false;
    @Override
    public boolean send(Activity context) {
        if (user == null || image == null)
            return false;

        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            String image = getStringImage(getImage());
            params.put(Config.KEY_NAME, user.getName());
            params.put(Config.KEY_PHONE, user.getPhone());
            params.put(Config.KEY_ADDR, user.getAddress());
            //  params.put(Config.KEY_EMAIL, "example@gmail.com");
            params.put(Config.KEY_IMAGE, image);

            entity = new StringEntity(params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestHandler handler = RequestHandler.getInstance();
        handler.make_post_Request(context, entity, Config.UPLOAD_URL, new RequestListener() {
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

    private Bitmap onSelectFromGalleryResult(Context context, Intent data) {

        Uri filePath = data.getData();
        Bitmap bitmap = null;
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

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

}
