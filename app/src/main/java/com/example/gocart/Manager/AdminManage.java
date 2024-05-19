package com.example.gocart.Manager;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gocart.R;

import java.util.ArrayList;
import java.util.List;

public class AdminManage extends AppCompatActivity {
    private TableLayout tableLayout;
    private List<Admin> adminList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_manage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tableLayout = findViewById(R.id.admin_table);

        // Populate the admin list with sample data
        adminList = new ArrayList<>();
        adminList.add(new Admin("A001", "John Doe"));
        adminList.add(new Admin("A002", "Jane Smith"));
        // Add more admins as needed

        populateTable();
    }
    private void populateTable() {
        for (Admin admin : adminList) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Admin Code TextView
            TextView codeTextView = new TextView(this);
            codeTextView.setText(admin.getCode());
            codeTextView.setPadding(8, 8, 8, 8);
            codeTextView.setGravity(Gravity.CENTER);
            tableRow.addView(codeTextView);

            // Admin Name TextView
            TextView nameTextView = new TextView(this);
            nameTextView.setText(admin.getName());
            nameTextView.setPadding(8, 8, 8, 8);
            nameTextView.setGravity(Gravity.CENTER);
            tableRow.addView(nameTextView);

            // LinearLayout for buttons
            LinearLayout actionLayout = new LinearLayout(this);
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionLayout.setPadding(8, 8, 8, 8);

            // Edit Button
            Button editButton = new Button(this);
            editButton.setText("Edit");
            editButton.setOnClickListener(v -> {
                // Handle edit action
            });
            actionLayout.addView(editButton);

            // Delete Button
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> {
                // Handle delete action
            });
            actionLayout.addView(deleteButton);

            // Add the LinearLayout to the TableRow
            tableRow.addView(actionLayout);

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow);
        }
    }

}