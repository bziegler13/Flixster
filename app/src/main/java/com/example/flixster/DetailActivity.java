package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Iterator;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvTitle);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvDetailTitle.setText(movie.getTitle());
        tvDetailOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());



        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    }
                    // look through results and find a youtube trailer.
                    String youTubeKey;
                    for(int i = 0; i < results.length(); i++) {
                        String type = results.getJSONObject(i).getString("type");
                        String site = results.getJSONObject(i).getString("site");
                        if (type.equals("Trailer") && site.equals("YouTube")) {
                            // found a youtube trailer
                            youTubeKey = results.getJSONObject(i).getString("key");
                            Log.d("DetailActivity", youTubeKey);
                            if(movie.getRating() > 8.0) {
                                initializeYoutube(youTubeKey, true);
                                Log.d("DetailActivity", Double.toString(movie.getRating()));
                            }
                            else {
                                initializeYoutube(youTubeKey, false);
                            }
                            break;
                        }
                    }

                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

    }

    // pass the initialize method a boolean to tell it whether it should autoplay or not
    private void initializeYoutube(final String youTubeKey, boolean autoplay) {
        // initialize with API key stored in secrets.xml
        youTubePlayerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onInitializationSuccess");

                if(autoplay){
                    youTubePlayer.loadVideo(youTubeKey);
                }
                else {
                    youTubePlayer.cueVideo(youTubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }
}