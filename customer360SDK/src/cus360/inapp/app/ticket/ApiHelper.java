package cus360.inapp.app.ticket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.ticket.createticket.ModelPhoto;
import cus360.inapp.app.ticket.ticketlisting.ModelDetail;
import cus360.inapp.app.ticket.ticketlisting.ModelTicket;
import cus360.inapp.base.HomerLibs.HomerLogger;
import cus360.inapp.base.HomerLibs.HomerUtils;

public class ApiHelper {
	// "99c4ed35cad15d08de031158469dcc14"
	// public final static String mStrApiBaseUrl =
	// "http://192.168.1.8/crdibleinc/customer360/workspace/imageupload/";

	//
	// public final static String mStrApiBaseUrl =
	// " http://c360qa.c360dev.in/widget/attachment/uploadticketfiles";

	public final static String mStrApiBaseUrl = "http://c360qa.c360dev.in/widget";

	// public static String uploadTicketPhoto(String pathOfImageToUPload,
	// String ImageName) {
	// String result = "";
	// List<NameValuePair> mParams = new ArrayList<NameValuePair>();
	// try {
	// result = HomerUtils.executeHttpPostWithMultiPartEntity(
	// mStrApiBaseUrl + "/attachment/uploadticketfiles", mParams,
	// pathOfImageToUPload, ImageName, "");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	public static String createTicket(Context context, String name,
			String email, String message, ArrayList<ModelPhoto> mArrLisStrImages) {
		String result = "";
		JSONObject mJsonObParams = new JSONObject();
		try {
			mJsonObParams.put("name", name);
			mJsonObParams.put("email", email);
			mJsonObParams.put("message", message);
			mJsonObParams.put("device_type", "android");
			mJsonObParams.put("device_id", Cus360.getInstance()
					.getmStrGcmRegId(context));
			JSONArray jSonArr = new JSONArray();

			for (ModelPhoto p : mArrLisStrImages) {
				JSONObject mJson = new JSONObject();
				mJson.put("caption", p.getmCaption());
				mJson.put(
						"img",
						getBase64EncodedImageFromFile(context,
								new File(p.getFilePath(context))));

				// String[] mStrArr = p.getFilePath(context).split(".");
				//
				// String mStrExtensionValue = mStrArr[1];

				mJson.put("ext", "jpg");
				jSonArr.put(mJson);
			}

			mJsonObParams.put("attachments", jSonArr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> mParams = new ArrayList<NameValuePair>();

		NameValuePair mAccessToken = new BasicNameValuePair("access_token",
				Cus360.getInstance().getAcessToken(context));
		NameValuePair mParamss = new BasicNameValuePair("params",
				mJsonObParams.toString());

		mParams.add(mAccessToken);
		mParams.add(mParamss);

		HomerLogger.d("name" + name);
		HomerLogger.d("email" + email);
		HomerLogger.d("message" + message);
		HomerLogger.d("attachments" + null);
		HomerLogger.d("access_token == "
				+ Cus360.getInstance().getAcessToken(context));
		HomerLogger.d("params" + mJsonObParams.toString());
		try {
			result = HomerUtils.postData(mStrApiBaseUrl + "/createTicket",
					mParams);

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d(result);
		return result;
	}

	public static String getBase64EncodedImageFromFile(Context context, File f) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		byte[] bytes;
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bytes = output.toByteArray();
		String encodedString = "";

		encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

		// try {
		// String filename = "abc.txt";
		// File myFile = new File(Environment.getExternalStorageDirectory(),
		// filename);
		// if (!myFile.exists())
		// myFile.createNewFile();
		// FileOutputStream fos;
		// byte[] data = encodedString.getBytes();
		// try {
		// fos = new FileOutputStream(myFile);
		// fos.write(data);
		// fos.flush();
		// fos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		//
		// } catch (Exception e) {
		//
		// }
		return encodedString;
	}

	public static String fetchListOfTcikets(Context context, String email) {
		String result = "";
		JSONObject mJsonObParams = new JSONObject();
		try {
			mJsonObParams.put("email", email);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> mParams = new ArrayList<NameValuePair>();

		NameValuePair mAccessToken = new BasicNameValuePair("access_token",
				Cus360.getInstance().getAcessToken(context));
		NameValuePair mParamss = new BasicNameValuePair("params",
				mJsonObParams.toString());

		mParams.add(mAccessToken);
		mParams.add(mParamss);

		HomerLogger.d("email" + email);
		HomerLogger.d("access_token == "
				+ Cus360.getInstance().getAcessToken(context));
		HomerLogger.d("params" + mJsonObParams.toString());
		try {
			result = HomerUtils.postData(mStrApiBaseUrl + "/getTicketlist",
					mParams);

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d(result);
		return result;
	}

	public static ArrayList<ModelTicket> parseListOfTickets(String mStrResponse) {
		ArrayList<ModelTicket> mArrMtResult = new ArrayList<ModelTicket>();

		JSONObject mJsonOb = new JSONObject();
		JSONArray mJsonArrListofTickets = new JSONArray();

		try {

			mJsonOb = new JSONObject(mStrResponse);
			mJsonArrListofTickets = mJsonOb.getJSONArray("response");
			int mIntForLoopSize = mJsonArrListofTickets.length();

			for (int i = 0; i < mIntForLoopSize; i++) {
				JSONObject mJsonObOneTicket = new JSONObject();

				mJsonObOneTicket = mJsonArrListofTickets.getJSONObject(i);
				ModelTicket mModelTicket = new ModelTicket(mJsonObOneTicket);

				mArrMtResult.add(mModelTicket);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mArrMtResult;
	}

	public static String fetchTicketDetailsConversations(Context context,
			String email, String id) {
		String result = "";
		JSONObject mJsonObParams = new JSONObject();
		try {
			mJsonObParams.put("email", email);
			mJsonObParams.put("id", id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> mParams = new ArrayList<NameValuePair>();

		NameValuePair mAccessToken = new BasicNameValuePair("access_token",
				Cus360.getInstance().getAcessToken(context));
		NameValuePair mParamss = new BasicNameValuePair("params",
				mJsonObParams.toString());

		mParams.add(mAccessToken);
		mParams.add(mParamss);

		HomerLogger.d("email :" + email);
		HomerLogger.d("id :" + id);
		HomerLogger.d("access_token == " + "99c4ed35cad15d08de031158469dcc14");
		HomerLogger.d("params :" + mJsonObParams.toString());
		try {
			result = HomerUtils.postData(mStrApiBaseUrl + "/getTicketDetails",
					mParams);

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d(result);
		return result;
	}

	public static ArrayList<ModelDetail> parseTicketDetail(String mStrResponse) {
		ArrayList<ModelDetail> mArrMtResult = new ArrayList<ModelDetail>();

		JSONObject mJsonOb = new JSONObject();
		JSONArray mJsonArrListofTickets = new JSONArray();

		try {

			mJsonOb = new JSONObject(mStrResponse);
			mJsonArrListofTickets = mJsonOb.getJSONArray("response");
			int mIntForLoopSize = mJsonArrListofTickets.length();

			for (int i = 0; i < mIntForLoopSize; i++) {
				JSONObject mJsonObOneTicket = new JSONObject();
				mJsonObOneTicket = mJsonArrListofTickets.getJSONObject(i);
				ModelDetail mModelDetail = new ModelDetail(mJsonObOneTicket);

				mArrMtResult.add(mModelDetail);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mArrMtResult;
	}

	public static String sendTicketDetailConversation(Context context,
			String email, String id, String message) {
		String result = "";
		JSONObject mJsonObParams = new JSONObject();
		try {
			mJsonObParams.put("email", email);
			mJsonObParams.put("message", message);
			mJsonObParams.put("ticket_id", id);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		List<NameValuePair> mParams = new ArrayList<NameValuePair>();

		NameValuePair mAccessToken = new BasicNameValuePair("access_token",
				Cus360.getInstance().getAcessToken(context));
		NameValuePair mParamss = new BasicNameValuePair("params",
				mJsonObParams.toString());

		mParams.add(mAccessToken);
		mParams.add(mParamss);

		HomerLogger.d("email" + email);
		HomerLogger.d("message" + message);
		HomerLogger.d("id" + id);
		HomerLogger.d("access_token == "
				+ Cus360.getInstance().getAcessToken(context));
		HomerLogger.d("params" + mJsonObParams.toString());
		try {
			result = HomerUtils.postData(mStrApiBaseUrl
					+ "/createCommunication", mParams);

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d(result);
		return result;

	}

	public static String verifyAccessToken(Context context) {
		String result = "";

		List<NameValuePair> mParams = new ArrayList<NameValuePair>();

		HomerLogger.d("access_token == "
				+ Cus360.getInstance().getAcessToken(context));
		try {
			result = HomerUtils.postData(mStrApiBaseUrl
					+ "/authenticate?access_token="
					+ Cus360.getInstance().getAcessToken(context), mParams);

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d(result);
		return result;
	}

	public static void parseAcessTokenresponse(Context context,
			String mStrResponse) {
		ArrayList<ModelDetail> mArrMtResult = new ArrayList<ModelDetail>();

		JSONObject mJsonOb = new JSONObject();
		JSONObject mJsonObResponse = new JSONObject();

		try {

			mJsonOb = new JSONObject(mStrResponse);
			mJsonObResponse = mJsonOb.getJSONObject("response");
			JSONObject mJsonOBProductData = mJsonObResponse
					.getJSONObject("product_data");
			Cus360.getInstance().setmStrSenderId(context,
					mJsonOBProductData.getString("project_number"));

			Cus360.getInstance().setmStrChatUrl(context,
					mJsonOBProductData.getString("chat_url"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean checkIfFetchDataWasSuccess(String mStrResponseJson) {

		JSONObject mJsonOb = new JSONObject();
		Boolean mBoolSuccess = false;

		try {
			mJsonOb = new JSONObject(mStrResponseJson);
			mBoolSuccess = mJsonOb.getBoolean("success");

		} catch (Exception e) {
			e.printStackTrace();
		}

		HomerLogger.d("fetchData sucess value returned == " + mBoolSuccess);
		return mBoolSuccess;
	}

	public static String parseResponseKeyFromApiResponse(String mStrResponseJson) {

		JSONObject mJsonOb = new JSONObject();
		String mStrResponse = "";

		try {
			mJsonOb = new JSONObject(mStrResponseJson);
			mStrResponse = mJsonOb.getString("response");

		} catch (Exception e) {
			e.printStackTrace();
		}
		HomerLogger.d("fetchData Response value returned == " + mStrResponse);
		return mStrResponse;
	}

}