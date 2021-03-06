package com.tesla.framework.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.tesla.framework.R;
import com.tesla.framework.common.setting.SettingUtility;
import com.tesla.framework.common.util.ResUtil;
import com.tesla.framework.common.util.log.NLog;
import com.tesla.framework.common.util.view.StatusBarUtil;
import com.tesla.framework.network.task.ITaskManager;
import com.tesla.framework.network.task.TaskManager;
import com.tesla.framework.network.task.WorkTask;
import com.tesla.framework.support.inject.InjectUtility;
import com.tesla.framework.support.inject.ViewInject;
import com.tesla.framework.ui.fragment.ABaseFragment;
import com.tesla.framework.ui.widget.CustomToolbar;
import com.tesla.framework.ui.widget.ToastUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Created by JerryLiu on 17/04/08.
 */

public class BaseActivity extends AppCompatActivity implements ITaskManager,CustomToolbar.OnToolbarDoubleClickListener {

    static final String TAG = "Activity-Base";

    private static Class<? extends BaseActivityHelper> mHelperClass;

    private BaseActivityHelper mHelper;

    private int theme = 0;// 当前界面设置的主题

    private Locale language = null;// 当前界面的语言

    private TaskManager taskManager;

    private boolean isDestory;

    // 当有Fragment Attach到这个Activity的时候，就会保存
    private Map<String, WeakReference<ABaseFragment>> fragmentRefs;

    private static BaseActivity runningActivity;

    private View rootView;

    @ViewInject(idStr = "toolbar")

    Toolbar mToolbar;

    public static BaseActivity getRunningActivity() {
        return runningActivity;
    }

    public static void setRunningActivity(BaseActivity activity) {
        runningActivity = activity;
    }

    public static void setHelper(Class<? extends BaseActivityHelper> clazz) {
        mHelperClass = clazz;
    }

    protected int configTheme() {
        if (mHelper != null) {
            int appTheheme = mHelper.configTheme();
            if (appTheheme > 0)
                return appTheheme;
        }
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       injectActivityHelper();

        if (getActivityHelper() != null)
            getActivityHelper().onCreate(savedInstanceState);

        fragmentRefs = new HashMap<>();

        if (savedInstanceState == null) {
            theme = configTheme();

            language = new Locale(SettingUtility.getPermanentSettingAsStr("language", Locale.getDefault().getLanguage()),
                    SettingUtility.getPermanentSettingAsStr("language-country", Locale.getDefault().getCountry()));
        } else {
            theme = savedInstanceState.getInt("theme");

            language = new Locale(savedInstanceState.getString("language"), savedInstanceState.getString("language-country"));
        }
        // 设置主题
        if (theme > 0)
            setTheme(theme);
        // 设置语言
        setLanguage(language);

        taskManager = new TaskManager();

        try {
            // 如果设备有实体MENU按键，overflow菜单不会再显示
            ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
            if (viewConfiguration.hasPermanentMenuKey()) {
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(viewConfiguration, false);
            }
        } catch (Exception e) {
            NLog.printStackTrace(e);
        }

        super.onCreate(savedInstanceState);
    }


