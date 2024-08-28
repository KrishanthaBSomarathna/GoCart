package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.OrderItemListView;
import com.example.gocart.Model.OrderDetail;
import com.example.gocart.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Order4Adapter extends RecyclerView.Adapter<Order4Adapter.OrderDetailViewHolder> {
    private final String userId;
    private final String customerId;
    private final String specificDate;
    private List<OrderDetail> orderDetailList;
    private Context context; // Update to private

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdView;
        public TextView addressView;
        public LinearLayout itemIdsContainer;
        public ImageButton completebtn;

        public OrderDetailViewHolder(View v) {
            super(v);
            orderIdView = v.findViewById(R.id.OrderId);
            addressView = v.findViewById(R.id.Address);
            itemIdsContainer = v.findViewById(R.id.ItemIdsContainer);
            completebtn = v.findViewById(R.id.orderDetails);
        }
    }

    // Updated constructor to include context
    public Order4Adapter(Context context, List<OrderDetail> orderDetailList, String userId, String customerId, String specificDate) {
        this.context = context; // Initialize the context
        this.orderDetailList = orderDetailList;
        this.userId = userId;
        this.customerId = customerId;
        this.specificDate = specificDate;
    }

    @NonNull
    @Override
    public Order4Adapter.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        // Handle the complete button click to update the order status in Firebase
//        holder.completebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Customer")
//                        .child(customerId)
//                        .child("Orders")
//                        .child(specificDate)
//                        .child(orderDetail.getOrderId())
//                        .child("status");
//
//                orderRef.setValue("Complete").addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(context, "Order marked as complete.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "Failed to update order status.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });

        holder.completebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderItemListView.class);
                context.startActivity(intent);
            }
        });
        // Optional: You can remove this Toast if not needed
        Toast.makeText(context, "Order ID: " + orderDetail.getOrderId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }
}
