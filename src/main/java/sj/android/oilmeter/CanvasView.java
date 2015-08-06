package sj.android.oilmeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Vector;

/**
 * Created by Administrator on 2015/8/4.
 */
public class CanvasView extends View {
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        createPOLYGASKET(canvas, 5,5, new Vector2D(400, 400, 0, 200));
    }

    /**
     * 分形曲线，多边形，地垫
     * @param canvas
     * @param level
     * @param vector2D
     */
    private void createPOLYGASKET(Canvas canvas,int n, int level, Vector2D vector2D) {
        if (level == 0) {
            createPolygon(canvas, n, vector2D);
        } else {
            for(int i=0;i<n;i++)
            {
                vector2D.resize(1 / 2f);
                createPOLYGASKET(canvas, n, level - 1, vector2D);
                vector2D.resize(2f);
                vector2D.move(1);
                vector2D.turn((float) (2 * Math.PI / n));
                RectFloat rect = vector2D.getRect();
                Paint paint = new Paint();
                canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
            }
        }
    }

    private void createwalk(Canvas canvas, int n, Vector2D vector2D) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        for (int i = 0; i < n; i++) {
        vector2D.resize(1 / 4f);
        vector2D.turn((float) (Math.PI / 3));
        RectFloat rect = vector2D.getRect();
        Log.d("husj", "1" + rect.toString());
        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);


        vector2D.resize(1 / 4f);
        vector2D.turn((float) (Math.PI / 4));
        rect = vector2D.getRect();
        Log.d("husj", "2" + rect.toString());

        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);

        vector2D.move(1);
        vector2D.resize(1 / 3f);
        vector2D.turn((float) (-Math.PI / 2));
        rect = vector2D.getRect();
        Log.d("husj", "3" + rect.toString());

        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);

        vector2D.move(1);
        vector2D.resize(3 / 5f);
        vector2D.turn((float) (Math.PI / 6));
        rect = vector2D.getRect();
        Log.d("husj", "4" + rect.toString());
        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
