package com.example.gocart.Dashboard.Rep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.OrderItem;
import com.example.gocart.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context context;
    private List<String> customerIds;

    public OrdersAdapter(Context context, List<String> customerIds) {
        this.context = context;
        this.customerIds = customerIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String customerId = customerIds.get(position);
        holder.customerIdTextView.setText(customerId);
        // Handle other views if necessary
    }

    @Override
    public int getItemCount() {
        return customerIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerIdTextView;
        // Other views if necessary

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerIdTextView = itemView.findViewById(R.id.userid);
            // Initialize other views if necessary
        }
    }
}
