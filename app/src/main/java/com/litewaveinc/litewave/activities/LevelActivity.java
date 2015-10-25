package com.litewaveinc.litewave.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LevelActivity extends AppCompatActivity {

    ImageView backgroundImage;

    public class LevelsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {
            final LinearLayout lm = (LinearLayout) findViewById(R.id.chooseLevelLinearLayout);
            // create the layout params that will be used to define how your
            // button will be displayed
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //Set the margin spacing for the buttons so they are distributed.
            params.setMargins(0,24,0,24);

            //1. Enumerate through content getting levels and thier identifiers
            String result = "";
            ArrayList<Hashtable> levelList = new ArrayList<Hashtable>();
            for(int i = 0 ; i < content.length(); i++){
                try {
                    Hashtable<String, String> levelMap = new Hashtable<String, String>();
                    levelMap.put(content.getJSONObject(i).getString("_id"),
                            content.getJSONObject(i).getString("name"));
                    levelList.add(levelMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Create Buttons dependent on service call on number of rows per stadium.
            for(int j=0;j<levelList.size();j++) {
                // Create LinearLayout
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);

                final int index = j;
                final Button btn = new Button(getApplicationContext());
                ((Button) btn).setBackgroundResource(R.drawable.roundredbuttonhi);
                // Give button an ID
                btn.setId(index);

                //TODO: Set the button to the level of stadium
                btn.setText("Level " + index);
                // set the layoutParams on the button
                btn.setLayoutParams(params);

                //TODO: Wire up the listener to pass the bundle to the next choose row view
                // Set click listener for button
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Log.i("TAG", "index :" + index);

                        Toast.makeText(getApplicationContext(),
                                "Clicked Button Index :" + index,
                                Toast.LENGTH_LONG).show();
                    }
                });

                //Add button to LinearLayout
                ll.addView(btn);

                //Add button to LinearLayout defined in XML
                lm.addView(ll);
            }

            //TODO: TEMPORARY Code to wire up Choose Seat and develop seat activity code. Ultimately
            //we need upon seat selection bundle the _id for the section.

//            Intent intent = new Intent(LevelActivity.this, SeatActivity.class);
//            //Setup a bundle to be passed to the next intent
//            Bundle b = new Bundle();
//            //TODO: Once selected pass section identifier. Currently passing everything this will change
//            //NOTE: passed just the sections. We might want to add a API just to get the seating info.
//            //b.putSerializable("Sections", sectionList);
//            //TODO: Hardcoded section ID for now until UX is wired up.
//            b.putString("SelectedLevel", "55de78afa1d569ec11646bca");
//            b.putString("StadiumInfo", content.toString());
//            intent.putExtras(b); //Put your id to your next Intent
//            startActivity(intent);
//            finish();
        }
    }

    protected void getLevels(String stadiumID) {
        API.getLevels(stadiumID, new LevelsResponse());
    }

    protected void getImage() {
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = Config.getBitmap("logoBitmap");
                        if (bitmap != null) {
                            backgroundImage.setImageBitmap(bitmap);
                            timer.cancel();
                        }
                    }
                });


            }
        },0,500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LevelActivity:onCreate", "START");
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_level);
        ActionBar actionBar = getSupportActionBar();

        String[] colorRGB = Config.get("highlightColor").split(",");
        int color = Color.rgb(
                Integer.parseInt(colorRGB[0]),
                Integer.parseInt(colorRGB[1]),
                Integer.parseInt(colorRGB[2]));
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        backgroundImage.setAlpha((float) 0.05);
        getImage();

        Bundle b = getIntent().getExtras();
        String stadiumID = b.getString("StadiumID");

        //Get levels from server
        getLevels(stadiumID);
        Log.d("LevelActivity:onCreate", "FINISH");
    }
}
