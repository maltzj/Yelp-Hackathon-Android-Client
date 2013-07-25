package com.example.yelp_hackathon_11_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CheckIn {

	private static final String CHECK_INS_PREFS = "check_ins_prefs";
	private static final long CHECK_OUT_THRESHOLD_IN_MS = 1000 * 60 * 60; // One Hour

	public static boolean isCheckedIn(Context ctx, int id) {
		SharedPreferences prefs = ctx.getSharedPreferences(CHECK_INS_PREFS, Context.MODE_PRIVATE);
		Editor prefEditor = prefs.edit();

		long lastCheckIn = prefs.getLong(String.valueOf(id), -1);

		// If we got negative one, they didn't check in before
		if (lastCheckIn == -1 ) {
			prefEditor.putLong(String.valueOf(id), System.currentTimeMillis());
			prefEditor.commit();
			return false;
		} else {
			// Otherwise if they last ehcecked in within our check out threshold, make this a checkout
			if (System.currentTimeMillis() - lastCheckIn < CHECK_OUT_THRESHOLD_IN_MS) {
				prefEditor.remove(String.valueOf(id));
				prefEditor.commit();
				return true;
			} else {
				prefEditor.putLong(String.valueOf(id), System.currentTimeMillis());
				prefEditor.commit();
				return false;
			}
		}
	}
}
