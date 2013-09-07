package br.com.hackathon.hackathon1746;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

public class ParamsRequest extends StringRequest {

	private Map<String, String> mParams;

	public ParamsRequest(String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(url, listener, errorListener);
		mParams = new HashMap<String, String>();

	}
	
	public ParamsRequest(int method, String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(method, url, listener, errorListener);
		mParams = new HashMap<String, String>();

	}
	
	public void addParams(String key, String value){
		
		mParams.put(key, value);
		
	}

	@Override
	public Map<String, String> getParams() {
		return mParams;
	}
	
}