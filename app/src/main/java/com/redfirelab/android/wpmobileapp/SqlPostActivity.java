package com.redfirelab.android.wpmobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPContract;
import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;

public class SqlPostActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SqlAdapter.SqlClickListener {

    private static final String TAG = SqlPostActivity.class.getSimpleName();
    private static final int POST_LOADER_ID = 1;

    // Member variables for the adapter and RecyclerView
    private SqlAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private TextView mError;
    private ProgressBar mLoadingIndicator;
    private ImageView mImageView;

    private MethodsUtils methods = new MethodsUtils(SqlPostActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_post);

        //-------------------------------------------------------------------

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.favorite_posts_title_activity);

        }

        //-------------------------------------------------------------------

        mRecyclerView = findViewById(R.id.rv_sql_post_activity);

        mError = findViewById(R.id.tv_error_message_sql_data);

        mImageView = findViewById(R.id.iv_empty_sql_box);

        mLoadingIndicator = findViewById(R.id.pb_sql_loading_indicator);

        //create layout for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SqlAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(POST_LOADER_ID, null, this);

         /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                //Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                Log.v(TAG, "The id of ItemView.getTag ===> " + id);

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = WPContract.WPPostEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                Log.v(TAG, "The uri for delete ===> " + uri);

                //Delete a single row of data using a ContentResolver
                int result = getContentResolver().delete(uri, null, null);
                Log.i(TAG, "the result of delete ---> " + result);
                if(result > 0){

                    //Remove the id from Shared
                    WPPreferences.removeFavoritePosts(SqlPostActivity.this, id);
                    Log.v(TAG, "THE ITEM IS DELETE SUCCESSFULLY");

                }

                //Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(POST_LOADER_ID, null, SqlPostActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(POST_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpSharePreferences();
    }

    protected void showData() {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    protected void showError() {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);


    }
    //---------------------------------- Loader methods ---------------------------------------

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mPostData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mPostData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mPostData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    String sortOrder = WPContract.WPPostEntry._ID + " DESC";
                    return getContentResolver().query(WPContract.WPPostEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            sortOrder);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mPostData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.getCount() != 0) {

            mAdapter.swapCursor(data);

            showData();

        } else {

            showError();

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }

    //---------------------------------- End Loader methods ---------------------------------------

    //---------------------------------- Menu methods ---------------------------------------------

    //menu methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(long id) {

        Intent intent = new Intent(SqlPostActivity.this, SavePostDetailActivity.class);

        Uri uriForDateClicked = WPContract.WPPostEntry.buildPostUriWithId(id);

        intent.setData(uriForDateClicked);

        startActivity(intent);

    }

    private void setUpSharePreferences() {

        mAdapter.updateTitleSize(Float.parseFloat(WPPreferences.getSizeOfPostTitle(this)));
        mAdapter.updateDescriptionSize(Float.parseFloat(WPPreferences.getSizeOfPostDescription(this)));

    }

}
