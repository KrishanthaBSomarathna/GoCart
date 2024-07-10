package com.example.gocart.UserListView.Retailer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Authentication.RetailerAuth.RetailShopCreate;
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
import java.util.Arrays;
import java.util.List;

public class RetailerListOfRep extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RetailerAdapter retailerAdapter;
    private List<Retailer> retailerList;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private List<String> loggedInRepDivisions;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_list);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        floatingActionButton = findViewById(R.id.new_retailer_button);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RetailerListOfRep.this, RetailShopCreate.class));
            }
        });

        retailerList = new ArrayList<>();
        retailerAdapter = new RetailerAdapter(retailerList);
        recyclerView.setAdapter(retailerAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            fetchLoggedInRepDivisions();
        }
    }

    private void fetchLoggedInRepDivisions() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String divisions = dataSnapshot.child("divisional_secretariat").getValue(String.class);
                if (divisions != null) {
                    loggedInRepDivisions = Arrays.asList(divisions.split(",\\s*"));
                    fetchRetailers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void fetchRetailers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("retailer");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retailerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Retailer retailer = snapshot.getValue(Retailer.class);
                    if (retailer != null && "Shop".equals(retailer.getRole()) &&
                            loggedInRepDivisions.contains(retailer.getDivision())) {
                        retailerList.add(retailer);
                    }
                }
                progressBar.setVisibility(View.GONE);
                retailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
