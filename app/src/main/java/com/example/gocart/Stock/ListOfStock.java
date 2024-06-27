package com.example.gocart.Stock;

import android.content.Intent;
import android.os.Bundle;
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
    private RepItemAdapter repItemAdapter;
    private List<Item> itemList;

    private DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private String currentUserId ; // Set the logged-in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_stock); // Ensure correct layout file is referenced

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            currentUserId = firebaseUser.getUid();
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Set GridLayoutManager programmatically
        itemList = new ArrayList<>();
        repItemAdapter = new RepItemAdapter(this, itemList);
        recyclerView.setAdapter(repItemAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("repitem").child(currentUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    itemList.add(item);
                }
                repItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListOfStock.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Handle FAB click action
            Intent intent = new Intent(ListOfStock.this, AddItem.class); // Replace with your actual activity
            startActivity(intent);
        });
    }
}
