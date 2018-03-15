package com.redfirelab.android.wpmobileapp.sync;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;

/**
 * Created by Pouria on 12/10/2017.
 * wpMApp project.
 */

public class WordpressSyncIntentService extends IntentService {

    //  Create a constructor that calls super and passes the name of this class
    public WordpressSyncIntentService() {
        super("WordpressSyncIntentService");
    }

    // The method that intent service call on the background thread
    // Override onHandleIntent, and within it, call WordpressSyncTask.syncWordpress
    @Override
    protected void onHandleIntent(Intent intent) {

        WordpressSyncTask.syncWordpress(this);
    }
}