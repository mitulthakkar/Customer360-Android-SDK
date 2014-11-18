package cus360.inapp.base;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.ticket.ApiHelper;
import cus360.inapp.base.HomerLibs.HomerAlertBoxUtils;
import cus360.inapp.base.HomerLibs.HomerLogger;
import cus360.inapp.base.HomerLibs.HomerNetworkApis;

public class FragmentCustomerBase extends Fragment implements OnClickListener {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	GoogleCloudMessaging gcm = null;
	String regid = "";

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (Cus360.hasAccessTokenBeenVerifiedSuccesfully(getActivity())) {
			initGCM();
		} else {
			AsyctaskVerifyAccessToken mAtVerifyTask = new AsyctaskVerifyAccessToken();
			mAtVerifyTask.execute();
		}
	}

	public void initGCM() {
		if (Cus360.getInstance().getEnableNotifications(getActivity())) {
			// Check device for Play Services APK. If check succeeds, proceed
			// with
			// GCM registration.
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(getActivity());
				regid = getRegistrationId(getActivity());

				if (regid.trim().equals("")) {
					registerInBackground();
				}
				HomerLogger.d("Reg ID :: " + regid);
			} else {
				HomerLogger.d("No valid Google Play Services APK found.");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// if (Cus360.getInstance().getEnableNotifications(getActivity())) {
		// checkPlayServices();
		// }
	}

	public void replaceContainerWithFrag(int intResIdOfContainerToReplace,
			Fragment frag, Boolean FlagAddToBackStack, String tag) {
		try {
			FragmentTransaction t = getActivity().getSupportFragmentManager()
					.beginTransaction();
			t.replace(intResIdOfContainerToReplace, frag, tag);
			if (FlagAddToBackStack != null) {
				if (FlagAddToBackStack)
					t.addToBackStack(tag);
			}
			t.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void replaceContainerWithFrag(int intResIdOfContainerToReplace,
			Fragment frag, Boolean FlagAddToBackStack) {
		replaceContainerWithFrag(intResIdOfContainerToReplace, frag,
				FlagAddToBackStack, "tag" + intResIdOfContainerToReplace);
	}

	public static enum NavigationMode {
		Activity, Fragment
	};

	private NavigationMode mDefaultNavigationMode = NavigationMode.Activity;
	private int mIntResIdOfLayoutToBeReplacedByFrag = 0;

	public void setNavModeToFrag(int mIntResIdOfLayoutToBeReplacedByFragg) {
		mIntResIdOfLayoutToBeReplacedByFrag = mIntResIdOfLayoutToBeReplacedByFragg;
		mDefaultNavigationMode = NavigationMode.Fragment;
	}

	public void setNavModeToActivity() {
		mDefaultNavigationMode = NavigationMode.Activity;
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode,
						getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				HomerLogger.d("This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getActivity());
					}

					regid = gcm.register(Cus360.getInstance().getmStrSenderId(
							getActivity()));

					// String SENDER_ID = "570684666510";
					// // regid = gcm.register(SENDER_ID);

					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					Cus360.getInstance().setmStrGcmRegId(getActivity(), regid);
					storeRegistrationId(getActivity(), regid);
					HomerLogger.d("RegId returned from gcm server is :: "
							+ regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				HomerLogger.d(msg);
				HomerLogger.d(msg);
				HomerLogger.d(msg);
			}
		}.execute(null, null, null);
	}

	class AsyctaskVerifyAccessToken extends AsyncTask<Void, Void, Void> {

		private Boolean mBoolWasInternetPresent = false;
		String mStrResponse = "";
		ProgressDialog mPd;

		@Override
		protected void onPreExecute() {
			mPd = ProgressDialog.show(getActivity(), "Alert", "Loading ");
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (HomerNetworkApis.isOnline()) {
				mStrResponse = ApiHelper.verifyAccessToken(getActivity());
				mBoolWasInternetPresent = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			mPd.dismiss();
			if (mBoolWasInternetPresent) {

				if (ApiHelper.checkIfFetchDataWasSuccess(mStrResponse)) {

					ApiHelper.parseAcessTokenresponse(getActivity(),
							mStrResponse);
					Cus360.getInstance().setmBoolAcessTokenHasBeenChanged(
							getActivity(), false);

					initGCM();
					// manipulateUi(mVRootView);
				} else {
					HomerAlertBoxUtils
							.getAlertDialogBox(
									getActivity(),
									ApiHelper
											.parseResponseKeyFromApiResponse(mStrResponse))
							.show();
				}
			} else {
				HomerAlertBoxUtils.getAlertDialogBox(
						getActivity(),
						getActivity().getResources().getString(
								R.string.cus_Error_NoInternetAvail)).show();
			}
		}
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {

	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		HomerLogger.d("Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.trim().equals("")) {
			HomerLogger.d("Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			HomerLogger.d("App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (Exception e) {
			// should never happen
			e.printStackTrace();
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getActivity().getSharedPreferences(
				FragmentCustomerBase.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	public static void doOnNotificationRecieved() {

	}
}
