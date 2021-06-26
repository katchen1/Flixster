package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    // Constants for passing variables between intents
    public static final String KEY_GENRE_FILTER = "genres";
    public static final String KEY_ALL_MOVIES = "allMovies";
    public static final String KEY_MOVIES = "movies";
    public static final String KEY_SELECTED_GENRES = "selectedGenres";
    public static final int GENRE_FILTER_CODE = 1;

    public static final String TAG = "MainActivity"; // for printing to logcat
    public static Map<Integer, String> genreIdToString = new HashMap<>(); // maps genre's id to name
    List<Movie> allMovies; // all movies that are now playing
    List<Movie> movies; // displayed movies with genre filtered
    List<Integer> selectedGenres; // only display the movies with these genres
    SwitchCompat switchSortRating; // toggle to sort the movies by rating in decreasing order
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view (apply the view binding library to reduce view boilerplate)
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchSortRating = binding.switchSortRating;

        // Used the saved instance if onCreate is called as a result of orientation change
        if (savedInstanceState == null) {
            allMovies = new ArrayList<>();
            movies = new ArrayList<>();
            selectedGenres = new ArrayList<>();
            fetchGenres(); // retrieve the genre id and names

            // Make a GET request to retrieve the now playing movies from the online database
            String key = getString(R.string.moviedb_api_key);
            String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + key;
            new AsyncHttpClient().get(url, new JsonHttpResponseHandler() {

                /* Handles what happens when the request succeeds (error code 200). */
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        // Populate the movies member variable with the retrieved results
                        JSONArray results = jsonObject.getJSONArray("results");
                        allMovies.addAll(Movie.fromJsonArray(results));
                        movies.addAll(Movie.fromJsonArray(results));

                        // Notify the adapter that the dataset has changed
                        movieAdapter.notifyDataSetChanged();
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
        } else {
            // Restore member variables from the saved instance
            allMovies = Parcels.unwrap(savedInstanceState.getParcelable(KEY_ALL_MOVIES));
            movies = Parcels.unwrap(savedInstanceState.getParcelable(KEY_MOVIES));
            selectedGenres = savedInstanceState.getIntegerArrayList(KEY_SELECTED_GENRES);
        }

        // Create the adapter
        movieAdapter = new MovieAdapter(this, movies);

        // Set the adapter on the recycler view
        binding.rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
    }

    /* Sets the mapping from genre ids to names, called when the app starts. */
    public void fetchGenres() {
        String url =  "https://api.themoviedb.org/3/genre/movie/list?api_key="
                + getString(R.string.moviedb_api_key);

        // Make a GET request to retrieve trailer videos of the movie
        new AsyncHttpClient().get(url, new JsonHttpResponseHandler() {

            /* Handles what happens when the request succeeds (error code 200). */
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    // Fill in the map
                    JSONArray genres = json.jsonObject.getJSONArray("genres");
                    for (int j = 0; j < genres.length(); j++) {
                        JSONObject obj = genres.getJSONObject(j);
                        genreIdToString.put(obj.getInt("id"), obj.getString("name"));
                    }

                    // Select all genres on initialization
                    selectedGenres = new ArrayList<>(genreIdToString.keySet());
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

    /* When the genre filter button is clicked, create an intent for the genre filter activity
     * and pass in the currently selected genres. */
    public void genreFilterOnClick(View view) {
        Intent i = new Intent(MainActivity.this, GenreFilterActivity.class);
        i.putIntegerArrayListExtra(KEY_GENRE_FILTER, (ArrayList<Integer>) selectedGenres);
        startActivityForResult(i, GENRE_FILTER_CODE);
    }

    /* Handles the result of the genre filter activity. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GENRE_FILTER_CODE) {
            // Retrieve the edited genre selections
            selectedGenres = data.getIntegerArrayListExtra("genres");

            // Filter the movies to display only those with the selected genres
            movies.clear();
            for (Movie m: allMovies) {
                if (!Collections.disjoint(m.getGenreIds(), selectedGenres)) movies.add(m);
            }

            // Notify the adapter that the dataset has changed
            movieAdapter.notifyDataSetChanged();
            sortRatingOnClick(switchSortRating);
            Toast.makeText(getApplicationContext(), "Genres updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "Unknown call to onActivityResult");
        }
    }

    /* When the sort by rating switch is toggled, sort the currently displayed movies. */
    public void sortRatingOnClick(View view) {
        boolean checked = switchSortRating.isChecked();
        if (checked) {
            // Toggle on: sort by rating
            movies.sort((m1, m2) -> -1 * m1.getVoteAverage().compareTo(m2.getVoteAverage()));
        } else {
            // Toggle off: keep original order in which the movies are fetched from the database
            movies.sort((m1, m2) -> allMovies.indexOf(m1) - allMovies.indexOf(m2));
        }

        // Notify the adapter that the dataset has changed
        movieAdapter.notifyDataSetChanged();
    }

    /* Saves the states of the current activity to persist data across orientation changes. */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ALL_MOVIES, Parcels.wrap(allMovies));
        outState.putParcelable(KEY_MOVIES, Parcels.wrap(movies));
        outState.putIntegerArrayList(KEY_SELECTED_GENRES, (ArrayList<Integer>) selectedGenres);
    }
}