package com.litewaveinc.litewave;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

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
        String value = b.getString("StadiumID");

        //TODO: Call Webservice to get the stadium information.
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

}
