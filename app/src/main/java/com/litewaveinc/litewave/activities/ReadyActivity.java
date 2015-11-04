package com.litewaveinc.litewave.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.API;
import com.litewaveinc.litewave.services.APIResponse;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReadyActivity extends AppCompatActivity {

    Context context;
    ReadyActivity self;

    public class LeaveEventResponse extends APIResponse {

        @Override
        public void success(JSONObject content) {
            clearSeat();

            Intent parentActivityIntent = new Intent(ReadyActivity.this, ViewStack.pop());
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
        }

        @Override
        public void failure(JSONArray content, int statusCode) {
            Helper.showDialog("Error", "Sorry, could not leave event at this time", self);
        }
    }

    public void clearSeat() {
        Config.setPreference("UserLocationID", null, context);
        Config.setPreference("LevelID", null, context);
        Config.setPreference("SectionID", null, context);
        Config.setPreference("RowID", null, context);
        Config.setPreference("SeatID", null, context);
    }

    public void leaveEvent() {
        API.leaveEvent((String)Config.get("UserLocationID"), new LeaveEventResponse());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_ready);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int color = Helper.getColor((String) Config.get("highlightColor"));
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                leaveEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
