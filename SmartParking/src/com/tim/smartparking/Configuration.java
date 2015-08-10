package com.tim.smartparking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		
		SharedPreferences storage = this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
		EditText name = (EditText)findViewById(R.id.name);
		name.setText(storage.getString("name", "Me"));		
		
	}
	
	public void save(View v)
	{
                
        SharedPreferences storage = Configuration.this.getSharedPreferences("Configuration", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = storage.edit();
		EditText name = (EditText)findViewById(R.id.name);
        editor.putString("name", String.valueOf(name.getText()));
        editor.commit();
        Toast.makeText(Configuration.this, "Saved", Toast.LENGTH_SHORT).show();
	}


}
