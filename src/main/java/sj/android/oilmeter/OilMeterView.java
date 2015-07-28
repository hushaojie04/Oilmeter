package sj.android.oilmeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.jar.Attributes;

/**
 * Created by Administrator on 2015/7/27.
 */
public class OilMeterView extends ImageView {
    Bitmap oilmeterBitmap;
    Bitmap pointBitmap;
    byte[] oilmeterByte;
    byte[] pointByte;
    int[] oilmeterInt;
    int[] pointInt;
    State mState;
    float mAngle = 0;
    float currentAngle = 0;
    float centerx;
    float centery;
    int radius;
    Map<Integer, Integer> oilAngles = new HashMap<Integer, Integer>();

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

    public OilMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        oilmeterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.olimeter);
        oilmeterByte = Bitmap2Bytes(oilmeterBitmap);
        pointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.point);
        centerx = pointBitmap.getWidth() / 2;
        centery = pointBitmap.getHeight() / 2;
        pointByte = Bitmap2Bytes(pointBitmap);
        oilmeterInt = new int[oilmeterBitmap.getHeight() * oilmeterBitmap.getWidth()];
        oilmeterBitmap.getPixels(oilmeterInt, 0, oilmeterBitmap.getWidth(), 0, 0, oilmeterBitmap.getWidth(), oilmeterBitmap.getHeight());


        pointInt = new int[pointBitmap.getHeight() * pointBitmap.getWidth()];
        int[] temp = new int[pointBitmap.getHeight() * pointBitmap.getWidth()];
        pointBitmap.getPixels(temp, 0, pointBitmap.getWidth(), 0, 0, pointBitmap.getWidth(), pointBitmap.getHeight());
        //初始化角度。
        rorateInt(pointInt, temp, 314);

        mState = new State(oilmeterInt);
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

//        for (int i = 0; i < pointInt.length; i++) {
//            int x = i % pointBitmap.getWidth();
//            int y = i / pointBitmap.getHeight();
//            if (pointInt[i] != 0) {
//                double r = Math.sqrt((x - centerx) * (x - centerx) + (y - centery) * (y - centery));
//                if (r > radius) {
//                    radius = (int) r;
//                }
//            }
//        }


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isRunning) {
            isRunning = true;
            new Thread(new MyRunnable()).start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);    //消除锯齿
        mState.pushStack();
//        Log.d("husj", "0 " + pointBitmap.getHeight() + " " + pointBitmap.getWidth());
//        Log.d("husj", "0 " + pointByte.length + " " + pointBitmap.getHeight() * pointBitmap.getWidth());
        int[] rorate = new int[pointInt.length];
        rorateInt(rorate, pointInt, currentAngle);
        mState.addState(rorate);
        Bitmap bb = Bitmap.createBitmap(mState.getState(), pointBitmap.getWidth(), pointBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(bb, 0, 0, paint);
        mState.popStack();
//        bb = Bitmap.createBitmap(rorate, pointBitmap.getWidth(), pointBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        canvas.drawBitmap(bb, 0, 0, paint);

//        canvas.drawCircle(centerx,centery,radius,paint);

//        Log.d("husj", "radius=" + radius);
//        double tmpy = radius;
//        double tmpx = 0;
//        boolean isNoBlack;
//        int count = 0;
//        int ccc = 0;
//        for (double angle = 0f; angle < 360f; angle += 0.1f) {
//            double x = (double) ((tmpx) * Math.cos(angle) - (tmpy) * Math.sin(angle));
//            double y = (double) ((tmpx) * Math.sin(angle) + (tmpy) * Math.cos(angle));
//            int index = (int) ((x + centerx) + (y + centery) * oilmeterBitmap.getWidth());
//            int color = oilmeterInt[index];
//            int pixR = Color.red(color);
//            int pixG = Color.green(color);
//            int pixB = Color.blue(color);
//            if (color == 0xff000000) {
//                count++;
//            } else {
//                count = 0;
//            }
//            if (color == 0xffffe107) {
//                paint.setColor(Color.BLUE);
//                canvas.drawPoint((float) (x + centerx), (float) (y + centery), paint);
//            }
//            Log.d("husj", index + " color=" + Integer.toHexString(color) + " " + angle);
//
////            if (pixR == 0xff && pixG > 0xe0 && pixG < 0xef)
////                Log.d("husj", "color=" + Integer.toHexString(color) + " " + angle);
//        }
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
                postInvalidate();
            }

        }
    }

    private void calculate() {

    }

    public void rorateInt(int[] array, int[] srcArray, float depress) {
        int[][] temp = new int[pointBitmap.getWidth()][pointBitmap.getHeight()];
        for (int i = 0; i < srcArray.length; i++) {
            temp[i % pointBitmap.getWidth()][i / pointBitmap.getWidth()] = srcArray[i];
        }

        for (int i = 0; i < pointBitmap.getWidth(); i++) {
            for (int j = 0; j < pointBitmap.getHeight(); j++) {
                if (temp[i][j] != 0) {
                    double dp = Math.toRadians(depress);
                    int x = (int) ((i - centerx) * Math.cos(dp) - (j - centery) * Math.sin(dp));
                    int y = (int) ((i - centerx) * Math.sin(dp) + (j - centery) * Math.cos(dp));
                    int index = (int) ((x + centerx) + (y + centery) * pointBitmap.getWidth());
                    array[index] = temp[i][j];
                    index = (int) ((x + 1 + centerx) + (y + 1 + centery) * pointBitmap.getWidth());
                    array[index] = 0xffffe107;
                }

            }
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int scaleWidth, int scaleHeight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) scaleWidth / w);
        float sy = ((float) scaleHeight / h);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap scaleBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return scaleBmp;
    }

    public Bitmap rorateBitmap(Bitmap bitmap, float angle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
//        matrix = matrix.setConcat()
        // 计算缩放比例
        // 设置缩放比例
//        matrix.postRotate(angle);
        matrix.postRotate(angle, centerx , centery );
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap scaleBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return scaleBmp;
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public void pp() {
        try {
            int count = 0;
            byte[] buffer = new byte[1024];
            //等号后面是图片的路径
            //下面是将图片读成字节，而且是以1024为单位读的
            InputStream inputStream = getResources().openRawResource(R.raw.point);
//定义整数类型对象
            int number;
            //利用循环方式将图片读完整
            while ((number = inputStream.read(buffer)) > 0) {
                count += number;
            }
            Log.d("husj", "0 count=" + count);
        } catch (IOException e) {

        }
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    class State {
        int max_num = 10;
        Stack<int[]> stack = new Stack<>();
        int[] currentByte;

        public State(int[] bytes) {
            currentByte = bytes;
        }

        public void addState(int[] bytes) {
            for (int i = 0; i < currentByte.length; i++) {
                if (i >= bytes.length) break;
                if (bytes[i] != 0) {
                    currentByte[i] = bytes[i];
                    int x = i % pointBitmap.getWidth();
                    int y = i / pointBitmap.getWidth();
                }
            }
        }

        public int[] getState() {
            return currentByte;
        }

        private void pushStack() {
            //保存现场
            int[] temp = new int[currentByte.length];
            for (int i = 0; i < currentByte.length; i++) {
                temp[i] = currentByte[i];
            }
            stack.push(temp);
        }

        private void popStack() {
            //恢复现象
            //保存现场
            int[] temp = stack.pop();
            for (int i = 0; i < currentByte.length; i++) {
                currentByte[i] = temp[i];
            }
        }
    }
}
