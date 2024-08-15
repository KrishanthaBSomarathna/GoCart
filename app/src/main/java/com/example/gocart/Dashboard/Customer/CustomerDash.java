package com.example.gocart.Dashboard.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Customer.Item.BestDeal;
import com.example.gocart.Dashboard.Customer.Item.CustomerItemAdapter;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.example.gocart.Stock.AddItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerDash extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerItemAdapter customerItemAdapter;
    private List<Item> itemList;
    private List<Item> filteredItemList;
    private List<String> customerDivisions;
    private String customerDistrict;

    private DatabaseReference databaseReference;
    private DatabaseReference customerReference;
    TextView district,bestDeal;
    LinearLayout veg, drinks, dairy, instant, tea, atta, masala, chicken, other;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bestDeal = findViewById(R.id.bestDeal);
        district = findViewById(R.id.textView4);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();
        customerItemAdapter = new CustomerItemAdapter(this, filteredItemList);
        recyclerView.setAdapter(customerItemAdapter);

        // Set database reference to the general items path
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerReference = FirebaseDatabase.getInstance().getReference("Customer");

        // Assuming you have already initialized these views and set up the intent
        veg = findViewById(R.id.category_vegetables);
        dairy = findViewById(R.id.category_dairy_breakfast);
        drinks = findViewById(R.id.category_cold_drinks);
        instant = findViewById(R.id.category_instant_food);
        tea = findViewById(R.id.category_tea_coffee);
        atta = findViewById(R.id.category_atta_rice_dal);
        masala = findViewById(R.id.category_masala_oil_dry_fruits);
        chicken = findViewById(R.id.category_meat_fish);
        other = findViewById(R.id.linearLayout);

        Intent intent = new Intent(CustomerDash.this, CustomerCategory.class);

        bestDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BestDeal.class));
            }
        });

        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Vegetables & Fruits");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Dairy & Breakfast");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Cold Drinks & Juices");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        instant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Instant & Frozen Food");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        tea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Tea & Coffee");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        atta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Atta, Rice & Dal");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        masala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Masala, Oil & Dry Fruits");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Chicken, Meat & Fish");
                startActivity(intent); // Assuming you want to start an activity
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("category", "Other");
                startActivity(intent); // Assuming you want to start an activity
            }
        });



        // Fetch customer data
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            customerReference.child(currentUserUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String divisionsString = snapshot.child("divisional_secretariat").getValue(String.class);
                        customerDistrict = snapshot.child("district").getValue(String.class);
                        district.setText(customerDistrict);

                        if (divisionsString != null) {
                            // Split the divisions string into a list
                            customerDivisions = Arrays.asList(divisionsString.split("\\s*,\\s*"));
                            fetchAndFilterItems();

                            // Show district in a Toast
//                            if (customerDistrict != null) {
//                                Toast.makeText(CustomerDash.this, "District: " + customerDistrict, Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CustomerDash.this, "Failed to load customer data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent inten = new Intent(CustomerDash.this, AddItem.class);
            startActivity(inten);
        });
    }

    private void fetchAndFilterItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null && item.isBestdeal() && customerDivisions.contains(item.getDivision())) {
                            itemList.add(item);
                        }
                    }
                }
                filterItemList(""); // Initially show all filtered items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerDash.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemList(String query) {
        filteredItemList.clear();
        for (Item item : itemList) {
            String itemName = item.getItemName();
            if (itemName != null && itemName.toLowerCase().contains(query.toLowerCase())) {
                filteredItemList.add(item);
            }
        }
        customerItemAdapter.notifyDataSetChanged();
    }

    private String getCurrentUserUID() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
