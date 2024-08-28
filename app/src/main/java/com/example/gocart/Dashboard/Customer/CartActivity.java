package com.example.gocart.Dashboard.Customer;

import android.app.Dialog;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private ImageButton placeOrderbtn;
    private double totalSum;

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
                placeOrder();
                Toast.makeText(CartActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartItemsRealtime() {
        cartReference = customerReference.child(currentUserId).child("Cart");

        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemIdsToLoad.clear();
                totalSum = 0.0;

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

    private void placeOrder() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String orderNumber = generateOrderNumber(currentDate);

        // Create order data map
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("division", "Wariyapola");
        orderData.put("address", "Kurunegala,Padeniya");
        orderData.put("totalpayment", totalSum);

        // Initialize a map to store ordered items and their quantities
        Map<String, Map<String, Object>> orderedItems = new HashMap<>();

        // Keep track of the number of items processed
        final int[] itemsProcessed = {0};

        // Iterate over each item in the cart
        for (String itemId : itemIdsToLoad) {
            cartReference.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get the quantity and other details of the item
                        int quantity = snapshot.child("cartQty").getValue(Integer.class);
                        double unitPrice = convertToDouble(snapshot.child("unitPrice").getValue());
                        double total = convertToDouble(snapshot.child("total").getValue());
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        // Create a map to store item details
                        Map<String, Object> itemDetails = new HashMap<>();
                        itemDetails.put("quantity", quantity);
                        itemDetails.put("unitPrice", unitPrice);
                        itemDetails.put("total", total);
                        itemDetails.put("imageUrl", imageUrl);
                        itemDetails.put("division", snapshot.child("division").getValue());
                        itemDetails.put("userId",snapshot.child("userId").getValue());

                        // Add the item details to the orderedItems map
                        orderedItems.put(itemId, itemDetails);
                    }

                    // Increment the processed items count
                    itemsProcessed[0]++;

                    // Check if all items have been processed
                    if (itemsProcessed[0] == itemIdsToLoad.size()) {
                        // Add ordered items to the order data
                        orderData.put("items", orderedItems);

                        // Store the order data in Firebase
                        customerReference.child(currentUserId).child("Orders").child(currentDate).child(orderNumber).setValue(orderData)
                                .addOnSuccessListener(aVoid -> {
                                    // Show the success dialog
                                    showOrderSuccessDialog();

                                    // Clear the cart after placing the order
                                    customerReference.child(currentUserId).child("Cart").removeValue();
                                })
                                .addOnFailureListener(e -> Toast.makeText(CartActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to retrieve item quantity: " + error.getMessage());
                    Toast.makeText(CartActivity.this, "Failed to retrieve item quantity", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showOrderSuccessDialog() {
        // Create a custom dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_order_success);

        // Find views in the dialog layout
        TextView myOrdersButton = dialog.findViewById(R.id.myOrdersButton);
        TextView okButton = dialog.findViewById(R.id.okButton);

        // Handle My Orders button click
        myOrdersButton.setOnClickListener(v -> {
            // Open MyOrdersActivity (Assume you have this activity)
            startActivity(new Intent(CartActivity.this, CustomerDash.class));
            finish();
            dialog.dismiss();
        });

        // Handle OK button click

        okButton.setOnClickListener(v -> {
            // Open MyOrdersActivity (Assume you have this activity)
            startActivity(new Intent(CartActivity.this, CustomerDash.class));
            finish();
            dialog.dismiss();
        });
        // Display the dialog
        dialog.show();
    }

    private String generateOrderNumber(String currentDate) {
        return currentDate + "-" + System.currentTimeMillis();  // Generates a unique order number based on the current time
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
