package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by patny on 2017-07-05.
 */

public interface IMark {
    void update(Matrix m);
    void draw(final Canvas canvas, Paint paint);
}
