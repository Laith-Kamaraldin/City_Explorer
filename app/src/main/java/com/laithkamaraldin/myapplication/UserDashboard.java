package com.laithkamaraldin.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.laithkamaraldin.myapplication.HomeAdapters.CategoriesAdapter;
import com.laithkamaraldin.myapplication.HomeAdapters.FeaturedAdapter;
import com.laithkamaraldin.myapplication.HomeAdapters.MostViewedAdpater;
import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.CategoriesHelperClass;
import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.FeaturedHelperClass;
import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.MostViewedHelperClass;

//A tutorial was followed and modified when making this class on youtube more details
//found in the readme

import java.util.ArrayList;
//parts of this code have been adapted from an online tutorial more info found in the readme
public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //variables to be used in the
    static final float END_SCALE = 0.7f;
    private static final String TAG = "GoogleMapHelperActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageButton btnMap;
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;

    //drawer burger menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);
        //Hooking the XML elements to the java activity
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.most_viewed_recycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        btnMap  =  findViewById(R.id.map);

        //hooks for the drawer menu
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationDrawer();
        //calling the menu on page create

        //Functions called are for the different scroll view cards
        featuredRecycler();
        mostViewedRecycler();
        categoriesRecycler();

        if(isServicesOK()){
            init();
            //calling the init function that opens the map view if services are ok
        }


    }

    private void init(){

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            //linking the map button
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboard.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserDashboard.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and log request can be made
            Log.d(TAG, "isServicesOK: Google Play Services is Working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error has occured but it can be fixed
            Log.d(TAG, "isServicesOK: an error occured but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserDashboard.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make any map requests", Toast.LENGTH_SHORT).show();
        }

        return false;

    }


    private void navigationDrawer() {
        navigationView.bringToFront();
        //getting the menu to appear on top of all elements
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        //hooking it to checkable XML element

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
            //onclick listener for when menu button is clicked
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // setting up the scale of the menu when opened
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // setting up the scale of the menu based on the width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
    //condition to close the navigation drawer when back button is pressed

    private void categoriesRecycler() {

        //swapping 0xff for # from hex colour codes to be java compatible
        //adding colours to the scrollable category cards
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff65B9E5, 0xff65B9E5});
        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffFAD347, 0xffFAD347});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff8ADC7D, 0xff8ADC7D});
        gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffE99469, 0xffE99469});

        //setting up the categories cards using the helper class that takes gradient, title, description and image as arguments
        ArrayList<CategoriesHelperClass> categoriesHelperClasses = new ArrayList<>();
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.llandmarks, "Landmarks", "TOURIST_ATTRACTION"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2, R.drawable.restaurant_image, "Restaurants", "RESTAURANT"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3, R.drawable.parkk, "Parks", "PARK"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient4, R.drawable.shops, "Shopping", "SHOPPING_MALL"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.coffeecup, "Cafes", "CAFE"));

        //fixing the size of the cards so they don't change for different screens
        categoriesRecycler.setHasFixedSize(true);
        adapter = new CategoriesAdapter(categoriesHelperClasses);
        //creating a new instance of the Categories Helper Class found in the home adapters folder
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesRecycler.setAdapter(adapter);

    }

    private void mostViewedRecycler() {

        mostViewedRecycler.setHasFixedSize(true);
        mostViewedRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<MostViewedHelperClass> mostViewedLocations = new ArrayList<>();
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.skygarden, "Sky Garden", "Known as the walkie talkie due to its unique shape, this skyscraper features a top-floor restaurant.", 51.5109, -0.0837));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.bpalace, "Buckingham Palace", "DescriptionBuckingham Palace is the London residence and administrative headquarters of the monarchy of the United Kingdom. Located in the City of Westminster, the palace is often at the centre of state occasions and royal hospitality.", 51.501400, -0.141900));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.ngallery, "National Gallery", "The National Gallery is an art museum in Trafalgar Square in the City of Westminster, in Central London. Founded in 1824", 51.5089, -0.1283));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.oxfordstreet, "Oxford Street", "Oxford Street is a major road in the City of Westminster in the West End of London, running from Tottenham Court Road to Marble Arch via Oxford Circus. It is Europe's busiest shopping street, with around half a million daily visitors, and as of 2012 had approximately 300 shops.", 51.515419, -0.141099));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.theshard, "The Shard", "The Shard, also referred to as the Shard of Glass, Shard London Bridge and formerly London Bridge Tower, is a 95-storey supertall skyscraper, designed by the Italian architect Renzo Piano, in Southwark, London, that forms part of the Shard Quarter development", 51.5045, -0.0865));
        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.hydepark, "Hyde Park", "Hyde Park is a Grade I-listed major park in Central London. It is the largest of four Royal Parks that form a chain from the entrance of Kensington Palace through Kensington Gardens and Hyde Park, via Hyde Park Corner and Green Park past the main entrance to Buckingham Palace", 51.5073, -0.1657));

        adapter = new MostViewedAdpater(mostViewedLocations);
        mostViewedRecycler.setAdapter(adapter);

    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();
        featuredLocations.add(new FeaturedHelperClass(R.drawable.bigben, "Big Ben", "Big Ben is the nickname for the Great Bell of the striking clock at the north end of the Palace of Westminster in London and is usually extended to refer to both the clock and the clock tower.", 51.5007,  -0.1246));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.londoneye, "London Eye", "The London Eye, or the Millennium Wheel, is a cantilevered observation wheel on the South Bank of the River Thames in London.", 51.5033, -0.1195));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.arena, "The O2", "Arena hosting concerts from the world's top stars and elite sporting events for up to 20,000 fans.", 51.5030, 0.0032));
        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_all_categories:
                Intent intent = new Intent(getApplicationContext(), CategoryView.class);
                startActivity(intent);
                break;


        }

        switch (menuItem.getItemId()){
            case R.id.nav_profile:
                Intent applicationIntent = getIntent();
                String username = applicationIntent.getStringExtra("username");
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
        }

        switch (menuItem.getItemId()){
            case R.id.map:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                break;
        }

        switch (menuItem.getItemId()){
            case R.id.galleryView:
                Intent intent = new Intent(getApplicationContext(), Gallery.class);
                startActivity(intent);
                break;
        }

        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
