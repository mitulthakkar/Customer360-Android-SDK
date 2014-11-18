package cus360.inapp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cus360.inapp.R;

public class ActionBarCustomPort extends LinearLayout implements
		OnLongClickListener, OnClickListener {

	private RelativeLayout mRl_actionBar;
	private ImageView mIv_Up;
	private ImageView mIv_Logo;
	private TextView mTv_Tittle;

	private ImageView mIv_ActionAttachImage;
	private LinearLayout mLlHome;
	private LinearLayout mLl_contentHolder;
	private Boolean mBoolHomeAsUpEnabled = true;

	public Boolean getmHomeAsUpEnabled() {
		return mBoolHomeAsUpEnabled;
	}

	public void setmHomeAsUpEnabled(Boolean mBoolHomeAsUpEnabled) {
		this.mBoolHomeAsUpEnabled = mBoolHomeAsUpEnabled;
		setUpBackButtonIcon();
	}

	private Integer mInt_CustomColorResID = null;

	public Integer getmCustomColorResID() {
		return mInt_CustomColorResID;
	}

	public void setmCustomColorResID(Context context,
			Integer mInt_CustomColorResID) {

		this.mInt_CustomColorResID = mInt_CustomColorResID;
		if (isColorDark(context.getResources().getColor(mInt_CustomColorResID))) {
			setmCusActionBarTheme(CusActionBarTheme.DARK);
		} else {
			setmCusActionBarTheme(CusActionBarTheme.LIGHT);
		}
	}

	public String mStr_Tittle = null;

	public String getmTittleText() {
		return mStr_Tittle;
	}

	public void setmTittleText(String mStr_Tittle) {
		this.mStr_Tittle = mStr_Tittle;
	}

	public Integer mInt_LogoResid = null;

	public Integer getmLogoResid() {
		return mInt_LogoResid;
	}

	public void setmLogoResid(Integer mInt_LogoResid) {
		this.mInt_LogoResid = mInt_LogoResid;
	}

	private CusActionBarTheme mTheme = CusActionBarTheme.DARK;

	public CusActionBarTheme getmCusActionBarTheme() {
		return mTheme;
	}

	public void setmCusActionBarTheme(CusActionBarTheme mTheme) {
		this.mTheme = mTheme;
		setUpTheme();
	}

	public static enum CusActionBarTheme {
		DARK, LIGHT
	}

	public ActionBarCustomPort(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.cus__actionbar, this);
			findViewByIds();
			setUpTheme();
			setUpBackButtonIcon();
			;
			try {
				PackageManager pm = context.getPackageManager();

				ApplicationInfo ai = context.getApplicationInfo();

				CharSequence mCharseqTitle = ai.loadLabel(pm);

				setmLogo(pm.getApplicationIcon(ai));
				
				
				setmTitle(mCharseqTitle.toString());

				setOnClickListeners();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void setOnClickListeners() {
		mLlHome.setOnLongClickListener(this);
		mLlHome.setOnClickListener(this);
		// mIv_ActionAttachImage.setOnClickListener(this);
	}

	public void setUpTheme() {
		if (getmCusActionBarTheme().equals(CusActionBarTheme.DARK)) {

			if (mInt_CustomColorResID == null) {
				mRl_actionBar.setBackgroundColor(getResources().getColor(
						R.color.cus_actbar_bg_holo_dark));
			} else {
				mRl_actionBar.setBackgroundColor(getResources().getColor(
						mInt_CustomColorResID));
			}
			mTv_Tittle.setTextColor(getResources().getColor(
					R.color.cus_actbar_txtcolor_holo_dark));
			mIv_Up.setImageResource(R.drawable.cus_ic_ab_back_holo_dark);

		} else {
			if (mInt_CustomColorResID == null) {
				mRl_actionBar.setBackgroundColor(getResources().getColor(
						R.color.cus_actbar_bg_holo_light));
			} else {
				mRl_actionBar.setBackgroundColor(getResources().getColor(
						mInt_CustomColorResID));
			}
			mTv_Tittle.setTextColor(getResources().getColor(
					R.color.cus_actbar_txtcolor_holo_light));
			mIv_Up.setImageResource(R.drawable.cus_ic_ab_back_holo_light);

		}

	}

	public void setUpBackButtonIcon() {
		if (getmHomeAsUpEnabled()) {
			mIv_Up.setVisibility(View.VISIBLE);
			mLl_contentHolder.setClickable(true);
		} else {
			mIv_Up.setVisibility(View.INVISIBLE);
			mLl_contentHolder.setClickable(true);
		}

	}

	public void findViewByIds() {
		mRl_actionBar = (RelativeLayout) findViewById(R.id.cusab_rl_actionbar);
		mIv_Up = (ImageView) findViewById(R.id.cusab_iv_up);
		mIv_Logo = (ImageView) findViewById(R.id.cusab_iv_logo);
		mTv_Tittle = (TextView) findViewById(R.id.cusab_tv_tittle);
		mLlHome = (LinearLayout) findViewById(R.id.cusab_ll_actionbar_home);
		mLl_contentHolder = (LinearLayout) findViewById(R.id.cusab_ll_actionbar_contentHodler);

	}

	public void setmLogo(int resId) {
		setmLogoResid(resId);
		mIv_Logo.setImageResource(getmLogoResid());
	}

	public void setmLogo(Drawable mDrawable) {
		mIv_Logo.setImageDrawable(mDrawable);
	}

	public void setmLogo(Bitmap mBitmap) {
		mIv_Logo.setImageBitmap(mBitmap);
	}

	public void setmTitle(String title) {
		setmTittleText(title);
		mTv_Tittle.setText(getmTittleText());
	}

	public static boolean isColorDark(int residcolor) {

		double a = 1 - (0.299 * Color.red(residcolor) + 0.587
				* Color.green(residcolor) + 0.114 * Color.blue(residcolor)) / 255;
		if (a < 0.5) {
			return false; // It's a light color
		} else {
			return true; // It's a dark color
		}
	}

	public void show() {
		mRl_actionBar.setVisibility(View.VISIBLE);
	}

	public void hide() {
		mRl_actionBar.setVisibility(View.GONE);

	}

	@Override
	public boolean onLongClick(View v) {

		if (v == mLlHome) {
			try {
				Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (v == mIv_ActionAttachImage) {
			try {
				Toast.makeText(getContext(), "Attach Image", Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mLlHome) {
			try {
				((Activity) getContext()).finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Overridden to save instance state when device orientation changes. This
	 * method is called automatically if you assign an id to the RangeSeekBar
	 * widget using the {@link #setId(int)} method. Other members of this class
	 * than the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putInt("Color", getmCustomColorResID());
		bundle.putInt("Logo", getmLogoResid());
		bundle.putString("Tittle", getmTittleText());
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));

		try {
			setmCustomColorResID(getContext(), bundle.getInt("Color"));

			setmLogo(bundle.getInt("Logo"));

			setmTitle(bundle.getString("Tittle"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addActionItem(ActionItem mItem) {
		mLl_contentHolder.addView(mItem);
	}

}

// HomerLogger.d("light blue is "
// + isColorDark(context.getResources().getColor(
// R.color.lightblue)));
// HomerLogger.d("dark green is "
// + isColorDark(context.getResources().getColor(
// R.color.darkgreen)));
// HomerLogger.d("darkish grey is "
// + isColorDark(context.getResources().getColor(
// R.color.darkishgrey)));
// HomerLogger.d("purple is "
// + isColorDark(context.getResources().getColor(
// R.color.purple)));
// HomerLogger.d("black blue is "
// + isColorDark(context.getResources()
// .getColor(R.color.black)));
// HomerLogger.d("holo light is "
// + isColorDark(context.getResources().getColor(
// R.color.cus_actbar_bg_holo_light)));