package com.example.hari.nytsearch.service;

import com.example.hari.nytsearch.model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("articlesearch.json")
    Call<SearchResult> get(
            @Query("begin_date") String beginDate,
            @Query("sort") String sortOrder,
            @Query("api-key") String apiKey,
            @Query("q") String query,
            @Query("fq") String newsDesk,
            @Query("page") Integer page
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
