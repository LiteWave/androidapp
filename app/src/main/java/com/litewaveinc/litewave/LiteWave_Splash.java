package com.litewaveinc.litewave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LiteWave_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_lite_wave__splash);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{

                    try {
                        URL url;
                        HttpURLConnection conn;
                        BufferedReader rd;
                        String line;
                        StringBuilder result = new StringBuilder();

                        url = new URL("http://104.130.156.82:8080/api/clients/5260316cbf80240000000001/events");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = rd.readLine()) != null) {
                            result.append(line);
                        }
                        rd.close();
                        String responseText = result.toString();
                        JSONArray mainResponseJSONObject = new JSONArray(responseText);
                        //mainResponseJSONObject = mainResponseJSONObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent myIntent = new Intent(LiteWave_Splash.this, ChooseLevel.class);
                    startActivity(myIntent);
                }
            }
        };
        timer.start();

        // Check to see if there is a show then goto the chooselevel
        // Call getEvents
        // Else display No Events today displayed in text.
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
