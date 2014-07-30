package adat.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {
	public final static boolean IS_DEBUG = true;
    public static final String DEBUG = "ADAT_DEBUG";
    public static final String TCP_DUMP = "tcpdump -i any -p -vv -s 0 -w ";

	public static String getCurrentTime() {
		Long currentTimeMillis = System.currentTimeMillis();
		return formatCurrentTime(currentTimeMillis);
	}

    /**
     * 格式化当前时间
     * @param currentTimeMillis
     * @return
     */
    public static String formatCurrentTime(long currentTimeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyyMMdd-HHmmss", Locale.CHINA);
		Date currentDate = new Date(currentTimeMillis);
		return simpleDateFormat.format(currentDate);
	}

    public static void log(Object obj) {
        if (IS_DEBUG) {
            Log.d(DEBUG, obj.toString());
        }
    }
}
