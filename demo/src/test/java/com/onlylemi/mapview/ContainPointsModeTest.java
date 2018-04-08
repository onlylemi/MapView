package com.onlylemi.mapview;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.Mocks.MockUser;
import com.onlylemi.mapview.library.camera.MapViewCamera;
import com.onlylemi.mapview.library.camera.ContainPointsMode;
import com.onlylemi.mapview.library.camera.ContainPointsUserMode;
import com.onlylemi.mapview.library.utils.MapUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

/**
 * Created by patnym on 2018-03-31.
 */

@RunWith(RobolectricTestRunner.class)
public class ContainPointsModeTest {

    @Test
    public void getMaxMinPoints_correct() {

        ArrayList<PointF> points = new ArrayList<>();
        points.add(new PointF(0.0f, 10.0f));
        points.add(new PointF(1.0f, 1.0f));
        points.add(new PointF(-10.0f, 0.0f));
        points.add(new PointF(30.0f, 25.0f));
        points.add(new PointF(4.0f, 7.0f));
        points.add(new PointF(9.1f, 0.1f));

        //Smallest values are -10, 0.0
        //Higest are 30.0, 25.0

        float[] p = ContainPointsMode.getMaxMinFromPointList(points);

        Assert.assertEquals(30.0f, p[0]);
        Assert.assertEquals(25.0f, p[1]);
        Assert.assertEquals(-10.0f, p[2]);
        Assert.assertEquals(0.0f, p[3]);
    }

    @Test
    public void getCorrectZoomToContainPoints() {

        //I have a 800x600 view frustum with a uniform zoom
        //I input a points at 1, 1 and -1, -1]
        PointF p1 = new PointF(1, 1);
        PointF p2 = new PointF(-1, -1);

        int viewWidth = 800;
        int viewHeight = 600;

        //I expect to get back a zooming factor of 600 since the height is gonna "go outside" first
        float zoom = ContainPointsMode.getZoomWithinPoints(p1.x, p1.y, p2.x, p2.y, viewWidth, viewHeight);

        Assert.assertEquals(300.0f, zoom);
    }

    @Test
    public void calculateCorrectRelativeMidpoint() {
        PointF p2 = new PointF(0, 0);
        PointF p1 = new PointF(2, 2);

        //Target zoom should be 300
        float targetZoom = 5.0f;

        //800x600 view
        int viewWidth = 20;
        int viewHeight = 10;

        PointF target = ContainPointsMode.getTranslationTarget(p1.x, p1.y, p2.x, p2.y, targetZoom, viewWidth, viewHeight);

        Assert.assertEquals(5.0f, target.x);
        Assert.assertEquals(0.0f, target.y);
    }

    @Test
    public void calculateCorrectRelativeMidpointNegativeMin() {
        PointF p2 = new PointF(-2, -2);
        PointF p1 = new PointF(2, 2);

        //Target zoom should be 300
        float targetZoom = 5.0f;

        //800x600 view
        int viewWidth = 20;
        int viewHeight = 10;

        PointF target = ContainPointsMode.getTranslationTarget(p1.x, p1.y, p2.x, p2.y, targetZoom, viewWidth, viewHeight);

        Assert.assertEquals(10.0f, target.x);
        Assert.assertEquals(5.0f, target.y);
    }

    @Test
    public void testCameraModeUpdateInstantMove() {
        ArrayList<PointF> points = new ArrayList<>();
        points.add(new PointF(0, 0));
        points.add(new PointF(2, 2));

        MapViewCamera camera = new MapViewCamera(20,10, 10, 5);

        ContainPointsMode cp = new ContainPointsMode(camera, points, 0.0f);

        cp.onStart();

        Matrix m = cp.update(camera.getWorldMatrix(), Long.MAX_VALUE);

        float[] b = new float[9];
        m.getValues(b);

        PointF p = MapUtils.getPositionFromMatrix(m);

        Assert.assertEquals(5.0f, p.x);
        Assert.assertEquals(0.0f, p.y);
        Assert.assertEquals(5.0f, b[0]);
        Assert.assertEquals(5.0f, b[4]);
    }

    @Test
    public void testCameraModeContainWithUser() {
        ArrayList<PointF> points = new ArrayList<>();
        points.add(new PointF(0, 0));
        points.add(new PointF(2, 2));

        MapViewCamera camera = new MapViewCamera(20,10, 10, 5);

        MockUser user = new MockUser(new PointF(4, 4));
        camera.initialize(user);

        ContainPointsUserMode cp = new ContainPointsUserMode(camera, points, user, 0.0f);

        cp.onStart();

        Matrix m = cp.update(camera.getWorldMatrix(), Long.MAX_VALUE);

        float[] b = new float[9];
        m.getValues(b);

        PointF p = MapUtils.getPositionFromMatrix(m);

        Assert.assertEquals(5.0f, p.x);
        Assert.assertEquals(0.0f, p.y);
        Assert.assertEquals(2.5f, b[0]);
        Assert.assertEquals(2.5f, b[4]);
    }

    @Test
    public void testCameraModeContainWithUser_MoveUser() {
        ArrayList<PointF> points = new ArrayList<>();
        points.add(new PointF(0, 0));
        points.add(new PointF(2, 2));

        MapViewCamera camera = new MapViewCamera(20,10, 10, 5);

        MockUser user = new MockUser(new PointF(4, 4));
        camera.initialize(user);

        ContainPointsUserMode cp = new ContainPointsUserMode(camera, points, user, 0.0f);

        cp.onStart();

        cp.update(camera.getWorldMatrix(), Long.MAX_VALUE);

        user.position.x = 5;
        user.position.y = 5;

        Matrix m = cp.update(camera.getWorldMatrix(), Long.MAX_VALUE);

        float[] b = new float[9];
        m.getValues(b);

        PointF p = MapUtils.getPositionFromMatrix(m);

        Assert.assertEquals(5.0f, p.x);
        Assert.assertEquals(0.0f, p.y);
        Assert.assertEquals(2.0f, b[0]);
        Assert.assertEquals(2.0f, b[4]);
    }

}
