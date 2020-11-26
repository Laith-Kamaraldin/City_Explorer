package com.laithkamaraldin.myapplication;
//A tutorial was followed and modified when making this class by braces media on youtube more
//found in the readme
public class pictureUpload {
    private String imgName;
    private String imgUrl;
    public pictureUpload() {}
    public pictureUpload(String imgName, String imgUrl) {
        //picture upload constructor
        if(imgName.trim().equals(""))  {
            imgName="No name";}
        this.imgName = imgName;
        this.imgUrl = imgUrl;}
    public String getImgName() {
        return imgName;
    }
    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    //class used with GalleryUpload to help prepare images for uploading
}
