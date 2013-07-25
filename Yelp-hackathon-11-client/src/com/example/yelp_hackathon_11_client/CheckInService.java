package com.example.yelp_hackathon_11_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class CheckInService extends IntentService {

	private int mRoomId;

	private static final String SERVICE_URL_BASE = "STUFF";

	private static final String KEY_ROOM_ID = "room_id_key";
	private static final int KEY_CHECK_IN_NOTIFICATION = 1;

	private NotificationManager mManager;

	public CheckInService(String name) {
		super(name);
	}

	public static Intent intentForCheckIn(int roomId, Context ctx) {
		Intent newIntent = new Intent(ctx, CheckInService.class);
		newIntent.putExtra(KEY_ROOM_ID, roomId);
		return newIntent;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mRoomId = intent.getExtras().getInt(KEY_ROOM_ID);

		NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
		.setOngoing(true)
		.setSmallIcon(android.R.drawable.stat_sys_upload)
		.setContentText(getString(R.string.notification));
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mManager.notify(KEY_CHECK_IN_NOTIFICATION, notification.build());

		fireRequest(mRoomId);
	}

	protected void fireRequest(int roomId) {
		String url = null;
		if (!(CheckIn.isCheckedIn(this, roomId))) {
			url = SERVICE_URL_BASE + roomId + "/in";
		} else {
			url = SERVICE_URL_BASE + roomId + "/out";
		}

		// Try to open a connection
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.connect();
			InputStream inStream = connection.getInputStream();
			OutputStream outStream = connection.getOutputStream();
			inStream.close();
			outStream.close();
		} catch (MalformedURLException e) {
			// Be sad
		} catch (IOException e) {
			// Be sad
		}

		mManager.cancel(KEY_CHECK_IN_NOTIFICATION);
	}
}
