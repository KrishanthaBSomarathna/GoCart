package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gocart.Model.OrderItem;
import com.example.gocart.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private Context context;
    private List<OrderItem> orderItemList;
    private String customerId, orderId, date, role;
    String newStatus = "Item Confirmed";

    public OrderItemAdapter(Context context, List<OrderItem> orderItemList, String customerId, String orderId, String date, String role) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.customerId = customerId;
        this.orderId = orderId;
        this.date = date;
        this.role = role;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_view, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);
        holder.itemName.setText(orderItem.getItemName());
        holder.itemValue.setText(orderItem.getValue());
        holder.price.setText("Rs: " + orderItem.getUnitPrice());
        holder.qty.setText("Qty: " + orderItem.getQuantity());
        holder.status.setText("Status: " + orderItem.getStatus());

        // Update status to "Item Confirmed"
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newStatus = "Item Confirmed";
                updateItemStatus(orderItem, newStatus);
                orderItem.setStatus(newStatus);
                holder.status.setText("Status: " + orderItem.getStatus()); // Update the UI

                Toast.makeText(context, "Item status updated to Confirmed", Toast.LENGTH_SHORT).show();
            }
        });

        // Update status to "Item Not Available"
        holder.na.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newStatus = "Item Not Available";
                updateItemStatus(orderItem, newStatus);
                orderItem.setStatus(newStatus);
                holder.status.setText("Status: " + orderItem.getStatus()); // Update the UI

                Toast.makeText(context, "Item status updated to Not Available", Toast.LENGTH_SHORT).show();
            }
        });

        // Load image using Glide
        String imageUrl = orderItem.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    // Method to update the status of an item in Firebase
    private void updateItemStatus(OrderItem orderItem, String newStatus) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(customerId)
                .child("Orders")
                .child(date)
                .child(orderId)
                .child("items")
                .child(orderItem.getItemName());

        // Update the "status" field in the database
        itemRef.child("status").setValue(newStatus);
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemValue, price, qty, status;
        ImageView imageView, na, add;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemValue = itemView.findViewById(R.id.itemValue);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
            imageView = itemView.findViewById(R.id.imageView);
            status = itemView.findViewById(R.id.status);
            add = itemView.findViewById(R.id.add);
            na = itemView.findViewById(R.id.notAvailble);
        }
    }
}