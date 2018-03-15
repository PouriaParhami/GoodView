package com.redfirelab.android.wpmobileapp.ultilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.R;
import com.redfirelab.android.wpmobileapp.data.WPPreferences;

import org.w3c.dom.Text;

/**
 * Created by Pouria on 1/3/2018.
 * wpMApp project.
 */

public class MethodsUtils {

    public Context context;

    public MethodsUtils(Context context) {
        this.context = context;
    }

    private Bundle bundle = new Bundle();

    //Create static bundle for posts
    public Bundle createPostBundle(String loaderKey) {

        bundle.putString(loaderKey, NetworkUtils.buildUrlPosts(WPPreferences.getSiteAddress(context), "1").toString());
        return bundle;
    }

    public Bundle createCategoryBundle(String loaderKey) {

        bundle.putString(loaderKey, NetworkUtils.buildUrlCategory(WPPreferences.getSiteAddress(context), "1").toString());
        return bundle;

    }

    public Bundle createCategoryBundleToGetPosts(String loaderKey, String categoryId) {

        bundle.putString(loaderKey, NetworkUtils.buildUrlForGetPostsOfCategory(WPPreferences.getSiteAddress(context), "1", categoryId).toString());
        return bundle;

    }

    public boolean checkNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();

    }

    public void noInternetConnection(RecyclerView mRecyclerView, ProgressBar mLoadingIndicator, TextView mErrorMessage, ImageView mImageView, Button mTrayAgainButton) {

        mRecyclerView.setVisibility(View.INVISIBLE);

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mErrorMessage.setText(R.string.error_no_internet);
        mErrorMessage.setVisibility(View.VISIBLE);

        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageResource(R.drawable.ic_signal_wifi_off_48px);

        mTrayAgainButton.setVisibility(View.VISIBLE);

    }

    public void weCanNotGetData(RecyclerView mRecyclerView, TextView mErrorMessage, ImageView mImageView, Button mTrayAgainButton) {

        mRecyclerView.setVisibility(View.INVISIBLE);

        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(R.string.data_error_message);

        mImageView.setImageResource(R.drawable.ic_cloud_off_48px);

        mImageView.setVisibility(View.VISIBLE);

        mTrayAgainButton.setVisibility(View.VISIBLE);

    }

    public void weHaveDataShowViews(RecyclerView mRecyclerView, TextView mErrorMessage, ImageView mImageView, TextView mLoadingText, Button mTrayAgainButton) {

        mRecyclerView.setVisibility(View.VISIBLE);

        mErrorMessage.setVisibility(View.INVISIBLE);

        mImageView.setVisibility(View.INVISIBLE);

        mLoadingText.setVisibility(View.INVISIBLE);

        mTrayAgainButton.setVisibility(View.INVISIBLE);

    }

    public void hideViewsErrorMessage(TextView mErrorMessage, ImageView mImageView, TextView mLoadingText, Button mTrayAgainButton) {

        mErrorMessage.setVisibility(View.INVISIBLE);

        mImageView.setVisibility(View.INVISIBLE);

        mLoadingText.setVisibility(View.INVISIBLE);

        mTrayAgainButton.setVisibility(View.INVISIBLE);

    }

    public void showUnknownHostException(RecyclerView mRecyclerView, ProgressBar mLoadingIndicator, TextView mErrorMessage, TextView mLoadingText, ImageView mImageView, Button mTrayAgainButton) {

        mRecyclerView.setVisibility(View.INVISIBLE);

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        mLoadingText.setVisibility(View.INVISIBLE);

        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(R.string.unknown_host_exception);

        mImageView.setImageResource(R.drawable.ic_server);

        mImageView.setVisibility(View.VISIBLE);

        mTrayAgainButton.setVisibility(View.VISIBLE);

    }

    public void setActivityTitle(ActionBar actionBar, int title) {

        if (actionBar != null) {

            actionBar.setDisplayShowCustomEnabled(true);

            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(context);

            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.activity_custom_action_bar, null);

            TextView toolbarText = v.findViewById(R.id.toolbar_title);
            toolbarText.setText(title);

            actionBar.setCustomView(v);

        }

    }

}
