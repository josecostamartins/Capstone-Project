package br.com.digitaldreams.redditfeeder;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by josecostamartins on 6/13/17.
 */

public class AppController extends Application {

    private static AppController mInstance;

    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;
    }

    public static AppController getmInstance() {
        return mInstance;
    }

    public Boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null){
            return false;
        }
        return info.isConnected();
    }
}
