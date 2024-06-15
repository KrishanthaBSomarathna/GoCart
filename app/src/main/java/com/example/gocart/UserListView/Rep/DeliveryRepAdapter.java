package com.example.gocart.UserListView.Rep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.User;
import com.example.gocart.R;

import java.util.List;

public class DeliveryRepAdapter extends RecyclerView.Adapter<DeliveryRepAdapter.DeliveryRepViewHolder> {
    private List<User> deliveryRepList;

    public static class DeliveryRepViewHolder extends RecyclerView.ViewHolder {
        public TextView deliveryRepUid;
        public TextView deliveryRepName;
        public ImageButton deliveryRepEdit;
        public ImageButton deliveryRepDelete;

        public DeliveryRepViewHolder(View itemView) {
            super(itemView);
            deliveryRepUid = itemView.findViewById(R.id.user_unicid);
            deliveryRepName = itemView.findViewById(R.id.user_name);
            deliveryRepEdit = itemView.findViewById(R.id.user_edit);
            deliveryRepDelete = itemView.findViewById(R.id.user_del);
        }
    }

    public DeliveryRepAdapter(List<User> deliveryRepList) {
        this.deliveryRepList = deliveryRepList;
    }

    @Override
    public DeliveryRepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new DeliveryRepViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeliveryRepViewHolder holder, int position) {
        User currentItem = deliveryRepList.get(position);

        holder.deliveryRepUid.setText(currentItem.getUid());
        holder.deliveryRepName.setText(currentItem.getName());

        holder.deliveryRepEdit.setOnClickListener(v -> {
            // Handle edit action
        });

        holder.deliveryRepDelete.setOnClickListener(v -> {
            // Handle delete action
        });
    }

    @Override
    public int getItemCount() {
        return deliveryRepList.size();
    }
}
