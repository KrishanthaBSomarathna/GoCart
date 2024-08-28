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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Order2Adapter extends RecyclerView.Adapter<Order2Adapter.ViewHolder> {

    private Context context;
    private List<String> customerIds;
    private String targetUserId;

    public Order2Adapter(Context context, List<String> customerIds, String targetUserId) {
        this.context = context;
        this.customerIds = customerIds;
        this.targetUserId = targetUserId;
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

        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String uID = dataSnapshot.child("uID").getValue(String.class);

                    holder.customerNameTextView.setText(name);
                    holder.customerPhoneTextView.setText(phone);
                    holder.customerEmailTextView.setText(email);
                    holder.customerUidTextView.setText(uID);
                } else {
                    Toast.makeText(context, "Customer details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error fetching customer details", Toast.LENGTH_SHORT).show();
            }
        });

        holder.showdetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, Order3.class);
            intent.putExtra("customerId", customerId);
            intent.putExtra("shopId", targetUserId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customerIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView customerPhoneTextView;
        TextView customerEmailTextView;
        TextView customerUidTextView;
        ImageButton showdetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customer_name);
            customerPhoneTextView = itemView.findViewById(R.id.customer_phone);
            customerEmailTextView = itemView.findViewById(R.id.customer_email);
            customerUidTextView = itemView.findViewById(R.id.customer_uid);
            showdetails = itemView.findViewById(R.id.showdetails);
        }
    }
}
