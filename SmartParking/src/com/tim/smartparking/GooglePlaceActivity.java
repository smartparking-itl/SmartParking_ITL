package com.tim.smartparking;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class GooglePlaceActivity extends Activity {

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces;

	// GPS Location
	GPSTracker gps;

	// Button
	Button btnShowOnMap;

	// Progress dialog
	ProgressDialog pDialog;
	
	// Places Listview
	ListView lv;
	
	PlaceDetails placeDetails;
	
	String routeDistance;
	
	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
	
	
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);
		
		String internet_connect_err=getBaseContext().getString(R.string.internet_connect_err);
		String work_internet=getBaseContext().getString(R.string.work_internet);
		String gps_status=getBaseContext().getString(R.string.gps_status);
		String gps_inform=getBaseContext().getString(R.string.gps_inform);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(GooglePlaceActivity.this, internet_connect_err,
					work_internet, false);
			// stop executing code by return
			return;
		}

		// creating GPS Class object
		gps = new GPSTracker(this);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(GooglePlaceActivity.this, gps_status,
					gps_inform, false);
			// stop executing code by return
			return;
		}

		// Getting listview
		lv = (ListView) findViewById(R.id.list);
		
		// button show on map
		btnShowOnMap = (Button) findViewById(R.id.btn_show_map);

		// calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		new LoadPlaces().execute();

		/** Button click event for shown on map */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						PlacesMapActivity.class);
				// Sending user current geo location
				i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
				i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
				
				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});
		
		
		/**
		 * ListItem click event
		 * On selecting a listitem SinglePlaceActivity is launched
		 * */
		lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	// getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);
                
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
		Intent i = getIntent();
		String reference = i.getStringExtra(KEY_REFERENCE);
		new GetDistance().execute(reference);
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {
		
		String near_places=getBaseContext().getString(R.string.near_places);
		String place_not_found=getBaseContext().getString(R.string.place_not_found);
		String place_error=getBaseContext().getString(R.string.place_not_found);
		String occured_error=getBaseContext().getString(R.string.occured_error);
		String query_limit=getBaseContext().getString(R.string.query_limit);
		String denied=getBaseContext().getString(R.string.denied);
		String invalid_request=getBaseContext().getString(R.string.invalid_request);
		String error_occured=getBaseContext().getString(R.string.error_occured);
		String load_places=getBaseContext().getString(R.string.load_places);

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GooglePlaceActivity.this);
			pDialog.setMessage(Html.fromHtml(load_places));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();
			
			try {
				// Separeate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				// 
				String types = "parking"; // Listing places only cafes, restaurants
				
				// Radius in meters - increase this value if you don't find any places
				double radius = 1000; // 1000 meters 
				
				// get nearest places
				nearPlaces = googlePlaces.search(gps.getLatitude(),
						gps.getLongitude(), radius, types);
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = nearPlaces.status;
					
					// Check for all possible status
					if(status.equals("OK")){
						// Successfully got places details
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();
								
								// Place reference won't display in listview - it will be hidden
								// Place reference is used to get "place full details"
								map.put(KEY_REFERENCE, p.reference);
								
								// Place name
								map.put(KEY_NAME, p.name);
								
								
								// adding HashMap to ArrayList
								placesListItems.add(map);
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(GooglePlaceActivity.this, placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(GooglePlaceActivity.this, near_places,
								place_not_found, 
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(GooglePlaceActivity.this, place_error,
								occured_error,
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(GooglePlaceActivity.this, place_error,
								query_limit,
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(GooglePlaceActivity.this, place_error,
								denied,
								false);
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(GooglePlaceActivity.this, place_error,
								invalid_request,
								false);
					}
					else
					{
						alert.showAlertDialog(GooglePlaceActivity.this, place_error,
								"Sorry error occured.",
								false);
					}
				}
			});

		}

	}
	
	class GetDistance extends AsyncTask<String, String, String> {
		
		String near_places=getBaseContext().getString(R.string.near_places);
		String place_not_found=getBaseContext().getString(R.string.place_not_found);
		String place_error=getBaseContext().getString(R.string.place_not_found);
		String occured_error=getBaseContext().getString(R.string.occured_error);
		String query_limit=getBaseContext().getString(R.string.query_limit);
		String denied=getBaseContext().getString(R.string.denied);
		String invalid_request=getBaseContext().getString(R.string.invalid_request);
		String error_occured=getBaseContext().getString(R.string.error_occured);
		

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			String reference = args[0];
			
			// creating Places class object
			googlePlaces = new GooglePlaces();

			// Check if used is connected to Internet
			try {
				placeDetails = googlePlaces.getPlaceDetails(reference);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {
			
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					if(placeDetails != null){
						String status = placeDetails.status;
						
						// check place deatils status
						// Check for all possible status
						if(status.equals("OK")){
							if (placeDetails.result != null) {
								String name = placeDetails.result.name;
								String address = placeDetails.result.formatted_address;
								String phone = placeDetails.result.formatted_phone_number;
								String latitude = Double.toString(placeDetails.result.geometry.location.lat);
								String longitude = Double.toString(placeDetails.result.geometry.location.lng);
								
								if (KEY_NAME.equals(name)){
									routeDistance = GetRouteDistance();
									Log.d("Distance Link : ", routeDistance);
								}
								else{
									Log.d("Distance Link : ", "код говно");
								}
								
								Log.d("Place ", name + address + phone + latitude + longitude);
								
								// Displaying all the details in the view
								// single_place.xml
								TextView lbl_name = (TextView) findViewById(R.id.name);
								TextView lbl_address = (TextView) findViewById(R.id.address);
								TextView lbl_phone = (TextView) findViewById(R.id.phone);
								TextView lbl_location = (TextView) findViewById(R.id.location);
								
								
								// Check for null data from google
								// Sometimes place details might missing
								name = name == null ? "Not present" : name; // if name is null display as "Not present"
								address = address == null ? "Not present" : address;
								phone = phone == null ? "Not present" : phone;
								latitude = latitude == null ? "Not present" : latitude;
								longitude = longitude == null ? "Not present" : longitude;
								
								lbl_name.setText(name);
								lbl_address.setText(address);
								lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
								lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));
							}
						}
						else if(status.equals("ZERO_RESULTS")){
							alert.showAlertDialog(GooglePlaceActivity.this, near_places,
									place_not_found,
									false);
						}
						else if(status.equals("UNKNOWN_ERROR"))
						{
							alert.showAlertDialog(GooglePlaceActivity.this, place_error,
									occured_error,
									false);
						}
						else if(status.equals("OVER_QUERY_LIMIT"))
						{
							alert.showAlertDialog(GooglePlaceActivity.this, place_error,
									query_limit,
									false);
						}
						else if(status.equals("REQUEST_DENIED"))
						{
							alert.showAlertDialog(GooglePlaceActivity.this, place_error,
									denied,
									false);
						}
						else if(status.equals("INVALID_REQUEST"))
						{
							alert.showAlertDialog(GooglePlaceActivity.this, place_error,
									invalid_request,
									false);
						}
						else
						{
							alert.showAlertDialog(GooglePlaceActivity.this, place_error,
									error_occured,
									false);
						}
					}else{
						alert.showAlertDialog(GooglePlaceActivity.this, "Places Error",
								error_occured,
								false);
					}
					
					
				}
			});
	         
		}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public String GetRouteDistance()
	{
	String Distance = "error";
	  String Status = "error";
	  try {
		  String gpsLat = Double.toString(placeDetails.result.geometry.location.lat);
			String gpsLong = Double.toString(placeDetails.result.geometry.location.lng);
	      Log.d("Distance Link : ", "http://maps.googleapis.com/maps/api/directions/json?origin="+ gps.getLatitude() +","+ gps.getLongitude() +"&destination="+ gpsLat +","+ gpsLong +"&sensor=false");
	        JSONObject jsonObj = parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/directions/json?origin="+ gps.getLatitude() +","+ gps.getLongitude() +"&destination="+ gpsLat +","+ gpsLong +"&sensor=false"); 
	        Status = jsonObj.getString("status");
	        gpsLat = gpsLat == null ? "Not present" : gpsLat;
			gpsLong = gpsLong == null ? "Not present" : gpsLong;
	        if(Status.equalsIgnoreCase("OK"))
	        {
	        JSONArray routes = jsonObj.getJSONArray("routes"); 
	         JSONObject zero = routes.getJSONObject(0);
	         JSONArray legs = zero.getJSONArray("legs");
	         JSONObject zero2 = legs.getJSONObject(0);
	         JSONObject dist = zero2.getJSONObject("distance");
	         Distance = dist.getString("text");
	        }
	        else
	        {
	            Distance = "Too Far";
	        }
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	return Distance;
	}
}
