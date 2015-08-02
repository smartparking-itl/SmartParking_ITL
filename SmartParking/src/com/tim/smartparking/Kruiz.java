package com.tim.smartparking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Kruiz extends Activity implements OnClickListener {
	Button button1;
	Button button2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kruiz);
		
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kruiz, menu);
		return true;
	}

	public void onClick(View v) {
		if(v.getId() == R.id.button1) {
		String geoUriString = "geo:0,0?q=Автостоянка Круиз&z=8";
    	Uri geo = Uri.parse(geoUriString);
    	Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
    	startActivity(geoMap);
		}
    	if(v.getId() == R.id.button2) {
			Intent intent = new Intent(); 
			intent.setClass(this, ServerTest.class); 
			startActivity(intent); 
			finish();
    	}
	}
}
