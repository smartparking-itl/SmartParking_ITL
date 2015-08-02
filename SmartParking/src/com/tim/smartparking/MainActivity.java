package com.tim.smartparking;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.easibeacon.protocol.IBeacon;
import com.easibeacon.protocol.IBeaconListener;
import com.easibeacon.protocol.IBeaconProtocol;
import com.easibeacon.protocol.Utils;

public class MainActivity extends Activity {
	
	
	private static final int minorKzn = 4301;
	Button btnFind;
	static Spinner Spinner;				//Спиннер
	static AlertDialog.Builder ad;		//Нужно
	static int act = 0;					// Какой активити открыть1`
	static AlertDialog ald, ald1, ald2, ald3;	//ald3 - internet; ald2 - wait; ald - findYou; ald1 - no GPS
	static boolean readyAld2 = false;	//Это две
	static boolean isFound = false;		//нужных переменных
	static boolean doSearch = false;	//Чекает, если юзер хочет, чтоб его нашли
	static CountDownTimer cdt;			//Проверяет и открывает другое активити при необходимости
	static Geocoder g;					//Превращает координаты в название города
	static String myTown; // В эту строку будет записан город, например,
							// "Уруссу"
	public static IBeaconProtocol ibp;
	public static LocationManager lm;
	  
	public static LocationListener locationlistener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			try { // Здесь определяется город и записывается в строку
				myTown = findMyTown(g.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1).toString());
				if (readyAld2 && isFound) {
					if (myTown.equals("Казань")) {
						act = 1;
					} else if (myTown.equals("Набережные Челны")) {
						act = 2;
					} else {
					}
					ald2.cancel();
				}
			} catch (IOException e) { /*
				// Тут рассматривается случай, когда нет инета и не удается
				// определить город по координатам
				ad.setTitle("Нет доступа в Интернет");
				ad.setMessage("Не удается подключиться к Интернету. Перейти в настройки?");
				ad.setPositiveButton("Перейти",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								MainActivity ma = new MainActivity();
								ma.startActivityForResult(
										new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS),
										1);
							}
						});
				ad.setNegativeButton("Я сам выберу город",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								doSearch = false;
								dialog.cancel();
							}
						});
				ald3 = ad.create();
				ald3.show(); */
			} 
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			if (ald1 != null && doSearch == true)
				ald1.cancel(); // Закрываем это меню, если пользователь включил
								// GPS через шторку
			readyAld2 = true;
			ald2.show();
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
		
		// Тут инизиализируем поиск iBeacon
		ibp = IBeaconProtocol.getInstance(this);
		ibp.setListener(new IBeaconListener() {

			@Override
			public void enterRegion(IBeacon ibeacon) {
				// TODO Auto-generated method stub

			}

			@Override
			public void exitRegion(IBeacon ibeacon) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beaconFound(IBeacon ibeacon) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void searchState(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void operationError(int status) {
				// TODO Auto-generated method stub
				
			}
			
		});		
		// Тут инициализируем геокодер и GPS-менеджер
		g = new Geocoder(this);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Тут диалог, предлагающий найти тебя. Также тут предложение включить
		// GPS
		ad = new AlertDialog.Builder(this);
		
		
		// Тут создаются AlertDialog'и
		ad.setMessage(R.string.btn_find_me);
		ad.setPositiveButton("", null);
		ad.setNegativeButton("", null);
		ald2 = ad.create();
		isFound = true;
		
		ad.setMessage(R.string.dlg_find_me);
		ad.setPositiveButton(R.string.dlg_find_me_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
						startActivityForResult(
								new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
								0);
					}
				});
		ad.setNegativeButton(R.string.dlg_find_me_no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		ald1 = ad.create();

		ad.setTitle("");
		ad.setMessage(R.string.dlg_find_me_city);
		ad.setCancelable(false);
		ad.setPositiveButton(R.string.dlg_find_me_yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				doSearch = true;
				dialog.cancel();
				if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { // Если
																				// выключен
																				// GPS
					ald1.show();
				} else if(doSearch) { // Если включен GPS
					startGPS();
					ald2.show();
				}
			}

		});
		ad.setNegativeButton(R.string.dlg_find_me_no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
						doSearch = false;
					}
				});
		ald = ad.create();
		
		btnFind = (Button) findViewById(R.id.btnFind);
		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cdt.start();
				isFound = true;
				ald.show();
			}
			
		});
		cdt = new CountDownTimer(Long.MAX_VALUE, 100) {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				if(act != 0 && readyAld2) {
					if(act == 1) {
						doSearch = false;
						if(ald2 != null) {
							ald2.cancel();
						}
						startActivity(new Intent(MainActivity.this, KazanParks.class));
					}
				}
			}
			
		}.start();

		Spinner = (Spinner) findViewById(R.id.spinner);
		Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				System.err.println("**************" + arg2);

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
    
	private void scanBeacons(){
		// Check Bluetooth every time
		Log.i(Utils.LOG_TAG,"Scanning");
		
		// Filter based on default easiBeacon UUID, remove if not required
		//_ibp.setScanUUID(UUID here);

		if(!IBeaconProtocol.configureBluetoothAdapter(this)){
			//Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    //startActivityForResult(enableBtIntent, 9 );
		}else{
			if(ibp.isScanning())
				ibp.stopScan();
			ibp.reset();
			ibp.startScan();		
		}		
	}
	
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
			Toast.makeText(getBaseContext(), R.string.exit,
					Toast.LENGTH_SHORT).show();
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
		startGPS();
		if(ald2 != null) ald2.cancel();
		Spinner.setSelection(0);
		if(doSearch) cdt.start();
		scanBeacons();
	}

	protected void onPause() {
		super.onPause();
		stopGPS();
		stopAll();
		if(ibp.isScanning()) {
			ibp.stopScan();
		}
	}

	public static String findMyTown(String s) {
		int a, b;
		a = s.indexOf(",1:\"") + 4;
		b = s.indexOf("\"", a);
		return s.substring(a, b);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(requestCode == 9){
				if(resultCode == Activity.RESULT_OK){
					scanBeacons();
				}
			}
		doSearch = true;
		cdt.start();
		if(ald2 != null) {
			ald2.cancel();
		}
		if (requestCode == 0) {
			if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				ald1.show();
			} else {
				readyAld2 = true;
				ald2.show();
			}
		} else if (requestCode == 1) {
			if (isOnline()) {
				if (ald3 != null) {
					ald3.cancel();
				}
			} else {
				ald2.show();
			}
		}
	}
	
	protected void stopAll() {
		if(ald2 != null) ald2.cancel();
		if(ald1 != null) ald1.cancel();
		if(ald != null) ald.cancel();
		if(ald3 != null) ald3.cancel();
		cdt.cancel();
	}
	
	// Отключаемся от iBeacon
	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	  }

	  // Проверка поддержки Bluetooth 4.0 и его состояния (вкл\выкл)
	  @Override
	  protected void onStart() {
	    super.onStart();
	  }

	  @Override
	  protected void onStop() {
		  super.onStop();
	  }
}