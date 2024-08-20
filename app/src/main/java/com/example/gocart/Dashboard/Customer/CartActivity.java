package com.example.gocart.Dashboard.Customer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Customer.Item.CartItemAdapter;
import com.example.gocart.Model.Item;
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

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter shopItemAdapter;
    private List<Item> itemList;

    private DatabaseReference databaseReference;
    private DatabaseReference cartReference;
    private DatabaseReference customerReference;

    private Set<String> itemIdsToLoad = new HashSet<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Set up GridLayoutManager with 1 column
        itemList = new ArrayList<>();
        shopItemAdapter = new CartItemAdapter(this, itemList);
        recyclerView.setAdapter(shopItemAdapter);

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerReference = FirebaseDatabase.getInstance().getReference("Customer");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user ID

        // Fetch cart items for the logged-in customer
        fetchCartItems();
    }

    private void fetchCartItems() {
        cartReference = customerReference.child(currentUserId).child("Cart");

        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemIdsToLoad.clear();
                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    String itemId = cartItemSnapshot.getKey();
                    if (itemId != null) {
                        itemIdsToLoad.add(itemId);
                    }
                }
                // After collecting item IDs, fetch the details for these items
                fetchItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null && itemIdsToLoad.contains(item.getItemId())) {
                            itemList.add(item);
                        }
                    }
                }
                shopItemAdapter.notifyDataSetChanged(); // Notify adapter to refresh UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
