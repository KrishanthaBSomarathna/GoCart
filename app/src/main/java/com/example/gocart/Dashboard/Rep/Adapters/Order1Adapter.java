package com.example.gocart.Dashboard.Rep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Order2;
import com.example.gocart.Model.Shop;
import com.example.gocart.R;

import java.util.List;

public class Order1Adapter extends RecyclerView.Adapter<Order1Adapter.ShopViewHolder> {

    private Context context;
    private List<Shop> shopList;

    public Order1Adapter(Context context, List<Shop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item_layout, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);
        holder.shopIdTextView.setText(shop.getShopId());
        holder.showdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Order2.class);
                intent.putExtra("shopId", shop.getShopId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView shopIdTextView;
        ImageButton showdetails;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopIdTextView = itemView.findViewById(R.id.userid);
            showdetails = itemView.findViewById(R.id.showdetails);
        }
    }
}
