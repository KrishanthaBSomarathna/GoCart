package com.example.gocart.Stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;
    private static final String STORAGE_PATH = "items/"; // Path in Firebase Storage
    private static final String DATABASE_PATH = "repitem"; // Path in Firebase Realtime Database

    private EditText itemNameEditText, quantityEditText, priceEditText, valueEditText;
    private ImageView selectedImage;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        itemNameEditText = findViewById(R.id.name);
        quantityEditText = findViewById(R.id.quantity);
        priceEditText = findViewById(R.id.price);
        valueEditText = findViewById(R.id.value);
        selectedImage = findViewById(R.id.selected_image);

        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    // Open gallery to pick an image
    public void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    // Handle result from gallery intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_PICK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    selectedImage.setImageBitmap(bitmap);
                    selectedImage.setBackground(null); // Remove the placeholder background
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to handle "Add Item" button click
    public void addItemToFirebase(View view) {
        String itemName = itemNameEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String value = valueEditText.getText().toString().trim();

        if (itemName.isEmpty() || quantity.isEmpty() || price.isEmpty() || value.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        showUploadDialog();

        final StorageReference imageRef = storageReference.child(STORAGE_PATH + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dismissUploadDialog();
                Toast.makeText(AddItem.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                dismissUploadDialog();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addItemToDatabase(itemName, quantity, price, value, downloadUri.toString(), userId);
                } else {
                    Toast.makeText(AddItem.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to add item details to Firebase Realtime Database
    private void addItemToDatabase(String itemName, String quantity, String price, String value, String imageUrl, String userId) {
        String itemId = databaseReference.child(DATABASE_PATH).push().getKey();
        if (itemId != null) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("itemName", itemName);
            itemData.put("quantity", quantity);
            itemData.put("price", price);
            itemData.put("value", value);
            itemData.put("imageUrl", imageUrl);
            itemData.put("userId", userId);

            databaseReference.child(DATABASE_PATH).child(userId).child(itemId).setValue(itemData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showUploadCompleteDialog();
                            } else {
                                Toast.makeText(AddItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Show dialog while uploading
    private void showUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uploading Item")
                .setMessage("Please wait while the item is being uploaded...")
                .setCancelable(false);
        uploadDialog = builder.create();
        uploadDialog.show();
    }

    // Dismiss the upload dialog
    private void dismissUploadDialog() {
        if (uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
    }

    // Show dialog when upload is complete
    private void showUploadCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Complete")
                .setMessage("The item has been uploaded successfully.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        clearFields();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    // Clear the input fields after upload
    private void clearFields() {
        itemNameEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        valueEditText.setText("");
        selectedImage.setImageResource(R.drawable.placeholder); // Reset to placeholder image
        imageUri = null;
    }

    private AlertDialog uploadDialog;
}
