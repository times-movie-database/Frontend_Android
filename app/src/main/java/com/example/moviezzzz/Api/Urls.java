package com.example.moviezzzz.Api;
/**
 * URL Class for getting and setting the base URLs
 */
public class Urls {
    String GetMoviesNode,BaseUrlJava;

    public Urls() {
        GetMoviesNode = "https://tmdbnodeapi.herokuapp.com";//Base Url written in NODE
        BaseUrlJava = "https://salty-hollows-74392.herokuapp.com";//Base Url written in JAVA
    }

    public String getGetMoviesNode() {
        return GetMoviesNode;
    }

    public void setGetMoviesNode(String getMoviesNode) {
        GetMoviesNode = getMoviesNode;
    }

    public String getBaseUrlJava() {
        return BaseUrlJava;
    }

    public void setBaseUrlJava(String baseUrlJava) {
        BaseUrlJava = baseUrlJava;
    }
}
