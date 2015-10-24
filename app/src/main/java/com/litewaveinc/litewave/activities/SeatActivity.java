package com.litewaveinc.litewave.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.APIResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;

public class SeatActivity extends AppCompatActivity {


    public class EventsResponse extends APIResponse {

        @Override
        public void success(JSONArray content) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        Bundle b = getIntent().getExtras();
        ArrayList<Hashtable> sectionList = (ArrayList<Hashtable>)b.getSerializable("table");

        Log.d("SeatActivity", "DEBUG");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_seat, menu);
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
