package com.example.gocart.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.Dashboard.Admin.AdminDash;
import com.example.gocart.Dashboard.Rep.RepDash;
import com.example.gocart.Dashboard.Retailer.RetailerDash;
import com.example.gocart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Spinner userTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.editText);
        passwordField = findViewById(R.id.editText2);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);

        // Set up the options for the spinner
        String[] userTypeOptions = {"Delivery Representative","Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        findViewById(R.id.loginButton).setOnClickListener(this::loginUser);
    }

    private void loginUser(View view) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String selectedRole = userTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(UserLogin.this, "Email and Password fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid(), selectedRole);
                        }
                    } else {
                        Toast.makeText(UserLogin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String uid, String selectedRole) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.child("role").getValue(String.class);
                if (selectedRole.equals(role)) {
                    switch (role) {
                        case "Admin":
                            Toast.makeText(UserLogin.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserLogin.this, AdminDash.class));
                            break;
                        case "Delivery Representative":
                            Toast.makeText(UserLogin.this, "Delivery Representative login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserLogin.this, RepDash.class));
                            // Add intent to navigate to the delivery representative dashboard
                            break;
                        default:
                            Toast.makeText(UserLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Add intent to navigate to the respective dashboard for other roles if necessary
                            break;
                    }
                } else {
                    Toast.makeText(UserLogin.this, "You are not authorized to login as " + selectedRole, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserLogin.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
