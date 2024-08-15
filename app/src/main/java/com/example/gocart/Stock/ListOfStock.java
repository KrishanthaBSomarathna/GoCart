package com.example.gocart.Stock;

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

import com.example.gocart.Model.Item;
import com.example.gocart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfStock extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopItemAdapter shopItemAdapter;
    private List<Item> itemList;
    private List<Item> filteredItemList;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stock);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();
        shopItemAdapter = new ShopItemAdapter(this, filteredItemList);
        recyclerView.setAdapter(shopItemAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("shopitem").child(currentUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    itemList.add(item);
                }
                filterItemList(""); // Initially show all items
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListOfStock.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        EditText entersearch = findViewById(R.id.entersearch);
        entersearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterItemList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ListOfStock.this, AddItem.class);
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
        shopItemAdapter.notifyDataSetChanged();
    }
}