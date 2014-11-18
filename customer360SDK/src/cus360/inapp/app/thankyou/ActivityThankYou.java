package cus360.inapp.app.thankyou;

import android.content.Intent;
import android.os.Bundle;
import cus360.inapp.R;
import cus360.inapp.base.ActivityCustomerBase;

public class ActivityThankYou extends ActivityCustomerBase {

	private static final String mStrFragmentThanYouTag = "FragmentThanyouTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cus__efl_layout);

		// replaceContainerWithFrag(
		// R.id.cus__actl_fl_layoutToBeReplaced,
		// FragmentThankYou
		// .newInstance(
		// "#87987987",
		// "<a href = \"http://google.com\" >Help Desk Url = www.helpdesk.com/?hasdhav</a>"),
		// false, mStrFragmentThanYouTag);
		hideSoftKeyboard();
		replaceContainerWithFrag(R.id.cus_efl_layoutToBeReplaced,
				FragmentThankYou.newInstance(), false, mStrFragmentThanYouTag);
	}

}
