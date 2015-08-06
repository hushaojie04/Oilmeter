package sj.android.oilmeter;

import android.media.*;
import android.media.Image;
import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/6.
 */
class ImageCache {
    private HashMap<Integer, ImageRef> mCacheMap;
    private ReferenceQueue<ImageRef> queue;
    private static ImageCache imageCache = new ImageCache();

    private class ImageRef extends SoftReference {
        int _id;

        public ImageRef(ImageInfo imageinfo, ReferenceQueue queue) {
            super(imageinfo, queue);
            _id = imageinfo.getPosition();
        }

        public int get_ID() {
            return _id;
        }
    }

    private ImageCache() {
        mCacheMap = new HashMap<Integer, ImageRef>();
        queue = new ReferenceQueue<ImageRef>();
    }

    public static ImageCache getInstance() {
        return imageCache;
    }

    public void cacheImageInfo(ImageInfo imageInfo) {
        ImageRef ref = new ImageRef(imageInfo, queue);
        mCacheMap.put(ref.get_ID(), ref);
    }

    public ImageInfo getCacheImageInfo(int id) {
        cleanCache();
        ImageInfo imageInfo = null;
        if (mCacheMap.containsKey(id)) {
            ImageRef ref = mCacheMap.get(id);
            imageInfo = (ImageInfo) ref.get();
        }
        return imageInfo;
    }

    private void cleanCache() {
        ImageRef ref = null;
        while ((ref = (ImageRef) queue.poll()) != null) {
            mCacheMap.remove(ref.get_ID());
        }
        String x = "";
        for (Integer set : mCacheMap.keySet()) {
            x = x + " " + set;
        }
        Log.d("uuu", "mCacheMap " + mCacheMap.size() + ":" + x);
    }

    public void clearCache() {
        cleanCache();
        mCacheMap.clear();
        System.gc();
        System.runFinalization();
    }
}
