package br.com.digitaldreams.redditfeeder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.dean.jraw.models.Subreddit;

import java.util.ArrayList;

import br.com.digitaldreams.redditfeeder.R;

/**
 * Created by josecostamartins on 6/23/17.
 */

public class SubredditAdapter extends BaseAdapter {

    private ArrayList<Subreddit> subreddits;
    private LayoutInflater inflater;
    private Context mContext;

    public SubredditAdapter(ArrayList<Subreddit> subreddits, Context mContext) {
        if (subreddits == null || mContext == null){
            throw new NullPointerException("Subreddits list and context can't be null");
        }
        this.subreddits = subreddits;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return subreddits== null ? 0 : subreddits.size();
    }

    @Override
    public Subreddit getItem(int i) {
        return subreddits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null) {
            view = inflater.inflate(R.layout.subreddit_list_layout, null);
        }

        ImageView headerImage = (ImageView) view.findViewById(R.id.subredditImageHeader);
        TextView title = (TextView) view.findViewById(R.id.subreddit_title);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);

        Subreddit subreddit = this.subreddits.get(i);
        if (subreddit.getHeaderImage() != null){
            Glide.with(mContext)
                    .load(subreddit.getHeaderImage())
                    .into(headerImage);
        }
        title.setText(subreddit.getDisplayName());
        subtitle.setText(subreddit.getAccountsActive() + mContext.getString(R.string.accounts_active));


        return view;
    }
}
