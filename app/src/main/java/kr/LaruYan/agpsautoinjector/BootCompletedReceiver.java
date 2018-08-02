package kr.LaruYan.agpsautoinjector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		context.startService(new Intent(context, AgpsAutoInjectorService.class));
	}

}
