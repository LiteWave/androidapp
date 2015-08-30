package com.litewaveinc.litewave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;

public class ChooseLevel extends AppCompatActivity {

    //private Button _imageButton;
    Handler mHandler = new Handler();
    TextView textView1;
    int num1=0,num2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        //Then grab the id in your new Activity:

        Bundle b = getIntent().getExtras();
        String value = b.getString("StadiumID");

        textView1 = (TextView) this.findViewById(R.id.textView1);

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
        mythread.start();

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        //this._imageButton = (Button) this.findViewById(R.id.button1);
        //_imageButton.setText("Text");


    }

}
