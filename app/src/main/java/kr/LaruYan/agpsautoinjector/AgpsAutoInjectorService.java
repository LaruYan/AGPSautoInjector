package kr.LaruYan.agpsautoinjector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class AgpsAutoInjectorService extends Service implements GpsStatus.Listener {

	public static final String LAST_INJECTED = "AGPS_LAST_INJECTED";
	
	LocationManager locMgr;
	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	/*@Override
	public void onStart(Intent intent, int startId) {
	   // handleCommand(intent);
	}*/

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	 //   handleCommand(intent);
		initialize();
		
		if(locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		// We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
			locMgr.addGpsStatusListener(this);
			return START_STICKY;
		}else{
			stopSelf();
			return START_NOT_STICKY;
		}
	}
	void initialize(){
		if (locMgr == null){
		locMgr = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch(event){
		case GpsStatus.GPS_EVENT_STARTED:
			injectData();
			//Toast.makeText(getApplicationContext(), "GPS_EVENT_STARTED",Toast.LENGTH_SHORT).show();
			break;
//		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			//Toast.makeText(getApplicationContext(), "GPS_EVENT_SATELLITE_STATUS",Toast.LENGTH_SHORT).show();
//			break;/
//		case GpsStatus.GPS_EVENT_FIRST_FIX:
			//Toast.makeText(getApplicationContext(), "GPS_EVENT_FIRST_FIX",Toast.LENGTH_SHORT).show();
//			break;
//		case GpsStatus.GPS_EVENT_STOPPED:
			//Toast.makeText(getApplicationContext(), "GPS_EVENT_STOPPED",Toast.LENGTH_SHORT).show();
//			break;
		default:
			break;
		}
	}

}