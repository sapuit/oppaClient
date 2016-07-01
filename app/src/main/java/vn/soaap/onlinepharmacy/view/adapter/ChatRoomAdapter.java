package vn.soaap.onlinepharmacy.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.entities.Message;

/**
 * Created by sapui on 6/21/2016.
 */
public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomAdapter.class.getSimpleName();

    private int SELF = 100;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        LinearLayout lnPreMessage;
        RoundedImageView ivPreMessage;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            lnPreMessage = (LinearLayout) itemView.findViewById(R.id.lnPreMessage);
            ivPreMessage = (RoundedImageView) itemView.findViewById(R.id.ivPreMessage);
        }
    }

    public ChatRoomAdapter(Context mContext, ArrayList<Message> messageArrayList) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getType().equals("user")) {
            return SELF;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);

        if (message.getType().equals("user")) {
            setMessageUser(holder, message);
        } else {
            setMessageServer(holder, message);
        }

        String timestamp = getTimeStamp(message.getCreatedAt());
        ((ViewHolder) holder).timestamp.setText(timestamp);
    }

    private void setMessageServer(RecyclerView.ViewHolder holder, Message message) {
        ((ViewHolder) holder).message.setText(message.getTitle() + "\n" + message.getMessage());
    }

    private void setMessageUser(RecyclerView.ViewHolder holder, Message message) {

        String drugs = "";
        try {

            if (message.getFlag() == Config.PRESCRIPTION_LIST) {
                JSONObject drugsJson = new JSONObject(message.getMessage());
                JSONArray jsonArr = drugsJson.getJSONArray("drugs");

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject json = jsonArr.getJSONObject(i);
                    drugs += String.valueOf(i + 1) + ". " + json.getString("name") + "\n"
                            + "\tsố lượng : " + String.valueOf(json.getInt("quantity"));
                    if (i != jsonArr.length() - 1)
                        drugs += "\n";
                }

                ((ViewHolder) holder).message.setText(message.getTitle() + " đã gửi lên : \n" + drugs);

            } else if (message.getFlag() == Config.PRESCRIPTION_IMAGE) {
                Bitmap image = MediaStore.Images.Media
                        .getBitmap(mContext.getContentResolver(), Uri.parse(message.getMessage()));

                ((ViewHolder) holder).message.setVisibility(View.GONE);
                ((ViewHolder) holder).lnPreMessage.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).ivPreMessage.setImageBitmap(image);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (messageArrayList == null)
            return 0;
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";
        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
