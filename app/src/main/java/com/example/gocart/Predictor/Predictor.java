package com.example.gocart.Predictor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.Model.CartItem;
import com.example.gocart.Model.Order;
import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Predictor extends AppCompatActivity {

    private Button predictButton;
    private DatabaseReference ordersRef;
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor);

        predictButton = findViewById(R.id.predict_button);

        // Initialize Firebase references
        ordersRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child("JD9ti7rxfeSQYQClJYEcwmt6kwG3").child("Orders");

        cartRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child("JD9ti7rxfeSQYQClJYEcwmt6kwG3").child("Cart");

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPastOrdersAndPredict();
                Toast.makeText(getApplicationContext(), "Prediction in progress...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPastOrdersAndPredict() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> pastOrders = new ArrayList<>();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : dateSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        pastOrders.add(order);
                    }
                }
                predictFutureOrders(pastOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Predictor", "Error fetching past orders: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to fetch past orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void predictFutureOrders(List<Order> pastOrders) {
        List<CartItem> predictedItems = runTimeSeriesModel(pastOrders);
        storePredictedOrders(predictedItems);
    }

    private List<CartItem> runTimeSeriesModel(List<Order> pastOrders) {
        // Implement your TensorFlow Lite time series forecasting model here
        List<CartItem> predictedItems = new ArrayList<>();
        predictedItems.add(new CartItem("item1", "3"));
        predictedItems.add(new CartItem("item2", "2"));
        return predictedItems;
    }

    private void storePredictedOrders(List<CartItem> predictedItems) {
        for (CartItem item : predictedItems) {
            String key = cartRef.push().getKey();
            if (key != null) {
                cartRef.child(key).setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Item added to cart: " + item.getItemId(), Toast.LENGTH_SHORT).show();
                        Log.d("Predictor", "Successfully added item to cart: " + item.getItemId());
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to add item to cart: " + item.getItemId(), Toast.LENGTH_SHORT).show();
                        Log.e("Predictor", "Failed to add item to cart: " + item.getItemId(), task.getException());
                    }
                });
            } else {
                Log.e("Predictor", "Generated key is null for item: " + item.getItemId());
                Toast.makeText(getApplicationContext(), "Error generating key for item: " + item.getItemId(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
