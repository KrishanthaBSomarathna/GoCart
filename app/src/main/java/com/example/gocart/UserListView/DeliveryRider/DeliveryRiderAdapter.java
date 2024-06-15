package com.example.gocart.UserListView.DeliveryRider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.DeliveryRider;
import com.example.gocart.R;

import java.util.List;

public class DeliveryRiderAdapter extends RecyclerView.Adapter<DeliveryRiderAdapter.DeliveryRiderViewHolder> {
    private List<DeliveryRider> riderList;

    public static class DeliveryRiderViewHolder extends RecyclerView.ViewHolder {
        public TextView riderUid;
        public TextView riderName;
        public ImageButton riderEdit;
        public ImageButton riderDelete;

        public DeliveryRiderViewHolder(View itemView) {
            super(itemView);
            riderUid = itemView.findViewById(R.id.user_unicid);
            riderName = itemView.findViewById(R.id.user_name);
            riderEdit = itemView.findViewById(R.id.user_edit);
            riderDelete = itemView.findViewById(R.id.user_del);
        }
    }

    public DeliveryRiderAdapter(List<DeliveryRider> riderList) {
        this.riderList = riderList;
    }

    @Override
    public DeliveryRiderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new DeliveryRiderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeliveryRiderViewHolder holder, int position) {
        DeliveryRider currentItem = riderList.get(position);

        holder.riderUid.setText(currentItem.getUid());
        holder.riderName.setText(currentItem.getName());

        holder.riderEdit.setOnClickListener(v -> {
            // Handle edit action
        });

        holder.riderDelete.setOnClickListener(v -> {
            // Handle delete action
        });
    }

    @Override
    public int getItemCount() {
        return riderList.size();
    }
}
