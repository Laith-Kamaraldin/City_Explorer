package com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses;

import com.google.android.gms.maps.model.LatLng;

public class PlaceHelperClass {
    private String title;
    private String address;
    private LatLng latLng;

    public PlaceHelperClass(String title, String address, LatLng latLng) {
        this.title = title;
        this.address = address;
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }
    public String getAddress() {
        return address;
    }
    public LatLng getLatLng() {
        return latLng;
    }
}
