package com.example.gocart.Authentication.RetailerAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetailShopCreate extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText, latitudeEditText, longitudeEditText;
    private AutoCompleteTextView districtTextView, divisionTextView;
    private ImageButton setLocationButton, createButton, loginButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private double latitude, longitude;
    private List<String> districtList;
    private Map<String, List<String>> divisionListMap;
    private TextView error;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retail_shop_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        loginButton = findViewById(R.id.loginButton);
        nameEditText = findViewById(R.id.editText9);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.editText);
        passwordEditText = findViewById(R.id.editText13);
        confirmPasswordEditText = findViewById(R.id.editText14);
        latitudeEditText = findViewById(R.id.latitude);
        longitudeEditText = findViewById(R.id.longitude);
        districtTextView = findViewById(R.id.district);
        divisionTextView = findViewById(R.id.division);
        setLocationButton = findViewById(R.id.setlocation);
        createButton = findViewById(R.id.createButton);
        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);
        scrollView = findViewById(R.id.srollview);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        districtList = new ArrayList<>();
        divisionListMap = new HashMap<>();

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
                                RetailShopCreate.this,
                                android.R.layout.simple_dropdown_item_1line,
                                districtList);
                        districtTextView.setAdapter(districtAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RetailShopCreate.this,
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
                        RetailShopCreate.this,
                        android.R.layout.simple_dropdown_item_1line,
                        divisionList);
                divisionTextView.setAdapter(divisionAdapter);
            }
        });

        // Set location button click listener
        setLocationButton.setOnClickListener(view -> {
            Intent intent = new Intent(RetailShopCreate.this, SetLocation.class);
            startActivityForResult(intent, 1);
        });

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(RetailShopCreate.this, RetailerLogin.class);
            startActivityForResult(intent, 1);
        });

        // Create button click listener
        createButton.setOnClickListener(view -> checkEmailExists(emailEditText.getText().toString()));
    }

    private void checkEmailExists(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> signInMethods = task.getResult().getSignInMethods();
                if (signInMethods != null && !signInMethods.isEmpty()) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Email is already registered");
                    scrollView.scrollTo(0, 0);
                } else {
                    registerShop();
                }
            } else {
                Toast.makeText(RetailShopCreate.this, "Failed to check email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerShop() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String district = districtTextView.getText().toString().trim();
        String division = divisionTextView.getText().toString().trim();
        String latitudeStr = latitudeEditText.getText().toString().trim();
        String longitudeStr = longitudeEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                district.isEmpty() || division.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RetailShopCreate.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
            return;
        }



        // Proceed to phone number verification
        Intent intent = new Intent(RetailShopCreate.this, RetailerMobileAuth.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        intent.putExtra("district", district);
        intent.putExtra("division", division);
        intent.putExtra("latitude", latitudeStr);
        intent.putExtra("longitude", longitudeStr);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            latitudeEditText.setText(String.valueOf(latitude));
            longitudeEditText.setText(String.valueOf(longitude));
        }
    }
}
