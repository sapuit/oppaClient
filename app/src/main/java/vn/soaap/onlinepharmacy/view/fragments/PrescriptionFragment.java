package vn.soaap.onlinepharmacy.view.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.app.Config;
import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.Message;
import vn.soaap.onlinepharmacy.view.adapter.ChatRoomAdapter;
import vn.soaap.onlinepharmacy.view.recyclerview.SimpleDividerItemDecoration;

/**
 * Created by sapui on 6/5/2016.
 */
public class PrescriptionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PrescriptionFragment.class.getSimpleName();

    private Context context;

    private ChatRoomAdapter adapter;

    NavigationView mNavigationView;

    private RecyclerView rvMessage;

    private Button btnNegative;
    private Button btnPositive;
    Message lastMessage;

    public PrescriptionFragment() {
    }

    public PrescriptionFragment(Context context, NavigationView mNavigationView) {
        this.context = context;
        this.mNavigationView = mNavigationView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pre, container, false);

        addView(root);
        addEvent();
        return root;
    }

    private void addView(View rootView) {
        rvMessage = (RecyclerView) rootView.findViewById(R.id.rvMessage);
        btnNegative = (Button) rootView.findViewById(R.id.btnNegative);
        btnPositive = (Button) rootView.findViewById(R.id.btnPositive);
    }

    private void addEvent() {
        btnNegative.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
        btnNegative.setVisibility(View.GONE);

        ArrayList<Message> messageList = getMessageFromPref();
        adapter = new ChatRoomAdapter(context, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setReverseLayout(true);
        rvMessage.setLayoutManager(layoutManager);
        rvMessage.setItemAnimator(new DefaultItemAnimator());
        rvMessage.setAdapter(adapter);

        lastMessage = getLastMessage();

        if (lastMessage == null) {
            btnPositive.setText("Gửi toa thuốc");
            showToast("Chưa có toa thuốc ");
            return;
        }

        if (lastMessage.getType().equals(Config.MESSAGE_SERVER)) {
            int flag = lastMessage.getFlag();
            Log.d(TAG, String.valueOf(flag));

            if (flag == Config.NOTIFICATION_RESEND_PRE ||
                    flag == Config.NOTIFICATION_UNAVAILABLE_PRE ||
                    flag == 101) {   //  không gửi lên được
                btnNegative.setVisibility(View.GONE);
                btnPositive.setVisibility(View.VISIBLE);
                btnPositive.setText("Gửi lại toa thuốc");
            } else {
                btnNegative.setVisibility(View.VISIBLE);
                btnPositive.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNegative:
                // từ chối yêu cầu từ server
                break;
            case R.id.btnPositive:
                positiveClick();
                break;
        }
    }

    private static final String MAIN_FRAGMENT = "main_fragment";
    private void positiveClick() {
//        if (lastMessage.getFlag() == Config.NOTIFICATION_UNAVAILABLE_PRE ) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, new MainFragment(context), MAIN_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mNavigationView.getMenu().getItem(0).setChecked(true);
//        }
    }

    public ArrayList<Message> getMessageFromPref() {
        ArrayList<Message> resultArr = new ArrayList<>();

        // get the notifications from shared preferences
        String oldNotification = MyApplication.getInstance().getPrefManager().getNotifications();
        if (oldNotification == null)
            return null;
        try {
            List<String> messagesArr = Arrays.asList(oldNotification.split("\\|"));

            if (messagesArr == null)
                return null;

            String jsonStr;
            JSONObject jsonObject;
            Message message;

            for (int i = messagesArr.size() - 1; i >= 0; i--) {

                jsonStr = messagesArr.get(i);
                jsonObject = new JSONObject(jsonStr);
                message = new Message();
                message.setType(jsonObject.getString(Config.MESSAGE_TYPE));
                message.setFlag(jsonObject.getInt(Config.MESSAGE_FLAG));
                message.setTitle(jsonObject.getString(Config.MESSAGE_TITLE));
                message.setMessage(jsonObject.getString(Config.MESSAGE_MESSAGE));
                message.setCreatedAt(jsonObject.getString(Config.MESSAGE_TIMESTAMP));
                resultArr.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return resultArr;
    }

    public Message getLastMessage() {

        Message message = new Message();

        // get the notifications from shared preferences
        String oldNotification = MyApplication.getInstance().getPrefManager().getNotifications();
        if (oldNotification == null)
            return null;
        try {
            List<String> messagesArr = Arrays.asList(oldNotification.split("\\|"));
            if (messagesArr == null)
                return null;

            String jsonStr = messagesArr.get(messagesArr.size() - 1);
            JSONObject jsonObject = new JSONObject(jsonStr);
            message.setType(jsonObject.getString(Config.MESSAGE_TYPE));
            message.setFlag(jsonObject.getInt(Config.MESSAGE_FLAG));
            message.setMessage(jsonObject.getString(Config.MESSAGE_MESSAGE));
            message.setCreatedAt(jsonObject.getString(Config.MESSAGE_TIMESTAMP));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    private void showToast(String message) {
        try {

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
