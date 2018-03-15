package com.redfirelab.android.wpmobileapp.sync;

import android.content.Context;
import android.util.Log;

import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.NetworkUtils;
import com.redfirelab.android.wpmobileapp.ultilities.NotificationUtils;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressJsonUtils;

import java.net.URL;

/**
 * Created by Pouria on 12/10/2017.
 * wpMApp project.
 * <p>
 * All works must do in background in here
 * This is a helper class
 */

public class WordpressSyncTask {

    /**
     * Performs the network request for updated Wordpress, parses the JSON from that request, and
     * inserts the new post information into our ContentProvider. Will notify the user that new
     * post has been loaded if the user hasn't been notified of the post within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */

    synchronized static void syncWordpress(Context context) {

        boolean notificationsEnabled = WPPreferences.areNotificationsEnabled(context);

        Log.v("WordpressSyncTask.java", "the notification is => " + notificationsEnabled);

        if (notificationsEnabled) {

            int lastPostId;

            try {

                URL postRequestUrl = NetworkUtils.buildUrlPosts(WPPreferences.getSiteAddress(context), "1");


                 /* Use the URL to retrieve the JSON */
                String jsonPostResponse = NetworkUtils.getResponseFromHttpUrl(postRequestUrl);


                /* Parse the JSON into a list of post */
                int postId = WordpressJsonUtils.getTheLastPostId(jsonPostResponse);

                //We get title of the latest post published.
                String titleOfNewPost = WordpressJsonUtils.getTheLastPostTitle(jsonPostResponse);

                //If the post id we get from json is not 0, that mean no problem of the json data
                if (postId != 0) {

                    /*
                        If our shared preference is not initialized before do it now,
                         else put the id we saved before in the lastPostId variable for compare
                    */
                    if (WPPreferences.getLastPostId(context) == 0) {

                        lastPostId = WordpressJsonUtils.getTheLastPostId(jsonPostResponse);
                        WPPreferences.saveLastPostId(context, lastPostId);

                    } else {

                        lastPostId = WPPreferences.getLastPostId(context);

                    }

                    //Now we compare id of latest post published with id we save before
                    if (postId != lastPostId) {

                        WPPreferences.saveLastPostId(context, postId);
                        WPPreferences.saveLastPostTitle(context, titleOfNewPost);

                        Log.v("WordpressSyncTask.java", "We get the lastId and replace it => " + lastPostId);

                        NotificationUtils.remindUserNewPostReleased(context);

                        Log.v("WordpressSyncTask.java", " And we send the notification... ");

                    }

                }

            } catch (Exception e) {
            /* Server probably invalid */
                e.printStackTrace();
            }

        }

    }

}
