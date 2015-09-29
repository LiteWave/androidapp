package com.litewaveinc.litewave;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;


public class LiteWave_Splash extends AppCompatActivity {

    Handler mHandler = new Handler();
    TextView noEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_lite_wave__splash);
        noEvents = (TextView) this.findViewById(R.id.textViewNoEvents);

        //Excecutes a Async Task from the main UX thread
        new checkEvents().execute("");
    }

    private class checkEvents extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //TODO: do while for future retry logic
            //do {
                try {
                    Thread.sleep(3000);
                    return RESTClientHelper.callRESTService(getString(R.string.getEventsURL));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            //} while (RESTClientHelper.callRESTService(getString(R.string.getEventsURL)) == null);
            return null;
        }

        private String getCurrentEvent(JSONArray eventCollection) throws JSONException {
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
                    result = jsonobject.getString("_id");
                    return result;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String currentStadiumID = "";

            if(result != "" && result !="[]") {
                try {
                     currentStadiumID = getCurrentEvent(JSONHelper.getJSONArray(result));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                noEvents.setText(R.string.noEventsToday);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if(RESTClientHelper.callRESTService(getString(R.string.getEventsURL)) == null)
            {
                noEvents.setText(R.string.noEventsToday);
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
