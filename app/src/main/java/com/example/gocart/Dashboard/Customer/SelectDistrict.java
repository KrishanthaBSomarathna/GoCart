package com.example.gocart.Dashboard.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectDistrict extends AppCompatActivity {

    private AutoCompleteTextView districtTextView;
    private MultiAutoCompleteTextView divisionTextView;
    private DatabaseReference databaseReference;
    private ImageButton apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_district);

        districtTextView = findViewById(R.id.district);
        divisionTextView = findViewById(R.id.division);

        databaseReference = FirebaseDatabase.getInstance().getReference("location");
        apply = findViewById(R.id.apply);

        apply.setOnClickListener(v -> saveUserLocation());

        loadDistricts();
        loadUserLocation(); // Load user location on activity start
    }

    private void loadDistricts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> districts = new ArrayList<>();
                for (DataSnapshot districtSnapshot : dataSnapshot.getChildren()) {
                    districts.add(districtSnapshot.getKey());
                }

                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(SelectDistrict.this,
                        android.R.layout.simple_dropdown_item_1line, districts);
                districtTextView.setAdapter(districtAdapter);

                districtTextView.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDistrict = (String) parent.getItemAtPosition(position);
                    loadDivisions(selectedDistrict);
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadDivisions(String district) {
        databaseReference.child(district).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> divisions = new ArrayList<>();
                for (DataSnapshot divisionSnapshot : dataSnapshot.getChildren()) {
                    divisions.add(divisionSnapshot.getValue(String.class));
                }

                ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(SelectDistrict.this,
                        android.R.layout.simple_dropdown_item_1line, divisions);
                divisionTextView.setAdapter(divisionAdapter);
                divisionTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadUserLocation() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLocationRef = FirebaseDatabase.getInstance().getReference("Customer").child(userId);

        userLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String district = dataSnapshot.child("district").getValue(String.class);
                String divisions = dataSnapshot.child("divisional_secretariat").getValue(String.class);

                if (district != null) {
                    districtTextView.setText(district);
                    loadDivisions(district); // Load divisions for the pre-selected district
                }

                if (divisions != null) {
                    divisionTextView.setText(divisions);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void saveUserLocation() {
        String selectedDistrict = districtTextView.getText().toString().trim();
        String selectedDivisions = divisionTextView.getText().toString().trim();

        if (selectedDistrict.isEmpty() || selectedDivisions.isEmpty()) {
            Toast.makeText(this, "Please select both district and divisions", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLocationRef = FirebaseDatabase.getInstance().getReference("Customer").child(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("district", selectedDistrict);
        updates.put("divisional_secretariat", selectedDivisions);

        userLocationRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SelectDistrict.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SelectDistrict.this, CustomerDash.class));
                    } else {
                        Toast.makeText(SelectDistrict.this, "Error saving location: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
