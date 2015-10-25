package com.litewaveinc.litewave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    TextView noEvents;

    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {
            Date currentDate = Calendar.getInstance().getTime();

            if (content.length() > 0) {
                for(int i = 0 ; i < content.length(); i++) {
                    JSONObject event = null;
                    String eventDate = null;
                    try {
                        event = content.getJSONObject(i);
                        eventDate = event.getString("date");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showNoEvents();
                    }

                    eventDate = eventDate.substring(0, eventDate.indexOf('T'));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-MM-d");
                    String formattedCurrentDate = simpleDateFormat.format(currentDate);

                    //DEBUG REMOVE
                    //showEvent(event);
                    //return;
                    //END DEBUG

                    if (eventDate.compareTo(formattedCurrentDate) == 0) {
                        Log.d("MainActivity", "Event found for: " + formattedCurrentDate.toString());
                        showEvent(event);
                        return;
                    }
                }
                Log.d("MainActivity", "No Events found for: " + currentDate.toString());
                showNoEvents();
            } else {
                Log.d("MainActivity", "No Events found for: " + currentDate.toString());
                showNoEvents();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);
        noEvents = (TextView) this.findViewById(R.id.textViewNoEvents);

        API.init(getApplicationContext());

        checkEvents();
    }

    protected void checkEvents() {
        API.getEvents(new EventsResponse());
    }

    protected void showNoEvents() {
        noEvents.setText(R.string.noEventsToday);
    }

    protected void showEvent(JSONObject event) {
        String stadiumID = "";

        try {
            stadiumID = event.getString("_stadiumId");
        } catch (JSONException e) {
            e.printStackTrace();
            showNoEvents();
        }

        Intent intent = new Intent(MainActivity.this, LevelActivity.class);

        Bundle b = new Bundle();
        b.putString("StadiumID", stadiumID);
        intent.putExtras(b);

        startActivity(intent);
        finish();
    }
}
