package com.redfirelab.android.wpmobileapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPContract;

/**
 * Created by Pouria on 12/4/2017.
 * wpMApp project.
 */

public class SqlAdapter extends RecyclerView.Adapter<SqlAdapter.PostViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;

    private float mTitleSize;
    private float mDescriptionSize;

    private final SqlClickListener mClickHandler;

    public void updateTitleSize(float v) {

        mTitleSize = v;
        notifyDataSetChanged();

    }

    public void updateDescriptionSize(float v) {

        mDescriptionSize = v;
        notifyDataSetChanged();
    }

    // Click listener interface
    public interface SqlClickListener {

        void onClick(long position);

    }

    public SqlAdapter(Context context, SqlClickListener clickHandler) {
        this.mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_sql_list_item, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(WPContract.WPPostEntry.POST_ID);
        int titleIndex = mCursor.getColumnIndex(WPContract.WPPostEntry.POST_TITLE);
        int descriptionIndex = mCursor.getColumnIndex(WPContract.WPPostEntry.POST_DESCRIPTION);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        String title = mCursor.getString(titleIndex);

        //set values
        holder.itemView.setTag(id);

        holder.postDescription.setText(Html.fromHtml(description));
        holder.postDescription.setTextSize(mDescriptionSize);

        holder.postTitle.setText(Html.fromHtml(title));
        holder.postTitle.setTextSize(mTitleSize);

    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    // Inner class for creating ViewHolders
    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView postTitle;
        TextView postDescription;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public PostViewHolder(View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.tv_sql_post_title);
            postDescription = itemView.findViewById(R.id.tv_sql_post_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            int id = mCursor.getColumnIndex(WPContract.WPPostEntry._ID);

            mCursor.moveToPosition(adapterPosition);

            mClickHandler.onClick(mCursor.getInt(id));


        }
    }
}
