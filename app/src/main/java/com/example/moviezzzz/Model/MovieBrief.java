package com.example.moviezzzz.Model;
/**
 * Model Class for crisp Movie Description
 */
public class MovieBrief
{
   String id,title,rating,count;

    public MovieBrief(String id, String title, String rating, String count) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.count = count;
    }

    public MovieBrief() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


}
