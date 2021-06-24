package com.example.flixster;
import android.os.Bundle;
import android.util.Log;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    public static final String TAG = "MovieTrailerActivity"; // for printing to logcat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        // Parse the trailer id from the intent
        final String trailerId = getIntent().getStringExtra("trailerId"); // ex. "508943"

        // Resolve the player view from the layout
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

        // Initialize the YouTube player with API key stored in secrets.xml
        String key = getString(R.string.youtube_api_key);
        playerView.initialize(key, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // Cue and play the video
                youTubePlayer.cueVideo(trailerId);
                youTubePlayer.play();
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
