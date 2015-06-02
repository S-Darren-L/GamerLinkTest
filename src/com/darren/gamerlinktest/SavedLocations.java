package com.darren.gamerlinktest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

public class SavedLocations extends ListActivity {

	public static final String MyPREFERENCES = "GamerLinkTestLocation";
	SharedPreferences sharedpreferences;
	public static final String SAVEDLOCATION = "SavedLocation";
	public static final String LOCATION = "Location";
	public static final String DESCRIPTION = "Description";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	ListView lvSavedLocationsItem;
	ArrayList<String[]> savedLocationsList = new ArrayList<String[]>();
	ListViewAdapter savedLocationsAdapter;
	String latitude;
	String longitude;
	String markerName;
	String markerDescription;

	String TAG_SAVE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_locations);
		lvSavedLocationsItem = (ListView) findViewById(android.R.id.list);
		savedLocationsAdapter = new ListViewAdapter(savedLocationsList, this);

		sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
		Map<String, ?> keys = sharedpreferences.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {

			System.out.println("map values" + entry.getKey() + ": "
					+ entry.getValue().toString());

			String locationKey = entry.getKey();
			Gson gson = new Gson();
			String json = sharedpreferences.getString(locationKey, "");
			ArrayList<String> savedLocationInfoArrayList = new ArrayList<String>(
					Arrays.asList(gson.fromJson(json, String[].class)));
			savedLocationInfoArrayList.add(locationKey);
			String[] savedLocationInfo = new String[savedLocationInfoArrayList
					.size()];
			savedLocationInfo = savedLocationInfoArrayList
					.toArray(savedLocationInfo);
			markerName = savedLocationInfo[0];
			markerDescription = savedLocationInfo[1];
			latitude = savedLocationInfo[2];
			longitude = savedLocationInfo[3];
			savedLocationsList.add(savedLocationInfo);
		}
		
		lvSavedLocationsItem.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Toast.makeText(getApplicationContext(),
						"Clicked", Toast.LENGTH_SHORT).show();
			}
			
		});
		lvSavedLocationsItem.setAdapter(savedLocationsAdapter);
	}

	public class ListViewAdapter extends BaseAdapter {

		Context ctxt;
		LayoutInflater listViewInflater;
		ArrayList<String[]> listItemsList;

		public ListViewAdapter(ArrayList<String[]> listItemsList, Context ctxt) {
			this.listItemsList = listItemsList;
			this.ctxt = ctxt;
			listViewInflater = (LayoutInflater) ctxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItemsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItemsList.get(position)[0];
		}

		public Object getItemPrivacy(int position) {
			return listItemsList.get(position)[2];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) ctxt
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listviet_row, parent,
						false);

			}
			TextView labelView = (TextView) convertView.findViewById(R.id.name);
			TextView valueView = (TextView) convertView
					.findViewById(R.id.location);
			ImageButton btnLocate = (ImageButton) convertView
					.findViewById(R.id.btnLocate);
			ImageButton btnShare = (ImageButton) convertView
					.findViewById(R.id.btnShare);
			ImageButton btnDelete = (ImageButton) convertView
					.findViewById(R.id.btnDelete);
			labelView.setText(listItemsList.get(position)[0]);
			valueView.setText(listItemsList.get(position)[1]);
			
			btnLocate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String locationKey = listItemsList.get(position)[4];
					String locationInfo = (sharedpreferences.getString(locationKey, ""));
					Gson gson = new Gson();
					ArrayList<String> savedLocationInfoArrayList = new ArrayList<String>(
							Arrays.asList(gson.fromJson(locationInfo, String[].class)));
					String[] savedLocationInfo = new String[savedLocationInfoArrayList
							.size()];
					savedLocationInfo = savedLocationInfoArrayList
							.toArray(savedLocationInfo);
					markerName = savedLocationInfo[0];
					markerDescription = savedLocationInfo[1];
					latitude = savedLocationInfo[2];
					longitude = savedLocationInfo[3];
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					intent.putExtra(SAVEDLOCATION, true);
					intent.putExtra(LOCATION, markerName);
					intent.putExtra(DESCRIPTION, markerDescription);
					intent.putExtra(LATITUDE, latitude);
					intent.putExtra(LONGITUDE, longitude);

					startActivity(intent);
					
				}
			});

			btnShare.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String locationKey = listItemsList.get(position)[4];
					String locationInfo = (sharedpreferences.getString(locationKey, ""));
					Gson gson = new Gson();
					ArrayList<String> savedLocationInfoArrayList = new ArrayList<String>(
							Arrays.asList(gson.fromJson(locationInfo, String[].class)));
					String[] savedLocationInfo = new String[savedLocationInfoArrayList
							.size()];
					savedLocationInfo = savedLocationInfoArrayList
							.toArray(savedLocationInfo);
					markerName = savedLocationInfo[0];
					markerDescription = savedLocationInfo[1];
					latitude = savedLocationInfo[2];
					longitude = savedLocationInfo[3];
					Intent intent = new Intent(getApplicationContext(),
							ShareLocation.class);
					intent.putExtra(LOCATION, markerName);
					intent.putExtra(DESCRIPTION, markerDescription);
					intent.putExtra(LATITUDE, latitude);
					intent.putExtra(LONGITUDE, longitude);

					startActivity(intent);
				}

			});

			btnDelete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String locationKey = listItemsList.get(position)[4];
					Editor editor = sharedpreferences.edit();
					editor.remove(locationKey);
					editor.commit();
					finish();
					startActivity(getIntent());
				}
			});
			return convertView;
		}
	}

}
