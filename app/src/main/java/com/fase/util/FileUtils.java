package com.fase.util;

import android.content.Context;

import java.io.File;

public class FileUtils {

    public static File createDefaultCacheDir(Context context, String folderName) {
        File cache = new File(context.getApplicationContext().getCacheDir(), folderName);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }
}
