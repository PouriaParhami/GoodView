package com.redfirelab.android.wpmobileapp.ultilities;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle Wordpress Rest API JSON data.
 */
public final class WordpressJsonUtils {

    private static final String TAG = WordpressJsonUtils.class.getSimpleName();

    //Get information from category
    public static List<WordpressCategoryData> getSimpleCategoryFromJson(String jsonStr)
            throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        List<WordpressCategoryData> categoryData = new ArrayList<>();
        JSONArray category = new JSONArray(jsonStr);

        int id;
        String name;
        int count;

        for (int i = 0; i < category.length(); i += 1) {

            //If category have post ( count > 0) then add it to the list
            if (category.getJSONObject(i).has("count") && category.getJSONObject(i).getInt("count") > 0) {

                count = category.getJSONObject(i).getInt("count");

                if (category.getJSONObject(i).has("name") && category.getJSONObject(i).getString("name").length() != 0) {

                    name = category.getJSONObject(i).getString("name");

                } else {

                    name = "this category has no name.";

                }

                if (category.getJSONObject(i).has("id") && category.getJSONObject(i).getInt("id") > 0) {

                    id = category.getJSONObject(i).getInt("id");

                } else {

                    id = 0;

                }

                categoryData.add(new WordpressCategoryData(id, count, name));

            }

        }

