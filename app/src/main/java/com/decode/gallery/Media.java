package com.decode.gallery;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

public class Media implements Parcelable{
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

    protected Media(Parcel in) {
        mName = in.readString();
        mType = in.readInt();
        mUrl = in.readString();
        if (in.readByte() == 0) {
            mDur = null;
        } else {
            mDur = in.readLong();
        }
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeInt(mType);
        parcel.writeString(mUrl);
        if (mDur == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(mDur);
        }
    }
}
