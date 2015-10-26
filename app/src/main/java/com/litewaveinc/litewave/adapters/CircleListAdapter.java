package com.litewaveinc.litewave.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;

/**
 * Created by davidanderson on 10/25/15.
 */
public class CircleListAdapter extends BaseAdapter {

    int STROKE_WIDTH = 6;
    protected int CIRCLE_RADIUS = 300;

    protected Context context;
    protected String[] data;

    private static LayoutInflater inflater = null;

    public CircleListAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null)
            view = inflater.inflate(R.layout.circle_row, null);
        else
            view = convertView;

        view.setMinimumHeight(CIRCLE_RADIUS + 75);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(data[position]);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    drawCircle(view, true);
                    drawText(view, true);
                    return false;
                }
                return false;
            }

        });

        drawText(view, false);
        drawCircle(view, false);

        return view;
    }

    protected void drawText(View view, boolean select) {
        TextView text = (TextView) view.findViewById(R.id.text);
        int color;
        String[] colorRGB;
        if (select) {
            colorRGB = Config.get("textSelectedColor").split(",");
        } else {
            colorRGB = Config.get("textColor").split(",");
        }
        color = Color.rgb(
                Integer.parseInt(colorRGB[0]),
                Integer.parseInt(colorRGB[1]),
                Integer.parseInt(colorRGB[2]));
        text.setTextColor(color);
    }

    protected void drawCircle(View view, boolean select) {
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        Bitmap bitmap = Bitmap.createBitmap(CIRCLE_RADIUS, CIRCLE_RADIUS, Bitmap.Config.ARGB_8888);
        bitmap = bitmap.copy(bitmap.getConfig(), true);

        Canvas canvas = new Canvas(bitmap);

        int color;
        String[] colorRGB;
        Paint paint;
        if (select) {
            colorRGB = Config.get("highlightColor").split(",");
            color = Color.rgb(
                    Integer.parseInt(colorRGB[0]),
                    Integer.parseInt(colorRGB[1]),
                    Integer.parseInt(colorRGB[2]));

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(color);

            canvas.drawCircle(CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 - STROKE_WIDTH, paint);
        } else {
            colorRGB = Config.get("backgroundColor").split(",");
            color = Color.rgb(
                    Integer.parseInt(colorRGB[0]),
                    Integer.parseInt(colorRGB[1]),
                    Integer.parseInt(colorRGB[2]));

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawCircle(CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 - STROKE_WIDTH, paint);

            colorRGB = Config.get("borderColor").split(",");
            color = Color.rgb(
                    Integer.parseInt(colorRGB[0]),
                    Integer.parseInt(colorRGB[1]),
                    Integer.parseInt(colorRGB[2]));

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(STROKE_WIDTH);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            canvas.drawCircle(CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 + STROKE_WIDTH/2, CIRCLE_RADIUS/2 - STROKE_WIDTH, paint);
        }



        imageView.setImageBitmap(bitmap);
    }
}