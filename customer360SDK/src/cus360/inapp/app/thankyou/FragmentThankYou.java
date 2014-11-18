package cus360.inapp.app.thankyou;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cus360.inapp.R;
import cus360.inapp.app.ticket.createticket.ActivityCreateTicket;
import cus360.inapp.base.FragmentCustomerBase;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper.SharedPrefConstants;

public class FragmentThankYou extends FragmentCustomerBase {

	private String mStrTicketRef = "";
	private String mStrHelpDeskUrl = "";

	// public String getmStrTicketRef() {
	// if (mStrTicketRef == null || mStrTicketRef.trim().equals("")) {
	// mStrTicketRef = getArguments().getString("mStrTicketRef");
	// }
	// return mStrTicketRef;
	// }
	public String getmStrTicketRef() {
		if (mStrTicketRef == null || mStrTicketRef.trim().equals("")) {
			mStrTicketRef = HomerSharedPrefrenceHelper
					.getPref(getActivity())
					.getString(SharedPrefConstants.mStrKeyThankYouPageRefNo, "");
		}
		return mStrTicketRef;
	}

	public void setmStrTicketRef(String mStrTicketRef) {
		this.mStrTicketRef = mStrTicketRef;
	}

	// public String getmStrHelpDeskUrl() {
	// if (mStrHelpDeskUrl == null || mStrHelpDeskUrl.trim().equals("")) {
	// mStrHelpDeskUrl = getArguments().getString("mStrHelpDeskUrl");
	// }
	// return mStrHelpDeskUrl;
	// }

	public String getmStrHelpDeskUrl() {
		if (mStrHelpDeskUrl == null || mStrHelpDeskUrl.trim().equals("")) {
			mStrHelpDeskUrl = HomerSharedPrefrenceHelper.getPref(getActivity())
					.getString(
							SharedPrefConstants.mStrThankyYouPageHelpDeskUrl,
							"");
		}
		return mStrHelpDeskUrl;
	}

	public void setmStrHelpDeskUrl(String mStrHelpDeskUrl) {
		this.mStrHelpDeskUrl = mStrHelpDeskUrl;
	}

	// public static FragmentThankYou newInstance(String mStrTicketRef,
	// String mStrHelpDeskUrl) {
	//
	// FragmentThankYou mFrag = new FragmentThankYou();
	// Bundle bundle = new Bundle();
	// bundle.putString("mStrTicketRef", mStrTicketRef);
	// bundle.putString("mStrHelpDeskUrl", mStrHelpDeskUrl);
	// mFrag.setArguments(bundle);
	//
	// return mFrag;
	// }

	public static FragmentThankYou newInstance() {

		FragmentThankYou mFrag = new FragmentThankYou();
		return mFrag;
	}

	private TextView mTvRefNo;
	private TextView mTvHelpDeskUrl;
	private Button mBtnClose;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.cus__fragthankyou_layout, null);

		findViewByIds(v);
		setOnClickListeners(v);
		manipulateUi(v);
		return v;
	}

	private void manipulateUi(View v) {
		mTvRefNo.setText("Your ticket refrence  is : " + getmStrTicketRef());
		mTvHelpDeskUrl.setText(Html.fromHtml(getmStrHelpDeskUrl()));
		mTvHelpDeskUrl.setVisibility(View.GONE);
	}

	private void setOnClickListeners(View v) {
		mBtnClose.setOnClickListener(this);
	}

	private void findViewByIds(View v) {
		mTvRefNo = (TextView) v.findViewById(R.id.cus_ftyl_tv_RefText);
		mTvHelpDeskUrl = (TextView) v
				.findViewById(R.id.cus_ftyl_tv_HelpDeskText);

		mTvHelpDeskUrl.setClickable(true);
		mTvHelpDeskUrl.setMovementMethod(LinkMovementMethod.getInstance());

		mBtnClose = (Button) v.findViewById(R.id.cus_ftyl_btn_Close);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v == mBtnClose) {
			getActivity().onBackPressed();
		}
	}

}
