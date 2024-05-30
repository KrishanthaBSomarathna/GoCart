package com.example.gocart.UserListView.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gocart.R;
import com.example.gocart.Authentication.EmployeeCreate.AdminCreate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminAdapter adminAdapter;
    private List<Admin> adminList;
    private ProgressBar progressBar;

    private FloatingActionButton newAdminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_list);

        progressBar = findViewById(R.id.progressBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adminList = new ArrayList<>();
        adminAdapter = new AdminAdapter(this, adminList);
        recyclerView.setAdapter(adminAdapter);

        newAdminButton = findViewById(R.id.newAdminButton);

        fetchAdmins();

        newAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminList.this, AdminCreate.class);
                startActivity(intent);
            }
        });
    }

    private void fetchAdmins() {
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar

        FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("role")
                .equalTo("Admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adminList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Admin admin = dataSnapshot.getValue(Admin.class);
                            if (admin != null) {
                                admin.setId(dataSnapshot.getKey());
                                adminList.add(admin);
                            }
                        }
                        adminAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                        progressBar.setVisibility(View.GONE); // Hide the ProgressBar in case of error
                    }
                });
    }
}
