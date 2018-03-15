package com.redfirelab.android.wpmobileapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.sync.WordpressSyncUtils;
import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;
import com.redfirelab.android.wpmobileapp.ultilities.NetworkUtils;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressJsonUtils;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressPostData;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PostAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<WordpressPostData>> {

    //used for Log's tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int WP_LOADER_ID = 0;
    private static final String WP_LOADER_KEY = "RESULT";
    private static final String CATEGORY_PUT_EXTRA_NAME = "CATEGORY_ID";

    //defined
    private TextView mErrorMessage;
    private RecyclerView mRecyclerView;
    private PostAdapter postAdapter;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayOut;
    private ImageView mImageView;
    private TextView mLoadingText;
    private Button mTrayAgainButton;

    //a list for keep track data we get from json api
    List<WordpressPostData> wordpressListData;

    private Bundle bundle = new Bundle();

    //helper class
    private MethodsUtils methods = new MethodsUtils(MainActivity.this);

    //use this variable to keep track of page of post's, like wp-json/wp/v2/posts?page=1 <----
    int counter = 1;
    //int categoryCounter = 1;

    //use this variable to tell program user change category, false mean not change, true mean changing
    private boolean useCategoryPostsUrl = false;

    /*
    * some times user enter wrong host name in the setting panel
    * so when we got this error for this action we need to tell program we have
    * this error and show different error message
    *
    * */
    private boolean unknownHostExceptionGlobal = false;

    //keep category id
    private String categoryId;

    //--------------------------------------------
    //this variables use for create menu
    ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<>();

    /*
    * when user reach end of the list we need to know,
    * so we use this variable to tell program we user reach end of the list
    * now we must join the previews data with new data
    *
    * */
    boolean endOfTheList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "on Create call too");

        //for use custom action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.main_app_name_activity_title);
        }

        // ---------------- initialize ----------------------------------
        mRecyclerView = findViewById(R.id.rv_main_activity);
        mErrorMessage = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mImageView = findViewById(R.id.iv_wifi);
        mLoadingText = findViewById(R.id.tv_loading_text);
        mTrayAgainButton = findViewById(R.id.but_try_again);
        mSwipeRefreshLayOut = findViewById(R.id.srl_main_activity);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //------------------- add item's to mNavItems for menu ---------------------------------------------------------
        mNavItems.add(new NavItem(getString(R.string.favorite_posts_title_activity), getString(R.string.menu_favorites_Description_text), R.drawable.ic_favorite_48px));
        mNavItems.add(new NavItem(getString(R.string.category_activity_title), getString(R.string.menu_category_Description_text), R.drawable.ic_category));
        mNavItems.add(new NavItem(getString(R.string.setting_activity_title), getString(R.string.menu_setting_Description_text), R.drawable.ic_settings_48px));
        mNavItems.add(new NavItem(getString(R.string.about_us), getString(R.string.menu_about_us_Description_text), R.drawable.ic_tea));

        //--------------- set adapter for Drawer ------------------------------------------------
        mDrawerList = findViewById(R.id.left_drawer);

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);

        mDrawerList.setAdapter(adapter);

        // ------------------------- create layout for recycler view used "Post Adapter" -------------------------------------
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        postAdapter = new PostAdapter(MainActivity.this);

        mRecyclerView.setAdapter(postAdapter);

        /*
        * ------------- get intent information, check network and set bundle ------------
        *
        * if we have  CATEGORY_PUT_EXTRA_NAME in our intent that is mean
        * user change the category, so we need to get id from intent and set useCategoryPostsUrl = true
        * and reset the counter. that mean counter = 1;
        *
        * then we must check network and set new bundle
        *
        * else
        *
        * check the net work and initialize bundle for first time, posts?page=1
        *
        * */
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getString(CATEGORY_PUT_EXTRA_NAME) != null) {

            categoryId = extras.getString(CATEGORY_PUT_EXTRA_NAME);
            useCategoryPostsUrl = true;
            counter = 1;

            checkNetworkConnectionAndSetBundle(true);

            //reset the intent
            extras.clear();
            Log.e(TAG, "get extra from category");

        } else {

            useCategoryPostsUrl = false;
            checkNetworkConnectionAndSetBundle(true);
            Log.e(TAG, "no extra and we set bundle");

        }

        //initialize loader
        getSupportLoaderManager().initLoader(WP_LOADER_ID, bundle, this);

        /*
        *
        * Call WordpressSyncUtils's scheduleNewPostReminder method
        *
        * this schedule check the site and if new post published make alert for user
        *
        * */
        WordpressSyncUtils.scheduleNewPostReminder(this);

        //-------------------------------------------------------------

        /*
          We check the scrolling recycler view
          If user reach end of the list, we want send request to load more data
          */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {

                    //Is this the end of the list?
                    if (!recyclerView.canScrollVertically(1)) {

                        Log.e(TAG, "the end of the list");

                    /*
                    * When user reach the end of the list and request for more data
                    * We need to know loading data in the process or done
                    * If we are in the processing of loading more data and user swipe down again
                    * We must don't care of that, but if processing is done and user want more we do that
                    * For detect that, we check if progressbar (mLoadingIndicator) is visible that mean we are in process of loading data
                    * And if invisible that mean processing is done and user can want more data
                    *
                    * */

                        if (checkNetworkConnectionAndSetBundle(false)) {

                            endOfTheList = true;

                            if (mLoadingIndicator.getVisibility() == View.INVISIBLE) {

                                //we want posts from special category?
                                if (useCategoryPostsUrl) {

                                    counter++;

                                    bundle.putString(WP_LOADER_KEY, NetworkUtils.buildUrlForGetPostsOfCategory(WPPreferences.getSiteAddress(MainActivity.this), String.valueOf(counter), categoryId).toString());

                                    getSupportLoaderManager().restartLoader(WP_LOADER_ID, bundle, MainActivity.this);

                                    Log.v(TAG, "reach End of the List And we must get more data, Category counter => " + counter);

                                } else {

                                    counter++;

                                    bundle.putString(WP_LOADER_KEY, NetworkUtils.buildUrlPosts(WPPreferences.getSiteAddress(MainActivity.this), String.valueOf(counter)).toString());

                                    Log.e(TAG, "the url -> " + NetworkUtils.buildUrlPosts(WPPreferences.getSiteAddress(MainActivity.this), String.valueOf(counter)));

                                    getSupportLoaderManager().restartLoader(WP_LOADER_ID, bundle, MainActivity.this);

                                    Log.e(TAG, "reach End of the List And we must get more data, counter => " + counter);

                                }

                            }

                        } else {

                            endOfTheList = false;
                            methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgainButton);

                        }

                    }
                }

            }
        });

        //---------------- Swipe Refresh Layout ------------------
        mSwipeRefreshLayOut.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.e(TAG, "User pull down for refresh.");

                if (checkNetworkConnectionAndSetBundle(false)) {

                    if (mLoadingIndicator.getVisibility() == View.INVISIBLE) {

                        resetTheLoader();

                        Log.e(TAG, "The ScrollView is pull down, this means user want refresh");
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


        //---------------------- click listener for try again button --------------------
        mTrayAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (methods.checkNetwork()) {
                    methods.hideViewsErrorMessage(mErrorMessage, mImageView, mLoadingText, mTrayAgainButton);
                    resetTheLoader();
                    Log.e(TAG, "on click try again");
                }


            }
        });
        //-------------------- set onclick for drawer menu -------------------------------
        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {

                    Intent intent = new Intent(MainActivity.this, SqlPostActivity.class);
                    startActivity(intent);

                } else if (position == 1) {

                    if (checkNetworkConnectionAndSetBundle(false)) {

                        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                        startActivity(intent);

                    } else {

                        methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgainButton);

                    }

                } else if (position == 2) {

                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);

                } else if (position == 3) {

                    Toast.makeText(MainActivity.this, "What do you want to know", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);

                }

                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });


        //---- drawer toggle open and close --------------
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        /*
        * we get the size of title and description from user
        * so first time app is coming up we need get size's from share preference and apply to out text box
        * */
        setUpSharePreferences();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "on start");

        //user change the size of posts title ?
        if (WPPreferences.getPostTitleSizeChange(this)) {

            Log.e(TAG, "title is changed, onstart");
            WPPreferences.savePostTitleSizeChange(this, false);
            postAdapter.updateTitleSize(Float.parseFloat(WPPreferences.getSizeOfPostTitle(this)));


        }

        //user change the description of the post's ?
        if (WPPreferences.getPostDescriptionSizeChange(this)) {

            Log.e(TAG, "site is Desc, onstart");
            WPPreferences.savePostDescriptionSizeChange(this, false);
            postAdapter.updateDescriptionSize(Float.parseFloat(WPPreferences.getSizeOfPostDescription(this)));


        }

        //user change the site address ? if address is changing we need to reset loader
        if (WPPreferences.getSiteAddressIsChange(this)) {

            methods.hideViewsErrorMessage(mErrorMessage, mImageView, mLoadingText, mTrayAgainButton);

            Log.e(TAG, "site is changed, onstart");

            WPPreferences.saveSiteAddressIsChange(this, false);
            WPPreferences.saveLastPostId(this, 0);

            resetTheLoader();

        }

    }

    /*
    * when we send notification and user click on it
    * we need to reset the loader to show new post
    * */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.hasExtra("RESET_THE_LOADER")) {

            int theNumber = intent.getIntExtra("RESET_THE_LOADER", 1);

            if (theNumber == 1) {

                resetTheLoader();

                intent.removeExtra("RESET_THE_LOADER");

                Log.e(TAG, "Ok we got the intent and it was 1, so we reset the loader and put extra 0");

            }

        } else {

            Log.e(TAG, "The intent is null.");

        }

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<WordpressPostData>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<WordpressPostData>>(this) {

            List<WordpressPostData> mWpjson;
            boolean unknownHostException = false;

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
                            mLoadingText.setVisibility(View.VISIBLE);
                            Log.e(TAG, "I am here #1");

                        }

                    }

                    forceLoad();
                    Log.v(TAG, "mWpjson is null");

                }

            }

            @Override
            public List<WordpressPostData> loadInBackground() {

                String searchQueryUrl = args.getString(WP_LOADER_KEY);

                if (searchQueryUrl == null || TextUtils.isEmpty(searchQueryUrl)) {
                    Log.v(TAG, "searchQueryUrl == null || TextUtils.isEmpty(searchQueryUrl) so return");

                    return null;

                }

                URL searchQuery;
                Log.e(TAG, "the args -> " + searchQueryUrl);

                try {

                    searchQuery = new URL(searchQueryUrl);
                    /*
                    *
                    * if app is running first time or reset the loader
                    * that's mean we want clear the list and show new data
                    *
                    * but if we reach end of the list and we have data in the wordpressListData
                    * that mean we want join the previews data and new data
                    *
                    * "endOfTheList" -> to make sure that the data is gathered only when we reach the end of the list.
                    *
                    * */
                    if (wordpressListData == null) {

                        Log.v(TAG, "wordpressListData == null");

                        wordpressListData = WordpressJsonUtils.getSimplePostsFromJson(NetworkUtils.getResponseFromHttpUrl(searchQuery));

                    } else if (endOfTheList) {

                        Log.v(TAG, "wordpressListData != null");

                        if ((WordpressJsonUtils.getSimplePostsFromJson(NetworkUtils.getResponseFromHttpUrl(searchQuery))) != null) {

                            wordpressListData.addAll((WordpressJsonUtils.getSimplePostsFromJson(NetworkUtils.getResponseFromHttpUrl(searchQuery))));

                            Log.e(TAG, "new link is -> " + searchQuery);
                            endOfTheList = false;
                        }

                    }

                    Log.v(TAG, "The size of List => " + wordpressListData.size());

                    Log.v(TAG, "in background try");

                    return wordpressListData;

                } catch (UnknownHostException e) {

                    unknownHostException = true;
                    Log.e(TAG, "Unknown Host Exception");

                    return null;

                } catch (IOException e) {

                    Log.e(TAG, "IOException message -> " + e.getMessage());

                    return wordpressListData;

                } catch (JSONException e) {

                    Log.e(TAG, "Error is -> " + e.getMessage());
                    return null;

                }
            }

            @Override
            public void deliverResult(List<WordpressPostData> wpdata) {

                //if we have IO exception from un known host exception do this things
                if (unknownHostException) {

                    methods.showUnknownHostException(mRecyclerView, mLoadingIndicator, mErrorMessage, mLoadingText, mImageView, mTrayAgainButton);

                    unknownHostException = false;
                    unknownHostExceptionGlobal = true;

                }

                mWpjson = wpdata;

                Log.v(TAG, "deiliver result " + wpdata);
                super.deliverResult(wpdata);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<List<WordpressPostData>> loader, List<WordpressPostData> data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayOut.setRefreshing(false);
        mLoadingText.setVisibility(View.INVISIBLE);

        Log.v(TAG, "onLoadFinished");

        if (data != null && !data.isEmpty()) {

            methods.weHaveDataShowViews(mRecyclerView, mErrorMessage, mImageView, mLoadingText, mTrayAgainButton);
            postAdapter.setWpdata(data);

            Log.v(TAG, "data != null && !data.isEmpty(), showWPPosts();  postAdapter.setWpdata(data);");


            /*
            * first time app is installed, notification id is 0, and we need save the last post id
            * so we check if WPPreferences.getLastPostId is 0, that mean app is first install
            * get the last post id and put in in
            * */
            if (WPPreferences.getLastPostId(MainActivity.this) == 0) {

                WPPreferences.saveLastPostId(MainActivity.this, data.get(0).getpId());
            }

        } else {

            Log.v(TAG, "data == null && data.isEmpty(), so showMessageCantGetData()");

            whichMessageMustShow();
            Log.e(TAG, "I am here #3");

        }


    }

    @Override
    public void onLoaderReset(Loader<List<WordpressPostData>> loader) {

    }

    //--------------------------------- menu ---------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.main_menu) {

            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

            if (drawerOpen) {

                mDrawerLayout.closeDrawer(Gravity.END);

            } else {

                mDrawerLayout.openDrawer(Gravity.END);

            }

        }

        return super.onOptionsItemSelected(item);
    }

    //----------- Post postAdapter click listener method, override for implement main activity -----------------
    @Override
    public void onListItemClick(WordpressPostData wordpressPostData) {

        Intent intent = new Intent(this, PostDetailActivity.class);

        //TODO chose use bundle or serialize class, or parcelble class
        // this time i use serialize class don't forgot it

        intent.putExtra(Intent.EXTRA_TEXT, wordpressPostData);

        startActivity(intent);

    }

    //-------------------------------- Helper method ----------------------------------------------------

    //Reset loader to default
    public void resetTheLoader() {

        if (checkNetworkConnectionAndSetBundle(false)) {

            wordpressListData = null;

            bundle = methods.createPostBundle(WP_LOADER_KEY);

            counter = 1;

            useCategoryPostsUrl = false;
            categoryId = null;

            getSupportLoaderManager().restartLoader(WP_LOADER_ID, bundle, MainActivity.this);

            Log.v(TAG, "Ok, we reset the loader.");

        }
    }

    //Check internet connection
    public boolean checkNetworkConnectionAndSetBundle(boolean setBundle) {

        // If there is a network connection, fetch data
        if (methods.checkNetwork()) {

            if (setBundle) {

                if (useCategoryPostsUrl) {

                    bundle = methods.createCategoryBundleToGetPosts(WP_LOADER_KEY, categoryId);
                    Log.e(TAG, "method.createCategoryBundle");
                } else {

                    bundle = methods.createPostBundle(WP_LOADER_KEY);
                    Log.e(TAG, "method.createPostBundle");
                }

                methods.weHaveDataShowViews(mRecyclerView, mErrorMessage, mImageView, mLoadingText, mTrayAgainButton);

            }

            Log.v(TAG, "We check the network, is connected, and we do postSearchQuery() and showWPPosts()");
            return true;

        } else {

            methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgainButton);

            Log.v(TAG, "We check the network, is not connected, and we do showMessageNoInterNet()");
            return false;

        }

    }

    //Announce post adapter some settings is change
    private void setUpSharePreferences() {

        postAdapter.updateTitleSize(Float.parseFloat(WPPreferences.getSizeOfPostTitle(this)));
        postAdapter.updateDescriptionSize(Float.parseFloat(WPPreferences.getSizeOfPostDescription(this)));

    }

    //Decision making which message must show, no internet connection or cant get data
    private void whichMessageMustShow() {

        if (checkNetworkConnectionAndSetBundle(false) && unknownHostExceptionGlobal) {

            methods.showUnknownHostException(mRecyclerView, mLoadingIndicator, mErrorMessage, mLoadingText, mImageView, mTrayAgainButton);
            unknownHostExceptionGlobal = false;

        } else if (checkNetworkConnectionAndSetBundle(false) && !unknownHostExceptionGlobal) {

            methods.weCanNotGetData(mRecyclerView, mErrorMessage, mImageView, mTrayAgainButton);

        } else {

            methods.noInternetConnection(mRecyclerView, mLoadingIndicator, mErrorMessage, mImageView, mTrayAgainButton);

        }

    }

//--------------------- inner class's for Drawer menu --------------------
    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.activity_drawer_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = view.findViewById(R.id.title);
            TextView subtitleView = view.findViewById(R.id.subTitle);
            ImageView iconView = view.findViewById(R.id.icon);

            titleView.setText(mNavItems.get(position).mTitle);
            subtitleView.setText(mNavItems.get(position).mSubtitle);
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (!drawerOpen) {

                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.ic_menu_animatable_reverce, null);
                menu.findItem(R.id.main_menu).setIcon(drawable);
                drawable.start();

            } else {

                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.ic_menu_animatable, null);
                menu.findItem(R.id.main_menu).setIcon(drawable);
                drawable.start();

            }

        } else {

            if (!drawerOpen) {

                menu.findItem(R.id.main_menu).setIcon(R.drawable.ic_menu_vector);

            } else {

                menu.findItem(R.id.main_menu).setIcon(R.drawable.ic_menu_rotate_48px);

            }

        }

        //menu.findItem(R.id.main_menu).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
}


