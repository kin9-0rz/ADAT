package adat.service;

import adat.utils.Common;
import adat.utils.ConfigUtils;
import adat.utils.Exec;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.logging.Logger;

import stericson.RootTools.RootTools;

public class ConnectionService extends Service {
	private final boolean DEBUG = Common.IS_DEBUG;
	private TelephonyManager mTelephonyMgr = null;
	private ConnectionListener connectionListener = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 监控网络状态
		connectionListener = new ConnectionListener();
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(connectionListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
	public void onDestroy() {
		mTelephonyMgr
				.listen(connectionListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	class ConnectionListener extends PhoneStateListener {
		private ConfigUtils configUtils = null;

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			super.onDataConnectionStateChanged(state, networkType);
		}

		@SuppressWarnings("ConstantConditions")
        @Override
		public void onDataConnectionStateChanged(int state) {
			super.onDataConnectionStateChanged(state);

			switch (state) {
			case TelephonyManager.DATA_DISCONNECTED:
                Common.log("网络连接断开");
				break;
			case TelephonyManager.DATA_CONNECTING:
                Common.log("网络正在连接");
				break;
			case TelephonyManager.DATA_CONNECTED:
                Common.log("网络连接成功");
				RootTools.killProcess("tcpdump");
				configUtils = new ConfigUtils(getApplicationContext());
				String fileName = configUtils.getCapDir() + "/"
						+ Common.getCurrentTime() + ".pcap";
				Exec.execRootCmd(Common.TCP_DUMP + fileName + "\n");

				break;
			}
		}
	}
}
