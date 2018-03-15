package com.redfirelab.android.wpmobileapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.redfirelab.android.wpmobileapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Pouria on 11/27/2017.
 * wpMApp project.
 */

public class WPPreferences {

    //Save list of favorite post's id
    public static void saveFavoritePosts(Context context, String data) {

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preference.edit();

        //Set the values
        Set<String> set = new HashSet<>();
        Set<String> oldSet = preference.getStringSet(String.valueOf(R.string.favorite_posts_key), null);

        set.add(data);

        if (oldSet != null) {
            set.addAll(oldSet);
        }

        Log.v("WP PREFERENCE ", "save favorate post ");

        editor.putStringSet(String.valueOf(R.string.favorite_posts_key), set);
        editor.apply();


    }

    //Get a list of favorite post's id
    public static List<String> getFavoritePosts(Context context) {

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> set = preference.getStringSet(String.valueOf(R.string.favorite_posts_key), null);

        List<String> favPosts = new ArrayList<>();

        Log.v("WP PREFERENCE ", "get Favorite Posts");

        if (set != null) {

            favPosts.addAll(set);

        }

        return favPosts;

    }

    //Remove one item from favorite list then update the list and save new data's
    public static void removeFavoritePosts(Context context, int id) {

        Log.v("WP PREFERENCE ", "remove fav posts");

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preference.edit();

        Set<String> set = preference.getStringSet(String.valueOf(R.string.favorite_posts_key), null);

        List<String> favPosts = new ArrayList<>();

        if (set != null) {

            favPosts.addAll(set);

            Log.v("WP PREFERENCE ", "ID OF REMOVE FAVORITE POSTS => " + id);
            Log.v("WP PREFERENCE-REMOVE", " the index ---> " + favPosts.indexOf(String.valueOf(id)));

            if (favPosts.indexOf(String.valueOf(id)) != -1) {

                favPosts.remove(favPosts.indexOf(String.valueOf(id)));

                set.clear();

                set.addAll(favPosts);

                editor.putStringSet(String.valueOf(R.string.favorite_posts_key), set);

                editor.apply();

            }

        }

    }

    //Get the value of post title
    public static String getSizeOfPostTitle(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.pref_size_title_key), context.getString(R.string.pref_size_title_default));

    }

    public static boolean getPostTitleSizeChange(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_title_size_is_changed), false);

    }

    public static void savePostTitleSizeChange(Context context, boolean change){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.pref_title_size_is_changed), change);
        editor.apply();

    }

    public static boolean getPostDescriptionSizeChange(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_description_size_is_changed), false);

    }

    public static void savePostDescriptionSizeChange(Context context, boolean change){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.pref_description_size_is_changed), change);
        editor.apply();

    }

    public static boolean getPostContentSizeChange(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_content_size_is_changed), false);

    }

    public static void savePostContentSizeChange(Context context, boolean change){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.pref_content_size_is_changed), change);
        editor.apply();

    }

    //Get size of post description
    public static String getSizeOfPostDescription(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.pref_size_desc_key), context.getString(R.string.pref_size_desc_default));

    }

    //Get size of content post
    public static String getSizeOfContentPost(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.pref_size_content_key), context.getString(R.string.pref_size_content_default));

    }

    //Save the latest published post for comparison
    public static void saveLastPostId(Context context, int postId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String lastId = context.getString(R.string.save_last_post_id);
        editor.putInt(lastId, postId);
        editor.apply();

    }

    //Get the latest published post id, we saved before
    public static int getLastPostId(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(context.getString(R.string.save_last_post_id), 0);

    }

    //Save the latest published post title
    public static void saveLastPostTitle(Context context, String postTitle){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.save_last_post_title), postTitle);
        editor.apply();

    }

    //Get the latest published post title, we saved before
    public static String getLastPostTitle(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.save_last_post_title), null);

    }

    //Get the Address of site, user enter in the setting
    public static String getSiteAddress(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(context.getString(R.string.pref_site_address_key),
                context.getString(R.string.pref_site_address));

    }

    public static boolean getSiteAddressIsChange(Context context){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_site_address_is_changed), false);

    }

    public static void saveSiteAddressIsChange(Context context, boolean change){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.pref_site_address_is_changed), change);
        editor.apply();

    }

    //Tell us user want have notification or not?, user can change it in the setting
    public static boolean areNotificationsEnabled(Context context) {
        /* Key for accessing the preference for showing notifications */
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);

        /*
         * In Sunshine, the user has the ability to say whether she would like notifications
         * enabled or not. If no preference has been chosen, we want to be able to determine
         * whether or not to show them. To do this, we reference a bool stored in bools.xml.
         */
        boolean shouldDisplayNotificationsByDefault = context
                .getResources()
                .getBoolean(R.bool.show_notifications_by_default);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        /* If a value is stored with the key, we extract it here. If not, use a default. */
        boolean shouldDisplayNotifications = sp
                .getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);

        Log.v("WPPreference", "areNotificationsEnable => " + shouldDisplayNotifications);

        return shouldDisplayNotifications;
    }

}
