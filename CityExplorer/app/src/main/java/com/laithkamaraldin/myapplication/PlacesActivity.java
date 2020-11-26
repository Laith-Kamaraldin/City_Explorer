package com.laithkamaraldin.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.laithkamaraldin.myapplication.HomeAdapters.PlacesAdapter;
import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.PlaceHelperClass;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PlacesActivity extends AppCompatActivity {
    private Bundle extras;
    private String type;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String TAG = "PlaceAPI";
    private List<PlaceHelperClass> myDataset;
    //setting up variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        myDataset = new ArrayList<>();

        // RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        recyclerView.setHasFixedSize(true);
        //setting up the xml recycler view
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("TYPE");
        } else {
            type = "RESTAURANT";
        }
        //if no data is passed assume type is restaurant
        String apiKey = getString(R.string.google_maps_key);
        //api key
        if (apiKey.equals("")) {
            Log.i(TAG, "No apiKey");
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Selecting fields to display from places API documentation
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES);
        //selected fields
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        //creating new place display instance
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            //getting permissions to access client location
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        List<Place.Type> placeTypes = placeLikelihood.getPlace().getTypes();
                        for (Place.Type placeType : placeTypes) {
                            if (placeType.toString().equals(type)) {
                                String title = placeLikelihood.getPlace().getName();
                                String address = placeLikelihood.getPlace().getAddress();
                                LatLng latLng = placeLikelihood.getPlace().getLatLng();
                                myDataset.add(new PlaceHelperClass(title, address, latLng));
                                //looping through the places that match the selected category and
                                //passing the data using a loop
                            }
                        }
                    }

                    mAdapter = new PlacesAdapter(myDataset);
                    recyclerView.setAdapter(mAdapter);
                    //updating the recycler view
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Toast.makeText(this, "Place not found: " + apiException.getStatusCode(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        //else updating user with error
                    }
                }
            });
        } else {
            Log.i(TAG, "getLocationPermission");
        }

    }
}