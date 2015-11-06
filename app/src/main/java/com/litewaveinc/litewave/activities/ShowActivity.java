package com.litewaveinc.litewave.activities;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.Helper;
import com.litewaveinc.litewave.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class ShowActivity extends AppCompatActivity {

    Context context;
    ShowActivity self;

    View view;
    TextView countdownTextView;
    TextView showStartTextView;
    TextView showHelpTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_show);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        view = findViewById(R.id.view);
        view.setBackgroundColor(Color.BLACK);

        int highlightColor = Helper.getColor((String) Config.get("highlightColor"));

        countdownTextView = (TextView)findViewById(R.id.countdownTextView);
        countdownTextView.setTextColor(highlightColor);

        showStartTextView = (TextView)findViewById(R.id.showStartTextView);
        showStartTextView.setTextColor(highlightColor);

        showHelpTextView = (TextView)findViewById(R.id.showHelpTextView);
        showHelpTextView.setTextColor(highlightColor);
    }

}
