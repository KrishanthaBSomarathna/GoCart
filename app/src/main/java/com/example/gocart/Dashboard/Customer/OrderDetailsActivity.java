package com.example.gocart.Dashboard.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;

import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

        private TextView tvOrderId, tvOrderDate, tvOrderAddress, tvOrderDivision, tvTotalPayment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_details);

            tvOrderId = findViewById(R.id.tvOrderId);
            tvOrderDate = findViewById(R.id.tvOrderDate);
            tvOrderAddress = findViewById(R.id.tvOrderAddress);
            tvOrderDivision = findViewById(R.id.tvOrderDivision);
            tvTotalPayment = findViewById(R.id.tvTotalPayment);

            // Retrieve order details from intent
            Intent intent = getIntent();
            String orderId = intent.getStringExtra("orderId");
            String date = intent.getStringExtra("date");
            String address = intent.getStringExtra("address");
            String division = intent.getStringExtra("division");
            double totalPayment = intent.getDoubleExtra("totalPayment", 0);

            // Set the details in the text views
            tvOrderId.setText(orderId);
            tvOrderDate.setText(date);
            tvOrderAddress.setText(address);
            tvOrderDivision.setText(division);
            tvTotalPayment.setText(String.format(Locale.getDefault(), "%.2f", totalPayment));
        }
    }
