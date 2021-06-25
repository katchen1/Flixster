package com.example.flixster;
import android.os.Bundle;
import android.util.Log;
import com.example.flixster.databinding.ActivityMovieTrailerBinding;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    public static final String TAG = "MovieTrailerActivity"; // for printing to logcat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieTrailerBinding binding = ActivityMovieTrailerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Parse the trailer id from the intent
        final String trailerId = getIntent().getStringExtra("trailerId"); // ex. "SUXWAEX2jlg"

        // Resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) binding.player;

        // Initialize the YouTube player with API key stored in secrets.xml
        String key = getString(R.string.youtube_api_key);
        playerView.initialize(key, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // Load and play
                youTubePlayer.loadVideo(trailerId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // Log the error
                Log.e(TAG, "Error initializing YouTube player");
            }
        });
    }
}
