package com.redfirelab.android.wpmobileapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.redfirelab.android.wpmobileapp.data.WPContract.*;

/**
 * Created by Pouria on 11/29/2017.
 * wpMApp project.
 */

public class WPDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wp_mobile_app.db";
    private static final int DATABASE_VERSION = 1;

    public WPDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_POST_TABLE = "CREATE TABLE "
                + WPPostEntry.TABLE_NAME
                + " ("
                + WPPostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WPPostEntry.POST_ID + " INTEGER NOT NULL, "
                + WPPostEntry.POST_LINK + " TEXT NOT NULL, "
                + WPPostEntry.POST_TITLE + " TEXT NOT NULL, "
                + WPPostEntry.POST_DESCRIPTION + " TEXT NOT NULL, "
                + WPPostEntry.POST_CONTENT + " TEXT NOT NULL, "
                + WPPostEntry.SITE_BASE_URL + " TEXT NOT NULL"
                +"); ";

        sqLiteDatabase.execSQL(SQL_CREATE_POST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WPPostEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
