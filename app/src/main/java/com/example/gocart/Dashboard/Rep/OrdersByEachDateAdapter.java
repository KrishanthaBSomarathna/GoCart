package com.example.gocart.Dashboard.Rep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gocart.Model.OrderData;
import com.example.gocart.R;
import java.util.List;

public class OrdersByEachDateAdapter extends RecyclerView.Adapter<OrdersByEachDateAdapter.ViewHolder> {

    private final List<OrderData> orders;

    public OrdersByEachDateAdapter(List<OrderData> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_date_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderData order = orders.get(position);
        holder.orderId.setText(order.getOrderId());
        holder.address.setText(order.getAddress());
        holder.totalPayment.setText(order.getTotalPayment());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView address;
        TextView totalPayment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.OrderId);
            address = itemView.findViewById(R.id.Address);
            totalPayment = itemView.findViewById(R.id.TotalPayment);
        }
    }
}