        return categoryData;
    }

    //Get posts information
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static List<WordpressPostData> getSimplePostsFromJson(String jsonStr) throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        List<WordpressPostData> postData = new ArrayList<>();
        JSONArray posts = new JSONArray(jsonStr);

        //Title of post, from json object
        String titleRender = "No title";

        //Description of post, from json object
        String excerptRender = "No Content.";

        int postId = 0;

        //Picture of post, from json object
        JSONArray featuredMedia;
        String picHref = "";
        String contentRender = "no content";

        //Link of the post
        String postLink;

        String baseUrlString = null;

        for (int i = 0; i < posts.length(); i += 1) {

            /*
            *
            * if id is exist in json object
            * put it in the postId then put it in to WordpressPostData
            *
            * else show error in log.
            *
            * */

            if (posts.getJSONObject(i).has("id") && posts.getJSONObject(i).getInt("id") > 0) {

                postId = posts.getJSONObject(i).getInt("id");

            } else {

                Log.e(TAG, "Post id (id) is not exist in json object");

            }

            /*
            *
            * if title is exist in json object
            * get rendered from it, then put it in to WordpressPostData
            *
            * if title or rendered is not exist show me error in log.
            *
            * */

            if (posts.getJSONObject(i).has("title")) {

                if (posts.getJSONObject(i).getJSONObject("title").has("rendered") &&
                        posts.getJSONObject(i).getJSONObject("title").getString("rendered").length() > 0) {

                    titleRender = posts.getJSONObject(i).getJSONObject("title").getString("rendered");

                } else {

                    Log.e(TAG, "post title, dont have rendered object.");

                }

            } else {

                Log.e(TAG, "post don have (title) in json object");

            }


            /*
            *
            * if excerpt exist in json object, get rendered from it
            * and if each one is not exist show error in log
            *
            * */

            if (posts.getJSONObject(i).has("excerpt")) {

                if (posts.getJSONObject(i).getJSONObject("excerpt").has("rendered") &&
                        posts.getJSONObject(i).getJSONObject("excerpt").getString("rendered").length() > 0) {

                    if (posts.getJSONObject(i).getJSONObject("excerpt").getString("rendered").length() == 0) {

                        excerptRender = "محتوایی وارد نشده است.";

                    } else if (posts.getJSONObject(i).getJSONObject("excerpt").getString("rendered").length() <= 165) {

                        excerptRender = posts.getJSONObject(i).getJSONObject("excerpt").getString("rendered");

                    } else {

                        excerptRender = posts.getJSONObject(i).getJSONObject("excerpt").getString("rendered").substring(0, 165);

                    }


                } else {

                    Log.e(TAG, "rendered of excerpt is not exist");

                }

            } else {

                Log.e(TAG, "excerpt is not exist in json object");

            }

            /*
            *
            * if link exist in json object, put it in the postLink variable
            * and if is not exist show error in log
            *
            * */

            if (posts.getJSONObject(i).has("link")) {

                postLink = posts.getJSONObject(i).getString("link");

            } else {

                postLink = "http://darkoobweb.com";
                Log.e(TAG, "post link (link) is not exist in json object");

            }

            /*
            *
            * if content exist in json object, get rendered from it
            * and if is not exist show error in log
            *
            * */

            if (posts.getJSONObject(i).has("content")) {

                posts.getJSONObject(i).getJSONObject("content");

                if (posts.getJSONObject(i).getJSONObject("content").has("rendered")
                        && posts.getJSONObject(i).getJSONObject("content").getString("rendered").length() > 0) {

                    contentRender = posts.getJSONObject(i).getJSONObject("content").getString("rendered");

                } else {

                    Log.e(TAG, "content dont have rendered");

                }


            } else {

                Log.e(TAG, "content is not exist in json object");

            }
            /*
            *
            * if _links exist in json object, get wp:featuredmedia from it
            * and if is not exist show error in log
            *
            * */

            if (posts.getJSONObject(i).has("_links")) {

                if (posts.getJSONObject(i).getJSONObject("_links").has("wp:featuredmedia")) {

                    featuredMedia = posts.getJSONObject(i).getJSONObject("_links").getJSONArray("wp:featuredmedia");

                    for (int b = 0; b < featuredMedia.length(); b += 1) {

                        URL newUrl = null;

                        if (detectPictureLink(featuredMedia.getJSONObject(b).getString("href"))) {

                            picHref = featuredMedia.getJSONObject(b).getString("href");

                        } else {

                            try {

                                newUrl = new URL(featuredMedia.getJSONObject(b).getString("href"));

                            } catch (MalformedURLException e) {

                                Log.e(TAG, "Something wrong " + e);

                            }

                            try {

                                if (newUrl != null) {

                                    picHref = getSimplePictureFromJson(NetworkUtils.getResponseFromHttpUrl(newUrl));

                                    if (picHref.isEmpty() || picHref == null) {

                                        picHref = "http://darkoobweb.com/wp-content/themes/darkoobweb/images/logo.png";

                                    }

                                } else {

                                    Log.e(TAG, "newUrl is null, so we use darkoobweb picture link");

                                }


                            } catch (IOException e) {

                                e.printStackTrace();

                            }

                        }

                    }

                } else {

                    picHref = "http://darkoobweb.com/wp-content/themes/darkoobweb/images/logo.png";

                }

            } else {

                picHref = "http://darkoobweb.com/wp-content/themes/darkoobweb/images/logo.png";
                Log.e(TAG, "_link is not exist in json object");

            }

            if (posts.getJSONObject(i).has("guid") && posts.getJSONObject(i).getJSONObject("guid").has("rendered") &&
                    posts.getJSONObject(i).getJSONObject("guid").getString("rendered").length() != 0) {

                try {

                    URL baseUrl = new URL(posts.getJSONObject(i).getJSONObject("guid").getString("rendered"));

                    baseUrlString = baseUrl.getProtocol() + "://" + baseUrl.getHost();


                } catch (MalformedURLException e) {

                    baseUrlString = "";
                    Log.e("json util", "ye margesh zade");
                }

            }

            postData.add(new WordpressPostData(postId, titleRender, excerptRender, picHref, contentRender, postLink, baseUrlString));
        }


        return postData;

    }

    //Get picture url of post
    private static String getSimplePictureFromJson(String jsonStr) throws JSONException {


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        String picReturn = null;


        try {

            JSONObject pic = new JSONObject(jsonStr);

            if (pic.has("guid")) {

                if (pic.getJSONObject("guid").has("rendered")) {

                    picReturn = pic.getJSONObject("guid").getString("rendered");

                    Log.v(TAG, "the URL: " + picReturn);
                }

            }

            return picReturn;

        } catch (Exception e) {

            Log.v("the default URL:", jsonStr);

            return jsonStr;

        }

    }

    //Get last post id
    public static int getTheLastPostId(String jsonStr) throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonStr)) {
            return 0;
        }

        JSONArray posts = new JSONArray(jsonStr);

        if (posts.getJSONObject(0).has("id") && posts.getJSONObject(0).getInt("id") > 0) {

            return posts.getJSONObject(0).getInt("id");

        }

        return 0;

    }

    public static String getTheLastPostTitle(String jsonStr) {

        if (TextUtils.isEmpty(jsonStr)) {
            return "don have title";
        }

        JSONArray posts = null;

        try {

            posts = new JSONArray(jsonStr);

        } catch (JSONException e) {

            Log.e(TAG, e.getMessage());
        }

        try {

            if (posts != null && posts.getJSONObject(0).has("title")) {


                if (posts.getJSONObject(0).getJSONObject("title").has("rendered")) {

                    return posts.getJSONObject(0).getJSONObject("title").getString("rendered");

                } else {

                    return "don have title";

                }


            }

        } catch (JSONException e) {

            Log.e(TAG, e.getMessage());
        }

        return "don have title";

    }

    //detect the link of the picture or we need parse the link
    private static boolean detectPictureLink(String url) {

        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif") || url.endsWith(".svg") || url.endsWith(".jpeg")) {

            Log.v("WordpressJsonUtil.java", "We got a picture link => " + url);

            return true;

        } else {

            return false;

        }

    }

    public static boolean detectIoException(String jsonStr) throws JSONException {

        if (TextUtils.isEmpty(jsonStr)) {
            return false;
        }

        JSONObject message = new JSONObject(jsonStr);

        return message.has("code") && message.getString("code").equals("rest_post_invalid_page_number");

    }

}