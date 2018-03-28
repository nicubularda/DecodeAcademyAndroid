package com.decode.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener, ICallback{


    private TabLayout mTabs;
    private ViewPager mPager;
    private int current = 0;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private FloatingActionButton mFAB;
    public static int REQUEST_PERMISSION_SETTING = 9;
    public static int REQUEST_PREVIEW = 8;
    private HashMap<String, Integer> mVisits= new HashMap<String, Integer>();
    private Gson gson;
    private DB.Helper mDB;
    private Cloud mCloud;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Cloud.CloudBinder binder = (Cloud.CloudBinder) service;
            mCloud = (Cloud) binder.getService();
            mBound = true;
            if (mCloud != null)
                mCloud.fetch();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_gallery);
        gson = new Gson();
        mDB = new DB.Helper(getApplicationContext());
        if (savedInstanceState != null) {
            current = savedInstanceState.getInt("type");
            mVisits = (HashMap<String, Integer>) savedInstanceState.getSerializable("visits");
        }
        else {
//            SharedPreferences prefs = getSharedPreferences("PERMISSION_SHARED_PREFERENCES", Context.MODE_PRIVATE);
//            String v = prefs.getString("visits", "");
//            if (v != "") {
//                mVisits = gson.fromJson(v, new TypeToken<HashMap<String, Integer>>(){}.getType());
//            }

            SQLiteDatabase db = mDB.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DB.Visit.Entry.TABLE_NAME, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    mVisits.put(cursor.getString(1), cursor.getInt(2));
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            Intent intent = new Intent(this, Cloud.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
//        SharedPreferences prefs = getSharedPreferences("PERMISSION_SHARED_PREFERENCES", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("visits", gson.toJson(mVisits));
//        editor.commit();
    }

    private void setAdapter() {
        mPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment frag = position == 2 ? new CloudGalleryFragment() : new GalleryFragment();
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
                return position == 0 ? "Photos" : position == 1 ? "Videos" : "Cloud";
            }
        });

    }

    private void writeData(String key, int visits) {
        SQLiteDatabase db = mDB.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB.Visit.Entry.COLUMN_URL, key);
        values.put(DB.Visit.Entry.COLUMN_VISITS, visits);

        if (db.update(DB.Visit.Entry.TABLE_NAME, values,
                DB.Visit.Entry.COLUMN_URL + "= ?", new String[]{key}) <= 0)
            db.insert(DB.Visit.Entry.TABLE_NAME, null, values);
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
    protected void onActivityResult(int requestCode, int result, Intent data) {
        if (requestCode == REQUEST_PREVIEW && result == RESULT_OK) {
            Media media = data.getParcelableExtra("media");
            int v = mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
            mVisits.put(media.getUrl(), v + 1);
            writeData(media.getUrl(), v + 1);
            for (Fragment f : getSupportFragmentManager().getFragments())
                f.onActivityResult(requestCode, result, data);
        }
        else if (requestCode == REQUEST_PERMISSION_SETTING)
            this.setAdapter();
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
        state.putSerializable("visits", mVisits);
    }

    private void selectItem(MenuItem item) {
        if (item.getItemId() == R.id.photos_action) {
            setCurrent(0);
        }
        else if (item.getItemId() == R.id.videos_action) {
            setCurrent(1);
        }
    }

    @SuppressLint("RestrictedApi")
    public void addPreview(View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.item_media_thumb),"thumbnail");

        intent.putExtra("media", (Media)view.getTag());
        startActivityForResult(intent, REQUEST_PREVIEW, options.toBundle());
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

    public int getVisits(Media media) {
        return mVisits.containsKey(media.getUrl()) ? mVisits.get(media.getUrl()) : 0;
    }

}
