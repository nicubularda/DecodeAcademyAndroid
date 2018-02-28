package com.decode.gallery;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GalleryActivity extends AppCompatActivity implements ICallback {


    private TabLayout mTabs;
    private ViewPager mPager;
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mTabs = findViewById(R.id.tabs);
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GalleryFragment frag = new GalleryFragment();
                Bundle arg = new Bundle();
                arg.putInt("type", position);
                frag.setArguments(arg);
                return frag;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        });
        mTabs.setupWithViewPager(mPager);
        if (savedInstanceState != null)
            current = savedInstanceState.getInt("type");
        mPager.setCurrentItem(current);
    }

    public void addPreview() {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int req, int type, Intent intent) {
        current = type;
        mPager.setCurrentItem(type);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("type", current);
    }
}
