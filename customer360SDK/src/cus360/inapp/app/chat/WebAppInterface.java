package cus360.inapp.app.chat;

import java.util.Observable;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface extends Observable {
	Context mContext;

	/** Instantiate the interface and set the context */
	public WebAppInterface(Context c) {
		mContext = c;
	}

	/** Show a toast from the web page */
	@JavascriptInterface
	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}

	private String html;

	/**
	 * @return The most recent HTML received by the interface
	 */

	public String getHtml() {
		return this.html;
	}

	/**
	 * Sets most recent HTML and notifies observers.
	 * 
	 * @param html
	 *            The full HTML of a page
	 */
	@JavascriptInterface
	public void setHtml(String html) {
		this.html = html;
		setChanged();
		notifyObservers(html);
	}
}