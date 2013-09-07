package br.com.hackathon.hackathon1746;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;

public abstract class ParameterResponse implements Listener {

	private Map<String, String> mMap;
	
	public ParameterResponse() {
		mMap = new HashMap<String, String>();
	}

	public void addParameter(String key, String value) {

		mMap.put(key, value);

	}

	public String getParameter(String key) {
		
		return mMap.get(key);
		
	}

}
