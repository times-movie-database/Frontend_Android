package com.example.moviezzzz.Api;

import com.example.moviezzzz.Model.AddMovie;
import com.example.moviezzzz.Model.Editmovie;
import com.example.moviezzzz.Model.GenereData;
import com.example.moviezzzz.Model.MovieBrief;
import com.example.moviezzzz.Model.Moviedetail;
import com.example.moviezzzz.Model.ReviewData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Apiset
{
    /**
     * GET API CALLS
     */
    /**
     * API Calling for getting All Genres
     */
    @GET("/tmdb/genre")
    Call<List<GenereData>>allGeners();

    /**
     * API Calling for getting Top 10 Movies in HomeActivity Page
     */
    @GET("/tmdb/movies/top-ten")
    Call<List<MovieBrief>>getTopten();

    /**
     * API Calling for getting Top 10 Movies based on a particular genre in HomeActivity Page
     */
    @GET("/tmdb/movies/top-ten")
    Call<List<MovieBrief>>generTopten(@Query("genre") String genre);

    /**
     * API Calling for getting Movies based on search field and genre in SearchActivity Page
     */
    @GET("/tmdb/movies/search")
    Call<List<MovieBrief>> SearchName(@Query("title") String title, @Query("genre") String genre, @Query("pageNumber") Integer pageNumber);

    /**
     * API Calling for getting Movies based on based on Movie ID
     */
    @GET("/tmdb/movies/{movieId}")
    Call<Moviedetail>findDetail(@Path("movieId") Integer movieId);

    /**
     * API Calling for getting list of reviews based on Movie ID , Page Number and Page Size
     */
    @GET("/tmdb/movies/{movieId}/review")
    Call<List<ReviewData>>findReviewss(@Path("movieId") Integer movieId, @Query("pageNumber") Integer pageNumber,@Query("pageSize") Integer pagesize);

    /**
     * POST API CALLS
     */
    /**
     * API Calling for Adding a Movie
     */
    @POST("/tmdb/movies")
    Call<Integer>addMoviee(@Body AddMovie addMovie);

    /**
     * API Calling for Adding rating of a movie based on Movie ID
     */
    @POST("/tmdb/movies/{movieId}/rating")
    Call<Void>addRating(@Path("movieId") Integer movieId, @Body Double x);

    /**
     * API Calling for Adding review of a movie based on Movie ID
     */
    @POST("/tmdb/movies/{movieId}/review")
    Call<Void>addRevw(@Path("movieId") Integer movieId, @Body String x);

    /**
     * PUT API CALLS
     */
    /**
     * API Calling for Editing a movie based on Movie ID
     */
    @PUT("/tmdb/movies/{movieId}")
    Call<Editmovie>editMoviee(@Path("movieId") Integer movieId, @Body AddMovie editMovie);
}
