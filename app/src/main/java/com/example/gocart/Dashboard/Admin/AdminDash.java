package com.example.gocart.Dashboard.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.example.gocart.UserListView.Admin.AdminList;
import com.example.gocart.UserListView.Customer.CustomerList;
import com.example.gocart.UserListView.DeliveryRider.DeliveryRiderList;
import com.example.gocart.UserListView.Rep.DeliveryRepList;
import com.example.gocart.UserListView.Retailer.RetailerList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDash extends AppCompatActivity {

    private TextView admin_name;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dash);
        admin_name = findViewById(R.id.adminname);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null && email.contains("@gmail.com")) {
                String name = email.split("@")[0];
                admin_name.setText(name);
            } else {
                admin_name.setText("Admin");
            }
        } else {
            admin_name.setText("Admin");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.administrators_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, AdminList.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.delivery_rep_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, DeliveryRepList.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.delivery_riders_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, DeliveryRiderList.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.retail_shops_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, RetailerList.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.user_hierarchy_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, AdminDash.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.customer_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDash.this, CustomerList.class);
                startActivity(intent);
            }
        });
    }
}
