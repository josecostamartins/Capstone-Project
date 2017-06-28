package br.com.digitaldreams.redditfeeder;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.auth.TokenStore;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;

/**
 * Created by josecostamartins on 6/13/17.
 */

public class AppController extends Application {

    private static AppController mInstance;
    private AuthenticationManager authenticationManager;
    private RedditClient reddit;
    private UserAgent userAgent;

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;
        initAuthenticationManager();
    }


    public static AppController getmInstance() {
        return mInstance;
    }

    public Boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null) {
            return false;
        }
        return info.isConnected();
    }

    private void initAuthenticationManager() {
        if (mInstance.userAgent == null) {
            mInstance.userAgent = UserAgent.of(
                    "Installed App",
                    "br.com.digitaldreams.redditfeeder",
                    "v1.0",
                    "sfamrcks");
        }

        if (mInstance.reddit == null) {
            mInstance.reddit = new RedditClient(mInstance.userAgent);
            mInstance.reddit.setLoggingMode(LoggingMode.ALWAYS);
        }

        mInstance.authenticationManager = AuthenticationManager.get();
        mInstance.authenticationManager.init(
                mInstance.reddit,
                new RefreshTokenHandler(
                        new AndroidTokenStore(this), mInstance.reddit));
    }

    public AuthenticationManager getAuthenticationManager() {
        if (mInstance.authenticationManager == null) {
            initAuthenticationManager();
        }
        return mInstance.authenticationManager;
    }

    public RedditClient getReddit() {
        if (mInstance.reddit == null) {
            initAuthenticationManager();
        }
        return mInstance.reddit;
    }

    private UserAgent getUserAgent() {
        if (mInstance.userAgent == null) {
            initAuthenticationManager();
        }
        return mInstance.userAgent;
    }
}
