package com.example.gocart.UserListView.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Authentication.CustomerAuth.CustomerRegister;
import com.example.gocart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private ProgressBar progressBar;
private FloatingActionButton floatingActionButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(this, customerList);
        recyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerList.this, CustomerRegister.class));
            }
        });
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer");

        // Fetch customer data from Firebase
        fetchCustomerData();
    }

    private void fetchCustomerData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);
                    if (customer != null) {
                        customerList.add(customer);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
