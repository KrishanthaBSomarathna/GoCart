package com.example.gocart.Predictor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.gocart.API.ApiService;
import com.example.gocart.Dashboard.Customer.CartActivity;
import com.example.gocart.Dashboard.Customer.CustomerDash;
import com.example.gocart.Dashboard.Customer.Item.CartItemAdapter;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PredictorActivity extends AppCompatActivity {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private PredictorItemAdapter predictorItemAdapter;
    private List<Item> itemList;

    private DatabaseReference shopItemReference;
    private DatabaseReference cartReference;
    private DatabaseReference customerReference;

    private Set<String> itemIdsToLoad = new HashSet<>();
    private String currentUserId;
    private ImageButton btnPredict, addtocart;
    private double totalSum;
    private Dialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "PredictorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor);

        apiService = new ApiService(this);
        btnPredict = findViewById(R.id.btnPredict);
        addtocart = findViewById(R.id.addtocart);
        addtocart.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize the progress dialog
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);  // Prevent closing the dialog by clicking outside

        btnPredict.setOnClickListener(v -> {
            assert firebaseUser != null;
            String customerId = firebaseUser.getUid(); // Example customer ID

            // Show the progress dialog when the prediction starts
            progressDialog.show();
            predictOrders(customerId);
            addtocart.setVisibility(View.VISIBLE);
        });

        addtocart.setOnClickListener(v -> {
            // Show progress dialog while moving data
            progressDialog.show();

            // Create a reference to the predicted items in the Firebase database
            DatabaseReference predictedItemsReference = customerReference.child(currentUserId).child("PredictedItem");
            DatabaseReference cartItemsReference = customerReference.child(currentUserId).child("Cart");

            // Retrieve predicted items
            predictedItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Create a map to hold the data to be moved
                        Map<String, Object> cartItems = new HashMap<>();
                        Map<String, Object> updates = new HashMap<>();

                        // Iterate through each item in the predicted items
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            String itemId = itemSnapshot.getKey();
                            if (itemId != null) {
                                // Add item data to cartItems map
                                cartItems.put(itemId, itemSnapshot.getValue());

                                // Prepare to remove item from predicted items
                                updates.put("PredictedItem/" + itemId, null);
                            }
                        }

                        // Add cart items to the Cart path in Firebase
                        cartItemsReference.updateChildren(cartItems).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Remove items from PredictedItem
                                customerReference.child(currentUserId).updateChildren(updates).addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(PredictorActivity.this, "Items moved to cart successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), CartActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(PredictorActivity.this, "Failed to update predicted items", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(PredictorActivity.this, "Failed to remove predicted items", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                });
                            } else {
                                Toast.makeText(PredictorActivity.this, "Failed to move items to cart", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(PredictorActivity.this, "Failed to move items to cart", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        });
                    } else {
                        Toast.makeText(PredictorActivity.this, "No predicted items to move", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to retrieve predicted items: " + error.getMessage());
                    Toast.makeText(PredictorActivity.this, "Failed to retrieve predicted items", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        });

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewPredictions);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        itemList = new ArrayList<>();
        predictorItemAdapter = new PredictorItemAdapter(this, itemList);
        recyclerView.setAdapter(predictorItemAdapter);

        // Initialize Firebase references
        shopItemReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerReference = FirebaseDatabase.getInstance().getReference("Customer");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch cart items in real-time
        fetchCartItemsRealtime();
    }

    private void fetchCartItemsRealtime() {
        cartReference = customerReference.child(currentUserId).child("PredictedItem");

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
                    predictorItemAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();  // Dismiss the progress dialog if no items found
                    Toast.makeText(PredictorActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load cart items: " + error.getMessage());
                Toast.makeText(PredictorActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();  // Dismiss the progress dialog in case of error
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

                    predictorItemAdapter.notifyDataSetChanged();

                    // Dismiss the progress dialog after loading items
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PredictorActivity.this, "No items found in shop", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load shop items: " + error.getMessage());
                Toast.makeText(PredictorActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();  // Dismiss the progress dialog in case of error
            }
        });
    }

    private void predictOrders(String customerId) {
        apiService.predictOrders(customerId, new ApiService.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.toString(4);
                    Log.d(TAG, "Prediction response: " + result);
                    // Once prediction is done, fetch cart items to display in RecyclerView
                    fetchCartItemsRealtime();
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing prediction response: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(PredictorActivity.this, "Failed to parse prediction response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // Log the full error message
                Log.e(TAG, "Error making prediction request: " + error.toString());

                // Additional logging of status code and network response (if available)
                if (error.networkResponse != null) {
                    Log.e(TAG, "HTTP Status Code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Network Response: " + new String(error.networkResponse.data));
                }

                progressDialog.dismiss();
                Toast.makeText(PredictorActivity.this, "Failed to make prediction request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}
