package adat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ConfigUtils {

	public boolean DEBUG = Common.IS_DEBUG;

	public SharedPreferences cfg = null;
	public SharedPreferences.Editor editor = null;

	public ConfigUtils(Context context) {
		cfg = context.getSharedPreferences("cfg", Context.MODE_PRIVATE);
		editor = cfg.edit();
	}
	
	public String getString(String key) {
		return cfg.getString(key, null);
	}

	public boolean isFirst() {
		return cfg.getBoolean("isFirst", true);
	}

	public void setFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst).commit();
	}

	public boolean isBoot() {
		return cfg.getBoolean("isBoot", false);
	}

	public void setBoot(boolean isBoot) {
		editor.putBoolean("isBoot", isBoot).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setBoot", String.valueOf(isBoot));
		}
	}

	public boolean isFileDirMonitor() {
		return cfg.getBoolean("isFileDirMonitor", false);
	}

	public void setFileDirMonitor(boolean isFileDirMonitor) {
		editor.putBoolean("isFileDirMonitor", isFileDirMonitor).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setFileDirMonitor",
					String.valueOf(isFileDirMonitor));
		}
	}

	public boolean isLogcat() {
		return cfg.getBoolean("isLogcat", false);
	}

	public void setLogcat(boolean isLogcat) {
		editor.putBoolean("isLogcat", isLogcat).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setLogcat", String.valueOf(isLogcat));
		}
	}

	public boolean isNetworkMonitor() {
		return cfg.getBoolean("isNetworkMonitor", false);
	}

	public void setNetworkMonitor(boolean isNetworkMonitor) {
		editor.putBoolean("isNetworkMonitor", isNetworkMonitor).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setNetworkMonitor",
					String.valueOf(isNetworkMonitor));
		}
	}

	public boolean isSMSLog() {
		return cfg.getBoolean("isSMSLog", false);
	}

	public void setSMSLog(boolean isSMSLog) {
		editor.putBoolean("isSMSLog", isSMSLog).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setSMSLog", String.valueOf(isSMSLog));
		}
	}

	public boolean isOn() {
		return cfg.getBoolean("isOn", false);
	}

	public void setOn(boolean isOn) {
		editor.putBoolean("isOn", isOn).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setOn", String.valueOf(isOn));
		}
	}

	public String getCapDir() {
		return cfg.getString("capDir", null);
	}

	public void setCapDir(String capDir) {
		editor.putString("capDir", capDir).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setCapDir", capDir);
		}
	}

	public String getSmsLogDir() {
		return cfg.getString("smsLogDir", null);
	}

	public void setSmsLogDir(String smsLogDir) {
		editor.putString("smsLogDir", smsLogDir).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setSmsLogDir", smsLogDir);
		}
	}

	public String getLogcatDir() {
		return cfg.getString("logcatDir", null);

	}

	public void setLogcatDir(String logcatDir) {
		editor.putString("logcatDir", logcatDir).commit();
		if (DEBUG) {
			Log.d("ConfigUtils-setLogcatDir", logcatDir);
		}
	}

	public int getInt(String string) {
		// TODO Auto-generated method stub
		return 0;
	}


}
