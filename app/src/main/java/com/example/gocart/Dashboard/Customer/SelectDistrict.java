package com.example.gocart.Dashboard.Customer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SelectDistrict extends AppCompatActivity {

    private AutoCompleteTextView districtTextView;
    private AutoCompleteTextView divisionTextView;
    private DatabaseReference databaseReference;
    private String selectedDistrict,selectDivision;
    private ImageButton apply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_district);

        districtTextView = findViewById(R.id.district);
        divisionTextView = findViewById(R.id.division);

        databaseReference = FirebaseDatabase.getInstance().getReference("location");

        apply = findViewById(R.id.apply);

        loadDistricts();

        
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
