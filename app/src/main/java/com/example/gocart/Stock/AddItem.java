package com.example.gocart.Stock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gocart.Model.Item;
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

public class AddItem extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;
    private static final String STORAGE_PATH = "items/";
    private static final String DATABASE_PATH = "repitem";

    private EditText itemNameEditText, quantityEditText, priceEditText, valueEditText;
    private ImageView selectedImage;
    private Uri imageUri;
    private Spinner categorySpinner;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private AlertDialog uploadDialog;

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
        categorySpinner = findViewById(R.id.category_spinner);

        selectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    public void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_PICK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    selectedImage.setImageBitmap(bitmap);
                    selectedImage.setBackground(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addItemToFirebase(View view) {
        String itemName = itemNameEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String value = valueEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (itemName.isEmpty() || quantity.isEmpty() || price.isEmpty() || value.isEmpty() || category.isEmpty()) {
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
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                dismissUploadDialog();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    addItemToDatabase(itemName, quantity, price, value, category, downloadUri.toString(), userId);
                } else {
                    Toast.makeText(AddItem.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addItemToDatabase(String itemName, String quantity, String price, String value, String category, String imageUrl, String userId) {
        String itemId = databaseReference.child(DATABASE_PATH).push().getKey();
        if (itemId != null) {
            Item item = new Item(itemId, imageUrl, itemName, price, quantity, userId, value, category, false);

            databaseReference.child(DATABASE_PATH).child(userId).child(itemId).setValue(item)
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

    private void showUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uploading Item")
                .setMessage("Please wait while the item is being uploaded...")
                .setCancelable(false);
        uploadDialog = builder.create();
        uploadDialog.show();
    }

    private void dismissUploadDialog() {
        if (uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
    }

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

    private void clearFields() {
        itemNameEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        valueEditText.setText("");
        selectedImage.setImageResource(R.drawable.placeholder);
        imageUri = null;
        categorySpinner.setSelection(0);
    }
}
