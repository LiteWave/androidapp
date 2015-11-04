package com.litewaveinc.litewave.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;

public class ReadyActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        //ViewStack.push(getClass());

        setContentView(R.layout.activity_ready);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int color = Helper.getColor((String) Config.get("highlightColor"));
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
    }

    public void clearSeat() {
        Config.setPreference("LevelID", null, context);
        Config.setPreference("SectionID", null, context);
        Config.setPreference("RowID", null, context);
        Config.setPreference("SeatID", null, context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                clearSeat();

                Class myClass = ViewStack.pop();
                Intent parentActivityIntent = new Intent(ReadyActivity.this, myClass);
                parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
