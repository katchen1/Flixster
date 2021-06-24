package com.example.flixster.adapters;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.flixster.DetailsActivity;
import com.example.flixster.R;
import com.example.flixster.databinding.ItemMovieBinding;
import com.example.flixster.models.Movie;
import org.parceler.Parcels;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/* Responsible for displaying data from the model into a row in the recycler view. */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    /* Constructor for the MovieAdapter class. */
    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    /* Inflates a layout from XML and returns the holder. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    /* Populates data into the item through the holder. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder " + position);
        Movie movie = movies.get(position); // get movie at the passed in position
        holder.bind(movie); // bind movie data into the ViewHolder
    }

    /* Returns the total count of items in the list. */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /* Container to provide easy access to views that represent each row of the list. */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        /* Constructor for the ViewHolder class. */
        public ViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            tvTitle = binding.tvTitle;
            tvOverview = binding.tvOverview;
            ivPoster = binding.ivPoster;
            itemView.setOnClickListener(this);
        }

        /* Updates what's inside the ViewHolder with the movie's data. */
        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // If device is in landscape, display backdrop image, otherwise display poster image
            boolean landscape = context.getResources()
                    .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            String imageUrl = landscape? movie.getBackdropPath() : movie.getPosterPath();
            int placeHolder = landscape?
                    R.drawable.flicks_backdrop_placeholder : R.drawable.flicks_movie_placeholder;
            Glide.with(context)
                    .load(imageUrl)
                    .transform(new RoundedCornersTransformation(30, 10))
                    .placeholder(placeHolder)
                    .into(ivPoster);
        }

        /* Displays a new activity via an Intent when the user clicks on a row. */
        @Override
        public void onClick(View v) {
            // Get the item's position
            int position = getAdapterPosition();

            // Ensure that the position is valid
            if (position != RecyclerView.NO_POSITION) {

                // Get the movie at the position
                Movie movie = movies.get(position);

                // Create an intent for the new activity
                Intent intent = new Intent(context, DetailsActivity.class);

                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // Show the activity
                context.startActivity(intent);
            }
        }
    }
}
