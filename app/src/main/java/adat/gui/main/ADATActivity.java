package adat.gui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

import adat.R;
import adat.service.SMSLogService;
import adat.utils.AssetsUtils;
import adat.utils.Common;
import adat.utils.ConfigUtils;
import adat.utils.Exec;
import adat.utils.SDCardUtils;
import stericson.RootTools.RootTools;

public class ADATActivity extends Activity {

    public ConfigUtils configUtils = null;

    public CheckBox cbBoot = null;
    public CheckBox cbNetworkMonitor = null;
    public CheckBox cbSMSLog = null;
    public ToggleButton tbSwitch = null;

    private boolean isNetworkMonitor = false;
    private boolean isSMSLog = false;

    /**
     * 根据界面加载界面 <br />
     * 1.监控开关是否打开。<br />
     * 2.其他监控是否打开。
     */
    private void loadViews() {

        cbBoot = ((CheckBox) findViewById(R.id.cbBoot));
        cbBoot.setChecked(configUtils.isBoot());

        cbNetworkMonitor = ((CheckBox) findViewById(R.id.cbNetworkMonitor));
        isNetworkMonitor = configUtils.isNetworkMonitor();
        cbNetworkMonitor.setChecked(isNetworkMonitor);

        cbSMSLog = ((CheckBox) findViewById(R.id.cbSMSLog));
        isSMSLog = configUtils.isSMSLog();
        cbSMSLog.setChecked(isSMSLog);

        tbSwitch = (ToggleButton) findViewById(R.id.tbSwitch);
        tbSwitch.setChecked(configUtils.isOn());

        if (configUtils.isOn()) {
            cbBoot.setEnabled(false);
            cbNetworkMonitor.setEnabled(false);
            cbSMSLog.setEnabled(false);
        } else {
            cbBoot.setEnabled(true);
            cbNetworkMonitor.setEnabled(true);
            cbSMSLog.setEnabled(true);
        }

        setEnable();

    }

    /**
     * 设置开关按钮是否可按
     */
    private void setEnable() {
        if (isNetworkMonitor || isSMSLog) {
            tbSwitch.setEnabled(true);
        } else {
            tbSwitch.setEnabled(false);
        }
    }

    /**
     * 第一次运行程序,则进行初始化。如果初始化不成功，软件本身则无法使用。
     */
    private boolean firstInitData() {
        // If /system/xbin/tcpdump doesn't exists, copy to there.
        File tcpdump = new File("/system/xbin/tcpdump");
        if (!tcpdump.exists()) {
            if (RootTools.isRootAvailable()) {
                AssetsUtils.copyFileToFilesDir("tcpdump", "tcpdump",
                        getApplicationContext());
                String tcpdumpPath = getFilesDir().getAbsolutePath()
                        + "/tcpdump";

                try {
                    RootTools.sendShell("chmod 755 " + tcpdumpPath + "\n", -1);
                    RootTools.copyFile(tcpdumpPath, "/system/xbin/tcpdump",
                            true, true);

                    RootTools.sendShell("rm " + tcpdumpPath + "\n", -1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                Toast.makeText(this, "手机还没ROOT吧。", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // Create directory in sd card.
        if (SDCardUtils.isSdCardAvailable()) {
            SDCardUtils.createDir("/ADAT");
            File capDir = SDCardUtils.createDir("/ADAT/capture");
            SDCardUtils.createDir("/ADAT/smslog");

            configUtils.setCapDir(capDir.toString());
            configUtils.setSmsLogDir("/ADAT/smslog");
        } else {
            Toast.makeText(this, "SD卡不可写!", Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        // save configure
        configUtils.setFirst(false);
        return true;

    }

    class Listener4cbBoot implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            configUtils.setBoot(isChecked);
        }

    }

    class Listener4cbNetworkMonitor implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            configUtils.setNetworkMonitor(isChecked);
            isNetworkMonitor = isChecked;
            setEnable();
        }

    }

    class Listener4cbSMSLog implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            configUtils.setSMSLog(isChecked);
            isSMSLog = isChecked;
            setEnable();
        }

    }

    class Listener4tbSwitch implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            configUtils.setOn(isChecked);

            if (isChecked) {
                cbBoot.setEnabled(false);
                cbNetworkMonitor.setEnabled(false);
                cbSMSLog.setEnabled(false);

                tbSwitch.setText(R.string.tbSwitch_on);
                missionStart();

            } else {
                cbBoot.setEnabled(true);
                cbNetworkMonitor.setEnabled(true);
                cbSMSLog.setEnabled(true);

                tbSwitch.setText(R.string.tbSwitch_off);
                missionStop();
            }
        }

    }

    private void setListeners() {
        cbBoot.setOnCheckedChangeListener(new Listener4cbBoot());
        cbNetworkMonitor
                .setOnCheckedChangeListener(new Listener4cbNetworkMonitor());
        cbSMSLog.setOnCheckedChangeListener(new Listener4cbSMSLog());
        tbSwitch.setOnCheckedChangeListener(new Listener4tbSwitch());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adat_main);

        configUtils = new ConfigUtils(getApplicationContext());

        loadViews();
        setListeners();
        if (configUtils.isFirst()) {
            firstInitData();
        }
    }

    /**
     * 返回键退出
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            new AlertDialog.Builder(this)
                    .setTitle("Dialog")
                    .setMessage("Exit this APP?")
                    .setIcon(Drawable.createFromPath(ALARM_SERVICE))
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    // 这个退出太彻底了，动态短信监控，则无法使用。
                                    // System.exit(0);
                                }
                            }
                    )
                    .setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            }
                    ).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 任务开始
     */
    public void missionStart() {
        if (configUtils.isNetworkMonitor()) {
            String fileName = configUtils.getCapDir() + "/"
                    + Common.getCurrentTime() + ".pcap";
            String cmd = Common.TCP_DUMP + fileName + "\n";
            Exec.execRootCmd(cmd);

        }

        if (configUtils.isSMSLog()) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SMSLogService.class);
            startService(intent);
        }

    }

    /**
     * 停止任务则清除配置
     */
    public void missionStop() {
        if (configUtils.isNetworkMonitor()) {
            RootTools.killProcess("tcpdump");
        }

        if (configUtils.isSMSLog()) {
            Intent intent = new Intent(getApplicationContext(), SMSLogService.class);
            stopService(intent);
        }
    }
}