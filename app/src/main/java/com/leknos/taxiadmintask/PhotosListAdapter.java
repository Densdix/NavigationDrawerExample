package com.leknos.taxiadmintask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leknos.taxiadmintask.model.Photo;
import com.leknos.taxiadmintask.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.PhotosListViewHolder> {
    private int numberItems;
    private ArrayList<Photo> photos;
    private CircleTransform circleTransform;

    public PhotosListAdapter(ArrayList<Photo> photos) {
        this.numberItems = photos.size();
        this.photos = photos;
        circleTransform = new CircleTransform();
    }

    @NonNull
    @Override
    public PhotosListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.photo_list_item, parent, false);
        PhotosListViewHolder photosListViewHolder = new PhotosListViewHolder(view);
        return photosListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosListViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class PhotosListViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private ImageView image;

        public PhotosListViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.photo_list_item__image_title);
            description = itemView.findViewById(R.id.photo_list_item__image_description);
            image = itemView.findViewById(R.id.photo_list_item__image);
        }

        void bind(int listIndex){
            title.setText("List item title #"+String.valueOf(photos.get(listIndex).getId()));
            description.setText(photos.get(listIndex).getTitle());
            Picasso.get()
                    .load(photos.get(listIndex).getThumbnailUrl())
                    .transform(circleTransform)
                    .placeholder(R.drawable.placeholder)
                    .into(image);
        }
    }
}
