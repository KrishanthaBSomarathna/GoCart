package com.example.gocart.UserListView.Rep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.Authentication.EmployeeCreate.AdminCreate;
import com.example.gocart.Authentication.EmployeeCreate.DeliveryRepCreate;
import com.example.gocart.Model.User;
import com.example.gocart.R;
import com.example.gocart.UserListView.Rep.DeliveryRepAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRepList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeliveryRepAdapter deliveryRepAdapter;
    private List<User> deliveryRepList;
    private DatabaseReference databaseReference;
    private FloatingActionButton newDeliveryRepButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_list);

        newDeliveryRepButton = findViewById(R.id.new_delivery_rep_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deliveryRepList = new ArrayList<>();
        deliveryRepAdapter = new DeliveryRepAdapter(deliveryRepList);
        recyclerView.setAdapter(deliveryRepAdapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        newDeliveryRepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryRepList.this, DeliveryRepCreate.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.orderByChild("role").equalTo("Delivery Representative");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryRepList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    deliveryRepList.add(user);
                }
                deliveryRepAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
