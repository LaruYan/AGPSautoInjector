package kr.LaruYan.agpsautoinjector;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;


public class MainActivity extends Activity {

	LocationManager locMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
/*
		Spinner spinner = (Spinner) findViewById(R.id.spinner_intervals);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.selectable_intervals_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
*/
		Button btn_clear = (Button)findViewById(R.id.button_delete);
		Button btn_inject= (Button)findViewById(R.id.button_inject);

		CheckBox cb_hideLauncher = (CheckBox)findViewById(R.id.checkBox_hideFromLauncher);
		
		btn_clear.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gpsIsOn()){
					clearData();
					Toast.makeText(getApplicationContext(), getString(R.string.agps_clear_request_made),Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		btn_inject.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gpsIsOn()){
					injectData();
					Toast.makeText(getApplicationContext(), getString(R.string.agps_inject_request_made),Toast.LENGTH_SHORT).show();
				}
			
			}
			
		});
		
		cb_hideLauncher.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton cb, boolean bool) {
				// TODO Auto-generated method stub
				if(bool){
					ComponentName componentToDisable =
							  new ComponentName("kr.LaruYan.agpsautoinjector",
							  "kr.LaruYan.agpsautoinjector.MainActivity");

							  getPackageManager().setComponentEnabledSetting(
							  componentToDisable,
							  PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
							  PackageManager.DONT_KILL_APP);
				}else{
					ComponentName componentToDisable =
							  new ComponentName("kr.LaruYan.agpsautoinjector",
							  "kr.LaruYan.agpsautoinjector.MainActivity");

							  getPackageManager().setComponentEnabledSetting(
							  componentToDisable,
							  PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
							  PackageManager.DONT_KILL_APP);
				}
			}});
		// startService
		getApplicationContext().startService(
				new Intent(getApplicationContext(),
						AgpsAutoInjectorService.class));
	}
	
	/*
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}
	*/
	
	void initialize(){
		if (locMgr == null){
		locMgr = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}
	private boolean gpsIsOn() {
		// TODO Auto-generated method stub
		initialize();
		if(locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			return true;
		}else{
			Toast.makeText(getApplicationContext(), getString(R.string.error_gps_off),Toast.LENGTH_LONG).show();
			
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			return false;
		}
	}

	void injectData(){
		if(checkNetwork()){
			//locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			initialize();
			//locMgr.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data", null);
			Bundle bundle = new Bundle();
			locMgr.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_xtra_injection", bundle);
			locMgr.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_time_injection", bundle);
		} else{
			Toast.makeText(getApplicationContext(), getString(R.string.error_no_networks),Toast.LENGTH_SHORT).show();
		}
	}
	//http://stackoverflow.com/questions/7404917/how-to-check-the-network-availability
	//The following very similar approach works, but has the added advantage of not caring what the underlying medium, since it looks as though there is support for more than just WiFi. Maybe these are also covered by mobile, but the docs aren't super clear:
	boolean checkNetwork(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cm.getActiveNetworkInfo();     
	    if (info == null) return false;
	    State network = info.getState();
	    return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
	}
	void clearData(){
		//locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		initialize();
		locMgr.sendExtraCommand(LocationManager.GPS_PROVIDER,"delete_aiding_data", null);
	}
}
