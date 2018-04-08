package com.onlylemi.mapview;


import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.Mocks.MockUser;
import com.onlylemi.mapview.library.camera.MapViewCamera;
import com.onlylemi.mapview.library.camera.FollowUserMode;
import com.onlylemi.mapview.library.utils.MapUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by patnym on 2018-04-01.
 */

@RunWith(RobolectricTestRunner.class)
public class FollowUserModeTest {

    private MapViewCamera camera;
    private MockUser user;

    @Before
    public void setup() {
        camera = new MapViewCamera(20,10, 10, 5);
        user = new MockUser(new PointF(0, 0));
        camera.initialize(user);
    }

    @Test
    public void testFollowUserMode_Translation() {
        FollowUserMode fp = new FollowUserMode(camera);

        fp.calculateTarget();

        PointF target = fp.getTargetedPosition();

        Assert.assertEquals(10.0f, target.x);
        Assert.assertEquals(5.0f, target.y);
    }

    @Test
    public void testFollowUserMode_OnStart() {
        FollowUserMode fp = new FollowUserMode(camera);

        fp.onStart();

        PointF target = fp.getTargetedPosition();

        Assert.assertEquals(10.0f, target.x);
        Assert.assertEquals(5.0f, target.y);
        Assert.assertEquals(camera.getDefaultContainUserZoom(), fp.getTargetedZoom());
    }

    @Test
    public void testFollowerUserMode_Update() {
        FollowUserMode fp = new FollowUserMode(camera);

        fp.onStart();

        Matrix m = new Matrix();

        m = fp.update(m, Long.MAX_VALUE);

        float[] b = new float[9];
        m.getValues(b);

        PointF p = MapUtils.getPositionFromMatrix(m);

        Assert.assertEquals(10.0f, p.x);
        Assert.assertEquals(5.0f, p.y);
        Assert.assertEquals(camera.getDefaultContainUserZoom(), b[0]);
        Assert.assertEquals(camera.getDefaultContainUserZoom(), b[4]);
    }

    @Test
    public void testFollowUserMode_MoveUser() {
        FollowUserMode fp = new FollowUserMode(camera);

        fp.onStart();

        Matrix m = new Matrix();

        m = fp.update(m, Long.MAX_VALUE);

        user.position.x = 10.0f;
        user.position.y = 0.0f;

        m = fp.update(m, Long.MAX_VALUE);

        float[] b = new float[9];
        m.getValues(b);

        PointF p = MapUtils.getPositionFromMatrix(m);

        Assert.assertEquals(10.0f - (10.0f * camera.getCurrentZoom()), p.x);
        Assert.assertEquals(5.0f, p.y);
        Assert.assertEquals(camera.getDefaultContainUserZoom(), b[0]);
        Assert.assertEquals(camera.getDefaultContainUserZoom(), b[4]);
    }

}
