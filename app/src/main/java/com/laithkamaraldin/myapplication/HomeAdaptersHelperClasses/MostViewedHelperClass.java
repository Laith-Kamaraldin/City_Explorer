package com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses;

public class MostViewedHelperClass {

    int imageView;
    String textView,ddescription;
    double latitude, longitude;


    public MostViewedHelperClass(int imageView, String textView, String ddescription) {
        this.imageView = imageView;
        this.textView = textView;
        this.ddescription = ddescription;
    }

    public MostViewedHelperClass(int imageView, String textView, String ddescription, double latitude, double longitude) {
        this.imageView = imageView;
        this.textView = textView;
        this.ddescription = ddescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getImageView() {
        return imageView;
    }

    public String getDdescription() {
        return ddescription;
    }

    public String getTextView() {
        return textView;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

}
