package com.example.gocart.Dashboard.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.example.gocart.Authentication.EmployeeCreate.DeliveryRepCreate;
import com.example.gocart.Authentication.EmployeeCreate.DeliveryRiderCreate;
import com.example.gocart.Authentication.RetailerAuth.RetailShopCreate;
import com.example.gocart.UserListView.Admin.AdminList;

public class AdminView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.administrators_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, AdminList.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.delivery_rep_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, DeliveryRepCreate.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.delivery_riders_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, DeliveryRiderCreate.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.retail_shops_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, RetailShopCreate.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.organization_structure_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminView.this, AdminView.class);
                startActivity(intent);
            }
        });
    }
}
