package cus360.inapp.app.ticket.createticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cus360.inapp.R;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.widget.ActionItem;

public class ActivityCreateTicket extends ActivityCustomerBase {

	public static final String mStrTagFragCreateTicket = "FragCreateTicket";
	ActionItem mAiAttachImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cus__efl_layout);
		if (savedInstanceState == null) {
			replaceContainerWithFrag(R.id.cus_efl_layoutToBeReplaced,
					FragmentCreateTicket.newInstance(), false,
					mStrTagFragCreateTicket);
		}
		mAiAttachImage = new ActionItem(this, "attach image",
				R.drawable.cus_action_picture_plus_holo_dark);

		getmActionBar(this).addActionItem(mAiAttachImage);
	}

	@Override
	public void doOnActionItemClicked(View v) {
		super.doOnActionItemClicked(v);

		if (v == mAiAttachImage.getmIcon()) {
			try {
				((FragmentCreateTicket) (getSupportFragmentManager()
						.findFragmentByTag(mStrTagFragCreateTicket)))
						.doOnAttachImageClicked();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		((FragmentCreateTicket) (getSupportFragmentManager()
				.findFragmentByTag(mStrTagFragCreateTicket))).onActivityResult(
				requestCode, resultCode, data);
	}
}
