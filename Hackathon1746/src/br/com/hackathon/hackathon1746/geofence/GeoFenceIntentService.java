package br.com.hackathon.hackathon1746.geofence;

import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import br.com.hackathon.hackathon1746.BackActivity;
import br.com.hackathon.hackathon1746.R;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

public class GeoFenceIntentService extends IntentService {

	private boolean isOffArea = true;

	public GeoFenceIntentService() {

		super("TesteIntentService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (LocationClient.hasError(intent)) {

			int errorCode = LocationClient.getErrorCode(intent);

			Log.e("ReceiveTransitionsIntentService",
					"Location Services error: " + Integer.toString(errorCode));

		} else {

			int transitionType = LocationClient.getGeofenceTransition(intent);
			if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER
					|| transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
				List<Geofence> geofences = LocationClient
						.getTriggeringGeofences(intent);
				String[] geofenceIds = new String[geofences.size()];
				for (int i = 0; i < geofences.size(); i++) {
					geofenceIds[i] = geofences.get(i).getRequestId();
				}

				// String transition = ((transitionType ==
				// Geofence.GEOFENCE_TRANSITION_ENTER) ? "you are in"
				// : "you are out");

				verifyTransitionType(transitionType);

				// sendNotification(transition, ids);

				// NotificationManager mNotificationManager =
				// (NotificationManager)
				// getSystemService(Context.NOTIFICATION_SERVICE);

			}

		}
	}

	public void verifyTransitionType(int transitionType) {

		if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER && isOffArea) {

			sendNotification("Você está de volta?", "Como está seu carro?");
			stopSelf();
			
		}else{
			if(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
				isOffArea = true;
			}
		}

	}

	private void sendNotification(String title, String text) {

		// Create an explicit content Intent that starts the main Activity
		Intent notificationIntent = new Intent(getApplicationContext(),
				BackActivity.class);

		// Construct a task stack
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the main Activity to the task stack as the parent
		stackBuilder.addParentStack(GeoFenceActivity.class);

		// Push the content Intent onto the stack
		stackBuilder.addNextIntent(notificationIntent);

		// Get a PendingIntent containing the entire back stack
		PendingIntent notificationPendingIntent = stackBuilder
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get a notification builder that's compatible with platform versions
		// >= 4
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		// Set the notification contents
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
				.setContentText(text)
				.setContentIntent(notificationPendingIntent);

		// Get an instance of the Notification manager
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = builder.build();
		
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		
		// Issue the notification
		mNotificationManager.notify(0, notification);
	}

}