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


public class LiteWave_Splash extends AppCompatActivity {

    public static boolean _isDebug = false;
    private String _eventsURL = "";

    TextView _noEvents;

    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {
            Log.d("Debug", content.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_lite_wave__splash);
        _noEvents = (TextView) this.findViewById(R.id.textViewNoEvents);

        this._eventsURL = getString(R.string.apiURL) + getString(R.string.getEvents);

        this._eventsURL = this._eventsURL.replaceFirst("\\[clientID\\]",
                                getString(R.string.clientID_Blazers));

        //Excecutes a Async Task from the main UX thread
        new checkEvents().execute("");

        API.getEvents(new EventsResponse());
    }

    private class checkEvents extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //TODO: do while for future retry logic
            //do {
                try {
                    Thread.sleep(3000);
                    return RESTClientHelper.callRESTService(_eventsURL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            //} while (RESTClientHelper.callRESTService(getString(R.string.getEventsURL)) == null);
            return null;
        }

        private String getCurrentStadium(JSONArray eventCollection) throws JSONException {
            String result = "";
            for(int i = 0 ; i < eventCollection.length(); i++){
                JSONObject jsonobject = eventCollection.getJSONObject(i);
                String serverDate = jsonobject.getString("date");
                serverDate = serverDate.substring(0,serverDate.indexOf('T'));

                Date currentDate = Calendar.getInstance().getTime();
                //Format the datetime to match the format so we con compare against the current date
                java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("y-MM-d");
                String formattedCurrentDate = simpleDateFormat.format(currentDate);

                //If there is an event today return the stadium id.
                if(0 == serverDate.compareTo(formattedCurrentDate))
                {
                    result = jsonobject.getString("_stadiumId");
                    return result;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String currentStadiumID = "";
            try {
            if((result != "" && result !="[]" && result != null) &&
                    (getCurrentStadium(JSONHelper.getJSONArray(result)) != null))
            {
                currentStadiumID = getCurrentStadium(JSONHelper.getJSONArray(result));

                Intent intent = new Intent(LiteWave_Splash.this, ChooseLevel.class);
                //Setup a bundle to be passed to the next intent
                Bundle b = new Bundle();
                //Pass the StadiumID to be used to get the seat information
                b.putString("StadiumID", currentStadiumID); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }
            else
            {
                //TOD: Possibly add a new activity for no events or pass to the next choose level activity.
                _noEvents.setText(R.string.noEventsToday);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if(_isDebug)
            {
                //TODO: Maybe add debug URLS?
            }
            else if(RESTClientHelper.callRESTService(_eventsURL) == null)
            {
                _noEvents.setText(R.string.noEventsToday);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lite_wave__splash, menu);
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
