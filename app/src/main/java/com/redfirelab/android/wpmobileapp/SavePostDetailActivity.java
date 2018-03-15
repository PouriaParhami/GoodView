package com.redfirelab.android.wpmobileapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPContract;
import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class SavePostDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SavePostDetailActivity.class.getSimpleName();

    private TextView mTitle;
    //private TextView mContent;
    private String postLink;
    private MethodsUtils methods = new MethodsUtils(SavePostDetailActivity.this);
    private HtmlTextView htmlTextView;

    private static final int ID_DETAIL_LOADER = 2;

    public static final String[] POST_DETAIL_PROJECTION = {

            WPContract.WPPostEntry.POST_TITLE,
            WPContract.WPPostEntry.POST_CONTENT,
            WPContract.WPPostEntry.POST_LINK,
            WPContract.WPPostEntry.SITE_BASE_URL
    };

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.v(TAG, "in the on Create");

        //for back button
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.main_app_name_activity_title);

        }

        mTitle = findViewById(R.id.tv_detail_post_title);

        htmlTextView = findViewById(R.id.tv_detail_post_content);

        //set size title and content
        mTitle.setTextSize(Float.parseFloat(WPPreferences.getSizeOfPostTitle(this)));
        htmlTextView.setTextSize(Float.parseFloat(WPPreferences.getSizeOfContentPost(this)));

        mUri = getIntent().getData();
        //Throw a NullPointerException if that URI is null
        if (mUri == null)
            throw new NullPointerException("URI for SavePostDetailActivity cannot be null");

        //Initialize the loader for SavePostDetailActivity
        /* This connects our Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "in the Loader");
        switch (id) {

            //COMPLETED (23) If the loader requested is our detail loader, return the appropriate CursorLoader
            case ID_DETAIL_LOADER:
                Log.v(TAG, "in the case");
                return new CursorLoader(this,
                        mUri,
                        POST_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "in the on finished");
        /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        mTitle.setText(Html.fromHtml(data.getString(0)));

        htmlTextView.setHtml(data.getString(1),
                new HtmlHttpImageGetter(htmlTextView, data.getString(3), true));

        postLink = data.getString(2);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.share_post_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    //--------------- menu --------------------

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * SavePostDetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Share menu item clicked */
        if (id == R.id.post_menu_share_link) {
            Intent shareIntent = createSharePostIntent(postLink);
            startActivity(shareIntent);

            return true;

        } else if (id == R.id.post_menu_share_content) {

            Intent shareIntetn = createSharePostIntent(String.valueOf(htmlTextView.getText()));
            startActivity(shareIntetn);

        } else if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createSharePostIntent(String content) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(mTitle.getText())
                .setType("text/plain")
                .setText(content + "\n#darkoobweb.com")
                .getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        return shareIntent;

    }

}
