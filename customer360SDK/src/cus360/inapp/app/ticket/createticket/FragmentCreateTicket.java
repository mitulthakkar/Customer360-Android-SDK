package cus360.inapp.app.ticket.createticket;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cus360.inapp.R;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.thankyou.ActivityThankYou;
import cus360.inapp.app.ticket.ApiHelper;
import cus360.inapp.app.ticket.CusBgTaskHelper;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.base.FragmentCustomerBase;
import cus360.inapp.base.HomerLibs.HomerAlertBoxUtils;
import cus360.inapp.base.HomerLibs.HomerLogger;
import cus360.inapp.base.HomerLibs.HomerNetworkApis;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper;
import cus360.inapp.base.HomerLibs.HomerSharedPrefrenceHelper.SharedPrefConstants;
import cus360.inapp.base.HomerLibs.HomerUtils;
import cus360.inapp.base.HomerLibs.HomerValidation;

public class FragmentCreateTicket extends FragmentCustomerBase {

	private Button mBtnSubmit, mBtnAttachImage;
	private EditText mEtTciketText, mEtName, mEtEmail;
	private View mVDivider;

	private CusBgTaskHelper mImageUploader;
	private LinearLayout mLlImagesPlaceHolder;
	private ArrayList<ModelPhoto> mArrLisStrImages = new ArrayList<ModelPhoto>();

	public static final int mInt_RequestCodeForOpeningPreviewActivityr = 1547;
	ProgressDialog mPdialog;
	AsyctaskCreateTicket mCreateTicket = new AsyctaskCreateTicket();

	public static FragmentCreateTicket newInstance() {
		return new FragmentCreateTicket();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.cus__actfct_layout, null);
		findViewByIds(v);
		setOnClickListeners(v);

