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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter shopItemAdapter;
    private List<Item> itemList;

    private DatabaseReference databaseReference;

    private Set<String> itemIdsToLoad = new HashSet<>(Arrays.asList(
            "-O4FXOlfeUE5mhLTY5Df",
            "-O4LHnVopPk6E-gM8k1Q"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // Set up GridLayoutManager with 1 column
        itemList = new ArrayList<>();
        shopItemAdapter = new CartItemAdapter(this, itemList);
        recyclerView.setAdapter(shopItemAdapter);

        // Set database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");

        // Fetch items based on the provided item IDs
        fetchItems();
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
                shopItemAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
