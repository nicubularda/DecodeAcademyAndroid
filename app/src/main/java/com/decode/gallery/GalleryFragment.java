package com.decode.gallery;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.List;

public class GalleryFragment extends Fragment implements View.OnClickListener {
    private int mType = 0;
    private View view;
    private RecyclerView mRecyclerView;

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
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), getResources().getInteger(R.integer.gallery_count)));
        mRecyclerView.setAdapter(new GalleryAdapter(Media.getMedia(mType, this.getContext()), mType));
        return view;
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ICallback
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing())
            ((ICallback) getActivity()).addPreview(view.getTag());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mLabel;
        private RelativeLayout mLayout;
        public ImageView mThumb;
        public ViewHolder(View v) {
            super(v);

            mLabel = v.findViewById(R.id.item_media_title);
            mLayout = v.findViewById(R.id.item_media);
            mThumb = v.findViewById(R.id.item_media_thumb);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Media> mMedia;
        private Picasso mThumbs;
        private int mType;

        public GalleryAdapter(List<Media> mMedia, int type) {
            this.mMedia = mMedia;
            this.mThumbs = mThumbs;
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
            String path = mType == 0 ? "file://":"video:";
            if (mType == 1)
                holder.mLabel.setText(U.format(m.getDur()));
            else
                holder.mLabel.setText(m.getName());
            holder.itemView.setOnClickListener(GalleryFragment.this);
            mThumbs.load(path + mMedia.get(position).getUrl()).fit().centerCrop().into(holder.mThumb);
        }
    }
}


