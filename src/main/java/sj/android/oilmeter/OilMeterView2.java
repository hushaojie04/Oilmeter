package sj.android.oilmeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/28.
 */
public class OilMeterView2 extends ImageView {
    Bitmap mBitmap;
    float mAngle = 0;
    float currentAngle = 0;
    Map<Integer, Integer> oilAngles = new HashMap<Integer, Integer>();
    Matrix default_Matrix;

    public void setAngle(float angle) {
        mAngle = angle;
        synchronized (wait_object) {
            Log.d("husj", " notify " + mAngle);
            wait_object.notify();
        }
    }

    public void setFloatToAngle(float f) {
        int i = (int) f;
        mAngle = oilAngles.get(i);
        if (i > 10) return;
        else if (i < 10) {
            mAngle += (f - i) * (oilAngles.get(i + 1) - mAngle);
        }
        synchronized (wait_object) {
            Log.d("husj", " notify " + f + " " + mAngle);
            wait_object.notify();
        }
    }

    public OilMeterView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        oilAngles.put(0, 0);
        oilAngles.put(1, 25);
        oilAngles.put(2, 50);
        oilAngles.put(3, 75);
        oilAngles.put(4, 100);
        oilAngles.put(5, 124);
        oilAngles.put(6, 148);
        oilAngles.put(7, 173);
        oilAngles.put(8, 198);
        oilAngles.put(9, 222);
        oilAngles.put(10, 248);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setScaleType(ImageView.ScaleType.MATRIX);   //required
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.point);
        if (!isRunning) {
            isRunning = true;
            new Thread(new MyRunnable()).start();
        }
        default_Matrix = new Matrix();
        default_Matrix.setRotate(315, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        setImageMatrix(default_Matrix);
    }


    Object wait_object = new Object();
    boolean isRunning = false;

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                Log.d("husj", "MyRunnable " + currentAngle + " " + mAngle);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Math.abs(currentAngle - mAngle) < 1) {
                    currentAngle = mAngle;
                    post(myUIRunnable);
                    try {
                        synchronized (wait_object) {
                            Log.d("husj", " wait ");
                            wait_object.wait();
                        }
                    } catch (InterruptedException e) {

                    }

                } else if (currentAngle > mAngle) {
                    currentAngle -= 1;
                    if (currentAngle < 0) {
                        currentAngle = 0;
                    }
                } else if (currentAngle < mAngle) {
                    currentAngle += 1;
                    if (currentAngle > 360) {
                        currentAngle = 360;
                    }
                }
//                postInvalidate();
                post(myUIRunnable);
            }


        }
    }

    private void rotate() {
        Matrix matrix = new Matrix();
        matrix.setRotate((float) currentAngle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        matrix.preConcat(default_Matrix);
        setImageMatrix(matrix);
    }

    Runnable myUIRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("husj", " myUIRunnable ");
            rotate();
        }
    };
}
