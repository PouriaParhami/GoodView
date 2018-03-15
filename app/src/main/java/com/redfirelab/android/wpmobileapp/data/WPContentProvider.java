package com.redfirelab.android.wpmobileapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redfirelab.android.wpmobileapp.data.WPContract.WPPostEntry;

/**
 * Created by Pouria on 11/30/2017.
 * wpMApp project.
 */

public class WPContentProvider extends ContentProvider {

    public static final int POST = 100;
    public static final int POST_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(WPContract.AUTHORITY, WPContract.PATH_WP, POST);

        uriMatcher.addURI(WPContract.AUTHORITY, WPContract.PATH_WP + "/#", POST_WITH_ID);

        return uriMatcher;
    }

    private WPDBHelper mWPDbHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mWPDbHelper = new WPDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mWPDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match) {

            case POST:

                cursor = db.query(WPPostEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case POST_WITH_ID:

                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                cursor = db.query(WPPostEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:

                throw new UnsupportedOperationException("Unknown Url " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mWPDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {

            case POST:

                long id = db.insert(WPPostEntry.TABLE_NAME, null, contentValues);

                if (id > 0) {

                    returnUri = ContentUris.withAppendedId(WPPostEntry.CONTENT_URI, id);

                } else {

                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }

                break;

            case POST_WITH_ID:

                break;

            default:

                throw new UnsupportedOperationException("Unknown Url " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mWPDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case POST_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(WPPostEntry.TABLE_NAME, "post_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
