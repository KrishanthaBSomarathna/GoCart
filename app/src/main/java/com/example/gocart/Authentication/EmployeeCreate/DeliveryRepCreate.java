package com.example.gocart.Authentication.EmployeeCreate;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class DeliveryRepCreate extends AppCompatActivity {
    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    AutoCompleteTextView districtTextView;
    MultiAutoCompleteTextView divisionalSecretariatTextView;
    ImageButton createButton;
    DatabaseReference databaseReference;
    DatabaseReference locationReference;
    Map<String, List<String>> locationData;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_rep_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        locationReference = database.getReference("location");

        nameEditText = findViewById(R.id.editText9);
        emailEditText = findViewById(R.id.emailEditText);
        districtTextView = findViewById(R.id.district);
        divisionalSecretariatTextView = findViewById(R.id.divisional_secretariat);
        passwordEditText = findViewById(R.id.editText13);
        confirmPasswordEditText = findViewById(R.id.editText14);
        createButton = findViewById(R.id.createButton);
        error = findViewById(R.id.error);

        error.setVisibility(View.GONE);

        locationData = new HashMap<>();

        loadLocationData();

        districtTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDistrict = (String) parent.getItemAtPosition(position);
            List<String> dsList = locationData.get(selectedDistrict);
            if (dsList != null) {
                ArrayAdapter<String> dsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dsList);
                divisionalSecretariatTextView.setAdapter(dsAdapter);
                divisionalSecretariatTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }
        });

        createButton.setOnClickListener(v -> checkEmailExists(emailEditText.getText().toString()));
    }

    private void loadLocationData() {
        locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot districtSnapshot : snapshot.getChildren()) {
                    String districtName = districtSnapshot.getKey();
                    List<String> dsList = new ArrayList<>();
                    for (DataSnapshot dsSnapshot : districtSnapshot.getChildren()) {
                        dsList.add(dsSnapshot.getValue(String.class));
                    }
                    locationData.put(districtName, dsList);
                }
                List<String> districtList = new ArrayList<>(locationData.keySet());
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(DeliveryRepCreate.this, android.R.layout.simple_dropdown_item_1line, districtList);
                districtTextView.setAdapter(districtAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DeliveryRepCreate.this, "Error loading locations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailExists(String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    error.setVisibility(View.VISIBLE);
                } else {
                    createDeliveryRep();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeliveryRepCreate.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDeliveryRep() {
        String nameText = nameEditText.getText().toString().trim();
        String emailText = emailEditText.getText().toString().trim();
        String districtText = districtTextView.getText().toString().trim();
        String dsText = divisionalSecretariatTextView.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();
        String confirmPasswordText = confirmPasswordEditText.getText().toString().trim();

        if (nameText.isEmpty() || emailText.isEmpty() || districtText.isEmpty() || dsText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordText.length() < 6) {
            Toast.makeText(DeliveryRepCreate.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordText.equals(confirmPasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("role").equalTo("Delivery Representative").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxId = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.child("uid").getValue(String.class);
                    if (uid != null && uid.startsWith("REP")) {
                        try {
                            long id = Long.parseLong(uid.replace("REP", ""));
                            if (id > maxId) {
                                maxId = id;
                            }
                        } catch (NumberFormatException e) {
                            // Handle the case where the uid is not in the expected format
                        }
                    }
                }
                String newRepId = "REP" + (maxId + 1);

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("uid", newRepId);  // Using REP ID format
                                userData.put("name", nameText);
                                userData.put("email", emailText);
                                userData.put("district", districtText);
                                userData.put("divisional_secretariat", dsText);
                                userData.put("role", "Delivery Representative");

                                databaseReference.child(userId).setValue(userData)
                                        .addOnCompleteListener(innerTask -> {
                                            if (innerTask.isSuccessful()) {
                                                Toast.makeText(DeliveryRepCreate.this, "Delivery Rep created successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(DeliveryRepCreate.this, "Error creating Delivery Rep: " + innerTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(DeliveryRepCreate.this, "Error creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DeliveryRepCreate.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
