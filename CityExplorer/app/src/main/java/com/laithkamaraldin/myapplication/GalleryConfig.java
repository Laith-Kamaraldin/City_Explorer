package com.laithkamaraldin.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
//A tutorial was followed and modified when making this class by braces media on youtube more
//found in the readme
public class GalleryConfig extends RecyclerView.Adapter<GalleryConfig.PictureViewHolder> {
    private Context galleryContext;
    private List<pictureUpload> galleryUploads;

    public GalleryConfig(Context context, List<pictureUpload> uploads){
        galleryContext = context;
        galleryUploads = uploads;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(galleryContext).inflate(R.layout.activity_gallery_component, parent,false);
        //instantiates the contents of the XML file into the the view object, pretty much build a
        //view of the component
        return  new PictureViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        pictureUpload currentImg=galleryUploads.get(position);
        //sets the current image as the picture at index from the array
        holder.picture_description.setText(currentImg.getImgName());
        //setting up the the text to be held in the java holder whilst its modified to -
        //be used later
        Picasso.get()
                .load(currentImg.getImgUrl())
                .placeholder(R.drawable.preview_photo)
                .fit()
                .centerCrop()
                .into(holder.picture_view);
        //using Picaso library to load and setup pictures into their frames
    }

    @Override
    public int getItemCount() {
        return galleryUploads.size();
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        public  TextView picture_description;
        public  ImageView picture_view;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            picture_description = itemView.findViewById(R.id.picture_description);
            picture_view = itemView.findViewById(R.id.picture_view);
        }
    }
}
