package com.litewaveinc.litewave.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.util.JSONHelper;
import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.util.RESTClientHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LevelActivity extends AppCompatActivity {


    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_level);
        //OPTION: This is the option to hide the title bar. Need to decide on best layout.
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        final LinearLayout lm = (LinearLayout) findViewById(R.id.chooseLevelLinearLayout);
        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Set the margin spacing for the buttons so they are distributed.
        params.setMargins(0,24,0,24);

        Bundle b = getIntent().getExtras();
        String stadiumID = b.getString("StadiumID");

        //TODO: Call Webservice to get the stadium information.
        //Excecutes a Async Task from the main UX thread
        new getLevels().execute(stadiumID);

        //Create Buttons dependent on service call on number of rows per stadium.
        //TODO: Add the iterator to number of levels in the stadium.
        //TODO: Move logic to post execute asyc task?
        for(int j=0;j<3;j++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);

            final int index = j;
            final Button btn = new Button(this);
            ((Button) btn).setBackgroundResource(R.drawable.roundredbuttonhi);
            // Give button an ID
            btn.setId(index);

            //TODO: Set the button to the level of stadium
            btn.setText("LevelActivity " + index);
            // set the layoutParams on the button
            btn.setLayoutParams(params);

            //TODO: Wire up the listener to pass the bundle to the next choose row view
            // Set click listener for button
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Log.i("TAG", "index :" + index);

                    Toast.makeText(getApplicationContext(),
                            "Clicked Button Index :" + index,
                            Toast.LENGTH_LONG).show();
                }
            });

            //Add button to LinearLayout
            ll.addView(btn);

            //Add button to LinearLayout defined in XML
            lm.addView(ll);
        }

    }

    private class getLevels extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String getStadiumInfoURL = getString(R.string.getStadiumInfoURL);
            getStadiumInfoURL = getStadiumInfoURL.replaceFirst("STADIUMID", params[0].toString());
            //TODO: do while for future retry logic
            //do {
            return RESTClientHelper.callRESTService(getStadiumInfoURL);
            //} while (RESTClientHelper.callRESTService(getString(R.string.getEventsURL)) == null);

        }

        private ArrayList<String> getCurrentLevels(JSONArray stadiumSeatingCollection) throws JSONException {
            String result = "";
            //NOTE: Sequence Section->LevelActivity->Row-SeatActivity
            ArrayList<String> sectionList = new ArrayList<String>();
            for(int i = 0 ; i < stadiumSeatingCollection.length(); i++){
                sectionList.add(stadiumSeatingCollection.getJSONObject(i).getString("name"));
            }
            return sectionList;
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<String> levels;
            //BUG: Seems to be a issue when no events today. currentStadiumID will be null (need to fix)
            if(result != "" && result !="[]") {
                try {
                    levels = getCurrentLevels(JSONHelper.getJSONArray(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Intent intent = new Intent(LevelActivity.this, SeatActivity.class);
                //Setup a bundle to be passed to the next intent
                Bundle b = new Bundle();
                //Pass the StadiumID to be used to get the seat information
                //b.putString("StadiumID", currentStadiumID); //Your id
                //intent.putExtras(b); //Put your id to your next Intent
                //startActivity(intent);
                finish();
            }
            else
            {
                //noEvents.setText(R.string.noEventsToday);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }


}
