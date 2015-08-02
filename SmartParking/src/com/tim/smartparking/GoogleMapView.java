package com.tim.smartparking;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapView extends FragmentActivity {

	SupportMapFragment smf;
	GoogleMap gm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_places);
		smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
		gm = smf.getMap();
		if (gm == null) {
			finish();
			return;
		}
		gm.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		init();

		gm.setMyLocationEnabled(true);
	}

	private void init() {
		gm.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng ll) {
				// TODO Auto-generated method stub

			}

		});
		gm.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng ll) {
				// TODO Auto-generated method stub

			}

		});
		gm.addMarker(new MarkerOptions().position(new LatLng(0, 0)).flat(true)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				.title("Test SP marker"));
	}

}
