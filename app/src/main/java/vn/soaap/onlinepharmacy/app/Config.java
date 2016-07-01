package vn.soaap.onlinepharmacy.app;

import android.os.Environment;

/**
 *
 */
public class Config {

    public static final String host = "ktvonline.vn/oppaServer";
//    public static final String host = "10.11.61.2";
    public static final String URL_UPLOAD       = "http://" + host + "/prescription/post-image";
    public static final String UPLOAD_LIST_URL  = "http://" + host + "/prescription/post-drugs";
    public static final String URL_CONFORM      = "http://" + host + "/prescription/conform";

    /* flag để xác định show 1 text
     * hay nhiều dòng text trong push notification
     * */
    public static boolean appendNotificationMessages = true;

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER     = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE    = "registrationComplete";
    public static final String PUSH_NOTIFICATION        = "pushNotification";

    // type of push messages
    // yêu cầu gửi lại đơn thuốc
    public static final int NOTIFICATION_RESEND_PRE = 1;
    // hiện tại chưa thể đáp ứng được đơn thuốc
    public static final int NOTIFICATION_UNAVAILABLE_PRE = -1;
    //  xác nhận có lấy đơn thuốc hay không
    public static final int NOTIFICATION_CONFIRM_PRE = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //  Message
    public static final String MESSAGE_TYPE     = "type";
    public static final String MESSAGE_MESSAGE  = "message";
    public static final String MESSAGE_FLAG     = "flag";
    public static final String MESSAGE_TITLE    = "title";
    public static final String MESSAGE_TIMESTAMP = "timestamp";
    public static final String MESSAGE_SERVER = "server";
    public static final String MESSAGE_USER = "user";

    //  type prescription
    public static final int PRESCRIPTION_LIST = 0;
    public static final int PRESCRIPTION_IMAGE = 1;

    public static final String APP_FOLDER =
            Environment.getExternalStorageDirectory().getPath() + "/oppa/";
    public static final String FOLDER_IMAGE= APP_FOLDER + "images/";

}
