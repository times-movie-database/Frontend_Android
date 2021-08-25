package com.example.moviezzzz.Model;
/**
 * Model Class for Review of a movie
 */
public class ReviewData {
    public int id;
    public String review;
    public String createdAt;

    public ReviewData(int id, String review, String createdAt) {
        this.id = id;
        this.review = review;
        this.createdAt = createdAt;
    }

    public ReviewData() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ReviewData{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
