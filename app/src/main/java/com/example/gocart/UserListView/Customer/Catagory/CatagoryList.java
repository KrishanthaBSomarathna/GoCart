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


import com.example.gocart.R;

public class CatagoryList extends AppCompatActivity {

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
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Atta, Rice & Dal");
                startActivity(intent);
            }
        });
        meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","");
                startActivity(intent);
            }
        });
        cool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Cold Drinks & Juices");
                startActivity(intent);
            }
        });
        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Dairy & Breakfast");
                startActivity(intent);
            }
        });
        instant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Instant & Frozen Food");
                startActivity(intent);
            }
        });
        masala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","");
                startActivity(intent);
            }
        });
        tea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Tea & Coffee");
                startActivity(intent);
            }
        });
        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CatagorySelector.class);
                intent.putExtra("category","Vegetables & Fruits");
                startActivity(intent);
            }
        });


    }
}