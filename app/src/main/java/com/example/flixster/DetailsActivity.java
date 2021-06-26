package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityDetailsBinding;
import com.example.flixster.models.Movie;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import okhttp3.Headers;

public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = "DetailsActivity"; // for printing to logcat
    Movie movie; // the movie we are interested in displaying
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view (apply the view binding library to reduce view boilerplate)
        ActivityDetailsBinding binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Movie Details");
        context = getApplicationContext();

        // Fill in the movie's details
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Movie.displayMoviePoster(movie, context, binding.ivPoster);
        binding.chipAdult.setText(movie.getAdult()? "R": "PG");
        binding.ratingBar.setRating(movie.getVoteAverage().floatValue() / 2.0f);
        binding.tvOverview.setText(movie.getOverview());
        binding.tvReleaseDate.setText(movie.getReleaseDate());
        binding.tvTitle.setText(movie.getTitle());
        binding.tvVoteAverage.setText(movie.getVoteAverage().toString());
        binding.tvVoteInfo.setText("/10 (" + movie.getVoteCount().toString() + ")");

        // Fill in genres chip group
        ChipGroup cgGenres = binding.cgGenres;
        for (Integer id: movie.getGenreIds()) {
            Chip chip = new Chip(this);
            chip.setText(MainActivity.genreIdToString.get(id));
            chip.setChipMinHeight(50);
            chip.setChipBackgroundColorResource(R.color.light_gray);
            chip.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
            cgGenres.addView(chip);
        }
    }

    /* Launches the trailer video when the poster or backdrop image is tapped.
     * Shows a toast if there are no trailer videos for the movie. */
    public void trailerOnClick(View view) {
        String url =  "https://api.themoviedb.org/3/movie/" + movie.getId()
                + "/videos?api_key=" + getString(R.string.moviedb_api_key);

        // Make a GET request to retrieve trailer videos of the movie
        new AsyncHttpClient().get(url, new JsonHttpResponseHandler() {

            /* Handles what happens when the request succeeds (error code 200). */
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    String trailerId = "";

                    // Consider the first video from YouTube
                    for (int j = 0; j < results.length(); j++) {
                        String site = results.getJSONObject(j).getString("site");
                        if (site.equals("YouTube")) {
                            trailerId = results.getJSONObject(j).getString("key");
                            break;
                        }
                    }

                    // If there's a YouTube trailer, launch an intent to play it
                    if (!trailerId.isEmpty()) {
                        Intent intent = new Intent(context, MovieTrailerActivity.class);
                        intent.putExtra("trailerId", trailerId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "No YouTube trailer available", Toast.LENGTH_SHORT).show();
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