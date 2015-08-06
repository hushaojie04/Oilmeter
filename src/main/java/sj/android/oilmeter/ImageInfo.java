package sj.android.oilmeter;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Administrator on 2015/8/6.
 */
public class ImageInfo {
    private int id;
    private String title;
    private String displayName;
    private String mimeType;
    private String path;
    private long size;
    private Bitmap mBitmap;
    private int mPosition;

    public ImageInfo() {
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
    public ImageInfo(int id, String title, String displayName, String mimeType,
                     String path, long size, int position) {
        super();
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.mPosition = position;
    }

    public int getId() {
        return id;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
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

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d("uuu", "[" + mPosition + " " + title + " " + displayName);

    }
}
