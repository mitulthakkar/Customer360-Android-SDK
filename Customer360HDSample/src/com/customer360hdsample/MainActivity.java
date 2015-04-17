package com.customer360hdsample;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	private static final String apiKey = "7aca791ccdfbdc2a86315e1d6d3b1582";   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchTicket(View v)
	{
		//configuation
		HashMap<String, Object> mConfig = new HashMap<String, Object>();
		mConfig.put(cus360.inapp.helpdesk.app.Cus360.mStrKeyEnvironmentType, 
				cus360.inapp.helpdesk.app.Cus360.EnvironmentTypes.TEST);
		
		//installation
		cus360.inapp.helpdesk.app.Cus360.install(this, apiKey, mConfig);

		//launch ticket module
		cus360.inapp.helpdesk.app.Cus360.launchTickets(this);    
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
