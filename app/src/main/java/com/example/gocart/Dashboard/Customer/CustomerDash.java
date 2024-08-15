package com.example.gocart.Dashboard.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Dashboard.Customer.Item.CustomerItemAdapter;
import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.example.gocart.Stock.AddItem;
import com.example.gocart.Stock.ShopItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    private DatabaseReference databaseReference;

    private static final List<String> DIVISIONS = Arrays.asList("Wariyapola", "Nikaweratiya");
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();
        customerItemAdapter = new CustomerItemAdapter(this, filteredItemList);
        recyclerView.setAdapter(customerItemAdapter);

        // Set database reference to the general items path
        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null && item.isBestdeal() && DIVISIONS.contains(item.getDivision())) {
                            itemList.add(item);
                        }
                    }
                }
                filterItemList(""); // Initially show all filtered items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerDash.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerDash.this, AddItem.class);
            startActivity(intent);
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
}
