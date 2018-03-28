package com.decode.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView mThumb;
    Picasso mThumbs;
    Media mMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setEnterTransition(new Explode());
//        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_preview);

        SquareRelativeLayout mLayout = findViewById(R.id.preview);
        mThumb = findViewById(R.id.preview_thumb);
        mMedia = getIntent().getParcelableExtra("media");
        if (mMedia.getType() == 0)
            this.mThumbs = new Picasso.Builder(getApplicationContext()).build();
        else
            this.mThumbs = new Picasso.Builder(getApplicationContext())
                    .addRequestHandler(new VideoRequestHandler()).build();
        String path = mMedia.mIsCloud == 1 ? "" : mMedia.getType() == 0 ? "file://":"video:";
        mThumbs.load(path + mMedia.getUrl()).fit().centerCrop().into(mThumb, new Callback() {
            @Override
            public void onSuccess() {
                startPostponedEnterTransition();
            }

            @Override
            public void onError() {
                startPostponedEnterTransition();
            }
        });
        supportPostponeEnterTransition();
    }

    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra("media", mMedia);
        setResult(RESULT_OK, result);
        super.finish();

        super.finish();
    }

    @Override
    public void onClick(View view) {
//        setResult(view.getId() == R.id.button_preview1 ? 0 : view.getId() == R.id.button_preview2 ? 1 : 2);
        finish();
    }
}
