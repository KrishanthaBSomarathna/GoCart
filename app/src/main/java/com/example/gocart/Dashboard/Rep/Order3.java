package com.example.gocart.Dashboard.Rep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Adapters.Order3Adapter;
import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Order3 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Order3Adapter adapter;
    private List<String> orderDates = new ArrayList<>();
    private DatabaseReference ordersRef;
    private String userId;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_by_order_date);

        Intent intent = getIntent();
        userId = intent.getStringExtra("shopId");
        customerId = intent.getStringExtra("customerId");

        if (userId != null && customerId != null) {
            Toast.makeText(this, userId + customerId, Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Order3Adapter(this, orderDates, userId, customerId);
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId).child("Orders");
        fetchOrderDates();
    }

    private void fetchOrderDates() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderDates.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : dateSnapshot.getChildren()) {
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            if (userId.equals(itemSnapshot.child("userId").getValue(String.class))) {
                                if (!orderDates.contains(dateSnapshot.getKey())) {
                                    orderDates.add(dateSnapshot.getKey());
                                }
                                break;
                            }
                        }
                    }
                }
                // Sort orderDates in descending order
                Collections.sort(orderDates, new Comparator<String>() {
                    @Override
                    public int compare(String date1, String date2) {
                        return date2.compareTo(date1); // Descending order
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Order3.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
