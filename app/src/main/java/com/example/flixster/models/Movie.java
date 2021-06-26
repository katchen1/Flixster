package com.example.flixster.models;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.flixster.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

@Parcel
public class Movie {

    Boolean adult;
    String backdropPath;
    List<Integer> genreIds;
    Integer id;
    String overview;
    String posterPath;
    String releaseDate;
    String title;
    Double voteAverage;
    Double voteCount;

    /* Default constructor. */
    public Movie () {}

    /* Constructor takes in a JSON object and extracts information to populate member variables. */
    public Movie(JSONObject jsonObject) throws JSONException {
        adult = jsonObject.getBoolean("adult");
        backdropPath = jsonObject.getString("backdrop_path");
        id = jsonObject.getInt("id");
        overview = jsonObject.getString("overview");
        posterPath = jsonObject.getString("poster_path");
        releaseDate = jsonObject.getString("release_date");
        title = jsonObject.getString("title");
        voteAverage = jsonObject.getDouble("vote_average");
        voteCount = jsonObject.getDouble("vote_count");

        // Convert the genre ids from an JSONArray to a List<Integer>
        genreIds = new ArrayList<>();
        JSONArray jsonGenreIds = jsonObject.getJSONArray("genre_ids");
        for (int i = 0; i < jsonGenreIds.length(); i++) {
            genreIds.add(jsonGenreIds.getInt(i));
        }
    }

    /* Creates a movie list from a JSON movie array. */
    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    /* Displays movie poster in an image view. If device is in landscape, displays backdrop image,
     * otherwise displays poster image */
    public static void displayMoviePoster(Movie movie, Context context, ImageView ivPoster) {
        boolean landscape = context.getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        String imageUrl = landscape? movie.getBackdropPath() : movie.getPosterPath();
        int placeHolder = landscape?
                R.drawable.flicks_backdrop_placeholder : R.drawable.flicks_movie_placeholder;

        // Display with rounded corners and placeholder
        Glide.with(context)
                .load(imageUrl)
                .transform(new RoundedCornersTransformation(30, 10))
                .placeholder(placeHolder)
                .into(ivPoster);
    }

    /* Checks if the movie is the same as another by comparing their ids. */
    @Override
    public boolean equals(Object other) {
        return other.getClass() == Movie.class && id.equals(((Movie) other).getId());
    }

    /* Getter for the movie's content rating. */
    public Boolean getAdult() { return adult; }

    /* Getter for the movies' backdrop path. */
    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    /* Getter for the movie's genre ids. */
    public List<Integer> getGenreIds() { return genreIds; }

    /* Getter for the movie's id. */
    public Integer getId() { return id; }

    /* Getter for the movie's overview. */
    public String getOverview() { return overview; }

    /* Getter for the movies' poster path. */
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    /* Getter for the movie's release date. */
    public String getReleaseDate() {
        return releaseDate;
    }

    /* Getter for the movie's title. */
    public String getTitle() {
        return title;
    }

    /* Getter for the movie's vote average. */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /* Getter for the movie's vote count (converted to an Integer). */
    public Integer getVoteCount() {
        return voteCount.intValue();
    }
}
