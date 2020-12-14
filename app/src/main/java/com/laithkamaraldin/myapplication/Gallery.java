package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//A tutorial was followed and modified when making this class by braces media on youtube more details
//found in the readme
public class Gallery extends AppCompatActivity {

    private RecyclerView photoRecycler;
    private GalleryConfig GalleryConfig;
    private Button Capture, Upload;
    private ProgressBar progressBar;
    private DatabaseReference firebaseDatabaseRef;
    private List<pictureUpload> picUploads;
    //referencing all the different XML elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //onCreate Function that generates objects when page is initiated
        setContentView(R.layout.activity_gallery);
        progressBar=findViewById(R.id.progress_circular);
        Capture = findViewById(R.id.camera_upload_btn);
        Upload = findViewById(R.id.picture_upload_btn);
        photoRecycler=findViewById(R.id.recycler_view);
        photoRecycler.setHasFixedSize(true);
        photoRecycler.setLayoutManager(new LinearLayoutManager(this));
        picUploads=new ArrayList<>();
        //hooking XML objects and generating instances

        firebaseDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        //Generates a reference of where the photos are stored in the database
        firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
            //adds a listener that checks for changes in this location
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                //looping over the data inside the database
                {
                    pictureUpload upload=postSnapshot.getValue(pictureUpload.class);
                    picUploads.add(upload);
                    //adding the data values found to array picUploads
                }
                GalleryConfig=new GalleryConfig(Gallery.this, picUploads);
                //generating an instance of the GalleryConfig class
                photoRecycler.setAdapter(GalleryConfig);
                //associating GalleryConfig with the photoRecycler listview using .setAdapter
                progressBar.setVisibility(View.INVISIBLE);
                //resetting the progress bar

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Gallery.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
            //Case for when upload is cancelled, toast is made with error message sent from firebase
        });
    }

    public void captureImage(View view) {
        Intent intent = new Intent(Gallery.this, Capture_upload.class);
        startActivity(intent);
        //linking the capture image page
    }

    public void uploadImage(View view) {
        Intent intent = new Intent(Gallery.this, GalleryUpload.class);
        startActivity(intent);
        //linking the upload image page
    }
}
