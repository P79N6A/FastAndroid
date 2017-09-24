package com.apache.fastandroid.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.apache.fastandroid.BuildConfig;
import com.apache.fastandroid.SplashActivity;
import com.apache.fastandroid.artemis.comBridge.ModularizationDelegate;
import com.apache.fastandroid.artemis.support.bean.OAuth;
import com.apache.fastandroid.support.TUncaughtExceptionHandler;
import com.apache.fastandroid.support.exception.FastAndroidExceptionDelegate;
import com.apache.fastandroid.support.imageloader.GlideImageLoader;
import com.apache.fastandroid.support.report.ActivityLifeCycleReportCallback;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tesla.framework.FrameworkApplication;
import com.tesla.framework.common.util.CrashHandler;
import com.tesla.framework.common.util.log.Logger;
import com.tesla.framework.common.util.log.NLog;
import com.tesla.framework.component.imageloader.ImageLoaderManager;
import com.tesla.framework.network.task.TaskException;
import com.tesla.framework.support.db.TeslaDB;
import com.tesla.framework.ui.activity.BaseActivity;
import com.tesla.framework.ui.activity.PermissionActivityHelper;
import com.tesla.framework.ui.widget.swipeback.SwipeActivityHelper;

import java.io.File;

/**
 * Created by jerryliu on 2017/3/26.
 */

public class MyApplication extends MultiDexApplication{
    public static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private ActivityLifecycleCallbacks activityLifecycleCallbacks;

    public static final String client_id = "7024a413";
    public static final String client_secret = "8404fa33ae48d3014cfa89deaa674e4cbe6ec894a57dbef4e40d083dbbaa5cf4";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        FrameworkApplication.onCreate(getApplicationContext());


        initLog();
        initModuleBridge();
        initCrashAndAnalysis();

        TaskException.config(new FastAndroidExceptionDelegate());


        TeslaDB.setDB();
        BaseActivity.setHelper(SwipeActivityHelper.class);
        BaseActivity.setPermissionHelper(PermissionActivityHelper.class);
        if (activityLifecycleCallbacks == null){
            activityLifecycleCallbacks = new ActivityLifeCycleReportCallback();
        }
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        ImageLoaderManager.getInstance().setImageLoaderStrategy(new GlideImageLoader());
        initAuth();
        initLeakCanry();

    }


    private void initLog(){
        if (BuildConfig.LOG_DEBUG){
            NLog.setDebug(true, Logger.DEBUG);
        }
    }

    private void initModuleBridge(){
        //主模块
        ModularizationDelegate.registerComponent("com.apache.fastandroid.applike.MainAppLike");
        //用户模块
        ModularizationDelegate.registerComponent("com.apache.fastandroid.user.applike.UserCenterAppLike");
        //主题模块
        ModularizationDelegate.registerComponent("com.apache.fastandroid.applike.TopicAppLike");
        //小说模块
        ModularizationDelegate.registerComponent("com.apache.fastandroid.novel.applike.NovelAppLike");
    }


    private void initCrashAndAnalysis(){
        //本地crash日志收集
        CrashHandler.setupCrashHandler(getApplicationContext());
        //bugly统计
        //CrashReport.initCrashReport(getApplicationContext(),BuildConfig.BUGLY_APP_ID,BuildConfig.LOG_DEBUG);
        TUncaughtExceptionHandler.getInstance(getApplicationContext(),configCrashFilePath()).init(this, BuildConfig.DEBUG, true, 0, SplashActivity.class);

    }

    private String configCrashFilePath(){
        String path = "";
        try {
            String storageStr = Environment.getExternalStorageState();
            if (storageStr != null && storageStr.equals(Environment.MEDIA_MOUNTED)) {
                File file = this.getExternalFilesDir(null);
                if (file != null) {
                    path = file + File.separator + "crash"; // Android/data/pkgName/files/crash
                }
            }
        } catch (Exception e) {
        } catch (NoSuchMethodError e) {
        }
        return path;
    }



    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (activityLifecycleCallbacks != null){
            unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            //强制字体不随着系统改变而改变
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                createConfigurationContext(newConfig);
            } else {
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
        }
        return res;
    }


    private void initAuth(){
        OAuth.client_id = client_id;
        OAuth.client_secret = client_secret;
    }

    private RefWatcher mRefWatcher;

    private void initLeakCanry(){
        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
}
