package com.litewaveinc.litewave.activities;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.adapters.CircleListAdapter;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.Helper;
import com.litewaveinc.litewave.util.JSONHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

public class SeatActivity extends AppCompatActivity {

    public ImageView backgroundImage;
    public String selectedLevel;
    public String selectedSection;
    public String selectedRow;
    public String selectedSeat;

    public ListView sectionsListView;
    public ListView rowsListView;
    public ListView seatsListView;

    public Button joinButton;

    public ArrayList<String> sections;
    public Hashtable<String, JSONObject>sectionsMap;
    public ArrayList<String> rows;
    public Hashtable<String, JSONObject>rowsMap;
    public ArrayList<String> seats;


    public class JoinEventResponse extends APIResponse {

        @Override
        public void success(JSONObject content) {
            Log.d("SeatActivity:onJoin", "START");
        }
    }

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

        CircleListAdapter adapter = new CircleListAdapter(sectionsListView, getApplicationContext(), sections);
        sectionsListView.setAdapter(adapter);
        sectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String newSelectedSection = (String) sectionsListView.getItemAtPosition(position);
                if (newSelectedSection == selectedSection) {
                    selectedSection = null;

                    rowsListView.setVisibility(View.INVISIBLE);
                    clearListView(rowsListView);

                    seatsListView.setVisibility(View.INVISIBLE);
                    clearListView(seatsListView);
                } else {
                    selectedSection = newSelectedSection;
                    JSONArray rowsContent;
                    try {
                        rowsContent = sectionsMap.get(selectedSection).getJSONArray("rows");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    buildRows(rowsContent);
                }
                disableJoin();
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

        CircleListAdapter adapter = new CircleListAdapter(rowsListView, getApplicationContext(), rows);
        rowsListView.setAdapter(adapter);
        rowsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newSelectedRow = (String) rowsListView.getItemAtPosition(position);
                if (newSelectedRow == selectedRow) {
                    selectedRow = null;

                    seatsListView.setVisibility(View.INVISIBLE);
                    clearListView(seatsListView);
                } else {
                    selectedRow = newSelectedRow;
                    JSONArray seatsContent;
                    try {
                        seatsContent = rowsMap.get(selectedRow).getJSONArray("seats");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    buildSeats(seatsContent);
                    seatsListView.setVisibility(View.VISIBLE);
                }
                disableJoin();
            }
        });
        rowsListView.setVisibility(View.VISIBLE);

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

        CircleListAdapter adapter = new CircleListAdapter(seatsListView, getApplicationContext(), seats);
        seatsListView.setAdapter(adapter);
        seatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newSelectedSeat = (String) rowsListView.getItemAtPosition(position);
                if (newSelectedSeat == selectedSeat) {
                    selectedSeat = null;
                    disableJoin();
                } else {
                    selectedSeat = newSelectedSeat;
                    enableJoin();
                }
            }
        });
    }

    protected void clearListView(ListView listView) {
        CircleListAdapter adapter = new CircleListAdapter(listView, getApplicationContext(), new ArrayList<String>());
        listView.setAdapter(adapter);
    }


    protected void getSeats(String level) {
        API.getSeats((String) Config.get("StadiumID"), level, new GetSeatsResponse());
    }

    protected void enableJoin() {

        int color = Helper.getColor((String)Config.get("highlightColor"));
        joinButton.setBackgroundColor(color);
        joinButton.setTextColor(Color.parseColor("#FFFFFF"));
        joinButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               joinEvent();
            }
        });
    }

    protected void disableJoin() {
        int color = Helper.getColor((String)Config.get("highlightColor"));
        joinButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.disabled_button_background));
        joinButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.disabled_button_text));
        joinButton.setOnClickListener(null);
    }

    protected void joinEvent() {
        JSONObject params = new JSONObject();
        JSONObject jsonUserSeat = new JSONObject();

        try {
            jsonUserSeat.put("level", selectedLevel);
            jsonUserSeat.put("section", selectedSection);
            jsonUserSeat.put("row", selectedRow);
            jsonUserSeat.put("seat", selectedSeat);

            params.put("userKey", (String)Config.get("UserID"));
            params.put("userSeat", jsonUserSeat);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        API.joinEvent((String)Config.get("EventID"), params, new JoinEventResponse());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int color = Helper.getColor((String) Config.get("highlightColor"));
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

        joinButton = (Button)findViewById(R.id.joinButton);

        selectedLevel = (String)Config.get("SelectedLevel");
        getSeats(selectedLevel);

        disableJoin();
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
