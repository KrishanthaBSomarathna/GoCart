package com.example.gocart.Authentication.EmployeeCreate;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DeliveryRiderCreate extends AppCompatActivity {
    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Spinner vehicleTypeSpinner;
    ImageButton createButton;
    DatabaseReference databaseReference;

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

        // Set up the options for the vehicle type spinner
        String[] vehicleTypeOptions = {"Bike", "Three-Wheeler"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);

        createButton.setOnClickListener(this::createDeliveryRider);
    }

    private void createDeliveryRider(View view) {
        String nameText = nameEditText.getText().toString().trim();
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();
        String confirmPasswordText = confirmPasswordEditText.getText().toString().trim();
        String vehicleTypeText = vehicleTypeSpinner.getSelectedItem().toString().trim();

        if (nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || vehicleTypeText.isEmpty()) {
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

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", nameText);
                        userData.put("email", emailText);
                        userData.put("vehicle_type", vehicleTypeText);
                        userData.put("role", "Delivery Rider");

                        databaseReference.child(userId).setValue(userData)
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {
                                        Toast.makeText(this, "Delivery Rider created successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(this, "Error creating Delivery Rider: " + innerTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Error creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
