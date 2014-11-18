package cus360.inapp.app.ticket.ticketlisting;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelDetail {

	private String mStrMessage = "";
	private String mStrSender = "";
	private String mStrSenderName = "";

	public String getmStrMessage() {
		return mStrMessage;
	}

	public void setmStrMessage(String mStrMessage) {
		this.mStrMessage = mStrMessage;
	}

	public String getmStrSender() {
		return mStrSender;
	}

	public void setmStrSender(String mStrSender) {
		this.mStrSender = mStrSender;
	}

	public String getmStrSenderName() {
		return mStrSenderName;
	}

	public void setmStrSenderName(String mStrSenderName) {
		this.mStrSenderName = mStrSenderName;
	}

	public ModelDetail() {

	}

	public ModelDetail(String mStrjson) {
		fromString(mStrjson);
	}

	public ModelDetail(JSONObject mjsonOB) {
		fromJson(mjsonOB);
	}

	@Override
	public String toString() {
		super.toString();

		JSONObject mJsonOb = new JSONObject();
		try {

			mJsonOb.put("message", getmStrMessage());
			mJsonOb.put("sender_name", getmStrSenderName());
			mJsonOb.put("sender", getmStrSender());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mJsonOb.toString();
	}

	public void fromString(String mStrJson) {

		JSONObject mjsonOb = new JSONObject();
		fromJson(mjsonOb);
	}

	public void fromJson(JSONObject mJsonOb) {

		try {
			setmStrMessage(mJsonOb.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setmStrSenderName(mJsonOb.getString("sender_name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setmStrSender(mJsonOb.getString("sender"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
