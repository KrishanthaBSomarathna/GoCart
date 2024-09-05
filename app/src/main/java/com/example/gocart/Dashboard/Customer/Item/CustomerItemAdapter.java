package com.example.gocart.Dashboard.Customer.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerItemAdapter extends RecyclerView.Adapter<CustomerItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public CustomerItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("Rs. " + item.getPrice());
        holder.value.setText(item.getValue());
//        holder.quantity.setText("Stock: " + item.getQuantity());

        // Get current user's ID and current date
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "UnknownUser";
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer");
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("cartQty").setValue(1);
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("unitPrice").setValue(item.getPrice());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("total").setValue(item.getPrice());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("userId").setValue(item.getUserId());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("division").setValue(item.getDivision());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("imageUrl").setValue(item.getImageUrl());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("itemName").setValue(item.getItemName());
                databaseReference.child(userId).child("Cart").child(item.getItemId()).child("value").setValue(item.getValue());
            }
        });

        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView itemName, price, quantity, value;
        ImageButton add;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            value = itemView.findViewById(R.id.itemValue);
            add = itemView.findViewById(R.id.add);
        }
    }
}
