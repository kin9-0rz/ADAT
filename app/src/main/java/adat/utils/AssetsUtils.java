package adat.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class AssetsUtils {
	static final boolean DEBUG = Common.IS_DEBUG;

	/**
	 * Copy file from assets to /data/data/{package name}/files/ directory.
	 * 
	 * @param srcFile
	 *            apk's assets/srcFile
	 * @param dstFile
	 *            /data/data/{package name}/files/dstFile
	 * @param ctx
	 *            Context
	 * @return
	 */
	public static void copyFileToFilesDir(String srcFile, String dstFile,
			Context ctx) {
		if (DEBUG) {
			Log.d("AssetsUtils", "srcFile : " + srcFile + " dstFile : "
					+ dstFile);
		}
		try {
			InputStream is = ctx.getAssets().open(srcFile);
			FileOutputStream fos = ctx.openFileOutput(dstFile,
					Context.MODE_PRIVATE);
			byte[] many = new byte[1024];
			int data;
			while (true) {
				data = is.read(many);
				if (data < 0) {
					is.close();
					fos.getChannel().force(true);
					fos.flush();
					fos.close();
					break;
				}
				fos.write(many, 0, data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("copyFileToFilesDir", "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("copyFileToFilesDir", "IOException");
		}
	}

}