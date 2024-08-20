package com.example.gocart.Dashboard.Customer.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public CartItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("Rs. " + item.getPrice());
        holder.value.setText(item.getValue());

        // Load image using Glide
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        // Fetch the current quantity from the cart
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Customer")
                    .child(userId).child("Cart").child(item.getItemId());

            cartRef.child("cartQty").get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    int currentQty = snapshot.getValue(Integer.class);
                    holder.qty.setText(String.valueOf(currentQty));
                    item.setCartQty(currentQty); // Update the item object with the fetched quantity
                } else {
                    holder.qty.setText("0");
                }
            }).addOnFailureListener(e -> Toast.makeText(context, "Failed to retrieve quantity", Toast.LENGTH_SHORT).show());
        } else {
            holder.qty.setText("0");
        }

        holder.delete.setOnClickListener(v -> deleteItem(item, position));
        holder.minusQty.setOnClickListener(v -> updateQuantity(item, -1, position));
        holder.plusQty.setOnClickListener(v -> updateQuantity(item, 1, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void updateQuantity(Item item, int delta, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(userId).child("Cart").child(item.getItemId());

        cartRef.child("cartQty").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                int currentQty = snapshot.getValue(Integer.class);
                int newQty = currentQty + delta;

                if (newQty <= 0) {
                    deleteItem(item, position); // Optionally remove item if quantity becomes zero
                } else {
                    cartRef.child("cartQty").setValue(newQty)
                            .addOnSuccessListener(aVoid -> {
                                // Update item object and notify RecyclerView
                                item.setCartQty(newQty);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show());
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Failed to retrieve quantity", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem(Item item, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(userId).child("Cart").child(item.getItemId());

        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove item from list and notify adapter
                    if (position >= 0 && position < itemList.size()) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Item not found in list", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show());
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete, minusQty, plusQty;
        TextView itemName, price, qty, value;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
            value = itemView.findViewById(R.id.itemValue);
            delete = itemView.findViewById(R.id.delete);
            minusQty = itemView.findViewById(R.id.minusQty);
            plusQty = itemView.findViewById(R.id.plusQty);
        }
    }
}
