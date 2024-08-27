package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.ShopAdapter;
import com.example.gocart.Model.Shop;
import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<Shop> shopList;
    private Set<String> uniqueShopIds;
    private List<String> assignedDivisions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Example division string; this should be dynamically set
        String divisionString = "Nikaweratiya,Wariyapola";
        assignedDivisions = new ArrayList<>();
        for (String division : divisionString.split(",")) {
            assignedDivisions.add(division.trim());
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shopList = new ArrayList<>();
        uniqueShopIds = new HashSet<>();

        fetchAllCustomers();
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
                                    shopList.add(new Shop(shopId));
                                }
                            }
                        }
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void updateRecyclerView() {
        if (shopAdapter == null) {
            shopAdapter = new ShopAdapter(OrderList.this, shopList);
            recyclerView.setAdapter(shopAdapter);
        } else {
            shopAdapter.notifyDataSetChanged();
        }
    }
}
