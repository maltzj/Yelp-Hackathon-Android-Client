package com.example.yelp_hackathon_11_client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CheckInService extends IntentService {

	private int mRoomId;

	private static final String SERVICE_URL_BASE = "http://10.50.2.4:5000";

	private static final String KEY_ROOM_ID = "room_id_key";
	private static final String TAG = CheckInService.class.getSimpleName();
	private static final int KEY_CHECK_IN_NOTIFICATION = 1;

	private NotificationManager mManager;

	public CheckInService() {
		super(TAG);
	}

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
		Log.e("MALTZ", "notified with " + mRoomId);
	}

	protected void fireRequest(int roomId) {
		String url = null;
		if (!(CheckIn.isCheckedIn(this, roomId))) {
			url = SERVICE_URL_BASE + "/room/checkin/" + roomId;
		} else {
			url = SERVICE_URL_BASE + "/room/checkout/" + roomId;
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		try {
			HttpResponse response = httpClient.execute(postRequest);
			Log.e("MALTZ", "response code is " + response.getStatusLine().getStatusCode());
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity != null) {
				InputStream responseStream = responseEntity.getContent();
				responseStream.close();
			}
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mManager.cancel(KEY_CHECK_IN_NOTIFICATION);
	}
}
