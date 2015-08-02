package com.tim.smartparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class ChelniParks extends Activity implements OnClickListener {
	boolean Leave_activity;
	static Spinner Spinner;
	Button button1;
	Button btnTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chelni_parks);
		
		Spinner = (Spinner) findViewById(R.id.spinner);
		button1 = (Button) findViewById(R.id.button1);
		btnTest = (Button) findViewById(R.id.btnTest);
		button1.setOnClickListener(this);
		btnTest.setOnClickListener(this);
		Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	        @Override
	        public void onItemSelected(AdapterView<?> arg0, View arg1,
	                int arg2, long arg3) {
	            switch (arg2) {
	            case 0:
	                break;
	                               case 1:
	                            	   Intent i = new Intent();
		                                i.setClass(ChelniParks.this, Kruiz.class);
		                                 startActivity(i);			                                 
	                break;
	                               case 2:
	              	                 Intent ir = new Intent();
		                                ir.setClass(ChelniParks.this, Garag500.class);
		                                 startActivity(ir);
	              	                break;
	                     }}
	                   @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	                	   
	            // TODO Auto-generated method stub

	        }
	    });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chelni_parks, menu);
		return true;
	}
	
	public void onResume() {
		super.onResume();
		Spinner.setSelection(0);
	}
	public void onClick(View v) {
		if(v.getId() == R.id.button1) {
			Intent intent = new Intent(); 
			intent.setClass(this, GoogleMapView.class); 
			startActivity(intent); 
			finish();
		}
		if(v.getId() == R.id.btnTest) {
			Intent intent = new Intent(); 
			intent.setClass(this, MyLocationActivity.class); 
			startActivity(intent); 
			finish();
		}
	}
}
