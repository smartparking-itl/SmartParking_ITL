package com.tim.smartparking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class MyService extends Service {
	static int t = 0;
	static int time = 0;
	NotificationManager nm;
	CountDownTimer cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			t++;
			if (t == 15) {
				t = 0;
				time++;
				updateNotif(time, "ООО Кольцо Казань");
			}
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub

		}

	}.start();

	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		sendNotif();
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressWarnings("deprecation")
	void sendNotif() {
		// 1-я часть
		Notification notif = new Notification(R.drawable.ic_launcher,
				"Вы только что припарковались", System.currentTimeMillis());

		// 3-я часть
		Intent intent = new Intent(this, ServerOplata.class);
		intent.putExtra(ServerOplata.FILE_NAME, "somefile");
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// 2-я часть
		notif.setLatestEventInfo(this, "Smart Parking",
				"Вы припарковались на парковке ТЦ Кольцо", pIntent);

		// ставим флаг, чтобы уведомление пропало после нажатия
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.FLAG_ONGOING_EVENT;
		notif.defaults |= Notification.DEFAULT_VIBRATE;
		// отправляем
		nm.notify(1, notif);	
	}

	@SuppressWarnings("deprecation")
	public void updateNotif(int time, String park) {
		Notification notif = new Notification(R.drawable.ic_launcher, "Вы уже "
				+ time + " часов на парквоке " + park,
				System.currentTimeMillis());
		if(time == 1) {
			notif.setLatestEventInfo(MyService.this, "Smart Parking", "Вы уже "
					+ time + " час на парквоке " + park, null);
		} else if(time == 2 || time == 3 || time == 4) {
			notif.setLatestEventInfo(MyService.this, "Smart Parking", "Вы уже "
					+ time + " часа на парквоке " + park, null);
		} else {
			notif.setLatestEventInfo(MyService.this, "Smart Parking", "Вы уже "
					+ time + " часов на парквоке " + park, null);
		}
		notif.defaults |= Notification.DEFAULT_VIBRATE;
		notif.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onDestroy() {
		t = 0;
		time = 0;
		cdt.cancel();
		nm.cancelAll();
	}
}
