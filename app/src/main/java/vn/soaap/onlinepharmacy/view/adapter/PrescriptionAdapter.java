package vn.soaap.onlinepharmacy.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.soaap.onlinepharmacy.R;
import vn.soaap.onlinepharmacy.entities.Drug;
import vn.soaap.onlinepharmacy.view.recyclerview.RecyclerViewAdapter;

/**
 * Created by sapui on 2/15/2016.
 */
public class PrescriptionAdapter extends RecyclerViewAdapter<Drug, PrescriptionAdapter.DrugViewHolder> {

    List<Drug> items;

    public static class DrugViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_item_drug_name)
        TextView tvDrugName;
        @Bind(R.id.tv_item_drug_quantities)
        TextView tvDrugQuan;

        public DrugViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        viewHolder.tvDrugQuan.setText("Số lượng : " + String.valueOf(drug.getQuatity()));
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

