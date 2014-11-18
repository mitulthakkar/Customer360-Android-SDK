package cus360.inapp.app.ticket.ticketlisting;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelTicket {
	private String mStrId = "";
	private String mStrMessage = "";
	private Boolean mBoolIsConversation = false;

	public ModelTicket() {

	}

	public ModelTicket(String mStrJsonOb) {
		fromString(mStrJsonOb);
	}

	public ModelTicket(JSONObject mJsonOb) {
		fromJsonOb(mJsonOb);
	}

	public String getmStrId() {
		return mStrId;
	}

	public void setmStrId(String mStrId) {
		this.mStrId = mStrId;
	}

	public String getmStrMessage() {
		return mStrMessage;
	}

	public void setmStrMessage(String mStrMessage) {
		this.mStrMessage = mStrMessage;
	}

	public Boolean getmBoolIsConversation() {
		return mBoolIsConversation;
	}

	public void setmBoolIsConversation(Boolean mBoolIsConversation) {
		this.mBoolIsConversation = mBoolIsConversation;
	}

	@Override
	public String toString() {
		super.toString();
		JSONObject mjsonOb = new JSONObject();

		try {
			mjsonOb.put("id", getmStrId());
			mjsonOb.put("message", getmStrMessage());
			mjsonOb.put("is_conversation", getmBoolIsConversation());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mjsonOb.toString();
	}

	public void fromString(String mJsonOb) {
		try {
			fromJsonOb(new JSONObject(mJsonOb));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fromJsonOb(JSONObject mJsonOb) {
		try {
			setmStrId(mJsonOb.getString("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setmStrMessage(mJsonOb.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setmBoolIsConversation(mJsonOb.getBoolean("is_conversation"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
