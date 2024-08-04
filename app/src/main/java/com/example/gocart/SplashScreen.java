package com.example.gocart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.Authentication.UserSelect;
import com.example.gocart.Dashboard.Admin.AdminDash;
import com.example.gocart.Dashboard.Customer.CustomerDash; // Add this import
import com.example.gocart.Dashboard.Customer.SelectDistrict;
import com.example.gocart.Dashboard.Rep.RepDash;
import com.example.gocart.Onboadings.Onboading1;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginStatus, SPLASH_SCREEN_TIMEOUT);
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is logged in, check their role
            checkUserRole(currentUser.getUid());
        } else {
            // User is not logged in, check if it's the first time opening the app
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
            if (isFirstTime) {
                // Go to onboarding pages
                startActivity(new Intent(SplashScreen.this, Onboading1.class));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTime", false);
                editor.apply();
            } else {
                // Go to login page
                startActivity(new Intent(SplashScreen.this, UserSelect.class));
            }
            finish();
        }
    }

    private void checkUserRole(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    String role = task.getResult().child("role").getValue(String.class);
                    switch (role) {
                        case "Admin":
                            startActivity(new Intent(SplashScreen.this, AdminDash.class));
                            break;
                        case "Delivery Representative":
                            startActivity(new Intent(SplashScreen.this, RepDash.class));
                            break;
                        case "customer": // Make sure the role matches the role in the database
                            checkCustomerDetails(userId);
                            break;
                        default:
                            // Unknown role, go to login page
                            startActivity(new Intent(SplashScreen.this, UserSelect.class));
                            break;
                    }
                } else {
                    // User data not found, go to login page
                    startActivity(new Intent(SplashScreen.this, UserSelect.class));
                }
            } else {
                // Error getting user data, go to login page
                startActivity(new Intent(SplashScreen.this, UserSelect.class));
            }
            finish();
        });
    }

    private void checkCustomerDetails(String userId) {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(userId);
        customerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    boolean hasDistrict = task.getResult().child("district").getValue(String.class) != null;
                    boolean hasDivision = task.getResult().child("divisional_secretariat").getValue(String.class) != null;

                    if (hasDistrict && hasDivision) {
                        startActivity(new Intent(SplashScreen.this, CustomerDash.class)); // Navigate to customer dashboard
                    } else {
                        startActivity(new Intent(SplashScreen.this, SelectDistrict.class)); // Navigate to select district
                    }
                } else {
                    // Customer data not found, go to select district
                    startActivity(new Intent(SplashScreen.this, SelectDistrict.class));
                }
            } else {
                // Error getting customer data, go to select district
                startActivity(new Intent(SplashScreen.this, SelectDistrict.class));
            }
            finish();
        });
    }
}
