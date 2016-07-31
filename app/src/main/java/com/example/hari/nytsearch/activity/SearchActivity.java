package com.example.hari.nytsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.hari.nytsearch.R;
import com.example.hari.nytsearch.adapter.ArticlesArrayAdapter;
import com.example.hari.nytsearch.fragment.FilterSettingsFragment;
import com.example.hari.nytsearch.model.Doc;
import com.example.hari.nytsearch.model.SearchResult;
import com.example.hari.nytsearch.model.Settings;
import com.example.hari.nytsearch.service.ServiceInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
    implements FilterSettingsFragment.FilterSettingsFragmentListener
{

    @BindView(R.id.gvResults)
    GridView gvResults;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
                if(adapter.getCount() > maxSearchResults)
                    return false;
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadDataFromApi(page - 1);
                // or customLoadDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        settings = new Settings();
        gvResults.setOnItemClickListener(this::handleItemClick);

        invalidateOptionsMenu();
        setSupportActionBar(toolbar);

        query = "news";
        startSearch();

    }

    private String BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    private String apiKey = "28e4cfed61e54e009959cdaeaa045680";

    private String query = "";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void startSearch() {
        adapter.clear();
        customLoadDataFromApi(0);
    }

    private int maxSearchResults = 999;

    private void customLoadDataFromApi(int page) {

        try {
            if (query.isEmpty())
                return;

            showProgressBar();

            ServiceInterface apiService =
                    retrofit.create(ServiceInterface.class);

            Call<SearchResult> call = apiService.get(
                    settings.getBeginDate(),
                    settings.getSortOrder(),
                    apiKey,
                    query,
                    settings.getNewsDeskValues(),
                    page
            );
            call.enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                    int statusCode = response.code();
                    searchResult = response.body();
                    maxSearchResults = searchResult.getResponse().getMeta().getHits();
                    adapter.addAll(searchResult.getResponse().getDocs());
                    hideProgressBar();
                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("search result", "Error!");
                    maxSearchResults = 0;
                    hideProgressBar();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            hideProgressBar();
        }
    }

    //@OnItemClick(R.id.gvResults)
    public void handleItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Doc doc = this.docs.get(position);
        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        //intent.putExtra("docs", Parcels.wrap(docs));
        intent.putExtra("url", doc.getWebUrl());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                // perform query here
                query = searchQuery;
                startSearch();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                query = newText;
                startSearch();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);




        //return true;
    }

    MenuItem miActionProgressItem;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }


    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem!=null)
            miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        if(miActionProgressItem!=null)
            miActionProgressItem.setVisible(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.settings_option)
        {
            showSettingsDialog();
        }

        return true;
    }

    private void showSettingsDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        FilterSettingsFragment filterSettingsFragment
                = FilterSettingsFragment.newInstance();
        filterSettingsFragment.show(fm, "fragment_filter_settings");

    }

    private Settings settings;

    @Override
    public void onFinishFilterSettingsFragment(Settings settings) {

        this.settings = settings;
        customLoadDataFromApi(0);
    }
}

