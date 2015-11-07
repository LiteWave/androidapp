package com.litewaveinc.litewave.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.adapters.CircleListAdapter;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class LevelActivity extends AppCompatActivity {

    private Context context;
    private LevelActivity self;

    private View view;
    private ImageView backgroundImage;
    private ListView listView;
    private ArrayList<String> levels;
    private Hashtable<String, JSONObject> levelsMap;

    private class GetLevelsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {

            levels = new ArrayList<String>();
            levelsMap = new Hashtable<String, JSONObject>();
            for (int i = 0 ; i < content.length(); i++){
                try {
                    String name = content.getJSONObject(i).getString("name");
                    levelsMap.put(name, content.getJSONObject(i));
                    levels.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            CircleListAdapter adapter = new CircleListAdapter(listView, getApplicationContext(), levels, null);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectLevel((String)listView.getItemAtPosition(position));
                }
            });
        }

        @Override
        public void failure(JSONArray content, int statusCode) {
            Helper.showDialog("Whoops", "Sorry, an error has occurred.", self);
        }
    }

    private void selectLevel(String level) {
        Config.set("LevelID", level);

        ViewStack.push(LevelActivity.class);

        Intent intent = new Intent(LevelActivity.this, SeatActivity.class);
        startActivity(intent);
        finish();
    }

    private void getLevels(String stadiumID) {
        API.getLevels(stadiumID, new GetLevelsResponse());
    }

    private void getImage() {
        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = (Bitmap)Config.get("logoBitmap");
                        if (bitmap != null) {
                            timer.cancel();
                            backgroundImage.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        },0,50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_level);

        int color = Helper.getColor((String)Config.get("highlightColor"));
        int backgroundColor = Helper.getColor((String)Config.get("backgroundColor"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        view = findViewById(R.id.view);
        view.setBackgroundColor(backgroundColor);

        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        backgroundImage.setAlpha((float) 0.05);
        getImage();

        listView = (ListView) findViewById(R.id.listView);
        listView.setDivider(null);

        // clear the seats
        Config.set("SectionID", "");
        Config.set("RowID", "");
        Config.set("SeatID", "");

        String stadiumID = (String)Config.get("StadiumID");

        getLevels(stadiumID);
    }

}
