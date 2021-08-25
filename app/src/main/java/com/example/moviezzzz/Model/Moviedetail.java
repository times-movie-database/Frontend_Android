package com.example.moviezzzz.Model;

import java.util.List;
/**
 * Model Class for brief Movie Description
 */
public class Moviedetail {
    public int id;
    public String title;
    public double rating;
    public String summary;
    public int count;
    public List<GenereData> genres;
    public List<CastList> cast;

    public Moviedetail(int id, String title, double rating, String summary, int count, List<GenereData> genres, List<CastList> cast) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.summary = summary;
        this.count = count;
        this.genres = genres;
        this.cast = cast;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<GenereData> getGenres() {
        return genres;
    }

    public void setGenres(List<GenereData> genres) {
        this.genres = genres;
    }

    public List<CastList> getCast() {
        return cast;
    }

    public void setCast(List<CastList> cast) {
        this.cast = cast;
    }

    @Override
    public String toString() {
        return "Moviedetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", summary='" + summary + '\'' +
                ", count=" + count +
                ", genres=" + genres +
                ", cast=" + cast +
                '}';
    }
}
