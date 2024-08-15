package com.example.gocart.Stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ShopItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("Rs. " + item.getPrice());
        holder.value.setText(item.getValue());
        holder.quantity.setText("Stock: " + item.getQuantity());
        holder.bestDeal.setChecked(item.isBestdeal());

        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        holder.bestDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBestDeal(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView itemName, price, quantity, value;
        CheckBox bestDeal;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            value = itemView.findViewById(R.id.itemValue);
            bestDeal = itemView.findViewById(R.id.bestDeal);
        }
    }

    private void updateBestDeal(Item item) {
        if (item.getItemId() != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("shopitem").child(item.getUserId()).child(item.getItemId());
            databaseReference.child("bestdeal").setValue(!item.isBestdeal()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    item.setBestdeal(!item.isBestdeal());
                    Toast.makeText(context, "Best deal status updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update best deal status", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Item ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}