		return v;
	}

	private void manipulateUi() {
		mImageUploader = new CusBgTaskHelper(getActivity());
		mPdialog = new ProgressDialog(getActivity());
		setUpAttachedPhotosDisplay();

		String mStrPrefEmailValue = HomerSharedPrefrenceHelper.getPref(
				getActivity()).getString(
				SharedPrefConstants.mStrKeyUserEmailId,
				SharedPrefConstants.mStrKeyUserEmailId);
		if (!mStrPrefEmailValue.equals(SharedPrefConstants.mStrKeyUserEmailId)) {
			mEtEmail.setVisibility(View.GONE);
			mEtEmail.setText(mStrPrefEmailValue);
		} else {
			mEtEmail.setVisibility(View.VISIBLE);
			mEtEmail.setText("");
		}

		if (getActivity() instanceof ActivityCustomerBase) {
			mBtnAttachImage.setVisibility(View.GONE);
		} else {
			mBtnAttachImage.setVisibility(View.VISIBLE);

		}

	}

	private void setUpAttachedPhotosDisplay() {

		mLlImagesPlaceHolder.removeAllViews();
		for (ModelPhoto p : mArrLisStrImages) {
			addPhotoToLayout(p);
		}
	}

	private void setOnClickListeners(View v) {
		mBtnAttachImage.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
	}

	private void findViewByIds(View v) {
		mEtTciketText = (EditText) v
				.findViewById(R.id.cus_actfctl_et_ticketText);
		mEtName = (EditText) v.findViewById(R.id.cus_actfctl_et_name);
		mEtEmail = (EditText) v.findViewById(R.id.cus_actfctl_et_email);
		mBtnSubmit = (Button) v.findViewById(R.id.cus_actfctl_btn_submit);
		mBtnAttachImage = (Button) v
				.findViewById(R.id.cus_actfctl_btn_AttachImage);
		mLlImagesPlaceHolder = (LinearLayout) v
				.findViewById(R.id.cus_actfctl_AttachmentplaceHolder);

		mVDivider = v.findViewById(R.id.cus_actfctl_v_Divider);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == mInt_RequestCodeForOpeningPreviewActivityr) {
			if (resultCode == getActivity().RESULT_OK) {
				String mModelPhotoJSon = data.getExtras().getString(
						ActivityPreview.mStrKeyModelPhoto);
				ModelPhoto mModelPhoto = new ModelPhoto(mModelPhotoJSon);
				AddPhoto(mModelPhoto);
			}
		}
		mCreateTicket.setmBoolStopOnPostFromExecuting(false);
	}

	public void AddPhoto(ModelPhoto mModelPhoto) {

		mArrLisStrImages.add(mModelPhoto);

		View v = addPhotoToLayout(mModelPhoto);

		if (mImageUploader == null)
			mImageUploader = new CusBgTaskHelper(getActivity());
		// mImageUploader.addToQueue(new RunnableUploadPhoto(v, mModelPhoto));
	}

	public View addPhotoToLayout(ModelPhoto mModelPhoto) {
		LayoutInflater mInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = mInflater.inflate(R.layout.cus__act_attachment_element, null);
		ImageView mIvIcon, mIvCancel;
		TextView mTvName, mTvSize;
		ProgressBar mPb;
		mIvIcon = (ImageView) v.findViewById(R.id.cus_actae_iv_thumbnail);
		mTvName = (TextView) v.findViewById(R.id.cus_actae_tv_name);
		mTvSize = (TextView) v.findViewById(R.id.cus_actae_tv_size);
		mPb = (ProgressBar) v.findViewById(R.id.cus_actae_pb_);
		mIvCancel = (ImageView) v.findViewById(R.id.cus_actae_iv_cancel);

		mPb.setVisibility(View.VISIBLE);
		mIvIcon.setVisibility(View.GONE);

		// mIvIcon.setImageURI(Uri.fromFile(new File(mModelPhoto
		// .getFilePath(getActivity()))));
		AsyctaskDoBitmapOperations mNewAsyncoperation = new AsyctaskDoBitmapOperations(
				mModelPhoto, mPb, mIvIcon);
		mNewAsyncoperation.execute();

		mTvName.setText(mModelPhoto.getmCaption());
		if (mTvName.getText().toString().trim().equals("")) {
			mModelPhoto.setmCaption("Photo-"
					+ mArrLisStrImages.indexOf(mModelPhoto));
			mTvName.setText(mModelPhoto.getmCaption());

		}
		try {
			mTvSize.setText(HomerUtils.formatFileSize(new File(mModelPhoto
					.getFilePath(getActivity())).length()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		v.setTag(mModelPhoto);
		mIvCancel.setTag(v);
		mIvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View parentView = (View) v.getTag();
				ModelPhoto mModelPhoto = (ModelPhoto) parentView.getTag();

				removePhoto(mModelPhoto, parentView);

			}
		});

		mLlImagesPlaceHolder.addView(v);
		mVDivider.setVisibility(View.VISIBLE);
		return v;
	}

	public Bitmap DoBitmapOperations(ModelPhoto mModelPhoto) {
		return HomerUtils.getBitmapFromFile(new File(mModelPhoto
				.getFilePath(getActivity())));
	}

	class AsyctaskDoBitmapOperations extends AsyncTask<Void, Void, Void> {
		ProgressBar mPb;
		ImageView mIvIcon;

		Bitmap bitmap = null;

		private Boolean mBoolInternetWasPresent = false;

		public Boolean getmBoolInternetWasPresent() {
			return mBoolInternetWasPresent;
		}

		public void setmBoolInternetWasPresent(Boolean mBoolInternetWasPresent) {
			this.mBoolInternetWasPresent = mBoolInternetWasPresent;
		}

		ModelPhoto mModelPhoto;

		public AsyctaskDoBitmapOperations(ModelPhoto mModelPhoto,
				ProgressBar pb, ImageView Iv) {

			mPb = pb;
			mIvIcon = Iv;
			this.mModelPhoto = mModelPhoto;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {

			bitmap = DoBitmapOperations(mModelPhoto);
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			try {
				mPb.setVisibility(View.GONE);
				mIvIcon.setVisibility(View.VISIBLE);

				mIvIcon.setImageBitmap(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removePhoto(ModelPhoto mModelPhoto, View v) {

		mArrLisStrImages.remove(mModelPhoto);
		mLlImagesPlaceHolder.removeView(v);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == mBtnAttachImage) {
			doOnAttachImageClicked();
		}
		if (v == mBtnSubmit) {
			if (HomerValidation.hasText(mEtTciketText)
					&& HomerValidation.hasText(mEtName)
					&& HomerValidation.hasText(mEtEmail)) {
				if (HomerValidation.isEmailAddress(mEtEmail, true)) {
					try {

						mCreateTicket = new AsyctaskCreateTicket();
						mCreateTicket.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	class AsyctaskCreateTicket extends AsyncTask<Void, Void, Void> {

		private Boolean mBoolInternetWasPresent = false;
		String mStrResponse;

		private volatile Boolean mBoolStopOnPostFromExecuting = false;

		public Boolean getmBoolInternetWasPresent() {
			return mBoolInternetWasPresent;
		}

		public void setmBoolInternetWasPresent(Boolean mBoolInternetWasPresent) {
			this.mBoolInternetWasPresent = mBoolInternetWasPresent;
		}

		public String getmStrResponse() {
			return mStrResponse;
		}

		public void setmStrResponse(String mStrResponse) {
			this.mStrResponse = mStrResponse;
		}

		public Boolean getmBoolStopOnPostFromExecuting() {
			return mBoolStopOnPostFromExecuting;
		}

		public void setmBoolStopOnPostFromExecuting(
				Boolean mBoolStopOnPostFromExecuting) {
			this.mBoolStopOnPostFromExecuting = mBoolStopOnPostFromExecuting;
		}

		@Override
		protected void onPreExecute() {
			mPdialog = ProgressDialog.show(getActivity(), "",
					"Creating your ticket...", true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (HomerNetworkApis.isOnline()) {
				mBoolInternetWasPresent = true;
				mStrResponse = ApiHelper.createTicket(getActivity(), mEtName
						.getText().toString(), mEtEmail.getText().toString(),
						mEtTciketText.getText().toString(), mArrLisStrImages);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			mPdialog.dismiss();
			if (!mBoolStopOnPostFromExecuting) {
				if (mBoolInternetWasPresent) {
					HomerLogger.d("response for createticket is ::"
							+ mStrResponse);
					openThankYouPage(mStrResponse);
					HomerSharedPrefrenceHelper
							.getPref(getActivity())
							.edit()
							.putString(SharedPrefConstants.mStrKeyUserEmailId,
									mEtEmail.getText().toString()).commit();

				} else {
					HomerAlertBoxUtils.getAlertDialogBox(
							getActivity(),
							getActivity().getResources().getString(
									R.string.cus_Error_NoInternetAvail)).show();
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (mPdialog != null) {
				if (mPdialog.isShowing()) {
					mPdialog.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doOnAttachImageClicked() {
		getActivity().startActivityForResult(
				new Intent(getActivity(), ActivityPreview.class),
				mInt_RequestCodeForOpeningPreviewActivityr);
	}

	Uri outputFileUri;

	public void doAfterPhotoUpload(View mLlRowOneView, ModelPhoto mModelPhoto) {

		ImageView mIvIcon, mIvCancel;
		TextView mTvName, mTvSize;
		ProgressBar mPb;
		mIvIcon = (ImageView) mLlRowOneView
				.findViewById(R.id.cus_actae_iv_thumbnail);
		mTvName = (TextView) mLlRowOneView.findViewById(R.id.cus_actae_tv_name);
		mTvSize = (TextView) mLlRowOneView.findViewById(R.id.cus_actae_tv_size);
		mPb = (ProgressBar) mLlRowOneView.findViewById(R.id.cus_actae_pb_);
		mIvCancel = (ImageView) mLlRowOneView
				.findViewById(R.id.cus_actae_iv_cancel);

		mPb.setVisibility(View.GONE);
		mIvIcon.setVisibility(View.VISIBLE);

		mIvIcon.setImageURI(Uri.fromFile(new File(mModelPhoto
				.getFilePath(getActivity()))));
		mModelPhoto.setmHasBeenUploaded(true);
		mTvName.setText(mModelPhoto.getmCaption());
		if (mTvName.getText().toString().trim().equals("")) {
			mTvName.setText("Photo-" + mArrLisStrImages.indexOf(mModelPhoto));
		}

	}

	public void openThankYouPage(String response) {

		JSONObject jsonOb = new JSONObject();
		String refno = "";
		Boolean success = false;
		try {
			jsonOb = new JSONObject(response);

			success = jsonOb.getBoolean("success");
			refno = jsonOb.getString("response");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (refno.trim().equals("")) {
			refno = response;
		}
		if (success) {
			HomerSharedPrefrenceHelper
					.getPref(getActivity())
					.edit()
					.putString(SharedPrefConstants.mStrKeyThankYouPageRefNo,
							refno).commit();
			getActivity().startActivity(
					new Intent(getActivity(), ActivityThankYou.class));

			if (getActivity() instanceof ActivityCustomerBase)
				getActivity().finish();
		} else {
			if (!Cus360.checkIfStringIsNitherNullNorEmpty(refno))
				refno = getActivity().getResources().getString(
						R.string.cus_Error_NoInternetAvail);
			HomerAlertBoxUtils.getAlertDialogBox(getActivity(), refno).show();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			String mStrJSonArr = (String) savedInstanceState
					.get("ArrListOfImagesToUpload");
			try {
				JSONArray jsonArr = new JSONArray(mStrJSonArr);
				mArrLisStrImages = new ArrayList<ModelPhoto>();
				for (int i = 0; i < jsonArr.length(); i++) {

					String s = (String) jsonArr.get(i);
					ModelPhoto mModelPhoto = new ModelPhoto(s);
					mArrLisStrImages.add(mModelPhoto);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mCreateTicket.setmBoolStopOnPostFromExecuting(false);

		}
		manipulateUi();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		JSONArray jsonArr = new JSONArray();

		for (ModelPhoto p : mArrLisStrImages) {
			jsonArr.put(p.toString());
		}

		outState.putString("ArrListOfImagesToUpload", jsonArr.toString());
		mCreateTicket.setmBoolStopOnPostFromExecuting(true);
	}
}

// public class RunnableUploadPhoto implements Runnable {
//
// private ModelPhoto mModelPhoto;
// private View mLlOneRowView;
//
// public RunnableUploadPhoto(View mOneRowView, ModelPhoto mModelPhotoo) {
// mLlOneRowView = mOneRowView;
// mModelPhoto = mModelPhotoo;
// }
//
// @Override
// public void run() {
// uploadTicketPhoto(
// mModelPhoto.getFilePath(mLlOneRowView.getContext()),
// mModelPhoto.getmFileName());
// Activity a = (Activity) mLlOneRowView.getContext();
//
// RunnableUpdateUiAfterPhotoUpload mRUpdateUi = new
// RunnableUpdateUiAfterPhotoUpload(
// mLlOneRowView, mModelPhoto);
// a.runOnUiThread(mRUpdateUi);
// }
//
// public String uploadTicketPhoto(String pathToImageToBeUploaded,
// String ImageName) {
// String mStr = "";
// mStr = ApiHelper.uploadTicketPhoto(pathToImageToBeUploaded,
// ImageName);
// HomerLogger.d("response from imageupload api is ::::" + mStr);
// return mStr;
// }
// }

// public class RunnableUpdateUiAfterPhotoUpload implements Runnable {
//
// private View mLlRowOneView;
// private ModelPhoto mModelPhoto;
//
// public RunnableUpdateUiAfterPhotoUpload(View mlayout,
// ModelPhoto mModelPhotoo) {
// mLlRowOneView = mlayout;
// mModelPhoto = mModelPhotoo;
// }
//
// @Override
// public void run() {
// try {
// ((FragmentCreateTicket) (getActivity()
// .getSupportFragmentManager()
// .findFragmentByTag(ActivityCreateTicket.mStrTagFragCreateTicket)))
// .doAfterPhotoUpload(mLlRowOneView, mModelPhoto);
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }