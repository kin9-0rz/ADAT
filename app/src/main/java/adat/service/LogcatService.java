package adat.service;

import adat.utils.Common;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogcatService extends Service implements Runnable {

	/**
	 * 判断线程是否在运行
	 */
	private boolean runing = true;
	/**
	 * logcat 日志的输出文件路径
	 */
	private static String logfile = null;
	/**
	 * 过滤器配置（com.htjf.adat_preferences.xml）
	 */
	private static SharedPreferences filterSP = null;
	/**
	 * 监控的应用包名
	 */
	private static String packageName = "ALL";

	@Override
	public void run() {
		while (true) {
			if (Common.IS_DEBUG) {
				Log.d("LogcatService", "run()");
			}
			if (!runing)
				return;
			getLog();
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		filterSP = getSharedPreferences("com.htjf.adat_preferences",
				Context.MODE_PRIVATE);

		logfile = getSharedPreferences("cfg", Context.MODE_PRIVATE).getString(
				"logcatDir", null)
				+ "/" + Common.getCurrentTime() + ".log";

		new Thread(this).start();
		runing = true;

		if (Common.IS_DEBUG) {
			Log.d("LogcatService", "onStartCommand()");
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		runing = false;
		if (Common.IS_DEBUG) {
			Log.d("LogcatService", "onDestroy");
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 获取当前时间，格式化当前时间
	 * 
	 * @return time
	 */
	private static String getCurrentTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mmZ", Locale.CHINA);
		Date currentDate = new Date(System.currentTimeMillis());

		return simpleDateFormat.format(currentDate);
	}

	/**
	 * 过滤器 —— 根据包名获取 PID，根据 PID 过滤
	 * 
	 * @return pids
	 * @throws java.io.IOException
	 */
	private static String[] filter() throws IOException {
		String tmpPkgName = filterSP.getString("application", null);
		if (packageName != tmpPkgName) {
			File f = new File(logfile);
			Writer out = new FileWriter(f, true);
			out.write("\r\n\n\n" + getCurrentTime() + "开始监控" + tmpPkgName);
			out.close();
			packageName = tmpPkgName;
		}
		
		if (Common.IS_DEBUG) {
			Log.d("LogcatService", "filter() --- pkgName: " + tmpPkgName);
		}

		if (tmpPkgName != null && tmpPkgName.trim() != "") {
			String ret = stericson.RootTools.RootTools.getProcessPid(tmpPkgName);
			// ret 需要分割空格，因为程序可能多开
			String[] pids = ret.split(" ");

			if (Common.IS_DEBUG) {
				for (String pid : pids) {
					Log.d("LogcatService", "filter() --- pid: " + pid);
				}
			}
			return pids;
		}

		return null;
	}

	/**
	 * 执行 logcat 获取日志。
	 */
	private static void getLog() {
		if (Common.IS_DEBUG) {
			Log.d("LogcatService", "getLog() ---- 开始");
		}

		try {
            // FIXME adb logcat -f /sdcard/1.txt
			// 设置命令 logcat -d 读取日志，不阻塞。
			// -d dump the log and then exit (don't block)
			ArrayList<String> cmdLine = new ArrayList<String>();
			cmdLine.add("logcat");
			cmdLine.add("-d");

			// 设置命令 logcat -c 清除日志
			// -c clear (flush) the entire log and exit
			ArrayList<String> clearLog = new ArrayList<String>();
			clearLog.add("logcat");
			clearLog.add("-c");

			Process process = Runtime.getRuntime().exec(
					cmdLine.toArray(new String[cmdLine.size()]));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			Runtime.runFinalizersOnExit(true);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				// 清理日志....不清理的话，任何操作都将产生新的日志，代码进入死循环，直到 bufferReader 满
				Runtime.getRuntime().exec(
						clearLog.toArray(new String[clearLog.size()]));
				if (Common.IS_DEBUG) {
					Log.d("LogcatService", getCurrentTime() + " " + str);
				}
				
				if (packageName != null) {
					
				}

				String[] pids = filter();
				if (pids != null) {
					for (String pid : pids) {
						if (str.contains(pid)) {
							File f = new File(logfile);
							Writer out = new FileWriter(f, true);
							out.write("\r\n" + getCurrentTime() + " " + str);
							out.close();
						}
					}
				} else {
					File f = new File(logfile);
					Writer out = new FileWriter(f, true);
					out.write("\r\n" + getCurrentTime() + " " + str);
					out.close();
				}

			}

			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
