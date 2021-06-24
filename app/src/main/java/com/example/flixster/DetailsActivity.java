package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityDetailsBinding;
import com.example.flixster.models.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = "DetailsActivity"; // for printing to logcat
    ImageView ivPoster;
    RatingBar ratingBar;
    TextView tvTitle;
    TextView tvOverview;
    Movie movie;
    ImageView playButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setTitle("Movie Details");
        context = getApplicationContext();

        // Get the components of the activity
        ivPoster = binding.ivPoster;
        ratingBar = binding.ratingBar;
        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        playButton = binding.playButton;

        // Fill in the movie's details
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        ratingBar.setRating(movie.getVoteAverage().floatValue() / 2.0f);
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

    /* Launches the trailer video when the poster or backdrop image is tapped.
     * Shows a toast if there are no trailer videos for the movie. */
    public void trailerOnClick(View view) {
        String movieId = movie.getId().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url =  "https://api.themoviedb.org/3/movie/" + movieId
                + "/videos?api_key=" + getString(R.string.moviedb_api_key);

        // Make a GET request to retrieve trailer videos of the movie
        client.get(url, new JsonHttpResponseHandler() {

            /* Handles what happens when the request succeeds (error code 200). */
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    String trailerId = "";

                    // Filter: only consider videos from YouTube
                    for (int j = 0; j < results.length(); j++) {
                        String site = results.getJSONObject(j).getString("site");
                        if (site.equals("YouTube")) {
                            trailerId = results.getJSONObject(j).getString("key");
                        }
                    }

                    // Ignore taps for movies with no YouTube trailer videos
                    if (trailerId.isEmpty()) {
                        Toast.makeText(context, "No YouTube trailer available", Toast.LENGTH_SHORT).show();
                    } else {
                        // Get the first YouTube video in the list and launch an intent to play it
                        trailerId = results.getJSONObject(0).getString("key");
                        Intent intent = new Intent(context, MovieTrailerActivity.class);
                        intent.putExtra("trailerId", trailerId);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            /* Handles what happens when the request fails (error code usually 4XX). */
            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}