package com.example.flixster;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.flixster.databinding.ActivityDetailsBinding;
import com.example.flixster.models.Movie;
import org.parceler.Parcels;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsActivity extends AppCompatActivity {

    ImageView ivPoster;
    RatingBar ratingBar;
    TextView tvTitle;
    TextView tvOverview;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setTitle("Movie Details");
        Context context = getApplicationContext();

        // Get the components ot the activity
        ivPoster = binding.ivPoster;
        ratingBar = binding.ratingBar;
        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;

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
}