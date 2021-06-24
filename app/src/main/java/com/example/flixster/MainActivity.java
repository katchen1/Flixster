package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; // for printing to logcat
    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movies = new ArrayList<>();

        // Get the recycler view from the activity
        RecyclerView rvMovies = binding.rvMovies;

        // Create the adapter
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        // Make a GET request to retrieve the now playing movies from the online database
        AsyncHttpClient client = new AsyncHttpClient();
        String key = getString(R.string.moviedb_api_key);
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + key;
        client.get(url, new JsonHttpResponseHandler() {
            /* Handles what happens when the request succeeds (error code 200). */
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    // Populate the movies member variable with the retrieved results
                    JSONArray results = jsonObject.getJSONArray("results");
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
    }
}