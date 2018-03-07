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
import android.widget.TextView;

import java.lang.reflect.Array;

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
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
        mRecyclerView.setAdapter(new GalleryAdapter(Media.getMedia(mType + 1)));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                int space = 5;
                outRect.left = space;
                outRect.right = space;
                outRect.top = space;
                outRect.bottom = space;
                if (pos % 3 == 0)
                    outRect.left = 0;
                if (pos % 3 == 2)
                    outRect.right = 0;
            }
        });
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
        private SquareRelativeLayout mLayout;
        public ViewHolder(View v) {
            super(v);

            mLabel = v.findViewById(R.id.item_media_title);
            mLayout = v.findViewById(R.id.item_media);
        }

        public void setColor(int color) {
            mLayout.setBackgroundColor(color);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Media[] mMedia;

        public GalleryAdapter(Media[] mMedia) {
            this.mMedia = mMedia;
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
            return mMedia.length;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mLabel.setText(mMedia[position].getName());
            holder.setColor(mMedia[position].getColor());
            holder.itemView.setTag(mMedia[position].getColor());
            holder.itemView.setOnClickListener(GalleryFragment.this);
        }
    }
}


