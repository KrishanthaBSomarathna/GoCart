package com.example.gocart.Authentication.CustomerAuth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerRegister extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private ImageView signUpButton;
    private ImageButton login;
    private TextView error1, error2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameEditText = findViewById(R.id.editText3);
        emailEditText = findViewById(R.id.editText4);
        phoneEditText = findViewById(R.id.editText5);
        passwordEditText = findViewById(R.id.editText6);
        confirmPasswordEditText = findViewById(R.id.editText7);
        signUpButton = findViewById(R.id.signUpButton);
        login = findViewById(R.id.login);
        error1 = findViewById(R.id.error1);
        error2 = findViewById(R.id.error2);

        error1.setVisibility(View.GONE);
        error2.setVisibility(View.GONE);

        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String role = "customer";

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(CustomerRegister.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(CustomerRegister.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(CustomerRegister.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else {
                if (isNetworkAvailable()) {
                    checkEmailExists(email, name, phone, password, role);
                } else {
                    Toast.makeText(CustomerRegister.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerRegister.this, CustomerLogin.class);
            startActivity(intent);
        });
    }

    private void checkEmailExists(String email, String name, String phone, String password, String role) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    error1.setVisibility(View.VISIBLE);
                    error2.setVisibility(View.VISIBLE);
                } else {
                    proceedToMobileAuth(name, email, phone, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CustomerRegister.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToMobileAuth(String name, String email, String phone, String password) {
        Intent intent = new Intent(CustomerRegister.this, CustomerMobileAuth.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
