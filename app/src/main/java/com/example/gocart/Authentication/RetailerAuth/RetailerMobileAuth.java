package com.example.gocart.Authentication.RetailerAuth;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetailerMobileAuth extends AppCompatActivity {

    private String name, email, phone, password, district, division, latitude, longitude;
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
        district = intent.getStringExtra("district");
        division = intent.getStringExtra("division");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        phoneNumber1.setText(phone);
        phoneNumber2.setText(phone);

        cancelbtn.setOnClickListener(v -> finish());

        sendOtpbtn.setOnClickListener(v -> {
            sendVerificationCode(phone);
            sendOtp.setVisibility(View.GONE);
            verifyOtp.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Sending OTP...", Toast.LENGTH_LONG).show();
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
            Toast.makeText(RetailerMobileAuth.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationId = s;
            sendOtp.setVisibility(View.GONE);
            verifyOtp.setVisibility(View.VISIBLE);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                createFirebaseUser();
            } else {
                Toast.makeText(RetailerMobileAuth.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createFirebaseUser() {
        DatabaseReference retailerRef = FirebaseDatabase.getInstance().getReference("retailer");
        retailerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long retailerCount = dataSnapshot.getChildrenCount();
                long newUserId = retailerCount + 1;
                String uId = "RT" + newUserId;

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                DatabaseReference data = FirebaseDatabase.getInstance().getReference("users");
                data.child(userId).child("role").setValue("Shop");
                Map<String, Object> retailerInfo = new HashMap<>();
                retailerInfo.put("name", name);
                retailerInfo.put("email", email);
                retailerInfo.put("phone", phone);
                retailerInfo.put("latitude", Double.parseDouble(latitude));
                retailerInfo.put("longitude", Double.parseDouble(longitude));
                retailerInfo.put("role", "Shop");
                retailerInfo.put("district", district);
                retailerInfo.put("division", division);
                retailerInfo.put("uId", uId);

                retailerRef.child(userId).setValue(retailerInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RetailerMobileAuth.this, "Retailer registered successfully", Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(RetailerMobileAuth.this, RetailerLogin.class));
                                    } else {
                                        Toast.makeText(RetailerMobileAuth.this, "Failed to create user: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RetailerMobileAuth.this, "Failed to register retailer: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RetailerMobileAuth.this, "Error retrieving retailer count: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

