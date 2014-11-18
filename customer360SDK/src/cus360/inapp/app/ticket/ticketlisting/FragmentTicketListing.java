package cus360.inapp.app.ticket.ticketlisting;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.ticket.ApiHelper;
import cus360.inapp.app.ticket.createticket.ActivityCreateTicket;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.base.FragmentCustomerBase;
import cus360.inapp.base.HomerLibs.HomerAlertBoxUtils;
import cus360.inapp.base.HomerLibs.HomerNetworkApis;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper.SharedPrefConstants;
import cus360.inapp.base.HomerLibs.HomerUtils;

public class FragmentTicketListing extends FragmentCustomerBase {

	private View mVRootView;

	private ArrayList<ModelTicket> mArrLisModelTickets = new ArrayList<ModelTicket>();

	private LinearLayout mLlNoItemsFound;
	private ListView mListView;

	private ImageView mIvCreateTicket;
	private TextView mTvErrorTxt;

	private ListAdapterTickets mLaAdapter;

	public ProgressDialog mPd;

	private RelativeLayout mRlRetryWrapper;

	private TextView mTvCreateTciket;

	private String mStrEmailId = null;

	public String getmStrEmailId() {
		if (mStrEmailId == null) {
			mStrEmailId = HomerSharedPrefrenceHelper.getPref(getActivity())
					.getString(SharedPrefConstants.mStrKeyUserEmailId,
							SharedPrefConstants.mStrKeyUserEmailId);
		}

		return mStrEmailId;
	}

	public void setmStrEmailId(String mStrEmailId) {
		this.mStrEmailId = mStrEmailId;
	}

	public static FragmentTicketListing newInstance() {
		return new FragmentTicketListing();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.cus__atlftl_layout, null);
		mVRootView = v;

		findViewByIds(v);
		setOnClickListeners(v);
		manipulateUi(v);

