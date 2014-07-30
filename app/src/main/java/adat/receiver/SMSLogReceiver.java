package adat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import adat.utils.Common;
import adat.utils.ConfigUtils;
import adat.utils.SDCardUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 短信日志类.<br />
 * 用来保存收到的短信.
 */
public class SMSLogReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[pdus.length];

            StringBuilder body = new StringBuilder();
            for (int i = 0; i < pdus.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                body.append(smsMessages[i].getDisplayMessageBody());
            }

            String address = smsMessages[0].getDisplayOriginatingAddress();
            if (address == null) {
                address = smsMessages[0].getOriginatingAddress();
            }

            String timeStamp = Common.formatCurrentTime(smsMessages[0].getTimestampMillis());

            ConfigUtils configUtils = new ConfigUtils(context);
            File smsLogFile = SDCardUtils.createFile(
                    configUtils.getSmsLogDir(), "sms.log");

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(smsLogFile, true);
                fileWriter.write("号码 : " + address + "\n");
                fileWriter.write("时间 : " + timeStamp + "\n");
                fileWriter.write("内容 : " + body + "\n\n\n");
                fileWriter.flush();
                fileWriter.close();
            } catch (FileNotFoundException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e1) {
                        // FIXME 错误日志,需要记录下来.
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }

        }
    }
}
