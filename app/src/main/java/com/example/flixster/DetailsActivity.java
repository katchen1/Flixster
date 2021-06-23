package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    ImageView ivPoster;
    RatingBar ratingBar;
    TextView tvTitle;
    TextView tvOverview;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setTitle("Movie Details");

        // Get the components ot the activity
        ivPoster = findViewById(R.id.ivPoster);
        ratingBar = findViewById(R.id.ratingBar);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);

        // Fill in the movie's details
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        ratingBar.setRating(movie.getVoteAverage().floatValue() / 2.0f);
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // If device is in landscape, display backdrop image
        Context context = getApplicationContext();
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
}