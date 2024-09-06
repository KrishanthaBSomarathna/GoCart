package com.example.gocart.Dashboard.Customer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.Item;
import com.example.gocart.Model.Order;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, itemList,role);
        recyclerView.setAdapter(itemAdapter);

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
                    Toast.makeText(CustomerOrderList.this,  "Role: " + role, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CustomerOrderList.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerOrderList.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load customer orders
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Customer/" + userId + "/Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : dateSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        // Null check for the order object and its fields
                        if (order != null) {
                            // Null check for items map
                            if (order.items != null) {
                                for (Item item : order.items.values()) {
                                    if (item != null && item.getStatus() != null && !item.getStatus().equals("Completed")) {
                                        // Add only if item status is not "Completed"
                                        itemList.add(item);
                                    }
                                }
                            }
                        }
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
