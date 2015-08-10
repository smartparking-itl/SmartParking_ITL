package com.tim.smartparking;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

public class SPMapActivity extends Activity {
	private static final String TAG = "bluetooth2";

	WebView WebArduino;
	Handler h;
	SPImageView iv;

	private static final int REQUEST_ENABLE_BT = 1;
	final int RECIEVE_MESSAGE = 1; // Статус для Handler
	private BluetoothAdapter btAdapter = null;
	private static ImageView[] ivCar = new ImageView[2];
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();

	private ConnectedThread mConnectedThread;

	// SPP UUID сервиса
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// MAC-адрес Bluetooth модуля
	private static String address = "20:13:06:03:05:81";

	private static void setColorCars(String s) {
		int i;
		for (i = 0; i <= s.length() - 1; i++) {
			if (s.charAt(i) == '1') {
				ivCar[i].setBackgroundResource(R.drawable.redcar);
			} else if (s.charAt(i) == '2') {
				ivCar[i].setBackgroundResource(R.drawable.greencar);
			}
		}
	}

	/** Called when the activity is first created. */
	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kolco_map1);

	/*	ivCar[0] = (ImageView) findViewById(R.id.imageView2);
		ivCar[1] = (ImageView) findViewById(R.id.ImageView3);*/
		iv = (SPImageView) findViewById(R.id.spiv1);
		iv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent mv) {
				// TODO Auto-generated method stub
				Toast.makeText(SPMapActivity.this,
						"X: " + mv.getX() + " Y: " + mv.getY(),
						Toast.LENGTH_SHORT).show();
				if (mv.getAction() == MotionEvent.ACTION_UP) {
					v.performClick();
				}
				return false;
			}

		});

		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE: // если приняли сообщение в Handler
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);
					sb.append(strIncom); // формируем строку
					int endOfLineIndex = sb.indexOf("\r\n"); // определяем
																// символы конца
																// строки
					if (endOfLineIndex > 0) { // если встречаем конец строки,
						String sbprint = sb.substring(0, endOfLineIndex); // то
																			// извлекаем
																			// строку
						sb.delete(0, sb.length()); // Дальше можно обрабатывать
													// строку
						// и очищаем sb
						setColorCars(sbprint);
						// Log.d(TAG, "...Строка:"+ sb.toString() + "Байт:" +
						// msg.arg1 + "...");
						break;
					}
				}
			};
		};

		btAdapter = BluetoothAdapter.getDefaultAdapter(); // получаем локальный
															// Bluetooth адаптер
		checkBTState();

	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "...onResume - попытка соединения...");

		// Set up a pointer to the remote node using it's address.
		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		// Two things are needed to make a connection:
		// A MAC address, which we got above.
		// A Service ID or UUID. In this case we are using the
		// UUID for SPP.
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: "
					+ e.getMessage() + ".");
		}

		// Discovery is resource intensive. Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection. This will block until it connects.
		Log.d(TAG, "...Соединяемся...");
		try {
			btSocket.connect();
			Log.d(TAG,
					"...Соединение установлено и готово к передачи данных...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Создание Socket...");

		mConnectedThread = new ConnectedThread(btSocket);
		mConnectedThread.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		MainActivity.stopGPS();
		Log.d(TAG, "...In onPause()...");

		try {
			btSocket.close();
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket."
					+ e2.getMessage() + ".");
		}
	}

	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned
		// on
		// Emulator doesn't support Bluetooth and will return null
		if (btAdapter == null) {
			errorExit("Fatal Error", "Bluetooth not supported");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth включен...");
			} else {
				// Prompt user to turn on Bluetooth
				@SuppressWarnings("static-access")
				Intent enableBtIntent = new Intent(
						btAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	private void errorExit(String title, String message) {
		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
		}

		public void run() {
			byte[] buffer = new byte[256]; // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer); // Получаем кол-во байт и
														// само собщение в
														// байтовый массив
														// "buffer"
					h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
							.sendToTarget(); // Отправляем в очередь сообщений
												// Handler
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */

		/* Call this from the main activity to shutdown the connection */
		@SuppressWarnings("unused")
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
