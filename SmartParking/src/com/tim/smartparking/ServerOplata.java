package com.tim.smartparking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ServerOplata extends Activity implements OnItemSelectedListener {
	
	public final static String FILE_NAME = "filename";

	private Button btnAddNewCategory;
	private TextView txtCategory;
	private Spinner spinnerFood;
	// array list for spinner adapter
	private static ArrayList<Category> categoriesList;
	ProgressDialog pDialog;
	Chronometer chronometer;
	
	

	// API urls
	// Url to create new category
	private String URL_NEW_CATEGORY = "http://192.168.1.31/food_api/new_category.php";
	// Url to get all categories
	private String URL_CATEGORIES = "http://192.168.1.31/food_api/get_categories.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oplata);
		
		btnAddNewCategory = (Button) findViewById(R.id.btnAddNewCategory);
		final Button butStart = (Button) findViewById(R.id.btnAddNewCategory);
		final Button butStop = (Button) findViewById(R.id.button1);
		
		final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer1);
		
		
		butStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				chronometer.start();
			}
		});
		
		butStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				stopService(new Intent(ServerOplata.this, MyService.class));
				chronometer.stop();
			}
		});
		chronometer
		.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

			@Override
			public void onChronometerTick(Chronometer c) {  
		        int cTextSize = c.getText().length();
		        if (cTextSize == 5) {
		            chronometer.setText("00:"+c.getText().toString());
		        } else if (cTextSize == 7) {
		            chronometer.setText("0"+c.getText().toString());
		        }
		    }
		});
		spinnerFood = (Spinner) findViewById(R.id.spinFood);
		txtCategory = (TextView) findViewById(R.id.txtCategory);
		
		categoriesList = new ArrayList<Category>();

		// spinner item select listener
		spinnerFood.setOnItemSelectedListener(this);

		// Add new category click event
		btnAddNewCategory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
		        switch (v.getId()) {
		        case R.id.btnAddNewCategory: 
		        	startService(new Intent(ServerOplata.this, MyService.class));
		            TextView textview = (TextView)findViewById(R.id.txtCategory); 
		            textview.setText(calc());
		            String newCategory = txtCategory.getText().toString();
		            new AddNewCategory().execute(newCategory);
		            chronometer.start();
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
		});

		new GetCategories().execute();
		
	}

	/**
	 * Adding spinner data
	 * */
	private void populateSpinner() {
	    List<String> lables = new ArrayList<String>();
	 
	    for (int i = 0; i < categoriesList.size(); i++) {
	        lables.add(categoriesList.get(i).getName());
	    }
	 
	    // Creating adapter for spinner
	    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, lables);
	 
	    // Drop down layout style - list view with radio button
	    spinnerAdapter
	            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 
	    // attaching data adapter to spinner
	    spinnerFood.setAdapter(spinnerAdapter);
	}

	/**
	 * Async task to get all food categories
	 * */
	private class GetCategories extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ServerOplata.this);
			pDialog.setMessage("Connection to the server..");
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ServiceHandler jsonParser = new ServiceHandler();
			String json = jsonParser.makeServiceCall(URL_CATEGORIES, ServiceHandler.GET);

			Log.e("Response: ", "> " + json);

			if (json != null) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					if (jsonObj != null) {
						JSONArray categories = (JSONArray) jsonObj.get("time");					

						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Category cat = new Category(catObj.getInt("id"),
									catObj.getString("name"));
							categoriesList.add(cat);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					
				}

			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			populateSpinner();
		}

	}

	/**
	 * Async task to create a new food category
	 * */
	private class AddNewCategory extends AsyncTask<String, Void, Void> {

		boolean isNewCategoryCreated = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ServerOplata.this);
			pDialog.setMessage("We add your time to base..");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(String... arg) {

			String newCategory = arg[0];

			// Preparing post params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", newCategory));

			ServiceHandler serviceClient = new ServiceHandler();

			String json = serviceClient.makeServiceCall(URL_NEW_CATEGORY,
					ServiceHandler.POST, params);

			Log.d("Create Response: ", "> " + json);

			if (json != null) {
				try {
					JSONObject jsonObj = new JSONObject(json);
					boolean error = jsonObj.getBoolean("error");

					if (jsonObj != null) {
						JSONArray categories = jsonObj
								.getJSONArray("categories");

						for (int i = 0; i < categories.length(); i++) {
							JSONObject catObj = (JSONObject) categories.get(i);
							Category cat = new Category(catObj.getInt("id"),
									catObj.getString("name"));
							categoriesList.add(cat);
						}
					}
					// checking for error node in json
					if (!error) {	
						// new category created successfully
						isNewCategoryCreated = true;
					} else {
						Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("JSON Data", "Didn't receive any data from server!");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing()) {
				pDialog.dismiss();
				populateSpinner();
			}
			if (isNewCategoryCreated) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// fetching all categories
						new GetCategories().execute();
					}
				});
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(
				getApplicationContext(),
						parent.getItemAtPosition(position).toString() + " Selected" ,
				Toast.LENGTH_LONG).show();

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {		
	}
}

