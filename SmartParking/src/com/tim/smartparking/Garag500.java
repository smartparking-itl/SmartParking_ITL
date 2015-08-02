package com.tim.smartparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Garag500 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garag500);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.garag500, menu);
		return true;
	}

}
