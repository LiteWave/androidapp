package com.litewaveinc.litewave.services;

import android.graphics.Bitmap;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidanderson on 10/24/15.
 */
public class Config {

    private static Map<String, String> strings = new HashMap<String, String>();
    private static Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

    public static String get(String name) {
        return strings.get(name);
    }

    public static void set(String name, String value) {
        strings.put(name, value);
    }

    public static Bitmap getBitmap(String name) {
        return bitmaps.get(name);
    }

    public static Bitmap setBitmap(String name, Bitmap bitmap) {
        return bitmaps.put(name, bitmap);
    }

}
