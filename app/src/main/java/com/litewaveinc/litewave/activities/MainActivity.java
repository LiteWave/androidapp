package com.litewaveinc.litewave.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.litewaveinc.litewave.util.JSONHelper;
import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.util.RESTClientHelper;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    TextView noEvents;

    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {
            Log.d("Debug", content.toString());
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
                    Date currentDate = Calendar.getInstance().getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-MM-d");
                    String formattedCurrentDate = simpleDateFormat.format(currentDate);

                    if (eventDate.compareTo(formattedCurrentDate) == 0) {
                        showEvent(event);
                        return;
                    }
                }
                showNoEvents();
            } else {
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

        checkEvents();
    }

    protected void checkEvents() {
        API.getEvents(getApplicationContext(), new EventsResponse());
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
