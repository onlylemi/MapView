package com.onlylemi.mapview.library.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.utils.math.FloydAlgorithm;
import com.onlylemi.mapview.library.utils.math.GeneticAlgorithm;
import com.onlylemi.mapview.library.utils.math.TSPNearestNeighbour;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * MapMath
 *
 * @author onlylemi
 */
public final class MapMath {

    private MapMath() {}

    /**
     * Represents 1 second in nanoseconds
     */
    public static long NANOSECOND = 1000000000;

    /**
     * Represetns 1 second in miliseconds
     */
    public static long MILISECOND = 1000;


    /**
     * the distance between two points
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float getDistanceBetweenTwoPoints(float x1, float y1,
                                                    float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * the distance between two points
     *
     * @param start
     * @param end
     * @return
     */
    public static float getDistanceBetweenTwoPoints(PointF start, PointF end) {
        return (float) Math.sqrt(Math.pow(end.x - start.x, 2)
                + Math.pow(end.y - start.y, 2));
    }


    /**
     * the shortest path between two points (FloydAlgorithm)
     *
     * @param begin
     * @param end
     * @param matrix adjacency matrix
     * @return
     */
    public static List<Integer> getShortestPathBetweenTwoPoints(int begin,
                                                                int end, float[][] matrix) {
        return FloydAlgorithm.getInstance().findCheapestPath(begin, end, matrix);
    }

    /**
     * the best path between some points (NearestNeighbour tsp)
     *
     * @param matrix adjacency matrix
     * @return
     */
    public static List<Integer> getBestPathBetweenPointsByNearestNeighbour(float[][] matrix) {
        return TSPNearestNeighbour.getInstance().tsp(matrix);
    }

    /**
     * the best path between some points (GeneticAlgorithm tsp)
     *
     * @param matrix
     * @return
     */
    public static List<Integer> getBestPathBetweenPointsByGeneticAlgorithm(float[][] matrix) {
        GeneticAlgorithm ga = GeneticAlgorithm.getInstance();
        ga.setAutoNextGeneration(true);
        ga.setMaxGeneration(200);
        int[] best = ga.tsp(matrix);

        List<Integer> result = new ArrayList<>(best.length);
        for (int i = 0; i < best.length; i++) {
            result.add(best[i]);
        }
        return result;
    }


    /**
     * get the angle between two points and the horizontal plane
     *
     * @param start
     * @param end
     * @return
     */
    public static float getDegreeBetweenTwoPointsWithHorizontal(PointF start, PointF end) {
        float angle = 90.0f;
        if (start.x != end.x) {
            angle = (float) Math.toDegrees(Math.atan((end.y - start.y)
                    / (end.x - start.x)));
            if (end.x < start.x && end.y >= start.y) {
                angle = angle + 180.0f;
            } else if (end.x < start.x && end.y > start.y) {
                angle = angle - 180.f;
            }
        } else {
            if (start.y < end.y) {
                angle = 90.0f;
            } else if (start.y > end.y) {
                angle = -90.0f;
            }
        }
        return angle;
    }

    /**
     * get the angle between two points and the vertical plane
     *
     * @param start
     * @param end
     * @return
     */
    public static float getDegreeBetweenTwoPointsWithVertical(PointF start, PointF end) {
        float angle = 90.0f;
        if (start.y != end.y) {
            angle = -(float) Math.toDegrees(Math.atan((end.x - start.x)
                    / (end.y - start.y)));
            if (end.y > start.y && end.x >= start.x) {
                angle = angle + 180.0f;
            } else if (end.y > start.y && end.x > start.x) {
                angle = angle - 180.f;
            }
        } else {
            if (start.x < end.x) {
                angle = 90.0f;
            } else if (start.x > end.x) {
                angle = -90.0f;
            }
        }
        return angle;
    }

    /**
     * get degree between two points
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float getDegreeBetweenTwoPoints(float x1, float y1, float x2, float y2) {
        double radians = Math.atan2(y1 - y2, x1 - x2);
        return (float) Math.toDegrees(radians);
    }

    /**
     * get degree between two points
     *
     * @param start
     * @param end
     * @return
     */
    public static float getDegreeBetweenTwoPoints(PointF start, PointF end) {
        return getDegreeBetweenTwoPoints(start.x, start.y, end.x, end.y);
    }

    /**
     * The coordinates of the midpoint between two points are obtained
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static PointF getMidPointBetweenTwoPoints(float x1, float y1, float x2, float y2) {
        return new PointF((x1 + x2) / 2, (y1 + y2) / 2);
    }

    /**
     * The coordinates of the midpoint between two points are obtained
     *
     * @param start
     * @param end
     * @return
     */
    public static PointF getMidPointBetweenTwoPoints(PointF start, PointF end) {
        return getMidPointBetweenTwoPoints(start.x, start.y, end.x, end.y);
    }

    /**
     * Get the coordinates of any point between two points
     *
     * @param start
     * @param end
     * @param value
     * @return
     */
    public static PointF getEveryPointBetweenTwoPoints(PointF start, PointF end, float value) {
        // y=kx+b
        float x, y;
        // with slope
        if (start.x != end.x) {
            float k = (end.y - start.y) / (end.x - start.x);
            float b = end.y - k * end.x;

            if (end.x > start.x) {
                x = Math.min(end.x, start.x) + (end.x - start.x) * value;
            } else {
                x = Math.max(end.x, start.x) + (end.x - start.x) * value;
            }
            y = k * x + b;
        } else { // no slope
            x = start.x;
            if (end.y > start.y) {
                y = Math.min(end.y, start.y) + (end.y - start.y) * value;
            } else {
                y = Math.max(end.y, start.y) + (end.y - start.y) * value;
            }
        }
        return new PointF(x, y);
    }


