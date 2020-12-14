package com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses;

public class FeaturedHelperClass {

    int image;
    String title, description;
    double latitude, longitude;

    public FeaturedHelperClass(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public FeaturedHelperClass(int image, String title, String description, double latitude, double longitude) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getImage() {
        return image;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}