package br.com.digitaldreams.redditfeeder.reddit;

import android.util.Log;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.SubredditStream;
import net.dean.jraw.paginators.UserSubredditsPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.digitaldreams.redditfeeder.AppController;
import br.com.digitaldreams.redditfeeder.Networking;
import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by josecostamartins on 7/13/17.
 */

public class RedditApi {

    public static Observable<String> observableUserChallenge(String TAG, final String URL, final Credentials creds) {

        return Observable.fromCallable(() -> {
            String authenticatedUser = null;
            try {
                OAuthData data = AuthenticationManager.get().getRedditClient().getOAuthHelper().onUserChallenge(URL, creds);
                AuthenticationManager.get().getRedditClient().authenticate(data);
                authenticatedUser = AuthenticationManager.get().getRedditClient().getAuthenticatedUser();
            } catch (NetworkException | OAuthException e) {
                Log.e(TAG, "Could not log in", e);
            } finally {
                return authenticatedUser;
            }
        });

    }

    public static Observable<ArrayList<Subreddit>> observableSubreddits(){

        return Observable.fromCallable(() -> {
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
                Collections.sort(mySubreddits, (o1, o2) -> o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName()));
            }

            return mySubreddits;
        });
    }


    public static Observable<ArrayList<Subreddit>> observableSubredditStream(){

        return Observable.fromCallable(() -> {
            SubredditStream stream = new SubredditStream(AppController.getmInstance().getReddit(), "new");
            ArrayList<Subreddit> mySubreddits = new ArrayList<>();
            while (stream.hasNext()) {
                Listing<Subreddit> subreddits = stream.next();
                for (Subreddit subreddit : subreddits) {
                    if (!subreddit.isNsfw()) {
                        mySubreddits.add(subreddit);
                    }
                }
            }

            return mySubreddits;
        });
    }

//    public static  Observable<Integer> observableLogin(){
//
//    }

}
