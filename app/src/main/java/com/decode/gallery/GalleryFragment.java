package com.decode.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GalleryFragment extends Fragment implements View.OnClickListener {
    private int mType = 0;
    private Button mPreviewButton;
    private View view;

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
        mPreviewButton = (Button) view.findViewById(R.id.button_preview);
        mPreviewButton.setOnClickListener(this);
        mPreviewButton.setText("Preview " + mType);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ICallback
                && !getActivity().isDestroyed()
                && !getActivity().isFinishing())
            ((ICallback) getActivity()).addPreview();
    }
}
