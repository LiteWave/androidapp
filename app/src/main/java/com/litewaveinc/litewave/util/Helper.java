package com.litewaveinc.litewave.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.litewaveinc.litewave.activities.SeatActivity;
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

    public static void showDialog(String title, String info, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(info);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
