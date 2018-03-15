package com.decode.gallery;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

public class Media {
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int PERM = 1984;

    private String mName;
    private int mType;
    private String mUrl;
    private Long mDur;

    public Media(int type, String name, String url, Long dur) {
        mType = type;
        mName = name;
        mUrl = url;
        mDur = dur;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public long getDur() {
        return mDur;
    }

    public String getUrl() { return mUrl; }

    public static List<Media> getMedia(int type, Context context) {
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE, MediaStore.Video.Media.DURATION
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                (type == 0 ? MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE : MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);


        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        Cursor cursor = cursorLoader.loadInBackground();
        List<Media> media = new ArrayList<>();
        cursor.moveToFirst();

        do {
            // create Media objects here
            media.add(new Media(type, cursor.getString(5), cursor.getString(1), cursor.getLong(6)));
        } while (cursor.moveToNext());

        cursor.close();
        return media;
    }
}
