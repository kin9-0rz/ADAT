package adat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import adat.service.ConnectionService;
import adat.service.SMSLogService;
import adat.utils.Common;
import adat.utils.Exec;
import stericson.RootTools.RootTools;


/**
 * 开机后, 程序处理情况.
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

        if (action == null) {
            return;
        }

		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			SharedPreferences cfg = context.getSharedPreferences("cfg",
					Context.MODE_PRIVATE);
			if (cfg.getBoolean("isOn", false)) {
				if (cfg.getBoolean("isBoot", false)) {
					if (cfg.getBoolean("isNetworkMonitor", false)) {
                        // FIXME 这段功能重复的情况比较多, 可以重构
						RootTools.killProcess("tcpdump");
						String capDir = cfg.getString("capDir", null);
						if (capDir != null) {
							String fileName = capDir + "/"
									+ Common.getCurrentTime() + ".pcap";
							Exec.execRootCmd(Common.TCP_DUMP + fileName + "\n");

							intent.setClass(context, ConnectionService.class);
							context.startService(intent);
						} else {
                            // TODO 之前的抓包文件目录删除的话,重启之后,不会继续抓包.
							Log.d("EventReceiver", "没有数据，请初始化目录");
						}
					}

					if (cfg.getBoolean("isSMSLog", false)) {
						intent.setClass(context, SMSLogService.class);
						context.startService(intent);
					}
				}
			}
		}

	}

}
