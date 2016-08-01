package com.example.hari.nytsearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hari.nytsearch.R;
import com.example.hari.nytsearch.model.Doc;
import com.example.hari.nytsearch.model.Headline;
import com.example.hari.nytsearch.model.Multimedium;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hari on 7/31/2016.
 */
public class ArticlesAdapter
extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    public static class ArticleViewHolder
    extends RecyclerView.ViewHolder
    {

        @BindView(R.id.ivImage)
        ImageView ivImage;

        public TextView getTvTitle() {
            return tvTitle;
        }

        public void setTvTitle(TextView tvTitle) {
            this.tvTitle = tvTitle;
        }

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvPubDateTI)
        TextView tvPubDate;

        public ArticleViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public static class TextViewHolder
    extends RecyclerView.ViewHolder
    {
        public TextView getTvTitleText() {
            return tvTitleText;
        }

        public void setTvTitleText(TextView tvTitleText) {
            this.tvTitleText = tvTitleText;
        }

        public TextView getTvSnippet() {
            return tvSnippet;
        }

        public void setTvSnippet(TextView tvSnippet) {
            this.tvSnippet = tvSnippet;
        }

        @BindView(R.id.tvTitleText)
        TextView tvTitleText;

        @BindView(R.id.tvSnippet)
        TextView tvSnippet;

        @BindView(R.id.tvPubDate)
        TextView tvPubDate;

        public TextViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private List<Doc> docs;

    private Context context;

    public ArticlesAdapter(Context context, List<Doc> docs)
    {
        this.docs = docs;
        this.context = context;
    }

    private Context getContext()
    {
        return context;
    }

    private final int TEXT_ONLY = 0, TEXT_IMAGE = 1;

    @Override
    public int getItemViewType(int position) {

        Doc doc = docs.get(position);

        List<Multimedium> media = doc.getMultimedia();

        if(media.size() == 0)
             return TEXT_ONLY;

        for (Multimedium m :media
             ) {
            if(m.getUrl() != null)
                return TEXT_IMAGE;
        }
        return  TEXT_ONLY;
    }

        // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;

        switch (viewType)
        {
            case TEXT_IMAGE:
                // Inflate the custom layout
                View v = inflater.inflate(R.layout.item_article_result, parent, false);

                // Return a new holder instance
                holder = new ArticleViewHolder(v);

                break;
            case TEXT_ONLY:
                // Inflate the custom layout
                View v2 = inflater.inflate(R.layout.item_article_result_text, parent, false);
                // Return a new holder instance
                holder = new TextViewHolder(v2);


                break;
            default:
                // Inflate the custom layout
                View v3 = inflater.inflate(R.layout.item_article_result_text, parent, false);
                // Return a new holder instance
                holder = new TextViewHolder(v3);
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case TEXT_IMAGE:
                ArticleViewHolder vh = (ArticleViewHolder)holder;
                configureArticleViewHolder(vh, position);
                break;
            case TEXT_ONLY:

                TextViewHolder vh1 = (TextViewHolder)holder;
                configureTextViewHolder(vh1, position);
                break;
            default:

                break;

        }
    }

    private void configureTextViewHolder(TextViewHolder holder, int position)
    {
        Doc doc = docs.get(position);
        Headline headline = doc.getHeadline();

        if(headline != null) {
            String title = headline.getMain();
            if (!title.isEmpty())
                holder.tvTitleText.setText(headline.getMain());
            else {
                holder.tvTitleText.setText(doc.getLeadParagraph());
            }
        }

        String snippet = doc.getSnippet();
        if(snippet==null || snippet.isEmpty())
            snippet = doc.getSource();
        holder.tvSnippet.setText(snippet);

        String pubDate = doc.getPubDate();
        if(pubDate!=null && !pubDate.isEmpty())
        {
            pubDate = pubDate.split("T")[0];
            String year = pubDate.split("-")[0];
            String month = pubDate.split("-")[1].split("-")[0];
            String day = pubDate.split("-")[2];
            holder.tvPubDate.setText(String.format("%s/%s/%s", month, day, year));
        }

    }

    private void configureArticleViewHolder(ArticleViewHolder holder, int position)
    {
        Doc doc = docs.get(position);
        Headline headline = doc.getHeadline();

        if(headline != null) {
            String title = headline.getMain();
            if(!title.isEmpty())
                holder.tvTitle.setText(headline.getMain());
        }

        List<Multimedium> mmList = doc.getMultimedia();

        String url = "";
        for (Multimedium m : mmList
             ) {
            if(m.getUrl() != null) {
                url = m.getUrl();
                break;
            }
        }

        if(url.isEmpty())
            return;

        if(mmList.size() > 0) {
            Glide.with(getContext())
                    .load(thumbnailBaseUrl + url)
                    .into(holder.ivImage);
        }

        String pubDate = doc.getPubDate();
        if(pubDate!=null && !pubDate.isEmpty())
        {
            pubDate = pubDate.split("T")[0];
            String year = pubDate.split("-")[0];
            String month = pubDate.split("-")[1].split("-")[0];
            String day = pubDate.split("-")[2];
            holder.tvPubDate.setText(String.format("%s/%s/%s", month, day, year));
        }
    }

    private String thumbnailBaseUrl = "http://www.nytimes.com/";
    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return docs.size();
    }

    public void clear()
    {
        docs.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Doc> docs)
    {
        this.docs.addAll(docs);
    }

}