		return v;
	}

	private void manipulateUi(View v) {

		if (!getmStrEmailId().equals(SharedPrefConstants.mStrKeyUserEmailId)) {
			if (mArrLisModelTickets.size() > 0) {
				showListOfTickets();
			} else {
				showError("Sorry No Tickets Were Found.");
			}
		} else {
			showCreateNeTicketView();
		}

		if (getActivity() instanceof ActivityCustomerBase) {
			mTvCreateTciket.setVisibility(View.GONE);
		} else {
			mTvCreateTciket.setVisibility(View.VISIBLE);
		}

	}

	public Boolean checkIfNewUser() {
		mStrEmailId = null;
		if (!getmStrEmailId().equals(SharedPrefConstants.mStrKeyUserEmailId)) {
			return false;
		} else {
			return true;
		}
	}

	public void setUpListView() {
		try {
			mLaAdapter = new ListAdapterTickets(getActivity());
			mListView.setAdapter(mLaAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setOnClickListeners(View v) {
		mLlNoItemsFound.setOnClickListener(this);
		mRlRetryWrapper.setOnClickListener(this);

		mTvCreateTciket.setOnClickListener(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				try {
					ModelTicket mModelTicket = mArrLisModelTickets.get(pos);
					if (mModelTicket.getmBoolIsConversation()) {

						Intent mIntent = new Intent();

						mIntent.setClass(getActivity(),
								ActivityTicketDetailsConversation.class);
						mIntent.putExtra("ModelTicket", mModelTicket.toString());
						mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_CLEAR_TOP);
						getActivity().startActivity(mIntent);
					} else {
						Toast.makeText(
								getActivity(),
								"There is no conversation on this ticket yet .",
								Toast.LENGTH_SHORT).show();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void showView(View v) {
		v.setVisibility(View.VISIBLE);
	}

	public void hideView(View v) {
		v.setVisibility(View.GONE);
	}

	public void hideViewButShowItsSpace(View v) {
		v.setVisibility(View.INVISIBLE);
	}

	public void showCreateNeTicketView() {
		hideView(mListView);
		showView(mLlNoItemsFound);
		showView(mIvCreateTicket);
		showView(mTvErrorTxt);
		mTvErrorTxt.setText("Create a new ticket.");
	}

	public void showRetryView() {
		mRlRetryWrapper.setVisibility(View.VISIBLE);
	}

	public void showError(String MStrError) {
		hideView(mListView);
		showView(mLlNoItemsFound);
		hideView(mIvCreateTicket);
		showView(mTvErrorTxt);
		mTvErrorTxt.setText(MStrError);
		mRlRetryWrapper.setVisibility(View.GONE);
	}

	public void showListOfTickets() {
		hideView(mLlNoItemsFound);
		showView(mListView);
		setUpListView();
		mRlRetryWrapper.setVisibility(View.GONE);
	}

	private void findViewByIds(View v) {
		mListView = (ListView) v.findViewById(R.id.cus_atlftl_lv);

		mLlNoItemsFound = (LinearLayout) v
				.findViewById(R.id.cus_atlftl_ll_NoItemsFoundWrapper);

		mTvErrorTxt = (TextView) v
				.findViewById(R.id.cus_atlftl_tv_NoItemsFoundText);
		mIvCreateTicket = (ImageView) v
				.findViewById(R.id.cus_atlftl_iv_NewTicket);
		mRlRetryWrapper = (RelativeLayout) v
				.findViewById(R.id.cus_atlftl_rl_RetryWrapper);

		mTvCreateTciket = (TextView) v
				.findViewById(R.id.cus_atlftl_tv_CreateTicket);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (!checkIfNewUser()) {
			AsyctaskFetchTickets mAtFecthDataTask = new AsyctaskFetchTickets();
			mAtFecthDataTask.execute();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v == mLlNoItemsFound) {
			doOnCreateTicketClicked();
		}

		if (v == mRlRetryWrapper) {
			onResume();
		}
		if (v == mTvCreateTciket) {
			doOnCreateTicketClicked();
		}
	}

	public void doOnCreateTicketClicked() {
		getActivity().startActivity(
				new Intent(getActivity(), ActivityCreateTicket.class));
	}

	public class ListAdapterTickets extends ArrayAdapter<String> {

		public ListAdapterTickets(Context context) {
			super(context, 0);

		}

		@Override
		public int getCount() {
			return mArrLisModelTickets.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater mLiInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = mLiInflater.inflate(
						R.layout.cus__atlftl_list_element, null);
			}

			ModelTicket mModelTicket = mArrLisModelTickets.get(position);

			ImageView mIvArrow = (ImageView) convertView
					.findViewById(R.id.cus_atlftl_iv_arrow);

			TextView mTvText = (TextView) convertView
					.findViewById(R.id.cus_atlftl_tv_text);

			if (mModelTicket.getmBoolIsConversation()) {
				mIvArrow.setVisibility(View.VISIBLE);
			} else {
				mIvArrow.setVisibility(View.GONE);
			}

			mTvText.setText(HomerUtils.capitalizeFirstLetter(mModelTicket
					.getmStrMessage()));

			return convertView;
		}
	}

	class AsyctaskFetchTickets extends AsyncTask<Void, Void, Void> {

		private Boolean mBoolWasInternetPresent = false;
		String mStrResponse = "";

		@Override
		protected void onPreExecute() {

			if (checkIfLoadingBoxRequired()) {
				mPd = ProgressDialog.show(getActivity(), "Alert",
						"Fetching Tickets...");
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
				e.printStackTrace();
			}
			if (mBoolWasInternetPresent) {

				if (ApiHelper.checkIfFetchDataWasSuccess(mStrResponse)) {

					mArrLisModelTickets = ApiHelper
							.parseListOfTickets(mStrResponse);
					manipulateUi(mVRootView);
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
				if (mArrLisModelTickets != null) {
					if (mArrLisModelTickets.size() > 0) {
						return false;
					}
				}
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				return true;
			}
		}
	}

	public String fetchData() {
		return ApiHelper.fetchListOfTcikets(getActivity(), getmStrEmailId());
	}

}
