package com.example.hari.nytsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hari.nytsearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle)
    WebView webView;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        ButterKnife.bind(this);

        //Doc doc =  (Doc) Parcels.unwrap(getIntent().getParcelableExtra("docs"));
        final String url = getIntent().getStringExtra("url"); //doc.getWebUrl();

        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                         //return super.shouldOverrideUrlLoading(view, request);
                                         view.loadUrl(url);
                                         return true;
                                     }
                                 }
        );
        webView.loadUrl(url);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar!=null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setIcon(R.mipmap.ic_launcher);
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(true);

        }


    }

    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_article_view, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());

        if (miShareAction != null && shareIntent != null)
            miShareAction.setShareIntent(shareIntent);
        // Return true to display menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Animation slideLeftAnimation = AnimationUtils.loadAnimation(getBaseContext (), R.anim.zoom_out);
            webView.startAnimation(slideLeftAnimation);
//            webView.goBack();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
