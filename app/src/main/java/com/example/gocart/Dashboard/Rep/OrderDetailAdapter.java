package com.example.gocart.Dashboard.Rep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.OrderDetail;
import com.example.gocart.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetail> orderDetailList;

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdView;
        public TextView addressView;
        public LinearLayout itemIdsContainer;
        public ImageButton orderDetailsButton;

        public OrderDetailViewHolder(View v) {
            super(v);
            orderIdView = v.findViewById(R.id.OrderId);
            addressView = v.findViewById(R.id.Address);
            itemIdsContainer = v.findViewById(R.id.ItemIdsContainer);
            orderDetailsButton = v.findViewById(R.id.orderDetails);
        }
    }

    public OrderDetailAdapter(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new OrderDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
        holder.orderIdView.setText(orderDetail.getOrderId());
        holder.addressView.setText(orderDetail.getAddress());

        // Clear previous views to avoid duplication
        holder.itemIdsContainer.removeAllViews();

        // Dynamically add TextViews for each item ID
        for (String itemId : orderDetail.getItemIds()) {
            TextView itemIdView = new TextView(holder.itemView.getContext());
            itemIdView.setText(itemId);
            holder.itemIdsContainer.addView(itemIdView);
        }

        // Set onClickListener for the details button if needed
        holder.orderDetailsButton.setOnClickListener(v -> {
            // Handle details button click, e.g., open a new activity with order details
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }
}
