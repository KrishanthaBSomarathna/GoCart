package com.example.gocart.Dashboard.Rep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Adapters.OrderItemAdapter;
import com.example.gocart.Model.OrderItem;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderItemListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;
    private List<OrderItem> orderItemList;
    private String orderId;
    private String customerId;
    private String date;
    private String TARGET_USER_ID; // Target user ID
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_list_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId");
            customerId = intent.getStringExtra("customerId");
            date = intent.getStringExtra("specificDate");
            TARGET_USER_ID = intent.getStringExtra("userId");
        }
        adapter = new OrderItemAdapter(this, orderItemList, customerId, orderId, date,role);
        recyclerView.setAdapter(adapter);

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to users node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users/" + userId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user role
                     role = dataSnapshot.child("role").getValue(String.class);

                    // Toast user ID and role
                    Toast.makeText(OrderItemListView.this, "Role: " + role, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OrderItemListView.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderItemListView.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch order items from Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child(customerId)
                .child("Orders")
                .child(date)
                .child(orderId)
                .child("items");

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String userId = itemSnapshot.child("userId").getValue(String.class);
                    if (TARGET_USER_ID.equals(userId)) {
                        String itemName = itemSnapshot.getKey(); // or get specific field if needed
                        String division = itemSnapshot.child("division").getValue(String.class);
                        int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        double total = itemSnapshot.child("total").getValue(Double.class);
                        double unitPrice = itemSnapshot.child("unitPrice").getValue(Double.class);
                        String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                        String value = itemSnapshot.child("value").getValue(String.class);// Fetch imageUrl
                        String status = itemSnapshot.child("status").getValue(String.class);

                        OrderItem orderItem = new OrderItem(itemName, division, quantity, total, unitPrice, imageUrl, value, status);
                        orderItemList.add(orderItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
