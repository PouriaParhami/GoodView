
package com.redfirelab.android.wpmobileapp.ultilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat.Action;

import com.redfirelab.android.wpmobileapp.MainActivity;
import com.redfirelab.android.wpmobileapp.R;
import com.redfirelab.android.wpmobileapp.data.WPPreferences;
import com.redfirelab.android.wpmobileapp.sync.WordpressSyncIntentService;
import com.redfirelab.android.wpmobileapp.sync.WordpressSyncTask;

/**
 * Utility class for creating post notifications
 */
public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int NEW_POST_REMINDER_NOTIFICATION_ID = 1177;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int POST_REMINDER_PENDING_INTENT_ID = 3477;


    public static void remindUserNewPostReleased(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "darkoobwe_web_notifi_123")
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setSmallIcon(R.drawable.ic_add_alert_48px)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.fa_new_post_reminder_notification_title))
                .setContentText(String.format(context.getString(R.string.fa_new_post_reminder_notification_body), WPPreferences.getLastPostTitle(context)) )
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        String.format(context.getString(R.string.fa_new_post_reminder_notification_body), WPPreferences.getLastPostTitle(context))))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        // Get a NotificationManager, using context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()

        if (notificationManager != null) {
            notificationManager.notify(NEW_POST_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
        }
    }
    //------------------------------------------------------------------------------------------------------------



    // Create a helper method called contentIntent with a single parameter for a Context. It
    // should return a PendingIntent. This method will create the pending intent which will trigger when
    // the notification is pressed. This pending intent should open up the MainActivity.

    private static PendingIntent contentIntent(Context context) {

        //  Create an intent that opens up the MainActivity

        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.putExtra("RESET_THE_LOADER", 1);

        return PendingIntent.getActivity(
                context,
                POST_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // Create a helper method called largeIcon which takes in a Context as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context) {
        // Get a Resources object from the context.
        Resources res = context.getResources();
        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_add_alert_48px
        return BitmapFactory.decodeResource(res, R.drawable.ic_add_alert_48px);
    }
}
