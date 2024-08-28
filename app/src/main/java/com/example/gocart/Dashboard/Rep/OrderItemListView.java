package com.example.gocart.Dashboard.Rep;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Rep.Adapters.OrderItemAdapter;
import com.example.gocart.Model.OrderItem;
import com.example.gocart.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_list_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemList = new ArrayList<>();
        adapter = new OrderItemAdapter(this, orderItemList);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Customer")
                .child("JD9ti7asdadaasdQClJYEcwmt6kwG3")
                .child("Orders")
                .child("2024-02-17")
                .child("2024-02-17-2067119000001")
                .child("items");

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String itemName = itemSnapshot.getKey(); // or get specific field if needed
                    String division = itemSnapshot.child("division").getValue(String.class);
                    int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                    double total = itemSnapshot.child("total").getValue(Double.class);
                    double unitPrice = itemSnapshot.child("unitPrice").getValue(Double.class);
                    String imageUrl = ""; // Add URL path if needed

                    OrderItem orderItem = new OrderItem(itemName, division, quantity, total, unitPrice, imageUrl);
                    orderItemList.add(orderItem);
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
