package com.example.moviezzzz.Model;

import java.util.List;
/**
 * Model Class for Adding a Movie
 */
public class AddMovie {
        public List<CastList> castList;
        public List<Integer> genreIdList;
        public String summary;
        public String title;

    public AddMovie(List<CastList> castList, List<Integer> genreIdList, String summary, String title) {
        this.castList = castList;
        this.genreIdList = genreIdList;
        this.summary = summary;
        this.title = title;
    }

    public List<CastList> getCastList() {
        return castList;
    }

    public void setCastList(List<CastList> castList) {
        this.castList = castList;
    }

    public List<Integer> getGenreIdList() {
        return genreIdList;
    }

    public void setGenreIdList(List<Integer> genreIdList) {
        this.genreIdList = genreIdList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "AddMovie{" +
                "castList=" + castList +
                ", genreIdList=" + genreIdList +
                ", summary='" + summary + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
