package com.litewaveinc.litewave.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.util.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Hashtable;

public class SeatActivity extends AppCompatActivity {


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
        Bundle b = getIntent().getExtras();
        //NOTE: If we use a single API to get seating info we might want to implement this.
        //ArrayList<Hashtable> sectionList = (ArrayList<Hashtable>)b.getSerializable("table");

        return;
        //String levelIdentifier = b.getString("SelectedLevel");
        //JSONArray stadiumInfo = JSONHelper.getJSONArray(b.getString("StadiumInfo"));

        //ArrayList<String> sectionList =  this.getSections(stadiumInfo, levelIdentifier);

        //Log.d("SeatActivity:onCreate", "FINISH");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_seat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
