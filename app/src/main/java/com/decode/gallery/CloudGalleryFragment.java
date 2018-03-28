package com.decode.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CloudGalleryFragment extends Fragment implements View.OnClickListener {
    private int mType = 0;
    private View view;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    BroadcastReceiver mReceiver;
    private DB.Helper mDB;


    public CloudGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }

        mDB = new DB.Helper(getContext());
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                getCloudData();
            }
        };

    }

    private void getCloudData() {
        SQLiteDatabase db = mDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB.CloudPhoto.Entry.TABLE_NAME, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            List<Media> media = new ArrayList<>();
            do {
                Media m = new Media(Media.TYPE_IMAGE, cursor.getString(2),
                        cursor.getString(1)+"?w=200", new Long(0));
                m.mIsCloud = 1;
                media.add(m);
            } while (cursor.moveToNext());

            setAdapter(media);
        }
        cursor.close();

    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(Cloud.ACTION_CLOUD);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRecyclerView = view.findViewById(R.id.gallery_fragment);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.gallery_count)));
        getCloudData();
        return view;
    }

    private void setAdapter(List<Media> data) {
        mRecyclerView.setAdapter(mAdapter = new GalleryAdapter(data, 0));
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ICallback
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing())
            ((ICallback) getActivity()).addPreview(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mLabel;
        public ImageView mThumb;
        public TextView mVisits;
        public ViewHolder(View v) {
            super(v);

            mLabel = v.findViewById(R.id.item_media_title);
            mThumb = v.findViewById(R.id.item_media_thumb);
            mVisits = itemView.findViewById(R.id.visits);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Media> mMedia;
        private Picasso mThumbs;
        private int mType;

        public GalleryAdapter(List<Media> mMedia, int type) {
            this.mMedia = mMedia;
            this.mType = type;
            if (mType == 0)
                this.mThumbs = new Picasso.Builder(getContext()).build();
            else
                this.mThumbs = new Picasso.Builder(getContext())
                        .addRequestHandler(new VideoRequestHandler()).build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater in = LayoutInflater.from(parent.getContext());
            View v = in.inflate(R.layout.item_media,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Media m = mMedia.get(position);
            if (mType == 1)
                holder.mLabel.setText(U.format(m.getDur()));
            else
                holder.mLabel.setText(m.getName());
            holder.itemView.setOnClickListener(CloudGalleryFragment.this);
            mThumbs.load( m.getUrl()).fit().centerCrop().into(holder.mThumb);
            holder.itemView.setTag(m);
            ICallback gallery = (ICallback) getActivity();
            holder.mVisits.setVisibility(gallery.getVisits(m) > 0 ? View.VISIBLE : View.GONE);
            holder.mVisits.setText("" + gallery.getVisits(m));
        }
    }
}


