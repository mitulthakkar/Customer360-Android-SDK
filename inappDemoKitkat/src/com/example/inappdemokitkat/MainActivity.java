package com.example.inappdemokitkat;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cus360.inapp.app.Cus360;
import cus360.inapp.app.Cus360.EnvironmentTypes;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// Cus360.install(this, "99c4ed35cad15d08de031158469dcc14");
		//Cus360.install(this, "fa9903a09d9d15c486533d6563fd0e11");//hornok qa environment
		 
		HashMap<String, Object> mConfig = new HashMap<String, Object>();
		mConfig.put(Cus360.mStrKeyEnvironmentType, EnvironmentTypes.TEST);
		
		
	//	Cus360.install(this, "dc4a374daeb4960729b91bab8cd12248",mConfig);
	//	Cus360.install(this, "fa9903a09d9d15c486533d6563fd0e11",mConfig);//hornok qa environment with config ..
		
		
		//After new authentication
	//	Cus360.install(this, "fa9903a09d9d15c486533d6563fd0e11","af2e12ewd564f354dcc",mConfig);//hornok qa environment with config ..with access_token old
		Cus360.install(this,"af2e12ewd564f354dcc",mConfig);//hornok qa environment with config ..
		
		Cus360.setThemeColor(R.color.reyaad); 
		// Cus360.getInstance().launchCreateTicket(this);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		TextView mTvLaunchHelloWorld, mTvLaunchChat, mTvlaunchCreateTicket;
		TextView mTvText;

		public static final String mStrDemoText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris dapibus tempus mi, et dapibus quam pharetra id. Suspendisse pretium dui non arcu ultrices, a egestas nunc ornare. Phasellus cursus id mauris eu suscipit. Sed quis urna mauris. Cras viverra leo metus, in commodo tellus venenatis fermentum. Praesent et consectetur mauris. Nullam cursus in nunc nec interdum. Nulla sit amet semper ipsum, ac varius turpis. Sed purus ipsum, lacinia sit amet pretium nec, egestas et quam."
				+ "                                                                                                 "
				+ "Phasellus arcu tortor, dignissim sed gravida ac, tincidunt in risus. Morbi a est faucibus, aliquet nisl ac, volutpat ante. Etiam efficitur felis sed purus vulputate blandit. Curabitur ac diam in erat aliquam pulvinar. Donec quis erat et nibh ultrices scelerisque dignissim vel nulla. Nullam quis nulla porttitor, cursus est vel, fermentum quam. Phasellus et nisl iaculis, lobortis nunc non, aliquam ligula. Pellentesque sollicitudin nibh sed egestas consectetur. Maecenas scelerisque aliquam odio, sit amet blandit ipsum tincidunt vitae. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam ullamcorper posuere tellus."
				+ "Nulla posuere, urna sit amet pretium convallis, quam purus imperdiet mauris, vitae rhoncus ligula massa at dolor. Nulla id bibendum massa, ut mollis ipsum. Aliquam consequat quis ante quis dapibus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed imperdiet lectus sit amet aliquam vehicula. Maecenas sodales dolor eu metus auctor, eget vulputate nisi volutpat. Ut fringilla leo sit amet enim tincidunt vehicula. Donec rhoncus feugiat elit. Suspendisse pharetra, augue sit amet euismod viverra, dolor orci volutpat nulla, sed placerat turpis erat id est. Ut nisi augue, venenatis id nunc accumsan, imperdiet elementum magna. Mauris luctus massa ipsum, sit amet facilisis justo finibus non. In ut sodales justo. Nam viverra rutrum massa. Nulla dignissim sem in sapien ultrices, sed scelerisque nulla fermentum. Donec porttitor, mi id tincidunt ornare, lacus eros luctus erat, eget fermentum mi orci eu eros. ";
		public static final String mStrCustomerLink = "<a href = \"http://customer360.com\">Customer 360</a>";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			mTvText = (TextView) rootView.findViewById(R.id.fm_tv_paragraph);
			mTvText.setText(Html.fromHtml(mStrDemoText + mStrCustomerLink));
			mTvText.setClickable(true);
			mTvText.setMovementMethod(LinkMovementMethod.getInstance());

			return rootView;
		}
	}

	public void launchChat(View v) {
		Cus360.launchChat(this);
	}

	public void launchCreateTicket(View v) {
		Cus360.launchTickets(this);
	}

}

// @Override
// public boolean onCreateOptionsMenu(Menu menu) {
//
// // Inflate the menu; this adds items to the action bar if it is present.
// getMenuInflater().inflate(R.menu.main, menu);
// return true;
// }
//
// @Override
// public boolean onOptionsItemSelected(MenuItem item) {
// // Handle action bar item clicks here. The action bar will
// // automatically handle clicks on the Home/Up button, so long
// // as you specify a parent activity in AndroidManifest.xml.
// int id = item.getItemId();
// if (id == R.id.action_settings) {
// return true;
// }
// return super.onOptionsItemSelected(item);
// }