//        }
    }
    /*
       多角星
     */
    private void createStar(Canvas canvas, int n, Vector2D vector2D) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < n; i++) {
            vector2D.move(1);
            vector2D.turn((float) (4 * Math.PI / n));
            RectFloat rect = vector2D.getRect();
            Log.d("husj", rect.toString());
            canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
        }
    }
    /*
     轮形
     */
    private void createWheel(Canvas canvas, Vertex2D vertex2D, float radius, int num) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float angle = (float) ((2 * Math.PI) / (num - 1));
        ;
        Vertex2D tmp = new Vertex2D(0, radius);
        for (int i = 0; i < num; i++) {
            float x = (float) (tmp.x * Math.cos(angle) - tmp.y * Math.sin(angle));
            float y = (float) (tmp.x * Math.sin(angle) + tmp.y * Math.cos(angle));
//            canvas.drawLine(vertex2D.x, vertex2D.y, x, y, paint);
            canvas.drawLine(vertex2D.x, vertex2D.y, x + vertex2D.x, y + vertex2D.y, paint);
            canvas.drawLine(tmp.x + vertex2D.x, tmp.y + vertex2D.y, x + vertex2D.x, y + vertex2D.y, paint);
            tmp.x = x;
            tmp.y = y;
            Log.d("husj", "angle=" + angle);
        }
    }
    /*
    玫瑰花结
     */
    private void create5Golden(Canvas canvas, Vertex2D center) {
        int radius = 50;
        int num = 20;
        float angle = 0;
        Vertex2D vertex2D = new Vertex2D(0, radius);
        for (int i = 0; i < 5; i++) {
            float x = (float) (vertex2D.x * Math.cos(angle) - vertex2D.y * Math.sin(angle));
            float y = (float) (vertex2D.x * Math.sin(angle) + vertex2D.y * Math.cos(angle));
            createGolden(canvas, new Vertex2D(x + center.x, y + center.y), radius, num);
            Log.d("husj", "x y=" + x + " " + y);

            angle += (float) ((2 * Math.PI) / 5);
        }

    }
    /*
      玫瑰花
       */
    private void createGolden(Canvas canvas, Vertex2D center, float radius, int num) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float angle = (float) ((2 * Math.PI) / (num - 1));
        ;
        Vertex2D tmp = new Vertex2D(0, radius);
        Vertex2D[] vertex2Ds = new Vertex2D[num + 1];
        for (int i = 0; i < num; i++) {
            float x = (float) (tmp.x * Math.cos(angle) - tmp.y * Math.sin(angle));
            float y = (float) (tmp.x * Math.sin(angle) + tmp.y * Math.cos(angle));
            vertex2Ds[i] = new Vertex2D(x, y);
            tmp.x = vertex2Ds[i].x;
            tmp.y = vertex2Ds[i].y;
            Log.d("husj", "angle=" + angle);
        }
        vertex2Ds[num] = new Vertex2D(0, 0);
        for (int i = 0; i < num + 1; i++) {
            for (int j = i; j < num + 1; j++) {
                canvas.drawLine(vertex2Ds[i].x + center.x, vertex2Ds[i].y + center.y, vertex2Ds[j].x + center.x, vertex2Ds[j].y + center.y, paint);
            }
        }

    }
    /*
    正多边形
     */
    private void createPolygon(Canvas canvas, int n, Vector2D vector2D) {
        Log.d("husj", "createPolygon");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < n; i++) {
            RectFloat rect = vector2D.getRect();
            Log.d("husj", rect.toString());
            canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
            vector2D.move(1);
            vector2D.turn((float) (2 * Math.PI / n));
        }
    }
    /*
    螺旋
     */
    private void createSpiral(Canvas canvas, int n, Vector2D vector2D) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < n; i++) {
            vector2D.move(1);
            vector2D.turn((float) (4 * Math.PI / 5));
            vector2D.resize(0.95f);
            RectFloat rect = vector2D.getRect();
            Log.d("husj", rect.toString());
            canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
        }
    }

    class Vertex2D {
        public float x = 0, y = 0;

        public Vertex2D(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class Vector2D {
        public float rx = 0, ry = 0;
        private float _x1 = 0, _y1 = 0;
        private float _x0 = 0, _y0 = 0;

        private float angle = 0;
        private float length = 0;

        public Vector2D(float x0, float y0, float x, float y) {
            _x0 = x0;
            _y0 = y0;
            rx = x;
            ry = y;
        }

        public float length() {
            return (float) Math.sqrt(rx * rx + ry * ry);
        }

        public void move(int x, int y) {
            _x0 = _x0 + x;
            _y0 = _y0 + y;
        }

        public void move(float anthor) {
            _x0 = _x0 + anthor * rx;
            _y0 = _y0 + anthor * ry;
        }

        public void resize(float scale) {
            length = length * scale;
            rx = rx * scale;
            ry = ry * scale;
            _x1 = _x0 + rx;
            _y1 = _y0 + ry;
        }

        public RectFloat getRect() {
            _x1 = _x0 + rx;
            _y1 = _y0 + ry;
            return new RectFloat(_x0, _y0, _x1, _y1);
        }

        public void turn(float angle) {
            float tempx = rx, tempy = ry;
            rx = (float) (tempx * Math.cos(angle) - tempy * Math.sin(angle));
            ry = (float) (tempx * Math.sin(angle) + tempy * Math.cos(angle));
            _x1 = _x0 + rx;
            _y1 = _y0 + ry;
            this.angle = angle;
        }
    }

    class RectFloat {
        public float left;
        public float top;
        public float right;
        public float bottom;

        public RectFloat(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Rect(");
            sb.append(left);
            sb.append(", ");
            sb.append(top);
            sb.append(" - ");
            sb.append(right);
            sb.append(", ");
            sb.append(bottom);
            sb.append(")");
            return sb.toString();
        }
    }

    class Matrix2D {
        float[][] mMatrix = new float[3][3];

        public void normalize() {

        }
    }
}
