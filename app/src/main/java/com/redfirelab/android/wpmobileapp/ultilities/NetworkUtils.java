package com.redfirelab.android.wpmobileapp.ultilities;

/*
  Created by Pouria on 11/21/2017.
  wpMApp project.
 */

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private final static String WORDPRESS_CATEGORY_URL =

            "/wp-json/wp/v2/categories";

    private final static String WORDPRESS_POSTS_URL =

            "/wp-json/wp/v2/posts";

    private final static String WORDPRESS_POSTS_CATEGORY_URL =

            "/wp-json/wp/v2/posts?categories=";

    private final static String PARAM_PAGE = "page";

    /**
     * Builds the URL used to query Wordpress Category.
     *
     * @return The URL to use to query the Wordpress.
     */
    public static URL buildUrlCategory(String address, String page) {
        Uri builtUri = Uri.parse(address + WORDPRESS_CATEGORY_URL)
                .buildUpon()
                .appendQueryParameter(PARAM_PAGE, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query Wordpress Category.
     *
     * @return The URL to use to query the Wordpress.
     */
    public static URL buildUrlForGetPostsOfCategory(String address, String page, String id) {
        Uri builtUri = Uri.parse(address + WORDPRESS_POSTS_CATEGORY_URL + id)
                .buildUpon()
                .appendQueryParameter(PARAM_PAGE, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Builds the URL used to query Wordpress Posts.
     *
     * @return The URL to use to query the Wordpress.
     */
    public static URL buildUrlPosts(String address, String page) {
        Uri builtUri = Uri.parse(address + WORDPRESS_POSTS_URL).buildUpon()
                .appendQueryParameter(PARAM_PAGE, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}