    /**
     * Get a shortest distance from point to line
     *
     * @param point
     * @param linePoint1 Determine the first point of a straight line
     * @param linePoint2 Determine the second point of a straight line
     * @return
     */
    public static float getDistanceFromPointToLine(PointF point, PointF linePoint1, PointF
            linePoint2) {
        // y = kx + b;
        // d = |kx-y+b| / √(k^2+1)
        float d;
        if (linePoint1.x != linePoint2.x) { // with slope
            float k = (linePoint2.y - linePoint1.y) / (linePoint2.x - linePoint1.x);
            float b = linePoint2.y - k * linePoint2.x;
            d = Math.abs(k * point.x - point.y + b) / (float) Math.sqrt(k * k + 1);
        } else { // no slope
            d = Math.abs(point.x - linePoint1.x);
        }
        return d;
    }

    /**
     * get intersection coordinates from a point to a line
     *
     * @param point
     * @param linePoint1
     * @param linePoint2
     * @return
     */
    public static PointF getIntersectionCoordinatesFromPointToLine(PointF point, PointF linePoint1, PointF
            linePoint2) {
        // y = kx + b;
        float x, y;
        if (linePoint1.x != linePoint2.x) { // with slope
            float k = (linePoint2.y - linePoint1.y) / (linePoint2.x - linePoint1.x);
            float b = linePoint2.y - k * linePoint2.x;
            // The equation of point
            if (k != 0) {
                float kV = -1 / k;
                float bV = point.y - kV * point.x;
                x = (b - bV) / (kV - k);
                y = kV * x + bV;
            } else {
                x = point.x;
                y = linePoint1.y;
            }
        } else { // no slope
            x = linePoint1.x;
            y = point.y;
        }
        return new PointF(x, y);
    }

    /**
     * is/not obtuse angle between a point and a line
     *
     * @param point
     * @param linePoint1
     * @param linePoint2
     * @return
     */
    public static boolean isObtuseAnglePointAndLine(PointF point, PointF linePoint1, PointF
            linePoint2) {
        // A*A + B*B < C*C
        float p_l1, p_l2, l1_l2;
        p_l1 = getDistanceBetweenTwoPoints(point, linePoint1);
        p_l2 = getDistanceBetweenTwoPoints(point, linePoint2);
        l1_l2 = getDistanceBetweenTwoPoints(linePoint1, linePoint2);

        return ((p_l1 * p_l1 + l1_l2 * l1_l2) < p_l2 * p_l2)
                || ((p_l2 * p_l2 + l1_l2 * l1_l2) < p_l1 * p_l1);
    }

    /**
     * Creates a mapping matrix that is used to transform a point from the input coordinate system to graphics coordinate system
     * topLeft 0,0 and botRight mapview.image.width, mapview.image.height is a mapping across the entire image
     * @param width of the input coordinate system (X)
     * @param height of the input cooridnate system (Y)
     * @param mapViewTopLeft topLeft corner on the mapview where the input coordinate system starts.
     * @param mapViewBotRight botLeft corner on the mapview where the input cooridnate system starts.
     * @return the transform matrix
     */
    public static Matrix createMappingMatrix(Bitmap map, float width, float height, PointF mapViewTopLeft, PointF mapViewBotRight) {
        //X scale remove the offsets
        float scaleX = (map.getWidth() - mapViewTopLeft.x - (map.getWidth() - mapViewBotRight.x)) / width;
        //Y scale
        float scaleY = (map.getHeight() - mapViewTopLeft.y - (map.getHeight() - mapViewBotRight.y)) / height;

        Matrix mappingMatrix = new Matrix();

        //Set scale
        mappingMatrix.setScale(scaleX, scaleY);

        //Translate
        mappingMatrix.postTranslate(mapViewTopLeft.x, mapViewTopLeft.y);

        return mappingMatrix;
    }

    /**
     * Transforms a point using the matrix
     * This is needed bacause Andorid graphics lib is stupid
     * @param m transform
     * @param point position
     * @return transformed position
     */
    public static PointF transformPoint(Matrix m, PointF point) {
        float[] p = { point.x, point.y };
        m.mapPoints(p, p);

        return new PointF(p[0], p[1]);
    }

    /**
     * Multiplies matrix a and b and returns the result
     * @param a
     * @param b
     * @return
     */
    public static float[] matrixMultiplication(Matrix a, Matrix b) {
        float[] A = new float[9];
        float[] B = new float[9];

        a.getValues(A);
        b.getValues(B);

        float[] result = new float[9];
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
        return result;
    }

    /**
     *  Determines the shortest angle between 2 angles. Assumes 360 and not 180 to -180
     *  Translates destination 0 to 360 to return the correct sign
     */
    public static float shortestAngleBetweenAngles(float start, float dst) {
        float a = (dst == 0 ? 360 : dst) - start;
        return Math.abs((a + 180) % 360) - 180;
    }

    /**
     * Truncates a value between a min and a max. Can never be higher or lower then the input min/max
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float truncateNumber(float value, float min, float max) {
        if(value > max)
            return max;
        else if(value < min)
            return min;

        return value;
    }

    public static float max(float v1, float v2) {
        return v1 > v2 ? v1 : v2;
    }

    public static float min(float v1, float v2) {
        return v1 < v2 ? v1: v2;
    }

    public static PointF normalize(PointF outPoint) {
        float length = outPoint.length();
        outPoint.x /= length;
        outPoint.y /= length;
        return outPoint;
    }
    public static PointF pointSubtract(PointF p1, PointF p2) {
        return new PointF(p1.x - p2.x, p1.y - p2.y);
    }

}
