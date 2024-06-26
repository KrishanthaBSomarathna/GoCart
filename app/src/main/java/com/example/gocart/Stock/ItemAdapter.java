package com.example.gocart.Stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.Item;
import com.example.gocart.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
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
        holder.name.setText(item.getName());
        holder.size.setText(item.getSize());
        holder.price.setText("Rs." + item.getPrice());
        holder.imageView.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateList(List<Item> newList) {
        itemList = new ArrayList<>();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, size, price;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            size = itemView.findViewById(R.id.item_size);
            price = itemView.findViewById(R.id.item_price);
            imageView = itemView.findViewById(R.id.item_image);
        }
    }
}
