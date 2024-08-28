package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Order3;
import com.example.gocart.R;

import java.util.List;
public class Order2Adapter extends RecyclerView.Adapter<Order2Adapter.ViewHolder> {

    private Context context;
    private List<String> customerIds;
    private String targetUserId; // Add this field

    public Order2Adapter(Context context, List<String> customerIds, String targetUserId) {
        this.context = context;
        this.customerIds = customerIds;
        this.targetUserId = targetUserId; // Initialize this field
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
        Toast.makeText(context, customerId+"sd", Toast.LENGTH_SHORT).show();
        holder.customerIdTextView.setText(customerId);
        holder.showdetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, Order3.class);
            intent.putExtra("customerId", customerId);
            Log.d("OrdersAdapter", "Using targetUserId: " + targetUserId); // Add this line
            intent.putExtra("shopId", targetUserId); // Use the passed targetUserId

            Toast.makeText(context, customerId+"sd", Toast.LENGTH_SHORT).show();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customerIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerIdTextView;
        ImageButton showdetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerIdTextView = itemView.findViewById(R.id.userid);
            showdetails = itemView.findViewById(R.id.showdetails);
        }
    }
}
