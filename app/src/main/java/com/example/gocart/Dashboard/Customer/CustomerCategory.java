package com.example.gocart.Dashboard.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Customer.Item.CustomerItemAdapter;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerCategory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerItemAdapter shopItemAdapter;
    private List<Item> itemList;
    private List<Item> filteredItemList;

    private DatabaseReference databaseReference;
    private DatabaseReference customerRef;

    private List<String> customerDivisions = new ArrayList<>();
    private String categoryFilter = "Vegetables & Fruits";
    private EditText searchEditText; // Reference to the search EditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_category);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Set up GridLayoutManager with 2 columns
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();
        shopItemAdapter = new CustomerItemAdapter(this, filteredItemList);
        recyclerView.setAdapter(shopItemAdapter);

        // Set database references
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerRef = FirebaseDatabase.getInstance().getReference("Customer");

        // Get category data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("category")) {
            categoryFilter = intent.getStringExtra("category");
        }

        // Initialize the search EditText
        searchEditText = findViewById(R.id.entersearch);

        // Set up the search functionality using TextWatcher
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list based on the search input
                filterItemList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing after text changes
            }
        });

        // Fetch customer divisions and then items
        fetchCustomerDivisions();
    }

    private void fetchCustomerDivisions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            customerRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String divisionalSecretariat = snapshot.child("divisional_secretariat").getValue(String.class);
                        if (divisionalSecretariat != null) {
                            customerDivisions = Arrays.asList(divisionalSecretariat.split(",\\s*"));
                        }
                        fetchItems(); // Fetch items after getting divisions
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CustomerCategory.this, "Failed to load customer data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null && customerDivisions.contains(item.getDivision())) {
                            itemList.add(item);
                        }
                    }
                }
                filterItemList(""); // Initially show all filtered items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerCategory.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemList(String query) {
        filteredItemList.clear();
        for (Item item : itemList) {
            String itemName = item.getItemName();
            String itemCategory = item.getCategory();
            if (itemCategory != null && itemCategory.equals(categoryFilter) &&
                    (itemName != null && itemName.toLowerCase().contains(query.toLowerCase()))) {
                filteredItemList.add(item);
            }
        }
        shopItemAdapter.notifyDataSetChanged();
    }
}
