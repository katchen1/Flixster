package com.example.flixster.models;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {

    String posterPath;
    String backdropPath;
    String title;
    String overview;
    Double voteAverage;
    Double voteCount;
    String releaseDate;
    Boolean adult;
    List<Integer> genreIds;
    Integer id;

    /* Default constructor. */
    public Movie () {}

    /* Constructor takes in a JSON object and extracts information to populate member variables. */
    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        voteCount = jsonObject.getDouble("vote_count");
        releaseDate = jsonObject.getString("release_date");
        adult = jsonObject.getBoolean("adult");
        id = jsonObject.getInt("id");

        // Convert the genre ids from an JSONArray to an ArrayList
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

    /* Getter for the movies' poster path. */
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    /* Getter for the movies' backdrop path. */
    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    /* Getter for the movie's title. */
    public String getTitle() {
        return title;
    }

    /* Getter for the movie's overview. */
    public String getOverview() { return overview; }

    /* Getter for the movie's vote average. */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /* Getter for the movie's vote count. */
    public Integer getVoteCount() {
        return voteCount.intValue();
    }

    /* Getter for the movie's release date. */
    public String getReleaseDate() {
        return releaseDate;
    }

    /* Getter for the movie's genre ids. */
    public List<Integer> getGenreIds() { return genreIds; }

    /* Getter for the movie's content rating. */
    public Boolean getAdult() { return adult; }

    /* Getter for the movie's id. */
    public Integer getId() { return id; }
}
