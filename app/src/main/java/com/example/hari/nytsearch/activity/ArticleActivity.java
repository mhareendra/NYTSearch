package com.example.hari.nytsearch.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hari.nytsearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle)
    WebView webView;

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

    }
}
