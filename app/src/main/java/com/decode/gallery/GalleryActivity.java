package com.decode.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mPreviewButton;
    private TextView mResultTextView;
    private String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mPreviewButton = (Button) findViewById(R.id.button_preview);
        mPreviewButton.setOnClickListener(this);

        mResultTextView = (TextView) findViewById(R.id.text_result);
        if (savedInstanceState != null)
            mResultTextView.setText(savedInstanceState.getString("result"));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int req, int resp, Intent intent) {
        result = "Result " + resp;
        mResultTextView.setText(result);

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("result", result);

    }
}
