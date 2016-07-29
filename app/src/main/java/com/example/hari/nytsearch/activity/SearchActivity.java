package com.example.hari.nytsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.hari.nytsearch.R;
import com.example.hari.nytsearch.adapter.ArticlesArrayAdapter;
import com.example.hari.nytsearch.model.Doc;
import com.example.hari.nytsearch.model.SearchResult;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {


    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    @BindView(R.id.gvResults)
    GridView gvResults;

    ArticlesArrayAdapter adapter;
    SearchResult searchResult;
    ArrayList<Doc> docs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        docs = new ArrayList<>();
        adapter = new ArticlesArrayAdapter(this, docs);
        gvResults.setAdapter(adapter);

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadDataFromApi(page);
                // or customLoadDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    private String BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    private String apiKey = "28e4cfed61e54e009959cdaeaa045680";

    private String query = "";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @OnClick(R.id.btnSearch)
    public void startSearch() {

        String enteredText = etSearch.getText().toString();
        if (enteredText.isEmpty())
            return;
        query = enteredText;
        adapter.clear();
        customLoadDataFromApi(0);

    }

    private void customLoadDataFromApi(int page)
    {
        ServiceInterface apiService =
                retrofit.create(ServiceInterface.class);

        Call<SearchResult> call = apiService.get(apiKey, 0, query);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int statusCode = response.code();
                searchResult = response.body();

                adapter.addAll(searchResult.getResponse().getDocs());
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                // Log error here since request failed
                Log.e("search result", "Error!");

            }
        });
    }

    @OnItemClick(R.id.gvResults)
    public void handleItemClick(int position)
    {

        Doc doc = this.docs.get(position);
        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        //intent.putExtra("docs", Parcels.wrap(docs));
        intent.putExtra("url", doc.getWebUrl());
        startActivity(intent);
    }


}

