package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Model.OrderItem;
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

public class OrdersByCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<String> uniqueCustomerIds;
    private String targetUserId = "wwww"; // Replace with the desired userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders_by_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        uniqueCustomerIds = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, uniqueCustomerIds);
        recyclerView.setAdapter(ordersAdapter);

        fetchOrders();
    }

    private void fetchOrders() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customer");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> customerIdSet = new HashSet<>(); // Use a Set to ensure uniqueness

                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                    boolean hasTargetUserId = false;

                    for (DataSnapshot orderDateSnapshot : customerSnapshot.child("Orders").getChildren()) {
                        for (DataSnapshot orderSnapshot : orderDateSnapshot.getChildren()) {
                            for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                                String userId = itemSnapshot.child("userId").getValue(String.class);
                                if (targetUserId.equals(userId)) {
                                    hasTargetUserId = true;
                                    break;
                                }
                            }
                            if (hasTargetUserId) break;
                        }
                        if (hasTargetUserId) break;
                    }

                    if (hasTargetUserId) {
                        String customerId = customerSnapshot.getKey();
                        customerIdSet.add(customerId);
                    }
                }

                uniqueCustomerIds.clear();
                uniqueCustomerIds.addAll(customerIdSet);
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
