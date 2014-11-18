package cus360.inapp.app.chat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.base.HomerLibs.ConnectionDetector;
import cus360.inapp.base.HomerLibs.HomerLogger;

/**
 * Chat Fragment
 */
public class FragmentChat extends Fragment implements OnClickListener, Observer {

	public static ProgressDialog dialog;
	public View mVRootView;

	public LinearLayout mLlRetryWrapper;
	public TextView mTvErrorText, mTvErrorText2;
	public ImageView mIvRetry;
	private LinearLayout mLlWebViewHolder;
	public ConnectionDetector mCd;

	public FragmentChat() {
	}

	public static FragmentChat newInstance() {
		return new FragmentChat();
	}

	private String mStrHtml = "";

	// private String mStrUrl =
	// "http://iconia.c360dev.in/c360qa/iframe/chat/product_id/2981";
	// private String mStrUrl = "http://google.com";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cus__ac_frch_layout,
				container, false);

		findViewByIds(rootView);

		setOnClickListeners(rootView);

		mVRootView = rootView;

		return mVRootView;
	}

	public void setOnClickListeners(View rootView) {
		mIvRetry.setOnClickListener(this);
	}

	public void findViewByIds(View rootView) {
		// myWebView = (WebView) rootView.findViewById(R.id.webview);
		mLlWebViewHolder = (LinearLayout) rootView
				.findViewById(R.id.cus_acfcl_ll_PlaceHolder);
		mTvErrorText = (TextView) rootView
				.findViewById(R.id.cus_acfcl_tv_ErrorTxt);
		mTvErrorText2 = (TextView) rootView
				.findViewById(R.id.cus_acfcl_tv_ErrorTxt2);

		mIvRetry = (ImageView) rootView.findViewById(R.id.cus_acfcl_iv_retry);
		mLlRetryWrapper = (LinearLayout) rootView
				.findViewById(R.id.cus_acfcl_ll_RetryWapper1);
	}

	public void init(View v) {

		mCd = new ConnectionDetector(getActivity());
		if (mCd.isConnectingToInternet()) {
			setUpWebView();
		} else {

			// HomerAlertBoxUtils
			// .getAlertDialogBox(getActivity(),
			// "Please check your internet connection , please try again.")
			// .show();

			showRetryView(
					"No Internet Connection ",
					"Please check your internet connection , and click on the above refresh icon to  try again.");

		}
	}

	public void showRetryView(String ErrorTxt, String ErrorTxt2) {
		mLlWebViewHolder.setVisibility(View.GONE);
		mLlRetryWrapper.setVisibility(View.VISIBLE);
		if (mTvErrorText != null) {
			mTvErrorText.setText(ErrorTxt);
		}
		if (mTvErrorText2 != null) {
			mTvErrorText2.setText(ErrorTxt2);
		}
	}

	public void hideRetryView() {
		mLlRetryWrapper.setVisibility(View.GONE);
		mLlWebViewHolder.setVisibility(View.VISIBLE);

	}

	public void setUpWebView() {
		if (Cus360.checkIfWebViewIsNull()) {
			dialog = ProgressDialog.show(getActivity(), "", "Lodaing", true,
					true, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
						}
					});
		}
		mLlWebViewHolder.setVisibility(View.VISIBLE);

		mLlWebViewHolder.addView(Cus360.getMyWebView(getActivity()));

		// this.webView.loadUrl(this.url);
		// myWebView.setWebViewClient(new WebViewClient());

		// did not work properly
		// myWebView.setPictureListener(new PictureListener() {
		//
		// @Override
		// public void onNewPicture(WebView view, Picture picture) {
		// if (dialog != null && dialog.isShowing())
		// dialog.dismiss();
		// }
		// });

		// did not work properly
		// myWebView.setWebChromeClient(new WebChromeClient() {
		//
		// @Override
		// public void onProgressChanged(WebView view, int newProgress) {
		// super.onProgressChanged(view, newProgress);
		//
		// if (newProgress < 100) {
		// if (dialog == null || !dialog.isShowing())
		// dialog = ProgressDialog.show(getActivity(), "",
		// "Lodaing", true, true,
		// new OnCancelListener() {
		//
		// @Override
		// public void onCancel(
		// DialogInterface dialog) {
		// }
		// });
		// } else {
		// try {
		// dialog.dismiss();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// });n,

		// did not work properly
		Cus360.getMyWebView(getActivity()).setWebViewClient(
				new WebViewClient() {
					private int webViewPreviousState;
					private final int PAGE_STARTED = 0x1;
					private final int PAGE_REDIRECTED = 0x2;

					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String urlNewString) {
						webViewPreviousState = PAGE_REDIRECTED;
						Cus360.getMyWebView(getActivity())
								.loadUrl(urlNewString);
						return true;
					}

					@Override
					public void onPageStarted(WebView view, String url,
							Bitmap favicon) {
						super.onPageStarted(view, url, favicon);
						webViewPreviousState = PAGE_STARTED;
						if (dialog == null || !dialog.isShowing())
							dialog = ProgressDialog.show(getActivity(), "",
									"Lodaing", true, true,
									new OnCancelListener() {

										@Override
										public void onCancel(
												DialogInterface dialog) {
										}
									});
					}

					@Override
					public void onPageFinished(WebView view, String url) {

						try {
							if (webViewPreviousState == PAGE_STARTED) {
								dialog.dismiss();
								dialog = null;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		// myWebView
		// .loadUrl("http://iconia.c360dev.in/c360qa/iframe/chat/product_id/2981");

		// myWebView.loadUrl("http://google.com");
		// showDialogBoxIFRequired();
	}

	// public void showDialogBoxIFRequired() {
	// final Handler mHandler = new Handler();
	// // in onCreate
	// mHandler.postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// if (myWebView.getContentHeight() > 0) {
	// if (dialog != null && dialog.isShowing()) {
	// dialog.dismiss();
	// }
	// mHandler.removeCallbacks(this);
	// } else {
	//
	// if (dialog == null || !dialog.isShowing())
	// dialog = ProgressDialog.show(getActivity(), "",
	// "Lodaing", true, true,
	// new OnCancelListener() {
	//
	// @Override
	// public void onCancel(
	// DialogInterface dialog) {
	// }
	// });
	//
	// mHandler.postDelayed(this, 100);
	// }
	// }
	// }, 100);
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// myWebView.destroy();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			try {
				Cus360.getMyWebView(getActivity()).goBack();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble
		// up
		// to the default
		// system behavior (probably exit the activity)
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();

		init(mVRootView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// if (webViewBundle != null) {
		// // myWebView.loadDataWithBaseURL(mStrUrl,
		// // webViewBundle.getString("html"), "text/html", "UTF-8",
		// // mStrUrl);
		//
		// String html = "initiaed";
		// try {
		// html = readFile(Cus360.getOrCreateAFolderWithName(
		// getActivity(), "webCache") + "/chatCache.mht");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// String FileURl = "file:///"
		// + Cus360.getOrCreateAFolderWithName(getActivity(),
		// "webCache") + "/chatCache.mht";
		//
		// myWebView.loadDataWithBaseURL(FileURl, html, "text/html",
		// "UTF-8", null);
		// //
		// // myWebView.loadUrl(
		// // "file:///"
		// // + Cus360.getOrCreateAFolderWithName(
		// // getActivity(), "webCache")+
		// // "/chatCache.mht" );
		//
		// } else {
		// init(mVRootView);
		// }
	}

	String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			HomerLogger.d("mht file content is ::" + sb.toString());

			return sb.toString();
		} finally {
			br.close();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// // myWebView.saveState(outState);
		// webViewBundle = new Bundle();
		// myWebView.saveState(webViewBundle);
		//
		// // webViewBundle.putString("html", mStrHtml);
		//
		// if ((Build.VERSION.SDK_INT >= 11)) {
		// myWebView.saveWebArchive(new File(Cus360
		// .getOrCreateAFolderWithName(getActivity(), "webCache"),
		// "/chatCache.mht").getAbsolutePath());
		// // this.setRetainInstance(true);
		// }
	}

	@Override
	public void onPause() {
		super.onPause();

		// webViewBundle = new Bundle();
		// myWebView.saveState(webViewBundle);
		//
		// // webViewBundle.putString("html", mStrHtml);
		//
		// myWebView.saveWebArchive(new File(Cus360
		// .getOrCreateAFolderWithName(getActivity(), "webCache"),
		// "/chatCache.mht").getAbsolutePath());
		// this.setRetainInstance(true);
		mLlWebViewHolder.removeAllViews();
	}

	@Override
	public void onClick(View v) {
		if (v == mIvRetry) {
			init(mVRootView);
		}
	}

	@Override
	public void update(Observable observable, Object observation) {

		// Got full page source.
		if (observable instanceof WebAppInterface) {
			mStrHtml = (String) observation;

			HomerLogger.d("onupdate html is ::" + mStrHtml);
			// onHtmlChanged();
		}
	}
}