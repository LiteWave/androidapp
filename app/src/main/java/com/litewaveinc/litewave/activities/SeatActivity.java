package com.litewaveinc.litewave.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.litewaveinc.litewave.services.ViewStack;
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

    Context context;
    SeatActivity self;

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
            String userLocationID = "";
            try {
                userLocationID = content.getString("_id");
            } catch (JSONException e) {
                return;
            }

            saveSeat(userLocationID);

            ViewStack.push(SeatActivity.class);

            Intent intent = new Intent(SeatActivity.this, ReadyActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void failure(JSONArray content, int statusCode) {
            if (statusCode == 400) {
                Helper.showDialog("Seat", "Sorry, this seat has been taken.", self);
            } else {
                Helper.showDialog("Whoops", "Sorry, an error has occurred.", self);
            }
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

            if (selectedSection.equals("")) {
                disableJoin();
            } else {
                selectSection(selectedSection);
            }
        }

        @Override
        public void failure(JSONArray content, int statusCode) {
            Helper.showDialog("Whoops", "Sorry, an error has occurred.", self);
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

        CircleListAdapter adapter = new CircleListAdapter(sectionsListView, context, sections, selectedSection);
        sectionsListView.setAdapter(adapter);
        sectionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSection((String)sectionsListView.getItemAtPosition(position));
            }
        });
        rowsListView.setVisibility(View.VISIBLE);
        clearListView(rowsListView);

        seatsListView.setVisibility(View.INVISIBLE);
        clearListView(seatsListView);
    }

    protected void selectSection(String newSelectedSection) {
        if (!newSelectedSection.equals(selectedSection)) {
            selectedRow = "";
            selectedSeat = "";
        }
        selectedSection = newSelectedSection;

        JSONArray rowsContent;
        try {
            rowsContent = sectionsMap.get(selectedSection).getJSONArray("rows");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        buildRows(rowsContent);

        if (selectedRow.equals("")) {
            disableJoin();
        } else {
            selectRow(selectedRow);
        }
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

        CircleListAdapter adapter = new CircleListAdapter(rowsListView, context, rows, selectedRow);
        rowsListView.setAdapter(adapter);
        rowsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRow((String) rowsListView.getItemAtPosition(position));
            }
        });
        rowsListView.setVisibility(View.VISIBLE);

        seatsListView.setVisibility(View.VISIBLE);
        clearListView(seatsListView);
    }

    protected void selectRow(String newSelectedRow) {
        if (!newSelectedRow.equals(selectedRow )) {
            selectedSeat = "";
        }
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

        if (selectedSeat.equals("")) {
            disableJoin();
        } else {
            selectSeat(selectedSeat);
        }

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

        CircleListAdapter adapter = new CircleListAdapter(seatsListView, context, seats, selectedSeat);
        seatsListView.setAdapter(adapter);
        seatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSeat((String) seatsListView.getItemAtPosition(position));
            }
        });
    }

    protected void selectSeat(String newSelectedSeat) {
        selectedSeat = newSelectedSeat;

        if (selectedSeat.equals("")) {
            disableJoin();
        } else {
            enableJoin();
        }
    }

    protected void clearListView(ListView listView) {
        CircleListAdapter adapter = new CircleListAdapter(listView, context, new ArrayList<String>(), null);
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
        joinButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabled_button_background));
        joinButton.setTextColor(ContextCompat.getColor(context, R.color.disabled_button_text));
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

        API.joinEvent((String) Config.get("EventID"), params, new JoinEventResponse());
    }

    protected void saveSeat(String userLocationID) {
        Config.setPreference("UserLocationID", (String) Config.set("UserLocationID", userLocationID), context);
        Config.setPreference("EventID", (String) Config.get("EventID"), context);
        Config.setPreference("LevelID", (String)Config.set("LevelID", selectedLevel), context);
        Config.setPreference("SectionID", (String)Config.set("SectionID", selectedSection), context);
        Config.setPreference("RowID", (String)Config.set("RowID", selectedRow), context);
        Config.setPreference("SeatID", (String)Config.set("SeatID", selectedSeat), context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

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

        selectedLevel = (String)Config.get("LevelID");
        selectedSection = (String)Config.get("SectionID");
        selectedRow = (String)Config.get("RowID");
        selectedSeat = (String)Config.get("SeatID");
        getSeats(selectedLevel);

        disableJoin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentActivityIntent = new Intent(SeatActivity.this, ViewStack.pop());
                parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
