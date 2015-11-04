package com.litewaveinc.litewave.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    TextView noEventsTextView;
    TextView poweredByTextView;
    ImageView backgroundImage;
    ImageView logoImage;

    public class GetEventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {
            Date currentDate = Calendar.getInstance().getTime();

            if (content.length() > 0) {
                for(int i = 0 ; i < content.length(); i++) {
                    JSONObject event = null;
                    JSONObject settings = null;
                    String eventDate = null;
                    try {
                        event = content.getJSONObject(i);
                        eventDate = event.getString("date");
                        settings = event.getJSONObject("settings");
                    } catch (JSONException e) {
                        showError(e);
                        return;
                    }

                    eventDate = eventDate.substring(0, eventDate.indexOf('T'));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-MM-dd");
                    String formattedCurrentDate = simpleDateFormat.format(currentDate);

                    if (eventDate.compareTo(formattedCurrentDate) == 0) {
                        Log.d("MainActivity", "Event found for: " + formattedCurrentDate.toString());
                        saveSettings(settings);
                        saveLogo();
                        showEvent(event);
                        return;
                    }
                }
                Log.d("MainActivity", "No Events found for: " + currentDate.toString());
                getClient();

            } else {
                Log.d("MainActivity", "No Events found for: " + currentDate.toString());
                getClient();
            }

        }
    }

    public class GetClientResponse extends APIResponse {
        @Override
        public void success(JSONObject content) {
            JSONObject settings = null;
            try {
                settings = content.getJSONObject("settings");
            } catch (JSONException e) {
                showError(e);
                return;
            }

            saveSettings(settings);
            showNoEvents();
        }

    }

    private class DownloadLogoTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadLogoTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            double height = metrics.heightPixels*1.5;
            double width = (result.getWidth()*height)/result.getHeight();
            Bitmap resized = Bitmap.createScaledBitmap(result, (int)width, (int)height, true);
            bmImage.setImageBitmap(resized);
            bmImage.setAlpha((float) 0.05);

            Config.set("logoBitmap", resized);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);
        noEventsTextView = (TextView) this.findViewById(R.id.noEventsTextView);
        poweredByTextView = (TextView) this.findViewById(R.id.poweredByTextView);
        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        logoImage = (ImageView) this.findViewById(R.id.logoImage);

        API.init(getApplicationContext());

        loadPreferences();
        checkEvents();
    }

    protected void loadPreferences() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getString("UserID", "") == "") {
            editor.putString("UserID", UUID.randomUUID().toString());
            editor.commit();
        }

        Config.set("UserID", preferences.getString("UserID", ""));

        Config.set("LevelID", preferences.getString("LevelID", ""));
        Config.set("SectionID", preferences.getString("SectionID", ""));
        Config.set("RowID", preferences.getString("RowID", ""));
        Config.set("SeatID", preferences.getString("SeatID", ""));
    }

    protected void checkEvents() {
        API.getEvents(getString(R.string.clientID_Blazers), new GetEventsResponse());
    }

    protected void getClient() {
        API.getClient(getString(R.string.clientID_Blazers), new GetClientResponse());
    }

    protected void showNoEvents() {
        noEventsTextView.setText(R.string.noEventsToday);
        noEventsTextView.setVisibility(View.VISIBLE);

        poweredByTextView.setVisibility(View.VISIBLE);

        logoImage.setVisibility(View.VISIBLE);
        saveLogo();
    }

    protected void saveSettings(JSONObject settings) {
        try {
            Config.set("backgroundColor", settings.getString("backgroundColor"));
            Config.set("borderColor", settings.getString("borderColor"));
            Config.set("highlightColor", settings.getString("highlightColor"));
            Config.set("textColor", settings.getString("textColor"));
            Config.set("textSelectedColor", settings.getString("textSelectedColor"));
            Config.set("logoUrl", settings.getString("logoUrl"));
        } catch (JSONException e) {
            showError(e);
        }
    }

    protected void saveLogo() {
        new DownloadLogoTask((ImageView) findViewById(R.id.backgroundImage))
                .execute((String)Config.get("logoUrl"));
    }

    protected void showEvent(JSONObject event) {
        String stadiumID = "";
        String eventID = "";

        try {
            stadiumID = event.getString("_stadiumId");
            eventID = event.getString("_id");
        } catch (JSONException e) {
            showError(e);
            return;
        }
        Config.set("StadiumID", stadiumID);
        Config.set("EventID", eventID);


        Intent intent = new Intent(MainActivity.this, LevelActivity.class);

        startActivity(intent);
        finish();
    }

    protected void showError(Exception e) {
        e.printStackTrace();
    }
}
