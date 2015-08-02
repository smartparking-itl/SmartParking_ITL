package com.tim.smartparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
 
public class RunActivity extends Activity implements OnClickListener 
{ 
	Button btnRead;
	String[] data = {"Мега", "Южный", "Тандем"};
	  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkings);
        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);
        
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Парковки");
        // выделяем элемент 
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        // показываем позиция нажатого элемента
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });
    }
	@Override
  	public void onClick(View v) {
  		Intent intent = new Intent(); 
  		intent.setClass(this, SPMapActivity.class); 
  		 
  		 startActivity(intent); 
  		 finish();
  	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(); 
  		intent.setClass(this, MainActivity.class); 
  		 
  		 startActivity(intent); 
  		 finish();
	}
}
