package com.example.hari.nytsearch.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hari.nytsearch.R;
import com.example.hari.nytsearch.adapter.ArticlesAdapter;
import com.example.hari.nytsearch.fragment.FilterSettingsFragment;
import com.example.hari.nytsearch.helper.ItemClickSupport;
import com.example.hari.nytsearch.helper.SpacesItemDecoration;
import com.example.hari.nytsearch.model.Doc;
import com.example.hari.nytsearch.model.SearchResult;
import com.example.hari.nytsearch.model.Settings;
import com.example.hari.nytsearch.service.ServiceInterface;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
    implements FilterSettingsFragment.FilterSettingsFragmentListener
{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvArticles)
    RecyclerView rvArticles;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    ArticlesAdapter adapter;
    SearchResult searchResult;
    ArrayList<Doc> docs;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        docs = new ArrayList<>();

        adapter = new ArticlesAdapter(this, docs);

        settings = new Settings();
        invalidateOptionsMenu();
        setSupportActionBar(toolbar);
        setupRvArticles();
        setupSwipeContainer();

        query = getString(R.string.default_query);
        startSearch();

        ActionBar bar = getSupportActionBar();
        if (bar!=null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.ic_launcher);
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(true);

        }

//        setupWindowAnimations();

    }

    private void setupRvArticles() {
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvArticles.setLayoutManager(gridLayoutManager);

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadDataFromApi(page - 1, false);
            }
        });

        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    handleItemClick(position,v);
                }
        );

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        rvArticles.addItemDecoration(decoration);

        setupGridAnimator(AnimationEvent.AddAll);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations()
    {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }

    public enum AnimationEvent {Add, Delete, AddAll, DeleteAll}

    private void setupGridAnimator(AnimationEvent event)
    {
        switch (event)
        {
            case Add:
                case AddAll:
                    SlideInBottomAnimationAdapter animator = new SlideInBottomAnimationAdapter(adapter);
                animator.setDuration(1000);
                animator.setFirstOnly(false);
                animator.setInterpolator(new OvershootInterpolator(1f));

                AlphaInAnimationAdapter alphaAnimator = new AlphaInAnimationAdapter(animator);
                alphaAnimator.setFirstOnly(false);
                alphaAnimator.setDuration(600);
                rvArticles.setAdapter(new ScaleInAnimationAdapter(alphaAnimator));

                break;

            default:
                SlideInBottomAnimationAdapter animator2 = new SlideInBottomAnimationAdapter(rvArticles.getAdapter());
                animator2.setDuration(1000);
                animator2.setFirstOnly(false);
                animator2.setInterpolator(new OvershootInterpolator(1f));

                AlphaInAnimationAdapter alphaAnimator1 = new AlphaInAnimationAdapter(animator2);
                alphaAnimator1.setFirstOnly(false);
                alphaAnimator1.setDuration(600);
                rvArticles.setAdapter(alphaAnimator1);
                break;
        }
    }

    private void setupSwipeContainer()
    {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.


            int docCount = docs.size();
            adapter.clear();
            adapter.notifyItemRangeRemoved(0, docCount);

            customLoadDataFromApi(0, true);

        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private String BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    private String apiKey = "0f01920406814c448599dbab2eee1bd2";

    private String query = "";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void startSearch() {

        adapter.clear();
        customLoadDataFromApi(0, false);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void customLoadDataFromApi(int page, boolean isSwipeRefreshed) {

        try {
            if (query.isEmpty()) {
                swipeContainer.setRefreshing(false);
                return;
            }
            if(!isNetworkAvailable())
            {
                Toast.makeText(this, "This device is not connected to a network", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                return;
            }
            if(!isOnline())
            {
                Toast.makeText(this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                return;
            }

            if(!isSwipeRefreshed)
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

                    if(searchResult == null) {
                        if(isSwipeRefreshed)
                            swipeContainer.setRefreshing(false);
                        else
                            hideProgressBar();
                        return;
                    }
                        int docCount = docs.size();
                        docs.addAll(searchResult.getResponse().getDocs());

                        adapter.notifyItemRangeInserted(docCount, docs.size());


                    if(isSwipeRefreshed)
                        swipeContainer.setRefreshing(false);
                    else
                        hideProgressBar();

                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("search result", "Error!");
                    if(isSwipeRefreshed)
                        swipeContainer.setRefreshing(false);
                    else
                        hideProgressBar();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            hideProgressBar();
            swipeContainer.setRefreshing(false);
        }
    }


    //@OnItemClick(R.id.gvResults)
    public void handleItemClick(int position, View v) {

        Animation a = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        v.startAnimation(a);

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

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                query = getString(R.string.default_query);
                startSearch();
                return true;
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
                = FilterSettingsFragment.newInstance(settings);
        filterSettingsFragment.show(fm, "fragment_filter_settings");

    }

    private Settings settings;

    @Override
    public void onFinishFilterSettingsFragment(Settings settings) {

        this.settings = settings;
        adapter.clear();
        customLoadDataFromApi(0, false);
    }
}

