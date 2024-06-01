package com.example.gocart.Authentication.RetailerAuth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.example.gocart.Authentication.UserLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RetailShopCreate extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private AutoCompleteTextView locationAutoCompleteTextView;
    private ImageButton setLocationButton, createButton, loginButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private double latitude, longitude;
    private List<String> locationsList;

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

        nameEditText = findViewById(R.id.editText9);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.editText);
        passwordEditText = findViewById(R.id.editText13);
        confirmPasswordEditText = findViewById(R.id.editText14);
        locationAutoCompleteTextView = findViewById(R.id.divisional_secretariat);
        setLocationButton = findViewById(R.id.setlocation);
        createButton = findViewById(R.id.createButton);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        locationsList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("locations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String location = snapshot.getValue(String.class);
                            if (location != null) {
                                locationsList.add(location);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                RetailShopCreate.this,
                                android.R.layout.simple_dropdown_item_1line,
                                locationsList);
                        locationAutoCompleteTextView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RetailShopCreate.this,
                                "Failed to retrieve locations",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        setLocationButton.setOnClickListener(view -> {
            Intent intent = new Intent(RetailShopCreate.this, SetLocation.class);
            startActivityForResult(intent, 1);
        });

        createButton.setOnClickListener(view -> registerShop());

        loginButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserLogin.class)));
    }

    private void registerShop() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String location = locationAutoCompleteTextView.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                location.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            DatabaseReference userRef = databaseReference.child("users").child(uid);
                            userRef.child("name").setValue(name);
                            userRef.child("email").setValue(email);
                            userRef.child("phone").setValue(phone);
                            userRef.child("location").setValue(location);
                            Toast.makeText(this, "Shop registered successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error registering shop: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
        }
    }
}
