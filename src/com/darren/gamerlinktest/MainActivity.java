package com.darren.gamerlinktest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GoogleMap map;
	private double lat = 0;
	private double lng = 0;
	private static final float DEFAULT_ZOOM = 14;
	private static String MARKER_TITLE = "You are here";
	LocationTracker location;

	public static final String MyPREFERENCES = "GamerLinkTestLocation";
	public static final String SAVEDLOCATION = "SavedLocation";
	public static final String LOCATION = "Location";
	public static final String DESCRIPTION = "Description";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	SharedPreferences sharedpreferences;

	EditText etSearch;
	Marker marker;

	String markerName;
	String markerDescription = "";
	Boolean savedLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etSearch = (EditText) findViewById(R.id.etSearch);

		sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

		Intent intent = getIntent();
		savedLocation = intent.getBooleanExtra(SAVEDLOCATION, false);
		if (savedLocation == true) {
			markerName = intent.getStringExtra(LOCATION);
			markerDescription = intent.getStringExtra(DESCRIPTION);
			lat = Double.valueOf(intent.getStringExtra(LATITUDE));
			lng = Double.valueOf(intent.getStringExtra(LONGITUDE));
		}

		if (lat == 0 && lng == 0) {
			location = new LocationTracker(MainActivity.this);
			if (location.canGetLocation()) {
				lat = location.getLatitude();
				lng = location.getLongitude();
			} else {
				lat = 43.653815;
				lng = -79.376256;
			}
		}
		if (markerName == null) {
			markerName = MARKER_TITLE;
		}

		initMap();

		if (map == null) {
			initMap();
		}
		if (map != null) {
			map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng ll) {
					// TODO Auto-generated method stub
					Geocoder gc = new Geocoder(MainActivity.this);
					List<Address> list = null;
					lat = ll.latitude;
					lng = ll.longitude;
					try {
						list = gc.getFromLocation(lat, lng, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					Address add = list.get(0);
					showInputDialog(lat, lng);

				}
			});

			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					// TODO Auto-generated method stub
					System.out.println("setOnMarkerClickListener start");
					LatLng latlng = new LatLng(lat, lng);
					marker = map.addMarker(new MarkerOptions()
							.title(markerName).position(latlng)
							.snippet(markerDescription));
					System.out.println("lat: " + lat + " lng: " + lng
							+ " markerName: " + markerName
							+ " markerDescription: " + markerDescription);
					marker.showInfoWindow();
					return true;
				}

			});
		}

		map.setMyLocationEnabled(true);

		gotoLocation(lat, lng, DEFAULT_ZOOM, markerName, markerDescription);
	}

	private boolean initMap() {
		MapFragment mapFrag = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		map = mapFrag.getMap();
		return (map != null);
	}

	private void gotoLocation(double lat, double lng, float zoom,
			String markerName, String markerDescription) {
		setMarker(lat, lng, markerName, markerDescription);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		LatLng latlng = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
				DEFAULT_ZOOM);
		map.animateCamera(update);
	}

	public void onSettingClick(View view) {
		Intent intent = new Intent(getApplicationContext(),
				SavedLocations.class);
		startActivity(intent);
	}

	public void onSearchClick(View view) throws IOException {
		hideSoftKeyboard(view);
		String searchLocation = etSearch.getText().toString();
		if (!searchLocation.equals("")) {
			Geocoder gc = new Geocoder(this);
			List<Address> list = gc.getFromLocationName(searchLocation, 1);
			Address add = list.get(0);
			lat = add.getLatitude();
			lng = add.getLongitude();
			String localtiy = add.getLocality();
			markerDescription = add.getFeatureName();
			markerName = localtiy;
			gotoLocation(lat, lng, DEFAULT_ZOOM, markerName, markerDescription);
		} else {
			Toast.makeText(getApplicationContext(), "Noting to search",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void setMarker(double lat, double lng, String markerName,
			String markerDescription) {
		if (marker != null) {
			marker.remove();
			map.clear();
		}
		LatLng latlng = new LatLng(lat, lng);
		MarkerOptions markerOptions = new MarkerOptions().title(markerName)
				.position(latlng).snippet(markerDescription);
		marker = map.addMarker(markerOptions);
		marker.showInfoWindow();
	}

	protected void showInputDialog(final double lat, final double lng) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
		View promptView = layoutInflater.inflate(
				R.layout.input_location_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);
		alertDialogBuilder.setView(promptView);

		final EditText etMarkerName = (EditText) promptView
				.findViewById(R.id.etMarkerName);
		final EditText etMarkerDescription = (EditText) promptView
				.findViewById(R.id.etMarkerDescription);
		final TextView evMarkerLatLngValue = (TextView) promptView
				.findViewById(R.id.evMarkerLatLngValue);

		evMarkerLatLngValue.setText(lat + ", " + lng);
		// setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								markerName = etMarkerName.getText().toString();
								markerDescription = etMarkerDescription
										.getText().toString();
								saveLocation(lat, lng, markerName,
										markerDescription);
								setMarker(lat, lng, markerName, markerDescription);

								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										etMarkerDescription.getWindowToken(), 0);
							}
						})
				.setNeutralButton("Share",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								markerName = etMarkerName.getText().toString();
								markerDescription = etMarkerDescription
										.getText().toString();
								saveLocation(lat, lng, markerName,
										markerDescription);
								System.out.println("markerName: " + markerName
										+ "markerDescription: "
										+ markerDescription + "latitude: "
										+ lat + "longitude: " + lng);

								Intent intent = new Intent(
										getApplicationContext(),
										ShareLocation.class);
								intent.putExtra(LOCATION, markerName);
								intent.putExtra(DESCRIPTION, markerDescription);
								intent.putExtra(LATITUDE,
										String.valueOf(lat));
								intent.putExtra(LONGITUDE,
										String.valueOf(lng));

								startActivity(intent);
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

	public void saveLocation(double latitude, double longitude,
			String markerName, String markerDescription) {
		Editor editor = sharedpreferences.edit();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
		String TAG_SAVE = sdf.format(c.getTime());
		String[] defValues = { markerName, markerDescription,
				String.valueOf(latitude), String.valueOf(longitude) };
		Gson gson = new Gson();
		String json = gson.toJson(defValues);

		editor.putString(TAG_SAVE, json);
		editor.commit();
	}

	private void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(map);
	}

//	 @Override
//	 protected void onResume() {
//	 super.onResume();
//	 MapStateManager mgr = new MapStateManager(this);
//	 CameraPosition position = mgr.getSavedCameraPosition();
//	 if (position != null) {
//	 CameraUpdate update = CameraUpdateFactory
//	 .newCameraPosition(position);
//	 map.moveCamera(update);
//	 setMarker(lat, lat, markerName, markerDescription);
//	 }
//	 }

}
