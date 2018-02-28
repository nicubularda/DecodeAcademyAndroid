package com.decode.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mPreviewButton1;
    private Button mPreviewButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mPreviewButton1 = (Button) findViewById(R.id.button_preview1);
        mPreviewButton1.setOnClickListener(this);

        mPreviewButton2 = (Button) findViewById(R.id.button_preview2);
        mPreviewButton2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        setResult(view.getId() == R.id.button_preview1 ? 1 : 2);
        finish();
    }
}
