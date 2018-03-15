package com.redfirelab.android.wpmobileapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.ultilities.WordpressCategoryData;

import java.util.List;

/**
 * Created by Pouria on 12/24/2017.
 * wpMApp project.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<WordpressCategoryData> wpdata;

    private final ListItemClickListener mOnClickListener;

    // Click listener interface
    public interface ListItemClickListener {

        void onListItemClick(WordpressCategoryData clickItemIndex);

    }

   public CategoryAdapter(ListItemClickListener listener) {

        mOnClickListener = listener;

    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.activity_category_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {

        holder.bind(position);

    }

    @Override
    public int getItemCount() {

        if (wpdata == null) return 0;

        return wpdata.size();
    }

    void setWpdata(List<WordpressCategoryData> WordpressCategoryData) {

        wpdata = WordpressCategoryData;
        notifyDataSetChanged();

    }

    //-------------------------- Post view holder class --------------------
    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDisplayCategoryName;
        private TextView mDisplayCategoryCountPosts;


        CategoryViewHolder(View itemView) {
            super(itemView);

            mDisplayCategoryName = itemView.findViewById(R.id.tv_category_title);
            mDisplayCategoryCountPosts = itemView.findViewById(R.id.tv_count_category_posts);

            //for click listener
            itemView.setOnClickListener(this);

        }

        void bind(final int myListPostPosition) {

            List<WordpressCategoryData> WordpressCategoryData = wpdata;

            mDisplayCategoryName.setText(WordpressCategoryData.get(myListPostPosition).getName());
            mDisplayCategoryCountPosts.setText(String.valueOf(WordpressCategoryData.get(myListPostPosition).getCount()));

        }

        @Override
        public void onClick(View view) {
            WordpressCategoryData WordpressCategoryData = wpdata.get(getAdapterPosition());
            mOnClickListener.onListItemClick(WordpressCategoryData);
        }

    } //----------------- end of the inner class -----------------------------------------------

}