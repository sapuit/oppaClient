package vn.soaap.onlinepharmacy.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.soaap.onlinepharmacy.R;

import vn.soaap.onlinepharmacy.app.MyApplication;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.entities.User;
import vn.soaap.onlinepharmacy.view.Listener.DeleteClickListener;
import vn.soaap.onlinepharmacy.view.Listener.DrugClickListener;
import vn.soaap.onlinepharmacy.view.Listener.EditClickListener;
import vn.soaap.onlinepharmacy.view.recyclerview.RecyclerViewAdapter;

/**
 * Created by sapui on 2/15/2016.
 */
public class PrescriptionAdapter extends RecyclerViewAdapter<Drug, RecyclerView.ViewHolder> {

    static  final String TAG = "PrescriptionAdapter";
    static final int TYPE_HEADER = 0;

    static final int TYPE_CELL = 1;

    static final int TYPE_IMAGE = 2;

    List<Drug> items = new ArrayList<>();

    Context context;

    DrugClickListener callback;

    boolean isImage = false;

    Bitmap image = null;

    public PrescriptionAdapter(Context context, List<Drug> items, DrugClickListener callback) {
        setHasStableIds(true);
        this.context = context;
        this.items = items;
        this.callback = callback;
    }

    public PrescriptionAdapter(Context context, boolean isImage, Bitmap image) {
        setHasStableIds(true);
        this.context = context;
        this.isImage = isImage;
        this.image = image;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                if (isImage)
                    return TYPE_IMAGE;
                else
                    return TYPE_CELL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                return new InfoViewHolder(view) {
                };
            }

            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new DrugViewHolder(view) {
                };
            }

            case TYPE_IMAGE : {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_image, parent, false);
                return new ImageViewHolder(view) {
                };
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);

        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                InfoViewHolder holder = (InfoViewHolder) viewHolder;
                User user = MyApplication.getInstance().getPrefManager().getUser();
                holder.userName.setText(user.getName());
                holder.userPhoneNum.setText(user.getPhone());
                holder.userAddr.setText(user.getAddress());
                if (isImage)
                    holder.btnAddDrug.setVisibility(View.INVISIBLE);
                break;

            case TYPE_IMAGE:
                ImageViewHolder imageHolder = (ImageViewHolder) viewHolder;
                if (image != null)
                    imageHolder.ivPre.setImageBitmap(image);
                break;

            case TYPE_CELL:
                final Drug drug = items.get(position);
                DrugViewHolder Drugholder = (DrugViewHolder) viewHolder;
                Drugholder.tvDrugName.setText(String.valueOf(position) + ". " + drug.getName());
                Drugholder.tvDrugQuan.setText("Số lượng : " + String.valueOf(drug.getQuatity()));
                Drugholder.imDelete.setOnClickListener(new DeleteClickListener(position, callback));
                Drugholder.imEdit.setOnClickListener(new EditClickListener(position, callback));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (isImage)
            return 2;
        return items.size();
    }

    @Override
    public void swapPositions(int from, int to) {
        Collections.swap(items, from, to);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        return items.get(position).getId();
    }


    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.btnAddDrug) Button btnAddDrug;

        @Bind(R.id.userName) TextView userName;

        @Bind(R.id.userPhone) TextView userPhoneNum;

        @Bind(R.id.userAddr) TextView userAddr;

        public InfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class DrugViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvDrugName) TextView tvDrugName;

        @Bind(R.id.tvDrugQuan) TextView tvDrugQuan;

        @Bind(R.id.imEdit) ImageView imEdit;

        @Bind(R.id.imDelete) ImageView imDelete;

        public DrugViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivPre) ImageView ivPre;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}

