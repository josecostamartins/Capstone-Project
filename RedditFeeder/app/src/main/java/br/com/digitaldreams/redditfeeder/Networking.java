package br.com.digitaldreams.redditfeeder;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by josecostamartins on 6/13/17.
 * based on http://progur.com/2016/10/how-to-use-reddit-oauth2-in-android-apps.html
 */

public class Networking {

    private static Networking sInstance;
    private static OkHttpClient client;
    private static Call mCall;


    public static final String CLIENT_ID = "-55kCXOdwiC4pg";

    public static final String REDIRECT_URI =
            "https://github.com/josecostamartins/Capstone-Project";

    public static final String ACCESS_TOKEN_URL =
            "https://www.reddit.com/api/v1/access_token";

    public static final String AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                    "&response_type=code&state=%s&redirect_uri=%s&" +
                    "duration=permanent&scope=identity";

    public static final String STATE = "REDDIT_FEEDER";

    /**
     * Singleton constructor
     * Every RESTLess call should pass through this class
     *
     * @return
     */
    public static synchronized Networking getInstance() {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new Networking();
        }
//        checkHeaderData();
        return sInstance;
    }

    private Networking() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS) // has to connect in 10 seconds
                .readTimeout(60, TimeUnit.SECONDS) // has to read in 60 seconds
                .writeTimeout(60, TimeUnit.SECONDS) // has to write in 60 seconds
                .build();
    }

    public void getAccessToken(String code) {
        this.getAccessToken(code, this.getClass().getSimpleName());
    }

    public void getAccessToken(String code, final String TAG) {
        if (TAG == null) {
            throw new NullPointerException("TAG in Networking cannot be null");
        }

        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);

        Request request = new Request.Builder()
                .addHeader("User-Agent",
                        AppController.getmInstance().getResources().getString(R.string.app_name))
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=authorization_code&code=" + code +
                                "&redirect_uri=" + REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    String accessToken = data.optString("access_token");
                    String refreshToken = data.optString("refresh_token");

                    Log.d(TAG, "Access Token = " + accessToken);
                    Log.d(TAG, "Refresh Token = " + refreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
