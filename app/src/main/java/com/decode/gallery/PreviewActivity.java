package com.decode.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        SquareRelativeLayout mLayout = findViewById(R.id.preview);
        mLayout.setBackgroundColor(getIntent().getIntExtra("color", 0));
    }

    @Override
    public void onClick(View view) {
//        setResult(view.getId() == R.id.button_preview1 ? 0 : view.getId() == R.id.button_preview2 ? 1 : 2);
        finish();
    }
}
