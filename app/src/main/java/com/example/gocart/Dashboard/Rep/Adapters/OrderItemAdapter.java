package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gocart.Model.OrderItem;
import com.example.gocart.R;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private Context context;
    private List<OrderItem> orderItemList;

    public OrderItemAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
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
        holder.status.setText("Status: "+orderItem.getStatus());

        // Load image using Glide
        String imageUrl = orderItem.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image) // Handle loading error
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image); // Optional placeholder
        }
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemValue;
        TextView price;
        TextView qty;
        ImageView imageView;
        TextView status;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemValue = itemView.findViewById(R.id.itemValue);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
            imageView = itemView.findViewById(R.id.imageView);
            status = itemView.findViewById(R.id.status);
        }
    }
}
