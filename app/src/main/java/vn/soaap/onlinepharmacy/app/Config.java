package vn.soaap.onlinepharmacy.app;

/**
 *
 */
public class Config {
    // flag để xác định show 1 text
    // hay nhiều dòng text trong  push notification
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

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


}
