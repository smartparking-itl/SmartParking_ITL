package com.tim.smartparking;

import java.util.Date;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Oplata extends Activity implements OnClickListener {
	
	Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oplata);
		btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(this);
	}
	public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button1: 
            TextView textview = (TextView)findViewById(R.id.textView2); 
            textview.setText(calc());
        }
    }
 
    @SuppressWarnings("deprecation")
	public String calc() 
    {
        Date date = new Date();
        int hours = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds();
        String text = String.valueOf(hours) + ":" + String.valueOf(min) + 
           ":" + String.valueOf(sec);
        return text;
    }   
}
