package com.example.gocart.Dashboard.Customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private List<Item> itemList;
    private TextView totalTextView;

    private DatabaseReference shopItemReference;
    private DatabaseReference cartReference;
    private DatabaseReference customerReference;

    private Set<String> itemIdsToLoad = new HashSet<>();
    private String currentUserId;
    ImageButton placeOrderbtn;

    private static final String TAG = "CartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        totalTextView = findViewById(R.id.total);
        placeOrderbtn = findViewById(R.id.placeOrderbtn);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        itemList = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(this, itemList);
        recyclerView.setAdapter(cartItemAdapter);

        // Initialize Firebase references
        shopItemReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerReference = FirebaseDatabase.getInstance().getReference("Customer");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch cart items in real-time
        fetchCartItemsRealtime();

        placeOrderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerReference.child(currentUserId).child("Orders").child(curentdate).child(ordernumberinsamedate gose here).child("division").setValue("Wariyapola");
                customerReference.child(currentUserId).child("Orders").child(curentdate).child(ordernumberinsamedate gose here).child("address").setValue("Kurunegala,Padeniya");
                customerReference.child(currentUserId).child("Orders").child(curentdate).child(ordernumberinsamedate gose here).child("items").setValue({"itemid1":2,"itemid2"4});
                customerReference.child(currentUserId).child("Orders").child(curentdate).child(ordernumberinsamedate gose here).child("totalpayment").setValue(4000);
            }
        });
    }

    private void fetchCartItemsRealtime() {
        cartReference = customerReference.child(currentUserId).child("Cart");

        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemIdsToLoad.clear();
                double totalSum = 0.0;

                if (snapshot.exists()) {
                    for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                        String itemId = cartItemSnapshot.getKey();

                        if (itemId != null) {
                            itemIdsToLoad.add(itemId);

                            // Retrieve 'total' value and add to totalSum
                            Object totalObj = cartItemSnapshot.child("total").getValue();
                            double itemTotal = convertToDouble(totalObj);
                            totalSum += itemTotal;
                        }
                    }
                    // Fetch item details after retrieving all item IDs
                    fetchItems(totalSum);
                } else {
                    // No items in cart
                    itemList.clear();
                    cartItemAdapter.notifyDataSetChanged();
                    totalTextView.setText("Total: Rs. 0.00");
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load cart items: " + error.getMessage());
                Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchItems(double totalSum) {
        shopItemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot itemSnapshot : shopSnapshot.getChildren()) {
                            Item item = itemSnapshot.getValue(Item.class);
                            if (item != null && itemIdsToLoad.contains(item.getItemId())) {
                                itemList.add(item);
                            }
                        }
                    }

                    cartItemAdapter.notifyDataSetChanged();

                    // Display total price
                    totalTextView.setText(String.format("Total: Rs. %.2f", totalSum));
                } else {
                    totalTextView.setText("Total: Rs. 0.00");
                    Toast.makeText(CartActivity.this, "No items found in shop", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load shop items: " + error.getMessage());
                Toast.makeText(CartActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double convertToDouble(Object value) {
        if (value == null) {
            return 0.0;
        }

        try {
            if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            } else if (value instanceof Integer) {
                return ((Integer) value).doubleValue();
            } else {
                Log.w(TAG, "Unknown data type for total value: " + value.getClass().getSimpleName());
                return 0.0;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to convert value to double: " + e.getMessage());
            return 0.0;
        }
    }
}
