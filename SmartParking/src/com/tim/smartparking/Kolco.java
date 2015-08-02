package com.tim.smartparking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Kolco extends Activity implements OnClickListener {
	Button button2;
	Button btn3;
	Button oplata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kolco);
		
		btn3 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		oplata = (Button) findViewById(R.id.button3);
		oplata.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kolco, menu);
		return true;
	}

	public void onClick(View v) {
		if(v.getId() == R.id.button2) {
			Intent intent = new Intent(); 
			intent.setClass(this, ServerTest.class); 
			startActivity(intent); 
			finish();
		} else if(v.getId() == R.id.button1) {
			String geoUriString = "geo:0,0?q=ТЦ Кольцо Казань&z=8";
	    	Uri geo = Uri.parse(geoUriString);
	    	Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
	    	startActivity(geoMap);
		} else if(v.getId() == R.id.button3) {
			Intent intent = new Intent(); 
			intent.setClass(this, ServerOplata.class); 
			startActivity(intent); 
			finish();
		}
	}
}
