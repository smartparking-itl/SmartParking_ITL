package com.tim.smartparking;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btnFind;
	static Spinner Spinner; // �������
	static AlertDialog.Builder ad; // �����
	static int act = 0; // ����� �������� �������1`
	static AlertDialog ald1, ald2, ald3; // ald3 - internet; ald2 - wait; ald1 -
											// no GPS
	static Geocoder g; // ���������� ���������� � �������� ������
	static String myTown; // � ��� ������ ����� ������� �����, ��������,
							// "������"
	// public static IBeaconProtocol ibp;
	public static LocationManager lm;
	public static Location currLoc;
	static findTown ft;

	public static LocationListener locationlistener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			currLoc = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			ald1.show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			if (ald1.isShowing())
				ald1.cancel(); // ��������� ��� ����, ���� ������������ �������
								// GPS ����� ������
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo != null && nInfo.isConnected()) {
			Log.v("status", "ONLINE");
			return true;
		} else {
			Log.v("status", "OFFLINE");
			return false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/*
		 * // ��� �������������� ����� iBeacon ibp =
		 * IBeaconProtocol.getInstance(this); ibp.setListener(new
		 * IBeaconListener() {
		 * 
		 * @Override public void enterRegion(IBeacon ibeacon) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void exitRegion(IBeacon ibeacon) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void beaconFound(IBeacon ibeacon) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void searchState(int state) { // TODO Auto-generated
		 * method stub
		 * 
		 * }
		 * 
		 * @Override public void operationError(int status) { // TODO
		 * Auto-generated method stub
		 * 
		 * }
		 * 
		 * });
		 */
		// ��� �������������� �������� � GPS-��������
		g = new Geocoder(this);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		// ��� ������, ������������ ����� ����. ����� ��� ����������� ��������
		// GPS
		ad = new AlertDialog.Builder(this);

		// ��� ��������� AlertDialog'�
		ad.setMessage(R.string.btn_find_me);
		ad.setPositiveButton("", null);
		ad.setNegativeButton("", null);
		ald2 = ad.create();
		if (ald2 != null) {
			ald2.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					try {
						ft.cancel(true);
					} catch (NullPointerException e) {
					}
				}

			});
		}

		ad.setMessage(R.string.dlg_find_me);
		ad.setNegativeButton("������", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ald1 = ad.create();

		ad.setTitle("��� ������� � ��������");
		ad.setMessage("��� ������ ���� ������� ��������� ������ � ���������");
		ad.setNegativeButton("������", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ald3 = ad.create();

		btnFind = (Button) findViewById(R.id.btnFind);
		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { // ����
																				// ��������
																				// GPS
					ald1.show();
				} else {
					if (!isOnline()) {
						ald3.show();
					} else { // ���� ������� GPS
						startGPS();
						ft = new findTown();
						ald2.show();
						ft.execute(null, null, null);
					}
				}
			}

		});

		Spinner = (Spinner) findViewById(R.id.spinner);
		Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				switch (arg2) {
				case 0:
					break;
				case 1:
					Intent i = new Intent();
					i.setClass(MainActivity.this, ChelniParks.class);
					startActivity(i);
					break;
				case 2:
					Intent ir = new Intent();
					ir.setClass(MainActivity.this, KazanParks.class);
					startActivity(ir);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	// Not used
	/*
	 * private void scanBeacons(){ // Check Bluetooth every time
	 * Log.i(Utils.LOG_TAG,"Scanning");
	 * 
	 * // Filter based on default easiBeacon UUID, remove if not required
	 * //_ibp.setScanUUID(UUID here);
	 * 
	 * if(!IBeaconProtocol.configureBluetoothAdapter(this)){ //Intent
	 * enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	 * //startActivityForResult(enableBtIntent, 9 ); }else{ if(ibp.isScanning())
	 * ibp.stopScan(); ibp.reset(); ibp.startScan(); } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static long back_pressed;

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis())
			super.onBackPressed();
		else
			Toast.makeText(getBaseContext(), R.string.exit, Toast.LENGTH_SHORT)
					.show();
		back_pressed = System.currentTimeMillis();
	}

	public static void stopGPS() {
		lm.removeUpdates(locationlistener);
	}

	public static void startGPS() {
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000,
				locationlistener);
	}

	protected void onResume() {
		super.onResume();
		Spinner.setSelection(0);
		// scanBeacons();
	}

	protected void onPause() {
		super.onPause();
		stopGPS();
		stopAll();
		/*
		 * if(ibp.isScanning()) { ibp.stopScan(); }
		 */
	}

	public void startAct() {
		if (act == 1) {
			startActivity(new Intent(this, KazanParks.class));
		} else if (act == 2) {
			startActivity(new Intent(this, ChelniParks.class));
		}
	}

	public static String findMyTown(String s) {
		int a, b;
		a = s.indexOf(",1:\"") + 4;
		b = s.indexOf("\"", a);
		return s.substring(a, b);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 9) {
			if (resultCode == Activity.RESULT_OK) {
				// scanBeacons();
			}
		}
	}

	protected void stopAll() {
		if (ald2 != null)
			ald2.cancel();
		if (ald1 != null)
			ald1.cancel();
		if (ald3 != null)
			ald3.cancel();
		try {
			ft.cancel(true);
		} catch (NullPointerException e) {
		}
	}

	public class findTown extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean break_c = true;
			while (break_c) {
				try { // ����� ������������ ����� � ������������ � ������
					myTown = findMyTown(g.getFromLocation(
							currLoc.getLatitude(), currLoc.getLongitude(), 1)
							.toString());

					if (myTown.equals("������")) {
						act = 1;
					} else if (myTown.equals("���������� �����")) {
						act = 2;
					} else {
					}
					break_c = false;
					Log.d("SP", Integer.toString(act));
				} catch (IOException e) {
					if (isCancelled()) {
						return null;
					}
				} catch (NullPointerException e1) {
					Log.d("SP", "Error NullPointerException");
					if (isCancelled()) {
						return null;
					}
				}
			}
			ald2.cancel();
			Log.d("ASYNC", "Finished!");
			startAct();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}

}