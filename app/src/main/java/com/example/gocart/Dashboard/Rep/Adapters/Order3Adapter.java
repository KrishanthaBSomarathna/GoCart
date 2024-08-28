package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Order4;
import com.example.gocart.R;

import java.util.List;

public class Order3Adapter extends RecyclerView.Adapter<Order3Adapter.ViewHolder> {

    private final List<String> orderDates;
    private final String userId;
    private final String customerId;
    private final Context context;

    public Order3Adapter(Context context, List<String> orderDates, String userId, String customerId) {
        this.context = context;
        this.orderDates = orderDates;
        this.userId = userId;
        this.customerId = customerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String orderDate = orderDates.get(position);
        holder.orderDate.setText(orderDate);
        holder.date = orderDate;

        holder.orderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, Order4.class);
            intent.putExtra("userId", userId);
            intent.putExtra("customerId", customerId);
            intent.putExtra("specificDate", holder.date);
            Toast.makeText(context, userId + customerId + orderDate, Toast.LENGTH_SHORT).show();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton orderDetails;
        TextView orderDate;
        String date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.OrderDate);
            orderDetails = itemView.findViewById(R.id.details); // Initialize the ImageButton here
        }
    }
}
