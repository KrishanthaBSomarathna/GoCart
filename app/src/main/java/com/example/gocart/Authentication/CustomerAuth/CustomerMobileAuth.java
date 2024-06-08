package com.example.gocart.Authentication.CustomerAuth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.Authentication.EmployeeCreate.AdminCreate;
import com.example.gocart.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class CustomerMobileAuth extends AppCompatActivity {

    private String name, email, phone, password;
    private EditText otpEditText;
    private ImageView verifyButton;
    private TextView resendTextView, phoneNumber1, phoneNumber2;
    private FirebaseAuth mAuth;
    private String verificationId;
    private RelativeLayout sendOtp, verifyOtp;
    private ImageButton sendOtpbtn, cancelbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mobile_auth);

        otpEditText = findViewById(R.id.editText8);
        verifyButton = findViewById(R.id.verifyButton);
        resendTextView = findViewById(R.id.resendTextView);
        sendOtp = findViewById(R.id.sentOtp);
        verifyOtp = findViewById(R.id.verifyOtp);
        sendOtpbtn = findViewById(R.id.sendOtpbtn);
        cancelbtn = findViewById(R.id.cancelbtn);
        phoneNumber1 = findViewById(R.id.phoneNumber1);
        phoneNumber2 = findViewById(R.id.phoneNumber2);

        verifyOtp.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");

        phoneNumber1.setText(phone);
        phoneNumber2.setText(phone);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendOtpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(phone);
                sendOtp.setVisibility(View.GONE);
                verifyOtp.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Sending OTP...", Toast.LENGTH_LONG).show();
            }
        });

        verifyButton.setOnClickListener(v -> {
            String code = otpEditText.getText().toString();
            if (code.isEmpty() || code.length() < 6) {
                otpEditText.setError("Enter valid code");
                otpEditText.requestFocus();
                return;
            }
            verifyCode(code);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                otpEditText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("VerificationFailed", "onVerificationFailed: " + e.getMessage());
            Toast.makeText(CustomerMobileAuth.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken token,PhoneAuthCredential credential) {
            super.onCodeSent(s, token);
            String code = credential.getSmsCode();

            otpEditText.setText(code);
            verificationId = s;
            sendOtp.setVisibility(View.GONE);
            verifyOtp.setVisibility(View.VISIBLE);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthProvider.getCredential(verificationId, code);
        createUserWithEmailAndPassword(email, password);

    }

    private void createUserWithEmailAndPassword(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully created
                        createFirebaseUser();


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(CustomerMobileAuth.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createFirebaseUser() {
        // Get a reference to the "Customer" node in Firebase
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer");

        // Retrieve the count of existing customers
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long customerCount = dataSnapshot.getChildrenCount();

                // Increment the customer count by 1 to get the new user ID
                long newUserId = customerCount + 1;

                // Create the new user ID string
                String userId = "customer" + newUserId;

                // Create the user object
                User user = new User(name, email, phone);

                // Store the user in Firebase using the generated user ID
                customerRef.child(userId).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CustomerMobileAuth.this, "User registered successfully", Toast.LENGTH_LONG).show();
                        // Redirect to another activity or close
                    } else {
                        Toast.makeText(CustomerMobileAuth.this, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerMobileAuth.this, "Error retrieving customer count: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class User {
        public String name, email, phone,role;

        public User() {
        }

        public User(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.role = "customer";
        }
    }
}
