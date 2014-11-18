package cus360.inapp.app.ticket.ticketlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cus360.inapp.R;
import cus360.inapp.app.ticket.createticket.FragmentCreateTicket;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.widget.ActionItem;

public class ActivityTicketDetailsConversation extends ActivityCustomerBase {

	public static final String mStrTagFragTicketDetailsConversation = "FragTicketDetailsConversation";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String mStrModelTicket = getIntent().getExtras().getString(
				"ModelTicket");

		setContentView(R.layout.cus__efl_layout);
		if (savedInstanceState == null) {
			replaceContainerWithFrag(R.id.cus_efl_layoutToBeReplaced,
					FragmentTicketDetailsConversation
							.newInstance(mStrModelTicket), false,
					mStrTagFragTicketDetailsConversation);
		}
		// mAiCreateTicket = new ActionItem(this, "attach image",
		// R.drawable.cus_action_picture_holo_dark);
		//
		// getmActionBar(this).addActionItem(mAiCreateTicket);
	}

	@Override
	public void doOnActionItemClicked(View v) {
		super.doOnActionItemClicked(v);
		//
		// if (v == mAiCreateTicket.getmIcon()) {
		// try {
		// ((FragmentTicketListing) (getSupportFragmentManager()
		// .findFragmentByTag(mStrTagFragTicketListing)))
		// .doOnCreateTicketClicked();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			((FragmentTicketDetailsConversation) (getSupportFragmentManager()
					.findFragmentByTag(mStrTagFragTicketDetailsConversation)))
					.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
