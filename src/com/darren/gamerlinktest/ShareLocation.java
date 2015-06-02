package com.darren.gamerlinktest;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareLocation extends Activity {

	public static final String LOCATION = "Location";
	public static final String DESCRIPTION = "Description";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String EMAIL = "Email";
	public static final String SMS = "SMS";
	String locationInfo;
	String latitude;
	String longitude;
	String markerName;
	String markerDescription;

	TextView tvMarkerNameValue;
	TextView tvMarkerDesciptionValue;
	TextView tvMarkerLatLngValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);

		tvMarkerNameValue = (TextView) findViewById(R.id.tvMarkerNameValue);
		tvMarkerDesciptionValue = (TextView) findViewById(R.id.tvMarkerDesciptionValue);
		tvMarkerLatLngValue = (TextView) findViewById(R.id.tvMarkerLatLngValue);

		Intent intent = getIntent();
		markerName = intent.getStringExtra(LOCATION);
		markerDescription = intent.getStringExtra(DESCRIPTION);
		latitude = intent.getStringExtra(LATITUDE);
		longitude = intent.getStringExtra(LONGITUDE);

		tvMarkerNameValue.setText(markerName);
		tvMarkerDesciptionValue.setText(markerDescription);
		tvMarkerLatLngValue.setText(latitude + ", " + longitude);
	}

	public void onEmailClick(View view) {
		showInputDialog(EMAIL);
	}

	public void onSMSClick(View view) {
		showInputDialog(SMS);
	}

	protected void showInputDialog(final String sendingTypes) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(ShareLocation.this);
		View promptView = layoutInflater.inflate(
				R.layout.share_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ShareLocation.this);
		alertDialogBuilder.setView(promptView);

		TextView tvReceiverName = (TextView) promptView
				.findViewById(R.id.tvReceiverName);
		tvReceiverName.setText(sendingTypes + ": ");

		final EditText etReceiverName = (EditText) promptView
				.findViewById(R.id.etReceiverName);
		final String Message = "Shared locaiont: name: " + markerName
				+ " description: " + markerDescription + " location: "
				+ latitude + ", " + longitude;

		// setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Send",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								final String receiverName = etReceiverName
										.getText().toString();
								if (!receiverName.equals("")) {
									if(sendingTypes.equals(EMAIL) && !checkEmail(receiverName)){
										Toast.makeText(getApplicationContext(),
												"Invalid email address",
												Toast.LENGTH_SHORT).show();
									}
									else{
										shareLocation(sendingTypes, receiverName,
												Message);										
									}
								} else {
									Toast.makeText(getApplicationContext(),
											"Please input receiver",
											Toast.LENGTH_SHORT).show();
								}

								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										etReceiverName.getWindowToken(), 0);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	public void shareLocation(String sendingTypes, String receiverName,
			String Message) {
		String subject = "Share location";
		if (sendingTypes.equals(EMAIL)) {
			String[] TO = { receiverName };
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setData(Uri.parse("mailto:"));
			emailIntent.setType("text/plain");

			emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, Message);

			try {
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				finish();
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(ShareLocation.this,
						"There is no email client installed.",
						Toast.LENGTH_SHORT).show();
			}
		} else if (sendingTypes.equals(SMS)) {

			try {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(receiverName, null, Message, null, null);
				Toast.makeText(getApplicationContext(), "SMS sent.",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"SMS faild, please try again.", Toast.LENGTH_LONG)
						.show();
				e.printStackTrace();
			}
		}
	}
	
	boolean checkEmail(String mail) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		return mail.matches(regex);
		}
}
