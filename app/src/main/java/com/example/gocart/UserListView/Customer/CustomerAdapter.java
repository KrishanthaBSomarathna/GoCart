package com.example.gocart.UserListView.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.R;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.userUnicId.setText(customer.getUnicId());
        holder.userName.setText(customer.getName());

        // Implement edit and delete button functionality here if needed
        holder.userEdit.setOnClickListener(v -> {
            // Handle edit button click
        });

        holder.userDelete.setOnClickListener(v -> {
            // Handle delete button click
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView userUnicId, userName;
        ImageButton userEdit, userDelete;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            userUnicId = itemView.findViewById(R.id.user_unicid);
            userName = itemView.findViewById(R.id.user_name);
            userEdit = itemView.findViewById(R.id.user_edit);
            userDelete = itemView.findViewById(R.id.user_del);
        }
    }
}
