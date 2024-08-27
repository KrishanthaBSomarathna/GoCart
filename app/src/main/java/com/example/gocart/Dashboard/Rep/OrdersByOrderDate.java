package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrdersByOrderDate extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDateAdapter adapter;
    private List<String> orderDates = new ArrayList<>();
    private DatabaseReference ordersRef;
    private String userId = "ZkGWwrQPIkeFKzODjGodEEoeDYd2"; // Your userId
    private String customerId = "JD9ti7asdadaasdQClJYEcwmt6kwG3"; // Your customerId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_by_order_date); // Your activity layout

        recyclerView = findViewById(R.id.recycsxslerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderDateAdapter(orderDates);
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrdersByOrderDate.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
