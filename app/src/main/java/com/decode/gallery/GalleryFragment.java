package com.decode.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.security.Permission;
import java.security.Permissions;
import java.util.List;

public class GalleryFragment extends Fragment implements View.OnClickListener {
    private int mType = 0;
    private View view;
    private RecyclerView mRecyclerView;
    public static boolean mPermAsked = false;
    private RecyclerView.Adapter mAdapter;

    public GalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRecyclerView = view.findViewById(R.id.gallery_fragment);
        if (mType == Media.TYPE_SOUND) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        }
        else
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.gallery_count)));
        checkPerm(!GalleryFragment.mPermAsked, true);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryActivity.REQUEST_PREVIEW
                && resultCode == Activity.RESULT_OK)
            mAdapter.notifyDataSetChanged();
    }

    private void checkPerm(boolean ask, boolean retry) {
        String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this.getContext(), perm) != PackageManager.PERMISSION_GRANTED) {
            if (ask) {
                GalleryFragment.mPermAsked = true;
                SharedPreferences prefs = getActivity().getSharedPreferences("PERMISSION_SHARED_PREFERENCES", Context.MODE_PRIVATE);
                boolean req = prefs.getBoolean("requested_" + perm, false);
                if (retry && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
                    Snackbar mySnackbar = Snackbar.make(((ICallback) getActivity()).getRoot(),
                            "Grant gallery permissions, please!", Snackbar.LENGTH_SHORT);
                    mySnackbar.setAction("Grant", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkPerm(true, false);
                        }
                    });
                    mySnackbar.show();
                } else if (!req) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("requested_" + perm, true);
                    editor.commit();

                    ActivityCompat.requestPermissions(getActivity(), new String[]{perm}, Media.PERM);
                } else {
                    ((ICallback) getActivity()).askPerm();
                }
            }
        }
        else
            this.setAdapter();
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(mAdapter = new GalleryAdapter(Media.getMedia(mType, this.getContext()), mType));
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perm, int[] grants) {
        super.onRequestPermissionsResult(req, perm, grants);
        GalleryFragment.mPermAsked = false;
        checkPerm(false, false);
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
        public TextView mInfo;
        public ViewHolder(View v, int type) {
            super(v);

            if (type == Media.TYPE_SOUND) {
                mLabel = v.findViewById(R.id.sound_title);
                mInfo = v.findViewById(R.id.sound_info);
            }
            else {
                mLabel = v.findViewById(R.id.item_media_title);
                mThumb = v.findViewById(R.id.item_media_thumb);
                mInfo = itemView.findViewById(R.id.visits);
            }
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Media> mMedia;
        private Picasso mThumbs;
        private int mType;

        public GalleryAdapter(List<Media> mMedia, int type) {
            this.mMedia = mMedia;
            this.mType = type;
            if (mType == Media.TYPE_IMAGE)
                this.mThumbs = new Picasso.Builder(getContext()).build();
            else if (mType == Media.TYPE_VIDEO)
                this.mThumbs = new Picasso.Builder(getContext())
                        .addRequestHandler(new VideoRequestHandler()).build();
            else if (mType == Media.TYPE_SOUND) {

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater in = LayoutInflater.from(parent.getContext());
            View v = in.inflate(mType == Media.TYPE_SOUND ? R.layout.item_media_sound : R.layout.item_media,
                    parent, false);
            return new ViewHolder(v, mType);
        }

        @Override
        public int getItemCount() {
            return mMedia.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Media m = mMedia.get(position);
            String path = mType == Media.TYPE_IMAGE ? "file://":"video:";
            if (mType == Media.TYPE_VIDEO)
                holder.mLabel.setText(U.format(m.getDur()));
            else
                holder.mLabel.setText(m.getName());
            holder.itemView.setOnClickListener(GalleryFragment.this);
            if (mThumbs != null)
                mThumbs.load(path + m.getUrl()).fit().centerCrop().into(holder.mThumb);
            holder.itemView.setTag(m);
            ICallback gallery = (ICallback) getActivity();
            if (mType == Media.TYPE_SOUND)
                holder.mInfo.setText(m.getAlbum() + " by " + m.getArtist());
            else {
                holder.mInfo.setVisibility(gallery.getVisits(m) > 0 ? View.VISIBLE : View.GONE);
                holder.mInfo.setText("" + gallery.getVisits(m));
            }
        }
    }
}


