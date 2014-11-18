package cus360.inapp.app.ticket.createticket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cus360.inapp.R;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.base.HomerLibs.HomerAlertBoxUtils;
import cus360.inapp.base.HomerLibs.HomerLogger;
import cus360.inapp.base.HomerLibs.HomerNetworkApis;
import cus360.inapp.base.HomerLibs.HomerUtils;

public class ActivityPreview extends ActivityCustomerBase {

	private ImageView mIvPreview;
	private EditText mEtCaption;
	private ImageButton mBtnAttach, mBtnCancel, mBtnSelectImage;
	private ProgressBar mPb;

	private Boolean mBoolPicHasBeenSelectedSuccesfully = false;
	private Boolean mBoolCameraHasBeenOpened = false;

	private String mStrKeyBoolPicSavedAlready = "picAlreadySaved";
	private String mStrKeyBoolCameraHasBeenOpened = "picAlreadySaved";
	public static final String mStrKeyModelPhoto = "mModelPhoto";

	private ModelPhoto mModelPhoto = new ModelPhoto();

	public static final int mInt_RequestCodeForOpeningIntentChooser = 1347;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cus__ap_layout);
		hideSoftKeyboard();
		if (savedInstanceState != null) {

			mBoolPicHasBeenSelectedSuccesfully = savedInstanceState
					.getBoolean(mStrKeyBoolPicSavedAlready);
			mBoolCameraHasBeenOpened = savedInstanceState
					.getBoolean(mStrKeyBoolCameraHasBeenOpened);
			mModelPhoto = new ModelPhoto(
					savedInstanceState.getString(mStrKeyModelPhoto));

		}
		findViewByIds();
		setOnClickListeners();
		manipulateUi();
		if (!mBoolCameraHasBeenOpened) {
			openImageIntent();
		}
	}

	private void findViewByIds() {
		mIvPreview = (ImageView) findViewById(R.id.cus_apl_iv_Preview);
		mEtCaption = (EditText) findViewById(R.id.cus_apl_et_Caption);
		mBtnAttach = (ImageButton) findViewById(R.id.cus_apl_btn_Attach);
		mBtnCancel = (ImageButton) findViewById(R.id.cus_apl_btn_Cancel);
		mBtnSelectImage = (ImageButton) findViewById(R.id.cus_apl_btn_SelectImage);
		;
		mPb = (ProgressBar) findViewById(R.id.cus_apl_pb);
	}

	private void setOnClickListeners() {
		mBtnAttach.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnSelectImage.setOnClickListener(this);
	}

	private void manipulateUi() {
		mPb.setVisibility(View.GONE);
		setUpPriviewImage();
	}

	private void setUpPriviewImage() {
		try {
			mIvPreview.setImageURI(Uri.fromFile(new File(mModelPhoto
					.getFilePath(this))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Uri outputFileUri;

	private void openImageIntent() {

		mBoolCameraHasBeenOpened = true;

		final File sdImageMainDirectory = new File(
				mModelPhoto.getCameraFilePath(this));
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
				captureIntent, 0);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			// intent.setType("image/*");
			// intent.putExtra("outputX", HomerUtils.getPxValueforDp(this,
			// 150));
			// intent.putExtra("outputY", HomerUtils.getPxValueforDp(this,
			// 150));
			// intent.putExtra("aspectX", 1);
			// intent.putExtra("aspectY", 1);
			// intent.putExtra("scale", true);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_PICK);

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(galleryIntent,
				"Select Source");

		// Add the camera options.
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				cameraIntents.toArray(new Parcelable[] {}));

		startActivityForResult(chooserIntent,
				mInt_RequestCodeForOpeningIntentChooser);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			mPb.setVisibility(View.VISIBLE);
			if (requestCode == mInt_RequestCodeForOpeningIntentChooser) {
				final boolean isCamera;
				if (data == null) {
					isCamera = true;
				} else {
					final String action = data.getAction();
					if (action == null) {
						isCamera = false;
					} else {
						isCamera = action
								.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					}
				}
				AsyctaskDoBitmapOperations mNewAsyncTask = new AsyctaskDoBitmapOperations(
						isCamera, data);
				mNewAsyncTask.execute();
			}
		} else {
			mPb.setVisibility(View.GONE);
		}
	}

	public Bitmap DoBitmapOperations(Boolean isCamera, Intent data) {
		Uri selectedImageUri = null;
		Bitmap bitmap = null;
		if (isCamera) {

			try {
				// selectedImageUri = Uri.fromFile(new File(mModelPhoto
				// .getCameraFilePath(this)));
				// bitmap = (Bitmap) data.getExtras().get("data");

				bitmap = HomerUtils.getBitmapFromFile(new File(mModelPhoto
						.getCameraFilePath(this)));

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				selectedImageUri = data == null ? null : data.getData();

				bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), selectedImageUri);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long size = sizeOf(bitmap);

		// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
		long fileSizeInKB = size / 1024;
		// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
		long fileSizeInMB = fileSizeInKB / 1024;
		int compressionQuality = 80;
		if (fileSizeInMB > 1) {
			compressionQuality = 70;
		}
		if (fileSizeInMB > 2) {
			compressionQuality = 50;
		}
		if (fileSizeInMB > 3) {
			compressionQuality = 30;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, stream);

		try {
			File file = new File(mModelPhoto.getFilePath(this));
			file.createNewFile();
			OutputStream out = new FileOutputStream(file);

			out.write(stream.toByteArray());
			out.close();

			if (fileSizeInMB > 1) {
				bitmap = HomerUtils.getBitmapFromFile(new File(mModelPhoto
						.getFilePath(this)));
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		mBoolPicHasBeenSelectedSuccesfully = true;
		return bitmap;
	}

	class AsyctaskDoBitmapOperations extends AsyncTask<Void, Void, Void> {

		Bitmap bitmap = null;

		private Boolean mBoolInternetWasPresent = false;

		public Boolean getmBoolInternetWasPresent() {
			return mBoolInternetWasPresent;
		}

		public void setmBoolInternetWasPresent(Boolean mBoolInternetWasPresent) {
			this.mBoolInternetWasPresent = mBoolInternetWasPresent;
		}

		private Boolean mBoolIsCamera = false;
		Intent data;

		public AsyctaskDoBitmapOperations(Boolean isCamera, Intent data) {
			mBoolIsCamera = isCamera;
			this.data = data;
		}

		@Override
		protected void onPreExecute() {
			mPb.setVisibility(View.VISIBLE);
			mIvPreview.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {

			// if (HomerNetworkApis.isOnline()) {
			// mBoolInternetWasPresent = true;
			//
			// }
			bitmap = DoBitmapOperations(mBoolIsCamera, data);
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			try {

				mIvPreview.setImageBitmap(bitmap);
				mPb.setVisibility(View.GONE);
				mIvPreview.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected int sizeOf(Bitmap data) {
		return data.getRowBytes() * data.getHeight();
	}

	void savefile(URI sourceuri) {
		String sourceFilename = sourceuri.getPath();
		String destinationFilename = android.os.Environment
				.getExternalStorageDirectory().getPath()
				+ File.separatorChar
				+ "abc.mp3";

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(sourceFilename));
			bos = new BufferedOutputStream(new FileOutputStream(
					destinationFilename, false));
			byte[] buf = new byte[1024];
			bis.read(buf);
			do {
				bos.write(buf);
			} while (bis.read(buf) != -1);
		} catch (IOException e) {

		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {

			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v == mBtnAttach) {
			if (mBoolPicHasBeenSelectedSuccesfully) {
				Intent intent = new Intent();
				mModelPhoto.setmCaption(mEtCaption.getText().toString());
				intent.putExtra(mStrKeyModelPhoto, mModelPhoto.toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			} else {
				HomerAlertBoxUtils.getAlertDialogBox(this,
						"Please Select An Image First. ").show();
			}
		}
		if (v == mBtnSelectImage) {
			openImageIntent();
		}
		if (v == mBtnCancel) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(mStrKeyBoolPicSavedAlready,
				mBoolPicHasBeenSelectedSuccesfully);

		outState.putBoolean(mStrKeyBoolCameraHasBeenOpened,
				mBoolCameraHasBeenOpened);

		outState.putString(mStrKeyModelPhoto, mModelPhoto.toString());
	}

}
