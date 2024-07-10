package com.example.gocart.Dashboard.Rep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.example.gocart.Stock.ListOfStock;
import com.example.gocart.UserListView.DeliveryRider.DeliveryRiderListOfRep;
import com.example.gocart.UserListView.Retailer.RetailerListOfRep;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RepDash extends AppCompatActivity {

    CardView stock_card,rider_card,retailer_card;
    TextView admin_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rep_dash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        stock_card = findViewById(R.id.stock_card);
        rider_card = findViewById(R.id.delivery_riders_card);
        retailer_card = findViewById(R.id.retail_shops_card);
        admin_name = findViewById(R.id.adminname);

        retailer_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                startActivity(intent = new Intent(RepDash.this, RetailerListOfRep.class));
            }
        });

        stock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                startActivity(intent = new Intent(RepDash.this, ListOfStock.class));
            }
        });
        rider_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                startActivity(intent = new Intent(RepDash.this, DeliveryRiderListOfRep.class));
            }
        });
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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
    }
}