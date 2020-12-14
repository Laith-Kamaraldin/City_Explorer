package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.laithkamaraldin.myapplication.Modules.DirectionFinder;
import com.laithkamaraldin.myapplication.Modules.Route;
import com.laithkamaraldin.myapplication.Modules.DirectionFinderListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//A tutorial was followed and modified when making this class on youtube more details
//found in the readme
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener{

    private GoogleMap map;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1111;
    private static final String GOOGLE_API_KEY = "AIzaSyBHsVBODzKlsCvkjyQQlTog5SLIBmuEprk";
    private EditText SearchBar;
    private ImageView UserLocation;
    private Button btnFindPath, btnsearch;
    private FusedLocationProviderClient locationClient;
    private Bundle extras;
    private double latitude, longitude;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String destination;
    private Object View;
    //referencing all the different XML elements and setting up variables for use

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        UserLocation = (ImageView) findViewById(R.id.user_location);
        SearchBar = (EditText) findViewById(R.id.search_bar);
        btnsearch = (Button) findViewById(R.id.btnSearch);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        destination = new String();
        Places.initialize(getApplicationContext(), GOOGLE_API_KEY);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //hooking XML objects and generating instances
        mapFragment.getMapAsync(this);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        final LatLng latLng = place.getLatLng();
                        destination = place.getName();
                        Toast.makeText(MapsActivity.this, "" + latLng.latitude, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(Status status) {
                        Toast.makeText(MapsActivity.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //google autocomplete setup taken from the google autocomplete documentation.

        extras = getIntent().getExtras();
        //setting up extras to send data
        if (extras != null) {
            latitude = extras.getDouble("LATITUDE_ID");
            longitude = extras.getDouble("LONGITUDE_ID");
        } else {
            setLocation();
        }

        getLocationPermission();
        //setting location permissions
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting up onclick listener for find path button to call sendRequest() function
                sendRequest();
            }
        });
    }

    private void sendRequest(){

        if (destination.isEmpty()) {
            // Check to see if the user has entered a location to route to
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder((DirectionFinderListener) this, getOrigin(), destination).execute();
            //retrieving directions by passing in getOrigin and destination name into direction finder.
            // getOrigin passes latitude and longitude into direction finder class along with
            //direction name string which are the three parameters required by google directions.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void searchFunction() {

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
                //adding action to the search location button
            }
        });


        UserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                //gets the users current location when the button is pressed
            }
        });

    }

    private void searchLocation() {
        Geocoder geoLocation = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geoLocation.getFromLocationName(destination, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            camera(new LatLng(address.getLatitude(), address.getLongitude()), 15F, address.getAddressLine(0));
        }
    }

    public void getLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task location = locationClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (extras != null) {
                    //checks if there one of the featured or popular location cards has been clicked
                    //by the user if it has been the camera is moved to that location
                    //by passing in the latitude, longitude, zoom and location title from the extras
                    //passed by the parcelable data sent from the userDashboard Activity
                    camera(new LatLng(latitude, longitude), 15f, extras.getString("TITLE"));
                    map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(extras.getString("TITLE")));
                } else {
                    //if no card was selected and the map is opened normally the camera loads
                    //ontop of the the users current location by using the devices GPS as
                    //permissions where given.
                    Location currentLocation = (Location) task.getResult();
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    camera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f, "Current Location");
                    //passing in the required variables to move the camera ontop of the current user location.
                }
            }
        });
    }

    public void setLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = locationClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (extras == null) {
                    Location currentLocation = (Location) task.getResult();
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                }
            }
        });
    }

    public String getOrigin(){
        String origin = String.format("%.3f,%.3f", latitude, longitude);
        return origin;
    }

    private void camera(LatLng latLng, float zoom, String title){
        //function for moving the camera
        //moves the camera to current location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //if the title doesn't equal current location a new condition is invoked
        if(!title.equals("Current Location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            map.addMarker(options);
            //moves camera to the called location for example featured location card
            //and drops a marker on the destination
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //options for when the map is ready and loaded
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json);
        googleMap.setMapStyle(style);
        //applying a style to the map using a json file taken from the google documentation

        map = googleMap;
        // assigning the google map to a variable

        if (mLocationPermissionsGranted) {
            getLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //getting permissions to access the users device location.
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            //Turning of the default get location button in order to design my own.
            searchFunction();
            //invoking the location search function so its ready on map load.
        }

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        //function for adding markers and polylines for the directions function
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
        //resizing function to be used on the the logo so it can be used as a marker
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        //generating array lists to be used for the direction features

        for (Route route : routes) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            //updating the distance and durations texts to match the distance required to meet the location that
            //path is being found for

            destinationMarkers.add(map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("bgg",100,100)))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            //adding a marker at the destination using the city explorer using the resize function made earlier

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    width(10);
            //setting up the polyline by adding its width no colour was set so it will be the default colour
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            //looping through the routes points in order to plot the polyline
            polylinePaths.add(map.addPolyline(polylineOptions));
        }
    }

    private void initMap(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        //calling async map fragment so that it loads in background and doesnt cause threading issues
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        //required location permissions in order to access the devices gps
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        //if conditions are met init map is invoked
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize the map if permissions are granted
                    initMap();
                }
            }
        }
    }



}
