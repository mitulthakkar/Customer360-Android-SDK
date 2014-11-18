package cus360.inapp.base;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper.SharedPrefConstants;
import cus360.inapp.widget.ActionBarCustomPort;

public class ActivityCustomerBase extends FragmentActivity implements
		OnClickListener, ActionClickedListener {
	private View mViewBase;
	private Boolean mBoolOnPauseCalledByUs = false;

	public Boolean getOnPauseCalledByUs() {
		return mBoolOnPauseCalledByUs;
	}

	public void setOnPauseCalledByUs(Boolean mboolOnPauseCalledByUs) {
		mBoolOnPauseCalledByUs = mboolOnPauseCalledByUs;
	}

	// private Bundle mBSavedInstance = null;

	private ActionBarCustomPort mActionBar;

	public ActionBarCustomPort getmActionBar(Context context) {
		if (mActionBar == null) {
			mActionBar = new ActionBarCustomPort(context, null);
		}
		return mActionBar;
	}

	public void setmActionBar(ActionBarCustomPort mActionBar) {
		this.mActionBar = mActionBar;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUpActionBar();
		// mBSavedInstance = savedInstanceState;

		try {
			if (savedInstanceState != null) {
				String jsonC360 = savedInstanceState.getString(
						SharedPrefConstants.mStrKeyC360, null);
				if (jsonC360 != null) {
					Cus360.fromString(jsonC360);
				}
				Cus360.loadCustomisation(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUpActionBar() {
		try {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				getActionBar().hide();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mViewBase = inflater.inflate(R.layout.cus__acb_layout, null);
		View mActView = inflater.inflate(layoutResID, null);
		LinearLayout mLinearLayout = (LinearLayout) mViewBase
				.findViewById(R.id.cus_acb_ll_parent);

		mLinearLayout.addView(getmActionBar(this));

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mActView.setLayoutParams(params);
		mLinearLayout.addView(mActView);
		super.setContentView(mViewBase);
		Cus360.loadCustomisation(this);
	}

	public void setContentView(View view, Boolean addActionBar) {
		if (addActionBar) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			mViewBase = inflater.inflate(R.layout.cus__acb_layout, null);
			LinearLayout mLinearLayout = (LinearLayout) mViewBase
					.findViewById(R.id.cus_acb_ll_parent);

			// mActionBar = new ActionBarCustomPort(this, null);
			mLinearLayout.addView(getmActionBar(this));

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);
			mLinearLayout.addView(view);

			super.setContentView(mViewBase);
			Cus360.loadCustomisation(this);
		} else {
			super.setContentView(view);
		}
	}

	public void replaceContainerWithFrag(int intResIdOfContainerToReplace,
			Fragment frag, Boolean FlagAddToBackStack, String tag) {
		try {
			FragmentTransaction t = this.getSupportFragmentManager()
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
		try {
			replaceContainerWithFrag(intResIdOfContainerToReplace, frag,
					FlagAddToBackStack, "tag");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		setOnPauseCalledByUs(false);

	}

	public void navigateToActivity(Class ActivityToNavigateTo) {

		navigateToActivity(ActivityToNavigateTo, new ArrayList<Integer>());
	}

	public void navigateToActivity(Class ActivityToNavigateTo,
			ArrayList<Integer> mArrflag) {

		Intent intent = new Intent(this, ActivityToNavigateTo);
		// overridePendingTransition(R.anim.hl_anim_slide_from_right_to_left,
		// R.anim.hl_anim_slide_from_top_to_bottom);
		try {
			for (int i = 0; i < mArrflag.size(); i++) {
				intent.addFlags(mArrflag.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		startActivity(intent);
		setOnPauseCalledByUs(true);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		setOnPauseCalledByUs(true);
		super.startActivityForResult(intent, requestCode);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setOnPauseCalledByUs(false);

	}

	public void changeActivityAfterDelay(int delay,
			final Class ActivityToNavigateTo) {
		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				navigateToActivity(ActivityToNavigateTo);

				finish();
			}
		}, delay * 1000);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SharedPrefConstants.mStrKeyC360, Cus360.getInstance()
				.toString());

	}

	@Override
	protected void onPause() {
		super.onPause();
		// HomerSharedPrefrenceHelper
		// .getPref(this,
		// SharedPrefConstants.mStrSharedPrefNameStateMaintain)
		// .edit()
		// .putString(SharedPrefConstants.mStrKeyC360,
		// Cus360.getInstance().toString()).commit();
	}

	@Override
	public void doOnActionItemClicked(View v) {

	}

	/**
	 * Hides the soft keyboard
	 */
	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}
	}
}
