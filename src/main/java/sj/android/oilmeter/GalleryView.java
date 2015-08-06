package sj.android.oilmeter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2015/7/30.
 */
public class GalleryView extends View {
    Image[] mData;
    Context mContext;
    float itemWidth = 0;

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAsyncTask.execute();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    AsyncTask<String, Integer, String> mAsyncTask = new AsyncTask<String, Integer, String>() {
        @Override
        protected String doInBackground(String... params) {
            Context context = mContext;
            if (context != null) {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, null);
                if (cursor != null) {
                    mData = new Image[cursor.getCount()];
                    while (cursor.moveToNext()) {
                        int id = cursor
                                .getInt(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        String title = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                        String path = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        String displayName = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                        String mimeType = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                        long size = cursor
                                .getLong(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                        Image image = new Image(id, title, displayName, mimeType,
                                path, size);
                        mData[cursor.getPosition()] = image;
                        publishProgress(cursor.getPosition());
                    }
                    cursor.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Thread(new LoadBitmapCacheRunnable()).start();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress = values[0];
            if (progress > 5) {
                invalidate();
            }
        }
    };
    int mCurrentPosition = 0;
    int progress = -1;
    int space = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemWidth == 0)
            itemWidth = getMeasuredWidth() / 3;
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        paint.setAntiAlias(true);    //
        float left = scrollX, top = 0;
        top = getMeasuredHeight() / 3;
        int position = 0;

        if (scrollX > 0) {//to right
            left = scrollX - itemWidth;
        } else if (scrollX < 0) {//to left
            left = scrollX;
        }
        String xx = "";
        if (mDoubleLink.size() == 3 && mDoubleLink.get(0).index > mCurrentPosition) {
            left = scrollX;
        }
        for (int i = 0; i < 4; i++) {
            if (mDoubleLink.get(i) == null) continue;
            Bitmap bb = mDoubleLink.get(i).mBitmap;
            xx = xx + "{" + left + " " + mDoubleLink.get(i).index + "}";
            canvas.drawBitmap(bb, left, top, paint);
            left += bb.getWidth();
        }
        Log.d("husj", "Bitmap " + xx);


    }


    float x0, y0;
    float scrollX = 0;
    float lastScrollX = 0;
    private VelocityTracker vTracker = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (vTracker == null) {
                    vTracker = VelocityTracker.obtain();
                } else {
                    vTracker.clear();
                }
                vTracker.addMovement(event);
                x0 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                vTracker.addMovement(event);
                vTracker.computeCurrentVelocity(1000);
                scrollX += event.getX() - x0;
                x0 = event.getX();
//                Log.d("husj", "" + scrollX + " " + lastScrollX);

                if (Math.abs(scrollX) >= itemWidth || (lastScrollX != 0 && (lastScrollX > 0) ^ (scrollX > 0))) {
                    Log.d("husj", "scrollX 1 " + scrollX);

                    Log.d("husj", "scrollX 2 " + scrollX);
                    if (vTracker.getXVelocity() > 0) {
                        Log.d("husj", "mCurrentPosition--");
                        if (mCurrentPosition > 0) {
                            mCurrentPosition--;
                            synchronized (waitObject) {
                                waitObject.notify();
                            }
                        }
                    } else {
                        Log.d("husj", "mCurrentPosition++");
                        if (mCurrentPosition < progress) {
                            mCurrentPosition++;
                            synchronized (waitObject) {
                                waitObject.notify();
                            }
                        }
                    }
                    scrollX = 0;
                }
                lastScrollX = scrollX;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;

    }

    class BitmapInfo {
        Bitmap mBitmap;
        int index;
    }

    DoubleLink<BitmapInfo> mDoubleLink = new DoubleLink<BitmapInfo>(4);
    final Object waitObject = new Object();

    class LoadBitmapCacheRunnable implements Runnable {
        private int mPosition = -1;

        @Override
        public void run() {
            while (true) {
                if (itemWidth == 0)
                    itemWidth = getMeasuredWidth() / 3;
                Log.d("husj", "mPosition " + mPosition);
                Log.d("husj", "mCurrentPosition " + mCurrentPosition);
                if (mPosition >= 0 && mPosition < mCurrentPosition) {
                    mDoubleLink.deleteFirst();
                    int position = mCurrentPosition + 3;
                    Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[position].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                    BitmapInfo info = new BitmapInfo();
                    info.mBitmap = bb;
                    info.index = position;
                    mDoubleLink.addLast(info);
                } else if (mPosition > mCurrentPosition) {
                    int position = mCurrentPosition;
                    mDoubleLink.deleteLast();
                    Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[position].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                    BitmapInfo info = new BitmapInfo();
                    info.mBitmap = bb;
                    info.index = position;
                    mDoubleLink.addFirst(info);
                } else if (mPosition == -1) {
                    for (int i = 0; i < 4; i++) {
                        int p = mCurrentPosition + i;
                        Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[p].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                        BitmapInfo info = new BitmapInfo();
                        info.mBitmap = bb;
                        info.index = p;
                        mDoubleLink.addLast(info);
                    }
                }
                mPosition = mCurrentPosition;
                postInvalidate();

                try {
                    Log.d("husj", "waitObject ");
                    synchronized (waitObject) {
                        waitObject.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }

    class DoubleLink<T> {
        int capacity;
        LinkedList<T> bitmapCache = new LinkedList<T>();

        public DoubleLink(int num) {
            capacity = num;

        }

        public int size() {
            return bitmapCache.size();
        }

        public void addFirst(T data) {
            if (capacity == bitmapCache.size()) return;
            bitmapCache.offerFirst(data);
        }

        public void addLast(T data) {
            if (capacity == bitmapCache.size()) return;
            bitmapCache.offer(data);
        }

        public T deleteFirst() {
            if (bitmapCache.size() == 0) return null;
            if (bitmapCache.get(0) == null) return null;
            return bitmapCache.pop();
        }

        public T deleteLast() {
            if (bitmapCache.size() == 0) return null;
            if (bitmapCache.get(bitmapCache.size() - 1) == null) return null;

            return bitmapCache.pollLast();
        }

        public T get(int position) {
            if (position >= size()) return null;
            return bitmapCache.get(position);
        }
    }

    public class Image {
        private int id;
        private String title;
        private String displayName;
        private String mimeType;
        private String path;
        private long size;

        public Image() {
            super();
        }


        /**
         * @param id
         * @param title
         * @param displayName
         * @param mimeType
         * @param path
         * @param size
         */
        public Image(int id, String title, String displayName, String mimeType,
                     String path, long size) {
            super();
            this.id = id;
            this.title = title;
            this.displayName = displayName;
            this.mimeType = mimeType;
            this.path = path;
            this.size = size;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

    }
}
