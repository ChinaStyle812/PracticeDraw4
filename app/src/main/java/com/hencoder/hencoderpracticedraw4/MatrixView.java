package com.hencoder.hencoderpracticedraw4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lenovo on 2017/8/5.
 */

public class MatrixView extends View {

    private Bitmap bitmap;
    private Paint paint;
    private Matrix matrix;
    private Path path;
    private Paint pointPaint;
    private int pointRaduis;

    public MatrixView(Context context) {
        super(context);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        matrix = new Matrix();
        path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setColor(Color.GRAY);
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointRaduis = 24;
        pointPaint.setStrokeWidth(pointRaduis * 2);
        pointPaint.setColor(Color.LTGRAY);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private float[] src;
    private float[] dst;
    private Point[] points;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int left = cx - bitmap.getWidth() / 2;
        int right = left + bitmap.getWidth();
        int top = cy - bitmap.getHeight() / 2;
        int bottom = top + bitmap.getHeight();
        if (src == null) {
            src = new float[] {left, top, right, top, left, bottom, right, bottom};
            dst = new float[] {left, top, right, top, left, bottom, right, bottom};
        }
        if (points == null) {
            points = new Point[4];
            points[0] = new Point((int)(dst[0] - 10), (int)(dst[1] - 10));
            points[1] = new Point((int)(dst[2] + 10), (int)(dst[3] - 10));
            points[2] = new Point((int)(dst[4] - 10), (int)(dst[5] + 10));
            points[3] = new Point((int)(dst[6] + 10), (int)(dst[7] + 10));
        } else {
            dst = new float[] {points[0].x, points[0].y,
                    points[1].x, points[1].y,
                    points[2].x, points[2].y,
                    points[3].x, points[3].y};
        }

        matrix.reset();
        matrix.setPolyToPoly(src, 0, dst, 0, 4);

        path.reset();
        path.moveTo(points[0].x, points[0].y);
        path.lineTo(points[1].x, points[1].y);
        path.lineTo(points[3].x, points[3].y);
        path.lineTo(points[2].x, points[2].y);
        path.close();
        canvas.save();
        canvas.concat(matrix);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, left, top, paint);
        canvas.restore();

        for (Point p : points) {
            canvas.drawPoint(p.x, p.y, pointPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        TouchData touchData;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchData = touchInCtrlPoint(event.getX(), event.getY());
                handled = touchData.touched;
                break;
            case MotionEvent.ACTION_MOVE:
                touchData = touchInCtrlPoint(event.getX(), event.getY());
                if (touchData.touched) {
                    handled = true;
                    touchData.ctrlPoint.set((int)event.getX(), (int)event.getY());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }

        return handled;
    }

    private TouchData touchInCtrlPoint(float x, float y) {
        TouchData touchData = new TouchData();
        touchData.touched = false;
        for (Point p : points) {
            if (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) <= pointRaduis) {
                touchData.touched = true;
                touchData.ctrlPoint = p;
                break;
            }
        }

        return touchData;
    }

    private class TouchData {
        boolean touched;
        Point ctrlPoint;
    }
}
