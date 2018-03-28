package com.decode.gallery;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by nicu on 28/03/2018.
 */

public class Cloud extends Service {
    private final IBinder mBinder = new CloudBinder();

    public static final String ACTION_CLOUD = "action_cloud";

    private Executor mExecutor;
    private DB.Helper mDB;

    @Override
    public IBinder onBind(Intent intent) {
        mExecutor = Executors.newSingleThreadExecutor();
        mDB = new DB.Helper(getApplicationContext());
        return mBinder;
    }

    public class CloudBinder extends Binder {
        Service getService() {
            return Cloud.this; // return this instance of Cloud so clients can call public methods
        }
    }

    public void fetch() {
        // 1. access api: https://goo.gl/xATgGr
        // 2. interpret json
        // 3. store in db
        // 4. notify
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Photo[] photos = U.api("https://goo.gl/xATgGr", Photo[].class);
                SQLiteDatabase db = mDB.getWritableDatabase();

                db.execSQL(DB.CloudPhoto.SQL_DROP);
                db.execSQL(DB.CloudPhoto.SQL_CREATE);
                for (Photo p : photos) {
                    ContentValues values = new ContentValues();
                    values.put(DB.CloudPhoto.Entry.COLUMN_URL, p.url);
                    values.put(DB.CloudPhoto.Entry.COLUMN_TITLE, p.title);
                    db.insert(DB.CloudPhoto.Entry.TABLE_NAME, null, values);
                }

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(ACTION_CLOUD));
            }
        });
    }

    private static class Photo {
        public String url;
        public String title;
    }
}
