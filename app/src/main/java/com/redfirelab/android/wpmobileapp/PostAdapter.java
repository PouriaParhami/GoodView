package com.redfirelab.android.wpmobileapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPContract;
import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressPostData;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pouria on 11/21/2017.
 * wpMApp project.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = PostAdapter.class.getSimpleName();

    //how many item it hold
    private Context context;
    private List<WordpressPostData> wpdata;

    private final ListItemClickListener mOnClickListener;

    private float mTitleSize;
    private float mDescriptionSize;

    private List<String> oldSavePosts = new ArrayList<>();

    // Click listener interface
    public interface ListItemClickListener {

        void onListItemClick(WordpressPostData clickItemIndex);

    }

    public PostAdapter(ListItemClickListener listener) {

        mOnClickListener = listener;
    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        int layoutIdForListItem = R.layout.posts_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        holder.bind(position);

    }

    @Override
    public int getItemCount() {

        if (wpdata == null) return 0;

        return wpdata.size();
    }

    void setWpdata(List<WordpressPostData> wordpressPostData) {
        wpdata = wordpressPostData;
        notifyDataSetChanged();
    }

    void updateTitleSize(float size) {

        mTitleSize = size;
        notifyDataSetChanged();
    }

    void updateDescriptionSize(float size) {

        mDescriptionSize = size;
        notifyDataSetChanged();

    }


    //-------------------------- Post view holder class --------------------
    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDisplayPostTitle;
        private TextView mDisplayPostDescription;
        private ImageView mDisplayPostImage;
        private ImageView mImageView;
        private TextView mLikeIt;


        PostViewHolder(View itemView) {
            super(itemView);

            mDisplayPostTitle = itemView.findViewById(R.id.tv_post_title);
            mDisplayPostDescription = itemView.findViewById(R.id.tv_post_description);
            mDisplayPostImage = itemView.findViewById(R.id.iv_post_pic);
            mImageView = itemView.findViewById(R.id.iv_fav_icon);
            mLikeIt = itemView.findViewById(R.id.tv_like_it);

            //for click listener
            itemView.setOnClickListener(this);

        }

        void bind(final int myListPostPosition) {

            List<WordpressPostData> wordpressPostData = wpdata;

            oldSavePosts = WPPreferences.getFavoritePosts(context);

            mDisplayPostTitle.setText(Html.fromHtml(wordpressPostData.get(myListPostPosition).getpTitle()));
            mDisplayPostTitle.setTextSize(mTitleSize);

            mLikeIt.setText("علاقه مندی");

            mDisplayPostDescription.setText(Html.fromHtml(wordpressPostData.get(myListPostPosition).getpDescription()));
            mDisplayPostDescription.setTextSize(mDescriptionSize);

            mImageView.setImageResource(R.drawable.ic_favorite_border_48px);

            Picasso.with(context).load(wordpressPostData.get(myListPostPosition).getpLinkOfPicture()).into(mDisplayPostImage);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    WordpressPostData wordpressPostData = wpdata.get(myListPostPosition);

                    Log.v(TAG, "the position we saved -> " + wpdata.get(myListPostPosition));
                    Log.v(TAG, "The old post save size ON OnClick listener.. => " + oldSavePosts.size());

                    //------ we saved this post before or not ? ------------------------------------
                    if (!oldSavePosts.contains(String.valueOf(wordpressPostData.getpId()))) {

                        Log.v(TAG, " We don't have this id so we save it. ");

                        //--------- save the post information in database -------------------------------
                        //--------- if the post is not saved, then save it ------------------------------
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(WPContract.WPPostEntry.POST_ID, wordpressPostData.getpId());
                        contentValues.put(WPContract.WPPostEntry.POST_LINK, wordpressPostData.getpPostLink());
                        contentValues.put(WPContract.WPPostEntry.POST_TITLE, wordpressPostData.getpTitle());
                        contentValues.put(WPContract.WPPostEntry.POST_DESCRIPTION, wordpressPostData.getpDescription());
                        contentValues.put(WPContract.WPPostEntry.POST_CONTENT, wordpressPostData.getpContent());
                        contentValues.put(WPContract.WPPostEntry.SITE_BASE_URL, wordpressPostData.getpBaseUrl());

                        Uri uri = context.getContentResolver().insert(WPContract.WPPostEntry.CONTENT_URI, contentValues);

                        Log.v(TAG, "The VALUE IN THE URI INSERT ACTION IN POST ADAPTER => " + uri);

                        //--------- if save process is successfully change the button ---------------------------------
                        if (uri != null) {

                            Log.v(TAG, "Save process is successful, post id we save is -> " + wordpressPostData.getpId());

                            WPPreferences.saveFavoritePosts(context, String.valueOf(wordpressPostData.getpId()));

                            mLikeIt.setText("به لیست علاقه مندی اضافه شد.");
                            mLikeIt.setTextSize(9);

                            notifyDataSetChanged();

                        } else {

                            Log.e(TAG, "No Post save in db and no save in shared preference");

                        }


                    } else {

                        Log.v(TAG, "WE HAVE THIS POST SO ...");

                        Log.v(TAG, "THE BUTTON IS UNSAVE SO WE DELETE THE POST FROM DATA BASE AND SHARED");

                        Uri uri = WPContract.WPPostEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(String.valueOf(wordpressPostData.getpId())).build();

                        Log.v(TAG, "The URL We want To Delete = > " + uri);

                        //Delete a single row of data using a ContentResolver
                        ContentResolver contentResolver = context.getContentResolver();

                        int result = contentResolver.delete(uri, null, null);

                        if (result > 0) {

                            Log.e(TAG, "result of delete --> " + result);

                            //Remove the id from Shared Preference
                            WPPreferences.removeFavoritePosts(context, wordpressPostData.getpId());

                            oldSavePosts = WPPreferences.getFavoritePosts(context);

                            //mButton.setText(R.string.save_post_button);
                            mImageView.setImageResource(R.drawable.ic_favorite_border_48px);

                            mLikeIt.setText("علاقه مندی");
                            mLikeIt.setTextSize(11);

                            notifyDataSetChanged();

                        } else {

                            Log.e(TAG, "No post is delete from DB, and SharedPreference");

                        }

                    }

                }
            });


            /*
              check post is favorite post or not
              if the post is favorite post, we change the text box text to "unsaved" or
              change the picture.
              and user can detect the post is saved as favorite posts or not.
             */

            if (oldSavePosts != null && oldSavePosts.size() != 0) {

                Log.v(TAG, "if statement, Old saved posts is not null.");
                Log.v(TAG, "The old post save size is => " + oldSavePosts.size());

                //for (int i = 0; i < oldSavePosts.size(); i += 1) {
                // !oldSavePosts.contains(String.valueOf(wordpressPostData.getpId()))
                //Log.v(TAG, "Old save post id is ===> " + oldSavePosts.get(i));

                if (oldSavePosts.contains(String.valueOf(wordpressPostData.get(myListPostPosition).getpId()))) {

                    //  Log.v(TAG, "Old save post" + oldSavePosts.get(i) + " is EQUAL wordpress post data " + wordpressPostData.get(myListPostPosition).getpId());
                    // mButton.setText("unsaved");
                    Log.v(TAG, "The button have unsaved name.................");

                    mImageView.setImageResource(R.drawable.ic_favorite_48px);

                    mLikeIt.setText("به لیست علاقه مندی اضافه شد.");
                    mLikeIt.setTextSize(9);


                } else {

                    Log.v(TAG, "Not id match from post save list.");

                }

            } else {

                Log.e(TAG, "The List of save posts is empty");

            }

        }

        @Override
        public void onClick(View view) {
            WordpressPostData wordpressPostData = wpdata.get(getAdapterPosition());
            mOnClickListener.onListItemClick(wordpressPostData);
        }

    }

}
