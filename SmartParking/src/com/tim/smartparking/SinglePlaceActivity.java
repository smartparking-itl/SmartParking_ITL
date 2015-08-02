package com.tim.smartparking;

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
import android.widget.TextView;

public class SinglePlaceActivity extends Activity {
	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;
	
	// В stackoverflow посоветовали так сделать
	String routeDistance;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	GooglePlaces googlePlaces;
	
	// Place Details
	PlaceDetails placeDetails;
	
	// Progress dialog
	ProgressDialog pDialog;
	
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	
	GPSTracker gps;
	String gpsLat;
	String gpsLong;
	String resourceURI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		String gps_status=getBaseContext().getString(R.string.gps_status);
		String gps_inform=getBaseContext().getString(R.string.gps_inform);
		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);
		
		Intent i = getIntent();
		
		// Place referece id
		String reference = i.getStringExtra(KEY_REFERENCE);
		
		// Calling a Async Background thread
		new LoadSinglePlaceDetails().execute(reference);
		
		gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(SinglePlaceActivity.this, gps_status,
					gps_inform,
					false);
			// stop executing code by returno
			return;
		}
		
	}

	

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {
		
		String near_places=getBaseContext().getString(R.string.near_places);
		String place_not_found=getBaseContext().getString(R.string.place_not_found);
		String place_error=getBaseContext().getString(R.string.place_not_found);
		String occured_error=getBaseContext().getString(R.string.occured_error);
		String query_limit=getBaseContext().getString(R.string.query_limit);
		String denied=getBaseContext().getString(R.string.denied);
		String invalid_request=getBaseContext().getString(R.string.invalid_request);
		String error_occured=getBaseContext().getString(R.string.error_occured);
		String load_profile=getBaseContext().getString(R.string.load_profile);
		
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SinglePlaceActivity.this);
			pDialog.setMessage(load_profile);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Profile JSON
		 * */
		protected String doInBackground(String... args) {
			String reference = args[0];
			
			// creating Places class object
			googlePlaces = new GooglePlaces();

			// Check if used is connected to Internet
			try {
				placeDetails = googlePlaces.getPlaceDetails(reference);

			} catch (Exception e) {
				e.printStackTrace();
			}
			routeDistance = GetRouteDistance();
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			TextView lbl_distance = (TextView) findViewById(R.id.distance);
	         lbl_distance.setText(routeDistance);
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
								lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude + routeDistance));
								latitude = gpsLat;
								longitude = gpsLong;
							}
						}
						else if(status.equals("ZERO_RESULTS")){
							alert.showAlertDialog(SinglePlaceActivity.this, near_places,
									place_not_found,
									false);
						}
						else if(status.equals("UNKNOWN_ERROR"))
						{
							alert.showAlertDialog(SinglePlaceActivity.this, place_error,
									occured_error,
									false);
						}
						else if(status.equals("OVER_QUERY_LIMIT"))
						{
							alert.showAlertDialog(SinglePlaceActivity.this, place_error,
									query_limit,
									false);
						}
						else if(status.equals("REQUEST_DENIED"))
						{
							alert.showAlertDialog(SinglePlaceActivity.this, place_error,
									denied,
									false);
						}
						else if(status.equals("INVALID_REQUEST"))
						{
							alert.showAlertDialog(SinglePlaceActivity.this, place_error,
									invalid_request,
									false);
						}
						else
						{
							alert.showAlertDialog(SinglePlaceActivity.this, place_error,
									error_occured,
									false);
						}
					}else{
						alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
								error_occured,
								false);
					}
					
					
				}
			});
			
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
}

