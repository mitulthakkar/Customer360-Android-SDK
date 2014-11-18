package cus360.inapp.app.ticket.ticketlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cus360.inapp.R;
import cus360.inapp.app.ticket.createticket.FragmentCreateTicket;
import cus360.inapp.base.ActivityCustomerBase;
import cus360.inapp.widget.ActionItem;

public class ActivityTicketListing extends ActivityCustomerBase {
	public static final String mStrTagFragTicketListing = "FragTicketListing";
	private ActionItem mAiCreateTicket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cus__efl_layout);
		if (savedInstanceState == null) {
			replaceContainerWithFrag(R.id.cus_efl_layoutToBeReplaced,
					FragmentTicketListing.newInstance(), false,
					mStrTagFragTicketListing);
		}
		mAiCreateTicket = new ActionItem(this, "Create Ticket",
				R.drawable.cus_action_ticket_plus_dark);

		getmActionBar(this).addActionItem(mAiCreateTicket);
	}

	@Override
	public void doOnActionItemClicked(View v) {
		super.doOnActionItemClicked(v);

		if (v == mAiCreateTicket.getmIcon()) {
			try {
				((FragmentTicketListing) (getSupportFragmentManager()
						.findFragmentByTag(mStrTagFragTicketListing)))
						.doOnCreateTicketClicked();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		((FragmentCreateTicket) (getSupportFragmentManager()
				.findFragmentByTag(mStrTagFragTicketListing)))
				.onActivityResult(requestCode, resultCode, data);
	}

}
