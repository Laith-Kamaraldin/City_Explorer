package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Capture_upload extends AppCompatActivity {

    private Button captureUploadBtn, uploadButton;
    private ImageView uploadPreview;
    private TextInputLayout imgDescription;
    private ProgressBar uploadProgress;
    private Uri imgUrl;
    private StorageReference firebaseStorageRef;
    private DatabaseReference firebaseDatabaseRef;
    private StorageTask firebaseUploadTask;
    private static final int CAMERA_REQUEST_CODE = 1;
    private ProgressDialog mProgress;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        uploadProgress = findViewById(R.id.uploadProgress);
        captureUploadBtn = findViewById(R.id.capture_btn);
        imgDescription = findViewById(R.id.img_description);
        uploadPreview = findViewById(R.id.upload_preview);
        uploadButton = findViewById(R.id.upload_btn);
        firebaseStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUploadTask != null && firebaseUploadTask.isInProgress()) {
                    Toast.makeText(Capture_upload.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else {
                    uploadImage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Uploading Image ...");
            mProgress.show();

            // TESTING
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                uploadPreview.setImageBitmap(myBitmap);
            }
            mProgress.dismiss();
        }
    }

    public void captureImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i("Successful", "createdImageFile");
            } catch (IOException ex) {
                Log.e("Error", "createImageFile()");
            }

            if (photoFile != null) {
                Log.i("photoFile", "IS NOT NULL");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.laithkamaraldin.myapplication.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                imgUrl = photoURI;
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void uploadImage() {
        if (imgUrl != null) {
            StorageReference riversRef = firebaseStorageRef.child(imgUrl.getLastPathSegment());

            firebaseUploadTask = riversRef.putFile(imgUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pictureUpload upload = new pictureUpload(imgDescription.getEditText().getText().toString().trim(), uri.toString());

                                    String uploadID = firebaseDatabaseRef.push().getKey();
                                    firebaseDatabaseRef.child(uploadID).setValue(upload);
                                    uploadPreview.setImageResource(R.drawable.preview_photo);
                                    imgDescription.getEditText().setText("");
                                    Toast.makeText(Capture_upload.this, "Upload successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Capture_upload.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
