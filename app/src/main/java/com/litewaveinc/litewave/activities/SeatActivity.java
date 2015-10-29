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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.adapters.CircleListAdapter;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class SeatActivity extends AppCompatActivity {

    public ImageView backgroundImage;
    public String selectedLevel;
    public String selectedSection;
    public String selectedRow;
    public String selectedSeat;

    public ListView sectionsListView;
    public ListView rowsListView;
    public ListView seatsListView;

    public ArrayList<String> sections;
    public Hashtable<String, JSONObject>sectionsMap;
    public ArrayList<String> rows;
    public Hashtable<String, JSONObject>rowsMap;
    public ArrayList<String> seats;


    public class GetSeatsResponse extends APIResponse {

        @Override
        public void success(JSONObject content) {
            JSONArray sectionsContent;
            try {
                sectionsContent = content.getJSONArray("sections");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            buildSections(sectionsContent);
        }
    }

    protected void buildSections(JSONArray content) {
        sections = new ArrayList<String>();
        sectionsMap = new Hashtable<String, JSONObject>();
        for (int i = 0 ; i < content.length(); i++){
            try {
                String name = content.getJSONObject(i).getString("name");
                sectionsMap.put(name, content.getJSONObject(i));
                sections.add(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CircleListAdapter adapter = new CircleListAdapter(getApplicationContext(), sections);
        sectionsListView.setAdapter(adapter);
        sectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSection = (String) sectionsListView.getItemAtPosition(position);
                JSONArray rowsContent;
                try {
                    rowsContent = sectionsMap.get(selectedSection).getJSONArray("rows");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                buildRows(rowsContent);
            }
        });
        rowsListView.setVisibility(View.VISIBLE);
        clearListView(rowsListView);

        seatsListView.setVisibility(View.INVISIBLE);
        clearListView(seatsListView);
    }

    protected void buildRows(JSONArray content) {
        rows = new ArrayList<String>();
        rowsMap = new Hashtable<String, JSONObject>();
        for (int i = 0 ; i < content.length(); i++){
            try {
                String name = content.getJSONObject(i).getString("name");
                rowsMap.put(name, content.getJSONObject(i));
                rows.add(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CircleListAdapter adapter = new CircleListAdapter(getApplicationContext(), rows);
        rowsListView.setAdapter(adapter);
        rowsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRow = (String)rowsListView.getItemAtPosition(position);
                JSONArray seatsContent;
                try {
                    seatsContent = rowsMap.get(selectedRow).getJSONArray("seats");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                buildSeats(seatsContent);
            }
        });
        seatsListView.setVisibility(View.VISIBLE);
        clearListView(seatsListView);
    }

    protected void buildSeats(JSONArray content) {
        seats = new ArrayList<String>();
        for (int i = 0 ; i < content.length(); i++){
            try {
                String name = content.getString(i);
                seats.add(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CircleListAdapter adapter = new CircleListAdapter(getApplicationContext(), seats);
        seatsListView.setAdapter(adapter);
        seatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSeat = (String)seatsListView.getItemAtPosition(position);
            }
        });
    }

    protected void clearListView(ListView listView) {
        CircleListAdapter adapter = new CircleListAdapter(getApplicationContext(), new ArrayList<String>());
        listView.setAdapter(adapter);
    }


    protected void getSeats(String level) {
        API.getSeats((String)Config.get("StadiumID"), level, new GetSeatsResponse());
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

        sectionsListView = (ListView) findViewById(R.id.sectionsListView);
        rowsListView = (ListView) findViewById(R.id.rowsListView);
        seatsListView = (ListView) findViewById(R.id.seatsListView);

        selectedLevel = (String)Config.get("SelectedLevel");
        getSeats(selectedLevel);
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
