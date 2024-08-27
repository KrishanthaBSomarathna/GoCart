package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gocart.Model.OrderData;
import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrdersByEachDate extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersByEachDateAdapter adapter;
    private List<OrderData> orders = new ArrayList<>();
    private DatabaseReference ordersRef;
    private String userId = "ZkGWwrQPIkeFKzODjGodEEoeDYd2";
    private String customerId = "JD9ti7asdadaasdQClJYEcwmt6kwG3";
    private String specificDate = "2024-02-17";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_by_each_date);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrdersByEachDateAdapter(orders);
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(customerId)
                .child("Orders")
                .child(specificDate);

        fetchOrders();
    }

    private void fetchOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                    boolean containsUser = false;
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        if (userId.equals(itemSnapshot.child("userId").getValue(String.class))) {
                            containsUser = true;
                            break;
                        }
                    }
                    if (containsUser) {
                        String orderId = orderSnapshot.getKey();
                        String address = orderSnapshot.child("address").getValue(String.class);
                        String totalPayment = orderSnapshot.child("totalpayment").getValue(String.class);
                        orders.add(new OrderData(orderId, address, totalPayment));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrdersByEachDate.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
