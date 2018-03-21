package com.decode.gallery;

import android.view.View;

/**
 * Created by nicu on 28/02/2018.
 */

interface ICallback {
    void addPreview(View view);
    View getRoot();
    void askPerm();
    int getVisits(Media media);

}
