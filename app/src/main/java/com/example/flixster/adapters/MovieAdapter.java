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
import com.example.flixster.models.Movie;
import org.parceler.Parcels;
import java.util.List;

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
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
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
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            itemView.setOnClickListener(this);
        }

        /* Updates what's inside the ViewHolder with the movie's data. */
        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // If phone is in landscape, display backdrop image
            if (context.getResources()
                    .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Glide.with(context)
                        .load(movie.getBackdropPath())
                        .placeholder(R.drawable.flicks_backdrop_placeholder)
                        .into(ivPoster);
            }

            // Otherwise phone is in landscape, display poster image
            else {
                Glide.with(context)
                        .load(movie.getPosterPath())
                        .placeholder(R.drawable.flicks_movie_placeholder)
                        .into(ivPoster);
            }
        }

        /* Displays a new activity via an Intent when the user clicks on a row. */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // get item position
            if (position != RecyclerView.NO_POSITION) { // ensure position is valid
                Movie movie = movies.get(position); // get the movie at the position
                // Create an intent for the new activity
                Intent intent = new Intent(context, DetailsActivity.class);
                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                context.startActivity(intent); // show the activity
            }
        }
    }
}
