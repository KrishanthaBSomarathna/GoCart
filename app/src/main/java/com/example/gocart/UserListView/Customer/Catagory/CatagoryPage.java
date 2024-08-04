package com.example.gocart.UserListView.Customer.Catagory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryAtta;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryChicken;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryCool;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryDairy;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryInstant;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryMasala;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CatagoryTea;
import com.example.gocart.UserListView.Customer.Catagory.CategoryList.CategoryVeg;
import com.example.gocart.R;

public class CatagoryPage extends AppCompatActivity {

    CardView atta,meat,cool,dairy,instant,masala,tea,veg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catagory_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        atta = findViewById(R.id.atta);
        meat = findViewById(R.id.meat);
        cool = findViewById(R.id.cool);
        dairy = findViewById(R.id.dairy);
        instant = findViewById(R.id.instant);
        masala = findViewById(R.id.masala);
        tea = findViewById(R.id.tea);
        veg = findViewById(R.id.veg);


        atta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryAtta.class));
            }
        });
        meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryChicken.class));
            }
        });
        cool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryCool.class));
            }
        });
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryDairy.class));
            }
        });
        instant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryInstant.class));
            }
        });
        masala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryMasala.class));
            }
        });
        tea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CatagoryTea.class));
            }
        });
        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CategoryVeg.class));
            }
        });


    }
}