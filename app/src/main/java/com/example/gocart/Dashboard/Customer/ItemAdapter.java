package com.example.gocart.Dashboard.Customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.itemName.setText(item.getItemName());
        holder.price.setText("Price: Rs." + item.getTotal());
        holder.status.setText("Status: "+item.getStatus());
        if(item.getStatus().equals("Item Not Available")){
            holder.nt.setVisibility(View.VISIBLE);
            holder.search.setVisibility(View.VISIBLE);

            holder.search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("item_name",item.getItemName());
                    context.startActivity(intent);
                }
            });
        }


        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imageView,search;
        TextView itemName,nt;
        TextView itemValue,status;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
            itemName = itemView.findViewById(R.id.itemName);
            itemValue = itemView.findViewById(R.id.itemValue);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);
            nt = itemView.findViewById(R.id.nt);
            search = itemView.findViewById(R.id.search);
            search.setVisibility(View.GONE);
            nt.setVisibility(View.GONE);
        }
    }
}
