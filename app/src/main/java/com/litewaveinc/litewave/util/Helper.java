package com.litewaveinc.litewave.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.litewaveinc.litewave.R;
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

    public static void drawCircle(ImageView imageView, int radius, int strokeWidth, int borderColor, int backgroundColor) {
        Bitmap bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        bitmap = bitmap.copy(bitmap.getConfig(), true);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawCircle(radius/2 + strokeWidth/2, radius/2 + strokeWidth/2, radius/2 - strokeWidth, paint);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(borderColor);
        canvas.drawCircle(radius / 2 + strokeWidth / 2, radius / 2 + strokeWidth / 2, radius / 2 - strokeWidth, paint);
        imageView.setImageBitmap(bitmap);
    }

}
