package adat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import adat.utils.Common;
import adat.utils.ConfigUtils;
import adat.utils.Exec;
import stericson.RootTools.RootTools;




/**
 * 检测网络变化
 */
public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action == null) {
            return;
        }

        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();

            if (activeNetInfo != null) {
                RootTools.killProcess("tcpdump");
                ConfigUtils configUtils = new ConfigUtils(context);
                String fileName = configUtils.getCapDir() + "/"
                        + Common.getCurrentTime() + ".pcap";
                Exec.execRootCmd(Common.TCP_DUMP + fileName + "\n");

            }

        }

    }
}
