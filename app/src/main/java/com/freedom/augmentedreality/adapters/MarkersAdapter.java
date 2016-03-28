package com.freedom.augmentedreality.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.freedom.augmentedreality.app.ArApplication;
import com.freedom.augmentedreality.R;
import com.freedom.augmentedreality.app.AppConfig;
import com.freedom.augmentedreality.model.Marker;

import java.util.List;

/**
 * Created by hienbx94 on 3/21/16.
 */
public class MarkersAdapter extends RecyclerView.Adapter<MarkersAdapter.MyViewHolder> {

    private List<Marker> markersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, create_at;
        public NetworkImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_marker);
            create_at = (TextView) view.findViewById(R.id.txt_create_at);
            image = (NetworkImageView) view.findViewById(R.id.image_marker);
        }
    }


    public MarkersAdapter(List<Marker> markersList) {
        this.markersList = markersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Marker marker = markersList.get(position);
        holder.name.setText(marker.get_name());
        holder.create_at.setText(marker.getCreatedAt());
        String image_link = AppConfig.baseURL + marker.get_image();
        holder.image.setImageUrl(image_link, ArApplication.getInstance().getImageLoader());
    }

    @Override
    public int getItemCount() {
        return markersList.size();
    }
}
