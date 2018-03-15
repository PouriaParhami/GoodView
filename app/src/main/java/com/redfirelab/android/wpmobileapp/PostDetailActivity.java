package com.redfirelab.android.wpmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;
import com.redfirelab.android.wpmobileapp.ultilities.WordpressPostData;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlAssetsImageGetter;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class PostDetailActivity extends AppCompatActivity {

    protected TextView title;
    private String postLink;
    private MethodsUtils methods = new MethodsUtils(PostDetailActivity.this);
    private HtmlTextView htmlTextView;
    //-------------- on create method ----------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        title = findViewById(R.id.tv_post_act_title);
        ImageView mDisplayPostImage = findViewById(R.id.iv_post);

        title.setTextSize(Float.parseFloat(WPPreferences.getSizeOfPostTitle(this)));


        htmlTextView = findViewById(R.id.html_text);
        htmlTextView.setTextSize(Float.parseFloat(WPPreferences.getSizeOfContentPost(this)));

        //------------- get intent information ------------
        Intent intent = getIntent();

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {

            //serialize method
            WordpressPostData wordpressPostData = (WordpressPostData) intent.getSerializableExtra(Intent.EXTRA_TEXT);

            title.setText(Html.fromHtml(wordpressPostData.getpTitle()));

            htmlTextView.setHtml(wordpressPostData.getpContent(),
                    new HtmlHttpImageGetter(htmlTextView, WPPreferences.getSiteAddress(this), true));

            postLink = wordpressPostData.getpPostLink();
            Picasso.with(this).load(wordpressPostData.getpLinkOfPicture()).into(mDisplayPostImage);

        }

        //for back button
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.main_app_name_activity_title);

        }
    }


    //------------------------------------------------------------------------------------
    //menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.post_menu_share_content) {

            String mimeType = "text/plain";
            String TextToShare = htmlTextView.getText() + "\n#darkoobweb.com";

            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(R.string.sc_share_content)
                    .setType(mimeType)
                    .setText(TextToShare)
                    .startChooser();


            return true;

        } else if (item.getItemId() == R.id.post_menu_share_link) {

            String mimeType = "text/plain";
            String TextToShare = postLink + "\n#darkoobweb.com";

            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(R.string.sc_share_link)
                    .setType(mimeType)
                    .setText(TextToShare)
                    .startChooser();


            return true;

        } else if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
