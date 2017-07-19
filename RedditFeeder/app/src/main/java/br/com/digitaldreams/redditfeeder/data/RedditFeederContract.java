package br.com.digitaldreams.redditfeeder.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by josecostamartins on 7/17/17.
 * using this as guide: https://guides.codepath.com/android/Creating-Content-Providers
 */

public class RedditFeederContract {

    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "br.com.digitaldreams.redditfeeder.database";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */
    public static final String PATH_SUBREDDIT = "subreddit";
    public static final String PATH_SUBREDDIT_STREAMS = "streams";

    /**
     * Create one class for each table that handles all information regarding the table schema and
     * the URIs related to it.
     */
    public static final class SubredditEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBREDDIT).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI  + "/" + PATH_SUBREDDIT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_SUBREDDIT;

        // Define the table schema
        public static final String TABLE_NAME = "subreddit_table";
        public static final String COLUMN_NAME = "subreddit_name";
        public static final String COLUMN_HEADER_IMAGE = "header_image";
        public static final String COLUMN_DESCRIPTION = "public_description";

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildSubredditUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
