package com.example.gocart.UserListView.DeliveryRider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Authentication.EmployeeCreate.DeliveryRiderCreate;
import com.example.gocart.Model.DeliveryRider;
import com.example.gocart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeliveryRiderListOfRep extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeliveryRiderAdapter riderAdapter;
    private List<DeliveryRider> riderList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private FloatingActionButton newDeliveryRider;

    private List<String> repDivisions; // List to store rep's divisions
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider_list);

        newDeliveryRider = findViewById(R.id.newDeliveryRider);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        riderList = new ArrayList<>();
        riderAdapter = new DeliveryRiderAdapter(riderList);
        recyclerView.setAdapter(riderAdapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // Assuming you have stored the rep's UID somewhere, fetch the data
        assert firebaseUser != null;
        String repUid = (String)firebaseUser.getUid(); // Replace with actual logged-in rep's UID
        Toast.makeText(getApplicationContext(),repUid,Toast.LENGTH_SHORT).show();

        // Fetch rep's data from Firebase
        DatabaseReference repRef = FirebaseDatabase.getInstance().getReference("users").child(repUid);
        repRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve divisional_secretariat from rep's data
                    repDivisions = new ArrayList<>();
                    String divisionalSecretariat = dataSnapshot.child("divisional_secretariat").getValue(String.class);
                    if (divisionalSecretariat != null) {
                        // Split the divisional secretariat string into individual divisions
                        repDivisions = Arrays.asList(divisionalSecretariat.split(","));
                        for (int i = 0; i < repDivisions.size(); i++) {
                            repDivisions.set(i, repDivisions.get(i).trim()); // Trim whitespace from divisions
                        }
                    }

                    // Proceed with loading delivery riders based on rep's divisions
                    loadDeliveryRiders();
                } else {
                    // Handle case where rep's data doesn't exist
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DeliveryRiderListOfRep.this, "Rep's data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeliveryRiderListOfRep.this, "Failed to fetch rep's data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        newDeliveryRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryRiderListOfRep.this, DeliveryRiderCreate.class));
            }
        });
    }

    private void loadDeliveryRiders() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Query to fetch all delivery riders
        Query query = databaseReference.orderByChild("role").equalTo("Delivery Rider");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                riderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the user has the role of "Delivery Rider"
                    String role = snapshot.child("role").getValue(String.class);
                    if (role != null && role.equals("Delivery Rider")) {
                        // Check if the delivery rider's division matches any of rep's divisions
                        String division = snapshot.child("division").getValue(String.class);
                        if (division != null && doesMatchRepDivision(division)) {
                            DeliveryRider rider = snapshot.getValue(DeliveryRider.class);
                            riderList.add(rider);
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                riderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeliveryRiderListOfRep.this, "Failed to load delivery riders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the delivery rider's division matches any of rep's divisions
    private boolean doesMatchRepDivision(String division) {
        for (String repDivision : repDivisions) {
            if (division.contains(repDivision)) {
                return true;
            }
        }
        return false;
    }
}
