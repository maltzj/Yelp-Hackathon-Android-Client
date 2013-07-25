package com.example.yelp_hackathon_11_client;

import java.io.UnsupportedEncodingException;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class NFCActivity extends Activity {

	int mTagId;
	TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);

		mTextView = (TextView) findViewById(R.id.activity_nfc_text);
	}

	protected void onResume() {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage msg = (NdefMessage) rawMsgs[0];

			byte[] payload = msg.getRecords()[0].getPayload();

			// Code shamelessly stolen from stack overflow

			// Get the Text Encoding
			
			String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
					: "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0077;

			try {
				// Get the Text
				String text = new String(payload, languageCodeLength + 1,
						payload.length - languageCodeLength - 1, textEncoding);
				String id = text.substring(text.lastIndexOf(":") + 1);
				mTagId = Integer.parseInt(id);
				mTextView.setText(String.valueOf(mTagId));
			} catch (UnsupportedEncodingException e) {
				// If they don't have UTF-8 or 16 just cry a little bit
			}
		}

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc, menu);
		return true;
	}

}
