package com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses;

import android.graphics.drawable.Drawable;

public class CategoriesHelperClass {

    Drawable gradient;
    int image;
    String titile;
    String type;

    public CategoriesHelperClass(Drawable gradient, int image, String titile) {
        this.gradient = gradient;
        this.image = image;
        this.titile = titile;
    }

    public CategoriesHelperClass(Drawable gradient, int image, String titile, String type) {
        this.gradient = gradient;
        this.image = image;
        this.titile = titile;
        this.type = type;
    }


    public Drawable getGradient() {
        return gradient;
    }

    public int getImage() {
        return image;
    }

    public String getTitile() {
        return titile;
    }

    public String getType() { return type; }
}
