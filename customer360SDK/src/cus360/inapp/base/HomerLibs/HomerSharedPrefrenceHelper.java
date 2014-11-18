package cus360.inapp.base.HomerLibs;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

public class HomerSharedPrefrenceHelper {
	public static final String mStrPrefName = "HomerSharedPrefDefaultCustomer360";

	private static HashMap<String, SharedPreferences> mPrefMap = new HashMap<String, SharedPreferences>();

	public static SharedPreferences getPref(Context context) {

		return getPref(context, mStrPrefName);
	}

	public static SharedPreferences getPref(Context context, String PrefName) {
		SharedPreferences mPref = null;

		// if null is passed as preff name handle it ...
		if (PrefName == null) {
			PrefName = mStrPrefName;
		}

		// if map has been already fetched then retrive it from local cache
		if (mPrefMap != null) {
			try {
				mPref = mPrefMap.get(PrefName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// if map not present in local cache then retrive it from system
			if (mPref == null) {
				mPref = context.getSharedPreferences(PrefName,
						Context.MODE_PRIVATE);
				mPrefMap.put(PrefName, mPref);
			}
		} else {
			HomerLogger.e("ERROR WHILE FETCHING SHAREDPREF MAP IS NULL");
		}
		return mPrefMap.get(PrefName);
	}

	public static class SharedPrefConstants {

		public static final String mStrSharedPrefNameStateMaintain = "savedInstance";
		public static final String mStrKeyC360 = "c360";

		public static final String mStrKeyThankYouPageRefNo = "ThankyouPageRefNo";
		public static final String mStrThankyYouPageHelpDeskUrl = "ThankyouPageHelpDeskUrl";

		public static final String mStrKeyUserEmailId = "UserEmailId";

	}
}