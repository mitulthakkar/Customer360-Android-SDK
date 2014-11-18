/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cus360.inapp.base.gcm;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cus360.inapp.R;
import cus360.inapp.app.ticket.ticketlisting.ActivityTicketDetailsConversation;
import cus360.inapp.app.ticket.ticketlisting.ModelTicket;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work.
				// for (int i = 0; i < 5; i++) {
				// Log.i(TAG,
				// "Working... " + (i + 1) + "/5 @ "
				// + SystemClock.elapsedRealtime());
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e) {
				// }
				// }
				// Log.i(TAG, "Completed work @ " +
				// SystemClock.elapsedRealtime());

				// Post notification of received message.

				// generateNotification(this, "Received: " + extras.toString());

				String mStrData = extras.getString("data");
				sendNotification(mStrData);

				Log.i(TAG, "Received: " + mStrData);
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {

			JSONObject mJsonObject = new JSONObject(msg);

			String id = mJsonObject.getString("ticketId");
			String Message = mJsonObject.getString("message");

			ModelTicket mModelTicket = new ModelTicket();
			mModelTicket.setmStrId(id);
			mModelTicket.setmBoolIsConversation(true);

			Intent intent = new Intent(this,
					ActivityTicketDetailsConversation.class);
			intent.putExtra("ModelTicket", mModelTicket.toString());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			PackageManager pm = this.getPackageManager();

			ApplicationInfo ai = this.getApplicationInfo();

			CharSequence mCharseqTitle = ai.loadLabel(pm);

			Drawable d = pm.getApplicationIcon(ai);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setLargeIcon(drawableToBitmap(d))
					.setSmallIcon(R.drawable.cus_action_ticket_plus_dark)
					.setContentTitle(mCharseqTitle.toString())
					// .setSound(Notification.DEFAULT_SOUND)
					.setStyle(
							new NotificationCompat.BigTextStyle()
									.bigText(Message)).setContentText(Message);

			Uri alarmSound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);

			mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

			mBuilder.setLights(Color.WHITE, 3000, 3000);

			mBuilder.setAutoCancel(true);

			mBuilder.setTicker("Our agent has just replied to your ticket ");

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	// /**
	// * Old Deprecated code
	// * Issues a notification to inform the user that server has sent a
	// message.
	// */
	// private static void generateNotification(Context context, String message)
	// {
	//
	// try {
	//
	// // notification.sound = Uri.parse("android.resource://" +
	// // context.getPackageName() + "your_sound_file_name.mp3");
	//
	// Boolean mMessageNotNullOrEmpty = true;
	// if (message == null || message.trim().equals("")) {
	// mMessageNotNullOrEmpty = false;
	// } else {
	// mMessageNotNullOrEmpty = true;
	// }
	//
	// if (Cus360.getInstance().getEnableNotifications(context)
	// && mMessageNotNullOrEmpty) {
	//
	// PackageManager pm = context.getPackageManager();
	//
	// ApplicationInfo ai = context.getApplicationInfo();
	//
	// CharSequence mCharseqTitle = ai.loadLabel(pm);
	//
	// Drawable d = pm.getApplicationIcon(ai);
	//
	// int icon = R.drawable.ic_launcher;
	// long when = System.currentTimeMillis();
	// NotificationManager notificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// String title = context.getString(R.string.app_name);
	// Intent notificationIntent = new Intent(context,
	// ActivityTicketDetailsConversation.class);
	// // set intent so it does not start a new activity
	// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// PendingIntent intent = PendingIntent.getActivity(context, 0,
	// notificationIntent, 0);
	// // JSONObject jsonOb = new JSONObject();
	// // String feed_id = "", submenu_subcat_id = "", tittle = "";
	// // try {
	// // jsonOb = new JSONObject(message);
	// // feed_id = jsonOb.getString("feed_id");
	// // submenu_subcat_id = jsonOb.getString("feed_submenu_subcat");
	// // tittle = jsonOb.getString("feed_title");
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// // ModelFeed mModelFeed = new ModelFeed();
	// // mModelFeed.setmStrId(feed_id);
	// // SharedPrefrenceHelper
	// // .getPref(context)
	// // .edit()
	// // .putString(
	// // AbstractSharedPrefConstants.mStrKeyDetailsPageCategory,
	// // Categotries.getCategoryForId(submenu_subcat_id)
	// // .toString()).commit();
	// // SharedPrefrenceHelper
	// // .getPref(context)
	// // .edit()
	// // .putString(
	// // AbstractSharedPrefConstants.mStrKeyDetailsPageFeed,
	// // mModelFeed.toString()).commit();
	// Notification notification = new Notification(icon, title, when);
	// notification.largeIcon = drawableToBitmap(d);
	// notification.setLatestEventInfo(context, title, title, intent);
	// notification.flags |= Notification.FLAG_AUTO_CANCEL;
	// // Play default notification sound
	// notification.defaults |= Notification.DEFAULT_SOUND;
	// // Vibrate if vibrate is enabled
	// notification.defaults |= Notification.DEFAULT_VIBRATE;
	// notificationManager.notify(0, notification);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

}
