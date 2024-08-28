package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Adapters.Order1Adapter;
import com.example.gocart.Model.Shop;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Order1 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Order1Adapter order1Adapter;
    private List<Shop> shopList;
    private Set<String> uniqueShopIds;
    private List<String> assignedDivisions;
    private String loggedUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shopList = new ArrayList<>();
        uniqueShopIds = new HashSet<>();

        loggedUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchUserDivision(loggedUserUid);
    }

    private void fetchUserDivision(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String divisionString = snapshot.child("divisional_secretariat").getValue(String.class);
                    if (divisionString != null) {
                        assignedDivisions = new ArrayList<>();
                        for (String division : divisionString.split(",")) {
                            assignedDivisions.add(division.trim());
                        }
                        fetchAllCustomers();
                    } else {
                        // Handle the case where division information is not available
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void fetchAllCustomers() {
        DatabaseReference customersRef = FirebaseDatabase.getInstance().getReference("Customer");
        customersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                    fetchOrdersForCustomer(customerSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void fetchOrdersForCustomer(String customerId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId).child("Orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : dateSnapshot.getChildren()) {
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            String division = itemSnapshot.child("division").getValue(String.class);
                            String shopId = itemSnapshot.child("userId").getValue(String.class);
                            if (shopId != null && assignedDivisions.contains(division)) {
                                if (uniqueShopIds.add(shopId)) {
                                    fetchRetailerDetails(shopId);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void fetchRetailerDetails(String shopId) {
        DatabaseReference retailerRef = FirebaseDatabase.getInstance().getReference("retailer").child(shopId);
        retailerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String division = snapshot.child("division").getValue(String.class);
                    shopList.add(new Shop(shopId, name, phone, division));
                    updateRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }


    private void updateRecyclerView() {
        if (order1Adapter == null) {
            order1Adapter = new Order1Adapter(Order1.this, shopList);
            recyclerView.setAdapter(order1Adapter);
        } else {
            order1Adapter.notifyDataSetChanged();
        }
    }
}
