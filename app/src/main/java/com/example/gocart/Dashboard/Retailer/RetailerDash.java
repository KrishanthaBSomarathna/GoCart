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

import com.example.gocart.Catagory.CatagoryPage;
import com.example.gocart.R;
import com.example.gocart.Stock.ListOfStock;

import org.junit.experimental.categories.Category;

import java.util.Locale;

public class RetailerDash extends AppCompatActivity {

    CardView stock_card,retail_item_catagory;

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

        stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ListOfStock.class));
            }
        });

        retail_item_catagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryPage.class));
            }
        });
    }
}