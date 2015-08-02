package com.tim.smartparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Chistopolskaya extends Activity implements OnClickListener {
	Button button1;
	Button button2;
	public static final String EXTRAS_BEACON = "extrasBeacon";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chistopolskaya);
		
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button1.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chistopolskaya, menu);
		return true;
	}

	public void onClick(View v) {
		if(v.getId() == R.id.button1) {
			Intent intent = new Intent(); 
			intent.setClass(this, BeaconMap.class); 
			startActivity(intent); 
			finish();
		}
	}
}
