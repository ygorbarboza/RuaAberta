package br.com.hackathon.hackathon1746.geofence;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import android.widget.Toast;
import br.com.hackathon.hackathon1746.R;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

public class GeoFenceActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener,
		OnAddGeofencesResultListener, Listener, ErrorListener {

	private IntentFilter mIntentFilter;
	private LocationClient locationClient;
	private LocationRequest locatRequest;

	private PendingIntent intent;
	private List<Geofence> mGeoList;
	private Context mContext;
	private Geofence companyLocation;
	private GeofenceSampleReceiver mBroadcastReceiver;protected RequestQueue mRequestQueue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geofence);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRequestQueue = Volley.newRequestQueue(this);

		mContext = this;

		locatRequest = null;
		mGeoList = new ArrayList<Geofence>();

		intent = null;
		locationClient = new LocationClient(this, this, this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.example.geofence.ACTION_GEOFENCES_ADDED");
		mIntentFilter
				.addCategory("com.example.geofence.CATEGORY_LOCATION_SERVICES");
		mBroadcastReceiver = new GeofenceSampleReceiver();
	}

	@Override
	protected void onStart() {

		companyLocation = new Geofence.Builder()
				.setRequestId("1")
				.setTransitionTypes(
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_EXIT)
				.setCircularRegion(getIntent().getDoubleExtra("lat", 0.0),
						getIntent().getDoubleExtra("lng", 0.0), (float) 50)
				.setExpirationDuration(Geofence.NEVER_EXPIRE).build();

		mGeoList.add(companyLocation);
		locationClient.connect();
		super.onStart();

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		intent = getTransitionPendingIntent();

		locatRequest = LocationRequest.create();
		locatRequest
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locatRequest.setInterval(5000);

		try {
			addGeofence();
		} catch (UnsupportedOperationException e) {
			Toast.makeText(this, "add_geofences_already_requested_error",
					Toast.LENGTH_LONG).show();
		}

	}

	public void addGeofence() {
		locationClient.addGeofences(mGeoList, intent, this);

	}

	private PendingIntent getTransitionPendingIntent() {

		Intent localIntent = new Intent(this, GeoFenceIntentService.class);

		return PendingIntent.getService(this, 0, localIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Override
	protected void onStop() {

		locationClient.disconnect();
		super.onStop();

	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {

		Intent broadcastIntent = new Intent();

		if (LocationStatusCodes.SUCCESS == statusCode) {
			// Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

			broadcastIntent
					.setAction(
							"com.example.android.geofence.ACTION_GEOFENCES_ADDED")
					.addCategory(
							"com.example.android.geofence.CATEGORY_LOCATION_SERVICES")
					.putExtra(
							"com.example.android.geofence.EXTRA_GEOFENCE_STATUS",
							"test");
		} else {
			Toast.makeText(this, "AddGeoError", Toast.LENGTH_SHORT).show();
		}

		LocalBroadcastManager.getInstance(mContext).sendBroadcast(
				broadcastIntent);

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		switch (code) {

		case ConnectionResult.SERVICE_MISSING: {
			Toast.makeText(
					this,
					"SERVICE_MISSING " + code
							+ " ConnectionResult.SERVICE_MISSING "
							+ ConnectionResult.SERVICE_MISSING,
					Toast.LENGTH_SHORT).show();
			break;

		}
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {

			Toast.makeText(
					this,
					"SERVICE_VERSION_UPDATE_REQUIRED "
							+ code
							+ " ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED "
							+ ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
					Toast.LENGTH_SHORT).show();

			break;
		}
		default: {
			Toast.makeText(this, "start " + code, Toast.LENGTH_SHORT).show();
		}

		}
	}

	@Override
	protected void onResume() {

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mBroadcastReceiver, mIntentFilter);

		super.onResume();
	}

	@Override
	public void onDisconnected() {

		locationClient = null;
	}

	public class GeofenceSampleReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (TextUtils.equals(action,
					"com.example.geofence.ACTION_GEOFENCES_ADDED")) {

				handleGeofenceStatus(context, intent);

			} else if (TextUtils.equals(action,
					"com.example.geofence.ACTION_GEOFENCE_TRANSITION")) {

				handleGeofenceTransition(context, intent);

			} else {

				Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
			}
		}

		private void handleGeofenceStatus(Context context, Intent intent) {

		}

		private void handleGeofenceTransition(Context context, Intent intent) {

		}

		private void handleGeofenceError(Context context, Intent intent) {

		}
	}

	public void onAfterCLicked(View v) {
		
		finish();

	}

	public void onOKClicked(View v) {
		
		String url = null;
		
		mRequestQueue.add(new StringRequest(url, this, this));

	}

	@Override
	public void onErrorResponse(VolleyError error) {

		Toast.makeText(this, "Avaliação enviada", Toast.LENGTH_SHORT).show();
		finish();
		
	}

	@Override
	public void onResponse(Object response) {

		Toast.makeText(this, "Erro ao enviar avaliação", Toast.LENGTH_SHORT).show();
		
	}

}