package com.example.gocart.Dashboard.Customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.example.gocart.Predictor.PredictorActivity;
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
    private TextView district, bestDeal,all;
    private LinearLayout veg, drinks, dairy, instant, tea, atta, masala, chicken, other;
    private ImageView cart, setting, ordered;
    private FloatingActionButton fab;
    private float dX, dY;
    private int lastAction;

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
all = findViewById(R.id.all);
        cart = findViewById(R.id.cartPage);
        bestDeal = findViewById(R.id.bestDeal);
        district = findViewById(R.id.textView4);
        setting = findViewById(R.id.setting);
        ordered = findViewById(R.id.ordered_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();
        customerItemAdapter = new CustomerItemAdapter(this, filteredItemList);
        recyclerView.setAdapter(customerItemAdapter);

        // Set database reference to the general items path
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");
        customerReference = FirebaseDatabase.getInstance().getReference("Customer");

        // Initialize views
        veg = findViewById(R.id.category_vegetables);
        dairy = findViewById(R.id.category_dairy_breakfast);
        drinks = findViewById(R.id.category_cold_drinks);
        instant = findViewById(R.id.category_instant_food);
        tea = findViewById(R.id.category_tea_coffee);
        atta = findViewById(R.id.category_atta_rice_dal);
        masala = findViewById(R.id.category_masala_oil_dry_fruits);
        chicken = findViewById(R.id.category_meat_fish);
        other = findViewById(R.id.linearLayout);

        // Set up SharedPreferences to check first-time login
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstLogin = prefs.getBoolean("isFirstLogin", true);



        setting.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Setting.class)));

        cart.setOnClickListener(v -> startActivity(new Intent(CustomerDash.this, CartActivity.class)));

        Intent intent = new Intent(CustomerDash.this, CustomerCategory.class);

        bestDeal.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BestDeal.class)));
        all.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SearchActivity.class)));

        ordered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomerOrderList.class));
            }
        });
        veg.setOnClickListener(v -> {
            intent.putExtra("category", "Vegetables & Fruits");
            startActivity(intent);
        });

        dairy.setOnClickListener(v -> {
            intent.putExtra("category", "Dairy & Breakfast");
            startActivity(intent);
        });

        drinks.setOnClickListener(v -> {
            intent.putExtra("category", "Cold Drinks & Juices");
            startActivity(intent);
        });

        instant.setOnClickListener(v -> {
            intent.putExtra("category", "Instant & Frozen Food");
            startActivity(intent);
        });

        tea.setOnClickListener(v -> {
            intent.putExtra("category", "Tea & Coffee");
            startActivity(intent);
        });

        atta.setOnClickListener(v -> {
            intent.putExtra("category", "Atta, Rice & Dal");
            startActivity(intent);
        });

        masala.setOnClickListener(v -> {
            intent.putExtra("category", "Masala, Oil & Dry Fruits");
            startActivity(intent);
        });

        chicken.setOnClickListener(v -> {
            intent.putExtra("category", "Chicken, Meat & Fish");
            startActivity(intent);
        });

        other.setOnClickListener(v -> {
            intent.putExtra("category", "Other");
            startActivity(intent);
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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CustomerDash.this, "Failed to load customer data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent inten = new Intent(CustomerDash.this, PredictorActivity.class);
            startActivity(inten);
        });

        fab.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + dX);
                        view.setY(event.getRawY() + dY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick();
                        }
                        break;

                    default:
                        return false;
                }
                return true;
            }
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
        return FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }
}
