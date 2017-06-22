package br.com.digitaldreams.redditfeeder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mErrorView;
    private View mProgressView;
    private View mLoginFormView;
    private OAuthHelper helper;
    private WebView webView;

    private final String TAG = this.getClass().getSimpleName();
    public static final Credentials CREDENTIALS = Credentials.installedApp(Networking.CLIENT_ID, Networking.REDIRECT_URI);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mErrorView = (TextView) findViewById(R.id.error_label);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        webView = ((WebView) findViewById(R.id.webview));

        // Create our RedditClient
        helper = AuthenticationManager.get().getRedditClient().getOAuthHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        Log.d(TAG, "AuthenticationState for onResume(): " + state);

        switch (state) {
            case READY:
                break;
            case NONE:
//                Toast.makeText(LoginActivity.this, "Log in first", Toast.LENGTH_SHORT).show();
//                mErrorView.setText("Log in first");
                startSignIn(null);
                break;
            case NEED_REFRESH:
                refreshAccessTokenAsync();
                break;
        }
    }

    private void refreshAccessTokenAsync() {
        new AsyncTask<Credentials, Void, Void>() {
            @Override
            protected Void doInBackground(Credentials... params) {
                try {
                    AuthenticationManager.get().refreshAccessToken(LoginActivity.CREDENTIALS);
                } catch (NoSuchTokenException | OAuthException e) {
                    Log.e(TAG, "Could not refresh access token", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d(TAG, "Reauthenticated");
            }
        }.execute();
    }

    public void startSignIn(View view) {
//        mErrorView.setVisibility(View.GONE);
//        if (!Validations.isValidEmail(mEmailView.getText().toString())){
//            mErrorView.setText(getResources().getString(R.string.error_invalid_email));
//            mErrorView.setVisibility(View.VISIBLE);
//            return;
//        }

        // OAuth2 scopes to request. See https://www.reddit.com/dev/api/oauth for a full list
        String[] scopes = {"identity", "read", "mysubreddits"};

        final URL authorizationUrl = helper.getAuthorizationUrl(CREDENTIALS, true, true, scopes);
        webView.setVisibility(View.VISIBLE);

        // Load the authorization URL into the browser
        webView.loadUrl(authorizationUrl.toExternalForm());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("code=")) {
                    // We've detected the redirect URL
                    webView.setVisibility(View.GONE);
                    onUserChallenge(url, CREDENTIALS);
                } else if (url.contains("error=")) {
                    Toast.makeText(LoginActivity.this, "You must press 'allow' to log in with this account", Toast.LENGTH_SHORT).show();
                    webView.loadUrl(authorizationUrl.toExternalForm());
                }
            }
        });
    }

    private void onUserChallenge(final String url, final Credentials creds) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    OAuthData data = AuthenticationManager.get().getRedditClient().getOAuthHelper().onUserChallenge(params[0], creds);
                    AuthenticationManager.get().getRedditClient().authenticate(data);
                    return AuthenticationManager.get().getRedditClient().getAuthenticatedUser();
                } catch (NetworkException | OAuthException e) {
                    Log.e(TAG, "Could not log in", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i(TAG, s);
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.setText(s);
//                LoginActivity.this.finish();
            }
        }.execute(url);
    }

}

