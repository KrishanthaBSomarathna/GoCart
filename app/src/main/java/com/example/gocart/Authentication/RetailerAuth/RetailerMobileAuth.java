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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveUserToDatabase();
                startActivity(new Intent(RetailerMobileAuth.this, RetailerLogin.class));
                Toast.makeText(RetailerMobileAuth.this, "Email User Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RetailerMobileAuth.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserToDatabase() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = ((FirebaseUser) firebaseUser).getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("retailer").child(uid);
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("phone", phone);
            user.put("latitude", Double.parseDouble(latitude));
            user.put("longitude", Double.parseDouble(longitude));
            user.put("role", "Shop");
            user.put("district", district);
            user.put("division", division);

            userRef.setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RetailerMobileAuth.this, "Shop registered successfully", Toast.LENGTH_LONG).show();
                    // Redirect to another activity or close
                } else {
                    Toast.makeText(RetailerMobileAuth.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(RetailerMobileAuth.this, "Failed to create user", Toast.LENGTH_SHORT).show();
        }
    }
}
