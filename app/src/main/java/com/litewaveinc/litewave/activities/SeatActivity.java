package com.litewaveinc.litewave.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Hashtable;

public class SeatActivity extends AppCompatActivity {

    ImageView backgroundImage;


    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {

        }
    }

    private static ArrayList<String> getSections(JSONArray stadiumInfo, String levelIdentifier) {
        ArrayList<String> sectionList = new ArrayList<String>();

        for(int i = 0 ; i < stadiumInfo.length(); i++) {
            try {
                //Find level
                if(stadiumInfo.getJSONObject(i).getString("_id") == levelIdentifier); {
                    JSONArray sectionsInfo = stadiumInfo.getJSONObject(i).getJSONArray("sections");
                    //Collect the sections
                    for(int j = 0; j < sectionsInfo.length(); j++) {
                        sectionList.add(sectionsInfo.getJSONObject(j).getString("name"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sectionList;
    }

    private static ArrayList<String> getRows(JSONArray sectionInfo) {

        return null;
    }

    private static ArrayList<String> getSeats(JSONArray sectionInfo) {

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SeatActivity:onCreate", "START");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String[] colorRGB = ((String) Config.get("highlightColor")).split(",");
        int color = Color.rgb(
                Integer.parseInt(colorRGB[0]),
                Integer.parseInt(colorRGB[1]),
                Integer.parseInt(colorRGB[2]));
        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        backgroundImage.setAlpha((float) 0.05);
        Bitmap bitmap = (Bitmap)Config.get("logoBitmap");
        if (bitmap != null) {
            backgroundImage.setImageBitmap(bitmap);
        }

        return;
        //NOTE: If we use a single API to get seating info we might want to implement this.
        //ArrayList<Hashtable> sectionList = (ArrayList<Hashtable>)b.getSerializable("table");

        //String levelIdentifier = b.getString("SelectedLevel");
        //JSONArray stadiumInfo = JSONHelper.getJSONArray(b.getString("StadiumInfo"));

        //ArrayList<String> sectionList =  this.getSections(stadiumInfo, levelIdentifier);

        //Log.d("SeatActivity:onCreate", "FINISH");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
