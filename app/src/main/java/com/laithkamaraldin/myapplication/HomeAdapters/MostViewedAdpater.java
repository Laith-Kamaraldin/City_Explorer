package com.laithkamaraldin.myapplication.HomeAdapters;

import android.content.Intent;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.MostViewedHelperClass;
import com.laithkamaraldin.myapplication.MainActivity;
import com.laithkamaraldin.myapplication.MapsActivity;
import com.laithkamaraldin.myapplication.R;

import java.util.ArrayList;

public class MostViewedAdpater extends RecyclerView.Adapter<MostViewedAdpater.MostViewedViewHolder> {

    ArrayList<MostViewedHelperClass> mostViewedLocations;

    public MostViewedAdpater(ArrayList<MostViewedHelperClass> mostViewedLocations) {
        this.mostViewedLocations = mostViewedLocations;
    }

    @NonNull
    @Override
    public MostViewedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_viewed_card_design, parent, false);
        MostViewedViewHolder mostViewedViewHolder = new MostViewedViewHolder(view);
        return mostViewedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MostViewedViewHolder holder, int position) {
        final MostViewedHelperClass helperClass = mostViewedLocations.get(position);

        holder.imageView.setImageResource(helperClass.getImageView());
        holder.textView.setText(helperClass.getTextView());
        holder.ddescription.setText(helperClass.getDdescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("LATITUDE_ID", helperClass.getLatitude());
                intent.putExtra("LONGITUDE_ID", helperClass.getLongitude());
                intent.putExtra("TITLE", helperClass.getTextView());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mostViewedLocations.size();
    }

    public static class MostViewedViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView, ddescription;


        public MostViewedViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.mv_image);
            textView = itemView.findViewById(R.id.mv_title);
            ddescription = itemView.findViewById(R.id.mv_desc);


        }
    }
}

