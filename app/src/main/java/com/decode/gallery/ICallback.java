package com.decode.gallery;

import android.view.View;

/**
 * Created by nicu on 28/02/2018.
 */

interface ICallback {
    void addPreview(Object value);
    View getRoot();
    void askPerm();
}
