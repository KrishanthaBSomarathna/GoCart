package com.example.gocart.Authentication.EmployeeCreate;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryRiderCreate extends AppCompatActivity {
    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Spinner vehicleTypeSpinner;
    ImageButton createButton;
    DatabaseReference databaseReference;
    TextView error;
    private AutoCompleteTextView districtTextView, divisionTextView;
    private List<String> districtList;
    private Map<String, List<String>> divisionListMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_rider_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        nameEditText = findViewById(R.id.editText9);
        emailEditText = findViewById(R.id.emailEditText);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        passwordEditText = findViewById(R.id.editText13);
        confirmPasswordEditText = findViewById(R.id.editText14);
        createButton = findViewById(R.id.createButton);
        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);

        // Initialize AutoCompleteTextViews
        districtTextView = findViewById(R.id.district);
        divisionTextView = findViewById(R.id.division);

        districtList = new ArrayList<>();
        divisionListMap = new HashMap<>();

        // Set up the options for the vehicle type spinner
        String[] vehicleTypeOptions = {"Bike", "Three-Wheeler"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);

        createButton.setOnClickListener(this::createDeliveryRider);

        // Fetch location data from Firebase
        FirebaseDatabase.getInstance().getReference("location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot districtSnapshot : dataSnapshot.getChildren()) {
                            String district = districtSnapshot.getKey();
                            if (district != null) {
                                districtList.add(district);
                                List<String> divisionList = new ArrayList<>();
                                for (DataSnapshot divisionSnapshot : districtSnapshot.getChildren()) {
                                    String division = divisionSnapshot.getValue(String.class);
                                    if (division != null) {
                                        divisionList.add(division);
                                    }
                                }
                                divisionListMap.put(district, divisionList);
                            }
                        }

                        // Set district adapter
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                                DeliveryRiderCreate.this,
                                android.R.layout.simple_dropdown_item_1line,
                                districtList);
                        districtTextView.setAdapter(districtAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DeliveryRiderCreate.this,
                                "Failed to retrieve location data",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Handle district selection
        districtTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDistrict = (String) parent.getItemAtPosition(position);
            List<String> divisionList = divisionListMap.get(selectedDistrict);

            if (divisionList != null) {
                ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(
                        DeliveryRiderCreate.this,
                        android.R.layout.simple_dropdown_item_1line,
                        divisionList);
                divisionTextView.setAdapter(divisionAdapter);
            }
        });
    }

    private void createDeliveryRider(View view) {
        String nameText = nameEditText.getText().toString().trim();
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();
        String confirmPasswordText = confirmPasswordEditText.getText().toString().trim();
        String vehicleTypeText = vehicleTypeSpinner.getSelectedItem().toString().trim();
        String districtText = districtTextView.getText().toString().trim();
        String divisionText = divisionTextView.getText().toString().trim();

        if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || vehicleTypeText.isEmpty() || districtText.isEmpty() || divisionText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordText.length() < 6) {
            Toast.makeText(DeliveryRiderCreate.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordText.equals(confirmPasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("role").equalTo("Delivery Rider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxId = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.child("uid").getValue(String.class);
                    if (uid != null && uid.startsWith("RIDER")) {
                        try {
                            long id = Long.parseLong(uid.replace("RIDER", ""));
                            if (id > maxId) {
                                maxId = id;
                            }
                        } catch (NumberFormatException e) {
                            // Handle the case where the uid is not in the expected format
                        }
                    }
                }
                String newRiderId = "RIDER" + (maxId + 1);

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("unicId", newRiderId);  // Using RIDER ID format
                                userData.put("name", nameText);
                                userData.put("email", emailText);
                                userData.put("vehicle_type", vehicleTypeText);
                                userData.put("district", districtText);
                                userData.put("division", divisionText);
                                userData.put("role", "Delivery Rider");

                                databaseReference.child(userId).setValue(userData)
                                        .addOnCompleteListener(innerTask -> {
                                            if (innerTask.isSuccessful()) {
                                                Toast.makeText(DeliveryRiderCreate.this, "Delivery Rider created successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(DeliveryRiderCreate.this, "Error creating Delivery Rider: " + innerTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(DeliveryRiderCreate.this, "Error creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                error.setVisibility(View.VISIBLE);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeliveryRiderCreate.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
