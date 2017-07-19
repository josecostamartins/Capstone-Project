package br.com.digitaldreams.redditfeeder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by josecostamartins on 7/18/17.
 * using this as guide: https://guides.codepath.com/android/Creating-Content-Providers
 */

public class RedditFeederDBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = "DatabaseHelperDebug";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "reddit_data";

    public RedditFeederDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when the database is first created.
     * @param db The database being created, which all SQL statements will be executed on.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        addSubreddit(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void addSubreddit(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + RedditFeederContract.SubredditEntry.TABLE_NAME + " (" +
                        RedditFeederContract.SubredditEntry._ID + " INTEGER PRIMARY KEY, " +
                        RedditFeederContract.SubredditEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                        RedditFeederContract.SubredditEntry.COLUMN_DESCRIPTION + "TEXT, " +
                        RedditFeederContract.SubredditEntry.COLUMN_HEADER_IMAGE + "TEXT" +
                        ");"
        );
    }

}
