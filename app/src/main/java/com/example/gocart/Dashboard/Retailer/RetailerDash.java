package com.example.gocart.Dashboard.Retailer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.Dashboard.Rep.Order2;
import com.example.gocart.Stock.BestDealItem;
import com.example.gocart.UserListView.Customer.Catagory.CatagoryList;
import com.example.gocart.R;
import com.example.gocart.Stock.ListOfStock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RetailerDash extends AppCompatActivity {

    CardView stock_card, retail_item_catagory, bestDealcard, Orders_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retailer_dash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        retail_item_catagory = findViewById(R.id.retail_item_catagory);
        stock_card = findViewById(R.id.stock_card);
        bestDealcard = findViewById(R.id.bestDealcard);
        Orders_card = findViewById(R.id.orderscard);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String targetUserId = user.getUid();

        stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetailerDash.this, ListOfStock.class));
            }
        });

        bestDealcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetailerDash.this, BestDealItem.class));
            }
        });

        retail_item_catagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetailerDash.this, CatagoryList.class));
            }
        });

        Orders_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RetailerDash.this, Order2.class);
                intent.putExtra("shopId", targetUserId);
                startActivity(intent);
            }
        });
    }
}
