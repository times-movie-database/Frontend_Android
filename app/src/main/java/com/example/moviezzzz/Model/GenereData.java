package com.example.moviezzzz.Model;
/**
 * Model Class for Genres
 */
public class GenereData {
    String id,name;

    public GenereData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GenereData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
