package cus360.inapp.app.ticket.createticket;

import java.io.File;

import org.json.JSONObject;

import android.content.Context;
import cus360.inapp.app.Cus360;
import cus360.inapp.base.HomerLibs.HomerUtils;

public class ModelPhoto {

	private String mStrFileName = "";
	private String mStrApiRefName = "";
	private String mStrCaption = "";
	private Boolean mBoolHasBeenUploaded = false;

	public ModelPhoto() {

		setmFileName(HomerUtils.generateUniqueFileName());
	}

	public ModelPhoto(String mJson) {
		if (mJson != null) {
			fromString(mJson);
		}

	}

	public String getmFileName() {
		return mStrFileName;
	}

	private void setmFileName(String mStrFileName) {
		this.mStrFileName = mStrFileName;
	}

	public String getmApiRefName() {
		return mStrApiRefName;
	}

	public void setmApiRefName(String mStrApiRefName) {
		this.mStrApiRefName = mStrApiRefName;
	}

	public String getmCaption() {
		return mStrCaption;
	}

	public void setmCaption(String mStrCaption) {
		this.mStrCaption = mStrCaption;
	}

	public Boolean getmHasBeenUploaded() {
		return mBoolHasBeenUploaded;
	}

	public void setmHasBeenUploaded(Boolean mBoolHasBeenUploaded) {
		this.mBoolHasBeenUploaded = mBoolHasBeenUploaded;
	}

	public String getFilePath(Context context) {
		String filepath = "";
		try {
			filepath = Cus360.getRootCacheFolderPath(context) + "/"
					+ getmFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filepath;
	}

	public String getCameraFilePath(Context context) {
		String filepath = "";
		try {
			filepath = fetchAndcreateIfReqCameraFolder(context) + "/"
					+ getmFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filepath;
	}

	private String fetchAndcreateIfReqCameraFolder(Context context) {
		File cacheDir = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = new File(
						android.os.Environment.getExternalStorageDirectory()
								+ "/Android/data/" + context.getPackageName()
								+ "/cache/camera");
			else
				cacheDir = new File(context.getCacheDir().getAbsoluteFile()
						+ "/camera");
			if (!cacheDir.exists())
				cacheDir.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cacheDir.getAbsolutePath();
	}

	@Override
	public String toString() {
		super.toString();

		JSONObject jsonOb = new JSONObject();
		try {
			jsonOb.put("FileName", getmFileName());
			jsonOb.put("ApiRefName", getmApiRefName());
			jsonOb.put("Caption", getmCaption());
			jsonOb.put("Uploaded", getmHasBeenUploaded());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonOb.toString();
	}

	public void fromString(String mStrJson) {

		try {

			JSONObject mJsonOb = new JSONObject(mStrJson);
			setmFileName(mJsonOb.getString("FileName"));
			setmApiRefName(mJsonOb.getString("ApiRefName"));
			setmCaption(mJsonOb.getString("Caption"));
			setmHasBeenUploaded(mJsonOb.getBoolean("Uploaded"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}