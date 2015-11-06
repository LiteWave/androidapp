package com.litewaveinc.litewave.activities;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;
import com.litewaveinc.litewave.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class ShowActivity extends AppCompatActivity {

    public Context context;
    public ShowActivity self;

    public View view;
    public TextView countdownTextView;
    public TextView showStartTextView;
    public TextView showHelpTextView;

    public Timer countdownTimer;

    protected JSONArray commands;
    protected JSONObject show;
    protected String startAt;
    protected String winnerID;
    protected boolean isWinner = false;
    protected int framePosition;


    public void startCountdown() {
        countdownTimer= new Timer();
        countdownTimer.schedule(new TimerTask() {

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCountdown();
                    }
                });
            }
        }, 0, 50);
    }

    public void stopCountdown() {
        countdownTimer.cancel();
    }

    public void beginShow() {
        countdownTextView.setVisibility(View.INVISIBLE);
        showStartTextView.setVisibility(View.INVISIBLE);
        showHelpTextView.setVisibility(View.INVISIBLE);

        try {
            commands = show.getJSONArray("commands");
            winnerID = show.getString("_winnerId");
        } catch (JSONException e) {return;}

        if (((String)Config.get("UserLocationID")).equals(winnerID)) {
            isWinner = true;
        }

        framePosition = 0;
        playFrames();
    }

    public void stopShow() {
        ViewStack.push(ReadyActivity.class);

        Intent intent = new Intent(ShowActivity.this, ResultsActivity.class);
        startActivity(intent);
        finish();
    }

    public void startFrameTimer(int interval) {
        final Timer frameTimer = new Timer();

        frameTimer.schedule(new TimerTask() {

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        frameTimer.cancel();
                        playFrames();
                    }
                });
            }
        }, interval);

    }

    public void playFrames() {
        if (commands.length() == framePosition) {
            stopShow();
        } else {
            JSONObject frame;
            String commandType = "c";
            String commandIf = "";
            boolean shouldVibrate = false;
            int commandLength = 0;
            int backgroundColor = Color.BLACK;

            try {
                // cl -> command length
                // ct -> command type
                // sv -> should vibrate
                // bg -> background color (rgb)
                frame = commands.getJSONObject(framePosition);

                if (frame.getString("cl") != null) {
                    commandLength = Integer.parseInt(frame.getString("cl"));

                    if (frame.has("sv")) {
                        shouldVibrate = Boolean.parseBoolean(frame.getString("sv"));
                        if (shouldVibrate) {
                            vibrate();
                        }
                    }

                    if (frame.has("cif")) {
                        commandIf = frame.getString("cif");
                    }

                    if (frame.has("ct")) {
                        commandType = frame.getString("ct");
                    }

                    if (commandType.equals("c")) {
                        if (frame.has("bg")) {
                            backgroundColor = Helper.getColor(frame.getString("bg"));
                        }
                    } else if (commandType.equals("win")) {
                        showWinner();
                    }
                    setBackgroundColor(backgroundColor);

                    framePosition++;
                    if (commandIf.equals("w") && !isWinner) {
                        playFrames();
                    } else if (commandIf.equals("l") && isWinner) {
                        playFrames();
                    } else {
                        startFrameTimer(commandLength);
                    }

                } else {
                    framePosition++;
                    playFrames();
                }
            } catch (JSONException e) {e.printStackTrace(); return;}
        }

    }

    public void showWinner() {

    }

    public void vibrate() {
        Vibrator vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    public void setBackgroundColor(int color) {
        view.setBackgroundColor(color);
    }

    public void updateCountdown() {

        Date currentDate;
        TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        DateFormat gmtFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        gmtFormat.setTimeZone(gmtTimeZone);
        try {
            int offset = Integer.parseInt((String)Config.get("MobileOffset"));
            currentDate = gmtFormat.parse(Calendar.getInstance().getTime().toString());
            currentDate = new Date(currentDate.getTime() - offset);
        } catch (ParseException e) {e.printStackTrace(); return;}

        Date startDate;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        dateFormat.setTimeZone(gmtTimeZone);
        try {
            startDate = dateFormat.parse(startAt);
        } catch (ParseException e) {e.printStackTrace(); return;}

        long showSecondOffset = Helper.getDateDiff(currentDate, startDate, TimeUnit.SECONDS);
        if (showSecondOffset >= 1) {
            countdownTextView.setText(Integer.toString((int)showSecondOffset));
        }
        else if (showSecondOffset <= 0) {
            stopCountdown();
            beginShow();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_show);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        view = findViewById(R.id.view);
        setBackgroundColor(Color.BLACK);
        int highlightColor = Helper.getColor((String) Config.get("highlightColor"));

        countdownTextView = (TextView)findViewById(R.id.countdownTextView);
        countdownTextView.setTextColor(highlightColor);

        showStartTextView = (TextView)findViewById(R.id.showStartTextView);
        showStartTextView.setTextColor(highlightColor);

        showHelpTextView = (TextView)findViewById(R.id.showHelpTextView);
        showHelpTextView.setTextColor(highlightColor);

        show = (JSONObject)Config.get("ShowData");
        try {
            startAt = show.getString("mobileStartAt");
        } catch (JSONException e) { e.printStackTrace();}

        startCountdown();
    }

}
