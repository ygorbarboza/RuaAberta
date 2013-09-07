package br.com.hackathon.hackathon1746;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ReportActivity extends Activity implements Listener, ErrorListener {

	EditText mPlateEditText;
	CheckableAdapter mAdapter;
	EditText mLatLngEditText;
	Button mPictureButton;
	protected RequestQueue mRequestQueue;
	ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		mPlateEditText = (EditText) findViewById(R.id.plate_editText);
		ListView list = (ListView) findViewById(R.id.listView1);
		mLatLngEditText = (EditText) findViewById(R.id.location_editText);
		mPictureButton = (Button) findViewById(R.id.send_photo);

		mAdapter = new CheckableAdapter(this, getResources().getStringArray(
				R.array.posicao_do_estacionamento));
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(mAdapter);

		mRequestQueue = Volley.newRequestQueue(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	public void onSendPhotoClicked(View v) {

		mPictureButton.setVisibility(View.GONE);
		findViewById(R.id.photo_imageView).setVisibility(View.VISIBLE);

	}

	public void onLocationClicked(View v) {

		mLatLngEditText.setText("-22.983128, -43.204842");

	}

	public void onSendClicked(View v) {

		String url = "http://ondeeuparo.herokuapp.com/complaints.json";

		final ParamsRequest request = new ParamsRequest(Method.POST, url, this, this);

		// request.addParams(
		// "complaint",
		// "{     \"car_plate\": \"KFS 2213\",     \"category\": \"carro na calçada\",     \"lat\": \"-22.984037\",     \"lng\": \"-43.208248\" }");

		mRequestQueue.add(request);
		

//		mDialog.dismiss();
		Intent i = new Intent(this, CongratulationActivity.class);
		startActivity(i);
		finish();

//		ProgressDialog dialog = ProgressDialog.show(this, "", "Enviando...",
//				true, true, new OnCancelListener() {
//
//					@Override
//					public void onCancel(DialogInterface arg0) {
//
//						request.cancel();
//
//					}
//				});

	}

	@Override
	public void onErrorResponse(VolleyError error) {

		if (mDialog != null) {
		}

	}

	@Override
	public void onResponse(Object response) {

		if (mDialog != null) {
			mDialog.dismiss();
			Intent i = new Intent(this, CongratulationActivity.class);
			startActivity(i);
			finish();
		}

	}

}
