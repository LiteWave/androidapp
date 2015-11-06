package com.litewaveinc.litewave.activities;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.util.SystemUiHider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class ResultsActivity extends AppCompatActivity {

    Context context;
    ResultsActivity self;



    public JSONObject show;
    public boolean isWinner;
    public String winnerID;
    public String winnerURL;
    public String winnerImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        show = (JSONObject)Config.get("Show");
        try {
            winnerID = show.getString("_winnerId");
            winnerURL = show.getString("winnerUrl");
            winnerImageURL = show.getString("winnerImageUrl");
        } catch (JSONException e) {return;}

        if (((String)Config.get("UserLocationID")).equals(winnerID)) {
            isWinner = true;
        }

        if (isWinner) {
            // do winner stuff
        } else {
            // do loser stuff
            new DownloadImageTask((ImageView) findViewById(R.id.imageViewWinner))
                    .execute(winnerImageURL);
            ImageView winnerView = (ImageView) findViewById(R.id.imageViewWinner);
            winnerView.setVisibility(View.VISIBLE);
            TextView thanksView = (TextView) findViewById(R.id.textThanks);
            thanksView.setVisibility(View.INVISIBLE);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
            bmImage.setImageBitmap(result);
        }
    }
}
