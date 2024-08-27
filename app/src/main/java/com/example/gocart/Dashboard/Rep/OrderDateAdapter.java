package com.example.gocart.Dashboard.Rep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.R;

import java.util.List;

public class OrderDateAdapter extends RecyclerView.Adapter<OrderDateAdapter.ViewHolder> {

    private final List<String> orderDates;

    public OrderDateAdapter(List<String> orderDates) {
        this.orderDates = orderDates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.orderDate.setText(orderDates.get(position));
    }

    @Override
    public int getItemCount() {
        return orderDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.OrderDate);
        }
    }
}
