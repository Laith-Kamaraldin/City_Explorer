package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
//A tutorial was followed and modified when making this class by braces media on youtube more
//found in the readme
public class GalleryUpload extends AppCompatActivity {
    private static final int IMAGES_CHOOSEN = 1;
    private Button btnPictureSelect, btnPictureUpload, btnBackGallery;
    private ImageView uploadPreview;
    private TextInputLayout imgDescription;
    private ProgressBar uploadProgress;
    private Uri imgUrl;
    private StorageReference firebaseStorageRef;
    private DatabaseReference firebaseDatabaseRef;
    private StorageTask firebaseUploadTask;
    //referencing all the different XML elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        //linking above variables to their xml components
        uploadProgress = findViewById(R.id.uploadProgress);
        btnPictureSelect = findViewById(R.id.picture_select_btn);
        btnPictureUpload = findViewById(R.id.picture_upload_btn);
        btnBackGallery = findViewById(R.id.back_gallery);
        imgDescription = findViewById(R.id.img_description);
        uploadPreview = findViewById(R.id.upload_preview);
        firebaseStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        //hooking XML objects and generating instances

        btnBackGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryUpload.this, Gallery.class);
                startActivity(intent);
            }
        });
        btnPictureUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUploadTask != null && firebaseUploadTask.isInProgress()) {
                    Toast.makeText(GalleryUpload.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else {
                    uploadImage();
                    //starts the image upload
                }
            }
        });
        btnPictureSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImage();
            }
        });
        //adding different onClick listeners for all the buttons to start  their functions or activities
    }


    private void viewImage() {
        //intent allows for the  performing of an action, in this case opens gallery
        //to select picture
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGES_CHOOSEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGES_CHOOSEN && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUrl = data.getData();
            Picasso.get().load(imgUrl).into(uploadPreview);
        }
    }
    //uses picaso library to load selected image into the image preview.

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        //gets the file type of the selected file to be used in upload image class
        //singleton is a class that can have only one object
    }


    private void uploadImage() {
        if (imgUrl != null) {
            //checks when img url is loaded with data from view image function to allow for the upload
            //of image to start when btnPictureUpload is clicked
            final StorageReference fileReference = firebaseStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgUrl));
            firebaseUploadTask = fileReference.putFile(imgUrl)
            //using firebase upload guide from documentation to set up photo reference
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uploadProgress.setProgress(0);
                                }
                            }, 500);
                            //setting up the upload progress view on success
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //calling picture upload class found in full code
                                    pictureUpload upload = new pictureUpload(imgDescription.getEditText().getText().toString().trim(), uri.toString());
                                    String uploadID = firebaseDatabaseRef.push().getKey();
                                    //pushing the string photo description entered into the database
                                    firebaseDatabaseRef.child(uploadID).setValue(upload);
                                    Toast.makeText(GalleryUpload.this, "Image Uploaded successfully!", Toast.LENGTH_LONG).show();
                                    //Success notice
                                    uploadPreview.setImageResource(R.drawable.preview_photo);
                                    imgDescription.getEditText().setText("");
                                    //setting the image and description into view
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GalleryUpload.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            //show firebase error message on failure
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            uploadProgress.setProgress((int) progress);
                            //updating file upload progress
                        }
                    });
        } else {
            Toast.makeText(GalleryUpload.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    
    
    
}
