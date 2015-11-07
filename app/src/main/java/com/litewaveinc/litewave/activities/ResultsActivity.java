package com.litewaveinc.litewave.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.litewaveinc.litewave.R;
import com.litewaveinc.litewave.services.Config;
import com.litewaveinc.litewave.services.ViewStack;
import com.litewaveinc.litewave.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class ResultsActivity extends AppCompatActivity {

    Context context;
    ResultsActivity self;

    public Button returnButton;
    ImageView backgroundImage;
    View view;

    public JSONObject show;

    public String winnerID;
    public String winnerURL;
    public String winnerImageURL;

    private void returnReady()
    {
        // popping the current view
        ViewStack.pop();
        // popping the show view
        Intent parentActivityIntent = new Intent(ResultsActivity.this, ViewStack.pop());
        parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(parentActivityIntent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        int highlightColor = Helper.getColor((String) Config.get("highlightColor"));

        view = (View) this.findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebURL(winnerURL);
            }
        });

        backgroundImage = (ImageView) this.findViewById(R.id.backgroundImage);
        backgroundImage.setAlpha((float) 0.05);
        Bitmap bitmap = (Bitmap)Config.get("logoBitmap");
        if (bitmap != null) {
            backgroundImage.setImageBitmap(bitmap);
        }

        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setBackgroundColor(highlightColor);
        returnButton.setTextColor(Color.parseColor("#FFFFFF"));
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnReady();
            }
        });

        show = (JSONObject)Config.get("Show");
        try {
            winnerID = show.getString("_winnerId");
            winnerURL = show.getString("winnerUrl");
            winnerImageURL = show.getString("winnerImageUrl");
        } catch (JSONException e) {return;}

        if (((String)Config.get("UserLocationID")).equals(winnerID)) {
            showWinner();
        }
    }

    public void openWebURL(String url) {
        Intent browse = new Intent(Intent.ACTION_VIEW , Uri.parse(url));
        startActivity(browse);
    }

    private void showWinner() {
        TextView thanksView = (TextView) findViewById(R.id.textThanks);
        thanksView.setVisibility(View.INVISIBLE);

        TextView textPoweredBy =(TextView) findViewById(R.id.textPoweredBy);
        textPoweredBy.setVisibility(View.INVISIBLE);

        ImageView lwLogo = (ImageView) findViewById(R.id.lwLogo);
        lwLogo.setVisibility(View.INVISIBLE);

        ImageView winnerView = (ImageView) findViewById(R.id.imageViewWinner);
        winnerView.setVisibility(View.VISIBLE);

        ImageView backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        backgroundImage.setVisibility(View.INVISIBLE);

        view.setBackgroundColor(Color.BLACK);

        new DownloadImageTask((ImageView) findViewById(R.id.imageViewWinner))
                .execute(winnerImageURL);
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
