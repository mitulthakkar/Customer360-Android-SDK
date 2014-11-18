package cus360.inapp.app.ticket.ticketlisting;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.ticket.ApiHelper;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.base.FragmentCustomerBase;
import cus360.inapp.base.HomerLibs.HomerAlertBoxUtils;
import cus360.inapp.base.HomerLibs.HomerNetworkApis;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper.SharedPrefConstants;
import cus360.inapp.base.HomerLibs.HomerUtils;

public class FragmentTicketDetailsConversation extends FragmentCustomerBase {

	private EditText mEtInput;
	private ImageView mIvSendBtn;
	ProgressDialog mPd;
	private View mVRootView;
	private LinearLayout mLlConvisHolder;
	private RelativeLayout mRlRetryView;
	private ScrollView mSv;

	private ModelTicket mModelTicket = null;

	private ArrayList<ModelDetail> mArrLisMdConvis = new ArrayList<ModelDetail>();

	public ArrayList<ModelDetail> getmArrLisMdConvis() {
		return mArrLisMdConvis;
	}

	public void setmArrLisMdConvis(ArrayList<ModelDetail> mArrLisMdConvis) {
		this.mArrLisMdConvis = mArrLisMdConvis;
	}

	public ModelTicket getmModelTicket() {

		if (mModelTicket == null) {
			String mStrModelTicket = (String) getArguments().get("ModelTicket");
			mModelTicket = new ModelTicket(mStrModelTicket);
		}
		return mModelTicket;
	}

	public void setmModelTicket(ModelTicket mModelTicket) {
		this.mModelTicket = mModelTicket;
	}

	AsyctaskFetchTicketConversations mAtFetchData;
	AsyctaskSendMessage mAtSendMessage;

