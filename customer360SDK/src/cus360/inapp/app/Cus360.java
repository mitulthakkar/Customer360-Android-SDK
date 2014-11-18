package cus360.inapp.app;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import cus360.inapp.app.chat.ActivityChat;
import cus360.inapp.app.chat.WebAppInterface;
import cus360.inapp.app.ticket.ticketlisting.ActivityTicketListing;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.base.HomerLibs.HomerLogger;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.widget.ActionBarCustomPort;

/**
 * Cus360 is the main wrapper class that the user will interact with it is based
 * on singelton principle where the constructor of the class is hidden by
 * encapsulating it to private, ensuring that only one instance of this class
 * can ever be instantiated.
 * 
 * @author aman
 * 
 */
public class Cus360 {
	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////Singelton
	// functionality/////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * singelton instance of cus360
	 */
	private static Cus360 mInstance = new Cus360();

	private Cus360() {

	}

	public static Cus360 getInstance() {
		if (mInstance == null) {
			mInstance = new Cus360();
		}
		return mInstance;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////Singelton functionality ends
	// /////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////install functionality
	// /////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	private String mStrAcessToken = null;

	public static String getAcessToken(Context context) {

		if (getInstance().mStrAcessToken == null) {
			getInstance().mStrAcessToken = HomerSharedPrefrenceHelper.getPref(
					context).getString(mStrKeyAcessToken, "");
		}
		return getInstance().mStrAcessToken;
	}

	public static void setAcessToken(Context context, String mStrAcessToken) {

		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putString(mStrKeyAcessToken, mStrAcessToken).commit();

		getInstance().mStrAcessToken = null;
		getInstance().getAcessToken(context);
	}

	private Boolean mBoolAcessTokenHasBeenChanged = null;

	public Boolean getmBoolAcessTokenHasBeenChanged(Context context) {

		if (getInstance().mBoolAcessTokenHasBeenChanged == null) {
			getInstance().mBoolAcessTokenHasBeenChanged = HomerSharedPrefrenceHelper
					.getPref(context).getBoolean(
							mStrKeyAcessTokenHasBeenChanged, true);
		}
		return getInstance().mBoolAcessTokenHasBeenChanged;
	}

	public void setmBoolAcessTokenHasBeenChanged(Context context, Boolean bool) {
		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putBoolean(mStrKeyAcessTokenHasBeenChanged, bool).commit();
		getInstance().mBoolAcessTokenHasBeenChanged = null;
		getInstance().getmBoolAcessTokenHasBeenChanged(context);
	}

	public static void install(Context context, String AcessToken) {
		install(context, AcessToken, null);
	}

	public static void install(Context context, String AcessToken,
			HashMap<String, Object> mHmConfig) {

		if (getInstance().getAcessToken(context).equals(AcessToken)) {
			getInstance().setmBoolAcessTokenHasBeenChanged(context, false);

		} else {
			getInstance().setmBoolAcessTokenHasBeenChanged(context, true);
		}
		getInstance().setAcessToken(context, AcessToken);
		if (mHmConfig != null) {
			try {
				if (mHmConfig.containsKey(mStrKeyColor)) {
					getInstance().setThemeColor(
							(Integer) mHmConfig.get(mStrKeyColor));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (mHmConfig.containsKey(mStrKeyLogo)) {
					getInstance().setLogo((Integer) mHmConfig.get(mStrKeyLogo));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (mHmConfig.containsKey(mStrKeyTittle)) {
					getInstance().setTitle(
							(String) mHmConfig.get(mStrKeyTittle));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static Boolean hasAccessTokenBeenVerifiedSuccesfully(Context context) {
		Boolean result = false;
		if (checkIfStringIsNitherNullNorEmpty(getInstance().getAcessToken(
				context))
				&& checkIfStringIsNitherNullNorEmpty(getInstance()
						.getmStrSenderId(context))
				&& checkIfStringIsNitherNullNorEmpty(getInstance()
						.getmStrChatUrl(context))
				&& !getInstance().getmBoolAcessTokenHasBeenChanged(context)) {
			result = true;
		}

		return result;
	}

	public static Boolean checkIfStringIsNitherNullNorEmpty(String mStr) {

		try {
			return (mStr != null && !mStr.trim().equals("")) ? true : false;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}

	}

	public static Boolean checkIfInstallHasBeenCalled(Context context) {
		if (getInstance().getAcessToken(context) != null
				&& !getInstance().getAcessToken(context).trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// ///////////////////////install functionality ends
	// /////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// ///////////////////////Various Other required functionalities
	// /////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	private Boolean mBoolEnableNotifications = null;

	public Boolean getEnableNotifications(Context context) {

		if (getInstance().mBoolEnableNotifications == null) {
			getInstance().mBoolEnableNotifications = HomerSharedPrefrenceHelper
					.getPref(context).getBoolean(mStrKeyEnableNotification,
							true);
		}
		return getInstance().mBoolEnableNotifications;
	}

	public void enableNotifications(Context context) {
		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putBoolean(mStrKeyEnableNotification, true).commit();
		getInstance().mBoolEnableNotifications = null;
		getInstance().getEnableNotifications(context);
	}

	public void disableNotifications(Context context) {
		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putBoolean(mStrKeyEnableNotification, false).commit();
		getInstance().mBoolEnableNotifications = null;
		getInstance().getEnableNotifications(context);
	}

	private String mStrSenderId = null;

	public String getmStrSenderId(Context context) {

		if (getInstance().mStrSenderId == null) {
			getInstance().mStrSenderId = HomerSharedPrefrenceHelper.getPref(
					context).getString(mStrKeySenderId, "");
		}
		return getInstance().mStrSenderId;
	}

	public void setmStrSenderId(Context context, String mStrSenderId) {
		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putString(mStrKeySenderId, mStrSenderId).commit();
		getInstance().mStrSenderId = null;
		getInstance().getmStrSenderId(context);
	}

	private String mStrGcmRegId = null;

	private static String mStrChatUrl = null;

	public String getmStrGcmRegId(Context context) {
		if (getInstance().mStrGcmRegId == null) {
			getInstance().mStrGcmRegId = HomerSharedPrefrenceHelper.getPref(
					context).getString(mStrKeyRegId, "");
		}
		return getInstance().mStrGcmRegId;
	}

	public void setmStrGcmRegId(Context context, String mStrGcmRegId) {

		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putString(mStrKeyRegId, mStrGcmRegId).commit();
		getInstance().mStrGcmRegId = null;
		getInstance().getmStrGcmRegId(context);

	}

	public static String getmStrChatUrl(Context context) {

		if (getInstance().mStrChatUrl == null) {
			getInstance().mStrChatUrl = HomerSharedPrefrenceHelper.getPref(
					context).getString(mStrKeyChatUrl, "");
		}
		return getInstance().mStrChatUrl;
	}

	public static void setmStrChatUrl(Context context, String mStrChatUrl) {

		HomerSharedPrefrenceHelper.getPref(context).edit()
				.putString(mStrKeyChatUrl, mStrChatUrl).commit();
		getInstance().mStrChatUrl = null;
		getInstance().getmStrChatUrl(context);

	}

	public static WebView myWebView;

	public static Boolean checkIfWebViewIsNull() {
		return myWebView == null ? true : false;
	}

	public static WebView getMyWebView(Context context) {
		if (getInstance().myWebView == null) {
			setUpWebView(context);
		}

		return getInstance().myWebView;
	}

	public static void setMyWebView(WebView myWebView) {
		getInstance().myWebView = myWebView;
	}

	public static void setUpWebView(Context context) {

		myWebView = new WebView(context);

		LinearLayout.LayoutParams mParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		myWebView.setLayoutParams(mParams);

		WebSettings webSettings = myWebView.getSettings();

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// WebView.setWebContentsDebuggingEnabled(true);
		// }

		webSettings.setSaveFormData(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setSupportMultipleWindows(true);
		// webSettings.setLightTouchEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setSupportZoom(true);
		// webSettings.setSavePassword(true);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDatabaseEnabled(true);
		myWebView.setWebChromeClient(new WebChromeClient());
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		WebAppInterface mWebAppInterface = new WebAppInterface(context);

		myWebView.addJavascriptInterface(mWebAppInterface, "Android");
		// mWebAppInterface.addObserver(this);

		myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		if (Build.VERSION.SDK_INT >= 11)
			webSettings.setDisplayZoomControls(false);

		myWebView.loadUrl(getmStrChatUrl(context));
		HomerLogger.d("chaturl ===" + getmStrChatUrl(context));
	}

	public static String getRootCacheFolderPath(Context context) {
		File cacheDir = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = new File(
						android.os.Environment.getExternalStorageDirectory()
								+ "/Android/data/" + context.getPackageName()
								+ "/cache");
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists())
				cacheDir.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String path = "";
		try {
			path = cacheDir.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public static String getOrCreateAFolderWithName(Context context, String name) {
		File cacheDir = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = new File(
						android.os.Environment.getExternalStorageDirectory()
								+ "/Android/data/" + context.getPackageName()
								+ "/cache/" + name);
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists())
				cacheDir.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String path = "";
		try {
			path = cacheDir.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	// /////////////////////////////////////////////////////////////////////////
	// ///////////////////////Various Other required functionalities end here
	// ...
	// /////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////////customisation
	// functionality/////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	private Integer mIntThemeColor = null;

	public static Integer getThemeColor() {
		return getInstance().mIntThemeColor;
	}

	public static void setThemeColor(Integer mIntThemeColor) {
		getInstance().mIntThemeColor = mIntThemeColor;
	}

	private Integer mIntLogo = null;

	public static Integer getLogo() {
		return getInstance().mIntLogo;
	}

	public static void setLogo(Integer mIntLogo) {
		getInstance().mIntLogo = mIntLogo;
	}

	private String mStrTitle = null;

	public static String getTitle() {
		return getInstance().mStrTitle;
	}

	public static void setTitle(String mStrTitle) {
		getInstance().mStrTitle = mStrTitle;
	}

	public static void loadCustomisation(ActivityCustomerBase mAcivity) {

		ActionBarCustomPort mActionBar = mAcivity.getmActionBar(mAcivity);
		loadCustomisation(mAcivity, mActionBar);
	}

	public static void loadCustomisation(Context context,
			ActionBarCustomPort mActionBar) {
		if (getThemeColor() != null) {
			mActionBar.setmCustomColorResID(context, getThemeColor());
		}
		if (getLogo() != null) {
			mActionBar.setmLogo(getLogo());
		}
		if (getTitle() != null) {
			mActionBar.setmTitle(getTitle());
		}

	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////customisation functionality ends ////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////Launching Utilities ////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	public static void launchTickets(Activity activity) {
		activity.startActivity(new Intent(activity, ActivityTicketListing.class));
	}

	public static void launchChat(Activity activity) {
		activity.startActivity(new Intent(activity, ActivityChat.class));
	}

	// ///////////////////////////////////////////////////////////////////////////
	// ///////////////////Launching Utilities ends ////////////////////
	// //////////////////////////////////////////////////////////////////////////

	public static final String mStrKeyColor = "Color";
	public static final String mStrKeyLogo = "Logo";
	public static final String mStrKeyTittle = "Tittle";
	public static final String mStrKeyAcessToken = "AcessToken";
	public static final String mStrKeyEnableNotification = "EnableNotifications";
	private static final String mStrKeySenderId = "SenderId";
	private static final String mStrKeyRegId = "RegId";
	private static final String mStrKeyChatUrl = "ChatUrl";
	private static final String mStrKeyAcessTokenHasBeenChanged = "AccessTokenChanged";

	@Override
	public String toString() {
		super.toString();
		JSONObject mJsonOb = new JSONObject();
		try {
			mJsonOb.put(mStrKeyColor, getThemeColor());

			mJsonOb.put(mStrKeyLogo, getLogo());

			mJsonOb.put(mStrKeyTittle, getTitle());

			// mJsonOb.put(mStrKeyAcessToken, getAcessToken());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mJsonOb.toString();
	}

	public static void fromString(String json) {
		JSONObject mJsonOb = new JSONObject();
		try {
			mJsonOb = new JSONObject(json);

			try {
				if (mJsonOb.has(mStrKeyColor)) {
					getInstance().setThemeColor(mJsonOb.getInt(mStrKeyColor));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (mJsonOb.has(mStrKeyLogo)) {
					getInstance().setLogo(mJsonOb.getInt(mStrKeyLogo));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (mJsonOb.has(mStrKeyTittle)) {
					getInstance().setTitle(mJsonOb.getString(mStrKeyTittle));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// try {
			// if (mJsonOb.has(mStrKeyAcessToken)) {
			// getInstance().setAcessToken(
			// mJsonOb.getString(mStrKeyAcessToken));
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
