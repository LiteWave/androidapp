package com.litewaveinc.litewave.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.litewaveinc.litewave.services.Config;

/**
 * Created by davidanderson on 11/1/15.
 */
public class Helper {

    public static int getColor(String color) {
        String[] colorRGB = color.split(",");
        return Color.rgb(
                Integer.parseInt(colorRGB[0]),
                Integer.parseInt(colorRGB[1]),
                Integer.parseInt(colorRGB[2]));
    }
}
