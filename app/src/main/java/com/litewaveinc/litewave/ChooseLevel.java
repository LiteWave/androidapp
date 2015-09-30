package com.litewaveinc.litewave;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChooseLevel extends AppCompatActivity {

    //CODE SAMPLE as part of runnable sample below.
    //private Button _imageButton;
    //Handler mHandler = new Handler();
    //TextView textView1;
    //int num1=0,num2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
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
            btn.setText("Level " + index);
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



        //SAMPLE CODE: This code creates a runable to update text at runtime if needed.
        /*textView1 = (TextView) this.findViewById(R.id.textView1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    textView1.setText(String.valueOf(num1)+" ");
                    num1++;
                    if (num1<10)
                    {
                        mHandler.postDelayed(this, 1000);
                    }
                }
            }
        };
        mHandler.post(runnable);

        Thread mythread = new Thread(runnable);
        mythread.start();*/
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

                //Intent intent = new Intent(ChooseLevel.this, ChooseSeat.class);
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
            if(RESTClientHelper.callRESTService(getString(R.string.getEventsURL)) == null)
            {
                //noEvents.setText(R.string.noEventsToday);
            }
        }
    }


}
