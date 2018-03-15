package com.redfirelab.android.wpmobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;
import com.redfirelab.android.wpmobileapp.ultilities.NetworkUtils;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressCategoryData;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;


//this class look like main activity
public class CategoryActivity extends AppCompatActivity implements
        CategoryAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<WordpressCategoryData>> {

    private static final String TAG = CategoryActivity.class.getSimpleName();

    private static final int WP_LOADER_ID = 1;
    private static final String WP_LOADER_KEY = "CATEGORY_LOADER";
    private static final String CATEGORY_PUT_EXTRA_NAME = "CATEGORY_ID";

    //defined
    private TextView mErrorMessage;
    private RecyclerView mRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProgressBar mLoadingIndicator;
    private ImageView mImageView;
    private SwipeRefreshLayout mSwipeRefreshLayOut;
    private Button mTrayAgianButton;
    private TextView mLodingText;

    List<WordpressCategoryData> wordpressCategoryDataList;

    private Bundle bundle = new Bundle();

    private MethodsUtils methods = new MethodsUtils(CategoryActivity.this);

    //Defined for loading more data
    private int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.category_activity_title);

        }

        // ---------------- initialize ----------------------------------
        mRecyclerView = findViewById(R.id.rv_category_activity);
        mErrorMessage = findViewById(R.id.tv_error_message_display_category);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator_category);
        mImageView = findViewById(R.id.iv_inbox);
        mTrayAgianButton = findViewById(R.id.but_try_again_category);
        mLodingText = findViewById(R.id.tv_loading_text_category);

        mSwipeRefreshLayOut = findViewById(R.id.srl_category_activity);

        //Create layout for recycler view used "Post Adapter"
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        categoryAdapter = new CategoryAdapter(CategoryActivity.this);
        mRecyclerView.setAdapter(categoryAdapter);

        //------- Check network connection ------
        checkNetworkConnectionAndSetBundle(true);

        getSupportLoaderManager().initLoader(WP_LOADER_ID, bundle, this);

        //---------------- Swipe Refresh Layout ------------------

        mSwipeRefreshLayOut.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.v(TAG, "User pull down for refresh.");

                if (checkNetworkConnectionAndSetBundle(false)) {

                    if (mLoadingIndicator.getVisibility() == View.INVISIBLE) {

                        resetTheLoader();

                        Log.v(TAG, "The ScrollView is pull down, this means user want refresh");
                    }
                } else {

                    mSwipeRefreshLayOut.setRefreshing(false);

                }

            }
        });
        // Configure the refreshing colors
        mSwipeRefreshLayOut.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



         /*
          We check the scrolling recycler view
          If user rich end of the list and
          */

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {

                    //Is this the end of the list?
                    if (!recyclerView.canScrollVertically(1)) {

                        Log.v(TAG, "the end of the list");

                    /*
                    * When user rich the end of the list and request for more data
                    * We need to know loading data in the process or done
                    * If we are in the processing of loading more data and user swipe down again
                    * We must don't care of that, but if processing is done and user want more we do that
                    * For detect that, we check if progressbar (mLoadingIndicator) is visible that mean we are in process of loading data
                    * And if invisible that mean processing is done and user can want more data
                    *
                    * */

                        if (checkNetworkConnectionAndSetBundle(false)) {

                            if (mLoadingIndicator.getVisibility() == View.INVISIBLE) {

                                counter++;

                                bundle.putString(WP_LOADER_KEY, NetworkUtils.buildUrlCategory(WPPreferences.getSiteAddress(CategoryActivity.this), String.valueOf(counter)).toString());

                                getSupportLoaderManager().restartLoader(WP_LOADER_ID, bundle, CategoryActivity.this);

                                Log.v(TAG, "Rich End of the List And we must get more data, counter => " + counter);
                            }

                        }

                    }
                }
            }


        });

        mTrayAgianButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (methods.checkNetwork()) {

                    methods.hideViewsErrorMessage(mErrorMessage, mImageView, mLodingText, mTrayAgianButton);
                    resetTheLoader();

                }

            }
        });

    }

    //------------------- Loader method's over ride ----------------------------------------
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<WordpressCategoryData>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<WordpressCategoryData>>(this) {

            List<WordpressCategoryData> mWpjson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mWpjson != null) {

                    deliverResult(mWpjson);
                    Log.v(TAG, "mWpjson is not null");

                } else {

                    /*
                      If user use swipe refresh layout to refreshing data
                      We don't need the mLoadingIndicator
                     */

                    if (methods.checkNetwork()) {

                        if (!mSwipeRefreshLayOut.isRefreshing()) {

                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            mLodingText.setVisibility(View.VISIBLE);

                        }

                    }

                    forceLoad();
                    Log.v(TAG, "mWpjson is null");

                }

            }

            @Override
            public List<WordpressCategoryData> loadInBackground() {

                String searchQueryUrl = args.getString(WP_LOADER_KEY);
                if (searchQueryUrl == null || TextUtils.isEmpty(searchQueryUrl)) {
                    Log.v(TAG, "searchQueryUrl == null || TextUtils.isEmpty(searchQueryUrl) so return");
                    return null;
                }

                try {

                    URL searchQuery = new URL(searchQueryUrl);

                    if (wordpressCategoryDataList == null) {

                        Log.v(TAG, "WordpressCategoryData == null");
                        wordpressCategoryDataList = WordpressJsonUtils.getSimpleCategoryFromJson(NetworkUtils.getResponseFromHttpUrl(searchQuery));

                    } else {

                        Log.v(TAG, "WordpressCategoryData != null");

                        //noinspection ConstantConditions
                        wordpressCategoryDataList.addAll(WordpressJsonUtils.getSimpleCategoryFromJson(NetworkUtils.getResponseFromHttpUrl(searchQuery)));

                    }

                    Log.v(TAG, "The size of List => " + wordpressCategoryDataList.size());

                    Log.v(TAG, "in background try");

                    return wordpressCategoryDataList;

                } catch (IOException e) {

                    Log.e(TAG, "IOException " + e.getMessage());

                    return null;

                }catch (JSONException e){

                    Log.e(TAG, "JSONException " + e.getMessage());
                    return null;
                }
            }

            @Override
            public void deliverResult(List<WordpressCategoryData> wpdata) {
                mWpjson = wpdata;
                Log.v(TAG, "deiliver result" + wpdata);
                super.deliverResult(wpdata);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<WordpressCategoryData>> loader, List<WordpressCategoryData> data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayOut.setRefreshing(false);
        mLodingText.setVisibility(View.INVISIBLE);

        Log.v(TAG, "onLoadFinished");

        if (data != null && !data.isEmpty()) {

            methods.weHaveDataShowViews(mRecyclerView,mErrorMessage,mImageView,mLodingText,mTrayAgianButton);
            categoryAdapter.setWpdata(data);

            Log.v(TAG, "data != null && !data.isEmpty(), showWPPosts();  categoryAdapter.setWpdata(data);");

        } else {

            Log.v(TAG, "data == null && data.isEmpty(), so showMessageCantGetData()");
            whichMessageMustShow();

        }


    }

    @Override
    public void onLoaderReset(Loader<List<WordpressCategoryData>> loader) {

    }
    //------------------- Category adapter method's over ride ----------------------------------------

    @Override
    public void onListItemClick(WordpressCategoryData clickItemIndex) {

        Log.v(TAG, "The Index is => " + clickItemIndex.getId());

        if (checkNetworkConnectionAndSetBundle(false)) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(CATEGORY_PUT_EXTRA_NAME, String.valueOf(clickItemIndex.getId()));

            startActivity(intent);

        }
    }

    //----------------- Helper methods ------------------------------------------------------------------------------

    //check internet connection
    private boolean checkNetworkConnectionAndSetBundle(boolean setBundle) {

        // If there is a network connection, fetch data
        if (methods.checkNetwork()) {

            if (setBundle) {

                bundle = methods.createCategoryBundle(WP_LOADER_KEY);
                methods.weHaveDataShowViews(mRecyclerView, mErrorMessage, mImageView, mLodingText, mTrayAgianButton);

            }

            Log.v(TAG, "We check the network, is connected, and we do postSearchQuery() and showWPCategory()");
            return true;

        } else {

            methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgianButton);
            Log.v(TAG, "We check the network, is not connected, and we do showMessageNoInterNet()");
            return false;
        }

    }

    private void resetTheLoader() {

        if (checkNetworkConnectionAndSetBundle(false)) {

            wordpressCategoryDataList = null;
            counter = 1;
            bundle = methods.createCategoryBundle(WP_LOADER_KEY);
            getSupportLoaderManager().restartLoader(WP_LOADER_ID, bundle, CategoryActivity.this);

            Log.v(TAG, "Ok, we reset the loader.");

        }
    }

    //--------------- Menu methods override ------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void whichMessageMustShow() {

        if (methods.checkNetwork()) {

            methods.weCanNotGetData(mRecyclerView, mErrorMessage, mImageView, mTrayAgianButton);

        } else {

            methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgianButton);

        }

    }

}
