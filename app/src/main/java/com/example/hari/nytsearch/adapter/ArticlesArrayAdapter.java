package com.example.hari.nytsearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by Hari on 7/28/2016.
 */
public class ArticlesArrayAdapter extends ArrayAdapter<Doc> {

    static class ViewHolder {

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    public ArticlesArrayAdapter(Context context, List<Doc> searchResults) {
        super(context, android.R.layout.simple_list_item_1, searchResults);
    }


    private String thumbnailBaseUrl = "http://www.nytimes.com/";

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        Doc docs = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        Headline headline = docs.getHeadline();

        if(headline != null) {
            String title = headline.getMain();
            if(!title.isEmpty())
                holder.tvTitle.setText(headline.getMain());
        }

        List<Multimedium> mmList = docs.getMultimedia();

        if(mmList.size() > 0) {
            Glide.with(getContext())
                    .load(thumbnailBaseUrl + mmList.get(0).getUrl())
            .into(holder.ivImage);
        }
        return convertView;
    }
}
