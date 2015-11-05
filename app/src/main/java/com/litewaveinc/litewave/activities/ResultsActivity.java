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

import java.io.InputStream;


public class ResultsActivity extends AppCompatActivity {

    Context context;
    ResultsActivity self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String winnerUserLocationID = (String)Config.get("WinnerUserLocationID");
        String winnerURL = (String)Config.get("WinnerURL");
        String winnerImageURL = (String)Config.get("WinnerImageURL");

        if(!winnerImageURL.isEmpty()) {
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
