package br.com.digitaldreams.redditfeeder.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.UserSubredditsPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.digitaldreams.redditfeeder.AppController;
import br.com.digitaldreams.redditfeeder.R;
import br.com.digitaldreams.redditfeeder.reddit.RedditApi;
import br.com.digitaldreams.redditfeeder.ui.adapter.SubredditAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ListView subredditsListView;
    private Context mContext = this;
    private Disposable disposable;
    private ArrayList<Subreddit> mySubreddits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subredditsListView = (ListView) findViewById(R.id.my_subreddit);
        if (savedInstanceState == null) {
            getSubreddits();
        }

    }

    private void updateUI() {

    }

    private void getSubreddits() {
        disposable = RedditApi.observableSubreddits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    SubredditAdapter subredditAdapter = new SubredditAdapter(result, mContext);
                    subredditsListView.setAdapter(subredditAdapter);
                    subredditAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
