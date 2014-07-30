package adat.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class SDCardUtils {
    private static String sdCard = Environment.getExternalStorageDirectory().toString();

    /**
     * 在SD卡中创建目录
     *
     * @param dirName 目录名。
     * @return 返回目录，null则表示创建失败。
     */
    public static File createDir(String dirName) {
        System.out.println(sdCard + File.separator + dirName);
        File dir = new File(sdCard + File.separator + dirName);
        if (!dir.exists()) {
            boolean isOk = dir.mkdir();
            if (!isOk) {
                return null;
            }
        }

        return dir;
    }

    /**
     * 在SD卡制定的目录下，创建文件
     *
     * @param dirName  文件夹名
     * @param fileName 文件名
     * @return  文件，null 则表示文件创建失败！
     */
    public static File createFile(String dirName, String fileName) {
        File dir = new File(sdCard + File.separator + dirName);
        File file = new File(dir.toString() + File.separator + fileName);

        if (!dir.exists()) {
            boolean isOK = dir.mkdir();
            if (!isOK) {
                Log.d("SDCardUtils : ", dir.toString() + "文件夹创建失败！");
                return null;
            }
        }

        if (!file.exists()) {
            try {
                if(!file.createNewFile()) {
                    Log.d("SDCardUtils : ", file.getName() + "文件已存在！");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SDCardUtils : ", file.getName() + "创建失败！");
                return null;
            }
        }
        return file;
    }

    /**
     * SD卡是否可用。
     * @return true 可用，false 不可用。
     */
    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState() .equals(Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageState().equals("mounted");
    }

}