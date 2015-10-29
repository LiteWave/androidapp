package com.litewaveinc.litewave.services;

import android.graphics.Bitmap;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidanderson on 10/24/15.
 */
public class Config {

    private static Map<String, Object> data = new HashMap<String, Object>();
    private static Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

    public static Object get(String name) {
        return data.get(name);
    }

    public static void set(String name, Object value) {
        data.put(name, value);
    }

}
