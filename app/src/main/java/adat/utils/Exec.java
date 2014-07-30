package adat.utils;

import android.util.Log;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class Exec {

	static final boolean DEBUG = Common.IS_DEBUG;
	private static int PID = -1;
    private static final String BIN_SH = "/system/bin/sh";
    private static final String BIN_SU = "/system/bin/su";
    private static final String XBIN_SU = "/system/xbin/su";

    /**
     * 判断设备是否已经ROOT。
     * @return true 表示已root，false 则相反。
     */
	public static boolean isRoot() {
        File suFile = new File(BIN_SU);
        if (!suFile.exists()) {
            suFile = new File(XBIN_SU);
            if (!suFile.exists()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取 su 进程。
     * @return su 进程，null 则获取失败。
     */
	private static Process getRootProcess() {
        if (!isRoot()) {
            return null;
        }

        Process process;
        try {
            process = Runtime.getRuntime().exec("su");
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在普通进程中，执行一个命令。
     * @param cmd 命令
     */
	public static void execCmd(String cmd) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(BIN_SH);
			DataOutputStream dos = new DataOutputStream(process.getOutputStream());
			dos.writeBytes(cmd);
			dos.writeBytes("exit\n");
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkOutput(String cmd) {
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("/system/bin/sh");
			DataOutputStream dos = new DataOutputStream(proc.getOutputStream());
			dos.writeBytes(cmd);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String str;
		    while ((str = br.readLine())!=null) {
		    	System.out.println("check----- > " +str);
				
			}
		    br.close();
//		    dos.writeBytes("exit\n");
			dos.flush();
			dos.close();
			
			proc.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void execCmds(List<String> cmdList) {
		Process proc = null;
		DataOutputStream dos = null;
		try {
			proc = Runtime.getRuntime().exec("/system/bin/sh");
			dos = new DataOutputStream(proc.getOutputStream());
			for (Iterator<String> iterator = cmdList.iterator(); iterator
					.hasNext();) {
				String cmd = iterator.next();
				dos.writeBytes(cmd);
				proc.waitFor();
				// br = new BufferedReader(new InputStreamReader(
				// proc.getInputStream()));

				if (DEBUG) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(proc.getErrorStream()));
					String line = null;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					br.close();
					Log.d("writStrsToDos", cmd);
				}
			}
			dos.writeBytes("exit\n");
			dos.flush();
			dos.close();
		} catch (IOException e) {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (proc != null) {
				proc.destroy();
			}
			e.printStackTrace();
		} catch (InterruptedException e) {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (proc != null) {
				proc.destroy();
			}
			e.printStackTrace();
		}

	}


	/**
	 * run a command in the root process.
	 * @param cmd
	 *            Command must end with '\n'.
	 */
	public static void execRootCmd(String cmd) {
		Process suProc = getRootProcess();
		DataOutputStream dos = new DataOutputStream(suProc.getOutputStream());
		writeStrToDos(dos, cmd);
	}

	// run a set of commands in a root process.
	public static void execRootCmds(List<String> cmdList) {
		Process suProc = getRootProcess();
		DataOutputStream dos = new DataOutputStream(suProc.getOutputStream());
		writStrsToDos(dos, cmdList);
	}

	public static void getProcPID(String procName) {
		Process psProc = null;
		BufferedReader psBr = null;
		try {
			psProc = Runtime.getRuntime().exec("ps");
			psProc.waitFor();
			psBr = new BufferedReader(new InputStreamReader(
					psProc.getInputStream()));

			String str = psBr.readLine();
			while (str != null) {
				if (str.contains(procName)) {

					String[] strArr = str.split(" ");
					for (int i = 1; i < strArr.length; i++) {
						if (strArr[i].length() != 0) {
							if (DEBUG) {
								System.out.println(str);
							}
							setProcPID(Integer.valueOf(strArr[i]));
							break;
						}
					}
					break;
				}
				str = psBr.readLine();
			}
			psBr.close();
			psProc.destroy();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int getProcPID() {
		return PID;
	}

	public static void killProc(int PID) {
		execCmd("kill -9 " + PID);
		if (DEBUG) {
			Log.d("killProc", "KILL Process " + PID);
		}
	}

	public static void killRootProc(int PID) {
		String str = "kill -9 " + PID;
		execRootCmd(str);
		if (DEBUG) {
			Log.d("killRootProc", "KILL Root Process " + PID);
		}
	}

	private static void setProcPID(int pid) {
		PID = pid;
	}

	/**
	 * 
	 * @param dos
	 * @param cmd
	 *            Command have to ends with '\n'.
	 * @return
	 */
	private static void writeStrToDos(DataOutputStream dos, String cmd) {
		try {
			dos.writeBytes(cmd);
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param dos
	 * @param cmdList
	 *            Every command have to ends with '\n'.
	 */
	private static void writStrsToDos(DataOutputStream dos, List<String> cmdList) {
		try {
			for (Iterator<String> iterator = cmdList.iterator(); iterator
					.hasNext();) {
				String cmd = iterator.next();
				dos.writeBytes(cmd);
				if (DEBUG) {
					Log.d("writStrsToDos", cmd);
				}
			}
			dos.writeBytes("exit");
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("writStrsToDos", "IOException");
		}
	}
}
