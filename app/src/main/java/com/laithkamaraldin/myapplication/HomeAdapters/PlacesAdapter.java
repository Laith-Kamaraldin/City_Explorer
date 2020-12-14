package com.laithkamaraldin.myapplication.HomeAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.laithkamaraldin.myapplication.HomeAdaptersHelperClasses.PlaceHelperClass;
import com.laithkamaraldin.myapplication.MapsActivity;
import com.laithkamaraldin.myapplication.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {
    private List<PlaceHelperClass> placeDataSet;
    private Context context;

    public PlacesAdapter(List<PlaceHelperClass> myDataset) {
        placeDataSet = myDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_card_list, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (placeDataSet != null) {
            PlaceHelperClass current = placeDataSet.get(position);
            holder.placeTitleView.setText(current.getTitle());
            holder.placeAddressView.setText(current.getAddress());

            holder.cardView.setOnClickListener((v) -> {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("LATITUDE_ID", current.getLatLng().latitude);
                intent.putExtra("LONGITUDE_ID", current.getLatLng().longitude);
                intent.putExtra("TITLE", current.getTitle());
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return placeDataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView placeTitleView;
        TextView placeAddressView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTitleView = itemView.findViewById(R.id.place_title);
            placeAddressView = itemView.findViewById(R.id.place_address);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
