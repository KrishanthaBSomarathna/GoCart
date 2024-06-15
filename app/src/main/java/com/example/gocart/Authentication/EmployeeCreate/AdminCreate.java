package com.example.gocart.Authentication.EmployeeCreate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.util.HashMap;
import java.util.Map;

public class AdminCreate extends AppCompatActivity {
    EditText name, email, password, confirmPassword;
    ImageButton create;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        create = findViewById(R.id.Create);
        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);

        create.setOnClickListener(v -> checkEmailExists(email.getText().toString().trim()));
    }

    private void checkEmailExists(String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String role = snapshot.child("role").getValue(String.class);
                        if ("Admin".equals(role)) {
                            error.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
                createAdmin();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminCreate.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAdmin() {
        String emailText = email.getText().toString().trim();
        String nameText = name.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(nameText) ||
                TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)) {
            Toast.makeText(AdminCreate.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordText.length() < 6) {
            Toast.makeText(AdminCreate.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordText.equals(confirmPasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check count of existing admin users
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("users");
        adminRef.orderByChild("role").equalTo("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxId = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.child("unicId").getValue(String.class);
                    if (uid != null && uid.startsWith("admin")) {
                        try {
                            long id = Long.parseLong(uid.replace("admin", ""));
                            if (id > maxId) {
                                maxId = id;
                            }
                        } catch (NumberFormatException e) {
                            // Handle the case where the uid is not in the expected format
                        }
                    }
                }
                long newAdminId = maxId + 1;
                String adminId = "admin" + newAdminId;

                // Create a new user with email and password
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(AdminCreate.this, task -> {
                            if (task.isSuccessful()) {
                                // User is successfully created
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    User admin = new User(nameText, emailText, "Admin", adminId);
                                    databaseReference.child(user.getUid()).setValue(admin)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(AdminCreate.this, "Admin created successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AdminCreate.this, "Database error: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(AdminCreate.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminCreate.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // User class to represent admin user
    public static class User {
        public String name;
        public String email;
        public String role;
        public String unicId;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String name, String email, String role, String unicId) {
            this.name = name;
            this.email = email;
            this.role = role;
            this.unicId = unicId;
        }
    }
}
