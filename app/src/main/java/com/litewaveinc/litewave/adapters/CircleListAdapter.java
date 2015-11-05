package com.litewaveinc.litewave.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.Helper;

import java.util.ArrayList;

/**
 * Created by davidanderson on 10/25/15.
 */
public class CircleListAdapter extends BaseAdapter {

    int STROKE_WIDTH = 6;
    protected int CIRCLE_RADIUS = 300;

    protected Context context;
    protected ListView listView;
    protected ArrayList<String> data;

    protected int selectedIndex = -1;
    protected String initialSelection;

    private static LayoutInflater inflater = null;

    public CircleListAdapter(ListView listView, Context context, ArrayList data, String initialSelection) {
        this.context = context;
        this.listView = listView;
        this.data = data;
        this.initialSelection = initialSelection;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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

        view.setId(position);
        view.setMinimumHeight(CIRCLE_RADIUS + 75);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(data.get(position));

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedIndex = view.getId();
                notifyDataSetChanged();

                listView.performItemClick(
                        listView.getAdapter().getView(selectedIndex, null, null),
                        selectedIndex,
                        listView.getAdapter().getItemId(selectedIndex));

            }

        });


        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean selected = false;
                int position = view.getId();
                if (position == selectedIndex) {
                    selected = true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    drawCircle(view, !selected);
                    drawText(view, !selected);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE ) {
                    drawCircle(view, selected);
                    drawText(view, selected);
                }
                return false;
            }

        });

        boolean select = false;
        if (initialSelection != null && data.get(position).equals(initialSelection)) {
            selectedIndex = position;
            initialSelection = null;
        }

        if (position == selectedIndex) {
            select = true;
        }
        drawText(view, select);
        drawCircle(view, select);

        return view;
    }

    protected void drawText(View view, boolean select) {
        TextView text = (TextView) view.findViewById(R.id.text);
        int color;
        if (select) {
            color = Helper.getColor((String)Config.get("textSelectedColor"));
        } else {
            color = Helper.getColor((String)Config.get("textColor"));
        }
        text.setTextColor(color);
    }

    protected void drawCircle(View view, boolean select) {
        int backgroundColor;
        int borderColor;

        if (select) {
            borderColor = Helper.getColor((String)Config.get("highlightColor"));
            backgroundColor = Helper.getColor((String)Config.get("highlightColor"));
        } else {
            borderColor = Helper.getColor((String)Config.get("borderColor"));
            backgroundColor = Helper.getColor((String)Config.get("backgroundColor"));
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        Helper.drawCircle(imageView, CIRCLE_RADIUS, STROKE_WIDTH, borderColor, backgroundColor);
    }
}