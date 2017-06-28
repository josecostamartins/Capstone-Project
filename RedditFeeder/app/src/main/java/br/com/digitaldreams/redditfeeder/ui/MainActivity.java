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
import br.com.digitaldreams.redditfeeder.ui.adapter.SubredditAdapter;

public class MainActivity extends AppCompatActivity {

    ListView subredditsListView;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSubreddits();
            subredditsListView = (ListView) findViewById(R.id.my_subreddit);
        }

    }

    private void getSubreddits() {
        new AsyncTask<Void, Void, ArrayList<Subreddit>>() {
            @Override
            protected ArrayList<Subreddit> doInBackground(Void... params) {
                UserSubredditsPaginator paginator = new UserSubredditsPaginator(AppController.getmInstance().getReddit(), "subscriber");
                ArrayList<Subreddit> mySubreddits = new ArrayList<>();
                while (paginator.hasNext()) {
                    Listing<Subreddit> subreddits = paginator.next();
                    for (Subreddit subreddit : subreddits) {
                        if (!subreddit.isNsfw()) {
                            mySubreddits.add(subreddit);
                        }
                    }
                }

                // sorting
                if (!mySubreddits.isEmpty()) {
                    Collections.sort(mySubreddits, new Comparator<Subreddit>() {
                        @Override
                        public int compare(Subreddit o1, Subreddit o2) {
                            return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
                        }
                    });
                }

                return mySubreddits;
            }


            @Override
            protected void onPostExecute(ArrayList<Subreddit> localSubredditList) {
                SubredditAdapter subredditAdapter = new SubredditAdapter(localSubredditList, mContext);
                subredditsListView.setAdapter(subredditAdapter);
                subredditAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


}
