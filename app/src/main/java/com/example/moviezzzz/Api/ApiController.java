package com.example.moviezzzz.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController
{
    /**
     * API Controller ( Used for building the retrofit )
     */
   //private static final String url="https://salty-hollows-74392.herokuapp.com";
   private static ApiController clientobject;
   private static Retrofit retrofit;
     ApiController(String urll)
     {
        retrofit=new Retrofit.Builder()
                     .baseUrl(urll)
                     .addConverterFactory(GsonConverterFactory.create())
                     .build();
     }

     public static synchronized ApiController getInstance(String urll)
     {
          //if(clientobject==null)
          clientobject=new ApiController(urll);
          return clientobject;
     }

    public Apiset getapi()
     {
         return retrofit.create(Apiset.class);
     }
}
