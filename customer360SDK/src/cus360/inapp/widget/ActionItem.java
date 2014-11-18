package cus360.inapp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cus360.inapp.R;
import cus360.inapp.base.ActivityCustomerBase;

public class ActionItem extends RelativeLayout implements OnClickListener,
		OnLongClickListener {
	private ImageView mIv_Icon;

	private Integer mIntIconResId = 0;

	public Integer getmIconResId() {
		return mIntIconResId;
	}

	public void setmIconResId(Integer mIntIconResId) {
		this.mIntIconResId = mIntIconResId;
	}

	private String mStrTittle = "";

	public String getmTittle() {
		return mStrTittle;
	}

	public void setmTittle(String mStrTittle) {
		this.mStrTittle = mStrTittle;
	}

	public ActionItem(Context context, String Tittle, int IconResId) {
		super(context);

		if (!isInEditMode()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.cus__actionitem, this);

			findViewByIds();
			setOnClickListeners();
			// setUpTheme();
			try {
				setmTittle(Tittle);
				setmIcon(IconResId);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void findViewByIds() {
		mIv_Icon = (ImageView) findViewById(R.id.cusai_iv_Attachment);
	}

	public ImageView getmIcon() {
		return mIv_Icon;
	}

	public void setmIcon(Integer mIv_Ico) {
		mIntIconResId = mIv_Ico;
		mIv_Icon.setImageResource(getmIconResId());
	}

	public void setOnClickListeners() {
		mIv_Icon.setOnClickListener(this);
		mIv_Icon.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View v) {
		if (v == mIv_Icon) {
			Toast.makeText(getContext(), getmTittle(), Toast.LENGTH_SHORT)
					.show();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mIv_Icon) {
			((ActivityCustomerBase) getContext())
					.doOnActionItemClicked(mIv_Icon);
		}
	}

}
