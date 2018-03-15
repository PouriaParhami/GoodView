package com.redfirelab.android.wpmobileapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pouria on 11/29/2017.
 * wpMApp project.
 */

public class WPContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.redfirelab.android.wpmobileapp";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URL = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_WP = "post";


    /* WPPostEntry is an inner class that defines the contents of the task table */
    public static final class WPPostEntry implements BaseColumns {

        // WPPostEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URL.buildUpon().appendPath(PATH_WP).build();

        // Since WPPostEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below

        // Post table and column names

        public static final String TABLE_NAME = "post";

        public static final String POST_ID = "post_id";

        public static final String POST_TITLE = "title";

        public static final String POST_DESCRIPTION = "description";

        public static final String POST_CONTENT = "content";

        public static final String POST_LINK = "post_link";

        public static final String SITE_BASE_URL = "site_base_url";

        public static Uri buildPostUriWithId(long id) {
            return WPPostEntry.CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}
