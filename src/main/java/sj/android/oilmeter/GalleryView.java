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
import android.view.View;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

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
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAsyncTask.execute();
    }

    AsyncTask<String, Integer, String> mAsyncTask = new AsyncTask<String, Integer, String>() {
        @Override
        protected String doInBackground(String... params) {
            Context context = mContext;
            if (context != null) {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, null);
                mData = new Image[cursor.getCount()];
                if (cursor != null) {
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
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        if (itemWidth == 0)
            itemWidth = getMeasuredWidth() / 3;
        paint.setAntiAlias(true);    //
        float left = scrollX, top = 0;
        top = getMeasuredHeight() / 3;
        int position = mCurrentPosition;
        if (progress != -1) {
            for (int i = 0; i < 3; i++) {
                if (position >= 0 && position <= progress) {
                    Log.d("husj", "onDraw " + position);
                    Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[position].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                    if (bb != null) {
                        canvas.drawBitmap(bb, left, top, paint);
                        left += bb.getWidth();
//                    top += bb.getHeight();
                    }
                    bb.recycle();
                }
                position++;
            }
            if (scrollX > 0) {
                position = mCurrentPosition - 1;
                if (position >= 0 && position <= progress) {
                    Log.d("husj", "onDraw " + position);
                    Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[position].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                    if (bb != null) {
                        canvas.drawBitmap(bb, scrollX - itemWidth, top, paint);
                        left += bb.getWidth();
                    }
                    bb.recycle();
                }
            } else if (scrollX < 0) {
                if (position >= 0 && position <= progress) {
                    Log.d("husj", "onDraw " + position);
                    Bitmap bb = resizeBitmap(BitmapFactory.decodeFile(mData[position].getPath()), (int) itemWidth, getMeasuredHeight() / 4);
                    if (bb != null) {
                        canvas.drawBitmap(bb, left, top, paint);
                        left += bb.getWidth();
                    }
                    bb.recycle();
                }
            }

        }
    }

    float x0, y0;
    float scrollX = 0;
    float lastScrollX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(scrollX) >= itemWidth) {
                    Log.d("husj", "scrollX" + scrollX);
                    Log.d("husj", "itemWidth" + itemWidth);

                    if (scrollX > 0) {
                        Log.d("husj", "mCurrentPosition--");
                        if (mCurrentPosition > 0)
                            mCurrentPosition--;
                    } else {
                        Log.d("husj", "mCurrentPosition++");
                        if (mCurrentPosition < progress)
                            mCurrentPosition++;
                    }
                    scrollX = 0;
                } else {
                    scrollX += event.getX() - x0;
                    x0 = event.getX();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;

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
    class ImageCache
    {

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
