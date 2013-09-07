package br.com.hackathon.hackathon1746;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.hackathon.hackathon1746.geofence.GeoFenceActivity;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class HomeActivity extends BaseVolleyActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationRequest mLocationRequest;

	private LocationClient mLocationClient;
	private SupportMapFragment mMapFragment;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private GoogleMap mMap;
	boolean isFirstRequest = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new IconedTextViewAdapter(this, getResources()
				.getStringArray(R.array.navigation_item), getResources()
				.obtainTypedArray(R.array.navigation_icon)));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);

		mMap = mapFragment.getMap();
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);

		mLocationRequest = LocationRequest.create();

		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		mLocationClient = new LocationClient(this, this, this);

		getStreets();
		
		randomMarker();

	}

	private void getStreets() {
		
		String url = "http://ondeeuparo.herokuapp.com/paths.json";

		ParameterResponse response = new ParameterResponse() {

			@Override
			public void onResponse(Object response) {

				JSONArray json;
				try {
					json = new JSONArray((String) response);

					getDirectionFormGoogleMapsDirection(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		};

		ParamsRequest request = new ParamsRequest(url, response, this);

		mRequestQueue.add(request);
	}

	@Override
	public void onStop() {

		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		mLocationClient.disconnect();

		super.onStop();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onStart() {

		super.onStart();

		mLocationClient.connect();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		switch (requestCode) {

		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {

			case Activity.RESULT_OK:

				Log.d(LocationUtils.APPTAG, "OK");

				break;

			default:

				Log.d(LocationUtils.APPTAG, "Diferente");

				break;
			}

		default:

			Log.d(LocationUtils.APPTAG, "unknown");

			break;
		}
	}

	private boolean servicesConnected() {

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode) {

			Log.d(LocationUtils.APPTAG,
					getString(R.string.google_service_avaliable));

			return true;

		} else {

			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);

			if (dialog != null) {

			}

			return false;
		}
	}

	public LatLng getLocation() {

		if (servicesConnected()) {

			Location currentLocation = mLocationClient.getLastLocation();
			
			LatLng latLng = new LatLng(
					currentLocation.getLatitude(), currentLocation
					.getLongitude());
			
			return latLng;
			
			// mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
		}
		
		return null;

	}
	
	public void setUserLocation() {

		LatLng latLng = getLocation();
		
		CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		mMap.moveCamera(center);
		mMap.animateCamera(zoom);

	}

	public void startUpdates(View v) {
		// mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	public void stopUpdates(View v) {
		// mUpdatesRequested = false;

		if (servicesConnected()) {
			// stopPeriodicUpdates();
		}
	}

	@Override
	public void onConnected(Bundle bundle) {

		// if (mUpdatesRequested) {
		// startPeriodicUpdates();
		// }
		setUserLocation();
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (connectionResult.hasResolution()) {
			try {

				connectionResult.startResolutionForResult(this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {

				e.printStackTrace();
			}
		} else {

			// showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		// mConnectionStatus.setText(R.string.location_updated);

		// mLatLng.setText(LocationUtils.getLatLng(this, location));
	}

	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		// mConnectionState.setText(R.string.location_requested);
	}

	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
		// mConnectionState.setText(R.string.location_updates_stopped);
	}

	public void drawLine(ArrayList latlngs, String parking) {

		PolylineOptions rectOptions = new PolylineOptions();

		for (int i = 0; i < latlngs.size(); i++) {

			rectOptions = rectOptions.add((LatLng) latlngs.get(i));

		}

		if (parking.equals("true")) {

			rectOptions.color(Color.parseColor("#7000cc00"));

		} else {

			rectOptions.color(Color.parseColor("#70cc0000"));

		}

		Polyline polyline = mMap.addPolyline(rectOptions);

	}

	public void getDirections(double lat1, double lon1, double lat2,
			double lon2, String parking) {

		String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
				+ lat1
				+ ","
				+ lon1
				+ "&destination="
				+ lat2
				+ ","
				+ lon2
				+ "&sensor=false&units=metric&mode=walking";

		ParameterResponse response = new ParameterResponse() {

			@Override
			public void onResponse(Object response) {

				try {
					JSONObject json = new JSONObject((String) response);
					drawLineFromJSON(json, getParameter("parking"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		response.addParameter("parking", parking);

		ParamsRequest request = new ParamsRequest(url, response, this);

		mRequestQueue.add(request);

	}

	public ArrayList<LatLng> getLatLngFormJSON(JSONObject json)
			throws JSONException {

		JSONArray stepsJSON = json.getJSONArray("routes").getJSONObject(0)
				.getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
		ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();

		latLngArrayList.add(new LatLng(stepsJSON.getJSONObject(0)
				.getJSONObject("start_location").getDouble("lat"), stepsJSON
				.getJSONObject(0).getJSONObject("start_location")
				.getDouble("lng")));

		for (int i = 0; i < stepsJSON.length(); i++) {

			latLngArrayList.add(getEndLocation(stepsJSON.getJSONObject(i)));

		}

		return latLngArrayList;

	}

	public LatLng getEndLocation(JSONObject stepsJSON) throws JSONException {

		return new LatLng(stepsJSON.getJSONObject("end_location").getDouble(
				"lat"), stepsJSON.getJSONObject("end_location")
				.getDouble("lng"));

	}

	public void drawLineFromJSON(JSONObject json, String parking)
			throws JSONException {

		drawLine(getLatLngFormJSON(json), parking);

	}

	public void getDirectionFormGoogleMapsDirection(JSONArray json)
			throws JSONException {

		for (int i = 0; i < json.length(); i++) {

			JSONObject jsonObject = json.getJSONObject(i);

			getDirections(jsonObject.getDouble("lat_a"),
					jsonObject.getDouble("lng_a"),
					jsonObject.getDouble("lat_b"),
					jsonObject.getDouble("lng_b"),
					Boolean.toString(jsonObject.getBoolean("parking")));

		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			switch (position) {
			case 0:

				break;

			default:
				break;
			}
		}
	}

	// OnClick

	public void onReportClicked(View v) {
		
		Intent i = new Intent(getApplicationContext(), ReportActivity.class);
		startActivity(i);

	}

	public void onEditClicked(View v) {

	}

	public void onParklicked(View v) {
		
		LatLng latLng = getLocation();
		
		Intent i = new Intent(getApplicationContext(), GeoFenceActivity.class);
		i.putExtra("lat", latLng.latitude);
		i.putExtra("lng", latLng.longitude);
		
		startActivity(i);

	}
	 
	public void randomMarker(){
		
		addMarket(new LatLng(-22.986032,-43.213673), R.drawable.um);
		addMarket(new LatLng(-22.98623,-43.209232), R.drawable.um);
		addMarket(new LatLng(-22.986526,-43.205198), R.drawable.dois);
		addMarket(new LatLng(-22.98536,-43.205026), R.drawable.dois);
		addMarket(new LatLng(-22.984274,-43.205026), R.drawable.tres);
		addMarket(new LatLng(-22.983069,-43.204897), R.drawable.tres);
		addMarket(new LatLng(-22.982062,-43.204769), R.drawable.quatro);
		addMarket(new LatLng(-22.982022,-43.206871), R.drawable.quatro);
		addMarket(new LatLng(-22.981903,-43.208974), R.drawable.cinco);
		addMarket(new LatLng(-22.981765,-43.211184), R.drawable.cinco);
		addMarket(new LatLng(-22.982437,-43.209039), R.drawable.seis);
		addMarket(new LatLng(-22.982871,-43.209146), R.drawable.seis);
		addMarket(new LatLng(-22.982852,-43.211313), R.drawable.sete);
		addMarket(new LatLng(-22.982654,-43.213395), R.drawable.sete);
		addMarket(new LatLng(-22.984827,-43.213523), R.drawable.oito);
		addMarket(new LatLng(-22.984906,-43.211335), R.drawable.oito);
		addMarket(new LatLng(-22.9838,-43.211378), R.drawable.nove);
		addMarket(new LatLng(-22.984531,-43.198675), R.drawable.nove);
		addMarket(new LatLng(-22.984709,-43.196722), R.drawable.dez);
		addMarket(new LatLng(-22.983128, -43.204842), R.drawable.dez);
		
	}
	
	public void addMarket(LatLng latLng, int icon){
		
		Marker melbourne = mMap.addMarker(new MarkerOptions()
        .position(latLng)
        .title("Ipanema")
        .snippet("Obstaculo para estacionamento")
        .icon(BitmapDescriptorFactory.fromResource(icon)));
		
	}

}
