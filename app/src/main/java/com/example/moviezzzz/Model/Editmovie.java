package com.example.moviezzzz.Model;

import java.util.List;

public class Editmovie {
    public int id;
    public String title;
    public String summary;
    public double rating;
    public int count;
    public List<CastList> cast;
    public List<ReviewData> reviews;
    public List<GenereData> genres;

    public Editmovie(int id, String title, String summary, double rating, int count, List<CastList> cast, List<ReviewData> reviews, List<GenereData> genres) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.rating = rating;
        this.count = count;
        this.cast = cast;
        this.reviews = reviews;
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CastList> getCast() {
        return cast;
    }

    public void setCast(List<CastList> cast) {
        this.cast = cast;
    }

    public List<ReviewData> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewData> reviews) {
        this.reviews = reviews;
    }

    public List<GenereData> getGenres() {
        return genres;
    }

    public void setGenres(List<GenereData> genres) {
        this.genres = genres;
    }
}
