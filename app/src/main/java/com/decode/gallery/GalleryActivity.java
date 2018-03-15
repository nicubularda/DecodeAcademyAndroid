package com.decode.gallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener, ICallback{


    private TabLayout mTabs;
    private ViewPager mPager;
    private int current = 0;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private FloatingActionButton mFAB;
    private int REQUEST_PERMISSION_SETTING = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if (savedInstanceState != null)
            current = savedInstanceState.getInt("type");

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        mNavigation = findViewById(R.id.drawer_navigation);
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawer.closeDrawers();
                selectItem(item);
                return false;
            }
        });
        mFAB = findViewById(R.id.floating_button);
        mFAB.setOnClickListener(this);

        mTabs = findViewById(R.id.tabs);
        mPager = findViewById(R.id.pager);

        mTabs.setupWithViewPager(mPager);

        mPager.setCurrentItem(current);
        mNavigation.setCheckedItem(current);

        this.setAdapter();
    }

    private void setAdapter() {
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
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Photos" : "Videos";
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perm, int[] grants) {
        if (req == Media.PERM && grants.length > 0 && grants[0] == PackageManager.PERMISSION_GRANTED) {
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onRequestPermissionsResult(req, perm, grants);
        }
        else
            super.onRequestPermissionsResult(req, perm, grants);
    }

    @Override
    protected void onActivityResult(int req, int type, Intent intent) {
        if (req == REQUEST_PERMISSION_SETTING)
            this.setAdapter();
//        Snackbar mySnackbar = Snackbar.make(getRoot(),
//                req == 0 ? "image" : "video", Snackbar.LENGTH_SHORT);
//        mySnackbar.setAction("Undo", this);
//        mySnackbar.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_button) {
            Intent intent = new Intent(current == 0 ? MediaStore.ACTION_IMAGE_CAPTURE : MediaStore.ACTION_VIDEO_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, 1);
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
        }
        else
            selectItem(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("type", current);
    }

    private void selectItem(MenuItem item) {
        if (item.getItemId() == R.id.photos_action) {
            setCurrent(0);
        }
        else if (item.getItemId() == R.id.videos_action) {
            setCurrent(1);
        }
    }

    public void addPreview(Object value) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("color", (int)value);
        startActivityForResult(intent, 0);
    }

    public void askPerm() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    public View getRoot() {
        return findViewById(R.id.drawer_layout);
    }


    private void setCurrent(int value) {
        current = value;
        mPager.setCurrentItem(current);
    }


}