    public void injectActivityHelper(){
        if (mHelper == null) {
            try {
                if (mHelperClass != null) {
                    mHelper = mHelperClass.newInstance();
                    mHelper.bindActivity(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getActivityHelper() != null)
            getActivityHelper().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getActivityHelper() != null)
            getActivityHelper().onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getActivityHelper() != null)
            getActivityHelper().onRestart();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);

        rootView = view;
        InjectUtility.initInjectedView(this, this, rootView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setStatusBar();
        rootView = view;
        InjectUtility.initInjectedView(this, this, this.rootView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mHelper != null)
            mHelper.onSaveInstanceState(outState);

        outState.putInt("theme", theme);
        outState.putString("language", language.getLanguage());
        outState.putString("language-country", language.getCountry());
    }

    public void addFragment(String tag, ABaseFragment fragment) {
        fragmentRefs.put(tag, new WeakReference<>(fragment));
    }

    public void removeFragment(String tag) {
        fragmentRefs.remove(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getActivityHelper() != null)
            getActivityHelper().onResume();

        setRunningActivity(this);

        if (theme == configTheme()) {

        } else {
            NLog.i(TAG,"theme changed, reload()");
            reload();

            return;
        }

        /*String languageStr = SettingUtility.getPermanentSettingAsStr("language", Locale.getDefault().getLanguage());
        String country = SettingUtility.getPermanentSettingAsStr("language-country", Locale.getDefault().getCountry());
        if (language != null && language.getLanguage().equals(languageStr) && country.equals(language.getCountry())) {

        }
        else {
            Logger.i("language changed, reload()");
            reload();

            return;
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (getActivityHelper() != null)
            getActivityHelper().onPause();
    }

    public void setLanguage(Locale locale) {
        Resources resources = getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        config.locale = locale;
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mHelper != null)
            mHelper.onStop();
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {

        isDestory = true;

        removeAllTask(true);


        super.onDestroy();

        if (getActivityHelper() != null)
            getActivityHelper().onDestroy();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mHelper != null) {
            boolean handle = mHelper.onOptionsItemSelected(item);
            if (handle)
                return true;
        }

        if (item.getItemId() == android.R.id.home){
            if (onHomeClick())
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean onHomeClick() {
        if (getActivityHelper() != null) {
            boolean handle = getActivityHelper().onHomeClick();
            if (handle)
                return true;
        }

        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment.onHomeClick())
                return true;
        }

        return onBackClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mHelper != null) {
            boolean handle = mHelper.onKeyDown(keyCode, event);
            if (handle)
                return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (onBackClick())
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBackClick() {
        if (getActivityHelper() != null) {
            boolean handle = getActivityHelper().onBackClick();
            if (handle)
                return true;
        }

        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment.onBackClick())
                return true;
        }

        finish();

        return true;
    }



    @Override
    public final void addTask(@SuppressWarnings("rawtypes") WorkTask task) {
        taskManager.addTask(task);
    }

    @Override
     public final void removeTask(String taskId, boolean cancelIfRunning) {
        taskManager.removeTask(taskId, cancelIfRunning);
    }

    @Override
    public final void removeAllTask(boolean cancelIfRunning) {
        taskManager.removeAllTask(cancelIfRunning);
    }

    @Override
    public final int getTaskCount(String taskId) {
        return taskManager.getTaskCount(taskId);
    }

    /**
     * 以Toast形式显示一个消息
     *
     * @param msg
     */
    public void showMessage(CharSequence msg) {
        if (!TextUtils.isEmpty(msg) && msg.length() != 0){
            ToastUtils.showMessage(this,msg.toString());
        }
    }

    /**
     * @param msgId
     */
    public void showMessage(int msgId) {
        showMessage(getText(msgId));
    }

    @Override
    public void finish() {
        // 2014-09-12 解决ATabTitlePagerFragment的destoryFragments方法报错的BUG
        setDestory(true);

        super.finish();

        if (getActivityHelper() != null) {
            getActivityHelper().finish();
        }
    }

    public boolean isDestory() {
        return isDestory;
    }

    public void setDestory(boolean destory) {
        this.isDestory = destory;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getActivityHelper() != null) {
            getActivityHelper().onActivityResult(requestCode, resultCode, data);
        }
    }




    @Override
    public boolean OnToolbarDoubleClick() {
        Set<String> keys = fragmentRefs.keySet();
        for (String key : keys) {
            WeakReference<ABaseFragment> fragmentRef = fragmentRefs.get(key);
            ABaseFragment fragment = fragmentRef.get();
            if (fragment != null && fragment instanceof CustomToolbar.OnToolbarDoubleClickListener) {
                if (((CustomToolbar.OnToolbarDoubleClickListener) fragment).OnToolbarDoubleClick())
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (getActivityHelper() != null){
            getActivityHelper().onRequestPermissionsResult(requestCode,permissions,grantResults);
        }

    }

    public BaseActivityHelper getActivityHelper() {
        return mHelper;
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ResUtil.getColor(R.color.colorPrimary));
    }


    protected void setToolbarTitle(String msg){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(msg);

    }


}
