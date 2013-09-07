package br.com.hackathon.hackathon1746;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class BackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back);
		
		ListView list = (ListView) findViewById(R.id.listView1);
		
		CheckableAdapter adapter = new CheckableAdapter(this, getResources().getStringArray(R.array.problemas));
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.back, menu);
		return true;
	}

}
