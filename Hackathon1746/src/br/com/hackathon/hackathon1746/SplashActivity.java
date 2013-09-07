package br.com.hackathon.hackathon1746;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {

					sleep(3000);

					Intent i = new Intent(SplashActivity.this,
							HomeActivity.class);
					startActivity(i);
					overridePendingTransition(android.R.anim.fade_in,
							android.R.anim.fade_out);
					
					finish();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
