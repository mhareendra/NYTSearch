package com.example.hari.nytsearch.activity;

import com.example.hari.nytsearch.model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("articlesearch.json")
    Call<SearchResult> get(
            @Query("api-key") String apiKey,
            @Query("page") Integer page,
            @Query("q") String query

    );

//    @GET("users/{username}")
//    Call<SearchResult> getSearchResult(@Path("article") String searchQuery);
//
//    @GET("group/{id}/users")
//    Call<List<SearchResult>> groupList(@Path("id") int groupId, @Query("sort") String sort);
//
//    @POST("users/new")
//    Call<SearchResult> createUser(@Body SearchResult user);
}
