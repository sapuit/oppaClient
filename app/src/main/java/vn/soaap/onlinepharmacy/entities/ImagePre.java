package vn.soaap.onlinepharmacy.entities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.afollestad.materialdialogs.AlertDialogWrapper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.activity.MainActivity;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.util.download.RequestHandler;
import vn.soaap.onlinepharmacy.util.download.RequestListener;


public class ImagePre extends Prescription {
    private static final String TAG = ImagePre.class.getSimpleName();

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


    @Override
    public boolean send(final Activity context) {

        JSONObject params = new JSONObject();
        try {
            String image = getStringImage(getImage());
            params.put(context.getString(R.string.key_name), user.getName());
            params.put(context.getString(R.string.key_phone), user.getPhone());
            params.put(context.getString(R.string.key_address), user.getAddress());
            params.put(context.getString(R.string.key_token), user.getToken());
            params.put(context.getString(R.string.key_image), image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestHandler handler = RequestHandler.getInstance();
        handler.make_post_Request(context, params, Config.URL_UPLOAD, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String server_response = String.valueOf(new String(response, "UTF-8"));
                    Log.e(TAG, server_response);
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
                } catch (UnsupportedEncodingException e1) {
                    Log.e(TAG, e1.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            }
        });
        return true;
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
