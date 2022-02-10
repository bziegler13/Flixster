package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {



    int movieId;
    String backdropPath;
    String posterPath;
    String imageSize;
    String title;
    String overview;
    double rating;
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/";

    // empty constructor needed by the Parceler library
    public Movie(){
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        imageSize = "original";
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getDouble("vote_average");
        movieId = jsonObject.getInt("id");
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getPosterPath() {
        return IMAGE_URL + imageSize + "/" + posterPath;
    }

    public double getRating() {
        return rating;
    }

    public String getBackdropPath() {
        return IMAGE_URL + imageSize + "/" + backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public int getMovieId() { return movieId; }
}
