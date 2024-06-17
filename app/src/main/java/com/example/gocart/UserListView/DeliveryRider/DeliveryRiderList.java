package com.example.gocart.UserListView.DeliveryRider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Authentication.EmployeeCreate.DeliveryRiderCreate;
import com.example.gocart.Model.DeliveryRider;
import com.example.gocart.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRiderList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeliveryRiderAdapter riderAdapter;
    private List<DeliveryRider> riderList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private FloatingActionButton newDeliveryRider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider_list);

        newDeliveryRider = findViewById(R.id.newDeliveryRider);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        riderList = new ArrayList<>();
        riderAdapter = new DeliveryRiderAdapter(riderList);
        recyclerView.setAdapter(riderAdapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        newDeliveryRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryRiderList.this, DeliveryRiderCreate.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.orderByChild("role").equalTo("Delivery Rider");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                riderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DeliveryRider rider = snapshot.getValue(DeliveryRider.class);
                    riderList.add(rider);
                    progressBar.setVisibility(View.GONE);
                }
                riderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
