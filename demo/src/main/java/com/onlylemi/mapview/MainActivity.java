package com.onlylemi.mapview;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    private Bitmap bitmap;
    private MapView mapView;

    private List<PointF> nodes; //节点集
    private List<PointF> nodesContact; //节点连通集

    private List<Integer> routeList; //路线list
    private List<Float> routeListDegrees;

    private List<PointF> marks;
    private List<String> marksName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nodes = getNodesList();
        nodesContact = getNodesContactList();
        marks = getMarks();
        marksName = getMarksName();
        routeList = new ArrayList<>();
        MapUtils.init(nodes, nodesContact);

        bitmap = getImageFromAssetsFile("map.png");
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.loadMap(getPictureFromBitmap(bitmap));
        mapView.setMapViewListener(new MapViewListener() {

            @Override
            public void onMapLoadSuccess() {
                mapView.setCurrentRotateDegrees(30);
                mapView.refresh();

                final LocationLayer locationLayer = new LocationLayer(mapView, mapView.mapCenter(),
                        LocationLayer.MODE_COMPASS);
                locationLayer.setCompassIndicatorArrowRotateDegree(30);
                locationLayer.setCompassIndicatorCircleRotateDegree(90);
                mapView.addLayer(locationLayer);
                mapView.refresh();

                BitmapLayer bitmapLayer = new BitmapLayer(mapView, R.mipmap.ic_launcher);
                bitmapLayer.setLocation(new PointF(200, 200));
                mapView.addLayer(bitmapLayer);
                mapView.refresh();

                MarkLayer markLayer = new MarkLayer(mapView);
                markLayer.setMarks(marks);
                markLayer.setMarksName(marksName);

                final RouteLayer routeLayer = new RouteLayer(mapView, nodes, null);
                mapView.addLayer(routeLayer);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                        Log.i(TAG, num + " is click!");
                        PointF target = new PointF(marks.get(num).x, marks.get(num).y);
                        routeList = MapUtils.getShortestDistanceBetweenTwoPoints(locationLayer
                                .getCurrentPosition(), target, nodes, nodesContact);

                        routeLayer.setNodeList(nodes);
                        routeLayer.setRouteList(routeList);
                        mapView.refresh();
                    }
                });
                mapView.addLayer(markLayer);
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {

            }

            @Override
            public void onGetCurrentMap(Bitmap bitmap) {

            }
        });
    }

    private List<PointF> getNodesList() {
        List<PointF> nodes = new ArrayList<>();
        nodes.add(new PointF(222, 34));
        nodes.add(new PointF(268, 34));
        nodes.add(new PointF(314, 34));
        nodes.add(new PointF(359, 34));
        nodes.add(new PointF(406, 34));
        nodes.add(new PointF(455, 34));
        nodes.add(new PointF(500, 34));
        nodes.add(new PointF(547, 34));
        nodes.add(new PointF(590, 34));
        nodes.add(new PointF(630, 34));
        nodes.add(new PointF(229, 194));
        nodes.add(new PointF(268, 194));
        nodes.add(new PointF(314, 194));
        nodes.add(new PointF(359, 194));
        nodes.add(new PointF(406, 194));
        nodes.add(new PointF(455, 194));
        nodes.add(new PointF(500, 194));
        nodes.add(new PointF(547, 194));
        nodes.add(new PointF(590, 194));
        nodes.add(new PointF(630, 194));
        nodes.add(new PointF(425, 194));
        nodes.add(new PointF(229, 260));
        nodes.add(new PointF(425, 260));
        nodes.add(new PointF(630, 260));
        nodes.add(new PointF(229, 310));
        nodes.add(new PointF(425, 310));
        nodes.add(new PointF(630, 310));
        nodes.add(new PointF(229, 360));
        nodes.add(new PointF(425, 360));
        nodes.add(new PointF(630, 360));
        nodes.add(new PointF(229, 410));
        nodes.add(new PointF(425, 410));
        nodes.add(new PointF(630, 410));
        nodes.add(new PointF(229, 460));
        nodes.add(new PointF(425, 460));
        nodes.add(new PointF(630, 460));
        nodes.add(new PointF(229, 510));
        nodes.add(new PointF(425, 510));
        nodes.add(new PointF(630, 510));
        nodes.add(new PointF(229, 560));
        nodes.add(new PointF(425, 560));
        nodes.add(new PointF(571, 560));
        nodes.add(new PointF(229, 610));
        nodes.add(new PointF(425, 610));
        nodes.add(new PointF(571, 610));
        nodes.add(new PointF(229, 670));
        nodes.add(new PointF(425, 670));
        nodes.add(new PointF(571, 670));
        nodes.add(new PointF(240, 830));
        nodes.add(new PointF(280, 830));
        nodes.add(new PointF(320, 830));
        nodes.add(new PointF(390, 830));
        nodes.add(new PointF(475, 830));
        nodes.add(new PointF(560, 830));
        nodes.add(new PointF(620, 830));
        nodes.add(new PointF(320, 760));
        nodes.add(new PointF(390, 760));
        nodes.add(new PointF(475, 760));
        nodes.add(new PointF(560, 760));
        nodes.add(new PointF(280, 670));
        nodes.add(new PointF(320, 670));
        nodes.add(new PointF(475, 670));
        nodes.add(new PointF(620, 714));
        nodes.add(new PointF(1030, 210));
        nodes.add(new PointF(700, 210));
        nodes.add(new PointF(700, 620));
        nodes.add(new PointF(1030, 620));
        nodes.add(new PointF(1290, 200));
        nodes.add(new PointF(1290, 530));
        nodes.add(new PointF(1290, 840));
        nodes.add(new PointF(1090, 840));
        nodes.add(new PointF(700, 714));
        nodes.add(new PointF(1030, 0));

        return nodes;
    }

    private List<PointF> getNodesContactList() {
        List<PointF> nodesContact = new ArrayList<PointF>();
        nodesContact.add(new PointF(0, 1));
        nodesContact.add(new PointF(0, 10));
        nodesContact.add(new PointF(1, 2));
        nodesContact.add(new PointF(1, 11));
        nodesContact.add(new PointF(2, 3));
        nodesContact.add(new PointF(2, 12));
        nodesContact.add(new PointF(3, 4));
        nodesContact.add(new PointF(3, 13));
        nodesContact.add(new PointF(4, 5));
        nodesContact.add(new PointF(4, 14));
        nodesContact.add(new PointF(5, 6));
        nodesContact.add(new PointF(5, 15));
        nodesContact.add(new PointF(6, 7));
        nodesContact.add(new PointF(6, 16));
        nodesContact.add(new PointF(7, 8));
        nodesContact.add(new PointF(7, 17));
        nodesContact.add(new PointF(8, 9));
        nodesContact.add(new PointF(8, 18));
        nodesContact.add(new PointF(9, 19));
        nodesContact.add(new PointF(10, 11));
        nodesContact.add(new PointF(10, 21));
        nodesContact.add(new PointF(11, 12));
        nodesContact.add(new PointF(12, 13));
        nodesContact.add(new PointF(13, 14));
        nodesContact.add(new PointF(14, 15));
        nodesContact.add(new PointF(14, 20));
        nodesContact.add(new PointF(15, 16));
        nodesContact.add(new PointF(15, 20));
        nodesContact.add(new PointF(16, 17));
        nodesContact.add(new PointF(17, 18));
        nodesContact.add(new PointF(18, 19));
        nodesContact.add(new PointF(19, 23));
        nodesContact.add(new PointF(19, 64));
        nodesContact.add(new PointF(20, 22));
        nodesContact.add(new PointF(21, 22));
        nodesContact.add(new PointF(21, 24));
        nodesContact.add(new PointF(22, 25));
        nodesContact.add(new PointF(22, 23));
        nodesContact.add(new PointF(23, 26));
        nodesContact.add(new PointF(24, 25));
        nodesContact.add(new PointF(24, 27));
        nodesContact.add(new PointF(25, 26));
        nodesContact.add(new PointF(25, 28));
        nodesContact.add(new PointF(26, 29));
        nodesContact.add(new PointF(27, 28));
        nodesContact.add(new PointF(27, 30));
        nodesContact.add(new PointF(28, 29));
        nodesContact.add(new PointF(28, 31));
        nodesContact.add(new PointF(29, 32));
        nodesContact.add(new PointF(30, 31));
        nodesContact.add(new PointF(30, 33));
        nodesContact.add(new PointF(31, 32));
        nodesContact.add(new PointF(31, 34));
        nodesContact.add(new PointF(32, 35));
        nodesContact.add(new PointF(33, 34));
        nodesContact.add(new PointF(33, 36));
        nodesContact.add(new PointF(34, 35));
        nodesContact.add(new PointF(34, 37));
        nodesContact.add(new PointF(35, 38));
        nodesContact.add(new PointF(36, 37));
        nodesContact.add(new PointF(36, 39));
        nodesContact.add(new PointF(37, 38));
        nodesContact.add(new PointF(37, 40));
        nodesContact.add(new PointF(39, 40));
        nodesContact.add(new PointF(39, 42));
        nodesContact.add(new PointF(40, 41));
        nodesContact.add(new PointF(40, 43));
        nodesContact.add(new PointF(41, 44));
        nodesContact.add(new PointF(41, 65));
        nodesContact.add(new PointF(42, 43));
        nodesContact.add(new PointF(42, 45));
        nodesContact.add(new PointF(43, 44));
        nodesContact.add(new PointF(43, 46));
        nodesContact.add(new PointF(44, 47));
        nodesContact.add(new PointF(44, 65));
        nodesContact.add(new PointF(45, 48));
        nodesContact.add(new PointF(45, 59));
        nodesContact.add(new PointF(46, 60));
        nodesContact.add(new PointF(46, 61));
        nodesContact.add(new PointF(47, 61));
        nodesContact.add(new PointF(47, 62));
        nodesContact.add(new PointF(47, 65));
        nodesContact.add(new PointF(47, 58));
        nodesContact.add(new PointF(48, 49));
        nodesContact.add(new PointF(49, 50));
        nodesContact.add(new PointF(49, 59));
        nodesContact.add(new PointF(50, 55));
        nodesContact.add(new PointF(50, 51));
        nodesContact.add(new PointF(51, 52));
        nodesContact.add(new PointF(51, 56));
        nodesContact.add(new PointF(52, 53));
        nodesContact.add(new PointF(52, 57));
        nodesContact.add(new PointF(53, 54));
        nodesContact.add(new PointF(53, 58));
        nodesContact.add(new PointF(54, 62));
        nodesContact.add(new PointF(55, 56));
        nodesContact.add(new PointF(55, 60));
        nodesContact.add(new PointF(56, 57));
        nodesContact.add(new PointF(57, 61));
        nodesContact.add(new PointF(57, 58));
        nodesContact.add(new PointF(59, 60));
        nodesContact.add(new PointF(62, 71));
        nodesContact.add(new PointF(63, 67));
        nodesContact.add(new PointF(63, 72));
        nodesContact.add(new PointF(63, 64));
        nodesContact.add(new PointF(63, 66));
        nodesContact.add(new PointF(64, 65));
        nodesContact.add(new PointF(65, 66));
        nodesContact.add(new PointF(65, 71));
        nodesContact.add(new PointF(66, 68));
        nodesContact.add(new PointF(66, 70));
        nodesContact.add(new PointF(69, 70));

        return nodesContact;
    }

    private List<PointF> getMarks() {
        List<PointF> marks = new ArrayList<>();
        marks.add(new PointF(850, 135));
        marks.add(new PointF(720, 135));
        marks.add(new PointF(610, 135));
        marks.add(new PointF(435, 135));
        marks.add(new PointF(270, 135));
        marks.add(new PointF(320, 255));
        marks.add(new PointF(530, 255));
        marks.add(new PointF(320, 355));
        marks.add(new PointF(320, 480));
        marks.add(new PointF(320, 605));
        marks.add(new PointF(530, 355));
        marks.add(new PointF(530, 430));
        marks.add(new PointF(530, 505));
        marks.add(new PointF(500, 610));
        marks.add(new PointF(220, 765));
        marks.add(new PointF(260, 765));
        marks.add(new PointF(300, 765));
        marks.add(new PointF(360, 710));
        marks.add(new PointF(475, 710));
        marks.add(new PointF(353, 780));
        marks.add(new PointF(430, 780));
        marks.add(new PointF(580, 765));
        marks.add(new PointF(645, 780));
        marks.add(new PointF(800, 685));
        marks.add(new PointF(900, 685));
        marks.add(new PointF(990, 685));
        marks.add(new PointF(1140, 685));
        marks.add(new PointF(1140, 455));
        marks.add(new PointF(785, 525));
        marks.add(new PointF(836, 315));
        marks.add(new PointF(1140, 343));
        marks.add(new PointF(1140, 260));
        marks.add(new PointF(970, 310));
        marks.add(new PointF(190, 280));
        marks.add(new PointF(190, 410));
        marks.add(new PointF(190, 480));
        marks.add(new PointF(190, 550));
        marks.add(new PointF(630, 195));
        marks.add(new PointF(630, 635));
        marks.add(new PointF(1020, 40));

        return marks;
    }

    private List<String> getMarksName() {
        List<String> marksName = new ArrayList<>();
        marksName.add("欧麦卷饼");
        marksName.add("曹状元");
        marksName.add("日化");
        marksName.add("家居生活");
        marksName.add("纸品");
        marksName.add("膨化食品");
        marksName.add("饮料");
        marksName.add("休闲食品");
        marksName.add("方便食品");
        marksName.add("冲饮");
        marksName.add("酒水");
        marksName.add("面包");
        marksName.add("乳品");
        marksName.add("进口食品");
        marksName.add("调味品");
        marksName.add("包装调料");
        marksName.add("副食");
        marksName.add("散装食品");
        marksName.add("水果");
        marksName.add("鸡蛋");
        marksName.add("蔬菜");
        marksName.add("冷藏");
        marksName.add("冷饮");
        marksName.add("智多方工作室");
        marksName.add("鲜花礼品");
        marksName.add("博海文具");
        marksName.add("未开发1");
        marksName.add("未开发2");
        marksName.add("未来科技");
        marksName.add("双合城月饼");
        marksName.add("大明眼镜");
        marksName.add("COCO柠檬");
        marksName.add("七杯茶");
        marksName.add("卫生纸");
        marksName.add("熟肉");
        marksName.add("冷鲜肉");
        marksName.add("主食厨房");
        marksName.add("超市入口");
        marksName.add("超市出口");
        marksName.add("商场出入口");

        return marksName;
    }


    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public Picture getPictureFromBitmap(Bitmap bitmap) {
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(bitmap.getWidth(),
                bitmap.getHeight());
        canvas.drawBitmap(
                bitmap,
                null,
                new RectF(0f, 0f, (float) bitmap.getWidth(), (float) bitmap
                        .getHeight()), null);
        picture.endRecording();
        return picture;
    }
}
