package com.litewaveinc.litewave.activities;

import android.content.Intent;
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


public class MainActivity extends AppCompatActivity {

    TextView noEvents;
    ImageView backgroundImage;
    ImageView logoImage;

    public class EventsResponse extends APIResponse {

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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-MM-d");
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

            Config.setBitmap("logoBitmap", resized);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);
        noEvents = (TextView) this.findViewById(R.id.textViewNoEvents);
        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        logoImage = (ImageView) this.findViewById(R.id.logoImage);

        API.init(getApplicationContext());

        checkEvents();
    }

    protected void checkEvents() {
        API.getEvents(getString(R.string.clientID_Blazers), new EventsResponse());
    }

    protected void getClient() {
        API.getClient(getString(R.string.clientID_Blazers), new GetClientResponse());
    }

    protected void showNoEvents() {
        noEvents.setText(R.string.noEventsToday);
        noEvents.setVisibility(View.VISIBLE);
        logoImage.setVisibility(View.VISIBLE);
        Log.d("MainActivity", "Logo url: " + Config.get("logoUrl"));
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
                .execute(Config.get("logoUrl"));
    }

    protected void showEvent(JSONObject event) {
        String stadiumID = "";

        try {
            stadiumID = event.getString("_stadiumId");
        } catch (JSONException e) {
            showError(e);
            return;
        }

        Intent intent = new Intent(MainActivity.this, LevelActivity.class);

        Bundle b = new Bundle();
        b.putString("StadiumID", stadiumID);
        intent.putExtras(b);

        startActivity(intent);
        finish();
    }

    protected void showError(Exception e) {
        e.printStackTrace();
    }
}
