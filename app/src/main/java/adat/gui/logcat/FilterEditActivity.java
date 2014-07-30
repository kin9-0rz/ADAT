package adat.gui.logcat;

import adat.R;
import adat.utils.Common;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class FilterEditActivity extends PreferenceActivity {
    private static final boolean DEBUG = Common.IS_DEBUG;

    private static final int MENU_SAVE = 1;

    private EditTextPreference tag;
    private EditTextPreference message;
    private ListPreference application;
    private ListPreference levels;

    // filter 配置
    private SharedPreferences filterCfg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.filter_edit);

        initFilter(this.getApplicationContext());
        setOnPreferenceChangeListener();

    }

    private void initFilter(Context context) {
        tag = (EditTextPreference) findPreference("tag");
        message = (EditTextPreference) findPreference("message");
        application = (ListPreference) findPreference("application");
        levels = (ListPreference) findPreference("levels");

        // com.htjf.adat_preferences.xml 自动生成
        filterCfg = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String str1 = this.filterCfg.getString("tag", "");
        String str2 = this.filterCfg.getString("message", "");
        String str3 = this.filterCfg.getString("application", "");
        String str4 = this.filterCfg.getString("level", "vebose");

        tag.setText(str1);
        tag.setSummary(getString(R.string.ptagSummary) + "\t" + str1);

        message.setText(str2);
        message.setSummary(getString(R.string.pmessageSummary) + "\t" + str2);

        application.setValue(str3);
        application.setSummary(getString(R.string.papplicationSummary) + "\t"
                + str3);
        String[] arrayOfString = getInstalledApps();
        application.setEntries(arrayOfString);
        application.setEntryValues(arrayOfString);

        levels.setValue(str4);
        levels.setSummary(getString(R.string.plevelSummary) + "\t" + str4);

    }

    /**
     * 显示简介
     */
    private void setOnPreferenceChangeListener() {
        tag.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                tag.setSummary(getString(R.string.ptagSummary) + "\t"
                        + newValue.toString());

                return !preference.isSelectable();
            }

        });

        message.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                message.setSummary(getString(R.string.pmessageSummary) + "\t"
                        + newValue.toString());
                return true;
            }

        });

        application
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        application
                                .setSummary(getString(R.string.papplicationSummary)
                                        + "\t" + newValue.toString());

                        if (preference.isEnabled()) {
                            System.out.println("isEnable");
                        }
                        if (preference.isPersistent()) {
                            System.out.println("isPersistent");
                        }
                        if (preference.isSelectable()) {
                            System.out.println("isSelectable");
                        }

                        return true;
                    }

                });

        levels.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                levels.setSummary(getString(R.string.plevelSummary) + "\t"
                        + newValue.toString());
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SAVE, 0, "Save");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (DEBUG) {
                Log.d("onOptionsItemSelected", "Save Filter");
            }

            SharedPreferences.Editor editor = filterCfg.edit();
            editor.putString("tag", tag.getText());
            editor.putString("message", message.getText());
            editor.putString("application", application.getValue());
            editor.putString("level", levels.getValue());
            editor.commit();

            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * 获取安装的软件包名
     *
     * @return appArr
     */
    private String[] getInstalledApps() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(
                PackageManager.GET_UNINSTALLED_PACKAGES) ;
        String[] appArr = new String[packages.size()];
        int i = 0;
        for (PackageInfo packageInfo : packages) {
            appArr[i] = packageInfo.packageName;
            i++;
        }

        return appArr;
    }

}
