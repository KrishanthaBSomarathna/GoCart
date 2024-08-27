package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.OrderDetail;
import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;
    private List<OrderDetail> orderDetailsList;

    private String userId = "wwww";  // User ID to filter item IDs
    private String customerId = "JD9ti7rxfeSQYQClJYEcwmt6kwG3";  // Customer ID
    private String specificDate = "2024-03-18";  // Specific order date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderDetailsList = new ArrayList<>();
        adapter = new OrderDetailAdapter(orderDetailsList);
        recyclerView.setAdapter(adapter);

        fetchOrderDetails(customerId, specificDate);
    }

    private void fetchOrderDetails(String customerId, String specificDate) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(customerId)
                .child("Orders")
                .child(specificDate);

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetailsList.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    String address = orderSnapshot.child("address").getValue(String.class);

                    List<String> filteredItemIds = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                        String itemUserId = itemSnapshot.child("userId").getValue(String.class);
                        if (userId.equals(itemUserId)) {
                            filteredItemIds.add(itemSnapshot.getKey());
                        }
                    }

                    if (!filteredItemIds.isEmpty()) {  // Add only if there are matching item IDs
                        OrderDetail orderDetail = new OrderDetail(orderId, address, filteredItemIds);
                        orderDetailsList.add(orderDetail);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderListActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
