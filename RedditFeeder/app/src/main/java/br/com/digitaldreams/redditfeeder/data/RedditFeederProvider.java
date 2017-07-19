package br.com.digitaldreams.redditfeeder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by josecostamartins on 7/18/17.
 */

public class RedditFeederProvider extends ContentProvider {

    // Use an int for each URI we will run, this represents the different queries
    private static final int SUBREDDIT = 100;
    private static final int SUBREDDIT_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RedditFeederDBHelper mOpenHelper;



    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher(){
        String content = RedditFeederContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, RedditFeederContract.PATH_SUBREDDIT, SUBREDDIT);
        matcher.addURI(content, RedditFeederContract.PATH_SUBREDDIT + "/#", SUBREDDIT_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RedditFeederDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case SUBREDDIT:
                retCursor = db.query(
                        RedditFeederContract.SubredditEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUBREDDIT_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        RedditFeederContract.SubredditEntry.TABLE_NAME,
                        projection,
                        RedditFeederContract.SubredditEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)){
            case SUBREDDIT:
                return RedditFeederContract.SubredditEntry.CONTENT_TYPE;
            case SUBREDDIT_ID:
                return RedditFeederContract.SubredditEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch(sUriMatcher.match(uri)){
            case SUBREDDIT:
                _id = db.insertWithOnConflict(RedditFeederContract.SubredditEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if(_id > 0){
                    returnUri =  RedditFeederContract.SubredditEntry.buildSubredditUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch(sUriMatcher.match(uri)){
            case SUBREDDIT:
                rows = db.delete(RedditFeederContract.SubredditEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if(selection == null || rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch(sUriMatcher.match(uri)){
            case SUBREDDIT:
                rows = db.update(RedditFeederContract.SubredditEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;

    }
}