	public static FragmentTicketDetailsConversation newInstance(
			String mStrModelTicket) {
		FragmentTicketDetailsConversation mFrag = new FragmentTicketDetailsConversation();
		Bundle mBundle = new Bundle();
		mBundle.putString("ModelTicket", mStrModelTicket);
		mFrag.setArguments(mBundle);
		return mFrag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.cus__atdcftdc_layout, null);
		findViewByIds(v);
		setOnClickListeners(v);
		manipulateUi(v);
		mVRootView = v;
		return v;
	}

	private void manipulateUi(View v) {
		showConviView();

		setUpConvis();

	}

	private void showRetryView() {
		mRlRetryView.setVisibility(View.VISIBLE);
	}

	private void showConviView() {
		mRlRetryView.setVisibility(View.GONE);
	}

	private void setUpConvis() {

		LayoutInflater mInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mLlConvisHolder.removeAllViews();
		int mIntForLoopSize = mArrLisMdConvis.size();
		for (int i = 0; i < mIntForLoopSize; i++) {
			ModelDetail mModelDetail = mArrLisMdConvis.get(i);
			View v = new View(getActivity());
			if (mModelDetail.getmStrSender().trim().equals("agent")) {
				v = mInflater.inflate(
						R.layout.cus__atdcftdc_element_agent_text, null);
			} else {
				v = mInflater.inflate(R.layout.cus__atdcftdc_element_my_text,
						null);
			}

			TextView mTvName = (TextView) v
					.findViewById(R.id.cus_atdcftdcemt_tv_Name);
			TextView mTvText = (TextView) v
					.findViewById(R.id.cus_atdcftdcemt_tv_Text);

			mTvName.setText(HomerUtils.capitalizeFirstLetter(mModelDetail
					.getmStrSenderName()));
			mTvText.setText(HomerUtils.capitalizeFirstLetter(mModelDetail
					.getmStrMessage()));

			mLlConvisHolder.addView(v);

			mSv.post(new Runnable() {
				@Override
				public void run() {
					// This method works but animates the scrolling
					// which looks weird on first load
					// scroll_view.fullScroll(View.FOCUS_DOWN);

					// This method works even better because there are no
					// animations.
					mSv.fullScroll(View.FOCUS_DOWN);
				}
			});
		}
	}

	private void setOnClickListeners(View v) {
		mIvSendBtn.setOnClickListener(this);
		mRlRetryView.setOnClickListener(this);
	}

	private void findViewByIds(View v) {
		mEtInput = (EditText) v.findViewById(R.id.cus_atdcftdc_et_Input);
		mIvSendBtn = (ImageView) v.findViewById(R.id.cus_atdcftdc_iv_SendBtn);
		mLlConvisHolder = (LinearLayout) v
				.findViewById(R.id.cus_atdcftdc_ll_PlaceHolderConvi);

		mRlRetryView = (RelativeLayout) v
				.findViewById(R.id.cus_atdcftdc_rl_RetryWrapper);

		mSv = (ScrollView) v.findViewById(R.id.cus_atdcftdc_sv);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == mIvSendBtn) {
			sendMessage();
		}
		if (v == mRlRetryView) {
			mAtFetchData = new AsyctaskFetchTicketConversations();
			mAtFetchData.setmBoolShowDialog(true);
			mAtFetchData.execute();
		}
	}

	private void sendMessage() {
		if (Cus360.checkIfStringIsNitherNullNorEmpty(mEtInput.getText()
				.toString().trim())) {

			ModelDetail mMdSentMsg = new ModelDetail();
			mMdSentMsg.setmStrMessage(mEtInput.getText().toString().trim());
			mMdSentMsg.setmStrSender("consumer");
			mMdSentMsg.setmStrSenderName("Sending...");
			mArrLisMdConvis.add(mMdSentMsg);

			setUpConvis();
			mAtSendMessage = new AsyctaskSendMessage();
			mAtSendMessage.setmStrMessage(mEtInput.getText().toString());
			mAtSendMessage.execute();
			mEtInput.setText("");
		}
		// else{
		// HomerAlertBoxUtils.getAlertDialogBox(getActivity(),
		// "Please fill In text before sending ").show();
		// }
	}

	@Override
	public void onResume() {
		super.onResume();

		mAtFetchData = new AsyctaskFetchTicketConversations();
		mAtFetchData.setmBoolShowDialog(true);
		mAtFetchData.execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		// if (getActivity() instanceof ActivityCustomerBase) {
		// getActivity().finish();
		// }
		mAtFetchData.cancel(true);
	}

	class AsyctaskFetchTicketConversations extends AsyncTask<Void, Void, Void> {

		private Boolean mBoolWasInternetPresent = false;
		String mStrResponse = "";

		private Boolean mBoolShowDialog = true;

		public Boolean getmBoolShowDialog() {
			return mBoolShowDialog;
		}

		public void setmBoolShowDialog(Boolean mBoolShowDialog) {
			this.mBoolShowDialog = mBoolShowDialog;
		}

		@Override
		protected void onPreExecute() {
			if (checkIfLoadingBoxRequired()) {
				mPd = ProgressDialog.show(getActivity(), "Alert",
						"Fetching Conversations...");
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (HomerNetworkApis.isOnline()) {
				mStrResponse = fetchData();
				mBoolWasInternetPresent = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			try {
				if (mPd.isShowing()) {
					mPd.dismiss();
				}
			} catch (Exception e) {
			}
			if (mBoolWasInternetPresent) {

				if (ApiHelper.checkIfFetchDataWasSuccess(mStrResponse)) {

					mArrLisMdConvis = ApiHelper.parseTicketDetail(mStrResponse);
					manipulateUi(mVRootView);
					showConviView();

				} else {
					if (!Cus360.checkIfStringIsNitherNullNorEmpty(mStrResponse))
						mStrResponse = getActivity().getResources().getString(
								R.string.cus_Error_NoInternetAvail);
					HomerAlertBoxUtils
							.getAlertDialogBox(
									getActivity(),
									ApiHelper
											.parseResponseKeyFromApiResponse(mStrResponse))
							.show();
					showRetryView();

				}
			} else {
				HomerAlertBoxUtils.getAlertDialogBox(
						getActivity(),
						getActivity().getResources().getString(
								R.string.cus_Error_NoInternetAvail)).show();
				showRetryView();
			}
		}

		public Boolean checkIfLoadingBoxRequired() {

			try {
				if (mArrLisMdConvis != null) {
					if (mArrLisMdConvis.size() > 0) {
						return false;
					}
				}
				return true;
			} catch (Exception e) {

				e.printStackTrace();
				return true;
			}
		}
	}

	class AsyctaskSendMessage extends AsyncTask<Void, Void, Void> {

		private Boolean mBoolWasInternetPresent = false;
		String mStrResponse = "";
		private String mStrMessage = "";

		public String getmStrMessage() {
			return mStrMessage;
		}

		public void setmStrMessage(String mStrMessage) {
			this.mStrMessage = mStrMessage;
		}

		@Override
		protected void onPreExecute() {
			// mPd = ProgressDialog.show(getActivity(), "Alert",
			// "Fetchign Conversations...");
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (HomerNetworkApis.isOnline()) {
				mStrResponse = ApiHelper.sendTicketDetailConversation(
						getActivity(),
						HomerSharedPrefrenceHelper.getPref(getActivity())
								.getString(
										SharedPrefConstants.mStrKeyUserEmailId,
										""), mModelTicket.getmStrId(),
						mStrMessage);
				mBoolWasInternetPresent = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			// mPd.dismiss();
			if (mBoolWasInternetPresent) {

				if (ApiHelper.checkIfFetchDataWasSuccess(mStrResponse)) {

					doAfterMessageSentSuccesfully();
				} else {
					if (!Cus360.checkIfStringIsNitherNullNorEmpty(mStrResponse))
						mStrResponse = getActivity().getResources().getString(
								R.string.cus_Error_NoInternetAvail);
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

	private void doAfterMessageSentSuccesfully() {
		mAtFetchData = new AsyctaskFetchTicketConversations();
		mAtFetchData.setmBoolShowDialog(false);
		mAtFetchData.execute();

	}

	private String fetchData() {

		return ApiHelper.fetchTicketDetailsConversations(
				getActivity(),
				HomerSharedPrefrenceHelper.getPref(getActivity()).getString(
						SharedPrefConstants.mStrKeyUserEmailId,
						SharedPrefConstants.mStrKeyUserEmailId),
				getmModelTicket().getmStrId());
	}
}
