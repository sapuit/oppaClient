package vn.soaap.onlinepharmacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.recyclerview.RecyclerViewAdapter;

/**
 * Created by sapui on 2/15/2016.
 */
public class PrescriptionAdapter extends RecyclerViewAdapter<Drug, PrescriptionAdapter.DrugViewHolder> {

    List<Drug> items;

    public static class DrugViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDrugName;
        public TextView tvDrugQuan;
        public ImageButton btnbtnRemove;

        public DrugViewHolder(View itemView) {
            super(itemView);

            tvDrugName = (TextView)
                    itemView.findViewById(R.id.tv_item_drug_name);
            tvDrugQuan = (TextView)
                    itemView.findViewById(R.id.tv_item_drug_quantities);
            btnbtnRemove = (ImageButton)
                    itemView.findViewById(R.id.btnRemove);
        }
    }

    public PrescriptionAdapter(List<Drug> items) {
        setHasStableIds(true);
        this.items = items;
    }

    @Override
    public DrugViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_drug, viewGroup, false);
        return new DrugViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DrugViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        Drug drug = items.get(position);
        viewHolder.tvDrugName.setText(String.valueOf(position + 1) + ". " + drug.getName());
        viewHolder.tvDrugQuan.setText(" x " + String.valueOf(drug.getQuatities()));
        viewHolder.btnbtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
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

    public void removeItem(int pos) {
        items.remove(pos);
        notifyItemRemoved(pos);
    }
}

