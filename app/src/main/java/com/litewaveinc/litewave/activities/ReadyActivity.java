package com.litewaveinc.litewave.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class ReadyActivity extends AppCompatActivity {

    int STROKE_WIDTH = 6;
    protected int CIRCLE_RADIUS = 225;

    Context context;
    ReadyActivity self;

    public TextView eventStatusTextView;
    public ImageView backgroundImage;
    public Button joinButton;
    public ProgressBar spinner;

    public ImageView levelImageView;
    public TextView levelNameTextView;
    public ImageView sectionImageView;
    public TextView sectionNameTextView;
    public ImageView rowImageView;
    public TextView rowNameTextView;
    public ImageView seatImageView;
    public TextView seatNameTextView;

    public class LeaveEventResponse extends APIResponse {

        @Override
        public void success(JSONObject content) {
            clearSeat();

            Intent parentActivityIntent = new Intent(ReadyActivity.this, ViewStack.pop());
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
        }

        @Override
        public void failure(JSONArray content, int statusCode) {
            Helper.showDialog("Error", "Sorry, could not leave event at this time", self);
        }
    }

    public void clearSeat() {
        Config.setPreference("UserLocationID", null, context);
        Config.setPreference("LevelID", null, context);
        Config.setPreference("SectionID", null, context);
        Config.setPreference("RowID", null, context);
        Config.setPreference("SeatID", null, context);
    }

    public void leaveEvent() {
        API.leaveEvent((String) Config.get("UserLocationID"), new LeaveEventResponse());
    }

    protected void getImage() {
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = (Bitmap) Config.get("logoBitmap");
                        if (bitmap != null) {
                            timer.cancel();
                            backgroundImage.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }, 0, 50);
    }

    protected void enableJoin() {

        int color = Helper.getColor((String)Config.get("highlightColor"));
        joinButton.setBackgroundColor(color);
        joinButton.setTextColor(Color.parseColor("#FFFFFF"));
        joinButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                joinShow();
            }
        });

        eventStatusTextView.setText("Join the event to begin");
    }

    protected void disableJoin() {
        int color = Helper.getColor((String)Config.get("highlightColor"));
        joinButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled_button_background));
        joinButton.setTextColor(ContextCompat.getColor(context, R.color.disabled_button_text));
        joinButton.setOnClickListener(null);

        eventStatusTextView.setText("Waiting for the event to begin");
    }

    public void joinShow() {

    }

    public void drawCircle(ImageView imageView) {
        int borderColor = Helper.getColor((String)Config.get("highlightColor"));
        int backgroundColor = Helper.getColor((String)Config.get("highlightColor"));
        Helper.drawCircle(imageView, CIRCLE_RADIUS, STROKE_WIDTH, borderColor, backgroundColor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;
        int highlightColor = Helper.getColor((String)Config.get("highlightColor"));
        int textColor = Helper.getColor((String) Config.get("textColor"));

        setContentView(R.layout.activity_ready);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle((String) Config.get("EventName"));
        actionBar.setBackgroundDrawable(new ColorDrawable(highlightColor));

        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        backgroundImage.setAlpha((float) 0.05);
        getImage();

        joinButton = (Button)findViewById(R.id.joinButton);

        eventStatusTextView = (TextView)findViewById(R.id.eventStatusTextView);
        eventStatusTextView.setTextColor(textColor);

        spinner = (ProgressBar)findViewById(R.id.spinner);
        spinner.getIndeterminateDrawable().setColorFilter(highlightColor,
                android.graphics.PorterDuff.Mode.MULTIPLY);

        levelImageView = (ImageView)findViewById(R.id.levelCircleImageView);
        drawCircle(levelImageView);
        levelNameTextView = (TextView)findViewById(R.id.levelNameTextView);
        levelNameTextView.setTextColor(Helper.getColor((String)Config.get("textSelectedColor")));
        levelNameTextView.setText((String) Config.get("LevelID"));

        sectionImageView = (ImageView)findViewById(R.id.sectionCircleImageView);
        drawCircle(sectionImageView);
        sectionNameTextView = (TextView)findViewById(R.id.sectionNameTextView);
        sectionNameTextView.setTextColor(Helper.getColor((String)Config.get("textSelectedColor")));
        sectionNameTextView.setText((String) Config.get("SectionID"));

        rowImageView = (ImageView)findViewById(R.id.rowCircleImageView);
        drawCircle(rowImageView);
        rowNameTextView = (TextView)findViewById(R.id.rowNameTextView);
        rowNameTextView.setTextColor(Helper.getColor((String)Config.get("textSelectedColor")));
        rowNameTextView.setText((String) Config.get("RowID"));

        seatImageView = (ImageView)findViewById(R.id.seatCircleImageView);
        drawCircle(seatImageView);
        seatNameTextView = (TextView)findViewById(R.id.seatNameTextView);
        seatNameTextView.setTextColor(Helper.getColor((String)Config.get("textSelectedColor")));
        seatNameTextView.setText((String)Config.get("SeatID"));

        disableJoin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                leaveEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
