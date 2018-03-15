package com.redfirelab.android.wpmobileapp;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.redfirelab.android.wpmobileapp.ultilities.MethodsUtils;

public class SettingsActivity extends AppCompatActivity {

    private MethodsUtils methods = new MethodsUtils(SettingsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //for back button
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {

            methods.setActivityTitle(actionBar, R.string.setting_activity_title);

        }
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
